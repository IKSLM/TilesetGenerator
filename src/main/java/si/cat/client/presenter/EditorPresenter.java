package si.cat.client.presenter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

import si.cat.client.cache.TextureCache;
import si.cat.client.cache.TextureDescriptorCache;
import si.cat.client.cache.TileCache;
import si.cat.client.events.Bus;
import si.cat.client.events.BusHelper;
import si.cat.client.events.EventAddClearListener;
import si.cat.client.events.EventAddTile;
import si.cat.client.events.EventAddTiles;
import si.cat.client.events.EventApplySettings;
import si.cat.client.events.EventClearSelection;
import si.cat.client.events.EventGetSettings;
import si.cat.client.events.EventGetTilesetInfo;
import si.cat.client.events.EventLoadProject;
import si.cat.client.events.EventRefreshTiledInfo;
import si.cat.client.events.EventRefreshTiles;
import si.cat.client.events.EventRegisterHandlers;
import si.cat.client.events.EventSelectTiles;
import si.cat.client.presenter.EditorPresenter.Editor;
import si.cat.client.presenter.handlers.HasStartHandlers;
import si.cat.client.presenter.handlers.HasUpdateHandlers;
import si.cat.client.presenter.handlers.StartHandler;
import si.cat.client.utils.EventHelper;
import si.cat.client.view.EditorGui;
import si.cat.client.view.ProjectPageView;
import si.cat.client.view.SettingsPageView;
import si.cat.client.view.TexturesPageView;
import si.cat.client.view.TiledPageView;
import si.cat.client.view.TilesPageView;
import si.cat.client.view.View;
import si.cat.shared.model.OverlayTile;
import si.cat.shared.model.Settings;
import si.cat.shared.model.Tile;
import si.cat.shared.model.TilesetInfo;
import thothbot.parallax.core.client.events.HasEventBus;
import thothbot.parallax.core.client.events.ViewportResizeEvent;
import thothbot.parallax.core.client.renderers.WebGLRenderer;
import thothbot.parallax.core.shared.math.Ray;
import thothbot.parallax.core.shared.math.Vector2;

public class EditorPresenter extends NoModelPresenter<Editor> implements HasEventBus, StartHandler, BusHelper {

  public interface SceneProxy {
    Ray getRay(Vector2 mouseLocation);
  }

  public interface Editor extends View, HasStartHandlers, HasUpdateHandlers, SceneProxy {
    Widget getDomHandler();

    EditorGui getGui();

    void addTile(Tile tile, Integer index, boolean update);

    void refreshTile(Tile tile);

    TilesetInfo getTilesetInfo();

    void setTextureDescriptorCache(TextureDescriptorCache cache);

    void setTextureCache(TextureCache cache);

    void setTileCache(TileCache tileCache);

    void clear();

    void applySettings(Settings settings);

    Settings getSettings();

    void selectTile(Tile tile);

    void clearSelection();

    Tile getTile(Vector2 location);
  }

  private boolean ctrl = false;
  private List<Tile> clickedTiles = new ArrayList<>();

  public EditorPresenter(Editor view) {
    super(view);

    view.addStartHandler(this);
  }

  @Override
  public void bind() {

    // TODO: double click on the tile opens the UV editor

    // Handle tile selection.
    Bus.addHandler(EventSelectTiles.TYPE, e -> {

      // If we're the source of the event, ignore it.
      if (e.getOrigin() == EditorPresenter.this) {
        return;
      }

      view.clearSelection();
      e.getTiles().forEach(tile -> view.selectTile(tile));
    });

    Bus.addHandler(EventClearSelection.TYPE, e -> {
      view.clearSelection();
    });

    // Bind settings handling.
    Bus.addHandler(EventApplySettings.TYPE, e -> view.applySettings(e.getSettings()));
    Bus.addHandler(EventGetSettings.TYPE, e -> e.getCallback().onCallback(view.getSettings()));

    // Register as a clear listener. Do not listen for the clear event but rather add our selves as
    // a clear listener. This is important because clear events must be done in sync.
    post(new EventAddClearListener(() -> {
      view.clear();
    }));

    // Set the caches, then bind all.
    getTextureDescriptorCache(cache -> {
      getTextureCache(cache1 -> {
        getTileCache(cache2 -> {
          view.setTextureDescriptorCache(cache);
          view.setTextureCache(cache1);
          view.setTileCache(cache2);
          bindDelayed(cache2);
        });
      });
    });
  }

  private void bindDelayed(TileCache cache) {
    // Handle adding of a new tiles.
    Bus.addHandler(EventAddTiles.TYPE, event -> {

      if (event.isClear()) {
        view.clear();
      }

      AtomicInteger index = new AtomicInteger(0);
      event.getTiles().forEach(t -> view.addTile(t, index.getAndIncrement(), false));
      event.getCallback().onCallback();
    });

    Bus.addHandler(EventGetTilesetInfo.TYPE, e -> {
      e.getCallback().onCallback(view.getTilesetInfo());
    });

    // Handle refreshing of the tiled info.
    view.getGui().getTiledTabLink().addClickHandler(e -> {
      Bus.post(new EventRefreshTiledInfo());
    });

    // Handle adding of a new tile.
    Bus.addHandler(EventAddTile.TYPE, event -> {
      view.addTile(event.getTile(), null, false);
      event.getCallback().onCallback();
    });

    // Handle refreshing of the tile.
    Bus.addHandler(EventRefreshTiles.TYPE, event -> {
      event.getTiles().forEach(tile -> view.refreshTile(tile));

      List<String> aliases = event.getTiles().stream().map(t -> t.getAlias()).collect(Collectors.toList());

      // Check overlay tiles, if the overlay tile contains any of the tiles we updated, the overlay
      // tile itself must be updated.
      cache.values().stream().filter(t -> t instanceof OverlayTile).map(t -> (OverlayTile) t).forEach(ot -> {
        if (ot.containsAnyTile(aliases)) {
          view.refreshTile(ot);
        }
      });
    });
  }

  private Tile pressed = null;

  @Override
  public void onStart(WebGLRenderer renderer) {
    addHandler(EventRegisterHandlers.TYPE, event -> handleRegisterHandlers(event));

    // Make the project tab initially visible.
    view.getGui().getTabPanel().selectTab(0);

    // Lastly, we check if the user has attached an UID, then re proceed to load the project.
    handleLoad();

    // Register mouse actions.
    handleRegisterHandlers(new EventRegisterHandlers().setMouseDownHandler(e -> {

      // On click, find the underlying tile and select it.
      Tile pressed = view.getTile(EventHelper.getLocation(e));

      this.pressed = pressed;

      if (pressed != null) {

        // If the CTRL was held, we accumulate the results.
        if (!ctrl) {
          clickedTiles.clear();
        }

        clickedTiles.add(pressed);

        post(new EventSelectTiles(clickedTiles).setOrigin(EditorPresenter.this));
      } else if (!ctrl) {
        view.clearSelection();
        clickedTiles.clear();
        post(new EventSelectTiles(clickedTiles).setOrigin(EditorPresenter.this));
      }

    }).setKeyDownHandler(e -> {
      // Save the control key state.
      ctrl |= (e.getNativeKeyCode() == KeyCodes.KEY_CTRL);
    }).setKeyUpHandler(e -> {
      ctrl = false;
    }));

    getTileCache(cache -> {
      handleRegisterHandlers(new EventRegisterHandlers().setMouseUpHandler(e -> {

        if (pressed == null) {
          return;
        }

        Tile released = view.getTile(EventHelper.getLocation(e));

        if (released != null && pressed != released) {
          // Move the pressed tile onto the released location. All other tiles will be updated
          // accordingly.

          List<Tile> tiles = cache.values();

          int pressedIndex = tiles.indexOf(pressed);
          tiles.remove(pressedIndex);

          int releasedIndex = tiles.indexOf(released);
          tiles.add(releasedIndex + (releasedIndex >= pressedIndex ? 1 : 0), pressed);

          post(new EventAddTiles(tiles, () -> {}).setClear(true));
        }

        pressed = null;

      }));
    });
  }

  private void handleLoad() {
    String uid = Window.Location.getParameter("uid");

    if (uid != null && !uid.trim().isEmpty()) {
      Bus.post(new EventLoadProject(uid));
    }
  }

  @Override
  public void go(HasWidgets container) {
    container.clear();
    container.add(view.asWidget());

    // Spawn other presenters.
    new ProjectPagePresenter(new ProjectPageView()).go(view.getGui().getProjectTab());
    new TexturesPagePresenter(new TexturesPageView()).go(view.getGui().getTexturesTab());
    new TilesPagePresenter(new TilesPageView()).go(view.getGui().getTilesTab());
    new TiledPagePresenter(new TiledPageView()).go(view.getGui().getTiledTab());
    new SettingsPagePresenter(new SettingsPageView()).go(view.getGui().getSettingsTab());
  }

  protected void handleRegisterHandlers(EventRegisterHandlers event) {
    Widget handler = view.getDomHandler();

    // Keyboard events are root based.
    if (event.getKeyPressHandler() != null) {
      RootPanel.get().addDomHandler(event.getKeyPressHandler(), KeyPressEvent.getType());
    }

    if (event.getKeyDownHandler() != null) {
      RootPanel.get().addDomHandler(event.getKeyDownHandler(), KeyDownEvent.getType());
    }

    if (event.getKeyUpHandler() != null) {
      RootPanel.get().addDomHandler(event.getKeyUpHandler(), KeyUpEvent.getType());
    }

    if (event.getContextMenuHandler() != null) {
      addDomHandler(handler, event.getContextMenuHandler(), ContextMenuEvent.getType());
    }

    if (event.getMouseDownHandler() != null) {
      addDomHandler(handler, event.getMouseDownHandler(), MouseDownEvent.getType());
    }

    if (event.getMouseUpHandler() != null) {
      addDomHandler(handler, event.getMouseUpHandler(), MouseUpEvent.getType());
    }

    if (event.getMouseMoveHandler() != null) {
      addDomHandler(handler, event.getMouseMoveHandler(), MouseMoveEvent.getType());
    }

    if (event.getMouseOverHandler() != null) {
      addDomHandler(handler, event.getMouseOverHandler(), MouseOverEvent.getType());
    }

    if (event.getClickHandler() != null) {
      addDomHandler(handler, event.getClickHandler(), ClickEvent.getType());
    }

    if (event.getDoubleClickHandler() != null) {
      addDomHandler(handler, event.getDoubleClickHandler(), DoubleClickEvent.getType());
    }

    if (event.getMouseWheelHandler() != null) {
      addDomHandler(handler, event.getMouseWheelHandler(), MouseWheelEvent.getType());
    }

    if (event.getViewportResizeHandler() != null) {
      // Use the parallax provided bus for internal scene related events.
      addHandler(EVENT_BUS, ViewportResizeEvent.TYPE, event.getViewportResizeHandler());
    }
  }
}
