package com.thecsdev.betterstats;

import com.thecsdev.betterstats.client.BetterStatsClient;
import com.thecsdev.betterstats.command.StatisticsCommand;
import com.thecsdev.betterstats.server.BetterStatsServer;
import com.thecsdev.common.util.TUtils;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Properties;

/**
 * The main {@link Class} representing this mod.
 * This is the main "common" entry-point executed by all sides
 * (client/server) and all loaders (fabric/neoforge).
 */
public class BetterStats
{
	// ==================================================
	/**
	 * The value of this variable MUST accurately reflect the same
	 * value as 'mod.id' from 'gradle.properties'.
	 */
	public static final String MOD_ID = "betterstats";
	// ==================================================
	/**
	 * The primary {@link Logger} instance used by this mod.
	 * Intended for this mod's internal/personal use only.
	 */
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	/**
	 * Holds the properties of this mod, inherited from 'gradle.properties'.
	 * Automatically loaded during the initialization phase.
	 */
	private static final Properties PROPERTIES = new Properties();

	/**
	 * Holds the configuration of this mod.
	 * Automatically loaded during the initialization phase.
	 */
	private static final BetterStatsConfig CONFIG = new BetterStatsConfig();
	// --------------------------------------------------
	/**
	 * THE ONE and ONLY instance of this object representing this mod.
	 * Automatically assigned post-initialization.
	 */
	private static BetterStats INSTANCE;
	// --------------------------------------------------
	private final String modName;
	private final String modVersion;
	// ==================================================
	protected BetterStats()
	{
		//since sealed classes and modules are incompatible with Minecraft modding
		//environments, we use runtime instanceof checks instead
		if(!(this instanceof BetterStatsClient) && !(this instanceof BetterStatsServer))
			throw new IllegalStateException("Unexpected subclass " + getClass());

		//there can only ever be ONE instance of this object
		else if(INSTANCE != null)
			throw new IllegalStateException("Mod already initialized - " + MOD_ID);
		INSTANCE = this; //keep track of the instance

		//log instance initialization
		LOGGER.info("Initializing '" + MOD_ID + "' as '" + getClass().getSimpleName() + "'.");

		//load the mod properties
		try {
			PROPERTIES.load(BetterStats.class.getResourceAsStream("/" + MOD_ID + ".properties"));
		} catch(Exception e) {
			throw new RuntimeException("Failed to load '" + MOD_ID + ".properties'", e);
		}
		this.modName    = Objects.requireNonNull(PROPERTIES.getProperty("mod.name"));
		this.modVersion = Objects.requireNonNull(PROPERTIES.getProperty("mod.version"));

		//load the config
		TUtils.uncheckedCall(CONFIG::loadFromFile);

		//command registration
		CommandRegistrationEvent.EVENT.register((dispatcher, commandBuildContext, commandSelection) -> {
			//do not register if commands are disabled
			if(!CONFIG.canRegisterCommands()) return;
			//otherwise register commands
			StatisticsCommand.register(dispatcher, commandBuildContext);
		});
	}
	// ==================================================
	/**
	 * Returns the instance of this {@link BetterStats}.
	 */
	public static final BetterStats getInstance() { return INSTANCE; }

	/**
	 * Returns the configuration data for this mod.
	 */
	public static final BetterStatsConfig getConfig() { return CONFIG; }
	// --------------------------------------------------
	/**
	 * Returns the value of a given key from this mod's 'gradle.properties'.
	 * @param key The property key to look for.
	 * @throws IllegalStateException If this mod is not yet initialized.
	 */
	//TODO - Remove this. It is unsafe to use literal strings for static properties.
	@Deprecated(forRemoval = true)
	public static final String getProperty(@NotNull String key) throws IllegalStateException {
		if(INSTANCE == null) throw new IllegalStateException(MOD_ID + " is not initialized yet.");
		return Objects.requireNonNull(
				PROPERTIES.getProperty(key),
				"Attempt to access missing property '" + key + "' for the mod '" + MOD_ID + "'.");
	}

	/**
	 * Returns the name of this mod.
	 */
	public final String getModName() { return this.modName; }

	/**
	 * Returns the version of this mod, in {@link String} form.
	 */
	public final String getModVersion() { return this.modVersion; }
	// ==================================================
}
