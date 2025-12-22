package com.thecsdev.betterstats.client.gui.mcbs_view.editor;

import com.thecsdev.betterstats.api.mcbs.controller.McbsEditor;
import com.thecsdev.betterstats.api.mcbs.controller.McbsEditorTab;
import com.thecsdev.betterstats.api.mcbs.model.McbsStats;
import com.thecsdev.betterstats.api.mcbs.view.McbsEditorGUI;
import com.thecsdev.betterstats.api.mcbs.view.statsview.StatsView;
import com.thecsdev.betterstats.api.mcbs.view.statsview.StatsViewUtils;
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
import com.thecsdev.commonmc.api.client.gui.widget.TScrollBarWidget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * <b>Tabbed document interface</b> tab content element that houses the main content
 * of the currently selected {@link McbsEditorTab}.
 */
@ApiStatus.Internal
@Environment(EnvType.CLIENT)
public final class TabContentPanel extends TElement
{
	// ================================================== ==================================================
	//                                    TabContentPanel IMPLEMENTATION
	// ================================================== ==================================================
	private final @NotNull  McbsEditor    mcbsEditor;
	private final @Nullable McbsEditorTab tab;
	// --------------------------------------------------
	private long lastSeenTabEditCount; //for keeping up to date with tab controller's changes
	// ==================================================
	public TabContentPanel(@NotNull McbsEditor mcbsEditor) {
		this.mcbsEditor = Objects.requireNonNull(mcbsEditor);
		this.tab        = mcbsEditor.getCurrentTab();
	}
	// ==================================================
	protected final @Override void tickCallback() {
		//if last seen tab edit count is out of date, we need to reinitialize
		if(this.tab != null && this.lastSeenTabEditCount != tab.getEditCount())
			clearAndInit();
	}

	protected final @Override void initCallback()
	{
		//when reinitializing, we're up-to-date, so clear any "dirtiness" flags
		if(this.tab != null) this.lastSeenTabEditCount = this.tab.getEditCount();

		//initialize and add the stats filters panel
		final var panel_filters = new FiltersPanel();
		add(panel_filters);
		panel_filters.setBounds(UDim2.ZERO, new UDim2(0.3, 1, 1, 0));

		//initialize and add the stats tab itself
		final var panel_stats = new StatsPanel();
		add(panel_stats);
		panel_stats.setBounds(new UDim2(0.3, 0, 0, 0), new UDim2(0.7, -1, 1, 0));
	}
	// ================================================== ==================================================
	//                                       FiltersPanel IMPLEMENTATION
	// ================================================== ==================================================
	/**
	 * The "Filters" panel GUI, featuring user interface for filtering statistics.
	 */
	private final @ApiStatus.Internal class FiltersPanel extends TElement
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
		protected final @Override void initCallback() {
			if(TabContentPanel.this.tab == null) initNoMcbsEditorTabGui();
			else initGui(TabContentPanel.this.tab, TabContentPanel.this.tab.getCurrentView());
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
			Objects.requireNonNull(tab, "Missing editor 'tab' instance");
			Objects.requireNonNull(view, "Missing editor 'stats view' instance");

			//initialize the panel
			final var panel = new TPanelElement.Paintable(0, 0, 0x33FFFFFF);
			panel.setBounds(1, 1, getBounds().width - 2 - 8, getBounds().height - 2);
			panel.scrollPaddingProperty().set(8, FiltersPanel.class);
			addRel(panel);

			//initialize the scroll-bar
			final var scroll = new TScrollBarWidget.Flat(panel);
			scroll.setBounds(getBounds().width - 9, 1, 8, getBounds().height - 2);
			addRel(scroll);

			//initialize the filters
			final var ctx = new StatsView.FiltersInitContext() {
				public final @Override @NotNull TPanelElement getPanel() { return panel; }
				public final @Override @NotNull StatsView.Filters getFilters() { return tab.getStatFilters(); }
			};
			view.initFilters(ctx);

			//if no filters got initialized, initialize default filters
			if(panel.isEmpty()) StatsViewUtils.initDefaultFilters(ctx);
		}
		// ==================================================
	}
	// ================================================== ==================================================
	//                                         StatsPanel IMPLEMENTATION
	// ================================================== ==================================================
	/**
	 * The "Statistics" panel GUI that shows statistics relevant to given filters.
	 */
	private final @ApiStatus.Internal class StatsPanel extends TElement
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
		protected final @Override void initCallback() {
			if(TabContentPanel.this.tab == null) initNoMcbsEditorTabGui();
			else initGui(TabContentPanel.this.tab, TabContentPanel.this.tab.getCurrentView());
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
			final var lbl = new TLabelElement(BSSLang.gui_statsview_stats_noStats());
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
			Objects.requireNonNull(tab, "Missing editor 'tab' instance");
			Objects.requireNonNull(view, "Missing editor 'stats view' instance");

			//initialize the panel
			final var panel = new TPanelElement.Paintable(0, 0, 0x33FFFFFF);
			panel.setBounds(1, 1, getBounds().width - 2 - 8, getBounds().height - 2);
			panel.scrollPaddingProperty().set(8, StatsPanel.class);
			addRel(panel);

			//initialize the scroll-bar
			final var scroll = new TScrollBarWidget.Flat(panel);
			scroll.setBounds(getBounds().width - 9, 1, 8, getBounds().height - 2);
			addRel(scroll);

			//initialize the stats
			view.initStats(new StatsView.StatsInitContext() {
				public final @Override @NotNull TPanelElement getPanel() { return panel; }
				public final @Override @NotNull StatsView.Filters getFilters() { return tab.getStatFilters(); }
				public final @Override @NotNull McbsStats getStatsReadOnly() { return tab.getStatsReadOnly(); }
			});

			//if no statistics got initialized, init "no stats" gui
			if(panel.isEmpty()) initNoStats();
		}
		// ==================================================
	}
	// ================================================== ==================================================
}
