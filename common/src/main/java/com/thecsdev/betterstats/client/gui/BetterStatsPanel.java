package com.thecsdev.betterstats.client.gui;

import com.thecsdev.betterstats.api.client.gui.screen.BetterStatsScreen;
import com.thecsdev.betterstats.api.client.gui.screen.IBetterStatsGui;
import com.thecsdev.betterstats.api.client.gui.statstab.StatsTab;
import com.thecsdev.betterstats.api.client.registry.BClientRegistries;
import com.thecsdev.betterstats.api.mcbs.McbsStats;
import com.thecsdev.betterstats.client.gui.statstab.StatsTabSettings;
import com.thecsdev.betterstats.resources.BSSLang;
import com.thecsdev.betterstats.resources.BSSSprites;
import com.thecsdev.common.math.UDim2;
import com.thecsdev.common.util.enumerations.CompassDirection;
import com.thecsdev.commonmc.api.client.gui.TElement;
import com.thecsdev.commonmc.api.client.gui.label.TLabelElement;
import com.thecsdev.commonmc.api.client.gui.misc.TTextureElement;
import com.thecsdev.commonmc.api.client.gui.panel.TPanelElement;
import com.thecsdev.commonmc.api.client.gui.render.TGuiGraphics;
import com.thecsdev.commonmc.api.client.gui.tooltip.TTooltip;
import com.thecsdev.commonmc.api.client.gui.widget.TButtonWidget;
import com.thecsdev.commonmc.api.client.gui.widget.TScrollBarWidget;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * The main {@link TElement} that goes on a {@link BetterStatsScreen}.
 */
public final class BetterStatsPanel extends TElement
{
	// ================================================== ==================================================
	//                                   BetterStatsPanel IMPLEMENTATION
	// ================================================== ==================================================
	@SuppressWarnings("NotNullFieldNotInitialized") //assigned automatically later
	private @NotNull IBetterStatsGui statsGui;
	// ==================================================
	protected final @Override void initCallback()
	{
		//find the stats GUI instance in a parent
		if((this.statsGui = (IBetterStatsGui) findParent(p -> p instanceof IBetterStatsGui).orElse(null)) == null)
			throw new IllegalStateException(getClass() + " requires that its parent GUI element be a " + IBetterStatsGui.class + " instance");

		//initialize and add the menu bar
		final var panel_menu = new MenubarPanel();
		add(panel_menu);
		panel_menu.setBounds(UDim2.ZERO, new UDim2(1, 0, 0, 15));

		//initialize and add the stats filters panel
		final var panel_filters = new FiltersPanel();
		add(panel_filters);
		panel_filters.setBounds(new UDim2(0, 0, 0, 20), new UDim2(0.3, -5, 1, -20 - 25));

		//initialize and add the stats tab itself
		final var panel_stats = new StatsPanel();
		add(panel_stats);
		panel_stats.setBounds(new UDim2(0.3, 0, 0, 20), new UDim2(0.7, 0, 1, -20));

		//initialize and add the navigation panel
		final var panel_nav = new NavigationPanel();
		add(panel_nav);
		panel_nav.setBounds(new UDim2(0, 0, 1, -20), new UDim2(0.3, -5, 0, 20));
	}
	// ================================================== ==================================================
	//                                       FiltersPanel IMPLEMENTATION
	// ================================================== ==================================================
	/**
	 * The "Filters" panel in a {@link StatsTab}'s GUI, featuring user interface
	 * for filtering statistics on the {@link StatsPanel}.
	 */
	private final class FiltersPanel extends TElement
	{
		// ==================================================
		public FiltersPanel() { super(); }
		// ==================================================
		protected final @Override void initCallback()
		{
			//initialize the panel
			final var panel = new TPanelElement.Paintable();
			panel.backgroundColorProperty().set(0, FiltersPanel.class);
			panel.outlineColorProperty().set(0, FiltersPanel.class);
			panel.focusedOutlineColorProperty().set(0x33FFFFFF, FiltersPanel.class);
			add(panel);
			panel.setBounds(new UDim2(0, 1, 0, 1), new UDim2(1, -8 - 1, 1, -2));
			panel.scrollPaddingProperty().set(8, BetterStatsPanel.class);

			//initialize the scroll-bar
			final var scroll = new TScrollBarWidget.Flat(panel);
			add(scroll);
			scroll.setBounds(new UDim2(1, -8, 0, 0), new UDim2(0, 8, 1, 0));

			//initialize the filters
			BetterStatsPanel.this.statsGui.getStatsTab().initFilters(new StatsTab.FiltersInitContext()
			{
				// --------------------------------------------------
				public final @Override @NotNull TPanelElement getPanel() { return panel; }
				public final @Override @NotNull StatsTab.Filters getFilters() { return BetterStatsPanel.this.statsGui.getFilters(); }
				public final @Override @NotNull StatsTab getStatsTab() { return BetterStatsPanel.this.statsGui.getStatsTab(); }
				public final @Override void setStatsTab(@NotNull StatsTab tab) throws NullPointerException { BetterStatsPanel.this.statsGui.setStatsTab(tab); }
				// --------------------------------------------------
				StatsTab oldTab = getStatsTab(); //keeps track of changes to the stats tab
				public final @Override void applyFilters() {
					//find the screen
					final var screen = screenProperty().get();
					if(screen == null) return;
					final var newTab = getStatsTab();
					//if the tab did not update, just refresh the stats panel
					if(newTab == this.oldTab)
						screen.findChild(c -> c instanceof StatsPanel, true).ifPresent(TElement::clearAndInit);
					else screen.clearAndInit();
					//keep track of changes to the stats tab
					this.oldTab = newTab;
				}
				// --------------------------------------------------
			});
		}
		// ==================================================
		public final @Override void renderCallback(@NotNull TGuiGraphics pencil) {
			final var bb = getBounds();
			pencil.drawGuiSprite(BSSSprites.gui_panel_bgFilters(), bb.x, bb.y, bb.width, bb.height, -1);
		}
		public final @Override void postRenderCallback(@NotNull TGuiGraphics pencil) {
			final var bb = getBounds();
			pencil.drawGuiSprite(BSSSprites.gui_panel_fgFilters(), bb.x, bb.y, bb.width, bb.height, -1);
		}
		// ==================================================
	}
	// ================================================== ==================================================
	//                                         StatsPanel IMPLEMENTATION
	// ================================================== ==================================================
	/**
	 * The "Statistics" panel in a given {@link StatsTab}'s GUI, showing statistics
	 * related to said selected {@link StatsTab}.
	 */
	private final class StatsPanel extends TElement
	{
		// ==================================================
		public StatsPanel() { super(); }
		// ==================================================
		protected final @Override void initCallback()
		{
			//initialize the panel
			final var panel = new TPanelElement.Paintable();
			panel.backgroundColorProperty().set(0, StatsPanel.class);
			panel.outlineColorProperty().set(0, StatsPanel.class);
			panel.focusedOutlineColorProperty().set(0x33FFFFFF, StatsPanel.class);
			add(panel);
			panel.setBounds(new UDim2(0, 1, 0, 1), new UDim2(1, -8 - 1, 1, -2));
			panel.scrollPaddingProperty().set(8, BetterStatsPanel.class);

			//initialize the scroll-bar
			final var scroll = new TScrollBarWidget.Flat(panel);
			add(scroll);
			scroll.setBounds(new UDim2(1, -8, 0, 0), new UDim2(0, 8, 1, 0));

			//initialize the stats
			BetterStatsPanel.this.statsGui.getStatsTab().initStats(new StatsTab.StatsInitContext() {
				public final @Override @NotNull TPanelElement getPanel() { return panel; }
				public final @Override @NotNull StatsTab.Filters getFilters() { return BetterStatsPanel.this.statsGui.getFilters(); }
				public final @Override @NotNull McbsStats getStatsProvider() { return BetterStatsPanel.this.statsGui.getMcbsFile().getStats(); }
				public final @Override void refresh() { StatsPanel.this.clearAndInit(); }
			});

			//finishing touches
			if(!panel.isEmpty()) centerPanelContents(panel);
			else                 initNoStatsLabel(panel);
		}
		// --------------------------------------------------
		/**
		 * Initializes the "no stats yet" label.
		 * @param panel The target {@link TPanelElement}.
		 * @throws NullPointerException If the argument is {@code null}.
		 */
		@ApiStatus.Internal
		private static final void initNoStatsLabel(@NotNull TPanelElement panel) throws NullPointerException
		{
			final var pbb   = panel.getBounds();
			final var padd  = panel.scrollPaddingProperty().getI();
			final var label = new TLabelElement();
			label.setBounds(pbb.x + padd, pbb.y + padd, pbb.width - (padd * 2), pbb.height - (padd * 2));
			label.setText(BSSLang.gui_statstab_stats_noStats());
			label.textAlignmentProperty().set(CompassDirection.CENTER, StatsPanel.class);
			panel.add(label);
		}
		// --------------------------------------------------
		/**
		 * Centers the initialized contents.
		 * @param panel The target {@link TPanelElement}.
		 * @throws NullPointerException If the argument is {@code null}.
		 */
		@ApiStatus.Internal
		private static final void centerPanelContents(@NotNull TPanelElement panel) throws NullPointerException
		{
			//prepare for math
			final var bb  = panel.getBounds();
			final var cbb = panel.getContentBounds();
			final var pad = panel.scrollPaddingProperty().getI();
			//only proceed if there's available space
			if(cbb.width >= bb.width - (pad * 2)) return;
			//wrap the children into a single element
			final var el = new TElement();
			el.setBounds(bb.x + pad, bb.y + pad, bb.width - (pad * 2), cbb.height);
			el.addAll(panel);
			panel.add(el);
			//move to center
			el.move((bb.x - cbb.x) + (bb.width / 2) - (cbb.width / 2), 0);
			//resize to fit the panel, so scrolling doesn't undo this centering
			el.setBounds(bb.x + pad, bb.y + pad, bb.width - (pad * 2), cbb.height);
		}
		// ==================================================
		public final @Override void renderCallback(@NotNull TGuiGraphics pencil) {
			final var bb = getBounds();
			pencil.drawGuiSprite(BSSSprites.gui_panel_bgStats(), bb.x, bb.y, bb.width, bb.height, -1);
		}
		public final @Override void postRenderCallback(@NotNull TGuiGraphics pencil) {
			final var bb = getBounds();
			pencil.drawGuiSprite(BSSSprites.gui_panel_fgStats(), bb.x, bb.y, bb.width, bb.height, -1);
		}
		// ==================================================
	}
	// ================================================== ==================================================
	//                                       MenubarPanel IMPLEMENTATION
	// ================================================== ==================================================
	/**
	 * The top menubar panel in a {@link BetterStatsPanel}, featuring buttons
	 * for various actions.
	 */
	private final class MenubarPanel extends TElement
	{
		// ==================================================
		public MenubarPanel() { super(); }
		// ==================================================
		protected final @Override void initCallback()
		{
			//create and add the panel where the buttons will be placed
			final var panel = new TPanelElement.Paintable();
			panel.backgroundColorProperty().set(0, MenubarPanel.class);
			panel.outlineColorProperty().set(0, MenubarPanel.class);
			panel.focusedOutlineColorProperty().set(0x33FFFFFF, MenubarPanel.class);
			panel.setBounds(getBounds().add(1, 1, -2, -2));
			add(panel);

			//initialize the menubar buttons
			final var pbb = panel.getBounds();
			for(final var mapEntry : BClientRegistries.MENUBAR_BUTTON.entrySet())
			{
				//obtain button information
				final var entry       = mapEntry.getValue();
				final var btn_id      = mapEntry.getKey();
				final var btn_text    = entry.getKey();
				final var btn_onclick = entry.getValue();

				//ensure the required information is present
				Objects.requireNonNull(btn_text, "Menubar button does not have a valid text - " + btn_id);
				Objects.requireNonNull(btn_onclick, "Menubar button does not have a valid 'on click' action - " + btn_id);

				//create and add the button
				final var pcbb     = panel.getContentBounds();
				final var btn      = new Button();
				final var btn_font = btn.getLabel().fontProperty().get();
				btn.getLabel().setText(btn_text);
				btn.setBounds(pcbb.endX, pcbb.y, btn_font.width(btn_text) + 10, pbb.height);
				btn.contextMenuProperty().set(__ -> Objects.requireNonNull(
						btn_onclick.apply(BetterStatsPanel.this.statsGui),
						"Menubar button '" + btn_id + "' returned a 'null' context menu"),
					MenubarPanel.class);
				btn.eClicked.register(__ -> btn.showContextMenu());
				panel.add(btn);
			}
		}
		// ==================================================
		/**
		 * {@link TButtonWidget} implementation that doesn't apply a padding to its label.
		 */
		private static final class Button extends TButtonWidget.Transparent {
			protected final @Override void initCallback() {
				final var bb = getBounds();
				getLabel().setBounds(bb);
				add(getLabel());
			}
		}
		// ==================================================
		public final @Override void renderCallback(@NotNull TGuiGraphics pencil) {
			final var bb = getBounds();
			pencil.drawGuiSprite(BSSSprites.gui_panel_bgMenubar(), bb.x, bb.y, bb.width, bb.height, -1);
		}
		public final @Override void postRenderCallback(@NotNull TGuiGraphics pencil) {
			final var bb = getBounds();
			pencil.drawGuiSprite(BSSSprites.gui_panel_fgMenubar(), bb.x, bb.y, bb.width, bb.height, -1);
		}
		// ==================================================
	}
	// ================================================== ==================================================
	//                                    NavigationPanel IMPLEMENTATION
	// ================================================== ==================================================
	/**
	 * Additional buttons in the bottom left corner of the {@link BetterStatsPanel}.
	 */
	private final class NavigationPanel extends TElement
	{
		// ==================================================
		public NavigationPanel() { super(); }
		// ==================================================
		@SuppressWarnings("DataFlowIssue")
		protected final @Override void initCallback()
		{
			//obtain the bounding box
			final var bb = getBounds();

			//"Close" button
			final var btn_close = new TButtonWidget();
			btn_close.setBounds(bb.endX - 20, bb.y, 20, 20);
			btn_close.eClicked.register(__ -> __.getClient().screen.onClose());
			btn_close.tooltipProperty().set(__ -> TTooltip.of(BSSLang.gui_menubar_file_close()), NavigationPanel.class);
			add(btn_close);
			final var ico_close = new TTextureElement(BSSSprites.gui_icon_close());
			ico_close.setBounds(btn_close.getBounds().add(3, 3, -6, -6));
			add(ico_close); //don't add to button, init callback clears it

			//"Settings" button
			final var btn_settings = new TButtonWidget();
			btn_settings.setBounds(bb.endX - 40, bb.y, 20, 20);
			btn_settings.eClicked.register(__ -> {
				BetterStatsPanel.this.statsGui.setStatsTab(StatsTabSettings.INSTANCE);
				BetterStatsPanel.this.clearAndInit();
			});
			btn_settings.tooltipProperty().set(__ -> TTooltip.of(BSSLang.gui_menubar_file_settings()), NavigationPanel.class);
			add(btn_settings);
			final var ico_settings = new TTextureElement(BSSSprites.gui_icon_settings());
			ico_settings.setBounds(btn_settings.getBounds().add(3, 3, -6, -6));
			add(ico_settings); //don't add to button, init callback clears it

		}
		// ==================================================
		public final @Override void renderCallback(@NotNull TGuiGraphics pencil) {
			final var bb = getBounds();
			pencil.drawGuiSprite(BSSSprites.gui_panel_bgNav(), bb.x, bb.y, bb.width, bb.height, -1);
		}
		public final @Override void postRenderCallback(@NotNull TGuiGraphics pencil) {
			final var bb = getBounds();
			pencil.drawGuiSprite(BSSSprites.gui_panel_fgNav(), bb.x, bb.y, bb.width, bb.height, -1);
		}
		// ==================================================
	}
	// ================================================== ==================================================
}
