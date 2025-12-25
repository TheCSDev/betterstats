package com.thecsdev.betterstats.api.client.gui.screen;

import com.thecsdev.betterstats.api.mcbs.controller.McbsEditor;
import com.thecsdev.betterstats.api.mcbs.controller.McbsEditorTab;
import com.thecsdev.betterstats.api.mcbs.view.McbsEditorGUI;
import com.thecsdev.common.math.UDim2;
import com.thecsdev.commonmc.api.client.gui.screen.ILastScreenProvider;
import com.thecsdev.commonmc.api.client.gui.screen.IStatsListener;
import com.thecsdev.commonmc.api.client.gui.screen.TScreenPlus;
import com.thecsdev.commonmc.api.client.stats.LocalPlayerStatsProvider;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundClientCommandPacket;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

import static net.minecraft.network.protocol.game.ServerboundClientCommandPacket.Action.REQUEST_STATS;

/**
 * The "Better Statistics Screen" itself.
 * The improved and more useful "Statistics" screen.
 */
@Environment(EnvType.CLIENT)
public final class BetterStatsScreen extends TScreenPlus implements ILastScreenProvider, IStatsListener
{
	// ==================================================
	private final @Nullable Screen     lastScreen;
	private final @NotNull  McbsEditor mcbsEditor;
	// --------------------------------------------------
	private boolean receivedLocalPlayerStatsFlag = false;
	// ==================================================
	public BetterStatsScreen(@Nullable Screen lastScreen, @NotNull McbsEditor mcbsEditor) throws NullPointerException {
		super(Component.translatable("gui.stats"));
		this.lastScreen = lastScreen;
		this.mcbsEditor = Objects.requireNonNull(mcbsEditor);
	}
	public BetterStatsScreen(@Nullable Screen lastScreen) {
		this(lastScreen, McbsEditor.INSTANCE);
		this.mcbsEditor.addTab(McbsEditorTab.LOCALPLAYER, false);
	}
	// ==================================================
	/**
	 * Returns the primary MVC controller {@link McbsEditor} instance that
	 * represents the entire editor GUI.
	 */
	public final @NotNull McbsEditor getMcbsEditor() { return this.mcbsEditor; }
	// ==================================================
	public final @Override @Nullable Screen getLastScreen() { return this.lastScreen; }
	public final @Override boolean isAllowingInGameHud() { return false; }
	// --------------------------------------------------
	protected final @Override void openCallback()
	{
		//if already received stats before, no need to request again
		if(this.receivedLocalPlayerStatsFlag) return;

		//request local player's stats from the server
		final @Nullable var connection = getClient().getConnection();
		if(connection != null)
			connection.send(new ServerboundClientCommandPacket(REQUEST_STATS));
	}

	public final @Override void statsReceivedCallback()
	{
		//if already received stats before, do nothing. the reason for this is
		//that some servers love to spam statistics packets, resulting in client's
		//screen refreshing englessly - 'bricking' the user's ability to interface.
		//tldr - don't trust the server. rate-limit it.
		if(this.receivedLocalPlayerStatsFlag) return;
		this.receivedLocalPlayerStatsFlag = true;

		//update the local player statistics tab's stats data
		McbsEditorTab.LOCALPLAYER.loadStatsFrom(LocalPlayerStatsProvider.ofCurrentLocalPlayer());
	}
	// --------------------------------------------------
	protected final @Override void initCallback() {
		//create and add the main editor gui to this screen
		final var editor = new McbsEditorGUI(this.mcbsEditor);
		add(editor);
		editor.setBounds(new UDim2(0.05, 0, 0, 0), new UDim2(0.9, 0, 1, -5));
	}
	// ==================================================
}
