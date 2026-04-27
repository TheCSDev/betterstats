package com.thecsdev.betterstats.client.gui.widget;

import com.thecsdev.betterstats.mcbs.view.statsview.StatsViewGoals;
import com.thecsdev.betterstats.resource.BSprites;
import com.thecsdev.common.properties.DoubleProperty;
import com.thecsdev.common.properties.NotNullProperty;
import com.thecsdev.commonmc.api.client.gui.misc.TFillColorElement;
import com.thecsdev.commonmc.api.client.gui.render.TGuiGraphics;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * Visual progress-bar widget commonly used by {@link StatsViewGoals}.
 */
@Environment(EnvType.CLIENT)
public final @ApiStatus.Internal class BGoalProgressBar extends TFillColorElement
{
	// ==================================================
	private final NotNullProperty<Font> font  = new NotNullProperty<>(Minecraft.getInstance().font);
	private final DoubleProperty        value = new DoubleProperty(0D);
	// ==================================================
	public BGoalProgressBar() { this(0); }
	public BGoalProgressBar(double progress)
	{
		//configure this element
		focusableProperty().set(false, BGoalProgressBar.class);
		hoverableProperty().set(false, BGoalProgressBar.class);

		//initialize properties
		this.value.addFilter(val -> Math.clamp(val, 0d, 1d), BGoalProgressBar.class);
		this.value.getHandle().set(Math.clamp(progress, 0d, 1d));
	}
	// ==================================================
	/**
	 * The {@link Font} this {@link BGoalProgressBar} uses to render text.
	 */
	public final NotNullProperty<Font> fontProperty() { return this.font; }

	/**
	 * The visual progress value of this progress-bar widget, ranging from 0 to 1.
	 */
	public final DoubleProperty valueProperty() { return this.value; }
	// --------------------------------------------------
	/**
	 * Sets the value of {@link #valueProperty()}.
	 * @param value The new value, ranging from 0 to 1.
	 */
	public final void setValue(double value) {
		this.value.set(value, BGoalProgressBar.class);
	}
	// ==================================================
	public final @Override void renderCallback(@NotNull TGuiGraphics pencil) {
		final var bb = getBounds();
		final var pw = (int) ((double) bb.width * this.value.getD());
		pencil.drawGuiSprite(BSprites.gui_editor_goal_listedGoalPBarBg(), bb.x, bb.y, bb.width, bb.height, 0xFFFFFFFF);
		pencil.fillColor(bb.x + 2, bb.y + 2, Math.max(pw - 4, 0), bb.height - 4, 0xFF00FF00);
	}
	// ==================================================
}
