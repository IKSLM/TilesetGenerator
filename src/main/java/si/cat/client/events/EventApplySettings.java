package si.cat.client.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import si.cat.client.events.EventApplySettings.Handler;
import si.cat.shared.model.Settings;

@Data
@ToString
@EqualsAndHashCode(callSuper = false)
public class EventApplySettings extends GwtEvent<Handler> {
  public interface Handler extends EventHandler {
    void handle(EventApplySettings event);
  }

  public static Type<Handler> TYPE = new Type<Handler>();

  private final Settings settings;

  @Accessors(chain = true)
  private Object origin;

  @Override
  public Type<Handler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(Handler handler) {
    handler.handle(this);
  }
}
