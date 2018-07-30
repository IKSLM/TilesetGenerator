package si.cat.client.view;

import com.github.gwtbootstrap.client.ui.Button;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import si.cat.client.presenter.ProjectPagePresenter.ProjectPage;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(fluent = true)
public class ProjectPageView extends Composite implements ProjectPage {

  interface ProjectPageBinder extends UiBinder<Widget, ProjectPageView> {}

  private static ProjectPageBinder uiBinder = GWT.create(ProjectPageBinder.class);

  @UiField
  Button save;

  @UiField
  Button saveAs;

  @UiField
  Button open;

  @UiField
  Button clear;

  public ProjectPageView() {
    initWidget(uiBinder.createAndBindUi(this));
  }

  @Override
  public Widget asWidget() {
    return this;
  }
}
