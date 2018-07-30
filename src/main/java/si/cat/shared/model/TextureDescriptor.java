package si.cat.shared.model;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@SuppressWarnings("serial")
@Data
@RequiredArgsConstructor
@NoArgsConstructor
public class TextureDescriptor implements Serializable {

  // The texture alias.
  @NonNull
  private String alias;

  // And the base64 encoded image.
  @NonNull
  private String encodedImageData;

  // Magnify and minify filters.
  @NonNull
  private String minFilter = "NEAREST";

  @NonNull
  private String magFilter = "NEAREST";
}
