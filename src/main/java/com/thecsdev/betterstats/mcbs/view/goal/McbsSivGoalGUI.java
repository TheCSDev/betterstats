package com.thecsdev.betterstats.mcbs.view.goal;

import com.thecsdev.betterstats.api.mcbs.model.goal.McbsGoalType;
import com.thecsdev.betterstats.api.mcbs.model.goal.McbsSivGoal;
import com.thecsdev.betterstats.api.mcbs.view.goal.McbsGoalGUI;
import com.thecsdev.betterstats.resource.BLanguage;
import com.thecsdev.common.math.UDim2;
import com.thecsdev.commonmc.api.client.gui.TElement;
import com.thecsdev.commonmc.api.client.gui.misc.TBlockStateElement;
import com.thecsdev.commonmc.api.client.gui.misc.TEntityElement;
import com.thecsdev.commonmc.api.client.gui.misc.TItemStackElement;
import com.thecsdev.commonmc.api.client.gui.misc.TTextureElement;
import com.thecsdev.commonmc.api.client.gui.panel.window.TWindowElement;
import com.thecsdev.commonmc.api.client.gui.render.TGuiGraphics;
import com.thecsdev.commonmc.api.client.gui.screen.ILastScreenProvider;
import com.thecsdev.commonmc.api.client.gui.screen.TScreenPlus;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

import static com.thecsdev.commonmc.resource.TComponent.gui;

/**
 * {@link McbsGoalGUI} implementation for {@link McbsSivGoal}.
 */
@ApiStatus.Internal
@Environment(EnvType.CLIENT)
public final class McbsSivGoalGUI extends McbsGoalGUI<McbsSivGoal>
{
	// ================================================== ==================================================
	//                                     McbsSivGoalGUI IMPLEMENTATION
	// ================================================== ==================================================
	/**
	 * The main instance of this {@link Class}.
	 */
	public static final McbsSivGoalGUI INSTANCE = new McbsSivGoalGUI();
	// ==================================================
	private McbsSivGoalGUI() { super(McbsGoalType.STAT_INT_VALUE); }
	// ==================================================
	public final @Override void initIcon(
			@Nullable McbsSivGoal goal, @NotNull TElement onto, int pad)
			throws NullPointerException
	{
		//use default icon if the goal is not provided
		if(goal == null) { super.initIcon(null, onto, pad); return; }

		//obtain stat-type and stat-subject
		final @Nullable var statType = BuiltInRegistries.STAT_TYPE.getValue(goal.getStatType());
		if(statType == null) { super.initIcon(goal, onto, pad); return; }
		final @Nullable var statSubj = statType.getRegistry().getValue(goal.getStatSubject());
		if(statSubj == null) { super.initIcon(goal, onto, pad); return; }

		//handle stat-subject based on type
		final var bb     = onto.getBounds();
		final var bb_ico = bb.add(pad, pad, -pad * 2, -pad * 2);
		switch (statSubj)
		{
			case Item statSubjItem -> {
				final var ico = new TItemStackElement(statSubjItem.getDefaultInstance());
				ico.setBounds(bb_ico);
				onto.add(ico);
			}
			case Block statSubjBlock -> {
				final var ico = new TBlockStateElement(statSubjBlock.defaultBlockState());
				ico.setBounds(bb_ico);
				onto.add(ico);
			}
			case EntityType<?> statSubjEntityType -> {
				final var ico = new TEntityElement(statSubjEntityType);
				ico.followsCursorProperty().set(false, McbsSivGoalGUI.class);
				ico.setBounds(bb_ico);
				onto.add(ico);
			}
			default -> {
				final var ico = new TTextureElement(Identifier.parse("toast/recipe_book"));
				ico.setBounds(bb_ico);
				onto.add(ico);
			}
		}
	}
	// --------------------------------------------------
	public final @Override boolean isEditable() { return true; }
	// --------------------------------------------------
	public final @Override @NotNull Screen createEditScreen(
			@Nullable Screen lastScreen, @NonNull McbsSivGoal goal) throws NullPointerException {
		return new EditScreen(lastScreen, goal).getAsScreen();
	}
	// ================================================== ==================================================
	//                                         EditScreen IMPLEMENTATION
	// ================================================== ==================================================
	/**
	 * Goal editing screen for {@link McbsSivGoal}.
	 */
	@ApiStatus.Internal
	private static final class EditScreen extends TScreenPlus implements ILastScreenProvider
	{
		// ==================================================
		private final @Nullable Screen      lastScreen;
		private final @NotNull  McbsSivGoal goal;
		// ==================================================
		public EditScreen(@Nullable Screen lastScreen, @NotNull McbsSivGoal goal) {
			this.lastScreen = lastScreen;
			this.goal       = Objects.requireNonNull(goal);
			titleProperty().set(
					gui("container/cartography_table/map")
							.append(" ")
							.append(BLanguage.gui_statsview_stats_mcbsGoals_editBtn())
							.append(": ")
							.append(goal.getType().getName()),
					EditScreen.class);
		}
		// ==================================================
		public final @Override @Nullable Screen getLastScreen() { return this.lastScreen; }
		// --------------------------------------------------
		@SuppressWarnings("UnstableApiUsage")
		public final @Override void renderCallback(@NotNull TGuiGraphics pencil) {
			if(this.lastScreen != null) {
				this.lastScreen.extractRenderState(pencil.getNative(), pencil.getMouseX(), pencil.getMouseY(), pencil.getDeltaTicks());
				final var bb = this.getBounds();
				pencil.fillColor(bb.x, bb.y, bb.width, bb.height, 587202559);
			}
		}
		// --------------------------------------------------
		protected final @Override void initCallback() {
			final var dialog = new EditDialog(this.goal);
			dialog.titleProperty().set(titleProperty().get(), EditScreen.class);
			add(dialog);
			dialog.setBounds(new UDim2(0.25, 0, 0.25, 0), new UDim2(0.5, 0, 0.5, 0));
		}
		// ==================================================
	}
	// ================================================== ==================================================
	//                                         EditDialog IMPLEMENTATION
	// ================================================== ==================================================
	/**
	 * {@link TWindowElement} implementation that acts as a "dialog" that allows
	 * the user to edit an {@link McbsSivGoal}.
	 */
	@ApiStatus.Internal
	private static final class EditDialog extends TWindowElement
	{
		// ==================================================
		private final @NotNull McbsSivGoal goal;
		// ==================================================
		public EditDialog(@NotNull McbsSivGoal goal) {
			this.goal = Objects.requireNonNull(goal);
			this.backgroundColorProperty().set(-13948117, EditDialog.class);
			this.closeOperationProperty().set(CloseOperation.CLOSE_SCREEN, EditDialog.class);
		}
		// ==================================================
		protected final @Override void initBodyCallback(@NotNull TElement body)
		{
			//FIXME - IMPLEMENT
		}
		// ==================================================
	}
	// ================================================== ==================================================
}
