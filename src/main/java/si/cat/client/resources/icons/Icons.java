package si.cat.client.resources.icons;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface Icons extends ClientBundle {
  public Icons INSTANCE = GWT.create(Icons.class);

  ImageResource info_32();

  ImageResource warning_32();

  ImageResource error_32();
}
