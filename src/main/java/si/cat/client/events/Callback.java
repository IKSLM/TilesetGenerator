package si.cat.client.events;

public interface Callback<T> {
  void onCallback(T object);
}
