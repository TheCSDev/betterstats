package com.thecsdev.betterstats.api.client.gui.screen;

import com.thecsdev.betterstats.BetterStats;
import com.thecsdev.betterstats.resource.BLanguage;
import com.thecsdev.betterstats.resource.BSprites;
import com.thecsdev.common.math.Bounds2i;
import com.thecsdev.common.math.Point2d;
import com.thecsdev.common.math.UDim2;
import com.thecsdev.common.properties.BooleanProperty;
import com.thecsdev.common.properties.IChangeListener;
import com.thecsdev.common.util.TUtils;
import com.thecsdev.common.util.annotations.Virtual;
import com.thecsdev.common.util.enumerations.CompassDirection;
import com.thecsdev.commonmc.TCDCommons;
import com.thecsdev.commonmc.api.client.gui.TElement;
import com.thecsdev.commonmc.api.client.gui.label.TLabelElement;
import com.thecsdev.commonmc.api.client.gui.misc.TFillColorElement;
import com.thecsdev.commonmc.api.client.gui.misc.TTextureElement;
import com.thecsdev.commonmc.api.client.gui.panel.TPanelElement;
import com.thecsdev.commonmc.api.client.gui.panel.window.TWindowElement;
import com.thecsdev.commonmc.api.client.gui.render.TGuiGraphics;
import com.thecsdev.commonmc.api.client.gui.screen.ILastScreenProvider;
import com.thecsdev.commonmc.api.client.gui.screen.TScreen;
import com.thecsdev.commonmc.api.client.gui.screen.TScreenPlus;
import com.thecsdev.commonmc.api.client.gui.tooltip.TTooltip;
import com.thecsdev.commonmc.api.client.gui.widget.TCheckboxWidget;
import com.thecsdev.commonmc.api.client.gui.widget.TClickableWidget;
import com.thecsdev.commonmc.api.client.gui.widget.TScrollBarWidget;
import com.thecsdev.commonmc.api.client.gui.widget.text.TSimpleTextFieldWidget;
import com.thecsdev.commonmc.resource.TLanguage;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Consumer;

import static com.thecsdev.commonmc.resource.TComponent.gui;

/**
 * {@link TScreen} implementation featuring GUI for configuring this mod.
 */
@Environment(EnvType.CLIENT)
public final class BetterStatsConfigScreen extends TScreenPlus implements ILastScreenProvider
{
	// ================================================== ==================================================
	//                                    BetterStatsConfigScreen CONSTANTS
	// ================================================== ==================================================
	/** The accent color used to visually highlight {@link BetterStats}'s configuration GUI. */
	private static final int CFG_ACCENT_BETTERSTATS = 0xFFFFFF00;
	/** The accent color used to visually highlight {@link TCDCommons}'s configuration GUI. */
	private static final int CFG_ACCENT_TCDCOMMONS = 0xFF7FB3FF;
	// --------------------------------------------------
	/** The background color of a configuration GUI "card". */
	private static final int CFG_CARD_BG_COLOR      = 0x33000000;
	/** The outline color of a configuration GUI "card". */
	private static final int CFG_CARD_OUTLINE_COLOR = 0x24FFFFFF;
	/** The background color of a configuration GUI "card" header bar. */
	private static final int CFG_HEADER_BG_COLOR    = 0x40000000;
	/** The background color of a configuration GUI "setting row" when hovered. */
	private static final int CFG_ROW_HOVER_COLOR    = 0x22FFFFFF;
	/** The color of the thin divider drawn between configuration GUI "setting rows". */
	private static final int CFG_ROW_DIVIDER_COLOR  = 0x18FFFFFF;
	// --------------------------------------------------
	/** The vertical gap between two configuration GUI "cards". */
	private static final int CFG_GAP          = 8;
	/** The vertical gap between a mod's header card and its first configuration GUI "card". */
	private static final int CFG_HEADER_GAP   = 14;
	/**
	 * The content width below which a configuration GUI "setting row" stacks its
	 * control {@link TElement} beneath its name label, instead of next to it.
	 */
	private static final int CFG_STACK_WIDTH  = 240;
	// --------------------------------------------------
	/** The background color of the configuration GUI's sidebar. */
	private static final int CFG_SIDEBAR_BG_COLOR      = 0x40000000;
	/** The outline color of the configuration GUI's sidebar. */
	private static final int CFG_SIDEBAR_OUTLINE_COLOR = 0x24FFFFFF;
	/** The horizontal gap between the configuration GUI's sidebar and its content area. */
	private static final int CFG_SIDEBAR_GAP           = 8;
	/** The padding between the configuration GUI's sidebar borders and its page buttons. */
	private static final int CFG_SIDEBAR_PADDING       = 6;
	/** The minimum width of the configuration GUI's sidebar. */
	private static final int CFG_SIDEBAR_MIN_WIDTH     = 96;
	/** The maximum width of the configuration GUI's sidebar. */
	private static final int CFG_SIDEBAR_MAX_WIDTH     = 150;
	// ================================================== ==================================================
	//                            BetterStatsConfigScreen IMPLEMENTATION
	// ================================================== ==================================================
	private final @Nullable Screen lastScreen;
	// ==================================================
	public BetterStatsConfigScreen(@Nullable Screen lastScreen) {
		super(BLanguage.mmName_betterstats().append(" - ").append(BLanguage.gui_menubar_file_settings()));
		this.lastScreen = lastScreen;
	}
	// ==================================================
	public final @Override @Nullable Screen getLastScreen() { return this.lastScreen; }
	public final @Override boolean isAllowingInGameHud() { return false; }
	// ==================================================
	public final @Override void renderCallback(@NotNull TGuiGraphics pencil) {
		if(this.lastScreen == null) return;
		//noinspection UnstableApiUsage
		this.lastScreen.extractRenderState(pencil.getNative(), pencil.getMouseX(), pencil.getMouseY(), pencil.getDeltaTicks());
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
	 * Initializes a configuration GUI for this mod, featuring a sidebar with one
	 * selectable page per mod, and a content area displaying the selected page.
	 * @param panel The {@link TPanelElement} onto which the GUI is initialized.
	 * @throws NullPointerException If the argument is {@code null}.
	 */
	public static final void initConfigGui(@NotNull TPanelElement panel) throws NullPointerException
	{
		//argument not null requirement
		Objects.requireNonNull(panel);

		//prepare the available pages (one per mod)
		final var pages = new ArrayList<Page>();
		pages.add(new Page(
				BLanguage.mmName_betterstats(), null,
				CFG_ACCENT_BETTERSTATS, BetterStatsConfigScreen::initBetterStatsPage));
		pages.add(new Page(
				TLanguage.mmName_tcdcommons(), null,
				CFG_ACCENT_TCDCOMMONS, BetterStatsConfigScreen::initTcdCommonsPage));

		//preparation - the container's bounding-box, and the sidebar's dimensions
		final var bb       = panel.getBounds();
		final int sidebarW = Math.clamp(bb.width / 4, CFG_SIDEBAR_MIN_WIDTH, CFG_SIDEBAR_MAX_WIDTH);

		//initialize the sidebar
		final var sidebar = new TFillColorElement.Flat(CFG_SIDEBAR_BG_COLOR, CFG_SIDEBAR_OUTLINE_COLOR);
		sidebar.setBounds(bb.x, bb.y, sidebarW, bb.height);
		panel.add(sidebar);

		//initialize the content area and its scroll-bar
		final int contentX = bb.x + sidebarW + CFG_SIDEBAR_GAP;
		final var content  = new TPanelElement.Transparent();
		content.scrollPaddingProperty().set(10, BetterStatsConfigScreen.class);
		content.setBounds(contentX, bb.y, (bb.endX - 8) - contentX, bb.height);
		panel.add(content);
		final var scroll = new TScrollBarWidget.Flat(content);
		scroll.setBounds(bb.endX - 8, bb.y, 8, bb.height);
		panel.add(scroll);

		//initialize the sidebar's page buttons
		final var buttons = new ArrayList<PageButton>();
		int buttonY = bb.y + CFG_SIDEBAR_PADDING;
		for(final var page : pages) {
			final var btn = initPageButton(
					sidebar, page,
					bb.x + CFG_SIDEBAR_PADDING, buttonY,
					sidebarW - (CFG_SIDEBAR_PADDING * 2));
			buttons.add(btn);
			buttonY = btn.getBounds().endY + 4;
		}

		//the page-selection logic
		final Consumer<Integer> selectPage = index ->
		{
			//do nothing if the page is already the selected one
			if(buttons.get(index).selectedProperty().getZ()) return;

			//update the sidebar buttons' selected states
			for(int i = 0; i < buttons.size(); i++)
				buttons.get(i).selectedProperty().set(i == index, BetterStatsConfigScreen.class);

			//rebuild the content area for the selected page
			content.clear();
			content.scrollAmountProperty().set(Point2d.ZERO, BetterStatsConfigScreen.class);
			pages.get(index).initializer.accept(content);
		};

		//wire the page buttons, and select the first page by default
		for(int i = 0; i < buttons.size(); i++) {
			final int index = i;
			buttons.get(i).eClicked.addListener(_ -> selectPage.accept(index));
		}
		selectPage.accept(0);

		//flag element that saves the config once this gui is removed
		final var el_saveFlag = new TElement();
		el_saveFlag.setBounds(bb.x, bb.y, 0, 0);
		el_saveFlag.screenProperty().addChangeListener((_, _, n) ->
		{
			//save the config files otherwise
			if(n == null) TUtils.uncheckedCall(() -> {
				BetterStats.getConfig().saveToFile();
				TCDCommons.getConfig().saveToFile();
			});
		});
		panel.add(el_saveFlag);
	}
	// --------------------------------------------------
	/**
	 * Initializes the "Better Statistics Screen" configuration page's contents.
	 * @param panel The {@link TPanelElement} onto which the page is initialized.
	 * @throws NullPointerException If the argument is {@code null}.
	 */
	@ApiStatus.Internal
	private static final void initBetterStatsPage(@NotNull TPanelElement panel) throws NullPointerException
	{
		//obtain the config instance
		final var config = BetterStats.getConfig();

		//the mod's header card
		initModHeader(
				panel,
				BLanguage.mmName_betterstats(), BLanguage.mmSummary_betterstats(),
				null, CFG_ACCENT_BETTERSTATS);

		//common-sided settings
		new Section(panel, TLanguage.config_common(), CFG_ACCENT_BETTERSTATS)
				.addStringRow(
						BLanguage.config_common_apiEndpoint(),
						BLanguage.config_common_apiEndpoint_tooltip(),
						config.getApiEndpoint().toString(),
						(_, _, n) -> {
							try { config.setApiEndpoint(URI.create(n)); }
							catch(RuntimeException ignored) {}
						})
				.addBooleanRow(
						BLanguage.config_common_registerCommands(),
						BLanguage.config_common_registerCommands_tooltip(),
						config.canRegisterCommands(),
						(_, _, n) -> config.setRegisterCommands(n))
				.addBooleanRow(
						BLanguage.config_common_experiments(),
						BLanguage.config_common_experiments_tooltip(),
						config.experimentsEnabled(),
						(_, _, n) -> config.setExperimentsEnabled(n))
				.finish();

		//client-sided settings
		new Section(panel, TLanguage.config_client(), CFG_ACCENT_BETTERSTATS)
				.addBooleanRow(
						BLanguage.config_client_guiMobsFollowCursor(),
						BLanguage.config_client_guiMobsFollowCursor_tooltip(),
						config.getGuiMobsFollowCursor(),
						(_, _, n) -> config.setGuiMobsFollowCursor(n))
				.addBooleanRow(
						BLanguage.config_client_allowChatPsa(),
						BLanguage.config_client_allowChatPsa_tooltip(),
						config.allowsChatPsaMessages(),
						(_, _, n) -> config.setAllowChatPsaMessages(n))
				.finish();

		//server-sided settings
		new Section(panel, TLanguage.config_server(), CFG_ACCENT_BETTERSTATS)
				.addPlaceholderRow()
				.finish();
	}

	/**
	 * Initializes the "TCDCommons API" configuration page's contents.
	 * @param panel The {@link TPanelElement} onto which the page is initialized.
	 * @throws NullPointerException If the argument is {@code null}.
	 */
	@ApiStatus.Internal
	private static final void initTcdCommonsPage(@NotNull TPanelElement panel) throws NullPointerException
	{
		//obtain the config instance
		final var config = TCDCommons.getConfig();

		//the mod's header card
		initModHeader(
				panel,
				TLanguage.mmName_tcdcommons(), TLanguage.mmSummary_tcdcommons(),
				null, CFG_ACCENT_TCDCOMMONS);

		//common-sided settings
		new Section(panel, TLanguage.config_common(), CFG_ACCENT_TCDCOMMONS)
				.addPlaceholderRow()
				.finish();

		//client-sided settings
		new Section(panel, TLanguage.config_client(), CFG_ACCENT_TCDCOMMONS)
				.addBooleanRow(
						TLanguage.config_client_updateItemGroupsOnJoin(),
						TLanguage.config_client_updateItemGroupsOnJoin_tooltip(),
						config.updateItemGroupsOnJoin(),
						(_, _, n) -> config.setUpdateItemGroupsOnJoin(n))
				.finish();

		//server-sided settings
		new Section(panel, TLanguage.config_server(), CFG_ACCENT_TCDCOMMONS)
				.addPlaceholderRow()
				.finish();
	}
	// --------------------------------------------------
	/**
	 * Initializes a sidebar "page button" that displays a given page's icon and
	 * name, and adds it to a given sidebar element.
	 * @param sidebar The {@link TFillColorElement} serving as the sidebar.
	 * @param page The {@link Page} the button represents.
	 * @param x The button's X coordinate.
	 * @param y The button's Y coordinate.
	 * @param width The button's width.
	 * @return The created {@link PageButton}.
	 * @throws NullPointerException If a {@link NotNull} argument is {@code null}.
	 */
	@ApiStatus.Internal
	private static final PageButton initPageButton(
			@NotNull TFillColorElement sidebar, @NotNull Page page,
			int x, int y, int width) throws NullPointerException
	{
		//argument not null requirements
		Objects.requireNonNull(sidebar);
		Objects.requireNonNull(page);

		//preparation
		final int padX = 8, padY = 7, iconSize = 14;

		//measure the page's name label
		final int textX = (page.icon != null) ? (x + padX + iconSize + 6) : (x + padX);
		final int textW = (x + width - padX) - textX;
		final var lbl_name = new TLabelElement(page.name);
		lbl_name.wrapTextProperty().set(true, PageButton.class);
		lbl_name.textScaleProperty().set(0.9, PageButton.class);
		lbl_name.textColorProperty().set(0xFFA0A0A0, PageButton.class);
		lbl_name.setBounds(textX, y, textW, 0);
		lbl_name.setBoundsToFitText(textX, y, textW);

		//create the page button
		final var button = new PageButton(page.accentColor);
		button.setBounds(x, y, width, Math.max(30, lbl_name.getTextHeight() + (padY * 2)));

		//add the page's icon, if there is one
		if(page.icon != null) {
			final var ico = new TTextureElement(page.icon);
			ico.setBounds(x + padX, y + ((button.getBounds().height - iconSize) / 2), iconSize, iconSize);
			button.add(ico);
		}

		//add the page's name label, keeping its color in sync with the button's state
		lbl_name.setBounds(textX, y, textW, button.getBounds().height);
		button.selectedProperty().addChangeListener((_, _, selected) -> lbl_name.textColorProperty()
				.set(selected ? 0xFFFFFFFF : 0xFFA0A0A0, PageButton.class));
		button.add(lbl_name);

		//add the button to the sidebar
		sidebar.add(button);
		return button;
	}
	// --------------------------------------------------
	/**
	 * Initializes a "header card" that displays a given mod's icon, name and
	 * summary at the top of a configuration page.
	 * @param panel The {@link TPanelElement} onto which the header is initialized.
	 * @param name The mod's display name.
	 * @param summary The mod's summary/description text.
	 * @param icon The mod's icon {@link Identifier}, or {@code null} if it has none.
	 * @param accentColor The accent color used to visually highlight the header.
	 * @throws NullPointerException If a {@link NotNull} argument is {@code null}.
	 */
	@ApiStatus.Internal
	private static final void initModHeader(
			@NotNull TPanelElement panel,
			@NotNull Component name, @NotNull Component summary,
			@Nullable Identifier icon, int accentColor)
			throws NullPointerException
	{
		//not null requirements
		Objects.requireNonNull(panel);
		Objects.requireNonNull(name);
		Objects.requireNonNull(summary);

		//preparation
		final int padX = 10, padY = 7, iconSize = 18, accentWidth = 4;
		final var bb = panel.computeNextYBounds(0, CFG_HEADER_GAP);

		//create the header "card"
		final var card = new TFillColorElement.Flat(CFG_CARD_BG_COLOR, CFG_CARD_OUTLINE_COLOR);
		card.hoverableProperty().set(false, BetterStatsConfigScreen.class);

		//the accent bar (its height is finalized later)
		final var accent = new TFillColorElement.Flat(accentColor);
		accent.hoverableProperty().set(false, BetterStatsConfigScreen.class);
		accent.setBounds(bb.x, bb.y, accentWidth, 0);
		card.add(accent);

		//the mod's icon, if there is one
		int textX = bb.x + accentWidth + padX;
		if(icon != null) {
			final var ico = new TTextureElement(icon);
			ico.setBounds(textX, bb.y + padY, iconSize, iconSize);
			card.add(ico);
			textX = ico.getBounds().endX + padX;
		}

		//the mod's display name
		final int textW = (bb.endX - padX) - textX;
		final var lbl_name = new TLabelElement(name);
		lbl_name.textScaleProperty().set(1.2, BetterStatsConfigScreen.class);
		lbl_name.textColorProperty().set(accentColor, BetterStatsConfigScreen.class);
		lbl_name.setBounds(textX, bb.y + padY, textW, 0);
		lbl_name.setBoundsToFitText(textX, bb.y + padY, textW);
		card.add(lbl_name);

		//the mod's summary text
		final int summaryY = lbl_name.getBounds().endY + 2;
		final var lbl_summary = new TLabelElement(summary);
		lbl_summary.wrapTextProperty().set(true, BetterStatsConfigScreen.class);
		lbl_summary.textScaleProperty().set(0.85, BetterStatsConfigScreen.class);
		lbl_summary.textColorProperty().set(0xFFB0B0B0, BetterStatsConfigScreen.class);
		lbl_summary.setBounds(textX, summaryY, textW, 0);
		lbl_summary.setBoundsToFitText(textX, summaryY, textW);
		card.add(lbl_summary);

		//finalize the card's bounds based on its contents
		final int cardHeight = (lbl_summary.getBounds().endY + padY) - bb.y;
		card.setBounds(bb.x, bb.y, bb.width, cardHeight);
		accent.setBounds(bb.x, bb.y, accentWidth, cardHeight);

		//add the header "card" to the panel
		panel.add(card);
	}
	// ================================================== ==================================================
	//                                               Page IMPLEMENTATION
	// ================================================== ==================================================
	/**
	 * Represents a selectable page within the configuration GUI, dedicated
	 * to a specific mod and its configuration.
	 */
	@ApiStatus.Internal
	private static final class Page
	{
		// ==================================================
		private final @NotNull Component name;
		private final @Nullable Identifier icon;
		private final int accentColor;
		private final @NotNull Consumer<TPanelElement> initializer;
		// ==================================================
		/**
		 * Constructs a {@link Page}.
		 * @param name The page's display name.
		 * @param icon The page's icon {@link Identifier}, or {@code null} if it has none.
		 * @param accentColor The accent color used to visually highlight the page.
		 * @param initializer Use this to initialize the page's contents.
		 * @throws NullPointerException If a {@link NotNull} argument is {@code null}.
		 */
		public Page(@NotNull Component name, @Nullable Identifier icon, int accentColor, @NotNull Consumer<TPanelElement> initializer)
				throws NullPointerException
		{
			this.name        = Objects.requireNonNull(name);
			this.icon        = icon;
			this.accentColor = accentColor;
			this.initializer = Objects.requireNonNull(initializer);
		}
		// ==================================================
	}
	// ================================================== ==================================================
	//                                         PageButton IMPLEMENTATION
	// ================================================== ==================================================
	/**
	 * A {@link TClickableWidget} implementation that serves as a selectable
	 * page entry within the configuration GUI's sidebar.
	 */
	@ApiStatus.Internal
	private static final class PageButton extends TClickableWidget
	{
		// ==================================================
		private final BooleanProperty selected = new BooleanProperty(false);
		private final int             accentColor;
		// ==================================================
		public PageButton(int accentColor) { this.accentColor = accentColor; }
		// ==================================================
		/**
		 * The {@link BooleanProperty} holding whether this page button
		 * is the currently selected page.
		 */
		public final BooleanProperty selectedProperty() { return this.selected; }
		// ==================================================
		public @Virtual @Override void renderCallback(@NotNull TGuiGraphics pencil)
		{
			//prerequisites
			final var bb = getBounds();

			//the selected page is highlighted, and displays an accent bar
			if(this.selected.getZ()) {
				pencil.fillColor(bb.x, bb.y, bb.width, bb.height, 0x33000000);
				pencil.fillColor(bb.x, bb.y, 3, bb.height, this.accentColor);
			}
			//otherwise, highlight this page button on hover
			else if(isHoveredOrFocused())
				pencil.fillColor(bb.x, bb.y, bb.width, bb.height, CFG_ROW_HOVER_COLOR);
		}
		// ==================================================
	}
	// ================================================== ==================================================
	//                                            Section IMPLEMENTATION
	// ================================================== ==================================================
	/**
	 * A {@link TFillColorElement.Flat} implementation that renders a "card"-styled
	 * container, grouping a collection of related configuration settings under a
	 * shared header bar.
	 */
	@ApiStatus.Internal
	private static final class Section extends TFillColorElement.Flat
	{
		// ==================================================
		/** The height of this section's header bar. */
		private static final int HEADER_HEIGHT  = 22;
		/** The amount of padding between this section's borders and its contents. */
		private static final int PADDING        = 4;
		/** The horizontal padding applied to each setting row's contents. */
		private static final int ROW_PAD_X      = 8;
		/** The vertical padding applied to each setting row's contents. */
		private static final int ROW_PAD_Y      = 5;
		/** The gap between a setting's name label and its control element. */
		private static final int CONTROL_GAP    = 10;
		/** The height of a setting's control element. */
		private static final int CONTROL_HEIGHT = 20;
		/** The minimum height of a setting row. */
		private static final int ROW_MIN_HEIGHT = 29;
		// --------------------------------------------------
		private final TPanelElement panel;
		private final Component     title;
		private final int           accentColor;
		// --------------------------------------------------
		private final int contentX, contentWidth;
		private int       nextRowY, rowCount;
		// ==================================================
		/**
		 * Constructs a {@link Section} that is positioned at the bottom of a given
		 * {@link TPanelElement}'s {@link TElement#getContentBounds()}.
		 * @param panel The {@link TPanelElement} this section will be added to.
		 * @param title The section's header bar title.
		 * @param accentColor The accent color used to visually highlight this section.
		 * @throws NullPointerException If a {@link NotNull} argument is {@code null}.
		 */
		public Section(@NotNull TPanelElement panel, @NotNull Component title, int accentColor)
				throws NullPointerException
		{
			//super
			super(CFG_CARD_BG_COLOR, CFG_CARD_OUTLINE_COLOR);

			//assign fields
			this.panel       = Objects.requireNonNull(panel);
			this.title       = Objects.requireNonNull(title);
			this.accentColor = accentColor;

			//a "card" itself is not a setting that can be interacted with
			hoverableProperty().set(false, Section.class);

			//calculate where this section will be placed, and where its contents go
			final var bb = panel.computeNextYBounds(0, CFG_GAP);
			this.contentX     = bb.x + PADDING;
			this.contentWidth = bb.width - (PADDING * 2);
			this.nextRowY     = bb.y + HEADER_HEIGHT + PADDING;
			setBounds(bb);
		}
		// ==================================================
		/**
		 * Initializes a {@link TCheckboxWidget} setting row.
		 * @param name The setting's display name.
		 * @param tooltipText The setting's tooltip text, or {@code null} if there is none.
		 * @param value The setting's current value.
		 * @param changeListener Use this to apply value changes.
		 * @throws NullPointerException If a {@link NotNull} argument is {@code null}.
		 */
		public final Section addBooleanRow(
				@NotNull Component name, @Nullable Component tooltipText,
				boolean value, @NotNull IChangeListener<Boolean> changeListener)
				throws NullPointerException
		{
			//argument not null requirements
			Objects.requireNonNull(name);
			Objects.requireNonNull(changeListener);

			//preparation - decide the layout based on the available space
			final boolean stacked = (this.contentWidth < CFG_STACK_WIDTH);
			final int     nameX   = this.contentX + ROW_PAD_X;
			final int     nameW   = stacked ?
					(this.contentWidth - (ROW_PAD_X * 2)) :
					(this.contentWidth - (ROW_PAD_X * 2) - CONTROL_HEIGHT - CONTROL_GAP);

			//measure the name label so the row can fit the text
			final var lbl_name = new TLabelElement(name);
			lbl_name.wrapTextProperty().set(true, Section.class);
			lbl_name.setBounds(nameX, this.nextRowY, nameW, 0);
			lbl_name.setBoundsToFitText(nameX, this.nextRowY, nameW);
			final int nameHeight = lbl_name.getTextHeight();

			//calculate the row's height based on its contents
			final int rowHeight = stacked ?
					Math.max(ROW_MIN_HEIGHT, nameHeight + CONTROL_HEIGHT + (ROW_PAD_Y * 3)) :
					Math.max(ROW_MIN_HEIGHT, nameHeight + (ROW_PAD_Y * 2));

			//create and add the setting row
			final var row = initRowBase(initTooltip(name, tooltipText), rowHeight);

			//position and add the name label
			if(stacked) lbl_name.setBounds(nameX, row.getBounds().y + ROW_PAD_Y, nameW, nameHeight);
			else        lbl_name.setBounds(nameX, row.getBounds().y, nameW, rowHeight);
			row.add(lbl_name);

			//position and add the value checkbox
			final int checkY = stacked ?
					(row.getBounds().endY - ROW_PAD_Y - CONTROL_HEIGHT) :
					(row.getBounds().y + ((rowHeight - CONTROL_HEIGHT) / 2));
			final int checkX = stacked ?
					nameX :
					(this.contentX + this.contentWidth - ROW_PAD_X - CONTROL_HEIGHT);
			final var ch_value = new TCheckboxWidget(value);
			ch_value.checkedProperty().addChangeListener(changeListener);
			ch_value.setBounds(checkX, checkY, CONTROL_HEIGHT, CONTROL_HEIGHT);
			row.add(ch_value);

			//return this section for chaining
			return this;
		}

		/**
		 * Initializes a {@link TSimpleTextFieldWidget} setting row.
		 * @param name The setting's display name.
		 * @param tooltipText The setting's tooltip text, or {@code null} if there is none.
		 * @param value The setting's current value.
		 * @param changeListener Use this to apply value changes.
		 * @throws NullPointerException If a {@link NotNull} argument is {@code null}.
		 */
		public final Section addStringRow(
				@NotNull Component name, @Nullable Component tooltipText,
				@NotNull String value, @NotNull IChangeListener<String> changeListener)
				throws NullPointerException
		{
			//argument not null requirements
			Objects.requireNonNull(name);
			Objects.requireNonNull(value);
			Objects.requireNonNull(changeListener);

			//preparation - decide the layout based on the available space
			final boolean stacked  = (this.contentWidth < CFG_STACK_WIDTH);
			final int     nameX    = this.contentX + ROW_PAD_X;
			final int     controlW = stacked ?
					(this.contentWidth - (ROW_PAD_X * 2)) :
					Math.clamp(this.contentWidth / 2, 120, 260);
			final int     nameW    = stacked ?
					(this.contentWidth - (ROW_PAD_X * 2)) :
					(this.contentWidth - (ROW_PAD_X * 2) - controlW - CONTROL_GAP);

			//measure the name label so the row can fit the text
			final var lbl_name = new TLabelElement(name);
			lbl_name.wrapTextProperty().set(true, Section.class);
			lbl_name.setBounds(nameX, this.nextRowY, nameW, 0);
			lbl_name.setBoundsToFitText(nameX, this.nextRowY, nameW);
			final int nameHeight = lbl_name.getTextHeight();

			//calculate the row's height based on its contents
			final int rowHeight = stacked ?
					Math.max(ROW_MIN_HEIGHT, nameHeight + CONTROL_HEIGHT + (ROW_PAD_Y * 3)) :
					Math.max(ROW_MIN_HEIGHT, nameHeight + (ROW_PAD_Y * 2));

			//create and add the setting row
			final var row = initRowBase(initTooltip(name, tooltipText), rowHeight);

			//position and add the name label
			if(stacked) lbl_name.setBounds(nameX, row.getBounds().y + ROW_PAD_Y, nameW, nameHeight);
			else        lbl_name.setBounds(nameX, row.getBounds().y, nameW, rowHeight);
			row.add(lbl_name);

			//position and add the value text field
			final int fieldY = stacked ?
					(row.getBounds().endY - ROW_PAD_Y - CONTROL_HEIGHT) :
					(row.getBounds().y + ((rowHeight - CONTROL_HEIGHT) / 2));
			final int fieldX = stacked ?
					nameX :
					(this.contentX + this.contentWidth - ROW_PAD_X - controlW);
			final var in_value = new TSimpleTextFieldWidget();
			in_value.getTextLabel().textScaleProperty().set(0.9, Section.class);
			in_value.textProperty().set(value, Section.class);
			in_value.textProperty().addChangeListener(changeListener);
			in_value.setBounds(fieldX, fieldY, controlW, CONTROL_HEIGHT);
			row.add(in_value);
			in_value.clearAndInit(); //band-aid fix to its internal text labels disappearing

			//return this section for chaining
			return this;
		}

		/**
		 * Initializes a "placeholder" setting row, informing the user that there
		 * are no settings available there for the time being.
		 */
		public final Section addPlaceholderRow()
		{
			//create and add the placeholder row
			final var row = initRowBase(null, ROW_MIN_HEIGHT);

			//create and add the placeholder text label
			final var lbl_text = new TLabelElement(Component.literal("-"));
			lbl_text.textColorProperty().set(0x80FFFFFF, Section.class);
			lbl_text.textAlignmentProperty().set(CompassDirection.CENTER, Section.class);
			lbl_text.setBounds(row.getBounds());
			row.add(lbl_text);

			//return this section for chaining
			return this;
		}
		// ==================================================
		/**
		 * Finalizes this section by calculating its final bounds based on its
		 * contents, initializing its header bar, and adding it to its target
		 * {@link TPanelElement}.
		 */
		public final void finish()
		{
			//calculate and assign this section's final bounds
			final var sbb = getBounds();
			setBounds(sbb.x, sbb.y, sbb.width, (this.nextRowY + PADDING) - sbb.y);

			//initialize the header bar
			final var header = new TFillColorElement.Flat(CFG_HEADER_BG_COLOR);
			header.hoverableProperty().set(false, Section.class);
			header.setBounds(sbb.x + 1, sbb.y + 1, sbb.width - 2, HEADER_HEIGHT);
			add(header);

			//initialize the header's accent bar
			final var accent = new TFillColorElement.Flat(this.accentColor);
			accent.hoverableProperty().set(false, Section.class);
			accent.setBounds(header.getBounds().x, header.getBounds().y, 3, header.getBounds().height);
			header.add(accent);

			//initialize the header's title label
			final var lbl_title = new TLabelElement(this.title);
			lbl_title.textColorProperty().set(0xFFFFFFFF, Section.class);
			lbl_title.setBounds(header.getBounds().add(10, 0, -14, 0));
			header.add(lbl_title);

			//add this section to its target panel
			this.panel.add(this);
		}
		// ==================================================
		/**
		 * Creates the base of a setting row; that is, its outline, its divider,
		 * its hover-highlight and its tooltip.
		 * @param tooltip The row's {@link TTooltip}, or {@code null} if there is none.
		 * @param height The row's height.
		 * @return The created {@link RowElement}.
		 */
		private final RowElement initRowBase(@Nullable TTooltip tooltip, int height)
		{
			//the row's bounds
			final var rbb = new Bounds2i(this.contentX, this.nextRowY, this.contentWidth, height);

			//draw a divider above every row except the first one
			if(this.rowCount > 0) {
				final var divider = new TFillColorElement.Flat(CFG_ROW_DIVIDER_COLOR);
				divider.hoverableProperty().set(false, Section.class);
				divider.setBounds(rbb.x, rbb.y, rbb.width, 1);
				add(divider);
			}

			//create and add the row's hover-able background
			final var row = new RowElement(this.accentColor);
			row.setBounds(rbb);
			if(tooltip != null) row.tooltipProperty().set(_ -> tooltip, Section.class);
			add(row);

			//advance the row cursor and track the row count
			this.nextRowY += height;
			this.rowCount++;

			//return the created row
			return row;
		}

		/**
		 * Creates a {@link TTooltip} that displays a setting's display name and,
		 * if available, its tooltip text.
		 * @param name The setting's display name.
		 * @param tooltipText The setting's tooltip text, or {@code null} if there is none.
		 * @throws NullPointerException If a {@link NotNull} argument is {@code null}.
		 */
		private static final @NotNull TTooltip initTooltip(
				@NotNull Component name, @Nullable Component tooltipText)
				throws NullPointerException
		{
			//a setting's name is always shown in the tooltip
			final var text = Component.literal("")
					.append(Component.literal("").append(name).withStyle(ChatFormatting.YELLOW));

			//append the tooltip text, if there is any
			if(tooltipText != null)
				text.append("\n")
						.append(Component.literal("").append(tooltipText).withStyle(ChatFormatting.GRAY));

			//create and return the tooltip
			return TTooltip.of(text);
		}
		// ==================================================
	}
	// ================================================== ==================================================
	//                                         RowElement IMPLEMENTATION
	// ================================================== ==================================================
	/**
	 * A {@link TElement} implementation that serves as the hover-able background
	 * of a configuration setting row, highlighting itself when hovered.
	 */
	@ApiStatus.Internal
	private static final class RowElement extends TElement
	{
		// ==================================================
		private final int accentColor;
		// ==================================================
		public RowElement(int accentColor) { this.accentColor = accentColor; }
		// ==================================================
		public @Virtual @Override void renderCallback(@NotNull TGuiGraphics pencil)
		{
			//only render the highlight when this row is hovered or focused
			if(!isRowHovered()) return;

			//draw the highlight and the accent bar
			final var bb = getBounds();
			pencil.fillColor(bb.x, bb.y, bb.width, bb.height, CFG_ROW_HOVER_COLOR);
			pencil.fillColor(bb.x, bb.y + 3, 2, bb.height - 6, this.accentColor);
		}
		// ==================================================
		/**
		 * Returns {@code true} if this row, or any of its descendants, is currently
		 * hovered, or if this row itself is currently focused.
		 */
		private final boolean isRowHovered()
		{
			//this row itself being hovered or focused is enough
			if(isHoveredOrFocused()) return true;

			//otherwise, check if one of this row's descendants is hovered
			final @Nullable var screen = screenProperty().get();
			if(screen == null) return false;
			final @Nullable var hovered = screen.hoveredElementProperty().get();
			return hovered != null && hovered.findParent(parent -> parent == this).isPresent();
		}
		// ==================================================
	}
	// ================================================== ==================================================
	//                                      WindowElement IMPLEMENTATION
	// ================================================== ==================================================
	/**
	 * {@link TWindowElement} implementation that displays the graphical
	 * interface for configuring this mod.
	 */
	@ApiStatus.Internal
	private final class WindowElement extends TWindowElement
	{
		// ==================================================
		public WindowElement()
		{
			titleProperty().set(
					gui(BSprites.gui_icon_settings())
							.append(" ")
							.append(BetterStatsConfigScreen.this.titleProperty().get()),
					WindowElement.class);
			closeOperationProperty().set(CloseOperation.CLOSE_SCREEN, WindowElement.class);
			backgroundColorProperty().set(0xFF3c3f41, WindowElement.class);
		}
		// ==================================================
		protected final @Override void initBodyCallback(@NotNull TElement body)
		{
			//create and add the config GUI's container panel
			final var panel = new TPanelElement.Paintable(0, 0, 0x33FFFFFF);
			panel.setBounds(body.getBounds());
			body.add(panel);

			//initialize the config gui
			BetterStatsConfigScreen.initConfigGui(panel);
		}
		// ==================================================
	}
	// ================================================== ==================================================
}
