package si.cat.client.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import lombok.AllArgsConstructor;
import lombok.Getter;
import si.cat.client.events.EventShowPrompt.Handler;
import si.cat.client.presenter.DialogsPresenter.PromptResponse;
import si.cat.client.presenter.DialogsPresenter.PromptType;

@AllArgsConstructor
@Getter
public class EventShowPrompt extends GwtEvent<Handler> {
  public interface Handler extends EventHandler {
    void handle(EventShowPrompt event);
  }

  public static Type<Handler> TYPE = new Type<Handler>();

  private String title = null;
  private String message = null;
  private String help = null;
  private PromptType promptType = PromptType.OK_CANCEL;
  private Callback2<PromptResponse, String> callback;

  @Override
  public Type<Handler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(Handler handler) {
    handler.handle(this);
  }
}
