package com.thecsdev.betterstats.client.gui.statstab;

import com.thecsdev.betterstats.BetterStats;
import com.thecsdev.betterstats.BetterStatsConfig;
import com.thecsdev.betterstats.api.client.gui.statstab.StatsTab;
import com.thecsdev.betterstats.api.client.gui.statstab.StatsTabUtils;
import com.thecsdev.betterstats.resources.BSSLang;
import com.thecsdev.betterstats.resources.BSSSprites;
import com.thecsdev.common.math.UDim2;
import com.thecsdev.common.properties.IChangeListener;
import com.thecsdev.common.util.TUtils;
import com.thecsdev.common.util.enumerations.CompassDirection;
import com.thecsdev.commonmc.TCDCommons;
import com.thecsdev.commonmc.api.client.gui.TElement;
import com.thecsdev.commonmc.api.client.gui.label.TLabelElement;
import com.thecsdev.commonmc.api.client.gui.misc.TFillColorElement;
import com.thecsdev.commonmc.api.client.gui.panel.TPanelElement;
import com.thecsdev.commonmc.api.client.gui.tooltip.TTooltip;
import com.thecsdev.commonmc.api.client.gui.widget.TCheckboxWidget;
import com.thecsdev.commonmc.resources.TCDCLang;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.thecsdev.commonmc.resources.TComponent.gui;
import static net.minecraft.network.chat.Component.translatable;

/**
 * {@link StatsTab} implementation for configuring {@link BetterStats}.
 * @see BetterStatsConfig
 */
public final class StatsTabSettings extends StatsTab
{
	// ================================================== ==================================================
	//                                   StatsTabSettings IMPLEMENTATION
	// ================================================== ==================================================
	public static final StatsTabSettings INSTANCE = new StatsTabSettings();
	// ==================================================
	private StatsTabSettings() {}
	// ==================================================
	public final @Override @NotNull Component getDisplayName() {
		return gui(BSSSprites.gui_icon_settings()).append(" ").append(BSSLang.gui_menubar_file_settings());
	}
	// --------------------------------------------------
	@SuppressWarnings("UnstableApiUsage")
	public final @Override void initStats(StatsInitContext context)
	{
		//obtain config
		final @NotNull var bss_config = BetterStats.getConfig();
		final @NotNull var tcd_config = TCDCommons.getConfig();

		// ---------- better statistics screen
		StatsTabUtils.initGroupLabel(context.getPanel(), translatable(BetterStats.MOD_ID))
				.textAlignmentProperty().set(CompassDirection.CENTER, StatsTabSettings.class);

		//[betterstats] initialize common-sided settings
		initTableHead(context.getPanel(), TCDCLang.config_common(), TCDCLang.config_propertyValue());
		initBooleanProperty(
				context.getPanel(),
				BSSLang.config_common_registerCommands(),
				TTooltip.of(BSSLang.config_common_registerCommands_tooltip()),
				bss_config.canRegisterCommands(), (p, o, n) -> bss_config.setRegisterCommands(n));

		//[betterstats] initialize client-sided settings
		initTableHead(context.getPanel(), TCDCLang.config_client(), TCDCLang.config_propertyValue());
		initBooleanProperty(
				context.getPanel(),
				BSSLang.config_client_guiMobsFollowCursor(),
				TTooltip.of(BSSLang.config_client_guiMobsFollowCursor_tooltip()),
				bss_config.getGuiMobsFollowCursor(), (p, o, n) -> bss_config.setGuiMobsFollowCursor(n));

		//[betterstats] initialize server-sided settings
		initTableHead(context.getPanel(), TCDCLang.config_server(), TCDCLang.config_propertyValue());
		initNothingSetting(context.getPanel());

		// ---------- tcd-commons api
		StatsTabUtils.initGroupLabel(context.getPanel(), translatable(TCDCommons.MOD_ID))
				.textAlignmentProperty().set(CompassDirection.CENTER, StatsTabSettings.class);

		//[tcdcommons] initialize common-sided settings
		initTableHead(context.getPanel(), TCDCLang.config_common(), TCDCLang.config_propertyValue());
		initNothingSetting(context.getPanel());

		//[tcdcommons] initialize client-sided settings
		initTableHead(context.getPanel(), TCDCLang.config_client(), TCDCLang.config_propertyValue());
		initBooleanProperty(
				context.getPanel(),
				TCDCLang.config_client_updateItemGroupsOnJoin(),
				TTooltip.of(TCDCLang.config_client_updateItemGroupsOnJoin_tooltip()),
				tcd_config.updateItemGroupsOnJoin(), (p, o, n) -> tcd_config.setUpdateItemGroupsOnJoin(n));

		//[tcdcommons] initialize server-sided settings
		initTableHead(context.getPanel(), TCDCLang.config_server(), TCDCLang.config_propertyValue());
		initNothingSetting(context.getPanel());

		// ---------- end
		//flag element that saves the config once this tab closes
		final var el_saveFlag = new TElement();
		el_saveFlag.setBounds(context.getPanel().computeNextYBounds(0, 0));
		el_saveFlag.screenProperty().addChangeListener((p, o, n) -> {
			if(n == null) TUtils.uncheckedCall(() -> {
				bss_config.saveToFile();
				tcd_config.saveToFile();
			});
		});
		context.getPanel().add(el_saveFlag);
	}
	// ==================================================
	/**
	 * Initializes a table header.
	 * @param panel The panel to add the table header to.
	 * @param key The key label {@link Component}.
	 * @param value The value label {@link Component}.
	 */
	private final void initTableHead(
			@NotNull TPanelElement panel,
			@NotNull Component key, @NotNull Component value)
	{
		final var table_head = new TFillColorElement(0xFF2b2b2b, 0xFF000000);
		table_head.setBounds(panel.computeNextYBounds(25, 10));
		panel.add(table_head);

		final var lbl_key = new TLabelElement();
		lbl_key.setBounds(table_head.getBounds().add(10, 0, -20, 0));
		lbl_key.setText(key);
		lbl_key.textColorProperty().set(0xDDFFFFFF, StatsTabSettings.class);
		lbl_key.textAlignmentProperty().set(CompassDirection.WEST, StatsTabSettings.class);
		table_head.add(lbl_key);

		final var lbl_value = new TLabelElement();
		lbl_value.setBounds(table_head.getBounds().add(10, 0, -20, 0));
		lbl_value.setText(value);
		lbl_value.textColorProperty().set(0xDDFFFFFF, StatsTabSettings.class);
		lbl_value.textAlignmentProperty().set(CompassDirection.EAST, StatsTabSettings.class);
		table_head.add(lbl_value);

		final var divider = new TFillColorElement.Flat(0xFF000000, 0);
		table_head.add(divider);
		divider.setBounds(new UDim2(0.7, 0, 0, 0), new UDim2(0, 1, 1, 0));
	}
	// --------------------------------------------------
	/**
	 * Initializes a blank setting slot that has no setting there.
	 * @param panel The target {@link TPanelElement}.
	 */
	private final void initNothingSetting(@NotNull TPanelElement panel)
	{
		//background color element
		final var el_bg = new TFillColorElement.Flat((panel.size() % 2 == 0) ? 0x33000000 : 0x44000000, 0);
		el_bg.hoverableProperty().set(true, StatsTabSettings.class);
		el_bg.setBounds(panel.computeNextYBounds(26, 0));
		panel.add(el_bg);

		//name label
		final var lbl_name = new TLabelElement();
		lbl_name.setBounds(el_bg.getBounds().add(10, 0, -20, 0));
		lbl_name.setText(Component.literal("-"));
		lbl_name.textAlignmentProperty().set(CompassDirection.CENTER, StatsTabSettings.class);
		el_bg.add(lbl_name);
	}
	// --------------------------------------------------
	/**
	 * Initializes a boolean property setting.
	 * @param panel The panel where the setting is to be initialized.
	 * @param name The display name label text.
	 * @param tooltip The tooltip text.
	 * @param value The current value of the setting.
	 * @param changeListener Use this to apply setting value changes.
	 */
	private final void initBooleanProperty(
			@NotNull TPanelElement panel,
			@NotNull Component name, @Nullable TTooltip tooltip,
			boolean value, @NotNull IChangeListener<Boolean> changeListener)
	{
		//background color element
		final var el_bg = new TFillColorElement.Flat((panel.size() % 2 == 0) ? 0x33000000 : 0x44000000, 0);
		el_bg.hoverableProperty().set(true, StatsTabSettings.class);
		el_bg.setBounds(panel.computeNextYBounds(26, 0));
		if(tooltip != null) el_bg.tooltipProperty().set(__ -> tooltip, StatsTabSettings.class);
		panel.add(el_bg);

		//name label
		final var lbl_name = new TLabelElement();
		lbl_name.setBounds(el_bg.getBounds().add(10, 0, -20, 0));
		lbl_name.setText(name);
		el_bg.add(lbl_name);

		//value checkbox
		final var ch_value = new TCheckboxWidget(value);
		ch_value.checkedProperty().addChangeListener(changeListener);
		if(tooltip != null) ch_value.tooltipProperty().set(__ -> tooltip, StatsTabSettings.class);
		el_bg.add(ch_value);
		ch_value.setBounds(new UDim2(1, -30, 0, 3), new UDim2(0, 20, 0, 20));
	}
	// ================================================== ==================================================
}
