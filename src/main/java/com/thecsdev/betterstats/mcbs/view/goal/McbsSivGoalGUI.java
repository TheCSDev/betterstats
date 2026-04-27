package com.thecsdev.betterstats.mcbs.view.goal;

import com.thecsdev.betterstats.api.mcbs.model.goal.McbsGoalType;
import com.thecsdev.betterstats.api.mcbs.model.goal.McbsSivGoal;
import com.thecsdev.betterstats.api.mcbs.view.goal.McbsGoalGUI;
import com.thecsdev.commonmc.api.client.gui.TElement;
import com.thecsdev.commonmc.api.client.gui.misc.TBlockStateElement;
import com.thecsdev.commonmc.api.client.gui.misc.TEntityElement;
import com.thecsdev.commonmc.api.client.gui.misc.TItemStackElement;
import com.thecsdev.commonmc.api.client.gui.misc.TTextureElement;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * {@link McbsGoalGUI} implementation for {@link McbsSivGoal}.
 */
@ApiStatus.Internal
@Environment(EnvType.CLIENT)
public final class McbsSivGoalGUI extends McbsGoalGUI<McbsSivGoal>
{
	// ==================================================
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
	// ==================================================
}
