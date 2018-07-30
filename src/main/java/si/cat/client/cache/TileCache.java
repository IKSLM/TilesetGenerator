package si.cat.client.cache;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import si.cat.shared.model.Tile;

public class TileCache extends AliasCache<Tile> {

  private List<Tile> tiles = new ArrayList<>();

  @Override
  public void put(String alias, Tile item) {
    super.put(alias, item);

    tiles.add(item);
  }

  @Override
  public List<Tile> values() {
    return tiles;
  }

  @Override
  public void clear() {
    super.clear();
    tiles.clear();
  }

  @Override
  public void remove(String alias) {
    super.remove(alias);

    Iterator<Tile> iter = tiles.iterator();

    while (iter.hasNext()) {
      if (iter.next().getAlias().equals(alias)) {
        iter.remove();
      }
    }
  }
}
