package com.thecsdev.betterstats.api.client.gui.screen;

import com.thecsdev.betterstats.BetterStats;
import com.thecsdev.betterstats.api.mcbs.view.statsview.StatsViewUtils;
import com.thecsdev.betterstats.resources.BSSLang;
import com.thecsdev.betterstats.resources.BSSSprites;
import com.thecsdev.common.math.Bounds2i;
import com.thecsdev.common.math.UDim2;
import com.thecsdev.common.properties.IChangeListener;
import com.thecsdev.common.util.TUtils;
import com.thecsdev.common.util.enumerations.CompassDirection;
import com.thecsdev.commonmc.TCDCommons;
import com.thecsdev.commonmc.api.client.gui.TElement;
import com.thecsdev.commonmc.api.client.gui.label.TLabelElement;
import com.thecsdev.commonmc.api.client.gui.misc.TFillColorElement;
import com.thecsdev.commonmc.api.client.gui.panel.TPanelElement;
import com.thecsdev.commonmc.api.client.gui.panel.window.TWindowElement;
import com.thecsdev.commonmc.api.client.gui.render.TGuiGraphics;
import com.thecsdev.commonmc.api.client.gui.screen.ILastScreenProvider;
import com.thecsdev.commonmc.api.client.gui.screen.TScreen;
import com.thecsdev.commonmc.api.client.gui.screen.TScreenPlus;
import com.thecsdev.commonmc.api.client.gui.tooltip.TTooltip;
import com.thecsdev.commonmc.api.client.gui.widget.TCheckboxWidget;
import com.thecsdev.commonmc.api.client.gui.widget.TScrollBarWidget;
import com.thecsdev.commonmc.resources.TCDCLang;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static com.thecsdev.commonmc.resources.TComponent.gui;
import static net.minecraft.network.chat.Component.translatable;

/**
 * {@link TScreen} implementation featuring GUI for configuring this mod.
 */
@Environment(EnvType.CLIENT)
public final class BetterStatsConfigScreen extends TScreenPlus implements ILastScreenProvider
{
	// ================================================== ==================================================
	//                            BetterStatsConfigScreen IMPLEMENTATION
	// ================================================== ==================================================
	private final @Nullable Screen lastScreen;
	// ==================================================
	public BetterStatsConfigScreen(@Nullable Screen lastScreen) {
		super(BSSLang.betterstats().append(" - ").append(BSSLang.gui_menubar_file_settings()));
		this.lastScreen = lastScreen;
	}
	// ==================================================
	public final @Override @Nullable Screen getLastScreen() { return this.lastScreen; }
	public final @Override boolean isAllowingInGameHud() { return false; }
	// ==================================================
	public final @Override void renderCallback(@NotNull TGuiGraphics pencil) {
		if(this.lastScreen == null) return;
		//noinspection UnstableApiUsage
		this.lastScreen.render(pencil.getNative(), pencil.getMouseX(), pencil.getMouseY(), pencil.getDeltaTicks());
		Bounds2i bb = this.getBounds();
		pencil.fillColor(bb.x, bb.y, bb.width, bb.height, 587202559);
	}
	// --------------------------------------------------
	protected final @Override void initCallback() {
		//create and add the window element
		final var wnd = new WindowElement();
		this.add(wnd);
		wnd.setBounds(new UDim2(0.1, 0, 0.1, 0), new UDim2(0.8, 0, 0.8, 0));
	}
	// ==================================================
	/**
	 * Initializes a configuration GUI for this mod.
	 * @param panel The {@link TPanelElement} onto which the GUI is initialized.
	 * @throws NullPointerException If the argument is {@code null}.
	 */
	@SuppressWarnings("UnstableApiUsage")
	public static final void initConfigGui(@NotNull TPanelElement panel) throws NullPointerException
	{
		//argument not null requirement
		Objects.requireNonNull(panel);

		//obtain config instances
		final @NotNull var bss_config = BetterStats.getConfig();
		final @NotNull var tcd_config = TCDCommons.getConfig();

		// ---------- better statistics screen
		StatsViewUtils.initGroupLabel(panel, translatable(BetterStats.MOD_ID))
				.textAlignmentProperty().set(CompassDirection.CENTER, BetterStatsConfigScreen.class);

		//[betterstats] initialize common-sided settings
		initTableHead(panel, TCDCLang.config_common(), TCDCLang.config_propertyValue());
		initBooleanProperty(
				panel,
				BSSLang.config_common_registerCommands(),
				TTooltip.of(BSSLang.config_common_registerCommands_tooltip()),
				bss_config.canRegisterCommands(), (p, o, n) -> bss_config.setRegisterCommands(n));

		//[betterstats] initialize client-sided settings
		initTableHead(panel, TCDCLang.config_client(), TCDCLang.config_propertyValue());
		initBooleanProperty(
				panel,
				BSSLang.config_client_guiMobsFollowCursor(),
				TTooltip.of(BSSLang.config_client_guiMobsFollowCursor_tooltip()),
				bss_config.getGuiMobsFollowCursor(), (p, o, n) -> bss_config.setGuiMobsFollowCursor(n));

		//[betterstats] initialize server-sided settings
		initTableHead(panel, TCDCLang.config_server(), TCDCLang.config_propertyValue());
		initNothingSetting(panel);

		// ---------- tcd-commons api
		StatsViewUtils.initGroupLabel(panel, translatable(TCDCommons.MOD_ID))
				.textAlignmentProperty().set(CompassDirection.CENTER, BetterStatsConfigScreen.class);

		//[tcdcommons] initialize common-sided settings
		initTableHead(panel, TCDCLang.config_common(), TCDCLang.config_propertyValue());
		initNothingSetting(panel);

		//[tcdcommons] initialize client-sided settings
		initTableHead(panel, TCDCLang.config_client(), TCDCLang.config_propertyValue());
		initBooleanProperty(
				panel,
				TCDCLang.config_client_updateItemGroupsOnJoin(),
				TTooltip.of(TCDCLang.config_client_updateItemGroupsOnJoin_tooltip()),
				tcd_config.updateItemGroupsOnJoin(), (p, o, n) -> tcd_config.setUpdateItemGroupsOnJoin(n));

		//[tcdcommons] initialize server-sided settings
		initTableHead(panel, TCDCLang.config_server(), TCDCLang.config_propertyValue());
		initNothingSetting(panel);

		// ---------- end
		//flag element that saves the config once this gui is removed
		final var el_saveFlag = new TElement();
		el_saveFlag.setBounds(panel.computeNextYBounds(0, 0));
		el_saveFlag.screenProperty().addChangeListener((p, o, n) -> {
			if(n == null) TUtils.uncheckedCall(() -> {
				bss_config.saveToFile();
				tcd_config.saveToFile();
			});
		});
		panel.add(el_saveFlag);
	}

	/**
	 * Initializes a table header.
	 * @param panel The panel to add the table header to.
	 * @param key The key label {@link Component}.
	 * @param value The value label {@link Component}.
	 */
	private static final void initTableHead(
			@NotNull TPanelElement panel,
			@NotNull Component key, @NotNull Component value)
	{
		final var table_head = new TFillColorElement(0xFF2b2b2b, 0xFF000000);
		table_head.setBounds(panel.computeNextYBounds(25, 10));
		panel.add(table_head);

		final var lbl_key = new TLabelElement();
		lbl_key.setBounds(table_head.getBounds().add(10, 0, -20, 0));
		lbl_key.setText(key);
		lbl_key.textColorProperty().set(0xDDFFFFFF, BetterStatsConfigScreen.class);
		lbl_key.textAlignmentProperty().set(CompassDirection.WEST, BetterStatsConfigScreen.class);
		table_head.add(lbl_key);

		final var lbl_value = new TLabelElement();
		lbl_value.setBounds(table_head.getBounds().add(10, 0, -20, 0));
		lbl_value.setText(value);
		lbl_value.textColorProperty().set(0xDDFFFFFF, BetterStatsConfigScreen.class);
		lbl_value.textAlignmentProperty().set(CompassDirection.EAST, BetterStatsConfigScreen.class);
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
	private static final void initNothingSetting(@NotNull TPanelElement panel)
	{
		//background color element
		final var el_bg = new TFillColorElement.Flat((panel.size() % 2 == 0) ? 0x33000000 : 0x44000000, 0);
		el_bg.hoverableProperty().set(true, BetterStatsConfigScreen.class);
		el_bg.setBounds(panel.computeNextYBounds(26, 0));
		panel.add(el_bg);

		//name label
		final var lbl_name = new TLabelElement();
		lbl_name.setBounds(el_bg.getBounds().add(10, 0, -20, 0));
		lbl_name.setText(Component.literal("-"));
		lbl_name.textAlignmentProperty().set(CompassDirection.CENTER, BetterStatsConfigScreen.class);
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
	private static final void initBooleanProperty(
			@NotNull TPanelElement panel,
			@NotNull Component name, @Nullable TTooltip tooltip,
			boolean value, @NotNull IChangeListener<Boolean> changeListener)
	{
		//background color element
		final var el_bg = new TFillColorElement.Flat((panel.size() % 2 == 0) ? 0x33000000 : 0x44000000, 0);
		el_bg.hoverableProperty().set(true, BetterStatsConfigScreen.class);
		el_bg.setBounds(panel.computeNextYBounds(26, 0));
		if(tooltip != null) el_bg.tooltipProperty().set(__ -> tooltip, BetterStatsConfigScreen.class);
		panel.add(el_bg);

		//name label
		final var lbl_name = new TLabelElement();
		lbl_name.setBounds(el_bg.getBounds().add(10, 0, -20, 0));
		lbl_name.setText(name);
		el_bg.add(lbl_name);

		//value checkbox
		final var ch_value = new TCheckboxWidget(value);
		ch_value.checkedProperty().addChangeListener(changeListener);
		if(tooltip != null) ch_value.tooltipProperty().set(__ -> tooltip, BetterStatsConfigScreen.class);
		el_bg.add(ch_value);
		ch_value.setBounds(new UDim2(1, -30, 0, 3), new UDim2(0, 20, 0, 20));
	}
	// ================================================== ==================================================
	//                                      WindowElement IMPLEMENTATION
	// ================================================== ==================================================
	/**
	 * {@link TWindowElement} implementation that displays the graphical
	 * interface for configuring this mod.
	 */
	private final class WindowElement extends TWindowElement
	{
		// ==================================================
		public WindowElement()
		{
			titleProperty().set(
					gui(BSSSprites.gui_icon_settings())
							.append(" ")
							.append(BetterStatsConfigScreen.this.titleProperty().get()),
					WindowElement.class);
			closeOperationProperty().set(CloseOperation.CLOSE_SCREEN, WindowElement.class);
			backgroundColorProperty().set(0xFF3c3f41, WindowElement.class);
		}
		// ==================================================
		protected final @Override void initBodyCallback(@NotNull TElement body)
		{
			final var bb = body.getBounds();
			//create and add a panel element
			final var panel = new TPanelElement.Paintable(0, 0, 0x33FFFFFF);
			panel.setBounds(bb.add(0, 0, -8, 0));
			panel.scrollPaddingProperty().set(10, WindowElement.class);
			body.add(panel);
			//create and add scrollbar widget
			final var scroll = new TScrollBarWidget.Flat(panel);
			scroll.setBounds(bb.endX - 8, bb.y, 8, bb.height);
			body.add(scroll);
			//initialize the config gui
			BetterStatsConfigScreen.initConfigGui(panel);
		}
		// ==================================================
	}
	// ================================================== ==================================================
}
