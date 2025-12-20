package com.thecsdev.betterstats.api.mcbs.view;

import com.thecsdev.betterstats.api.client.gui.screen.BetterStatsScreen;
import com.thecsdev.betterstats.api.client.registry.BClientRegistries;
import com.thecsdev.betterstats.api.mcbs.controller.McbsEditor;
import com.thecsdev.betterstats.api.mcbs.controller.McbsEditorTab;
import com.thecsdev.betterstats.api.mcbs.model.McbsStats;
import com.thecsdev.betterstats.api.mcbs.view.statsview.StatsView;
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
import com.thecsdev.commonmc.api.client.gui.widget.TButtonWidget;
import com.thecsdev.commonmc.api.client.gui.widget.TScrollBarWidget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.CrashReport;
import net.minecraft.ReportedException;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static java.util.Objects.requireNonNull;

/**
 * {@link TElement} implementation that holds all of {@link BetterStatsScreen}'s
 * graphical user interface.
 */
@Environment(EnvType.CLIENT)
public final class McbsEditorGUI extends TElement
{
	// ================================================== ==================================================
	//                                     StatsEditorGUI IMPLEMENTATION
	// ================================================== ==================================================
	private final @NotNull McbsEditor mcbsEditor;
	// --------------------------------------------------
	private long lastSeenEditCount; //for keeping up to date with controller's changes
	// ==================================================
	public McbsEditorGUI(@NotNull McbsEditor mcbsEditor) throws NullPointerException {
		this.mcbsEditor = requireNonNull(mcbsEditor);
	}
	// ==================================================
	/**
	 * Returns the {@link McbsEditor} instance associated with this GUI.
	 */
	public final @NotNull McbsEditor getMcbsEditor() { return this.mcbsEditor; }
	// ==================================================
	protected final @Override void tickCallback() {
		//if last seen edit count is out of date, we need to reinitialize
		if(this.lastSeenEditCount != this.mcbsEditor.getEditCount()) {
			this.lastSeenEditCount = this.mcbsEditor.getEditCount();
			clearAndInit();
		}
	}

	protected final @Override void initCallback()
	{
		//when reinitializing, we're up-to-date, so clear any "dirtiness" flags
		this.lastSeenEditCount = this.mcbsEditor.getEditCount();

		//create and add the menubar
		final var menubar = new MenubarPanel();
		menubar.setBounds(0, 0, getBounds().width, 17);
		addRel(menubar);

		//create and add the editor-tab panel
		final var editorTab = new EditorTabPanel();
		editorTab.setBounds(0, 20, getBounds().width, getBounds().height - 20);
		addRel(editorTab);
	}
	// ================================================== ==================================================
	//                                     StatsEditorGUI IMPLEMENTATION
	// ================================================== ==================================================
	/**
	 * {@link McbsEditorGUI}'s menubar interface that appears at the top of the interface,
	 * featuring controls and options for the user to interact with.
	 */
	@ApiStatus.Internal
	private final class MenubarPanel extends TElement
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
			panel.scrollPaddingProperty().set(0, MenubarPanel.class);
			add(panel);

			//initialize the menubar buttons
			for(final var item : BClientRegistries.MENUBAR_ITEM.entrySet())
			{
				//attempt to initialize the gui for a menubar item
				try {
					//current panel content bounding box
					final var pcbb = panel.getContentBounds();
					//the label
					final var label = requireNonNull(
							item.getValue().getDisplayName(), "Missing display name");
					//the button
					final var button = new Button();
					button.getLabel().setText(label);
					button.setBounds(
							pcbb.endX, pcbb.y,
							button.getLabel().fontProperty().get().width(label) + 10,
							panel.getBounds().height);
					button.contextMenuProperty().set(
							__ -> requireNonNull(
									item.getValue().createContextMenu(
											requireNonNull(__.getClient(), "Missing 'client' instance"),
											McbsEditorGUI.this.mcbsEditor),
									"Menubar item failed to produce a context menu, ID " + item.getKey()),
							MenubarPanel.class);
					button.eClicked.register(TElement::showContextMenu);
					panel.add(button);
				}
				//hold menubar items accountable for failures
				catch(Exception e) {
					throw new ReportedException(new CrashReport(
							"Something went wrong creating menubar item ID " + item.getKey(),
							e));
				}
			}
		}
		// ==================================================
		/**
		 * {@link TButtonWidget} implementation specifically for menubar items.
		 */
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
	//                                     EditorTabPanel IMPLEMENTATION
	// ================================================== ==================================================
	/**
	 * {@link McbsEditorGUI}'s interface for a given {@link McbsEditorTab}.
	 */
	private final class EditorTabPanel extends TElement
	{
		// ==================================================
		private long lastSeenEditCount; //for keeping up to date with controller's changes
		// ==================================================
		protected final @Override void tickCallback()
		{
			//if last seen edit count is out of date, we need to reinitialize
			final @Nullable var tab = McbsEditorGUI.this.mcbsEditor.getCurrentTab();
			if(tab != null && this.lastSeenEditCount != tab.getEditCount()) {
				this.lastSeenEditCount = tab.getEditCount();
				clearAndInit();
			}
		}

		protected final @Override void initCallback()
		{
			//when reinitializing, we're up-to-date, so clear any "dirtiness" flags
			final @Nullable var tab = McbsEditorGUI.this.mcbsEditor.getCurrentTab();
			if(tab != null) this.lastSeenEditCount = tab.getEditCount();

			//initialize and add the stats filters panel
			final var panel_filters = new FiltersPanel();
			add(panel_filters);
			panel_filters.setBounds(UDim2.ZERO, new UDim2(0.3, 1, 1, 0));

			//initialize and add the stats tab itself
			final var panel_stats = new StatsPanel();
			add(panel_stats);
			panel_stats.setBounds(new UDim2(0.3, 0, 0, 0), new UDim2(0.7, 0, 1, 0));
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
				//initialize the gui
				initGui(tab, tab.getCurrentView());
			}
			// ==================================================
			/**
			 * GUI that is initialized when no {@link McbsEditorTab} is currently selected.
			 */
			private final void initNoMcbsEditorTabGui() {
				//FIXME - Implement this GUI
			}

			/**
			 * Main GUI initialization.
			 * @param tab The currently selected {@link McbsEditorTab}.
			 * @param view The currently selected {@link StatsView}.
			 */
			private final void initGui(@NotNull McbsEditorTab tab, @NotNull StatsView view)
			{
				//not null argument requirements
				requireNonNull(tab, "Missing editor 'tab' instance");
				requireNonNull(view, "Missing editor 'stats view' instance");

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
					public final @Override @NotNull StatsView.Filters getFilters() { return tab.getStatFilters(); }
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
				//initialize the gui
				initGui(tab, tab.getCurrentView());
			}
			// ==================================================
			/**
			 * GUI that is initialized when no {@link McbsEditorTab} is currently selected.
			 */
			private final void initNoMcbsEditorTabGui() { initNoStats(); }

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
				requireNonNull(tab, "Missing editor 'tab' instance");
				requireNonNull(view, "Missing editor 'stats view' instance");

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
					public final @Override @NotNull StatsView.Filters getFilters() { return tab.getStatFilters(); }
					public final @Override @NotNull McbsStats getStatsReadOnly() { return tab.getStatsReadOnly(); }
				});
			}
			// ==================================================
		}
		// ================================================== ==================================================
	}
	// ================================================== ==================================================
}
