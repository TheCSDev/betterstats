package com.thecsdev.betterstats.client.gui.panel;

import com.thecsdev.betterstats.api.net.BetterStatsRestAPI;
import com.thecsdev.common.util.enumerations.CompassDirection;
import com.thecsdev.commonmc.api.client.gui.label.TLabelElement;
import com.thecsdev.commonmc.api.client.gui.panel.TPanelElement;
import com.thecsdev.commonmc.api.client.gui.tooltip.TTooltip;
import com.thecsdev.commonmc.api.client.gui.widget.TButtonWidget;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.thecsdev.betterstats.api.net.BetterStatsRestAPI.fetchBuiltInCreditsAsync;
import static com.thecsdev.betterstats.mcbs.view.menubar.MenubarItemAbout.showUriScreen;

/**
 * A panel GUI element that displays "Credits" information for this mod.
 */
public final class BSCreditsPanel extends TPanelElement.Paintable
{
	// ==================================================
	private @Nullable Collection<BetterStatsRestAPI.CreditsSection> credits = null;
	// ==================================================
	public BSCreditsPanel()
	{
		//initialize properties
		scrollPaddingProperty().set(10, BSCreditsPanel.class);
		outlineColorProperty().set(0xFF000000, BSCreditsPanel.class);

		//fetch credits from the REST-ful API and built-in classpath
		BetterStatsRestAPI.fetchAsync()
				.thenCompose(BetterStatsRestAPI::fetchCreditsAsync)
				.exceptionally(e -> List.of())
				.thenCombine(fetchBuiltInCreditsAsync(), (out1, out2) -> {
					final var merged = new ArrayList<BetterStatsRestAPI.CreditsSection>(out1.size() + out2.size());
					merged.addAll(out1);
					merged.addAll(out2);
					return merged;
				})
				.thenAccept(credits -> {
					//set credits
					this.credits = credits;
					//obtain current screen and check if it is open
					final var scren = screenProperty().get();
					if(scren == null || !scren.isOpen()) return;
					//reinitialize gui only if the screen is open
					scren.getClient().execute(this::clearAndInit); //needs be done on main thread
				});
	}
	// ==================================================
	protected final @Override void initCallback()
	{
		//initialize remote credits first, if applicable
		if(this.credits != null)
			this.credits.forEach(this::initSection);
	}
	// ==================================================
	/**
	 * Initializes a credits section GUI inside this panel.
	 * @param section The credits section to initialize.
	 * @throws NullPointerException If the argument is {@code null}.
	 */
	public final void initSection(@NotNull BetterStatsRestAPI.CreditsSection section)
			throws NullPointerException
	{
		//section name label
		final var lbl_name = new TLabelElement(section.getName());
		lbl_name.textColorProperty().set(0xFFFFFF66, BSCreditsPanel.class);
		lbl_name.setBounds(computeNextYBounds(15, 5));
		add(lbl_name);

		//section summary text label
		final var lbl_summary = new TLabelElement(section.getSummary() != null ?
				section.getSummary() : Component.empty());
		lbl_summary.textAlignmentProperty().set(CompassDirection.NORTH_WEST, BSCreditsPanel.class);
		lbl_summary.wrapTextProperty().set(true, BSCreditsPanel.class);
		lbl_summary.textScaleProperty().set(0.85, BSCreditsPanel.class);
		lbl_summary.textColorProperty().set(0xFFAAAAAA, BSCreditsPanel.class);
		lbl_summary.setBounds(computeNextYBounds(0, 3));
		if(section.getSummary() != null) {
			lbl_summary.setBoundsToFitText(lbl_summary.getBounds().width);
			lbl_summary.setBounds(lbl_summary.getBounds().add(0, 0, 0, 5));
		}
		add(lbl_summary);

		//section entries
		for(final var entry : section.getEntries())
		{
			final var el_entry = new TButtonWidget.Transparent();
			el_entry.getLabel().setText(entry.getName());
			el_entry.getLabel().textScaleProperty().set(0.85, BSCreditsPanel.class);
			el_entry.getLabel().textAlignmentProperty().set(CompassDirection.WEST, BSCreditsPanel.class);
			if(entry.getSummary() != null)
				el_entry.tooltipProperty().set(__ -> TTooltip.of(entry.getSummary()), BSCreditsPanel.class);
			if(entry.getHomepageURI() != null)
				el_entry.eClicked.register(__ -> showUriScreen(entry.getHomepageURI().toString(), false));
			el_entry.setBounds(computeNextYBounds(15, 0));
			add(el_entry);
		}
	}
	// ==================================================
}
