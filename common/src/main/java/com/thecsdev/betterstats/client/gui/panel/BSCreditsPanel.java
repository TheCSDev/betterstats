package com.thecsdev.betterstats.client.gui.panel;

import com.thecsdev.betterstats.resources.BetterStatsRestAPI.Credits;
import com.thecsdev.betterstats.resources.BetterStatsRestAPI.CreditsSection;
import com.thecsdev.common.util.enumerations.CompassDirection;
import com.thecsdev.commonmc.api.client.gui.label.TLabelElement;
import com.thecsdev.commonmc.api.client.gui.panel.TPanelElement;
import com.thecsdev.commonmc.api.client.gui.tooltip.TTooltip;
import com.thecsdev.commonmc.api.client.gui.widget.TButtonWidget;
import com.thecsdev.commonmc.resources.TCDCLang;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import static com.thecsdev.betterstats.mcbs.view.menubar.MenubarItemAbout.showUriScreen;

/**
 * A panel GUI element that displays "Credits" information for this mod.
 */
public final class BSCreditsPanel extends TPanelElement.Paintable
{
	// ================================================== ==================================================
	//                                     BSCreditsPanel IMPLEMENTATION
	// ================================================== ==================================================
	private final @NotNull CompletableFuture<Credits> future;
	// ==================================================
	public BSCreditsPanel(@NotNull CompletableFuture<Credits> future) throws NullPointerException
	{
		//initialize properties
		scrollPaddingProperty().set(10, BSCreditsPanel.class);
		outlineColorProperty().set(0xFF000000, BSCreditsPanel.class);

		//initialize fields
		this.future = future;
	}
	// ==================================================
	protected final @Override void initCallback()
	{
		switch(this.future.state()) {
			case RUNNING:
				initLoading();
				final var client = Objects.requireNonNull(getClient(), "Missing 'client' instance");
				this.future.whenComplete((out, err) -> client.execute(this::clearAndInit));
				break;
			case SUCCESS: initCredits(this.future.resultNow()); break;
			default: initSomethingWentWrong(); break;
		}
	}
	// ==================================================
	/**
	 * Initializes an interface indicating that the credits information is
	 * currently loading.
	 */
	private final void initLoading()
	{
		//values needed for bounding box math and the label
		final var bb  = getBounds();
		final int pad = scrollPaddingProperty().getI();

		//initialize and add the label
		final var lbl = new TLabelElement(TCDCLang.misc_loading());
		lbl.setBounds(pad, pad, bb.width - (pad * 2), bb.height - (pad * 2));
		lbl.textAlignmentProperty().set(CompassDirection.CENTER, BSCreditsPanel.class);
		addRel(lbl);
	}
	// --------------------------------------------------
	/**
	 * Initializes an interface indicating that something went wrong while
	 * fetching or processing the 'credits' information.
	 */
	private final void initSomethingWentWrong()
	{
		//values needed for bounding box math and the label
		final var bb  = getBounds();
		final int pad = scrollPaddingProperty().getI();

		//initialize and add the label
		final var lbl = new TLabelElement(TCDCLang.misc_somethingWentWrong());
		lbl.setBounds(pad, pad, bb.width - (pad * 2), bb.height - (pad * 2));
		lbl.textAlignmentProperty().set(CompassDirection.CENTER, BSCreditsPanel.class);
		addRel(lbl);
	}
	// --------------------------------------------------
	/**
	 * Initializes the credits GUI inside this panel.
	 * @param credits The credits information to initialize the GUI with.
	 * @throws NullPointerException If the argument is {@code null}.
	 */
	private final void initCredits(@NotNull Credits credits) throws NullPointerException {
		credits.getSections().forEach(this::initSection);
	}
	// ==================================================
	/**
	 * Initializes a credits section GUI inside this panel.
	 * @param section The credits section to initialize.
	 * @throws NullPointerException If the argument is {@code null}.
	 */
	public final void initSection(@NotNull CreditsSection section)
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
