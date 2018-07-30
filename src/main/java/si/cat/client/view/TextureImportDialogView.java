package si.cat.client.view;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.FileUpload;
import com.github.gwtbootstrap.client.ui.Modal;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import si.cat.client.presenter.TextureImportDialogPresenter.TextureImportDialog;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(fluent = true)
public class TextureImportDialogView extends Composite implements TextureImportDialog {

  interface TextureImportDialogBinder extends UiBinder<Widget, TextureImportDialogView> {}

  private static TextureImportDialogBinder uiBinder = GWT.create(TextureImportDialogBinder.class);

  @UiField
  public Modal textureDialog;

  @UiField
  public TextBox textureAliasBox;

  @UiField
  public FileUpload loadLocalHandler;

  @UiField
  public TextBox loadUrlBox;

  @UiField
  public Image previewImage;

  @UiField
  public Button okHandler;

  @UiField
  public Button cancelHandler;

  public TextureImportDialogView() {
    initWidget(uiBinder.createAndBindUi(this));
  }

  @Override
  public Widget asWidget() {
    return this;
  }

  @Override
  public void show() {
    textureDialog.show();
  }

  @Override
  public void hide() {
    textureDialog.hide();
  }
}
