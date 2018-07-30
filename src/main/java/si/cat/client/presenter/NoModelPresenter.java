package si.cat.client.presenter;

import si.cat.client.view.View;

public abstract class NoModelPresenter<T extends View> extends PresenterImpl<T, Void> {

  public NoModelPresenter(T view) {
    super(view, null);
  }

}
