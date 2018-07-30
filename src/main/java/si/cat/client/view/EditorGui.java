package si.cat.client.view;

import com.github.gwtbootstrap.client.ui.TabLink;
import com.github.gwtbootstrap.client.ui.TabPane;
import com.github.gwtbootstrap.client.ui.TabPanel;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

import lombok.Data;
import lombok.EqualsAndHashCode;
import thothbot.parallax.core.client.AnimatedScene;
import thothbot.parallax.core.client.RenderingPanel;

@Data
@EqualsAndHashCode(callSuper = false)
public class EditorGui extends Composite {

  @UiField
  RenderingPanel renderingPanel;

  @UiField
  TabPane texturesTab;

  @UiField
  TabPane projectTab;

  @UiField
  TabPane tilesTab;

  @UiField
  TabPane tiledTab;

  @UiField
  TabLink tiledTabLink;

  @UiField
  TabPanel tabPanel;

  @UiField
  TabPane settingsTab;

  public void initWidget(Widget widget, AnimatedScene scene) {
    super.initWidget(widget);

    renderingPanel.setBackground(0xCDCEFF);
    renderingPanel.setAnimatedScene(scene);
  }
}
