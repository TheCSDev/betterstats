package com.thecsdev.betterstats.api.client.gui;

import com.thecsdev.betterstats.api.client.gui.screen.BetterStatsConfigScreen;
import com.thecsdev.betterstats.api.client.gui.screen.BetterStatsScreen;
import com.thecsdev.betterstats.api.client.gui.statsview.StatsView;
import com.thecsdev.betterstats.api.mcbs.controller.McbsEditor;
import com.thecsdev.betterstats.api.mcbs.controller.McbsEditorTab;
import com.thecsdev.betterstats.resources.BSSLang;
import com.thecsdev.betterstats.resources.BSSSprites;
import com.thecsdev.betterstats.resources.BSSTex;
import com.thecsdev.common.math.UDim2;
import com.thecsdev.common.util.enumerations.CompassDirection;
import com.thecsdev.commonmc.api.client.gui.TElement;
import com.thecsdev.commonmc.api.client.gui.label.TLabelElement;
import com.thecsdev.commonmc.api.client.gui.misc.TTextureElement;
import com.thecsdev.commonmc.api.client.gui.panel.TPanelElement;
import com.thecsdev.commonmc.api.client.gui.render.TGuiGraphics;
import com.thecsdev.commonmc.api.client.gui.screen.TScreen;
import com.thecsdev.commonmc.api.client.gui.tooltip.TTooltip;
import com.thecsdev.commonmc.api.client.gui.widget.TButtonWidget;
import com.thecsdev.commonmc.api.client.gui.widget.TScrollBarWidget;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * {@link TElement} implementation that holds all of {@link BetterStatsScreen}'s
 * graphical user interface.
 */
public final class McbsEditorGUI extends TElement
{
	// ================================================== ==================================================
	//                                     StatsEditorGUI IMPLEMENTATION
	// ================================================== ==================================================
	private final @NotNull McbsEditor mcbsEditor;
	// ==================================================
	public McbsEditorGUI(@NotNull McbsEditor mcbsEditor) throws NullPointerException {
		this.mcbsEditor = Objects.requireNonNull(mcbsEditor);
	}
	// ==================================================
	/**
	 * Returns the {@link McbsEditor} instance associated with this GUI.
	 */
	public final @NotNull McbsEditor getMcbsEditor() { return this.mcbsEditor; }

	/**
	 * Returns the current {@link McbsEditorTab} being edited in this GUI.
	 */
	public final @Nullable McbsEditorTab getCurrentMcbsFile() { return this.mcbsEditor.getCurrentTab(); }
	// ==================================================
	protected final @Override void initCallback()
	{
		//create and add the menubar
		final var menubar = new MenubarPanel();
		menubar.setBounds(0, 0, getBounds().width, 17);
		addRel(menubar);

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
	//                                     StatsEditorGUI IMPLEMENTATION
	// ================================================== ==================================================
	/**
	 * {@link McbsEditorGUI}'s menubar interface that appears at the top of the interface,
	 * featuring controls and options for the user to interact with.
	 */
	@ApiStatus.Internal
	private static final class MenubarPanel extends TElement
	{
		// ==================================================
		public final @Override void renderCallback(@NotNull TGuiGraphics pencil) {
			final var bb = getBounds();
			pencil.drawGuiSprite(BSSSprites.gui_panel_bgMenubar(), bb.x, bb.y, bb.width, bb.height, -1);
		}
		public final @Override void postRenderCallback(@NotNull TGuiGraphics pencil) {
			final var bb = getBounds();
			pencil.drawGuiSprite(BSSSprites.gui_panel_fgMenubar(), bb.x, bb.y, bb.width, bb.height, -1);
		}
		// --------------------------------------------------
		protected final @Override void initCallback()
		{
			//create and add the panel where the buttons will be placed
			final var panel = new TPanelElement.Paintable(0, 0, 0x33FFFFFF);
			panel.setBounds(getBounds().add(1, 1, -2, -2));
			add(panel);

			//initialize the menubar buttons
			//FIXME - Implement this GUI
		}
		// ==================================================
		private static final class Button extends TButtonWidget.Transparent {
			protected final @Override void initCallback() {
				getLabel().setBounds(getBounds());
				add(getLabel());
			}
			public final @Override void renderCallback(@NotNull TGuiGraphics pencil) {
				if(!isHoveredOrFocused()) return;
				final var bb = getBounds();
				pencil.fillColor(bb.x, bb.y, bb.width, bb.height, 0x33ffffff);
			}
		}
		// ==================================================
	}
	// ================================================== ==================================================
	//                                       FiltersPanel IMPLEMENTATION
	// ================================================== ==================================================
	/**
	 * The "Filters" panel GUI, featuring user interface for filtering statistics.
	 */
	@ApiStatus.Internal
	private final class FiltersPanel extends TElement
	{
		// ==================================================
		public final @Override void renderCallback(@NotNull TGuiGraphics pencil) {
			final var bb = getBounds();
			pencil.drawGuiSprite(BSSSprites.gui_panel_bgFilters(), bb.x, bb.y, bb.width, bb.height, -1);
		}
		public final @Override void postRenderCallback(@NotNull TGuiGraphics pencil) {
			final var bb = getBounds();
			pencil.drawGuiSprite(BSSSprites.gui_panel_fgFilters(), bb.x, bb.y, bb.width, bb.height, -1);
		}
		// --------------------------------------------------
		protected final @Override void initCallback()
		{
			//obtain the necessary variables for gui
			final @Nullable var tab = McbsEditorGUI.this.mcbsEditor.getCurrentTab();
			if(tab == null) { initNoMcbsEditorTabGui(); return; }
			final @Nullable var statsView = tab.getCurrentView();
			if(statsView == null) { initNoStstsViewGui(); return; }
			//initialize the gui
			initGui(tab, statsView);
		}
		// ==================================================
		/**
		 * GUI that is initialized when no {@link McbsEditorTab} is currently selected.
		 */
		private final void initNoMcbsEditorTabGui() {
			//FIXME - Implement this GUI
		}

		/**
		 * GUI that is initialized when no {@link StatsView} is currently selected.
		 */
		private final void initNoStstsViewGui() {
			//FIXME - Implement this GUI
		}

		/**
		 * Main GUI initialization.
		 * @param tab The currently selected {@link McbsEditorTab}.
		 * @param view The currently selected {@link StatsView}.
		 */
		private final void initGui(@Nullable McbsEditorTab tab, @Nullable StatsView view)
		{
			//not null argument requirements
			Objects.requireNonNull(tab);
			Objects.requireNonNull(view);

			//initialize the panel
			final var panel = new TPanelElement.Paintable(0, 0, 0x33FFFFFF);
			add(panel);
			panel.setBounds(new UDim2(0, 1, 0, 1), new UDim2(1, -8 - 1, 1, -2));
			panel.scrollPaddingProperty().set(8, McbsEditorGUI.class);

			//initialize the scroll-bar
			final var scroll = new TScrollBarWidget.Flat(panel);
			add(scroll);
			scroll.setBounds(new UDim2(1, -8, 0, 0), new UDim2(0, 8, 1, 0));

			//initialize the filters
			view.initFilters(new StatsView.FiltersInitContext() {
				public final @Override @NotNull TPanelElement getPanel() { return panel; }
				public final @Override @NotNull StatsView.Filters getFilters() { return tab.getFilters(); }
			});
		}
		// ==================================================
	}
	// ================================================== ==================================================
	//                                         StatsPanel IMPLEMENTATION
	// ================================================== ==================================================
	/**
	 * The "Statistics" panel GUI that shows statistics relevant to given filters.
	 */
	@ApiStatus.Internal
	private final class StatsPanel extends TElement
	{
		// ==================================================
		public final @Override void renderCallback(@NotNull TGuiGraphics pencil) {
			final var bb = getBounds();
			pencil.drawGuiSprite(BSSSprites.gui_panel_bgStats(), bb.x, bb.y, bb.width, bb.height, -1);
		}
		public final @Override void postRenderCallback(@NotNull TGuiGraphics pencil) {
			final var bb = getBounds();
			pencil.drawGuiSprite(BSSSprites.gui_panel_fgStats(), bb.x, bb.y, bb.width, bb.height, -1);
		}
		// --------------------------------------------------
		protected final @Override void initCallback()
		{
			//obtain the necessary variables for gui
			final @Nullable var tab = McbsEditorGUI.this.mcbsEditor.getCurrentTab();
			if(tab == null) { initNoMcbsEditorTabGui(); return; }
			final @Nullable var statsView = tab.getCurrentView();
			if(statsView == null) { initNoStstsViewGui(); return; }
			//initialize the gui
			initGui(tab, statsView);
		}
		// ==================================================
		/**
		 * GUI that is initialized when no {@link McbsEditorTab} is currently selected.
		 */
		private final void initNoMcbsEditorTabGui() { initNoStats(); }

		/**
		 * GUI that is initialized when no {@link StatsView} is currently selected.
		 */
		private final void initNoStstsViewGui() { initNoStats(); }

		/**
		 * GUI that is initialized when no statistics can be shown, most likely
		 * because no GUI elements got initialized after {@link #initCallback()}
		 * was called.
		 */
		private final void initNoStats()
		{
			//bounding boxes math nonsense
			final var bb = getBounds();
			final int w3 = bb.width / 3;

			//create and add a texture element, for the silhouette
			final var tex_silhouette = new TTextureElement(BSSTex.gui_images_nostatsSilhouette());
			add(tex_silhouette);
			tex_silhouette.setBounds(new UDim2(0.5, 0, 0.5, 0), new UDim2(0, w3, 0, w3));
			tex_silhouette.move(-w3 / 2, -w3 / 2);
			tex_silhouette.colorProperty().set(0xFFFFFFFF, StatsPanel.class);

			//create and add a label, indicating no stats can be shown
			final var lbl = new TLabelElement(BSSLang.gui_statstab_stats_noStats());
			lbl.setBounds(bb.x, bb.y + (bb.height / 2) - 7, bb.width, 14);
			lbl.textAlignmentProperty().set(CompassDirection.CENTER, StatsPanel.class);
			lbl.textColorProperty().set(0xFFFFFFFF, StatsPanel.class);
			add(lbl);
		}

		/**
		 * Main GUI initialization.
		 * @param tab The currently selected {@link McbsEditorTab}.
		 * @param view The currently selected {@link StatsView}.
		 */
		private final void initGui(@NotNull McbsEditorTab tab, @NotNull StatsView view)
		{
			//not null argument requirements
			Objects.requireNonNull(tab);
			Objects.requireNonNull(view);

			//initialize the panel
			final var panel = new TPanelElement.Paintable(0, 0, 0x33FFFFFF);
			add(panel);
			panel.setBounds(new UDim2(0, 1, 0, 1), new UDim2(1, -8 - 1, 1, -2));
			panel.scrollPaddingProperty().set(8, McbsEditorGUI.class);

			//initialize the scroll-bar
			final var scroll = new TScrollBarWidget.Flat(panel);
			add(scroll);
			scroll.setBounds(new UDim2(1, -8, 0, 0), new UDim2(0, 8, 1, 0));

			//initialize the stats
			view.initStats(new StatsView.StatsInitContext() {
				public final @Override @NotNull TPanelElement getPanel() { return panel; }
				public final @Override @NotNull StatsView.Filters getFilters() { return tab.getFilters(); }
			});
		}
		// ==================================================
	}
	// ================================================== ==================================================
	//                                    NavigationPanel IMPLEMENTATION
	// ================================================== ==================================================
	/**
	 * Features additional navigation-related buttons.
	 */
	@ApiStatus.Internal
	private static final class NavigationPanel extends TElement
	{
		// ==================================================
		public final @Override void renderCallback(@NotNull TGuiGraphics pencil) {
			final var bb = getBounds();
			pencil.drawGuiSprite(BSSSprites.gui_panel_bgNav(), bb.x, bb.y, bb.width, bb.height, -1);
		}
		public final @Override void postRenderCallback(@NotNull TGuiGraphics pencil) {
			final var bb = getBounds();
			pencil.drawGuiSprite(BSSSprites.gui_panel_fgNav(), bb.x, bb.y, bb.width, bb.height, -1);
		}
		// --------------------------------------------------
		protected final @Override void initCallback()
		{
			//obtain the bounding box
			final var bb = getBounds();

			//"Settings" button
			final var btn_settings = new TButtonWidget();
			btn_settings.setBounds(bb.endX - 40, bb.y, 20, 20);
			btn_settings.eClicked.register(__ -> {
				final var client = Objects.requireNonNull(__.getClient(), "Missing 'client' instance");
				client.setScreen(new BetterStatsConfigScreen(client.screen).getAsScreen());
			});
			btn_settings.tooltipProperty().set(__ -> TTooltip.of(BSSLang.gui_menubar_file_settings()), NavigationPanel.class);
			add(btn_settings);
			final var ico_settings = new TTextureElement(BSSSprites.gui_icon_settings());
			ico_settings.setBounds(btn_settings.getBounds().add(3, 3, -6, -6));
			add(ico_settings); //don't add to button, init callback clears it

			//"Close" button
			final var btn_close = new TButtonWidget();
			btn_close.setBounds(bb.endX - 20, bb.y, 20, 20);
			btn_close.eClicked.register(__ -> __.screenProperty().getOptional()
					.map(TScreen::getAsScreen).ifPresent(Screen::onClose));
			btn_close.tooltipProperty().set(__ -> TTooltip.of(
					BSSLang.gui_menubar_file_close()), NavigationPanel.class);
			add(btn_close);
			final var ico_close = new TTextureElement(BSSSprites.gui_icon_close());
			ico_close.setBounds(btn_close.getBounds().add(3, 3, -6, -6));
			add(ico_close); //don't add to button, init callback clears it
		}
		// ==================================================
	}
	// ================================================== ==================================================
}
