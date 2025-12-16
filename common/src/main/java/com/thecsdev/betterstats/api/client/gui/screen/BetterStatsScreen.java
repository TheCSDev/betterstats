package com.thecsdev.betterstats.api.client.gui.screen;

import com.thecsdev.betterstats.api.client.gui.statstab.StatsTab;
import com.thecsdev.betterstats.api.mcbs.McbsFile;
import com.thecsdev.betterstats.client.gui.BetterStatsPanel;
import com.thecsdev.common.math.UDim2;
import com.thecsdev.commonmc.api.client.gui.TElement;
import com.thecsdev.commonmc.api.client.gui.screen.ILastScreenProvider;
import com.thecsdev.commonmc.api.client.gui.screen.IStatsListener;
import com.thecsdev.commonmc.api.client.gui.screen.TScreenPlus;
import com.thecsdev.commonmc.api.client.gui.screen.TScreenWrapper;
import com.thecsdev.commonmc.api.client.stats.LocalPlayerStatsProvider;
import com.thecsdev.commonmc.api.stats.StatsProvider;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.protocol.game.ServerboundClientCommandPacket;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

/**
 * The "Better Statistics Screen" itself.
 * The improved and more useful "Statistics" screen.
 */
public final class BetterStatsScreen extends TScreenPlus implements IBetterStatsGui, IStatsListener, ILastScreenProvider
{
	// ==================================================
	final @Nullable Screen lastScreen;
	// --------------------------------------------------
	private @NotNull McbsFile         mcbsFile;
	private @NotNull StatsTab         statsTab = StatsTab.getHomePage();
	private final    StatsTab.Filters filters  = new StatsTab.Filters();
	// ==================================================
	public BetterStatsScreen(@Nullable Screen lastScreen) { this(lastScreen, LocalPlayerStatsProvider.ofCurrentLocalPlayer()); }
	public BetterStatsScreen(@Nullable Screen lastScreen, @Nullable StatsProvider stats) { this(lastScreen, new McbsFile(stats)); }
	public BetterStatsScreen(@Nullable Screen lastScreen, @Nullable McbsFile mcbsFile) {
		this.lastScreen = lastScreen;
		this.mcbsFile   = (mcbsFile != null) ? mcbsFile : new McbsFile();
	}
	// ==================================================
	public final @Override @Nullable Screen getLastScreen() { return this.lastScreen; }
	// ==================================================
	protected final @NotNull @Override TScreenWrapper<?> createWrapperScreen() { return new BetterStatsScreenWrapper(this); }
	public final @Override boolean isPauseScreen() { return true; }
	// --------------------------------------------------
	public final @Override @NotNull McbsFile getMcbsFile() { return this.mcbsFile; }
	public final @Override void setMcbsFile(@NotNull McbsFile file) throws NullPointerException {
		this.mcbsFile = Objects.requireNonNull(file);
	}
	// --------------------------------------------------
	public final @Override @NotNull StatsTab getStatsTab() { return this.statsTab; }
	public final @Override void setStatsTab(@NotNull StatsTab tab) throws NullPointerException {
		this.statsTab = requireNonNull(tab);
	}
	// --------------------------------------------------
	public final @Override @NotNull StatsTab.Filters getFilters() { return this.filters; }
	// ==================================================
	protected final @Override void openCallback()
	{
		//only request stats if looking and local player's stats
		if(!this.mcbsFile.getStats().isOfLocalPlayer())
			return;
		//request local player's stats from the server
		Optional.ofNullable(getClient())
				.flatMap(client -> Optional.ofNullable(client.getConnection()))
				.ifPresent(conn -> conn.send(new ServerboundClientCommandPacket(ServerboundClientCommandPacket.Action.REQUEST_STATS)));
	}

	public final @Override void statsReceivedCallback()
	{
		//only reinitialize when looking at local player's stats
		if(!this.mcbsFile.getStats().isOfLocalPlayer())
			return;
		//set the updated statistics data, and reinitialize the gui
		this.mcbsFile.getStats().setAll(LocalPlayerStatsProvider.ofCurrentLocalPlayer());
		refresh();
	}

	protected final @Override void initCallback() {
		//create and add the main panel, and then position/size it with UDim-s
		final var panel = new BetterStatsPanel();
		add(panel);
		panel.setBounds(new UDim2(0.05, 0, 0, 0), new UDim2(0.9, 0, 1, -5));
	}

	public final @Override void refresh() {
		//finds the "better stats panel" and reinitializes it if it is present
		if(!isOpen()) return;
		findChild(c -> c instanceof BetterStatsPanel, true).ifPresent(TElement::clearAndInit);
	}
	// ==================================================
}
