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
import si.cat.client.presenter.TilesPagePresenter.TilesPage;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(fluent = true)
public class TilesPageView extends Composite implements TilesPage {

  interface TilesPageBinder extends UiBinder<Widget, TilesPageView> {}

  private static TilesPageBinder uiBinder = GWT.create(TilesPageBinder.class);

  @UiField
  Button addTileHandler;

  @UiField
  Button mergeTilesHandler;

  @UiField
  Button removeTileHandler;

  @UiField
  Button clearSelectionHandler;

  @UiField
  Button sortTilesHandler;

  @UiField
  ListBox tileList;

  @UiField
  WellForm tilesForm;

  public TilesPageView() {
    initWidget(uiBinder.createAndBindUi(this));
  }

  @Override
  public Widget asWidget() {
    return this;
  }

  @Override
  public void clear() {
    tileList.clear();
  }

  @Override
  public void clearSelection() {
    tileList.setSelectedValue(null);
  }
}
