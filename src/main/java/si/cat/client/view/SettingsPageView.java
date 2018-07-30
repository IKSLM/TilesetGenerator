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
import si.cat.client.presenter.SettingsPagePresenter.SettingsPage;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(fluent = true)
public class SettingsPageView extends Composite implements SettingsPage {

  interface SettingsPageBinder extends UiBinder<Widget, SettingsPageView> {}

  private static SettingsPageBinder uiBinder = GWT.create(SettingsPageBinder.class);

  @UiField
  TextBox ambientLightColor;

  @UiField
  TextBox directionalLightColor;

  @UiField
  TextBox directionalLightPositionX;

  @UiField
  TextBox directionalLightPositionY;

  @UiField
  TextBox directionalLightPositionZ;

  public SettingsPageView() {
    initWidget(uiBinder.createAndBindUi(this));
  }

  @Override
  public Widget asWidget() {
    return this;
  }

}
