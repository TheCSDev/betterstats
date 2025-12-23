package com.thecsdev.betterstats.api.client.gui.screen;

import com.thecsdev.betterstats.api.mcbs.controller.McbsEditor;
import com.thecsdev.betterstats.api.mcbs.controller.McbsEditorTab;
import com.thecsdev.betterstats.api.mcbs.model.McbsFile;
import com.thecsdev.betterstats.api.mcbs.view.McbsEditorGUI;
import com.thecsdev.common.math.UDim2;
import com.thecsdev.commonmc.api.client.gui.screen.ILastScreenProvider;
import com.thecsdev.commonmc.api.client.gui.screen.TScreenPlus;
import com.thecsdev.commonmc.api.client.stats.LocalPlayerStatsProvider;
import com.thecsdev.commonmc.api.stats.IStatsProvider;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * The "Better Statistics Screen" itself.
 * The improved and more useful "Statistics" screen.
 */
@Environment(EnvType.CLIENT)
public final class BetterStatsScreen extends TScreenPlus implements ILastScreenProvider
{
	// ==================================================
	private final @Nullable Screen     lastScreen;
	private final @NotNull  McbsEditor mcbsEditor;
	// ==================================================
	public BetterStatsScreen(@Nullable Screen lastScreen, @NotNull McbsEditor mcbsEditor) throws NullPointerException {
		super(Component.translatable("gui.stats"));
		this.lastScreen = lastScreen;
		this.mcbsEditor = Objects.requireNonNull(mcbsEditor);
	}
	public BetterStatsScreen(@Nullable Screen lastScreen, @NotNull IStatsProvider statsProvider) {
		//initialize this object
		this(lastScreen, new McbsEditor());
		//create a fresh new mcbbs file instance
		final var mcbsFile = new McbsFile();
		mcbsFile.getStats().clearAndAddAll(Objects.requireNonNull(statsProvider));
		//create a new editor tab for the new mcbs file, add it, and set it as current
		this.mcbsEditor.addTab(new McbsEditorTab(mcbsFile), true);
	}
	public BetterStatsScreen(@Nullable Screen lastScreen) {
		this(lastScreen, LocalPlayerStatsProvider.ofCurrentLocalPlayer());
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
	protected final @Override void initCallback() {
		//create and add the main editor gui to this screen
		final var editor = new McbsEditorGUI(this.mcbsEditor);
		add(editor);
		editor.setBounds(new UDim2(0.05, 0, 0, 0), new UDim2(0.9, 0, 1, -5));
	}
	// ==================================================
}
