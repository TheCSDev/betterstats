package com.thecsdev.betterstats;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.thecsdev.commonmc.TCDCommonsConfig;
import com.thecsdev.commonmc.api.config.ModConfig;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.net.URI;
import java.util.Objects;

import static com.thecsdev.betterstats.BetterStats.MOD_ID;

/**
 * The configuration {@link ModConfig} for the {@link BetterStats} mod.
 */
public final class BetterStatsConfig extends ModConfig
{
	// ==================================================
	private @Expose @SerializedName("common-registerCommands")     boolean commands         = true;
	private @Expose @SerializedName("common-apiEndpoint")          String  apiEndpointStr   = "https://api.thecsdev.com/";
	private @Expose @SerializedName("common-experimentalFeatures") boolean experiments      = false;
	private @Expose @SerializedName("client-allowChatPsaMessages") boolean allowChatPsa     = true;
	private @Expose @SerializedName("client-guiMobsFollowCursor")  boolean mobsFollowCursor = true;
	// --------------------------------------------------
	private transient @NotNull URI apiEndpoint;
	// ==================================================
	public BetterStatsConfig() {
		super(MOD_ID);
		this.apiEndpoint = URI.create(this.apiEndpointStr);
	}
	// ==================================================
	/**
	 * Whether this mod's commands are to be registered.
	 */
	public final boolean canRegisterCommands() { return this.commands; }

	/**
	 * The HTTP rest API endpoint {@link URI} used by this mod.
	 */
	public final @NotNull URI getApiEndpoint() { return this.apiEndpoint; }

	/**
	 * Whether entities rendered in client "mob stats" GUI should follow
	 * the cursor.
	 */
	public final boolean getGuiMobsFollowCursor() { return this.mobsFollowCursor; }

	/**
	 * Whether this mod is allowed to display public-service-announcement
	 * messages in chat.
	 */
	public final boolean allowsChatPsaMessages() { return this.allowChatPsa; }

	/**
	 * Whether experimental features are allowed to show up in-game.
	 */
	public final boolean experimentsEnabled() {
		return TCDCommonsConfig.FLAG_DEV_ENV || this.experiments;
	}
	// ==================================================
	/**
	 * Sets whether this mod's commands are to be registered.
	 */
	public final void setRegisterCommands(boolean value) { this.commands = value; }

	/**
	 * Sets the HTTP rest API endpoint {@link URI} used by this mod.
	 * @param value The new API endpoint {@link URI}.
	 * @throws NullPointerException If the argument is {@code null}.
	 */
	public final void setApiEndpoint(@NotNull URI value) throws NullPointerException {
		Objects.requireNonNull(value);
		this.apiEndpoint    = value;
		this.apiEndpointStr = value.toString();
	}

	/**
	 * Sets whether entities rendered in client "mob stats" GUI should follow
	 * the cursor.
	 */
	public final void setGuiMobsFollowCursor(boolean value) { this.mobsFollowCursor = value; }

	/**
	 * Sets whether this mod is allowed to display public-service-announcement
	 * messages in chat.
	 */
	public final void setAllowChatPsaMessages(boolean value) { this.allowChatPsa = value; }

	/**
	 * Sets whether experimental features are allowed to show up in-game.
	 */
	public final void setExperimentsEnabled(boolean value) { this.experiments = value; }
	// ==================================================
	protected final @Override void onLoad(@NonNull JsonObject from)
	{
		//attempt to parse the api endpoint URI instance
		//(use default value if parsing fails)
		try {
			this.apiEndpoint = URI.create(this.apiEndpointStr);
		} catch(RuntimeException e) {
			this.apiEndpoint    = new BetterStatsConfig().apiEndpoint;
			this.apiEndpointStr = this.apiEndpoint.toString();
		}
	}
	// ==================================================
}
