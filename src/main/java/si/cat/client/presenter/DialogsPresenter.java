package si.cat.client.presenter;

import java.util.ArrayList;
import java.util.List;

import com.github.gwtbootstrap.client.ui.Button;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HasWidgets;

import si.cat.client.events.Callback2;
import si.cat.client.events.EventShowMessageBox;
import si.cat.client.events.EventShowPrompt;
import si.cat.client.view.View;

public class DialogsPresenter extends NoModelPresenter<DialogsPresenter.Dialogs> {
  public interface Dialogs extends View {
    Button getPromptOkButton();

    Button getPromptCancelButton();

    Button getInfoCloseButton();

    // There can be only one visible prompt at a time (modals).
    boolean showPrompt(String title, String message, String help, PromptType promptType);

    void hidePrompt();

    String getPromptValue();

    void hideInfo();

    boolean showInfo(String title, String message, MessageType type);
  }

  public static interface PromptCloseHandler {
    public void onOk(String value);

    public void onCancel();
  }

  public static interface MessageBoxCloseHandler {
    public void onClose();
  }

  public enum PromptType {
    OK, OK_CANCEL
  }

  public enum PromptResponse {
    OK, CANCEL
  }

  public enum MessageType {
    INFO, WARNING, ERROR
  }

  private List<Callback2<PromptResponse, String>> promptCloseHandlers = null;
  private List<MessageBoxCloseHandler> messageBoxCloseHandlers = null;

  public DialogsPresenter(Dialogs view) {
    super(view);

    promptCloseHandlers = new ArrayList<>();
    messageBoxCloseHandlers = new ArrayList<>();
  }

  @Override
  public void bind() {
    // Prompt actions.
    view.getPromptOkButton().addClickHandler(new ClickHandler() {

      @Override
      public void onClick(ClickEvent event) {
        view.hidePrompt();

        String value = view.getPromptValue();

        promptCloseHandlers.forEach(h -> h.onCallback(PromptResponse.OK, value));
        promptCloseHandlers.clear();
      }
    });

    view.getPromptCancelButton().addClickHandler(new ClickHandler() {

      @Override
      public void onClick(ClickEvent event) {
        view.hidePrompt();

        promptCloseHandlers.forEach(h -> h.onCallback(PromptResponse.CANCEL, null));
        promptCloseHandlers.clear();
      }
    });

    addHandler(EventShowPrompt.TYPE, new EventShowPrompt.Handler() {

      @Override
      public void handle(EventShowPrompt event) {
        if (view.showPrompt(event.getTitle(), event.getMessage(), event.getHelp(), event.getPromptType())) {
          promptCloseHandlers.add(event.getCallback());
        }
      }
    });

    // Info actions.
    view.getInfoCloseButton().addClickHandler(new ClickHandler() {

      @Override
      public void onClick(ClickEvent event) {
        view.hideInfo();

        for (MessageBoxCloseHandler handler : messageBoxCloseHandlers) {
          handler.onClose();
        }

        messageBoxCloseHandlers.clear();
      }
    });

    addHandler(EventShowMessageBox.TYPE, new EventShowMessageBox.Handler() {

      @Override
      public void handle(EventShowMessageBox event) {
        if (view.showInfo(event.getTitle(), event.getMessage(), event.getType())) {
          messageBoxCloseHandlers.add(event.getInfoHiddenHandler());
        }
      }
    });
  }

  @Override
  public void go(HasWidgets container) {}
}
