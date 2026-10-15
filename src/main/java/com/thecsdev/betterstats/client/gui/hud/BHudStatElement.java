package com.thecsdev.betterstats.client.gui.hud;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.thecsdev.betterstats.BetterStats;
import com.thecsdev.betterstats.resource.BLanguage;
import com.thecsdev.common.properties.IntegerProperty;
import com.thecsdev.common.util.annotations.Virtual;
import com.thecsdev.common.util.enumerations.CompassDirection;
import com.thecsdev.commonmc.api.client.gui.TElement;
import com.thecsdev.commonmc.api.client.gui.ctxmenu.TContextMenu;
import com.thecsdev.commonmc.api.client.gui.label.TLabelElement;
import com.thecsdev.commonmc.api.client.gui.panel.TPanelElement;
import com.thecsdev.commonmc.api.client.gui.render.TGuiGraphics;
import com.thecsdev.commonmc.api.client.gui.screen.hud.THudElement;
import com.thecsdev.commonmc.api.client.gui.widget.stats.TBlockStatsWidget;
import com.thecsdev.commonmc.api.client.gui.widget.stats.TCustomStatWidget;
import com.thecsdev.commonmc.api.client.gui.widget.stats.TEntityStatsWidget;
import com.thecsdev.commonmc.api.client.gui.widget.stats.TItemStatsWidget;
import com.thecsdev.commonmc.api.client.stats.LocalPlayerStatsProvider;
import com.thecsdev.commonmc.api.stats.IStatsProvider;
import com.thecsdev.commonmc.api.stats.util.BlockStats;
import com.thecsdev.commonmc.api.stats.util.CustomStat;
import com.thecsdev.commonmc.api.stats.util.EntityStats;
import com.thecsdev.commonmc.api.stats.util.ItemStats;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.stats.StatType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static net.minecraft.core.registries.BuiltInRegistries.BLOCK;
import static net.minecraft.core.registries.BuiltInRegistries.CUSTOM_STAT;
import static net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE;
import static net.minecraft.core.registries.BuiltInRegistries.ITEM;
import static net.minecraft.core.registries.BuiltInRegistries.STAT_TYPE;
import static net.minecraft.network.chat.Component.literal;

/**
 * A {@link THudElement} that displays the local player's value of a single
 * statistic on the HUD.
 * <p>
 * Statistics are described using a <b>subject kind</b>, a {@link StatType}
 * {@link Identifier}, and a subject {@link Identifier}. The subject kind is what
 * tells items, blocks, and entities apart, which matters because in the game's
 * code they fundamentally are separate concepts with separate registries and
 * separate {@link StatType}s. Only {@link StatType}s belonging to the pinned
 * subject kind are ever offered when switching statistics.
 *
 * @see BHudElementTypes#STAT
 */
@Environment(EnvType.CLIENT)
public final class BHudStatElement extends THudElement
{
	// ==================================================
	/**
	 * The kind of subject a pinned statistic is about.
	 * <p>
	 * Mirrors the mod's statistics views - the "Items" view pins {@link #ITEM}
	 * statistics, the "Blocks" view pins {@link #BLOCK} statistics, and so on.
	 */
	public enum SubjectKind
	{
		/** The subject is an {@link Item}. */
		ITEM,

		/** The subject is a {@link Block}. */
		BLOCK,

		/** The subject is an {@link EntityType}. */
		ENTITY_TYPE,

		/** The subject is a "custom" (aka general) statistic. */
		CUSTOM_STAT;
		// ----------------------------------------------
		/**
		 * {@link Codec} for {@link SubjectKind}s.
		 */
		public static final Codec<SubjectKind> CODEC = Codec.STRING.flatXmap(
				name -> {
					try { return DataResult.success(valueOf(name.toUpperCase(Locale.ROOT))); }
					catch(Exception e) { return DataResult.error(() -> "Unrecognized subject kind - " + name); }
				},
				kind -> DataResult.success(kind.name().toLowerCase(Locale.ROOT)));
		// ----------------------------------------------
		/**
		 * Returns the game's {@link Registry} that holds subjects of this kind.
		 */
		public final @NotNull Registry<?> getSubjectRegistry() {
			return switch(this) {
				case ITEM         -> BuiltInRegistries.ITEM;
				case BLOCK        -> BuiltInRegistries.BLOCK;
				case ENTITY_TYPE  -> BuiltInRegistries.ENTITY_TYPE;
				case CUSTOM_STAT  -> BuiltInRegistries.CUSTOM_STAT;
			};
		}
		// ----------------------------------------------
		/**
		 * Returns the {@link SubjectKind} that corresponds to a given
		 * {@link com.thecsdev.commonmc.api.stats.util.SubjectStats} implementation.
		 * @throws IllegalArgumentException If the argument matches no known kind.
		 */
		public static final @NotNull SubjectKind of(@NotNull com.thecsdev.commonmc.api.stats.util.SubjectStats<?> stats)
				throws NullPointerException, IllegalArgumentException
		{
			Objects.requireNonNull(stats);
			if(stats instanceof ItemStats)         return ITEM;
			else if(stats instanceof BlockStats)   return BLOCK;
			else if(stats instanceof EntityStats)  return ENTITY_TYPE;
			else if(stats instanceof CustomStat)   return CUSTOM_STAT;
			else throw new IllegalArgumentException("Unsupported statistics type - " + stats.getClass());
		}
	}

	/**
	 * The height of this element, in GUI units.
	 */
	public static final int HEIGHT = 20;

	/**
	 * The size of the statistic's icon, in GUI units.
	 */
	public static final int ICON_SIZE = 18;

	/**
	 * The width reserved for the value label, in GUI units.
	 */
	public static final int VALUE_LABEL_WIDTH = 44;

	/**
	 * The minimum total width of this element, in GUI units.
	 */
	public static final int MIN_WIDTH = 110;

	/**
	 * {@link MapCodec} implementation for serializing {@link BHudStatElement}s.
	 */
	public static final MapCodec<BHudStatElement> CODEC = RecordCodecBuilder.mapCodec(instance ->
			instance.group(
					SubjectKind.CODEC.fieldOf("subject_kind").forGetter(BHudStatElement::getSubjectKind),
					Identifier.CODEC.fieldOf("stat_type").forGetter(BHudStatElement::getStatTypeID),
					Identifier.CODEC.fieldOf("stat_subject").forGetter(BHudStatElement::getStatSubjectID)
			).apply(instance, BHudStatElement::new)
	);
	// ==================================================
	private final @NotNull SubjectKind subjectKind;
	private       @NotNull Identifier  statTypeId;
	private       @NotNull Identifier  statSubjectId;
	// --------------------------------------------------
	private @Nullable LocalPlayerStatsProvider statsProvider; //transient, refreshed on demand
	private @Nullable TLabelElement            lblValue;
	// ==================================================
	/**
	 * @param subjectKind The kind of subject the statistic is about.
	 * @param statTypeId The {@link Identifier} of the {@link StatType}.
	 * @param statSubjectId The {@link Identifier} of the statistic's subject.
	 * @throws NullPointerException If an argument is {@code null}.
	 */
	public BHudStatElement(
			@NotNull SubjectKind subjectKind,
			@NotNull Identifier statTypeId,
			@NotNull Identifier statSubjectId) throws NullPointerException
	{
		this.subjectKind   = Objects.requireNonNull(subjectKind);
		this.statTypeId    = Objects.requireNonNull(statTypeId);
		this.statSubjectId = Objects.requireNonNull(statSubjectId);
	}
	// ==================================================
	/**
	 * Returns the kind of subject this element's statistic is about.
	 */
	public final @NotNull SubjectKind getSubjectKind() { return this.subjectKind; }

	/**
	 * Returns the {@link Identifier} of this element's {@link StatType}.
	 */
	public final @NotNull Identifier getStatTypeID() { return this.statTypeId; }

	/**
	 * Returns the {@link Identifier} of this element's statistic subject.
	 */
	public final @NotNull Identifier getStatSubjectID() { return this.statSubjectId; }

	/**
	 * Assigns the statistic this element is to display.
	 * @param statTypeId The {@link Identifier} of the {@link StatType}.
	 * @param statSubjectId The {@link Identifier} of the statistic's subject.
	 * @throws NullPointerException If an argument is {@code null}.
	 * @apiNote The subject kind is retained, as switching statistics never
	 *          crosses the item/block/entity/custom boundary.
	 */
	public final void setStatType(@NotNull Identifier statTypeId, @NotNull Identifier statSubjectId)
			throws NullPointerException
	{
		this.statTypeId    = Objects.requireNonNull(statTypeId);
		this.statSubjectId = Objects.requireNonNull(statSubjectId);
		clearAndInit();
	}

	/**
	 * Returns the {@link StatType} of this element's statistic, or {@code null}
	 * if it is no longer registered in the game.
	 */
	public final @Nullable StatType<?> getStatType() { return STAT_TYPE.getValue(this.statTypeId); }

	/**
	 * Returns the game's {@link Registry} that holds this element's statistic subject.
	 */
	public final @NotNull Registry<?> getSubjectRegistry() { return this.subjectKind.getSubjectRegistry(); }
	// ==================================================
	protected @Override void initContentCallback()
	{
		//make sure this element has a fitting height
		final var bb0 = getBounds();
		if(bb0.height != HEIGHT) setBounds(bb0.x, bb0.y, bb0.width, HEIGHT);

		//obtain the statistics provider for the local player
		refreshStatsProvider();

		//the statistic's subject must be resolved through its registry, so that the
		//canonical instance is obtained. registries key their values by identity,
		//which is why passing an equal-but-distinct instance would fail.
		final @Nullable var subject = getSubjectRegistry().getValue(this.statSubjectId);
		if(getStatType() == null || subject == null) {
			initUnsupportedGui();
			return;
		}

		//figure out the subject's display name, and size this element accordingly
		final var subjectName = getSubjectDisplayName(subject);
		if(this.subjectKind == SubjectKind.CUSTOM_STAT)
		{
			//"custom" statistics already render both their name and value on their own
			final var customStat = new CustomStat((Identifier) subject, getRequiredStatsProvider());
			final int width = Math.max(MIN_WIDTH,
					8 + getFont().width(customStat.getSubjectDisplayName()) + 24 +
							getFont().width(literal(customStat.getValueF())) + 8);
			resize(width);

			//initialize the widget
			final var widget = new TCustomStatWidget(customStat);
			widget.setBounds(1, 1, width - 2, HEIGHT - 2);
			makeTransparent(widget);
			addRel(widget);
			return;
		}

		//size this element so that both the name and the value fit
		resize(Math.max(MIN_WIDTH,
				ICON_SIZE + 8 + getFont().width(subjectName) + VALUE_LABEL_WIDTH + 8));

		//initialize the icon widget
		if(this.subjectKind == SubjectKind.ITEM)
		{
			final var widget = new TItemStatsWidget(new ItemStats((Item) subject, getRequiredStatsProvider()));
			widget.setBounds(1, 1, ICON_SIZE, ICON_SIZE);
			makeTransparent(widget);
			addRel(widget);
		}
		else if(this.subjectKind == SubjectKind.BLOCK)
		{
			final var widget = new TBlockStatsWidget(new BlockStats((Block) subject, getRequiredStatsProvider()));
			widget.setBounds(1, 1, ICON_SIZE, ICON_SIZE);
			makeTransparent(widget);
			addRel(widget);
		}
		else if(this.subjectKind == SubjectKind.ENTITY_TYPE)
		{
			final var widget = new TEntityStatsWidget(new EntityStats((EntityType<?>) subject, getRequiredStatsProvider()));
			widget.setBounds(1, 1, ICON_SIZE, ICON_SIZE);
			widget.followsCursorProperty().set(false, BHudStatElement.class);
			makeTransparent(widget);
			addRel(widget);
		}
		else { initUnsupportedGui(); return; }

		//initialize the subject name label
		final var nameLabel = new TLabelElement();
		nameLabel.textAlignmentProperty().set(CompassDirection.WEST, BHudStatElement.class);
		nameLabel.setText(subjectName);
		nameLabel.setBounds(ICON_SIZE + 7, 0, getBounds().width - ICON_SIZE - 7 - VALUE_LABEL_WIDTH, HEIGHT);
		addRel(nameLabel);

		//initialize the value label
		final var valueLabel = new TLabelElement();
		valueLabel.textAlignmentProperty().set(CompassDirection.EAST, BHudStatElement.class);
		valueLabel.setBounds(getBounds().width - VALUE_LABEL_WIDTH - 6, 0, VALUE_LABEL_WIDTH, HEIGHT);
		addRel(valueLabel);
		this.lblValue = valueLabel;

		//finally, update the displayed value for the very first time
		updateValue();
	}
	// ==================================================
	public @Virtual @Override void renderCallback(@NotNull TGuiGraphics pencil)
	{
		//draw this element's own background and outline
		final var bb = getBounds();
		pencil.fillColor(bb.x, bb.y, bb.width, bb.height, TPanelElement.COLOR_BACKGROUND);
		pencil.drawOutlineIn(bb.x, bb.y, bb.width, bb.height, TPanelElement.COLOR_OUTLINE);

		//and keep the displayed value up-to-date
		updateValue();
	}
	// ==================================================
	/**
	 * Builds the context menu entries for this element.
	 * <p>
	 * Offers switching which {@link StatType} the pinned statistic represents.
	 * Only {@link StatType}s that belong to this element's {@link SubjectKind}
	 * are ever offered, as items, blocks, and entities are separate concepts in
	 * the game's code.
	 */
	protected @Override void buildContextMenu(@NotNull TContextMenu.Builder builder)
	{
		//offer switching the statistic, if there is anything to switch to
		final var alternativeTypes = getAlternativeStatTypes();
		if(!alternativeTypes.isEmpty())
		{
			for(final var statType : alternativeTypes)
				builder.addButton(IStatsProvider.getStatTypeName(statType), _ -> {
					final @Nullable var id = STAT_TYPE.getKey(statType);
					if(id != null) setStatType(id, this.statSubjectId);
				});
			builder.addSeparator();
		}

		//finally, the default entries (configuration and removal)
		super.buildContextMenu(builder);
	}

	/**
	 * Returns all {@link StatType}s that may be used with this element's pinned
	 * subject, except the one that is currently in use.
	 */
	public final @NotNull List<StatType<?>> getAlternativeStatTypes()
	{
		//the subject's own registry decides which stat types are even applicable
		final var subjectReg = getSubjectRegistry();
		final @Nullable var current = getStatType();

		//collect every applicable stat type, except the current one
		final var result = new ArrayList<StatType<?>>();
		for(final var candidate : STAT_TYPE)
		{
			if(candidate == current) continue;
			if(candidate.getRegistry() != subjectReg) continue;
			result.add(candidate);
		}
		return result;
	}
	// ==================================================
	/**
	 * Reads the statistic's current value and refreshes the value label,
	 * but only if the value has actually changed.
	 */
	private final @ApiStatus.Internal void updateValue()
	{
		//the value label is absent for "custom" statistics, because the
		//custom-stat widget takes care of displaying the value on its own
		if(this.lblValue == null) return;
		final @Nullable var value = getStatValue();
		if(value == null) return;
		//optimization - only touch the label if the value has changed
		final var text = literal(value.toString());
		if(!text.equals(this.lblValue.getText())) this.lblValue.setText(text);
	}
	// --------------------------------------------------
	/**
	 * Returns the current value of this element's statistic, or {@code null}
	 * if the value cannot be read at the moment.
	 */
	public final @Nullable Integer getStatValue()
	{
		//the stat type and its subject registry must both still be valid
		final @Nullable var statType = getStatType();
		if(statType == null) return null;
		if(!statType.getRegistry().containsKey(this.statSubjectId)) return null;

		//the statistics provider must be available as well
		refreshStatsProvider();
		final @Nullable var provider = this.statsProvider;
		if(provider == null) return null;

		//read and return the value
		final var statSubject = statType.getRegistry().getValue(this.statSubjectId);
		//noinspection rawtypes,unchecked
		return (statSubject != null) ? provider.getIntValue((StatType) statType, statSubject) : 0;
	}

	/**
	 * Re-creates the {@link LocalPlayerStatsProvider} instance whenever the
	 * local player instance changes.
	 */
	@ApiStatus.Internal
	private final void refreshStatsProvider()
	{
		final @Nullable var player = Minecraft.getInstance().player;
		if(this.statsProvider != null && this.statsProvider.getPlayer() == player) return;
		this.statsProvider = (player != null) ? LocalPlayerStatsProvider.of(player) : null;
	}

	/**
	 * Returns the {@link LocalPlayerStatsProvider}, or the {@link com.thecsdev.commonmc.api.stats.EmptyStatsProvider}
	 * substitute if the local player is not available.
	 */
	@ApiStatus.Internal
	private final @NotNull IStatsProvider getRequiredStatsProvider() {
		refreshStatsProvider();
		final @Nullable var provider = this.statsProvider;
		return (provider != null) ? provider :
				com.thecsdev.commonmc.api.stats.EmptyStatsProvider.INSTANCE;
	}
	// --------------------------------------------------
	/**
	 * Initializes the GUI that is shown when this element's statistic can no
	 * longer be resolved.
	 */
	private final @ApiStatus.Internal void initUnsupportedGui()
	{
		resize(Math.max(MIN_WIDTH, getFont().width(BLanguage.gui_hud_unsupportedStat()) + 16));
		final var label = new TLabelElement();
		label.textAlignmentProperty().set(CompassDirection.CENTER, BHudStatElement.class);
		label.textColorProperty().set(0xFFFF5555, BHudStatElement.class);
		label.setText(BLanguage.gui_hud_unsupportedStat());
		label.setBounds(0, 0, getBounds().width, HEIGHT);
		addRel(label);
	}

	/**
	 * Changes this element's width, retaining its current position and height.
	 */
	private final @ApiStatus.Internal void resize(int width) {
		final var bb = getBounds();
		setBounds(bb.x, bb.y, width, bb.height);
	}

	/**
	 * Returns the {@link net.minecraft.client.gui.Font} used for measuring texts.
	 */
	@ApiStatus.Internal
	private static final @NotNull net.minecraft.client.gui.Font getFont() {
		return Minecraft.getInstance().font;
	}

	/**
	 * Makes a stats widget visually transparent, so that this element's own
	 * background and outline act as the widget's background and outline.
	 */
	@ApiStatus.Internal
	private static final void makeTransparent(@NotNull TElement widget)
	{
		switch (widget) {
			case TItemStatsWidget w -> {
				w.backgroundColorProperty()  .set(0, BHudStatElement.class);
				w.outlineColorProperty()     .set(0, BHudStatElement.class);
				w.focusOutlineColorProperty().set(0, BHudStatElement.class);
			}
			case TBlockStatsWidget w -> {
				w.backgroundColorProperty()  .set(0, BHudStatElement.class);
				w.outlineColorProperty()     .set(0, BHudStatElement.class);
				w.focusOutlineColorProperty().set(0, BHudStatElement.class);
			}
			case TEntityStatsWidget w -> {
				w.backgroundColorProperty()  .set(0, BHudStatElement.class);
				w.outlineColorProperty()     .set(0, BHudStatElement.class);
				w.focusOutlineColorProperty().set(0, BHudStatElement.class);
			}
			case TCustomStatWidget w -> {
				w.backgroundColorProperty()  .set(0, BHudStatElement.class);
				w.outlineColorProperty()     .set(0, BHudStatElement.class);
				w.focusOutlineColorProperty().set(0, BHudStatElement.class);
			}
			default -> {}
		}
	}

	/**
	 * Returns the display name of this element's statistic subject.
	 */
	//TODO - BLanguage.java also solves a similar problem on its own. Maybe
	//       TCDCommons should have a centralized method for this.
	@ApiStatus.Internal
	private final @NotNull Component getSubjectDisplayName(@NotNull Object subject)
	{
		//"custom" statistics are described by identifiers alone
		if(this.subjectKind == SubjectKind.CUSTOM_STAT)
			return IStatsProvider.getCustomStatName(this.statSubjectId);

		//everything else has a registry entry to read a display name from
		else if(subject instanceof Item it)          return it.getName(it.getDefaultInstance());
		else if(subject instanceof Block b)          return b.getName();
		else if(subject instanceof EntityType<?> et) return et.getDescription();
		else return literal(String.valueOf(this.statSubjectId));
	}
	// ==================================================
}
