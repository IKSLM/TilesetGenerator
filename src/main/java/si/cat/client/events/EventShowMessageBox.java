package si.cat.client.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import lombok.AllArgsConstructor;
import lombok.Getter;
import si.cat.client.events.EventShowMessageBox.Handler;
import si.cat.client.presenter.DialogsPresenter.MessageBoxCloseHandler;
import si.cat.client.presenter.DialogsPresenter.MessageType;

@AllArgsConstructor
@Getter
public class EventShowMessageBox extends GwtEvent<Handler> {
  public interface Handler extends EventHandler {
    void handle(EventShowMessageBox event);
  }

  public static Type<Handler> TYPE = new Type<Handler>();

  private String title = null;
  private String message = null;
  private MessageType type = MessageType.INFO;
  private MessageBoxCloseHandler infoHiddenHandler = null;

  @Override
  public Type<Handler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(Handler handler) {
    handler.handle(this);
  }
}
