package si.cat.client.presenter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.OptionalInt;
import java.util.stream.Collectors;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.WellForm;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.ui.HasWidgets;

import si.cat.client.cache.TileCache;
import si.cat.client.events.Bus;
import si.cat.client.events.BusHelper;
import si.cat.client.events.EventAddClearListener;
import si.cat.client.events.EventAddTile;
import si.cat.client.events.EventAddTiles;
import si.cat.client.events.EventClearSelection;
import si.cat.client.events.EventProjectLoaded;
import si.cat.client.events.EventSelectTiles;
import si.cat.client.events.EventShowMessageBox;
import si.cat.client.presenter.DialogsPresenter.MessageType;
import si.cat.client.presenter.TilesPagePresenter.TilesPage;
import si.cat.client.view.TileEditorView;
import si.cat.client.view.View;
import si.cat.shared.Utils;
import si.cat.shared.model.OverlayTile;
import si.cat.shared.model.TexturingInfo;
import si.cat.shared.model.Tile;
import si.cat.shared.model.Tile.Side;

public class TilesPagePresenter extends NoModelPresenter<TilesPage> implements BusHelper {

  public interface TilesPage extends View {

    Button addTileHandler();

    Button mergeTilesHandler();

    Button removeTileHandler();

    Button clearSelectionHandler();

    ListBox tileList();

    WellForm tilesForm();

    void clear();

    void clearSelection();

    Button sortTilesHandler();
  }

  public TilesPagePresenter(TilesPage view) {
    super(view);

  }

  private TileEditorPresenter tileEditor = null;

  private int id = 0;

  @Override
  public void bind() {

    // Handle project loaded event (we set the ID).
    addHandler(EventProjectLoaded.TYPE, e -> {

      OptionalInt _id = e.getProject().getTiles().stream().mapToInt(t -> t.getId()).max();

      if (_id.isPresent()) {
        id = _id.getAsInt() + 1;
      }

    });

    // Bind the merge tiles action.
    view.mergeTilesHandler().addClickHandler(e -> handleTileMerge());

    // Bind the clear selection.
    view.clearSelectionHandler().addClickHandler(e -> {
      view.clearSelection();
      post(new EventClearSelection());
    });

    // Register as a clear listener. Do not listen for the clear event but rather add our selves as
    // a clear listener. This is important because clear events must be done in sync.
    post(new EventAddClearListener(() -> {
      view.clear();
    }));

    // Handle loading tiles from some external location.
    Bus.addHandler(EventAddTiles.TYPE, e -> {

      if (e.isClear()) {
        view.tileList().clear();
      }

      e.getTiles().forEach(t -> view.tileList().addItem(t.getAlias()));
    });

    getTileCache(cache -> {

      // Handle sorting of the tiles.
      view.sortTilesHandler().addClickHandler(e -> sort(cache.values()));

      // Handle external tile selection.
      Bus.addHandler(EventSelectTiles.TYPE, e -> {

        // If we're the source of the event, ignore it.
        if (e.getOrigin() == TilesPagePresenter.this) {
          return;
        }

        // First deselect all.
        for (int i = 0; i < cache.values().size(); i++) {
          view.tileList().setItemSelected(i, false);
        }

        // Select all, edit first.
        if (!e.getTiles().isEmpty()) {

          // Then select required.
          Scheduler.get().scheduleDeferred(() -> {
            e.getTiles().forEach(t -> view.tileList().setItemSelected(cache.values().indexOf(t), true));

            // And edit first.
            edit(cache, e.getTiles().get(0).getAlias());
          });
        }
      });

      // Handle adding of a new tile.
      view.addTileHandler().addClickHandler(e -> {

        // If any number of tiles is selected, clone them.
        List<Tile> selectedTiles = getSelectedTiles(cache);

        if (!selectedTiles.isEmpty()) {
          selectedTiles.forEach(t -> {
            Tile clone = t.clone(id++);
            registerTile(cache, clone, t == selectedTiles.get(selectedTiles.size() - 1));
          });
        } else {
          // Add a default tile, options will be setup after addition.
          registerTile(cache, new Tile(id++), true);
        }
      });

      // Handle selection of the tile in the list.
      view.tileList().addChangeHandler(e -> {
        edit(cache, view.tileList().getSelectedValue());
      });

      // Handle removal of selected tile.
      view.removeTileHandler().addClickHandler(e -> {
        // Remove all free tiles (not used in any overlay tile). Report others.
        List<Tile> freeTiles = new ArrayList<>();
        List<Tile> usedTiles = new ArrayList<>();

        // Collect all overlay tiles.
        List<OverlayTile> overlays = cache.values().stream().filter(t -> t instanceof OverlayTile)
            .map(t -> (OverlayTile) t).collect(Collectors.toList());

        getSelectedTiles(cache).forEach(tile -> {

          // A tile is free if it's not included in any overlay tile.
          boolean free = !overlays.stream()
              .filter(oTile -> oTile.containsAnyTile(Collections.singletonList(tile.getAlias()))).findAny().isPresent();

          if (free) {
            freeTiles.add(tile);
          } else {
            usedTiles.add(tile);
          }
        });

        // Remove free.
        freeTiles.forEach(t -> {
          int index = cache.values().indexOf(t);
          cache.remove(t.getAlias());
          view.tileList().removeItem(index);
        });

        // Update the display.
        if (!freeTiles.isEmpty()) {
          post(new EventAddTiles(cache.values(), () -> {}).setClear(true));
        }

        // And post a warning about not being able to remove certain tiles.
        if (!usedTiles.isEmpty()) {
          List<String> tiles = usedTiles.stream().map(t -> t.getAlias()).collect(Collectors.toList());
          post(new EventShowMessageBox("Warning", "Can not remove the tiles: " + tiles, MessageType.WARNING, () -> {}));
        }

      });

    });

  }

  private void sort(List<Tile> tiles) {

    // Sort the tiles, first by volume then by texture aliases.
    Collections.sort(tiles, (t1, t2) -> {

      double l1 = t1.getRelativeSizes().lengthSquared();
      double l2 = t2.getRelativeSizes().lengthSquared();

      if (Utils.equals(l1, l2)) {
        String a1 = composeAliases(t1);
        String a2 = composeAliases(t2);

        return a1.compareTo(a2);
      }
      return (l1 < l2 ? -1 : 1);

    });

    // Then refresh the view.
    post(new EventAddTiles(tiles, () -> {}).setClear(true));
  }

  private String composeAliases(Tile t) {

    StringBuffer buffer = new StringBuffer();

    // Compose from textures.
    for (Side s : Side.values()) {
      TexturingInfo ti = t.getTextures().get(s);

      if (ti.getAlias() == null) {
        continue;
      }

      if (buffer.length() > 0) {
        buffer.append(",");
      }

      buffer.append(s);
      buffer.append(":");
      buffer.append(ti.getAlias());
    }

    // And masks.
    for (Side s : Side.values()) {
      TexturingInfo ti = t.getMasks().get(s);

      if (ti.getAlias() == null) {
        continue;
      }

      if (buffer.length() > 0) {
        buffer.append(",");
      }

      buffer.append(s);
      buffer.append(":");
      buffer.append(ti.getAlias());
    }

    GWT.log(buffer.toString());

    return buffer.toString();
  }

  private void registerTile(TileCache cache, Tile tile, boolean edit) {
    String alias = tile.getAlias();

    cache.put(alias, tile);

    post(new EventAddTile(tile, () -> {
      // The tile has been added to the rendering panel. List the options. Update on change.
      view.tileList().addItem(alias);
      view.tileList().setSelectedValue(alias);

      if (edit) {
        edit(cache, alias);
      }
    }));
  }

  private void handleTileMerge() {
    getTileCache(cache -> {
      Collection<Tile> selected = getSelectedTiles(cache);

      // Check prerequisites.
      if (selected.size() < 2) {
        return;
      }

      // Create a new overlay tile.
      OverlayTile tile = new OverlayTile(cache.size());
      tile.getOverlays().addAll(selected.stream().map(t -> t.getAlias()).collect(Collectors.toList()));
      tile.getOverlayTiles().addAll(selected);

      registerTile(cache, tile, true);
    });
  }

  private void edit(TileCache cache, String label) {
    Tile tile = cache.get(label);

    if (tile == null) {
      return;
    }

    tileEditor.setModel(tile);

    // Select all the tiles, but setting properties with multiple selected tiles will only happen on
    // the first selected tile.
    post(new EventSelectTiles(getSelectedTiles(cache)).setOrigin(TilesPagePresenter.this));
  }

  private List<Tile> getSelectedTiles(TileCache cache) {

    List<Tile> selected = new ArrayList<>();

    ListBox tiles = view.tileList();

    for (int i = 0; i < tiles.getItemCount(); i++) {
      if (tiles.isItemSelected(i)) {
        selected.add(cache.values().get(i));
      }
    }

    return selected;
  }

  @Override
  public void go(HasWidgets container) {
    container.add(view.asWidget());

    tileEditor = new TileEditorPresenter(new TileEditorView());
    tileEditor.go(view.tilesForm());
  }
}
