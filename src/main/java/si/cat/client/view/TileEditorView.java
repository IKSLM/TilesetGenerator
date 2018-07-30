package si.cat.client.view;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.CheckBox;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.RadioButton;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import si.cat.client.presenter.TileEditorPresenter.TileEditor;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(fluent = true)
public class TileEditorView extends Composite implements TileEditor {

  interface TileEditorBinder extends UiBinder<Widget, TileEditorView> {}

  private static TileEditorBinder uiBinder = GWT.create(TileEditorBinder.class);

  @UiField
  HTMLPanel tileEditorPanel;

  @UiField
  ListBox allSidesTexture;

  @UiField
  ListBox topTexture;

  @UiField
  ListBox leftTexture;

  @UiField
  ListBox rightTexture;

  @UiField
  ListBox topMask;

  @UiField
  ListBox leftMask;

  @UiField
  ListBox rightMask;

  @UiField
  TextBox tileSize;

  @UiField
  TextBox tileWidth;

  @UiField
  RadioButton widthAlignLeft;

  @UiField
  RadioButton widthAlignCenter;

  @UiField
  RadioButton widthAlignRight;

  @UiField
  TextBox tileHeight;

  @UiField
  RadioButton heightAlignBottom;

  @UiField
  RadioButton heightAlignCenter;

  @UiField
  RadioButton heightAlignTop;

  @UiField
  TextBox tileDepth;

  @UiField
  RadioButton depthAlignFront;

  @UiField
  RadioButton depthAlignCenter;

  @UiField
  RadioButton depthAlignBack;

  @UiField
  Button allSidesApplyTexture;

  @UiField
  Button allSidesUV;

  @UiField
  Button topUV;

  @UiField
  Button leftUV;

  @UiField
  Button rightUV;

  @UiField
  Button topMaskUV;

  @UiField
  Button leftMaskUV;

  @UiField
  Button rightMaskUV;

  @UiField
  CheckBox uvScaling;

  @UiField
  CheckBox billboard;

  @UiField
  TextBox offsetX;

  @UiField
  TextBox offsetY;

  @UiField
  TextBox offsetZ;

  public TileEditorView() {
    initWidget(uiBinder.createAndBindUi(this));
  }

  @Override
  public Widget asWidget() {
    return this;
  }
}
