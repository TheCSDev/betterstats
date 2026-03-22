package com.thecsdev.betterstats.client.gui.panel;

import com.thecsdev.betterstats.resource.dto.credits.CreditsSection;
import com.thecsdev.common.util.enumerations.CompassDirection;
import com.thecsdev.commonmc.api.client.gui.label.TLabelElement;
import com.thecsdev.commonmc.api.client.gui.panel.TPanelElement;
import com.thecsdev.commonmc.api.client.gui.tooltip.TTooltip;
import com.thecsdev.commonmc.api.client.gui.widget.TButtonWidget;
import com.thecsdev.commonmc.resource.TLanguage;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import static com.thecsdev.betterstats.mcbs.view.menubar.MenubarItemAbout.showUriScreen;

/**
 * A panel GUI element that displays "Credits" information for this mod.
 */
@Environment(EnvType.CLIENT)
public final class CreditsPanel extends TPanelElement.Paintable
{
	// ================================================== ==================================================
	//                                     BSCreditsPanel IMPLEMENTATION
	// ================================================== ==================================================
	private final @NotNull CompletableFuture<List<CreditsSection>> future;
	// ==================================================
	public CreditsPanel(@NotNull CompletableFuture<List<CreditsSection>> future) throws NullPointerException
	{
		//initialize properties
		scrollPaddingProperty().set(10, CreditsPanel.class);
		outlineColorProperty().set(0xFF000000, CreditsPanel.class);

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
		final var lbl = new TLabelElement(TLanguage.misc_loading());
		lbl.setBounds(pad, pad, bb.width - (pad * 2), bb.height - (pad * 2));
		lbl.textAlignmentProperty().set(CompassDirection.CENTER, CreditsPanel.class);
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
		final var lbl = new TLabelElement(TLanguage.misc_somethingWentWrong());
		lbl.setBounds(pad, pad, bb.width - (pad * 2), bb.height - (pad * 2));
		lbl.textAlignmentProperty().set(CompassDirection.CENTER, CreditsPanel.class);
		addRel(lbl);
	}
	// --------------------------------------------------
	/**
	 * Initializes the credits GUI inside this panel.
	 * @param credits The credits information to initialize the GUI with.
	 * @throws NullPointerException If the argument is {@code null}.
	 */
	private final void initCredits(@NotNull List<CreditsSection> credits) throws NullPointerException {
		credits.forEach(section -> initSection(this, section));
	}
	// ==================================================
	/**
	 * Initializes a credits section GUI inside this panel.
	 * @param panel The {@link TPanelElement} onto which the credits section will be initialized.
	 * @param section The credits section to initialize.
	 * @throws NullPointerException If the argument is {@code null}.
	 */
	public static final void initSection(
			@NotNull TPanelElement panel,
			@NotNull CreditsSection section) throws NullPointerException
	{
		//section name label
		final var lbl_name = new TLabelElement(section.getName());
		lbl_name.textColorProperty().set(0xFFFFFF66, CreditsPanel.class);
		lbl_name.setBounds(panel.computeNextYBounds(15, 5));
		panel.add(lbl_name);

		//section summary text label
		final var lbl_summary = new TLabelElement(section.getSummary() != null ?
				section.getSummary() : Component.empty());
		lbl_summary.textAlignmentProperty().set(CompassDirection.NORTH_WEST, CreditsPanel.class);
		lbl_summary.wrapTextProperty().set(true, CreditsPanel.class);
		lbl_summary.textScaleProperty().set(0.85, CreditsPanel.class);
		lbl_summary.textColorProperty().set(0xFFAAAAAA, CreditsPanel.class);
		lbl_summary.setBounds(panel.computeNextYBounds(0, 3));
		if(section.getSummary() != null) {
			lbl_summary.setBoundsToFitText(lbl_summary.getBounds().width);
			lbl_summary.setBounds(lbl_summary.getBounds().add(0, 0, 0, 5));
		}
		panel.add(lbl_summary);

		//section entries
		for(final var entry : section.getEntries())
		{
			final var el_entry = new TButtonWidget.Transparent();
			el_entry.getLabel().setText(entry.getName());
			el_entry.getLabel().textScaleProperty().set(0.85, CreditsPanel.class);
			el_entry.getLabel().textAlignmentProperty().set(CompassDirection.WEST, CreditsPanel.class);
			if(entry.getSummary() != null)
				el_entry.tooltipProperty().set(__ -> TTooltip.of(entry.getSummary()), CreditsPanel.class);
			if(entry.getHomepageURI() != null)
				el_entry.eClicked.register(__ -> showUriScreen(entry.getHomepageURI().toString(), false));
			el_entry.setBounds(panel.computeNextYBounds(15, 0));
			panel.add(el_entry);
			el_entry.clearAndInit(); //TODO - figure out what to do with this
		}
	}
	// ==================================================
}
