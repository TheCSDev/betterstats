package com.thecsdev.betterstats.client.gui.mcbs_view.editor;

import com.thecsdev.betterstats.api.mcbs.controller.McbsEditor;
import com.thecsdev.betterstats.api.mcbs.controller.McbsEditorTab;
import com.thecsdev.betterstats.api.mcbs.view.McbsEditorGUI;
import com.thecsdev.betterstats.resources.BSSSprites;
import com.thecsdev.common.util.enumerations.CompassDirection;
import com.thecsdev.commonmc.api.client.gui.TElement;
import com.thecsdev.commonmc.api.client.gui.panel.TPanelElement;
import com.thecsdev.commonmc.api.client.gui.render.TGuiGraphics;
import com.thecsdev.commonmc.api.client.gui.widget.TButtonWidget;
import com.thecsdev.commonmc.api.client.gui.widget.TClickableWidget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * <b>Tabbed document interface</b> tab-strip element that serves as a navigational
 * component, enabling users to switch between different {@link McbsEditorTab} instances.
 */
@ApiStatus.Internal
@Environment(EnvType.CLIENT)
public final class TabStripPanel extends TElement
{
	// ================================================== ==================================================
	//                                      TabStripPanel IMPLEMENTATION
	// ================================================== ==================================================
	private final @NotNull McbsEditor mcbsEditor;
	// ==================================================
	public TabStripPanel(@NotNull McbsEditor mcbsEditor) {
		this.mcbsEditor = Objects.requireNonNull(mcbsEditor);
	}
	// ==================================================
	public final @Override void renderCallback(@NotNull TGuiGraphics pencil) {
		final var bb = getBounds();
		pencil.drawGuiSprite(BSSSprites.gui_panel_bgTabstrip(), bb.x, bb.y, bb.width, bb.height, -1);
	}
	public final @Override void postRenderCallback(@NotNull TGuiGraphics pencil) {
		final var bb = getBounds();
		pencil.drawGuiSprite(BSSSprites.gui_panel_fgTabstrip(), bb.x, bb.y, bb.width, bb.height, -1);
	}
	// --------------------------------------------------
	protected final @Override void initCallback()
	{
		//initialize the panel
		final var panel = new TPanelElement.Paintable(0, 0, 0x33FFFFFF);
		panel.setBounds(1, 1, getBounds().width - 2, getBounds().height - 2);
		panel.scrollPaddingProperty().set(0, McbsEditorGUI.class);
		addRel(panel);

		//iterate tabs and initialize them
		for(final var tab : this.mcbsEditor.getTabsReadOnly())
			initEntry(panel, tab);
	}
	// ==================================================
	/**
	 * Initializes a tab-strip entry for the specified {@link McbsEditorTab}.
	 * @param panel The parent panel to which the entry will be added.
	 * @param tab The {@link McbsEditorTab} for which the entry is being created.
	 */
	private final void initEntry(@NotNull TPanelElement panel, @NotNull McbsEditorTab tab)
	{
		//ensure arguments are not null
		Objects.requireNonNull(panel);
		Objects.requireNonNull(tab);

		//create and add an entry element
		final var entry = new EntryElement(tab);
		final var pcbb  = panel.getContentBounds();
		entry.setBounds(
				pcbb.endX, pcbb.y,
				entry.getLabel().getTextWidth() + panel.getBounds().height + 5,
				panel.getBounds().height);
		panel.add(entry);
	}
	// ================================================== ==================================================
	//                                       EntryElement IMPLEMENTATION
	// ================================================== ==================================================
	/**
	 * <b>Tabbed document interface</b> tab-strip <b>entry</b> element that serves as
	 * a clickable element for switching to a specific {@link McbsEditorTab}.
	 */
	private final class EntryElement extends TButtonWidget.Transparent
	{
		// ==================================================
		private final @NotNull McbsEditorTab tab;
		// ==================================================
		public EntryElement(@NotNull McbsEditorTab tab)
		{
			//initialize fields
			this.tab = Objects.requireNonNull(tab);
			//configure properties
			enabledProperty().set(!isSelected(), TabStripPanel.class);
			getLabel().setText(tab.getDisplayName());
			getLabel().textAlignmentProperty().set(CompassDirection.WEST, EntryElement.class);
			getLabel().textScaleProperty().set(0.85, EntryElement.class);
			getLabel().textColorProperty().set(isSelected() ? 0xFFc5e762 : 0xEEFFFFFF, EntryElement.class);
			super.eClicked.register(btn -> {
				btn.enabledProperty().set(false, TabStripPanel.class);
				TabStripPanel.this.mcbsEditor.setCurrentTab(this.tab);
			});
		}
		// ==================================================
		public final @Override void renderCallback(@NotNull TGuiGraphics pencil)
		{
			final var bb = getBounds();
			pencil.drawGuiSprite(
					isSelected() ?
							BSSSprites.gui_panel_tabentrySelected() :
							BSSSprites.gui_panel_tabentry(),
					bb.x, bb.y, bb.width, bb.height, -1);
		}
		// --------------------------------------------------
		@SuppressWarnings("SuspiciousNameCombination") //sus names are among us
		protected final @Override void initCallback()
		{
			//initialize super label
			super.initCallback();
			//initialize close button
			final var bb = getBounds();
			final var btn_close = new TButtonWidget.Paintable(0, 0, 0x33888888);
			btn_close.setBounds(bb.endX - bb.height, bb.y, bb.height, bb.height);
			btn_close.getLabel().setText(Component.literal("x"));
			btn_close.getLabel().textAlignmentProperty().set(CompassDirection.CENTER, EntryElement.class);
			btn_close.getLabel().textScaleProperty().set(0.7, EntryElement.class);
			btn_close.getLabel().textColorProperty().set(0xBBFFFFFF, EntryElement.class);
			btn_close.eClicked.register(__ -> TabStripPanel.this.mcbsEditor.removeTab(this.tab));
			add(btn_close);
		}
		// ==================================================
		/**
		 * Returns {@code true} if the corresponding tab is currently selected.
		 */
		private final boolean isSelected() {
			return TabStripPanel.this.mcbsEditor.getCurrentTab() == this.tab;
		}
		// ==================================================
	}
	// ================================================== ==================================================
}
