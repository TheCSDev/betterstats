package com.thecsdev.betterstats.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.thecsdev.betterstats.BetterStats;
import com.thecsdev.betterstats.mixin.hooks.AccessorStatsCounter;
import com.thecsdev.betterstats.resources.BSSLang;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.commands.arguments.IdentifierArgument;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatType;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.StreamSupport;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

/**
 * Logic for the {@code /statistics} (or {@code /stats}) command.
 */
public final class StatisticsCommand
{
	// ==================================================
	private StatisticsCommand() {}
	// ==================================================
	/**
	 * Registers the {@code /statistics} (or {@code /stats}) command.
	 * @param dispatcher The {@link CommandDispatcher}.
	 * @param buildContext The {@link CommandBuildContext}.
	 * @throws NullPointerException If a {@link NotNull} argument is {@code null}.
	 */
	public static void register(
			@NotNull CommandDispatcher<CommandSourceStack> dispatcher,
			@NotNull CommandBuildContext buildContext) throws NullPointerException
	{
		//define the command and its alias
		final var statistics = literal("statistics").requires(scs -> scs.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
				.then(statistics_edit(buildContext))
				.then(statistics_clear())
				.then(statistics_query(buildContext));
		final var stats = literal("stats").requires(scs -> scs.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
				.then(statistics_edit(buildContext))
				.then(statistics_clear())
				.then(statistics_query(buildContext));

		//register the command to the dispatcher
		dispatcher.register(statistics);
		dispatcher.register(stats);
	}
	// ==================================================
	/**
	 * Returns an {@link ArgumentBuilder} for the {@code edit} argument.
	 * @param cbc The {@link CommandBuildContext}.
	 * @throws NullPointerException If a {@link NotNull} argument is {@code null}.
	 */
	private static ArgumentBuilder<CommandSourceStack, ?> statistics_edit(
			@NotNull CommandBuildContext cbc) throws NullPointerException
	{
		return literal("edit")
				.then(argument("targets", EntityArgument.players())
						.then(argument("stat_type", ResourceArgument.resource(cbc, Registries.STAT_TYPE))
								.then(argument("stat", IdentifierArgument.id()).suggests(SUGGEST_STAT)
										.then(literal("set")
												.then(argument("value", IntegerArgumentType.integer(0))
														.executes(ctx -> execute_edit(ctx, true))
														)
												)
										.then(literal("increase")
												.then(argument("value", IntegerArgumentType.integer())
														.executes(ctx -> execute_edit(ctx, false))
														)
												)
										)
								)
						);
	}

	/**
	 * Returns an {@link ArgumentBuilder} for the {@code clear} argument.
	 */
	private static ArgumentBuilder<CommandSourceStack, ?> statistics_clear()
	{
		return literal("clear")
				.then(argument("targets", EntityArgument.players())
						.executes(StatisticsCommand::execute_clear));
	}

	/**
	 * Returns an {@link ArgumentBuilder} for the {@code query} argument.
	 * @param cra The {@link CommandBuildContext}.
	 * @throws NullPointerException If a {@link NotNull} argument is {@code null}.
	 */
	private static ArgumentBuilder<CommandSourceStack, ?> statistics_query(
			@NotNull CommandBuildContext cra) throws NullPointerException
	{
		return literal("query")
				.then(argument("target", EntityArgument.player())
						.then(argument("stat_type", ResourceArgument.resource(cra, Registries.STAT_TYPE))
								.then(argument("stat", IdentifierArgument.id()).suggests(SUGGEST_STAT)
										.executes(StatisticsCommand::execute_query)
										)
								)
					);
	}
	// --------------------------------------------------
	/**
	 * {@link SuggestionProvider} instance that suggests {@link Stat} registry entries.<br/>
	 * Credit: <a href="https://github.com/TheCSMods/mc-better-stats/issues/102#issuecomment-2045698948">TCDC Issue #102</a>
	 * @apiNote The context should define a {@link StatType} with the name "stat_type".
	 */
	private static final SuggestionProvider<CommandSourceStack> SUGGEST_STAT = (context, builder) ->
	{
		//try to obtain the type of stats we want to be suggesting
		@Nullable StatType<?> statType = null;
		try { statType = ResourceArgument.getResource(context, "stat_type", Registries.STAT_TYPE).value(); }
		catch(Exception ignored) {}

		//if a stat type was not provided properly or at all, use default behavior
		if(statType == null) return IdentifierArgument.id().listSuggestions(context, builder);

		//next up, after obtaining the target stat type, list the suggestions
		@Nullable Iterable<Identifier> suggestions = statType.getRegistry().registryKeySet()
				.stream().map(ResourceKey::identifier).toList();
		return SharedSuggestionProvider.suggest(
				StreamSupport.stream(suggestions.spliterator(), false).map(Objects::toString),
				builder);
	};
	// ==================================================
	/**
	 * Executes the {@code /statistics edit} command.
	 * @param context The {@link CommandContext}.
	 * @param setOrIncrease If {@code true}, sets the statistic to the given value; if {@code false}, increases it by the given value.
	 * @throws NullPointerException If a {@link NotNull} argument is {@code null}.
	 */
	@SuppressWarnings("unchecked")
	private static int execute_edit(
			@NotNull CommandContext<CommandSourceStack> context,
			boolean setOrIncrease) throws NullPointerException
	{
		try
		{
			//get parameter values
			final var arg_targets = EntityArgument.getPlayers(context, "targets");
			final var arg_stat_type = (StatType<Object>)ResourceArgument.getResource(context, "stat_type", Registries.STAT_TYPE).value();
			final var arg_stat = IdentifierArgument.getId(context, "stat");
			final int arg_value = IntegerArgumentType.getInteger(context, "value");

			final var stat_object = arg_stat_type.getRegistry().getOptional(arg_stat).orElse(null);
			Objects.requireNonNull(stat_object, "Registry entry '" + arg_stat + "' does not exist for registry '" + arg_stat_type.getRegistry() + "'.");
			final var stat = arg_stat_type.get(stat_object);

			//execute
			final AtomicInteger affected = new AtomicInteger();
			for(final var target : arg_targets)
			{
				//null check
				if(target == null) continue;

				//set stat value
				if(setOrIncrease) target.getStats().setValue(target, stat, arg_value);
				else target.getStats().increment(target, stat, arg_value);
				affected.incrementAndGet();

				//update the client
				target.getStats().sendStats(target);
			}

			//send feedback
			context.getSource().sendSuccess(() -> BSSLang.cmd_stats_edit_out(
					Component.literal("[" + BuiltInRegistries.STAT_TYPE.getKey(arg_stat_type) + " / " + arg_stat + "]"),
					affected.get()
				), false);

			//return affected count, so command blocks and data-packs can know it
			return affected.get();
		}
		catch(Exception e) { handleError(context, e); return -1; }
	}

	/**
	 * Executes the {@code /statistics clear} command.
	 * @param context The {@link CommandContext}.
	 * @throws NullPointerException If a {@link NotNull} argument is {@code null
	 */
	private static int execute_clear(
			@NotNull CommandContext<CommandSourceStack> context) throws NullPointerException
	{
		try
		{
			//get parameter values
			final var targets = EntityArgument.getPlayers(context, "targets");

			//execute
			final AtomicInteger affected = new AtomicInteger();
			for(final var target : targets)
			{
				//null check
				if(target == null) continue;

				//clear statistics
				((AccessorStatsCounter)(Object)target.getStats()).getStats().clear();
				affected.incrementAndGet();

				//disconnect the player because that's the only way to update the client
				target.connection.disconnect(Component.literal("")
						.append(BSSLang.cmd_stats_clear_kick())
						.append("\n\n[EN]: Your statistics were cleared, which requires you to disconnect and re-join."));
			}

			//send feedback
			context.getSource().sendSuccess(() -> BSSLang.cmd_stats_clear_out(affected.get()), false);

			//return affected count, so command blocks and data-packs can know it
			return affected.get();
		}
		catch(Exception e) { handleError(context, e); return -1; }
	}

	/**
	 * Executes the {@code /statistics query} command.
	 * @param context The {@link CommandContext}.
	 * @throws NullPointerException If a {@link NotNull} argument is {@code null}.
	 */
	@SuppressWarnings({"unchecked", "ConstantValue"})
	private static int execute_query(
			@NotNull CommandContext<CommandSourceStack> context) throws NullPointerException
	{
		try
		{
			//get parameter values
			final var arg_target = EntityArgument.getPlayer(context, "target");
			if(arg_target == null) throw new SimpleCommandExceptionType(Component.literal("Player not found.")).create();
			final var arg_stat_type = (StatType<Object>)ResourceArgument.getResource(context, "stat_type", Registries.STAT_TYPE).value();
			final var arg_stat = IdentifierArgument.getId(context, "stat");

			final var stat_object = arg_stat_type.getRegistry().getOptional(arg_stat).orElse(null);
			Objects.requireNonNull(stat_object, "Registry entry '" + arg_stat + "' does not exist for registry '" + arg_stat_type.getRegistry() + "'.");

			final var stat = arg_stat_type.get(stat_object);
			final int statValue = arg_target.getStats().getValue(stat);

			//execute
			context.getSource().sendSuccess(() -> BSSLang.cmd_stats_query_out(
					arg_target.getDisplayName(),
					Component.literal("[" + BuiltInRegistries.STAT_TYPE.getKey(arg_stat_type) + " / " + arg_stat + "]"),
					statValue
				), false);
			return statValue;
		}
		catch(Exception e) { handleError(context, e); return -1; }
	}
	// ==================================================
	/**
	 * Handles errors that occur during command execution.
	 * @param context The {@link CommandContext}.
	 * @param throwable The {@link Throwable} that occurred.
	 * @throws NullPointerException If a {@link NotNull} argument is {@code null}.
	 */
	public static @ApiStatus.Internal void handleError(
			@NotNull CommandContext<CommandSourceStack> context,
			@NotNull Throwable throwable) throws NullPointerException
	{
		//not-null requirements
		Objects.requireNonNull(context);
		Objects.requireNonNull(throwable);
		//handle Errors-s
		final var msg = "An unexpected error occurred trying to execute the /statistics command";
		if(throwable instanceof Error) throw new Error(msg, throwable);
		//handle command syntax errors
		context.getSource().sendFailure(Component.translatable("command.failed").append(":\n    " + throwable.getMessage()));
		BetterStats.LOGGER.error(msg, throwable);
	}
	// ==================================================
}