package si.cat.client.view;

import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import si.cat.client.presenter.TiledPagePresenter.TiledPage;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(fluent = true)
public class TiledPageView extends Composite implements TiledPage {

  interface TiledPageBinder extends UiBinder<Widget, TiledPageView> {}

  private static TiledPageBinder uiBinder = GWT.create(TiledPageBinder.class);

  @UiField
  TextBox mapTileWidth;

  @UiField
  TextBox mapTileHeight;

  @UiField
  TextBox tilesetTileWidth;

  @UiField
  TextBox tilesetTileHeight;

  @UiField
  TextBox selectedIndices;

  public TiledPageView() {
    initWidget(uiBinder.createAndBindUi(this));
  }

  @Override
  public Widget asWidget() {
    return this;
  }
}
