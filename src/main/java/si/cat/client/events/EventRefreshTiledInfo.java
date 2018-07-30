package si.cat.client.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import si.cat.client.events.EventRefreshTiledInfo.Handler;

@Data
@ToString
@EqualsAndHashCode(callSuper = false)
public class EventRefreshTiledInfo extends GwtEvent<Handler> {
  public interface Handler extends EventHandler {
    void handle(EventRefreshTiledInfo event);
  }

  public static Type<Handler> TYPE = new Type<Handler>();

  @Override
  public Type<Handler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(Handler handler) {
    handler.handle(this);
  }
}
