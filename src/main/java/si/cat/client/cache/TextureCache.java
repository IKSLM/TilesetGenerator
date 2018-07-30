package si.cat.client.cache;

import si.cat.client.events.Callback2;
import si.cat.client.textures.TextureHelper;
import si.cat.shared.Size;
import si.cat.shared.model.TextureDescriptor;
import thothbot.parallax.core.client.textures.Texture;

public class TextureCache extends AliasCache<Texture> {

  private AliasCache<Size> sizeCache = new AliasCache<>();

  public Texture get(TextureDescriptor td, Callback2<Texture, Size> callback) {

    String alias = td.getAlias();

    Texture texture = super.get(alias);

    if (texture == null) {
      texture = TextureHelper.load(td, (tx, size) -> {
        // Cache the texture size.
        sizeCache.put(alias, size);
        callback.onCallback(tx, size);
      });

      put(alias, texture);
    } else {
      callback.onCallback(texture, sizeCache.get(alias));
    }

    return texture;
  }

  public Size getSize(String alias) {
    return sizeCache.get(alias);
  }

  @Override
  public void clear() {
    super.clear();
    sizeCache.clear();
  }

  @Override
  public void remove(String alias) {
    super.remove(alias);
    sizeCache.remove(alias);
  }
}
