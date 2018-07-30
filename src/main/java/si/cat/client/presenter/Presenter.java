package si.cat.client.presenter;

import com.google.gwt.user.client.ui.HasWidgets;

public interface Presenter<T, M> {
  T getView();

  M getModel();

  void setModel(M model);

  void go(final HasWidgets container);

  void bind();

  void dispose();
}
