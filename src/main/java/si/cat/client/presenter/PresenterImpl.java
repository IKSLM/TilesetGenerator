package si.cat.client.presenter;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Widget;

import lombok.Getter;
import lombok.Setter;
import si.cat.client.events.Bus;
import si.cat.client.view.View;

public abstract class PresenterImpl<T extends View, M> implements Presenter<T, M> {
  @Getter
  protected T view = null;

  @Getter
  @Setter
  protected M model = null;
  private List<HandlerRegistration> registrations = null;

  public PresenterImpl(T view, M model) {
    registrations = new ArrayList<HandlerRegistration>();

    this.view = view;
    this.model = model;

    bind();
  }

  @Override
  public void dispose() {
    synchronized (registrations) {
      for (HandlerRegistration reg : registrations) {
        reg.removeHandler();
      }

      registrations.clear();
    }
  }

  public final <H extends EventHandler> void addHandler(GwtEvent.Type<H> type, final H handler) {
    HandlerRegistration registration = Bus.addHandler(type, handler);

    synchronized (registrations) {
      registrations.add(registration);
    }
  }

  public final <H extends EventHandler> void addHandler(EventBus eventBus, GwtEvent.Type<H> type, final H handler) {
    HandlerRegistration registration = eventBus.addHandler(type, handler);

    synchronized (registrations) {
      registrations.add(registration);
    }
  }

  public final <H extends EventHandler> void addDomHandler(Widget widget, final H handler, DomEvent.Type<H> type) {
    HandlerRegistration registration = widget.addDomHandler(handler, type);

    synchronized (registrations) {
      registrations.add(registration);
    }
  }
}
