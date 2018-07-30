package si.cat.shared.model;

import java.io.Serializable;

import javax.vecmath.Vector3d;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

// @SuppressWarnings("serial")
@Data
@Accessors(chain = true)
@EqualsAndHashCode(of = {"id"})
@NoArgsConstructor
@RequiredArgsConstructor
public class Tile implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = -1877018064028448240L;

  public static enum Side {
    Top, Left, Right
  }

  public static enum AlignWidth {
    Left, Center, Right
  }

  public static enum AlignHeight {
    Bottom, Center, Top
  }

  public static enum AlignDepth {
    Front, Center, Back
  }

  @NonNull
  protected Integer id;

  // The size in pixels. Cube side length is 64/sqrt(2).
  protected double size = 64;

  // Percentages from the base size.
  protected Vector3d relativeSizes = new Vector3d(1.0, 1.0, 1.0);

  protected Vector3d relativeOffsets = new Vector3d(0.0, 0.0, 0.0);

  // If any relative size != 1 and uvScaling is true, the UVs will be scaled accordingly (the
  // texture will be cut or repeated). Else, if uvScaling is false, UVs will remain as is.
  protected boolean uvScaling = true;

  // Aligns for each side.
  protected AlignWidth alignWidth = AlignWidth.Center;
  protected AlignHeight alignHeight = AlignHeight.Bottom;
  protected AlignDepth alignDepth = AlignDepth.Center;

  // The texturing info for each side. If null, no texture will be set.
  protected TextureMap textures = new TextureMap();

  // The mask for each side.
  protected TextureMap masks = new TextureMap();

  // If true, the tile will be rendered as a single plane textured quad of given width and height,
  // always facing the camera.
  protected boolean billboard = false;

  public String getAlias() {
    return String.valueOf(id);
  }

  public boolean hasTexture(String alias) {
    return textures.values().stream().filter(ti -> alias.equals(ti.getAlias())).findAny().isPresent()
        || masks.values().stream().filter(ti -> alias.equals(ti.getAlias())).findAny().isPresent();
  }

  public Tile clone(Integer id) {
    Tile tile = new Tile(id);

    tile.size = size;
    tile.relativeSizes = relativeSizes.clone();
    tile.alignWidth = alignWidth;
    tile.alignHeight = alignHeight;
    tile.alignDepth = tile.alignDepth;
    tile.uvScaling = uvScaling;
    tile.billboard = billboard;

    textures.forEach((key, value) -> tile.textures.put(key, value == null ? null : value.clone()));
    masks.forEach((key, value) -> tile.masks.put(key, value == null ? null : value.clone()));

    return tile;
  }
}
