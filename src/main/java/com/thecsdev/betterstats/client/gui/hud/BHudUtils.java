package com.thecsdev.betterstats.client.gui.hud;

import com.thecsdev.betterstats.api.mcbs.controller.tab.McbsEditorFileTab;
import com.thecsdev.betterstats.api.mcbs.model.McbsFile;
import com.thecsdev.betterstats.api.mcbs.model.goal.McbsGoal;
import com.thecsdev.betterstats.BetterStats;
import com.thecsdev.betterstats.resource.BLanguage;
import com.thecsdev.betterstats.resource.BSprites;
import com.thecsdev.commonmc.api.client.gui.ctxmenu.TContextMenu;
import com.thecsdev.commonmc.api.client.gui.screen.hud.THudCanvasScreen;

import static com.thecsdev.commonmc.resource.TComponent.gui;
import com.thecsdev.commonmc.api.stats.util.SubjectStats;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.Objects;


/**
 * Utility methods for "pinning" things onto the game's HUD (heads-up display).
 */
@Environment(EnvType.CLIENT)
public final class BHudUtils
{
	// ==================================================
	/**
	 * {@link Path} to the file this mod's pinned HUD elements are saved to and
	 * loaded from.
	 */
	@ApiStatus.Experimental //have not yet decided on a concrete path and implementation
	public static final Path SAVE_FILE = Path.of("config", BetterStats.MOD_ID, "hud_screen.json");
	// ==================================================
	private BHudUtils() {}
	// ==================================================
	/**
	 * Pins a single statistic of the local player onto the HUD.
	 * @param statTypeId The {@link Identifier} of the {@link net.minecraft.stats.StatType}.
	 * @param statSubjectId The {@link Identifier} of the statistic's subject.
	 * @return The newly pinned {@link BHudStatElement}, or {@code null} on failure.
	 * @throws NullPointerException If an argument is {@code null}.
	 */
	public static final @Nullable BHudStatElement pinStat(
			@NotNull BHudStatElement.SubjectKind subjectKind,
			@NotNull Identifier statTypeId,
			@NotNull Identifier statSubjectId)
			throws NullPointerException
	{
		//construct the element with the exact statistic, and adopt it into its type
		final var element = BHudElementTypes.STAT.adopt(new BHudStatElement(
				Objects.requireNonNull(subjectKind, "'SubjectKind' must not be 'null'"),
				Objects.requireNonNull(statTypeId),
				Objects.requireNonNull(statSubjectId)));
		return THudCanvasScreen.INSTANCE.addElement(element) ? element : null;
	}

	/**
	 * Pins a given {@link McbsGoal}'s progress onto the HUD.
	 * @param goal The {@link McbsGoal} to pin.
	 * @param mcbsFile The {@link McbsFile} the goal's progress is measured against.
	 * @return The newly pinned {@link BHudGoalElement}, or {@code null} on failure.
	 * @throws NullPointerException If an argument is {@code null}.
	 */
	public static final @Nullable BHudGoalElement pinGoal(
			@NotNull McbsGoal goal, @NotNull McbsFile mcbsFile) throws NullPointerException
	{
		final var element = BHudElementTypes.GOAL.adopt(
				new BHudGoalElement(Objects.requireNonNull(goal), Objects.requireNonNull(mcbsFile)));
		return THudCanvasScreen.INSTANCE.addElement(element) ? element : null;
	}

	/**
	 * Pins a given {@link McbsGoal}'s progress onto the HUD.
	 * @param goal The {@link McbsGoal} to pin.
	 * @param tab The {@link McbsEditorFileTab} the goal belongs to.
	 * @return The newly pinned {@link BHudGoalElement}, or {@code null} on failure.
	 * @throws NullPointerException If an argument is {@code null}.
	 */
	public static final @Nullable BHudGoalElement pinGoal(
			@NotNull McbsGoal goal, @NotNull McbsEditorFileTab tab) throws NullPointerException {
		return pinGoal(goal, Objects.requireNonNull(tab).getMcbsFile());
	}

	/**
	 * Opens the {@link THudCanvasScreen}, so that the user may rearrange and
	 * configure the elements that are pinned onto the HUD.
	 * @throws NullPointerException If the {@link Minecraft} instance is {@code null}.
	 */
	public static final void openCanvas() throws NullPointerException {
		THudCanvasScreen.INSTANCE.open(Objects.requireNonNull(Minecraft.getInstance()));
	}
	// --------------------------------------------------
	/**
	 * Pins a given {@link SubjectStats} of the local player onto the HUD.
	 * @param stats The statistics to pin.
	 * @return The newly pinned {@link BHudStatElement}, or {@code null} on failure.
	 * @throws NullPointerException If the argument is {@code null}.
	 */
	public static final @Nullable BHudStatElement pinSubjectStats(
			@NotNull SubjectStats<?> stats) throws NullPointerException
	{
		//requirements
		Objects.requireNonNull(stats);

		//the subject kind tells items, blocks, and entities apart
		final @NotNull BHudStatElement.SubjectKind subjectKind;
		try { subjectKind = BHudStatElement.SubjectKind.of(stats); }
		catch(IllegalArgumentException e) { return null; }

		//the statistic's values tell which stat type it belongs to
		final var values = stats.getValues();
		if(values.isEmpty()) return null;
		final var statTypeId = BuiltInRegistries.STAT_TYPE.getKey(values.keySet().iterator().next().getType());
		if(statTypeId == null) return null;

		//pin the statistic
		return pinStat(subjectKind, statTypeId, stats.getSubjectID());
	}
	// --------------------------------------------------
	/**
	 * Adds a "Pin to HUD" entry to a given context menu builder, that pins a
	 * given {@link SubjectStats} onto the HUD when clicked.
	 * @param builder The context menu builder to add the entry to.
	 * @param stats The statistics the entry is to pin.
	 * @throws NullPointerException If an argument is {@code null}.
	 */
	public static final void addPinToHudButton(
			@NotNull TContextMenu.Builder builder, @NotNull SubjectStats<?> stats)
			throws NullPointerException
	{
		Objects.requireNonNull(builder);
		Objects.requireNonNull(stats);
		builder.addButton(
				gui(BSprites.gui_icon_pin()).append(" ").append(BLanguage.gui_hud_pinToHud()),
				_ -> {
					pinSubjectStats(stats);
					openCanvas();
				});
	}
	// ==================================================
}
