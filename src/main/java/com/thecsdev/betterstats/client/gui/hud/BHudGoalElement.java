package com.thecsdev.betterstats.client.gui.hud;

import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.thecsdev.betterstats.api.mcbs.controller.tab.McbsEditorFileTab;
import com.thecsdev.betterstats.api.mcbs.model.McbsFile;
import com.thecsdev.betterstats.api.mcbs.model.goal.McbsGoal;
import com.thecsdev.betterstats.api.mcbs.view.goal.McbsGoalGUI;
import com.thecsdev.common.util.annotations.Virtual;
import com.thecsdev.common.util.enumerations.CompassDirection;
import com.thecsdev.commonmc.api.client.gui.TElement;
import com.thecsdev.commonmc.api.client.gui.label.TLabelElement;
import com.thecsdev.commonmc.api.client.gui.misc.TFillColorElement;
import com.thecsdev.commonmc.api.client.gui.panel.TPanelElement;
import com.thecsdev.commonmc.api.client.gui.render.TGuiGraphics;
import com.thecsdev.commonmc.api.client.gui.screen.hud.THudElement;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static net.minecraft.network.chat.Component.literal;

/**
 * A {@link THudElement} that displays the progress of an {@link McbsGoal}
 * on the HUD.
 * <p>
 * The goal itself is serialized alongside this element, which is what allows
 * the progress display to survive game restarts. The {@link McbsFile} the
 * progress is calculated against is <b>not</b> serialized, as it represents
 * live data - it is assigned when the goal is pinned, and re-assigned by
 * whoever owns the goal's data.
 *
 * @see BHudElementTypes#GOAL
 */
@Environment(EnvType.CLIENT)
public final class BHudGoalElement extends THudElement
{
	// ==================================================
	/**
	 * The height of this element, in GUI units.
	 */
	public static final int HEIGHT = 18;

	/**
	 * The width of the goal's icon, in GUI units.
	 */
	public static final int ICON_SIZE = 14;

	/**
	 * The gap between the goal's icon and its objective text, in GUI units.
	 */
	public static final int ICON_TEXT_GAP = 5;

	/**
	 * The right-hand margin of the progress text label, in GUI units.
	 */
	public static final int PROGRESS_LABEL_MARGIN = 4;

	/**
	 * The height of the progress line drawn along this element's bottom, in GUI units.
	 */
	public static final int PROGRESS_LINE_HEIGHT = 2;

	/**
	 * The width reserved for the progress text label, in GUI units.
	 */
	public static final int PROGRESS_LABEL_WIDTH = 68;

	/**
	 * The minimum total width of this element, in GUI units.
	 */
	public static final int MIN_WIDTH = 150;

	/**
	 * The maximum total width of this element, in GUI units.
	 */
	public static final int MAX_WIDTH = 280;

	/**
	 * {@link MapCodec} for an {@link McbsGoal}.
	 */
	@SuppressWarnings("unchecked") //McbsGoal.CODEC is a partial-dispatch codec
	private static final MapCodec<McbsGoal> GOAL_CODEC = (MapCodec<McbsGoal>) (MapCodec<?>) McbsGoal.CODEC.fieldOf("goal");

	/**
	 * {@link MapCodec} implementation for serializing {@link BHudGoalElement}s.
	 */
	public static final MapCodec<BHudGoalElement> CODEC = RecordCodecBuilder.mapCodec(instance ->
			instance.group(
					GOAL_CODEC.forGetter(BHudGoalElement::getGoal)
			).apply(instance, BHudGoalElement::new)
	);
	// ==================================================
	private final @NotNull  McbsGoal goal;
	private       @Nullable McbsFile mcbsFile;    //transient, the live data to measure progress against
	// --------------------------------------------------
	private @Nullable TLabelElement     lblObjective;
	private @Nullable TLabelElement     lblProgress;
	private @Nullable TFillColorElement barFill;
	// ==================================================
	/**
	 * @param goal The {@link McbsGoal} whose progress is to be displayed.
	 * @throws NullPointerException If the argument is {@code null}.
	 */
	public BHudGoalElement(@NotNull McbsGoal goal) throws NullPointerException {
		this.goal = Objects.requireNonNull(goal);
	}

	/**
	 * Constructs a new {@link BHudGoalElement} and assigns a default anchor point.
	 * @param goal The {@link McbsGoal} whose progress is to be displayed.
	 * @param mcbsFile The {@link McbsFile} the goal's progress is measured against.
	 * @throws NullPointerException If a {@link NotNull} argument is {@code null}.
	 */
	public BHudGoalElement(@NotNull McbsGoal goal, @NotNull McbsFile mcbsFile) throws NullPointerException {
		this(goal);
		this.mcbsFile = Objects.requireNonNull(mcbsFile);
	}
	// ==================================================
	/**
	 * Returns the {@link McbsGoal} whose progress this element displays.
	 */
	public final @NotNull McbsGoal getGoal() { return this.goal; }

	/**
	 * Returns the {@link McbsFile} this element's goal progress is measured
	 * against, or {@code null} if there is none.
	 */
	public final @Nullable McbsFile getMcbsFile() { return this.mcbsFile; }

	/**
	 * Sets the {@link McbsFile} this element's goal progress is to be measured against.
	 */
	public final void setMcbsFile(@Nullable McbsFile mcbsFile) { this.mcbsFile = mcbsFile; }
	// ==================================================
	protected @Override void initContentCallback()
	{
		//the goal's live data is not serialized, so attempt to re-attach it
		tryAttachMcbsFile();

		//figure out how wide this element needs to be to fit the objective text
		//alongside the icon and the progress label, and resize it accordingly
		final var objectiveText = this.goal.getObjectiveText();
		final int textX = 1 + ICON_SIZE + ICON_TEXT_GAP;
		final int width = Math.clamp(
				textX + getFont().width(objectiveText) + PROGRESS_LABEL_WIDTH + PROGRESS_LABEL_MARGIN + 4,
				MIN_WIDTH, MAX_WIDTH);
		final var bb = getBounds();
		if(bb.width != width || bb.height != HEIGHT)
			setBounds(bb.x, bb.y, width, HEIGHT);

		//the goal's icon, vertically centered in the row above the progress line
		{
			final var iconBg = new TFillColorElement.Flat(0, 0);
			iconBg.setBounds(1, 1, ICON_SIZE, HEIGHT - PROGRESS_LINE_HEIGHT - 2);
			addRel(iconBg);
			//ask the goal's GUI to initialize its icon onto the background
			final @Nullable var goalGui = McbsGoalGUI.findFor(this.goal);
			if(goalGui != null) goalGui.initIcon(this.goal, iconBg, 0);
		}

		//the objective label, on the same row as the icon
		final var objective = new TLabelElement();
		objective.wrapTextProperty().set(false, BHudGoalElement.class);
		objective.textAlignmentProperty().set(CompassDirection.WEST, BHudGoalElement.class);
		objective.textScaleProperty().set(0.9, BHudGoalElement.class);
		objective.setText(objectiveText);
		objective.setBounds(textX, 0, getBounds().width - textX - PROGRESS_LABEL_WIDTH - PROGRESS_LABEL_MARGIN, HEIGHT - PROGRESS_LINE_HEIGHT);
		addRel(objective);
		this.lblObjective = objective;

		//the progress label, right-aligned on the same row as the icon
		final var progress = new TLabelElement();
		progress.textAlignmentProperty().set(CompassDirection.EAST, BHudGoalElement.class);
		progress.textScaleProperty().set(0.85, BHudGoalElement.class);
		progress.setBounds(getBounds().width - PROGRESS_LABEL_WIDTH - PROGRESS_LABEL_MARGIN, 0, PROGRESS_LABEL_WIDTH, HEIGHT - PROGRESS_LINE_HEIGHT);
		addRel(progress);
		this.lblProgress = progress;

		//the progress line spans this element's entire bottom edge
		final int lineY = HEIGHT - PROGRESS_LINE_HEIGHT;

		//the progress line's background
		final var lineBg = new TFillColorElement.Flat(0x90202020, 0);
		lineBg.setBounds(0, lineY, getBounds().width, PROGRESS_LINE_HEIGHT);
		addRel(lineBg);

		//the progress line's fill
		final var lineFill = new TFillColorElement.Flat(0xFF33AA33, 0);
		lineFill.setBounds(0, lineY, 0, PROGRESS_LINE_HEIGHT);
		addRel(lineFill);
		this.barFill = lineFill;

		//finally, update the displayed progress for the very first time
		updateProgress();
	}
	// ==================================================
	public @Virtual @Override void renderCallback(@NotNull TGuiGraphics pencil) {
		//draw this element's own background and outline
		final var bb = getBounds();
		pencil.fillColor(bb.x, bb.y, bb.width, bb.height, TPanelElement.COLOR_BACKGROUND);
		pencil.drawOutlineIn(bb.x, bb.y, bb.width, bb.height, TPanelElement.COLOR_OUTLINE);
		//and keep the displayed progress up-to-date
		updateProgress();
	}
	// ==================================================
	/**
	 * Recalculates this element's goal progress and refreshes the GUI, but only
	 * if anything has actually changed.
	 */
	@ApiStatus.Internal
	private final void updateProgress()
	{
		//there must be data to measure progress against
		final @Nullable var file = this.mcbsFile;
		if(file == null) {
			if(this.lblProgress != null && !this.lblProgress.getText().getString().isEmpty())
				this.lblProgress.setText(literal(""));
			setBarProgress(0);
			return;
		}

		//refresh the progress label
		if(this.lblProgress != null) {
			final var text = this.goal.getProgressText(file);
			if(!text.equals(this.lblProgress.getText())) this.lblProgress.setText(text);
		}

		//refresh the progress bar
		final double progress = Math.clamp(this.goal.getProgress(file), 0d, 1d);
		setBarProgress(progress);
	}

	/**
	 * Resizes the progress line's fill to match a given progress value.
	 */
	@ApiStatus.Internal
	private final void setBarProgress(double progress)
	{
		if(this.barFill == null) return;
		final var bb = this.barFill.getBounds(); //the fill's own bounds hold its position and height
		final int width = (int) Math.round(getBounds().width * progress);
		this.barFill.setBounds(bb.x, bb.y, width, bb.height);
	}
	// --------------------------------------------------
	/**
	 * Attempts to locate the {@link McbsFile} that holds a goal identical to this
	 * element's goal, so that progress can be tracked.
	 * <p>
	 * The {@link McbsFile} reference is <b>not</b> serialized alongside pinned
	 * goals, which is why it has to be re-attached this way.
	 */
	@ApiStatus.Internal
	private final void tryAttachMcbsFile()
	{
		//nothing to do if the file is already known, or the local player tab has no data
		if(this.mcbsFile != null) return;
		final @Nullable var file = McbsEditorFileTab.LOCALPLAYER.getMcbsFile();
		if(file == null || file.getGoals().isEmpty()) return;

		//serialize this element's goal, so it can be compared against candidates
		final @Nullable String myData;
		try { myData = encodeGoal(this.goal); } catch(Exception e) { return; }
		if(myData == null) return;

		//find a goal that serializes to the exact same data, and adopt its file
		for(final var goal : file.getGoals().values())
			if(myData.equals(encodeGoal(goal))) { this.mcbsFile = file; return; }
	}

	/**
	 * Serializes a given {@link McbsGoal} into {@link String} form.
	 */
	@SuppressWarnings("unchecked")
	@ApiStatus.Internal
	private static final @Nullable String encodeGoal(@NotNull McbsGoal goal) {
		try {
			final var codec = (com.mojang.serialization.Codec<McbsGoal>) (com.mojang.serialization.Codec<?>)
					goal.getType().getCodec().codec();
			return codec.encodeStart(JsonOps.INSTANCE, goal).getOrThrow().toString();
		} catch(Exception e) { return null; }
	}
	// --------------------------------------------------
	/**
	 * Returns the {@link net.minecraft.client.gui.Font} used for measuring texts.
	 */
	@ApiStatus.Internal
	private static final @NotNull net.minecraft.client.gui.Font getFont() {
		return Minecraft.getInstance().font;
	}
	// ==================================================
	/**
	 * Returns the {@link Identifier} key of this element's goal type.
	 */
	public final @Nullable Identifier getGoalTypeKey() {
		return com.thecsdev.betterstats.api.registry.BRegistries.GOAL_TYPE.getKey(this.goal.getType());
	}
	// ==================================================
}
