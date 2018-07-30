package si.cat.client.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import si.cat.client.events.EventProjectLoaded.Handler;
import si.cat.shared.model.Project;

@Data
@ToString
@EqualsAndHashCode(callSuper = false)
public class EventProjectLoaded extends GwtEvent<Handler> {
  public interface Handler extends EventHandler {
    void handle(EventProjectLoaded event);
  }

  public static Type<Handler> TYPE = new Type<Handler>();

  private final Project project;

  @Override
  public Type<Handler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(Handler handler) {
    handler.handle(this);
  }
}
