package si.cat.client.view;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Image;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import si.cat.client.presenter.TextureEditorPresenter.TextureEditor;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(fluent = true)
public class TextureEditorView extends Composite implements TextureEditor {

  interface TextureEditorBinder extends UiBinder<Widget, TextureEditorView> {}

  private static TextureEditorBinder uiBinder = GWT.create(TextureEditorBinder.class);

  @UiField
  ListBox textureMagFilter;

  @UiField
  ListBox textureMinFilter;

  @UiField
  Image texturePreview;

  @UiField
  Button applyFilters;

  public TextureEditorView() {
    initWidget(uiBinder.createAndBindUi(this));
  }

  @Override
  public Widget asWidget() {
    return this;
  }
}
