package si.cat.shared.model;

import java.util.ArrayList;
import java.util.Collection;

import lombok.Data;
import lombok.EqualsAndHashCode;

@SuppressWarnings("serial")
@Data
@EqualsAndHashCode(callSuper = true)
public class OverlayTile extends Tile {

  // Merged tiles have N overlays.
  private ArrayList<String> overlays = new ArrayList<String>();

  // This must be explicitly set after the tile has been loaded.
  private transient ArrayList<Tile> overlayTiles = new ArrayList<Tile>();

  public OverlayTile() {
    super();
  }

  public OverlayTile(Integer id) {
    super(id);
  }

  @Override
  public String getAlias() {

    StringBuffer buffer = new StringBuffer();

    overlays.forEach(o -> {
      if (buffer.length() > 0) {
        buffer.append("+");
      }
      buffer.append(o);
    });

    // Include tile's id and then add overlay tiles.
    return id + ":" + buffer.toString();
  }

  @Override
  public boolean hasTexture(String alias) {
    return overlayTiles.stream().filter(t -> t.hasTexture(alias)).findAny().isPresent();
  }

  public boolean containsAnyTile(Collection<String> aliases) {
    return aliases.stream().filter(a -> overlays.contains(a)).findAny().isPresent();
  }

  @Override
  public OverlayTile clone(Integer id) {
    OverlayTile tile = new OverlayTile(id);
    tile.overlays = new ArrayList<>(overlays);
    tile.overlayTiles = new ArrayList<>(overlayTiles);
    return tile;
  }

  @Override
  public boolean isBillboard() {
    for (Tile t : overlayTiles) {
      if (t.isBillboard()) {
        return true;
      }
    }
    return false;
  }
}
