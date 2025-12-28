package com.thecsdev.betterstats.api.mcbs.view.tab;

import com.thecsdev.betterstats.api.mcbs.controller.tab.McbsEditorNullTab;
import com.thecsdev.betterstats.resources.BSSLang;
import com.thecsdev.betterstats.resources.BSSTex;
import com.thecsdev.common.math.UDim2;
import com.thecsdev.common.util.enumerations.CompassDirection;
import com.thecsdev.commonmc.api.client.gui.TElement;
import com.thecsdev.commonmc.api.client.gui.label.TLabelElement;
import com.thecsdev.commonmc.api.client.gui.misc.TTextureElement;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * {@link McbsEditorTabGUI} implementation that is used as fallback for
 * when no tabs are selected.
 * @see McbsEditorNullTab
 */
@Environment(EnvType.CLIENT)
public final class McbsEditorNullTabGUI extends McbsEditorTabGUI<McbsEditorNullTab>
{
	// ==================================================
	public McbsEditorNullTabGUI(@NotNull McbsEditorNullTab editorTab) throws NullPointerException {
		super(editorTab);
	}
	// ==================================================
	protected final @Override void initTabGuiCallback()
	{
		//bounding boxes math nonsense
		final var bb = getBounds();
		final int w3 = bb.width / 3;

		//create and add a texture element, for the silhouette
		final var tex_silhouette = new TTextureElement(BSSTex.gui_images_nostatsSilhouette());
		add(tex_silhouette);
		tex_silhouette.setBounds(new UDim2(0.5, 0, 0.5, 0), new UDim2(0, w3, 0, w3));
		tex_silhouette.move(-w3 / 2, -w3 / 2);
		tex_silhouette.colorProperty().set(0xFFFFFFFF, McbsEditorNullTabGUI.class);

		//initialize a single hint label, for what the user could do
		final var lbl_hint = new TLabelElement(BSSLang.gui_menubar_view()
				.append(Component.literal(" > ").withStyle(ChatFormatting.DARK_GRAY))
				.append(BSSLang.gui_menubar_view_localPlayerStats()));
		lbl_hint.setBounds(getBounds());
		lbl_hint.wrapTextProperty().set(true, McbsEditorNullTabGUI.class);
		lbl_hint.textAlignmentProperty().set(CompassDirection.CENTER, McbsEditorNullTabGUI.class);
		add(lbl_hint);
	}
	// ==================================================
	/**
	 * Initializes GUI that is shown when no statistics can be shown.
	 * @param target The target {@link TElement} where the GUI is to be initialized.
	 * @throws NullPointerException If the argument is {@code null}.
	 */
	public static final void initNoStatsGUI(@NotNull TElement target) throws NullPointerException
	{
		//not null requirements
		Objects.requireNonNull(target);

		//bounding boxes math nonsense
		final var bb = target.getBounds();
		final int w3 = bb.width / 3;

		//create and add a texture element, for the silhouette
		final var tex_silhouette = new TTextureElement(BSSTex.gui_images_nostatsSilhouette());
		target.add(tex_silhouette);
		tex_silhouette.setBounds(new UDim2(0.5, 0, 0.5, 0), new UDim2(0, w3, 0, w3));
		tex_silhouette.move(-w3 / 2, -w3 / 2);
		tex_silhouette.colorProperty().set(0xFFFFFFFF, McbsEditorNullTabGUI.class);

		//create and add a label, indicating no stats can be shown
		final var lbl = new TLabelElement(BSSLang.gui_statsview_stats_noStats());
		lbl.setBounds(bb.x, bb.y + (bb.height / 2) - 7, bb.width, 14);
		lbl.textAlignmentProperty().set(CompassDirection.CENTER, McbsEditorNullTabGUI.class);
		lbl.textColorProperty().set(0xFFFFFFFF, McbsEditorNullTabGUI.class);
		target.add(lbl);
	}
	// ==================================================
}
