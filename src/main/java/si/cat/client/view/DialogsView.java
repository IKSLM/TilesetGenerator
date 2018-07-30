package si.cat.client.view;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.HelpBlock;
import com.github.gwtbootstrap.client.ui.Image;
import com.github.gwtbootstrap.client.ui.Modal;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

import lombok.Getter;
import si.cat.client.presenter.DialogsPresenter.Dialogs;
import si.cat.client.presenter.DialogsPresenter.MessageType;
import si.cat.client.presenter.DialogsPresenter.PromptType;
import si.cat.client.resources.icons.Icons;

@Getter
public class DialogsView extends Composite implements Dialogs {
  private static PortalUiBinder uiBinder = GWT.create(PortalUiBinder.class);

  interface PortalUiBinder extends UiBinder<Widget, DialogsView> {}

  // Prompt box
  @UiField
  Modal promptModal;

  @UiField
  HelpBlock promptMessage;

  @UiField
  TextBox promptValue;

  @UiField
  HelpBlock promptHelpMessage;

  @UiField
  Button promptOkButton;

  @UiField
  Button promptCancelButton;

  // Info box
  @UiField
  Modal infoModal;

  @UiField
  Image infoImage;

  @UiField
  HelpBlock infoMessage;

  @UiField
  Button infoCloseButton;

  public DialogsView() {
    initWidget(uiBinder.createAndBindUi(this));
  }

  @Override
  public boolean showPrompt(String title, String message, String help, PromptType promptType) {
    if (promptModal.isVisible()) {
      return false;
    }

    promptModal.setTitle(title);
    promptMessage.setText(message == null ? "" : message);
    promptHelpMessage.setText(help == null ? "" : help);

    switch (promptType) {
      case OK:
        promptOkButton.setVisible(true);
        promptCancelButton.setVisible(false);
        break;
      case OK_CANCEL:
        promptOkButton.setVisible(true);
        promptCancelButton.setVisible(true);
        break;
      default:
        break;
    }

    promptModal.show();

    return true;
  }

  @Override
  public void hidePrompt() {
    promptModal.hide();
  }

  @Override
  public String getPromptValue() {
    return promptValue.getValue();
  }

  @Override
  public void hideInfo() {
    infoModal.hide();
  }

  @Override
  public boolean showInfo(String title, String message, MessageType type) {
    if (infoModal.isVisible()) {
      return false;
    }

    infoModal.setTitle(title);
    infoMessage.setText(message);

    switch (type) {
      case ERROR:
        infoImage.setResource(Icons.INSTANCE.error_32());
        break;
      case INFO:
        infoImage.setResource(Icons.INSTANCE.info_32());
        break;
      case WARNING:
        infoImage.setResource(Icons.INSTANCE.warning_32());
        break;
      default:
        infoImage.setResource(Icons.INSTANCE.info_32());
        break;
    }

    infoModal.show();

    return true;
  }
}
