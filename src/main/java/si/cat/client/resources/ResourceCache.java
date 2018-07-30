package si.cat.client.resources;

import lombok.AllArgsConstructor;
import si.cat.client.events.Callback0;
import thothbot.parallax.core.client.textures.Texture;
import thothbot.parallax.core.client.textures.Texture.ImageLoadHandler;

public class ResourceCache {
  @AllArgsConstructor
  private static class ImageLoadObserver implements ImageLoadHandler {
    private Callback0 resourcesLoaded = null;
    private int numExpectedImages = 0;

    @Override
    public void onImageLoad(Texture texture) {
      synchronized (this) {
        if (resourcesLoaded == null) {
          return;
        }

        numExpectedImages--;

        if (numExpectedImages <= 0) {
          resourcesLoaded.onCallback();
          resourcesLoaded = null;
        }
      }
    }
  }
  // private static Map<Object, MeshBasicMaterial> materialCache = null;

  public static void build(Callback0 resourcesLoaded) {

    resourcesLoaded.onCallback();

    // ImageLoadObserver imageLoadObserver = new ImageLoadObserver(() -> {
    // resourcesLoaded.onCallback();
    // }, 0);

    // materialCache = new HashMap<>();
  }
}
