package si.cat.client.cache;

import si.cat.client.events.Bus;
import si.cat.client.events.EventOnTextureChanged;
import si.cat.client.events.EventOnTextureChanged.Change;
import si.cat.shared.model.TextureDescriptor;

public class TextureDescriptorCache extends AliasCache<TextureDescriptor> {

  @Override
  public void put(String alias, TextureDescriptor item) {
    super.put(alias, item);

    // Tell the system that the textures have been modified (added or removed or updated).
    Bus.post(new EventOnTextureChanged(Change.ADDED, alias));
  }

}
