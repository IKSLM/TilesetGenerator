package si.cat.client.cache;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AliasCache<T> {

  protected final Map<String, T> cache = new HashMap<>();

  public T get(String alias) {
    return cache.get(alias);
  }

  public void put(String alias, T item) {
    cache.put(alias, item);
  }

  public Set<String> getAliases() {
    return cache.keySet();
  }

  public int size() {
    return cache.size();
  }

  public void clear() {
    cache.clear();
  }

  public Collection<T> values() {
    return cache.values();
  }

  public void remove(String alias) {
    cache.remove(alias);
  }
}
