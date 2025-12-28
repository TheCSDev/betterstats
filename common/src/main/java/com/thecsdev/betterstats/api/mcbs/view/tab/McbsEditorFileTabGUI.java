package com.thecsdev.betterstats.api.mcbs.view.tab;

import com.thecsdev.betterstats.api.mcbs.controller.tab.McbsEditorFileTab;
import com.thecsdev.betterstats.api.mcbs.view.statsview.StatsView;
import com.thecsdev.betterstats.api.mcbs.view.statsview.StatsViewUtils;
import com.thecsdev.betterstats.resources.BSSSprites;
import com.thecsdev.common.math.UDim2;
import com.thecsdev.commonmc.api.client.gui.TElement;
import com.thecsdev.commonmc.api.client.gui.panel.TPanelElement;
import com.thecsdev.commonmc.api.client.gui.render.TGuiGraphics;
import com.thecsdev.commonmc.api.client.gui.widget.TScrollBarWidget;
import com.thecsdev.commonmc.api.stats.IStatsProvider;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * <b>Tabbed document interface</b> tab content element that houses the main content
 * of the currently selected {@link McbsEditorFileTab}.
 */
@Environment(EnvType.CLIENT)
public final class McbsEditorFileTabGUI extends McbsEditorTabGUI<McbsEditorFileTab>
{
	// ================================================== ==================================================
	//                               McbsEditorTabFileGUI IMPLEMENTATION
	// ================================================== ==================================================
	public McbsEditorFileTabGUI(@NotNull McbsEditorFileTab editorTab) throws NullPointerException {
		super(editorTab);
	}
	// ==================================================
	protected final @Override void initTabGuiCallback()
	{
		//initialize and add the stats filters panel
		final var panel_filters = new FiltersPanel();
		add(panel_filters);
		panel_filters.setBounds(UDim2.ZERO, new UDim2(0.3, 0, 1, 0));

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
	private final @ApiStatus.Internal class FiltersPanel extends TElement
	{
		// ==================================================
		public final @Override void renderCallback(@NotNull TGuiGraphics pencil) {
			final var bb = getBounds();
			pencil.drawGuiSprite(BSSSprites.gui_editor_tab_statsFile_filtersBackground(), bb.x, bb.y, bb.width, bb.height, -1);
		}
		public final @Override void postRenderCallback(@NotNull TGuiGraphics pencil) {
			final var bb = getBounds();
			pencil.drawGuiSprite(BSSSprites.gui_editor_tab_statsFile_filtersForeground(), bb.x, bb.y, bb.width, bb.height, -1);
		}
		// --------------------------------------------------
		protected final @Override void initCallback() {
			final var tab = McbsEditorFileTabGUI.this.getEditorTab();
			initGui(tab, tab.getCurrentView());
		}
		// ==================================================
		/**
		 * Main GUI initialization.
		 * @param tab The currently selected {@link McbsEditorFileTab}.
		 * @param view The currently selected {@link StatsView}.
		 */
		private final void initGui(@NotNull McbsEditorFileTab tab, @NotNull StatsView view)
		{
			//not null argument requirements
			Objects.requireNonNull(tab, "Missing editor 'tab' instance");
			Objects.requireNonNull(view, "Missing editor 'stats view' instance");

			//initialize the panel
			final var panel = new TPanelElement.Paintable(0, 0, 0x33FFFFFF);
			panel.setBounds(0, 0, getBounds().width - 8, getBounds().height);
			panel.scrollPaddingProperty().set(8, FiltersPanel.class);
			addRel(panel);

			//initialize the scroll-bar
			final var scroll = new TScrollBarWidget.Flat(panel);
			scroll.setBounds(getBounds().width - 8, 0, 8, getBounds().height);
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
			pencil.drawGuiSprite(BSSSprites.gui_editor_tab_statsFile_statsBackground(), bb.x, bb.y, bb.width, bb.height, -1);
		}
		public final @Override void postRenderCallback(@NotNull TGuiGraphics pencil) {
			final var bb = getBounds();
			pencil.drawGuiSprite(BSSSprites.gui_editor_tab_statsFile_statsForeground(), bb.x, bb.y, bb.width, bb.height, -1);
		}
		// --------------------------------------------------
		protected final @Override void initCallback() {
			final var tab = McbsEditorFileTabGUI.this.getEditorTab();
			initGui(tab, tab.getCurrentView());
		}
		// ==================================================
		/**
		 * Main GUI initialization.
		 * @param tab The currently selected {@link McbsEditorFileTab}.
		 * @param view The currently selected {@link StatsView}.
		 */
		private final void initGui(@NotNull McbsEditorFileTab tab, @NotNull StatsView view)
		{
			//not null argument requirements
			Objects.requireNonNull(tab, "Missing editor 'tab' instance");
			Objects.requireNonNull(view, "Missing editor 'stats view' instance");

			//initialize the panel
			final var panel = new TPanelElement.Paintable(0, 0, 0x33FFFFFF);
			panel.setBounds(0, 0, getBounds().width - 8, getBounds().height);
			panel.scrollPaddingProperty().set(8, StatsPanel.class);
			addRel(panel);

			//initialize the scroll-bar
			final var scroll = new TScrollBarWidget.Flat(panel);
			scroll.setBounds(getBounds().width - 8, 0, 8, getBounds().height);
			addRel(scroll);

			//initialize the stats
			view.initStats(new StatsView.StatsInitContext() {
				public final @Override @NotNull TPanelElement getPanel() { return panel; }
				public final @Override @NotNull StatsView.Filters getFilters() { return tab.getStatFilters(); }
				public final @Override @NotNull IStatsProvider getStats() { return tab.getStats(); }
			});

			//if no statistics got initialized, init "no stats" gui
			if(panel.isEmpty()) McbsEditorNullTabGUI.initNoStatsGUI(this);
		}
		// ==================================================
	}
	// ================================================== ==================================================
}
