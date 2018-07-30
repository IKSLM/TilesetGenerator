package si.cat.client.presenter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.CheckBox;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.RadioButton;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;

import si.cat.client.events.BusHelper;
import si.cat.client.events.EventGetSelectedTexture;
import si.cat.client.events.EventOnTextureChanged;
import si.cat.client.events.EventOnTextureChanged.Change;
import si.cat.client.events.EventRefreshTiles;
import si.cat.client.presenter.TileEditorPresenter.TileEditor;
import si.cat.client.ui.GUIHelper;
import si.cat.client.ui.UVEditor;
import si.cat.client.view.View;
import si.cat.shared.Rect;
import si.cat.shared.model.OverlayTile;
import si.cat.shared.model.TexturingInfo;
import si.cat.shared.model.Tile;
import si.cat.shared.model.Tile.AlignDepth;
import si.cat.shared.model.Tile.AlignHeight;
import si.cat.shared.model.Tile.AlignWidth;
import si.cat.shared.model.Tile.Side;

public class TileEditorPresenter extends PresenterImpl<TileEditor, Tile> implements BusHelper {

  public interface TileEditor extends View {

    HTMLPanel tileEditorPanel();

    ListBox allSidesTexture();

    ListBox topTexture();

    ListBox leftTexture();

    ListBox rightTexture();

    //
    TextBox tileSize();

    TextBox tileWidth();

    RadioButton widthAlignLeft();

    RadioButton widthAlignCenter();

    RadioButton widthAlignRight();

    TextBox tileHeight();

    RadioButton heightAlignBottom();

    RadioButton heightAlignCenter();

    RadioButton heightAlignTop();

    TextBox tileDepth();

    RadioButton depthAlignFront();

    RadioButton depthAlignCenter();

    RadioButton depthAlignBack();
    //

    Button allSidesApplyTexture();

    Button allSidesUV();

    Button topUV();

    Button leftUV();

    Button rightUV();

    Button topMaskUV();

    Button leftMaskUV();

    Button rightMaskUV();

    ListBox topMask();

    ListBox leftMask();

    ListBox rightMask();

    CheckBox uvScaling();

    CheckBox billboard();

    TextBox offsetX();

    TextBox offsetY();

    TextBox offsetZ();
  }

  private static final NumberFormat format = NumberFormat.getFormat("#.##");
  private Rect allUV = null;
  private UVEditor uvEditor = new UVEditor();

  public TileEditorPresenter(TileEditor view) {
    super(view, null);
  }

  @Override
  public void bind() {

    // Take care of UV scaling toggle.
    view.uvScaling().addValueChangeHandler(e -> {
      updateSizeAndAlignment();
    });

    view.billboard().addValueChangeHandler(e -> {
      updateSizeAndAlignment();
    });

    // Take care of per-side mask UV binding.
    for (Side side : Side.values()) {
      getMaskUVButton(side).addClickHandler(e -> {

        Tile tile = getModel();

        if (tile == null) {
          return;
        }

        TexturingInfo info = tile.getMasks().get(side);

        if (info == null) {
          return;
        }

        uvEditor.show(info.getAlias(), Collections.singletonList(info.getUv()), uv -> {

          // Set the new UV for the given side.
          info.setUv(uv);

          // And refresh the tile.
          post(new EventRefreshTiles(tile));
        });
      });
    }

    // Listen for side mask changes.
    for (Side side : Side.values()) {
      ListBox list = getMaskListBox(side);

      list.addChangeHandler(e -> {

        Tile tile = getModel();

        if (tile == null) {
          return;
        }

        String alias = list.getSelectedValue();

        // Set the texture to the correct side.
        tile.getMasks().get(side).setAlias(alias);

        // And refresh the tile.
        post(new EventRefreshTiles(tile));

      });
    }

    // Handle apply all textures and UVs. This is so we do not have to select the "all sides
    // texture" twice in case we want to apply the already selected texture to all sides.
    view.allSidesApplyTexture().addClickHandler(e -> {
      applyToAllSides();
    });

    // All sides texture selection sets the same texture to all sides.
    view.allSidesTexture().addChangeHandler(e -> {
      applyToAllSides();
    });

    // Take care of all UV.
    view.allSidesUV().addClickHandler(e -> {

      Tile tile = getModel();

      if (tile == null) {
        return;
      }

      String alias = view.allSidesTexture().getSelectedValue();

      List<Rect> uvs = tile.getTextures().values().stream().map(ti -> ti.getUv()).filter(uv -> uv != null)
          .collect(Collectors.toList());

      uvEditor.show(alias, uvs, uv -> {
        allUV = uv;

        for (Side side : Side.values()) {
          tile.getTextures().get(side).set(alias, allUV);
        }

        post(new EventRefreshTiles(tile));
      });
    });

    // Take care of per-side UV binding.
    for (Side side : Side.values()) {
      getTextureUVButton(side).addClickHandler(e -> {

        Tile tile = getModel();

        if (tile == null) {
          return;
        }

        TexturingInfo info = tile.getTextures().get(side);

        if (info == null) {
          return;
        }

        uvEditor.show(info.getAlias(), Collections.singletonList(info.getUv()), uv -> {

          // Set the new UV for the given side.
          info.setUv(uv);

          // And refresh the tile.
          post(new EventRefreshTiles(tile));
        });
      });
    }

    // Take care of texture modifications.
    addHandler(EventOnTextureChanged.TYPE, e -> {

      // When adding or removing texture, we need to update the drop downs.
      if (e.getChange() == Change.ADDED || e.getChange() == Change.REMOVED) {
        setModel(getModel());
      }
    });

    // Listen for side texture changes.
    for (Side side : Side.values()) {
      ListBox list = getTextureListBox(side);

      list.addChangeHandler(e -> {

        Tile tile = getModel();

        if (tile == null) {
          return;
        }

        String alias = list.getSelectedValue();

        // Set the texture to the correct side.
        tile.getTextures().get(side).setAlias(alias);

        // And refresh the tile.
        post(new EventRefreshTiles(tile));

      });
    }

    // Listen for size change.
    view.tileSize().addKeyDownHandler(e -> refresh(e));
    view.tileWidth().addKeyDownHandler(e -> refresh(e));
    view.tileHeight().addKeyDownHandler(e -> refresh(e));
    view.tileDepth().addKeyDownHandler(e -> refresh(e));

    // Listen for align changes.
    view.widthAlignLeft().addValueChangeHandler(e -> refresh(e));
    view.widthAlignCenter().addValueChangeHandler(e -> refresh(e));
    view.widthAlignRight().addValueChangeHandler(e -> refresh(e));

    view.heightAlignBottom().addValueChangeHandler(e -> refresh(e));
    view.heightAlignCenter().addValueChangeHandler(e -> refresh(e));
    view.heightAlignTop().addValueChangeHandler(e -> refresh(e));

    view.depthAlignFront().addValueChangeHandler(e -> refresh(e));
    view.depthAlignCenter().addValueChangeHandler(e -> refresh(e));
    view.depthAlignBack().addValueChangeHandler(e -> refresh(e));

    // Listen for offset change.
    view.offsetX().addKeyDownHandler(e -> refresh(e));
    view.offsetY().addKeyDownHandler(e -> refresh(e));
    view.offsetZ().addKeyDownHandler(e -> refresh(e));
  }

  private void refresh(KeyDownEvent e) {
    if (e.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
      updateSizeAndAlignment();
    }
  }

  private void refresh(ValueChangeEvent<Boolean> e) {
    if (e.getValue()) {
      updateSizeAndAlignment();
    }
  }

  private void applyToAllSides() {

    Tile tile = getModel();

    if (tile == null) {
      return;
    }

    String alias = view.allSidesTexture().getSelectedValue();

    // Set the texture to the correct side.
    for (Side side : Side.values()) {
      tile.getTextures().get(side).set(alias, allUV);
    }

    setModel(getModel());

    // And refresh the tile.
    post(new EventRefreshTiles(tile));
  }

  private void updateSizeAndAlignment() {

    Tile tile = getModel();

    if (tile == null) {
      return;
    }

    // Update the sizes and alignments.
    double tileSize = Double.valueOf(view.tileSize().getText());
    double tileWidth = Double.valueOf(view.tileWidth().getText()) / 100.0;
    double tileHeight = Double.valueOf(view.tileHeight().getText()) / 100.0;
    double tileDepth = Double.valueOf(view.tileDepth().getText()) / 100.0;

    tile.setSize(tileSize);
    tile.getRelativeSizes().set(tileWidth, tileHeight, tileDepth);

    if (view.widthAlignLeft().getValue()) {
      tile.setAlignWidth(AlignWidth.Left);
    } else if (view.widthAlignCenter().getValue()) {
      tile.setAlignWidth(AlignWidth.Center);
    } else if (view.widthAlignRight().getValue()) {
      tile.setAlignWidth(AlignWidth.Right);
    }

    if (view.heightAlignBottom().getValue()) {
      tile.setAlignHeight(AlignHeight.Bottom);
    } else if (view.heightAlignCenter().getValue()) {
      tile.setAlignHeight(AlignHeight.Center);
    } else if (view.heightAlignTop().getValue()) {
      tile.setAlignHeight(AlignHeight.Top);
    }

    if (view.depthAlignFront().getValue()) {
      tile.setAlignDepth(AlignDepth.Front);
    } else if (view.depthAlignCenter().getValue()) {
      tile.setAlignDepth(AlignDepth.Center);
    } else if (view.depthAlignBack().getValue()) {
      tile.setAlignDepth(AlignDepth.Back);
    }

    tile.setUvScaling(view.uvScaling().getValue());

    tile.setBillboard(view.billboard().getValue());

    double offsetX = Double.valueOf(view.offsetX().getText()) / 100.0;
    double offsetY = Double.valueOf(view.offsetY().getText()) / 100.0;
    double offsetZ = Double.valueOf(view.offsetZ().getText()) / 100.0;

    tile.getRelativeOffsets().set(offsetX, offsetY, offsetZ);

    // And refresh the tile.
    post(new EventRefreshTiles(tile));
  }

  private void setTextureAliases(ListBox listBox, Set<String> aliases) {
    listBox.clear();
    // Empty - no selection option.
    listBox.addItem("");
    aliases.forEach(a -> listBox.addItem(a));
  }

  @Override
  public void setModel(Tile tile) {
    super.setModel(tile);

    if (tile == null) {
      return;
    }

    // If this is an overlay tile, disable the edits; overlay tiles are handled on per-tile basis.
    if (tile instanceof OverlayTile) {
      GUIHelper.enableAllChildren(view.tileEditorPanel(), false);
      return;
    }

    GUIHelper.enableAllChildren(view.tileEditorPanel(), true);

    getTextureDescriptorCache(cache -> {

      Set<String> textures = cache.getAliases();

      setTextureAliases(view.allSidesTexture(), textures);
      setTextureAliases(view.topTexture(), textures);
      setTextureAliases(view.leftTexture(), textures);
      setTextureAliases(view.rightTexture(), textures);
      setTextureAliases(view.topMask(), textures);
      setTextureAliases(view.leftMask(), textures);
      setTextureAliases(view.rightMask(), textures);

      apply(textures);
    });
  }

  private void apply(Set<String> aliases) {

    // Set the texture if one has not yet been set.
    if (!aliases.isEmpty()) {
      post(new EventGetSelectedTexture(alias -> {
        applyInternal(alias, aliases);
      }));
    } else {
      applyInternal(null, aliases);
    }
  }

  private void applyInternal(String alias, Set<String> aliases) {
    // Set the selected texture of the first in the list.
    String first = alias;

    if (first == null && !aliases.isEmpty()) {
      first = aliases.iterator().next();
    }

    boolean updated = false;

    Tile tile = getModel();

    if (tile == null) {
      return;
    }

    if (first != null) {
      for (Side side : Tile.Side.values()) {
        if (tile.getTextures().get(side).getAlias() == null) {
          tile.getTextures().get(side).setAlias(first);
          updated = true;
        }
      }
    }

    // Select the texture.
    Arrays.asList(Tile.Side.values()).forEach(side -> {
      String texture = tile.getTextures().get(side).getAlias();
      if (texture != null) {
        getTextureListBox(side).setSelectedValue(texture);
      }
    });

    // Select the mask.
    Arrays.asList(Tile.Side.values()).forEach(side -> {
      String mask = tile.getMasks().get(side).getAlias();
      if (mask != null) {
        getMaskListBox(side).setSelectedValue(mask);
      }
    });

    // If all the sides have same texture, apply it to the 'all sides' texture selection.
    boolean same = (tile.getTextures().values().stream().filter(t -> t != null).map(t -> t.getAlias()).distinct()
        .collect(Collectors.toList()).size() == 1);

    if (same) {
      view.allSidesTexture().setSelectedValue(tile.getTextures().get(Side.Top).getAlias());
    }

    // Set the size boxes and alignments.
    view.tileSize().setText(String.valueOf(Math.round(tile.getSize())));
    view.tileWidth().setText(format.format(tile.getRelativeSizes().x * 100.0));
    view.tileHeight().setText(format.format(tile.getRelativeSizes().y * 100.0));
    view.tileDepth().setText(format.format(tile.getRelativeSizes().z * 100.0));

    switch (tile.getAlignWidth()) {
      case Center:
        view.widthAlignCenter().setValue(true);
        break;
      case Left:
        view.widthAlignLeft().setValue(true);
        break;
      case Right:
        view.widthAlignRight().setValue(true);
        break;
    }

    switch (tile.getAlignHeight()) {
      case Bottom:
        view.heightAlignBottom().setValue(true);
        break;
      case Center:
        view.heightAlignCenter().setValue(true);
        break;
      case Top:
        view.heightAlignTop().setValue(true);
        break;
    }

    switch (tile.getAlignDepth()) {
      case Back:
        view.depthAlignBack().setValue(true);
        break;
      case Center:
        view.depthAlignCenter().setValue(true);
        break;
      case Front:
        view.depthAlignFront().setValue(true);
        break;
    }

    // Set the scaling.
    view.uvScaling().setValue(tile.isUvScaling());

    // Set the relative offsets.
    view.offsetX().setText(format.format(tile.getRelativeOffsets().x * 100.0));
    view.offsetY().setText(format.format(tile.getRelativeOffsets().y * 100.0));
    view.offsetZ().setText(format.format(tile.getRelativeOffsets().z * 100.0));

    // Set billboard.
    view.billboard().setValue(tile.isBillboard());

    // If we have set the default texture, refresh the tile.
    if (updated) {
      post(new EventRefreshTiles(tile));
    }
  }

  private ListBox getTextureListBox(Side side) {
    switch (side) {
      case Left:
        return view.leftTexture();
      case Right:
        return view.rightTexture();
      case Top:
        return view.topTexture();
    }
    return null;
  }

  private ListBox getMaskListBox(Side side) {
    switch (side) {
      case Left:
        return view.leftMask();
      case Right:
        return view.rightMask();
      case Top:
        return view.topMask();
    }
    return null;
  }

  private Button getTextureUVButton(Side side) {
    switch (side) {
      case Left:
        return view.leftUV();
      case Right:
        return view.rightUV();
      case Top:
        return view.topUV();
    }
    return null;
  }

  private Button getMaskUVButton(Side side) {
    switch (side) {
      case Left:
        return view.leftMaskUV();
      case Right:
        return view.rightMaskUV();
      case Top:
        return view.topMaskUV();
    }
    return null;
  }

  @Override
  public void go(HasWidgets container) {
    container.add(view.asWidget());
  }
}
