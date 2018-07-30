package si.cat.client.events;

import java.util.Collection;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import si.cat.client.events.EventAddTextureDescriptors.Handler;
import si.cat.shared.model.TextureDescriptor;

@Data
@ToString
@EqualsAndHashCode(callSuper = false)
public class EventAddTextureDescriptors extends GwtEvent<Handler> {
  public interface Handler extends EventHandler {
    void handle(EventAddTextureDescriptors event);
  }

  public static Type<Handler> TYPE = new Type<Handler>();

  private final Collection<TextureDescriptor> textureDescriptors;
  private final Callback0 callback;

  @Override
  public Type<Handler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(Handler handler) {
    handler.handle(this);
  }
}
