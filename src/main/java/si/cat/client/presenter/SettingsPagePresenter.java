package si.cat.client.presenter;

import javax.vecmath.Vector3d;

import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.user.client.ui.HasWidgets;

import si.cat.client.events.Bus;
import si.cat.client.events.BusHelper;
import si.cat.client.events.EventApplySettings;
import si.cat.client.events.EventGetSettings;
import si.cat.client.presenter.SettingsPagePresenter.SettingsPage;
import si.cat.client.view.View;
import si.cat.shared.model.Settings;

public class SettingsPagePresenter extends NoModelPresenter<SettingsPage> implements BusHelper {

  public interface SettingsPage extends View {

    TextBox ambientLightColor();

    TextBox directionalLightColor();

    TextBox directionalLightPositionX();

    TextBox directionalLightPositionY();

    TextBox directionalLightPositionZ();
  }

  public SettingsPagePresenter(SettingsPage view) {
    super(view);
  }

  @Override
  public void bind() {

    // Bind external settings applying.
    Bus.addHandler(EventApplySettings.TYPE, e -> {
      if (e.getOrigin() == SettingsPagePresenter.this) {
        return;
      }

      apply(e.getSettings());
    });

    // Bind loading of settings.
    post(new EventGetSettings(settings -> apply(settings)));

    // Bind applying of settings.
    view.ambientLightColor().addKeyDownHandler(e -> publishSettings(e));
    view.directionalLightColor().addKeyDownHandler(e -> publishSettings(e));
    view.directionalLightPositionX().addKeyDownHandler(e -> publishSettings(e));
    view.directionalLightPositionY().addKeyDownHandler(e -> publishSettings(e));
    view.directionalLightPositionZ().addKeyDownHandler(e -> publishSettings(e));
  }

  private void publishSettings(KeyDownEvent e) {

    if (e.getNativeKeyCode() != KeyCodes.KEY_ENTER) {
      return;
    }

    // Prepare settings object and publish.
    Settings settings = new Settings();

    settings.setAmbientLightColor(view.ambientLightColor().getText());
    settings.setDirectionalLightColor(view.directionalLightColor().getText());

    String x = view.directionalLightPositionX().getText();
    String y = view.directionalLightPositionY().getText();
    String z = view.directionalLightPositionZ().getText();

    Vector3d dlp = new Vector3d(Double.valueOf(x), Double.valueOf(y), Double.valueOf(z));

    settings.setDirectionalLightPosition(dlp);

    // Tell the system to use these settings. Set the origin so we do not re-set ours.
    post(new EventApplySettings(settings).setOrigin(this));
  }

  private void apply(Settings settings) {
    view.ambientLightColor().setText(settings.getAmbientLightColor());
    view.directionalLightColor().setText(settings.getDirectionalLightColor());

    Vector3d p = settings.getDirectionalLightPosition();

    view.directionalLightPositionX().setText(String.valueOf(p.x));
    view.directionalLightPositionY().setText(String.valueOf(p.y));
    view.directionalLightPositionZ().setText(String.valueOf(p.z));
  }

  @Override
  public void go(HasWidgets container) {
    container.add(view.asWidget());
  }
}
