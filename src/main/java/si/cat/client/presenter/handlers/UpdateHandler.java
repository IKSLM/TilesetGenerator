package si.cat.client.presenter.handlers;

import si.cat.client.presenter.EditorPresenter.SceneProxy;

public interface UpdateHandler {
  boolean onUpdate(SceneProxy proxy, double delta);
}
