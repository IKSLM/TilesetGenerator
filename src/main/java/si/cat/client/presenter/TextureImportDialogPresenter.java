package si.cat.client.presenter;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.FileUpload;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Image;

import lombok.Data;
import si.cat.client.DataServiceAsync;
import si.cat.client.events.BusHelper;
import si.cat.client.events.Callback2;
import si.cat.client.events.EventOnRpcError;
import si.cat.client.presenter.TextureImportDialogPresenter.TextureDialogModel;
import si.cat.client.presenter.TextureImportDialogPresenter.TextureImportDialog;
import si.cat.client.view.IsDialog;
import si.cat.client.view.View;

public class TextureImportDialogPresenter extends PresenterImpl<TextureImportDialog, TextureDialogModel>
    implements
      BusHelper {

  @Data
  public static class TextureDialogModel {
    private final String initialAlias;
    private final Callback2<String, String> callback;
  }

  public interface TextureImportDialog extends View, IsDialog {

    FileUpload loadLocalHandler();

    TextBox loadUrlBox();

    Button okHandler();

    Button cancelHandler();

    Image previewImage();

    TextBox textureAliasBox();

  }

  private String url = null;

  public TextureImportDialogPresenter(TextureImportDialog view, TextureDialogModel model) {
    super(view, model);
  }

  @Override
  public void bind() {

    view.textureAliasBox().setText(getModel().getInitialAlias());

    // Handle upload local texture.
    FileUpload textureUpload = view.loadLocalHandler();

    textureUpload.addChangeHandler(new ChangeHandler() {

      @Override
      public void onChange(ChangeEvent event) {
        if (textureUpload.getFilename() != null && !textureUpload.getFilename().isEmpty()) {
          loadLocalImage(TextureImportDialogPresenter.this, event.getNativeEvent());
        }
      }
    });

    // Handle upload texture from URL.
    TextBox textureUrlBox = view.loadUrlBox();

    textureUrlBox.addKeyDownHandler(e -> {
      if (e.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
        loadRemoteImage(TextureImportDialogPresenter.this, textureUrlBox.getText());
      }
    });

    // Hide the add texture dialog, do not add the texture.
    view.cancelHandler().addClickHandler(e -> {
      view.hide();
    });

    // Hide the add texture dialog, add the texture.
    view.okHandler().addClickHandler(e -> {

      String alias = view.textureAliasBox().getText();

      view.hide();

      getModel().getCallback().onCallback(alias, url);
    });
  }

  private void loadRemoteImage(TextureImportDialogPresenter textureDialogPresenter, String url) {
    DataServiceAsync.Util.getInstance().loadRemoteTexture(url, new AsyncCallback<String>() {

      @Override
      public void onSuccess(String result) {
        imageLoaded(null, result);
      }

      @Override
      public void onFailure(Throwable caught) {
        post(new EventOnRpcError(caught));
      }
    });
  }

  //@formatter:off
  private native String loadLocalImage(TextureImportDialogPresenter source, NativeEvent event) /*-{
   var image = event.target.files[0];
   var source = source;

   // Check if file is an image.
   if (image.type.match('image.*')) {

     var reader = new FileReader();
     reader.onload = function(e) {
       source.@si.cat.client.presenter.TextureImportDialogPresenter::imageLoaded(Ljava/lang/String;Ljava/lang/String;)(image.name, e.target.result);
     }

     // Start reading the image.
     reader.readAsDataURL(image);
   }
  }-*/;
  //@formatter:on

  private void imageLoaded(String name, String url) {

    // Set the name if provided.
    if (name != null && !name.trim().isEmpty()) {
      if (name.length() > 4 && name.charAt(name.length() - 4) == '.') {
        name = name.substring(0, name.length() - 4);
      }

      view.textureAliasBox().setText(name);
    }

    this.url = url;

    view.previewImage().setUrl(url);
  }

  @Override
  public void go(HasWidgets container) {
    view.show();
  }

}
