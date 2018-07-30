package si.cat.client.utils;

import com.google.gwt.dom.client.Element;

public class NativeHelper {
  // @formatter:off
  public static final native double getNaturalHeight(Element i) /*-{
                                                                return i.naturalHeight;
                                                                }-*/;

  public static final native double getNaturalWidth(Element i) /*-{
                                                               return i.naturalWidth;
                                                               }-*/;
  // @formatter:on
}
