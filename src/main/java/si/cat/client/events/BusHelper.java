package si.cat.client.events;

import com.google.gwt.event.shared.GwtEvent;

import si.cat.client.cache.TextureCache;
import si.cat.client.cache.TextureDescriptorCache;
import si.cat.client.cache.TileCache;

public interface BusHelper {

  public default void post(GwtEvent<?> event) {
    Bus.post(event);
  }

  public default void getTileCache(Callback<TileCache> callback) {
    post(new EventGetTileCache(callback));
  }

  public default void getTextureCache(Callback<TextureCache> callback) {
    post(new EventGetTextureCache(callback));
  }

  public default void getTextureDescriptorCache(Callback<TextureDescriptorCache> callback) {
    post(new EventGetTextureDescriptorCache(callback));
  }
}
