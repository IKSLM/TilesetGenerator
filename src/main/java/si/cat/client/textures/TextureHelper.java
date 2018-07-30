package si.cat.client.textures;

import com.google.gwt.dom.client.Element;

import si.cat.client.events.Callback2;
import si.cat.client.utils.NativeHelper;
import si.cat.shared.Size;
import si.cat.shared.model.TextureDescriptor;
import thothbot.parallax.core.client.gl2.enums.TextureMagFilter;
import thothbot.parallax.core.client.gl2.enums.TextureMinFilter;
import thothbot.parallax.core.client.gl2.enums.TextureWrapMode;
import thothbot.parallax.core.client.textures.Texture;
import thothbot.parallax.core.shared.math.Mathematics;

public class TextureHelper {

  public static Texture load(TextureDescriptor td, Callback2<Texture, Size> callback) {

    Texture texture = new Texture(td.getEncodedImageData(), tx -> {
      Element image = tx.getImage();

      Size size = new Size(NativeHelper.getNaturalWidth(image), NativeHelper.getNaturalHeight(image));

      boolean pot = Mathematics.isPowerOfTwo(size.width()) && Mathematics.isPowerOfTwo(size.height());

      // If texture is not POT only CLAMP_TO_EDGE is possible.
      if (!pot) {
        tx.setWrapS(TextureWrapMode.CLAMP_TO_EDGE);
        tx.setWrapT(TextureWrapMode.CLAMP_TO_EDGE);
      }

      callback.onCallback(tx, size);
    });

    texture.setMagFilter(TextureMagFilter.valueOf(td.getMagFilter()));
    texture.setMinFilter(TextureMinFilter.valueOf(td.getMinFilter()));

    texture.setGenerateMipmaps(false);

    return texture;
  }

}
