package com.thecsdev.betterstats.api.mcbs.view.goal;

import com.thecsdev.betterstats.api.client.registry.BClientRegistries;
import com.thecsdev.betterstats.api.mcbs.model.goal.McbsGoal;
import com.thecsdev.betterstats.api.mcbs.model.goal.McbsGoalType;
import com.thecsdev.betterstats.resource.BLanguage;
import com.thecsdev.common.util.annotations.Virtual;
import com.thecsdev.commonmc.api.client.gui.TElement;
import com.thecsdev.commonmc.api.client.gui.misc.TTextureElement;
import com.thecsdev.commonmc.api.client.gui.screen.TTextDialogScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static com.thecsdev.betterstats.api.client.registry.BClientRegistries.GOAL_GUI;
import static java.util.Objects.requireNonNull;

/**
 * Client-sided factory for constructing GUIs for {@link McbsGoal}s.
 * @param <T> The {@link McbsGoal} type.
 */
@Environment(EnvType.CLIENT)
public abstract class McbsGoalGUI<T extends McbsGoal>
{
	// ==================================================
	private final @NotNull McbsGoalType<T> type;
	// ==================================================
	public McbsGoalGUI(@NotNull McbsGoalType<T> type) throws NullPointerException {
		this.type = requireNonNull(type);
	}
	// ==================================================
	/**
	 * Returns the {@link McbsGoalType} of the {@link McbsGoal} this
	 * {@link McbsGoalGUI} is for.
	 */
	public final @NotNull McbsGoalType<T> getType() { return this.type; }

	/**
	 * Returns the {@link Identifier} key for this {@link McbsGoalGUI} in the
	 * {@link BClientRegistries#GOAL_GUI} registry, or {@code null} if this
	 * {@link McbsGoalGUI} is not registered.
	 */
	public final @Nullable Identifier getKey() { return GOAL_GUI.getKey(this); }
	// ==================================================
	/**
	 * Creates GUI that visually acts as a goal's "icon", and then adds it
	 * to the provided {@link TElement}.
	 * @param goal The {@link McbsGoal} instance for which the icon is to be initialized.
	 * @param onto The icon's background, aka the 'slot' where the icon is to be initialized.
	 * @param preferredPadding The preferred padding to be applied around the icon.
	 * @throws NullPointerException If a {@link NotNull} argument is {@code null}.
	 */
	@Contract(pure = true)
	public @Virtual void initIcon(@Nullable T goal, @NotNull TElement onto, int preferredPadding)
			throws NullPointerException
	{
		final var ico = new TTextureElement(TextureManager.INTENTIONAL_MISSING_TEXTURE);
		ico.setBounds(onto.getBounds().add(
				preferredPadding, preferredPadding,
				-preferredPadding * 2, -preferredPadding * 2));
		onto.add(ico);
	}
	// --------------------------------------------------
	/**
	 * Returns {@code true} if and only if {@link #createEditScreen(McbsGoal, Screen)}
	 * is intended to be used.
	 * @implNote This method should return a constant value.
	 */
	@Contract(pure = true)
	public @Virtual boolean isEditable() { return false; }

	/**
	 * Creates a {@link Screen} that presents an interface allowing the user to
	 * edit properties for an {@link McbsGoal} of type {@code T}.
	 * @param goal The goal whose properties are to be edited.
	 * @param lastScreen The {@link Screen} that was open right before the returned edit screen opened.
	 * @throws NullPointerException If a {@link NotNull} argument is {@code null}.
	 * @implNote If {@link #isEditable()} returns {@code false}, avoid overriding this method.
	 */
	@Contract(pure = true)
	public @Virtual @NotNull Screen createEditScreen(@NotNull T goal, @Nullable Screen lastScreen)
			throws NullPointerException
	{
		Objects.requireNonNull(goal);
		return new TTextDialogScreen(
				lastScreen,
				Component.literal("")
						.append(BLanguage.gui_statsview_stats_mcbsGoals_noEditGui())
						.append("\n\n")
						.append("T: " + goal.getType().getClass() + "\n")            //type
						.append("G: " + goal.getClass() + "\n")                      //goal
						.append("N: ").append(goal.getType().getName()).append("\n") //name (of type)
						.append("O: ").append(goal.getObjectiveText())               //objective
		).getAsScreen();
	}
	// ==================================================
	/**
	 * Finds and returns the {@link McbsGoalGUI} instance for a given {@link McbsGoal}.
	 * @param goal The {@link McbsGoal} to find the GUI for.
	 * @param <T> The {@link McbsGoal}.
	 * @throws NullPointerException If the argument is {@code null}.
	 */
	@Contract(pure = true)
	public static final @Nullable <T extends McbsGoal> McbsGoalGUI<T> findFor(@NotNull T goal) throws NullPointerException {
		//noinspection unchecked | the goal's constructor ensures the type is correct
		return findFor((McbsGoalType<T>) requireNonNull(goal).getType());
	}

	/**
	 * Finds and returns the {@link McbsGoalGUI} instance for a given {@link McbsGoalType}.
	 * @param type The {@link McbsGoalType} to find the GUI for.
	 * @param <T> The {@link McbsGoal} type.
	 * @throws NullPointerException If the argument is {@code null}.
	 */
	@Contract(pure = true)
	public static final @Nullable <T extends McbsGoal> McbsGoalGUI<T> findFor(@NotNull McbsGoalType<T> type) throws NullPointerException
	{
		//argument must not be null
		requireNonNull(type);

		//obtain gui instance and ensure it matches
		final @Nullable var gui = GOAL_GUI.getValue(type.getKey());
		if(gui == null || gui.getType() != type) return null;

		//noinspection unchecked | already checked its class above
		return (McbsGoalGUI<T>) gui;
	}
	// ==================================================
}
