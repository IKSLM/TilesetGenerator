package si.cat.client.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import si.cat.client.events.EventGetSelectedTexture.Handler;

@Data
@ToString
@EqualsAndHashCode(callSuper = false)
public class EventGetSelectedTexture extends GwtEvent<Handler> {
  public interface Handler extends EventHandler {
    void handle(EventGetSelectedTexture event);
  }

  public static Type<Handler> TYPE = new Type<Handler>();

  private final Callback<String> callback;

  @Override
  public Type<Handler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(Handler handler) {
    handler.handle(this);
  }
}
