package com.thecsdev.betterstats.client.gui.panel;

import com.thecsdev.betterstats.BetterStats;
import com.thecsdev.betterstats.api.mcbs.view.statsview.StatsView;
import com.thecsdev.betterstats.api.mcbs.view.statsview.StatsViewUtils;
import com.thecsdev.common.util.TUtils;
import com.thecsdev.common.util.annotations.CallerSensitive;
import com.thecsdev.common.util.enumerations.CompassDirection;
import com.thecsdev.commonmc.api.client.gui.label.TLabelElement;
import com.thecsdev.commonmc.api.client.gui.misc.TFillColorElement;
import com.thecsdev.commonmc.api.client.gui.panel.TPanelElement;
import com.thecsdev.commonmc.api.client.gui.widget.TButtonWidget;
import com.thecsdev.commonmc.api.stats.util.SubjectStats;
import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;

import static java.lang.Math.clamp;
import static java.lang.Math.max;
import static net.minecraft.network.chat.Component.literal;

/**
 * A simple page chooser element for paginating statistics that are displayed on
 * {@link StatsView}s.
 */
@ApiStatus.Internal
public final class StatsPageChooser extends TFillColorElement.Flat
{
	// ==================================================
	private final          int               pageCount;
	private final @NotNull StatsView.Filters filters;
	private final @NotNull Identifier        filterId;
	// --------------------------------------------------
	private final @NotNull TLabelElement label     = new TLabelElement();
	private final @NotNull TButtonWidget btn_left  = new TButtonWidget();
	private final @NotNull TButtonWidget btn_right = new TButtonWidget();
	// ==================================================
	/**
	 * An event that is fired whenever the page is changed.
	 * The {@link Consumer} parameter provides the new page value (zero-indexed).
	 */
	@Deprecated
	public final Event<Consumer<Integer>> ePageChanged = EventFactory.createLoop();
	// ==================================================
	/**
	 * @param pageCount The total number of pages available.
	 * @param filters   The {@link StatsView.Filters} where the "current page" value is stored.
	 * @param filterId  The unique ID of the "current page" filter.
	 * @throws NullPointerException     If a {@link NotNull} argument is {@code null}.
	 * @throws IllegalArgumentException If "page count" is less than {@code 1}.
	 */
	public StatsPageChooser(
			int pageCount,
			@NotNull StatsView.Filters filters,
			@NotNull Identifier filterId) throws NullPointerException, IllegalArgumentException
	{
		//initialize super
		super(TPanelElement.COLOR_BACKGROUND, 0xFF000000);

		//argument validity checks
		if(pageCount < 1) throw new IllegalArgumentException("Page count must be at least 1");

		//initialize fields
		this.pageCount = pageCount;
		this.filters   = Objects.requireNonNull(filters);
		this.filterId  = Objects.requireNonNull(filterId);

		//pre-configure elements
		this.label.textAlignmentProperty().set(CompassDirection.CENTER, StatsViewUtils.class);
		this.btn_left.getLabel().setText(literal("<"));
		this.btn_right.getLabel().setText(literal(">"));
		refreshElements();

		//button functionality
		this.btn_left.eClicked.register(__ -> setPageValue(getPageValue() - 1));
		this.btn_right.eClicked.register(__ -> setPageValue(getPageValue() + 1));
	}
	// ==================================================
	protected final @Override void initCallback() {
		final var pbb = getBounds();
		this.label.setBounds(pbb);
		add(this.label);
		this.btn_left.setBounds(pbb.x, pbb.y, 20, 20);
		add(this.btn_left);
		this.btn_right.setBounds(pbb.endX - 20, pbb.y, 20, 20);
		this.add(btn_right);
	}
	// --------------------------------------------------
	/**
	 * Refreshes the elements of this page chooser by updating the
	 * label text and button states.
	 */
	private final void refreshElements() {
		final int pageValue = getPageValue();
		this.label.setText(literal((pageValue + 1) + " / " + this.pageCount));
		this.btn_left.enabledProperty().set(pageValue > 0, StatsViewUtils.class);
		this.btn_right.enabledProperty().set(pageValue < this.pageCount - 1, StatsViewUtils.class);
	}
	// ==================================================
	/**
	 * Returns the currently selected page value (zero-indexed).
	 */
	public final int getPageValue() {
		return clamp(this.filters.getProperty(Integer.class, this.filterId, 0), 0, this.pageCount - 1);
	}

	/**
	 * Sets the currently selected page value (zero-indexed).
	 * @param page The page to set (zero-indexed).
	 */
	public final void setPageValue(int page)
	{
		//compare old and new values
		final int oldValue = getPageValue();
		final int newValue = clamp(page, 0, this.pageCount - 1);
		if(oldValue == newValue) return; //do nothing if the value is unchanged
		//set filter property value, refresh the elements, and invoke the event
		this.filters.setProperty(Integer.class, this.filterId, newValue);
		refreshElements();
		this.ePageChanged.invoker().accept(newValue);
	}
	// ==================================================
	/**
	 * Returns the page chooser "filter id" that should be used for a given {@link StatsView},
	 * assuming the provided caller {@link Class} is a {@link StatsView} that seeks to paginate
	 * its statistics.
	 * @param caller The {@link StatsView} seeking to use this filter.
	 */
	@ApiStatus.Internal
	private static final Identifier getFilterID(@NotNull Class<?> caller) {
		Objects.requireNonNull(caller);
		return Identifier.fromNamespaceAndPath(
				BetterStats.MOD_ID,
				"statspagechooser/generated/" + caller.getName()
						.toLowerCase(Locale.ENGLISH).replaceAll("[^a-z0-9]", "_"));
	}

	/**
	 * Initializes a {@link StatsPageChooser} on the provided target {@link TPanelElement}.
	 * @param target The target {@link TPanelElement} where the {@link StatsPageChooser} is to initialize.
	 * @param filters The {@link StatsView.Filters} where the "current page" value is stored.
	 * @param itemsPerPage The number of items to show per page.
	 * @param itemsTotal The total number of items to paginate.
	 * @return The initialized {@link StatsPageChooser}, or {@code null} if there are no items to paginate.
	 * @throws NullPointerException If a {@link NotNull} argument is {@code null}.
	 * @throws IllegalArgumentException If "items per page" is less than {@code 1}.
	 */
	public static final @Nullable StatsPageChooser initPanel(
			@NotNull TPanelElement target,
			@NotNull StatsView.Filters filters,
			int itemsPerPage, int itemsTotal) throws NullPointerException, IllegalArgumentException
	{
		//do not initialize if there are no items to paginate
		if(itemsTotal < 1) return null;
		//create and add the page chooser to the target panel
		@SuppressWarnings("deprecation")
		final var el = new StatsPageChooser(
				(int) max(Math.ceil((double) itemsTotal / (double) itemsPerPage), 1),
				filters,
				getFilterID(TUtils.getStackWalkerRCR().getCallerClass()));
		el.setBounds(target.computeNextYBounds(20, StatsViewUtils.GAP));
		target.add(el);
		return el;
	}

	/**
	 * Applies pagination to a {@link Collection} of {@link SubjectStats}.
	 * @param stats The full {@link Collection} of all {@link SubjectStats} to paginate.
	 * @param filters The {@link StatsView.Filters} that holds the current page value.
	 * @param itemsPerPage The number of items to show per page.
	 * @param <SS> The type of {@link SubjectStats} contained in the {@link Collection}.
	 * @return A paginated {@link Collection} of {@link SubjectStats}.
	 * @throws NullPointerException If a {@link NotNull} argument is {@code null}.
	 * @throws IllegalArgumentException If {@code itemsPerPage} is less than {@code 1}.
	 */
	@CallerSensitive
	public static final <SS extends SubjectStats<?>> Collection<SS> applyFilter(
			@NotNull Collection<SS> stats,
			@NotNull StatsView.Filters filters,
			int itemsPerPage) throws NullPointerException, IllegalArgumentException
	{
		//argument validity checks
		Objects.requireNonNull(stats);
		Objects.requireNonNull(filters);
		if(itemsPerPage < 1) throw new IllegalArgumentException("Items per page must be at least 1");

		//noinspection deprecation - get and clamp the page value
		final var fid        = getFilterID(TUtils.getStackWalkerRCR().getCallerClass());
		final int pagesTotal = (int) max(Math.ceil((double) stats.size() / (double) itemsPerPage), 1);
		int       page       = clamp(filters.getProperty(Integer.class, fid, 0), 0, pagesTotal - 1);

		//return the paginated collection
		return stats.stream().skip((long) page * (long) itemsPerPage).limit(itemsPerPage).toList();
	}
	// ==================================================
}