package si.cat.client.view;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.WellForm;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import si.cat.client.presenter.TexturesPagePresenter.TexturesPage;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(fluent = true)
public class TexturesPageView extends Composite implements TexturesPage {

  interface TexturesPageBinder extends UiBinder<Widget, TexturesPageView> {}

  private static TexturesPageBinder uiBinder = GWT.create(TexturesPageBinder.class);

  @UiField
  Button addTextureHandler;

  @UiField
  Button updateTextureHandler;

  @UiField
  Button removeTextureHandler;

  @UiField
  ListBox texturesList;

  @UiField
  WellForm texturesForm;

  public TexturesPageView() {
    initWidget(uiBinder.createAndBindUi(this));
  }

  @Override
  public Widget asWidget() {
    return this;
  }

  @Override
  public void clear() {
    texturesList.clear();
  }
}
