package com.thecsdev.betterstats.mcbs.view.menubar;

import com.thecsdev.betterstats.api.mcbs.controller.McbsEditor;
import com.thecsdev.betterstats.api.mcbs.controller.tab.McbsEditorFileTab;
import com.thecsdev.betterstats.api.mcbs.controller.tab.McbsEditorSettingsTab;
import com.thecsdev.betterstats.api.mcbs.controller.tab.McbsEditorTab;
import com.thecsdev.betterstats.api.mcbs.model.McbsFile;
import com.thecsdev.betterstats.api.mcbs.view.menubar.MenubarItem;
import com.thecsdev.betterstats.resource.BLanguage;
import com.thecsdev.betterstats.resource.BSprites;
import com.thecsdev.common.util.TUtils;
import com.thecsdev.commonmc.api.client.gui.ctxmenu.TContextMenu;
import com.thecsdev.commonmc.api.client.gui.screen.TTextDialogScreen;
import com.thecsdev.commonmc.api.client.gui.screen.promise.TFileChooserScreen;
import com.thecsdev.commonmc.resource.TLanguage;
import com.thecsdev.commonmc.resource.TSprites;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;

import static com.thecsdev.commonmc.resource.TComponent.air;
import static com.thecsdev.commonmc.resource.TComponent.gui;
import static org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace;

/**
 * {@link MenubarItem} implementation for "File".
 */
@ApiStatus.Internal
@Environment(EnvType.CLIENT)
public final class MenubarItemFile extends MenubarItem
{
	// ==================================================
	public static final MenubarItemFile INSTANCE = new MenubarItemFile();
	// ==================================================
	public final @Override @NotNull Component getDisplayName() { return BLanguage.gui_menubar_file(); }
	// --------------------------------------------------
	public final @Override @NotNull TContextMenu createContextMenu(
			@NotNull Minecraft client, @NotNull McbsEditor mcbsEditor)
	{
		//not null requirements
		Objects.requireNonNull(client, "Missing 'client' instance");
		Objects.requireNonNull(mcbsEditor, "Missing 'editor' instance");

		//create new builder
		final var builder = new TContextMenu.Builder(client);

		//"Open" option
		builder.addButton(
				gui(TSprites.gui_icon_fsFolder()).append(" ").append(BLanguage.gui_menubar_file_open()),
				__ -> showOpenFileDialog(client, mcbsEditor));

		//"Save as" option
		if(mcbsEditor.getCurrentTab() instanceof McbsEditorFileTab)
			builder.addButton(air().append(" ").append(
					BLanguage.gui_menubar_file_saveAs()),
					__ -> showSaveFileDialog(client, mcbsEditor));

		//"Settings" option
		builder.addSeparator();
		builder.addButton(
				gui(BSprites.gui_icon_settings()).append(" ").append(BLanguage.gui_menubar_file_settings()),
				__ -> mcbsEditor.addTab(McbsEditorSettingsTab.INSTANCE, true));

		//"Close" option
		builder.addButton(
				gui(BSprites.gui_icon_close()).append(" ").append(BLanguage.gui_menubar_file_close()),
				__ -> Optional.ofNullable(client.screen).ifPresent(Screen::onClose));

		//build and return the context menu
		return builder.build();
	}
	// ==================================================
	/**
	 * Shows the "Open File" dialog and handles loading the selected {@link McbsFile}
	 * into a new {@link McbsEditorTab}.
	 * @param client The {@link Minecraft} client instance.
	 * @param mcbsEditor The {@link McbsEditor} instance to add the new tab to.
	 * @throws NullPointerException If an argument is {@code null}.
	 */
	private static final @ApiStatus.Internal void showOpenFileDialog(
			@NotNull Minecraft client, @NotNull McbsEditor mcbsEditor)
			throws NullPointerException
	{
		final var lastScreen = client.screen;
		final var dialog     = new TFileChooserScreen.Builder(TFileChooserScreen.Mode.CHOOSE_FILE)
				.setLastScreen(lastScreen)
				.setPathFilter(TFileChooserScreen.PathFilter.extname("json"))
				.addPathFilter(TFileChooserScreen.PathFilter.extname("nbt"))
				.build();
		dialog.getResult().thenApply(paths -> TUtils.uncheckedSupply(() -> {
			mcbsEditor.addTab(new McbsEditorFileTab(paths.getFirst()), true);
			return paths;
		}))
		.exceptionally(throwable -> {
			if(dialog.getResult().isCancelled()) return null;
			final var tit  = TLanguage.misc_somethingWentWrong();
			final var txt  = Component.literal(getStackTrace(throwable).replace("\r\n", "\n").replace("\t", "    "));
			final var edia = new TTextDialogScreen(client.screen, tit, txt);
			edia.getMessageLabel().wrapTextProperty().toggle();
			client.setScreen(edia.getAsScreen());
			return null;
		});
		client.setScreen(dialog.getAsScreen());
	}

	/**
	 * Shows the "Save File" dialog and handles saving the current {@link McbsEditorTab}'s
	 * {@link McbsFile} to the selected location.
	 * @param client The {@link Minecraft} client instance.
	 * @param mcbsEditor The {@link McbsEditor} instance containing the mcbs file to save.
	 * @throws NullPointerException If an argument is {@code null}.
	 */
	private static final @ApiStatus.Internal void showSaveFileDialog(
			@NotNull Minecraft client, @NotNull McbsEditor mcbsEditor)
			throws NullPointerException
	{
		//an editor tab for a file needs to be open, for this feature
		final var fileTab  = (mcbsEditor.getCurrentTab() instanceof McbsEditorFileTab meft) ? meft : null;
		if(fileTab == null) return;

		//create and show the dialog
		final var lastScreen = client.screen;
		final var dialog     = new TFileChooserScreen.Builder(TFileChooserScreen.Mode.CREATE_FILE)
				.setLastScreen(lastScreen)
				.setPathFilter(TFileChooserScreen.PathFilter.extname("json"))
				.addPathFilter(TFileChooserScreen.PathFilter.extname("nbt"))
				.build();
		dialog.getResult().thenApply(paths -> TUtils.uncheckedSupply(() -> {
			fileTab.saveAs(paths.getFirst());
			return paths;
		}))
		.exceptionally(throwable -> {
			if(dialog.getResult().isCancelled()) return null;
			final var tit  = TLanguage.misc_somethingWentWrong();
			final var txt  = Component.literal(getStackTrace(throwable).replace("\r\n", "\n").replace("\t", "    "));
			final var edia = new TTextDialogScreen(client.screen, tit, txt);
			edia.getMessageLabel().wrapTextProperty().toggle();
			client.setScreen(edia.getAsScreen());
			return null;
		});
		client.setScreen(dialog.getAsScreen());
	}
	// ==================================================
}
