package si.cat.client.presenter;

import java.util.List;
import java.util.stream.Collectors;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.WellForm;
import com.google.gwt.user.client.ui.HasWidgets;

import si.cat.client.cache.TextureDescriptorCache;
import si.cat.client.cache.TileCache;
import si.cat.client.events.Bus;
import si.cat.client.events.BusHelper;
import si.cat.client.events.EventAddClearListener;
import si.cat.client.events.EventAddTextureDescriptors;
import si.cat.client.events.EventGetSelectedTexture;
import si.cat.client.events.EventOnTextureChanged;
import si.cat.client.events.EventOnTextureChanged.Change;
import si.cat.client.events.EventRefreshTiles;
import si.cat.client.events.EventSelectTiles;
import si.cat.client.events.EventShowMessageBox;
import si.cat.client.events.EventShowTextureImportDialog;
import si.cat.client.presenter.DialogsPresenter.MessageType;
import si.cat.client.presenter.TexturesPagePresenter.TexturesPage;
import si.cat.client.view.TextureEditorView;
import si.cat.client.view.View;
import si.cat.shared.model.TextureDescriptor;
import si.cat.shared.model.Tile;

public class TexturesPagePresenter extends NoModelPresenter<TexturesPage> implements BusHelper {

  public interface TexturesPage extends View {
    Button addTextureHandler();

    Button updateTextureHandler();

    Button removeTextureHandler();

    ListBox texturesList();

    WellForm texturesForm();

    void clear();
  }

  private TextureEditorPresenter textureEditor = null;

  public TexturesPagePresenter(TexturesPage view) {
    super(view);
  }

  @Override
  public void bind() {

    // Register as a clear listener. Do not listen for the clear event but rather add our selves as
    // a clear listener. This is important because clear events must be done in sync.
    post(new EventAddClearListener(() -> {
      view.clear();
    }));

    // Bind all after we get the cache.
    getTextureDescriptorCache(cache -> {
      bindDelayed(cache);
    });
  }

  private void bindDelayed(TextureDescriptorCache cache) {

    // Bind external texture descriptor loading.
    Bus.addHandler(EventAddTextureDescriptors.TYPE, e -> {
      e.getTextureDescriptors().forEach(td -> view.texturesList().addItem(td.getAlias()));
    });

    // Handle removal of the texture. A texture can only be removed if no tile is using it. If it
    // is, notify the user which tile uses it.
    view.removeTextureHandler().addClickHandler(e -> {
      removeSelectedTexture();
    });

    // Show add texture dialog.
    view.addTextureHandler().addClickHandler(e -> {
      String initialAlias = "texture:" + cache.size();
      post(new EventShowTextureImportDialog(initialAlias, (alias, url) -> addTexture(cache, alias, url)));
    });

    // Show update texture dialog.
    view.updateTextureHandler().addClickHandler(e -> {
      String sel = getSelectedTexture();

      if (sel == null) {
        return;
      }

      TextureDescriptor td = cache.get(sel);

      if (td == null) {
        return;
      }

      String initialAlias = td.getAlias();

      post(new EventShowTextureImportDialog(initialAlias,
          (alias, url) -> updateTexture(cache, initialAlias, alias, url)));
    });

    // Notify the system of the texture selection change.
    view.texturesList().addChangeHandler(e -> {

      updatePreview(cache);

      // Select the tiles using this texture.
      getTileCache(tileCache -> {
        List<Tile> tilesWhichUseTexture = getTilesByTexture(tileCache, getSelectedTexture());
        post(new EventSelectTiles(tilesWhichUseTexture).setOrigin(TexturesPagePresenter.this));
      });
    });

    // Bind the get selected texture event.
    addHandler(EventGetSelectedTexture.TYPE, e -> {
      e.getCallback().onCallback(getSelectedTexture());
    });
  }

  private List<Tile> getTilesByTexture(TileCache cache, String textureAlias) {
    return cache.values().stream().filter(t -> t.hasTexture(textureAlias)).collect(Collectors.toList());
  }

  private void removeSelectedTexture() {
    String sel = getSelectedTexture();

    if (sel == null) {
      return;
    }

    // Check if the texture is being used. If so, we can not remove it. We notify the user which
    // tiles must be removed in order to be able to remove the texture.
    getTileCache(cache -> {
      List<Tile> tilesWhichUseTexture = getTilesByTexture(cache, sel);

      if (tilesWhichUseTexture.isEmpty()) {
        // It's safe to remove the texture.
        getTextureCache(tcache -> tcache.remove(sel));
        getTextureDescriptorCache(tdache -> tdache.remove(sel));
        view.texturesList().removeItem(view.texturesList().getSelectedIndex());
        post(new EventOnTextureChanged(Change.REMOVED, sel));
      } else {
        // Else, tell which tiles use it.
        List<String> tiles = tilesWhichUseTexture.stream().map(t -> t.getAlias()).collect(Collectors.toList());
        post(new EventShowMessageBox("Warning", "Can not remove the texture it is being used in tiles: " + tiles,
            MessageType.WARNING, () -> {}));
      }
    });
  }

  private void updatePreview(TextureDescriptorCache cache) {
    TextureDescriptor td = cache.get(getSelectedTexture());

    if (td == null) {
      return;
    }

    // Update the texture editor.
    textureEditor.setModel(view.texturesList().getSelectedValue());
  }

  private String getSelectedTexture() {
    return view.texturesList().getSelectedValue();
  }

  private void addTexture(TextureDescriptorCache cache, String alias, String url) {
    if (alias == null || alias.trim().isEmpty() || url == null || url.trim().isEmpty()) {
      return;
    }

    cache.put(alias, new TextureDescriptor(alias, url));

    view.texturesList().addItem(alias);
    view.texturesList().setSelectedValue(alias);

    updatePreview(cache);
  }

  private void updateTexture(TextureDescriptorCache cache, String oldAlias, String newAlias, String url) {
    TextureDescriptor td = cache.get(oldAlias);

    if (td == null) {
      return;
    }

    boolean update = !oldAlias.equals(newAlias) || !td.getEncodedImageData().equals(url);

    if (!update) {
      return;
    }

    int index = view.texturesList().getSelectedIndex();

    cache.remove(oldAlias);
    td.setAlias(newAlias);

    if (url != null && !url.isEmpty()) {
      td.setEncodedImageData(url);
    }
    cache.put(newAlias, td);

    view.texturesList().removeItem(index);
    view.texturesList().insertItem(newAlias, index);
    view.texturesList().setSelectedValue(newAlias);

    updatePreview(cache);

    // Remove from texture cache.
    getTextureCache(textureCache -> {
      textureCache.remove(oldAlias);

      // Update the tiles.
      getTileCache(tileCache -> {

        List<Tile> tiles = getTilesByTexture(tileCache, oldAlias);

        if (tiles.isEmpty()) {
          return;
        }

        tiles.forEach(tile -> {
          // Update textures.
          tile.getTextures().values().forEach(ti -> {
            if (oldAlias.equals(ti.getAlias())) {
              ti.setAlias(newAlias);
            }
          });

          // And light maps.
          tile.getMasks().values().forEach(ti -> {
            if (oldAlias.equals(ti.getAlias())) {
              ti.setAlias(newAlias);
            }
          });

        });

        // Then refresh them.
        post(new EventRefreshTiles(tiles));

        // And tell we're done.
        post(new EventOnTextureChanged(Change.UPDATED, oldAlias));
      });
    });
  }

  @Override
  public void go(HasWidgets container) {
    container.add(view.asWidget());

    textureEditor = new TextureEditorPresenter(new TextureEditorView());
    textureEditor.go(view.texturesForm());
  }
}
