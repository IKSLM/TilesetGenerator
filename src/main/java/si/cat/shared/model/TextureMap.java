package si.cat.shared.model;

import java.io.Serializable;
import java.util.HashMap;

import si.cat.shared.model.Tile.Side;

@SuppressWarnings("serial")
public class TextureMap extends HashMap<Side, TexturingInfo> implements Serializable {
  public TextureMap() {
    // Insert the defaults.
    for (Side s : Side.values()) {
      put(s, new TexturingInfo(null, null));
    }
  }
}
