package com.thecsdev.betterstats.client.gui.widget;

import com.thecsdev.betterstats.mcbs.view.statsview.StatsViewGoals;
import com.thecsdev.betterstats.resource.BSprites;
import com.thecsdev.common.math.Bounds2i;
import com.thecsdev.common.properties.DoubleProperty;
import com.thecsdev.common.properties.NotNullProperty;
import com.thecsdev.commonmc.api.client.gui.label.TLabelElement;
import com.thecsdev.commonmc.api.client.gui.misc.TFillColorElement;
import com.thecsdev.commonmc.api.client.gui.render.TGuiGraphics;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * Visual progress-bar widget commonly used by {@link StatsViewGoals}.
 */
@Environment(EnvType.CLIENT)
public final @ApiStatus.Internal class BGoalProgressBar extends TFillColorElement
{
	// ==================================================
	private final DoubleProperty value   = new DoubleProperty(0D);
	// --------------------------------------------------
	private       Bounds2i       _lastBB = Bounds2i.ZERO;
	private final TLabelElement  _text   = new TLabelElement();
	// ==================================================
	public BGoalProgressBar() { this(0); }
	public BGoalProgressBar(double progress)
	{
		//configure this element
		focusableProperty().set(false, BGoalProgressBar.class);
		hoverableProperty().set(false, BGoalProgressBar.class);

		//initialize properties
		this.value.addFilter(val -> Math.clamp(val, 0d, 1d), BGoalProgressBar.class);
		this.value.addChangeListener((_, _, _) -> refreshText());
		this.value.getHandle().set(Math.clamp(progress, 0d, 1d));

		//configure the label functionality
		this._text.textColorProperty().set(0xFFa7cd9e, BGoalProgressBar.class);
		this._text.dropShadowProperty().set(false, BGoalProgressBar.class);
		add(this._text);
	}
	// --------------------------------------------------
	/**
	 * Refreshes the text-based progress bar's text.
	 */
	private final @ApiStatus.Internal void refreshText()
	{
		//prepare to construct next textual progress bar
		final int maxTextW  = (int) ((double) this._text.getBounds().width * this.value.getD());
		final var textScale = this._text.textScaleProperty().getD();
		final var font      = this._text.fontProperty().get();

		//begin construction
		String text = "", nextText = "";
		while ((double) font.width(nextText = text + "=") * textScale < maxTextW) {
			text = nextText;
		}
		if(!text.isEmpty()) text = text.substring(1) + ">";
		this._text.setText(Component.literal(text));
	}
	// ==================================================
	/**
	 * The {@link Font} this {@link BGoalProgressBar} uses to render text.
	 */
	public final NotNullProperty<Font> fontProperty() { return this._text.fontProperty(); }

	/**
	 * The visual progress value of this progress-bar widget, ranging from 0 to 1.
	 */
	public final DoubleProperty valueProperty() { return this.value; }
	// --------------------------------------------------
	/**
	 * Returns the value of {@link #valueProperty()}.
	 */
	public final double getValue() { return this.value.getD(); }

	/**
	 * Sets the value of {@link #valueProperty()}.
	 * @param value The new value, ranging from 0 to 1.
	 */
	public final void setValue(double value) {
		this.value.set(value, BGoalProgressBar.class);
	}
	// ==================================================
	protected final @Override void tickCallback() {
		//update the label's bounds if this element's bounds changed
		if(this._lastBB != getBounds()) {
			this._lastBB = getBounds();
			this._text.setBounds(getBounds().add(2, 2, -4, -6));
			this._text.textScaleProperty().set(
					(double) this._text.getBounds().height /
					(double) fontProperty().get().lineHeight,
					BGoalProgressBar.class);
			refreshText();
		}
	}
	// --------------------------------------------------
	public final @Override void renderCallback(@NotNull TGuiGraphics pencil) {
		final var bb = getBounds();
		pencil.drawGuiSprite(BSprites.gui_editor_goal_listedGoalPBarBg(), bb.x, bb.y, bb.width, bb.height, 0xFFFFFFFF);
		//final var pw = (int) ((double) bb.width * this.value.getD());
		//pencil.fillColor(bb.x + 2, bb.y + 2, Math.max(pw - 4, 0), bb.height - 4, 0xFF00FF00);
	}
	// ==================================================
}
