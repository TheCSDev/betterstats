package com.thecsdev.betterstats.client.gui.hud;

import com.mojang.serialization.MapCodec;
import com.thecsdev.betterstats.api.client.registry.BClientRegistries;
import com.thecsdev.betterstats.api.mcbs.model.goal.McbsGoal;
import com.thecsdev.commonmc.api.client.gui.screen.hud.THudCanvasScreen;
import com.thecsdev.commonmc.api.client.gui.screen.hud.THudElement;
import com.thecsdev.commonmc.api.client.gui.screen.hud.THudElementType;
import com.thecsdev.commonmc.api.client.registry.TClientRegistries;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import static com.thecsdev.betterstats.api.registry.BRegistries.id;
import static net.minecraft.resources.Identifier.fromNamespaceAndPath;

/**
 * The {@link THudElementType}s this mod registers in the
 * {@link TClientRegistries#HUD_ELEMENT_TYPE} registry.
 * <p>
 * Registered entries are the types of {@link THudElement}s that this mod is
 * capable of pinning onto a {@link THudCanvasScreen}.
 * Each type's {@link Identifier} ends up in the serialized data of every
 * corresponding element, which is what allows pinned elements to be read back
 * after game restarts.
 *
 * @see BClientRegistries#bootstrap()
 */
@Environment(EnvType.CLIENT)
public final class BHudElementTypes
{
	// ==================================================
	private BHudElementTypes() {}
	// ==================================================
	/**
	 * {@link THudElementType} for {@link BHudStatElement}s.
	 */
	public static final THudElementType<BHudStatElement> STAT = new THudElementType<>() {
		public final @Override @NotNull MapCodec<BHudStatElement> getCodec() { return BHudStatElement.CODEC; }
		public final @Override @NotNull BHudStatElement create() {
			return new BHudStatElement(
					BHudStatElement.SubjectKind.CUSTOM_STAT,
					fromNamespaceAndPath("minecraft", "custom"),
					fromNamespaceAndPath("minecraft", "play_time"));
		}
	};

	/**
	 * {@link THudElementType} for {@link BHudGoalElement}s.
	 */
	public static final THudElementType<BHudGoalElement> GOAL = new THudElementType<>() {
		public final @Override @NotNull MapCodec<BHudGoalElement> getCodec() { return BHudGoalElement.CODEC; }
		public final @Override @NotNull BHudGoalElement create() {
			//cannot construct a goal without knowing its type, so throw a fitting error
			throw new UnsupportedOperationException(
					"Goals must be pinned from an existing '" + McbsGoal.class.getSimpleName() + "' instance");
		}
	};
	// ==================================================
	/**
	 * Registers all of this mod's {@link THudElementType}s.
	 * @apiNote Only ever invoked by {@link BClientRegistries#bootstrap()}.
	 */
	public static final @ApiStatus.Internal void bootstrap() {
		Registry.register(TClientRegistries.HUD_ELEMENT_TYPE, id("stat"), STAT);
		Registry.register(TClientRegistries.HUD_ELEMENT_TYPE, id("goal"), GOAL);
	}
	// ==================================================
}
