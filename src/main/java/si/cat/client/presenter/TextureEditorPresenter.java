package si.cat.client.presenter;

import java.util.List;
import java.util.stream.Collectors;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Image;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.google.gwt.user.client.ui.HasWidgets;

import si.cat.client.events.BusHelper;
import si.cat.client.events.EventRefreshTiles;
import si.cat.client.presenter.TextureEditorPresenter.TextureEditor;
import si.cat.client.view.View;
import si.cat.shared.model.TextureDescriptor;
import si.cat.shared.model.Tile;
import thothbot.parallax.core.client.gl2.enums.TextureMagFilter;
import thothbot.parallax.core.client.gl2.enums.TextureMinFilter;
import thothbot.parallax.core.client.textures.Texture;

public class TextureEditorPresenter extends PresenterImpl<TextureEditor, String> implements BusHelper {

  public interface TextureEditor extends View {
    ListBox textureMagFilter();

    ListBox textureMinFilter();

    Image texturePreview();

    Button applyFilters();
  }

  public TextureEditorPresenter(TextureEditor view) {
    super(view, null);
  }

  @Override
  public void bind() {

    ListBox magBox = view.textureMagFilter();
    ListBox minBox = view.textureMinFilter();

    // Insert the data.
    magBox.addItem("NEAREST");
    magBox.addItem("LINEAR");

    minBox.addItem("NEAREST");
    minBox.addItem("LINEAR");

    // Bind the apply filter handler.
    view.applyFilters().addClickHandler(e -> updateTexture());
  }

  private void updateTexture() {

    String alias = getModel();

    if (alias == null) {
      return;
    }

    TextureMinFilter minFilter = TextureMinFilter.valueOf(view.textureMinFilter().getValue());
    TextureMagFilter magFilter = TextureMagFilter.valueOf(view.textureMagFilter().getValue());

    getTextureDescriptorCache(cache -> {
      TextureDescriptor td = cache.get(alias);
      if (td == null) {
        return;
      }

      // Check if we need to update the filters.
      boolean update = minFilter != TextureMinFilter.valueOf(td.getMinFilter())
          || magFilter != TextureMagFilter.valueOf(td.getMagFilter());

      if (!update) {
        // Filters already up to date.
        return;
      }

      td.setMinFilter(minFilter.toString());
      td.setMagFilter(magFilter.toString());

      updateTexture(td);
    });
  }

  private void updateTexture(TextureDescriptor td) {

    String alias = td.getAlias();

    // If a texture based off of this descriptor already exist, we must replace it with the updated
    // version and then also update all the tiles using it.
    getTextureCache(cache -> {

      // Access the cache using the alias so we do not create a texture if not needed.
      Texture texture = cache.get(alias);

      if (texture == null) {
        // No updating required.
        return;
      }

      // Ok, remove the texture and create a new one.
      cache.remove(alias);

      // Then simply refresh the tiles using it.
      cache.get(td, (tx, size) -> refreshTiles(alias));
    });
  }

  private void refreshTiles(String alias) {
    getTileCache(cache -> {
      List<Tile> tiles = cache.values().stream().filter(t -> t.hasTexture(alias)).collect(Collectors.toList());
      post(new EventRefreshTiles(tiles));
    });
  }

  @Override
  public void setModel(String alias) {
    super.setModel(alias);

    if (alias == null) {
      return;
    }

    getTextureDescriptorCache(cache -> {
      TextureDescriptor td = cache.get(alias);
      if (td != null) {
        view.texturePreview().setUrl(td.getEncodedImageData());
        view.textureMinFilter().setSelectedValue(td.getMinFilter());
        view.textureMagFilter().setSelectedValue(td.getMagFilter());
      }
    });
  }

  @Override
  public void go(HasWidgets container) {
    container.add(view.asWidget());
  }
}
