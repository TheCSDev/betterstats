package com.thecsdev.betterstats.client.gui.panel;

import com.thecsdev.betterstats.BetterStats;
import com.thecsdev.betterstats.api.mcbs.controller.tab.McbsEditorFileTab;
import com.thecsdev.betterstats.api.mcbs.controller.tab.McbsEditorHomepageTab;
import com.thecsdev.betterstats.api.mcbs.view.statsview.StatsViewUtils;
import com.thecsdev.betterstats.api.mcbs.view.tab.McbsEditorHomepageTabGUI;
import com.thecsdev.betterstats.mcbs.view.statsview.StatsViewGeneral;
import com.thecsdev.betterstats.resource.BLanguage;
import com.thecsdev.betterstats.resource.BSprites;
import com.thecsdev.betterstats.resource.BTextures;
import com.thecsdev.common.util.enumerations.CompassDirection;
import com.thecsdev.commonmc.api.client.gui.TElement;
import com.thecsdev.commonmc.api.client.gui.ctxmenu.TContextMenu;
import com.thecsdev.commonmc.api.client.gui.label.TLabelElement;
import com.thecsdev.commonmc.api.client.gui.misc.TFillColorElement;
import com.thecsdev.commonmc.api.client.gui.misc.THoverScrollElement;
import com.thecsdev.commonmc.api.client.gui.misc.TTextureElement;
import com.thecsdev.commonmc.api.client.gui.panel.TPanelElement;
import com.thecsdev.commonmc.api.client.gui.widget.TButtonWidget;
import com.thecsdev.commonmc.api.client.gui.widget.stats.TTextualStatWidget;
import com.thecsdev.commonmc.api.stats.util.BlockStats;
import com.thecsdev.commonmc.api.stats.util.CustomStat;
import com.thecsdev.commonmc.api.stats.util.EntityStats;
import com.thecsdev.commonmc.api.stats.util.ItemStats;
import com.thecsdev.commonmc.api.util.modinfo.ModInfoProvider;
import com.thecsdev.commonmc.resource.TLanguage;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.StatFormatter;
import net.minecraft.stats.Stats;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static com.thecsdev.betterstats.BetterStats.MOD_ID;
import static com.thecsdev.betterstats.api.mcbs.view.statsview.StatsViewUtils.GAP;
import static com.thecsdev.betterstats.client.BetterStatsClient.getLastLoginTime;
import static com.thecsdev.betterstats.mcbs.view.menubar.MenubarItemAbout.showUriScreen;
import static com.thecsdev.commonmc.resource.TComponent.gui;
import static java.lang.System.currentTimeMillis;
import static net.minecraft.network.chat.Component.literal;
import static net.minecraft.network.chat.Component.translatable;

/**
 * {@link TPanelElement} whose interface serves as the local-player's personal
 * space, featuring statistics and utilities related to them.
 * <p>
 * Primarily found in {@link McbsEditorHomepageTabGUI}s.
 */
@ApiStatus.Internal
@Environment(EnvType.CLIENT)
public final class PersonalHomePanel extends TPanelElement.Paintable
{
	// ================================================== ==================================================
	//                                  PersonalHomePanel IMPLEMENTATION
	// ================================================== ==================================================
	private final @NotNull McbsEditorHomepageTab tab;
	// ==================================================
	public PersonalHomePanel(@NotNull McbsEditorHomepageTab tab) {
		this.tab = Objects.requireNonNull(tab);
		outlineColorProperty().set(0xFF000000, PersonalHomePanel.class);
		scrollPaddingProperty().set(10, PersonalHomePanel.class);
	}
	// ==================================================
	protected final @Override void initCallback() {
		initModSummary();
		initQuickAccess();
		initFeaturedStats();
		initComingSoon();
		StatsViewUtils.initGroupLabel(this, Component.empty()); //a bit of offset
	}
	// --------------------------------------------------
	/**
	 * Initializes the "mod summary" section of the panel, which provides a brief overview
	 * of the mod, including its name, version, and links to relevant resources.
	 */
	private final void initModSummary() {
		final var modSummary = new ModSummaryPanel();
		modSummary.setBounds(computeNextYBounds(40, 0));
		add(modSummary);
	}
	// --------------------------------------------------
	/**
	 * Initializes the "quick access" section of the panel, which provides shortcuts to
	 * commonly used features and utilities.
	 */
	private final void initQuickAccess()
	{
		//group label
		StatsViewUtils.initGroupLabel(this, TLanguage.gui_fileChooser_quickAccess());

		//main panel
		final var quickAccess = new QuickAccessPanel();
		quickAccess.setBounds(computeNextYBounds(55, GAP));
		add(quickAccess);
		final var ebb = quickAccess.getBounds();

		//hover-scroll elements for the entries panel
		final var scroll_left = new THoverScrollElement.Panel(quickAccess);
		scroll_left.directionProperty().set(CompassDirection.WEST, PersonalHomePanel.class);
		scroll_left.setBounds(ebb.x, ebb.y, 15, ebb.height);
		add(scroll_left);
		final var scroll_right = new THoverScrollElement.Panel(quickAccess);
		scroll_right.directionProperty().set(CompassDirection.EAST, PersonalHomePanel.class);
		scroll_right.setBounds(ebb.endX - 15, ebb.y, 15, ebb.height);
		add(scroll_right);

		final var lbl_left = new TLabelElement(literal("<"));
		lbl_left.textAlignmentProperty().set(CompassDirection.CENTER, PersonalHomePanel.class);
		lbl_left.setBounds(scroll_left.getBounds());
		scroll_left.add(lbl_left);
		final var lbl_right = new TLabelElement(literal(">"));
		lbl_right.textAlignmentProperty().set(CompassDirection.CENTER, PersonalHomePanel.class);
		lbl_right.setBounds(scroll_right.getBounds());
		scroll_right.add(lbl_right);
	}
	// --------------------------------------------------
	/**
	 * Initializes the "featured statistics" section of the panel, which displays a selection
	 * of important statistics related to the local player, such as playtime and deaths.
	 */
	private final void initFeaturedStats()
	{
		//preparation
		final var bb        = getBounds();
		final var pad       = scrollPaddingProperty().getI();
		final var lpTab     = McbsEditorFileTab.LOCALPLAYER;
		final var lpFilters = lpTab.getStatFilters();
		final var lpStats   = lpTab.getStats();
		final var distUnit  = lpFilters.getProperty(StatsViewGeneral.DistanceUnit.class, StatsViewGeneral.DistanceUnit.FID, StatsViewGeneral.DistanceUnit.VANILLA);
		final var timeUnit  = lpFilters.getProperty(StatsViewGeneral.TimeUnit.class, StatsViewGeneral.TimeUnit.FID, StatsViewGeneral.TimeUnit.VANILLA);

		//featured general statistics
		StatsViewUtils.initGeneralStats(this, BLanguage.gui_homeTab_featuredStats(), List.of(
				new CustomStat(Stats.PLAY_TIME, lpStats),
				new CustomStat(Stats.TIME_SINCE_DEATH, lpStats),
				new CustomStat(Stats.DEATHS, lpStats)
		), widget -> {
			final var stat = widget.statProperty().get();
			assert stat != null;
			if(stat.isDistance())
				widget.formatterOverrideProperty().set(distUnit.getFormatter(), StatsViewGeneral.class);
			else if(stat.isTime())
				widget.formatterOverrideProperty().set(timeUnit.getFormatter(), StatsViewGeneral.class);
		});

		final var sessionTime = new TTextualStatWidget(
				BLanguage.stat_betterstats_timeSinceLogin(),
				literal(timeUnit.getFormatter().format(StatFormatter.TIME, (int) (currentTimeMillis() - getLastLoginTime()) / 50)));
		sessionTime.setBounds(computeNextYBounds(18, GAP));
		add(sessionTime);

		//panel for centering item, block, and mob stats
		final var ibmPanel = new TPanelElement.Transparent();
		ibmPanel.scrollPaddingProperty().set(0, PersonalHomePanel.class);
		ibmPanel.setBounds(computeNextYBounds(255, GAP));

		//featured item statistics
		final var items = ItemStats.getItemStats(lpStats, null, null).stream()
				.sorted(Comparator.comparingDouble(
						(ItemStats obj) -> obj.getValues().values().stream()
						.mapToInt(Integer::intValue)
						.average()
						.orElse(0.0)).reversed())
				.limit(7)
				.toList();
		StatsViewUtils.initItemStats(ibmPanel, null, items);

		//featured block statistics
		final var blocks = BlockStats.getBlockStats(lpStats, null, null).stream()
				.sorted(Comparator.comparingDouble(
						(BlockStats obj) -> obj.getValues().values().stream()
						.mapToInt(Integer::intValue)
						.average()
						.orElse(0.0)).reversed())
				.limit(7)
				.toList();
		StatsViewUtils.initBlockStats(ibmPanel, null, blocks);

		//featured mob statistics
		final var entities = EntityStats.getEntityStats(lpStats, null, null).stream()
				.sorted(Comparator.comparingDouble(
						(EntityStats obj) -> obj.getValues().values().stream()
						.mapToInt(Integer::intValue)
						.average()
						.orElse(0.0)).reversed())
				.limit(5)
				.toList();
		StatsViewUtils.initMobStats(ibmPanel, null, entities);

		//and finally, center that panel and add it to this panel
		ibmPanel.setBounds(ibmPanel.getContentBounds());
		ibmPanel.move((bb.width - (pad * 2) - ibmPanel.getBounds().width) / 2, 0);
		add(ibmPanel);
	}
	// --------------------------------------------------
	/**
	 * Initializes the "coming soon" section of the panel, which is a placeholder for
	 * future features that are not yet implemented.
	 */
	private final void initComingSoon()
	{
		//group label
		StatsViewUtils.initGroupLabel(this, translatable("createWorld.tab.more.title"));

		//group contents
		final var lbl = new TLabelElement(TLanguage.misc_comingSoon().append("..."));
		lbl.setBounds(computeNextYBounds(lbl.fontProperty().get().lineHeight, GAP * 3));
		lbl.textAlignmentProperty().set(CompassDirection.CENTER, PersonalHomePanel.class);
		add(lbl);
	}
	// ================================================== ==================================================
	//                                    ModSummaryPanel IMPLEMENTATION
	// ================================================== ==================================================
	/**
	 * {@link TElement} implementation that displays some information about this mod.
	 */
	private static final @ApiStatus.Internal class ModSummaryPanel extends TFillColorElement
	{
		// ==================================================
		public ModSummaryPanel() { super(0x3F000000, 0x10000000); }
		// ==================================================
		protected final @Override void initCallback()
		{
			//math stuff
			final var bb       = getBounds();
			final int pad      = 5;
			final int iconSize = bb.height - (pad * 2);

			//icon
			final var icon = new TTextureElement(BTextures.icon());
			icon.setBounds(bb.x + pad, bb.y + pad, iconSize, iconSize);
			add(icon);

			//info panel
			final var info = new TElement();
			info.setBounds(
					bb.x + iconSize + (pad * 2),
					bb.y + pad,
					bb.width - iconSize - (pad * 3),
					bb.height - (pad * 2));
			initLabels(info);
			initButtons(info);
			add(info);
		}
		// ==================================================
		private final void initLabels(@NotNull TElement onto)
		{
			//prep
			assert (ModInfoProvider.getInstance() != null);
			final var bb   = onto.getBounds();
			final var info = ModInfoProvider.getInstance().getModInfo(MOD_ID);

			//labels
			final var lbl_name = new TLabelElement(BLanguage.mmName_betterstats());
			lbl_name.setBounds(0, 0, bb.width, lbl_name.fontProperty().get().lineHeight);
			lbl_name.textScaleProperty().set(0.9d, ModSummaryPanel.class);
			lbl_name.textColorProperty().set(0xFFFFFF00, ModSummaryPanel.class);
			onto.addRel(lbl_name);

			/*final var lbl_sum = new TLabelElement(BLanguage.mmSummary_betterstats());
			lbl_sum.wrapTextProperty().set(true, ModSummaryPanel.class);
			lbl_sum.textScaleProperty().set(0.75d, ModSummaryPanel.class);
			lbl_sum.setBoundsToFitText(0, lbl_name.getBounds().height + 2, bb.width);
			onto.addRel(lbl_sum);*/

			final var lbl_ver = new TLabelElement(Component.literal("v").append(info.getVersion()));
			lbl_ver.textScaleProperty().set(0.75d, ModSummaryPanel.class);
			lbl_ver.textColorProperty().set(0xFFFFFFFF, ModSummaryPanel.class);
			lbl_ver.setBoundsToFitText(bb.x, lbl_name.getBounds().endY + 4, bb.width);
			onto.add(lbl_ver);
		}
		// --------------------------------------------------
		@SuppressWarnings("removal")
		private final void initButtons(@NotNull TElement onto)
		{
			//hamburger menu button
			final var client  = Objects.requireNonNull(getClient(), "Missing 'client' instance");
			final var bb      = onto.getBounds();
			final var btn_ctx = new TButtonWidget.Paintable(COLOR_BACKGROUND, 0xFF000000, 0xFFFFFFFF);
			btn_ctx.getLabel().setText(Component.literal("☰"));
			btn_ctx.getLabel().textColorProperty().set(0xFFDDDDDD, ModSummaryPanel.class);
			btn_ctx.setBounds(bb.endX - 17, bb.endY - 17, 17, 17);
			btn_ctx.eClicked.register(__ -> btn_ctx.showContextMenu());
			btn_ctx.contextMenuProperty().set(__ -> new TContextMenu.Builder(client)
					.addButton(
							gui(BSprites.gui_icon_faviconCf()).append(" ").append(literal("CurseForge")),
							___ -> showUriScreen(BetterStats.getProperty("mod.link.curseforge"), true))
					.addButton(
							gui(BSprites.gui_icon_faviconMr()).append(" ").append(literal("Modrinth")),
							___ -> showUriScreen(BetterStats.getProperty("mod.link.modrinth"), true))
					.build(), ModSummaryPanel.class);
			onto.add(btn_ctx);
		}
		// ==================================================
	}
	// ================================================== ==================================================
	//                                   QuickAccessPanel IMPLEMENTATION
	// ================================================== ==================================================
	/**
	 * {@link TElement} implementation that serves as the main area for the "quick access"
	 * section of the panel.
	 * <p>
	 * This is where buttons and shortcuts to commonly used features will be placed, allowing
	 * users to quickly access important functionalities without navigating through multiple menus.
	 */
	private static final @ApiStatus.Internal class QuickAccessPanel extends TPanelElement.Paintable
	{
		// ==================================================
		public QuickAccessPanel() { super(0x22000000, 0, 0x33FFFFFF); }
		// ==================================================
		protected void initCallback()
		{
			//TODO - PLACE BUTTONS LIKE "My statistics", "Open file", etc...
		}
		// ==================================================
	}
	// ================================================== ==================================================
}
