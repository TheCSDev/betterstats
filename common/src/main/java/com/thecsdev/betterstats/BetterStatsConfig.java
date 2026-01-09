package com.thecsdev.betterstats;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.thecsdev.commonmc.api.config.ModConfig;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.net.URL;
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
	private @Expose @SerializedName("client-allowChatPsaMessages") boolean allowChatPsa     = true;
	private @Expose @SerializedName("client-guiMobsFollowCursor")  boolean mobsFollowCursor = true;
	// --------------------------------------------------
	private transient @NotNull URL apiEndpoint;
	// ==================================================
	public BetterStatsConfig()
	{
		//initialize super
		super(MOD_ID);

		//initialize fields
		try { this.apiEndpoint = URI.create(this.apiEndpointStr).toURL(); }
		catch(Exception e) { throw new AssertionError("Failed to construct URL: " + this.apiEndpointStr, e); }
	}
	// ==================================================
	/**
	 * Whether this mod's commands are to be registered.
	 */
	public final boolean canRegisterCommands() { return this.commands; }

	/**
	 * The HTTP rest API endpoint URL used by this mod.
	 */
	public final @NotNull URL getApiEndpoint() { return this.apiEndpoint; }

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
	// ==================================================
	/**
	 * Sets whether this mod's commands are to be registered.
	 */
	public final void setRegisterCommands(boolean value) { this.commands = value; }

	/**
	 * Sets the HTTP rest API endpoint {@link URL} used by this mod.
	 * @param value The new API endpoint {@link URL}.
	 * @throws NullPointerException If the argument is {@code null}.
	 */
	public final void setApiEndpoint(@NotNull URL value) throws NullPointerException {
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
	// ==================================================
	protected final @Override void onLoad(JsonObject from)
	{
		//attenot to parse the api endpoint URL instance
		//(use default value if parsing fails)
		try {
			this.apiEndpoint = URI.create(this.apiEndpointStr).toURL();
		} catch(Exception e) {
			this.apiEndpoint    = new BetterStatsConfig().apiEndpoint;
			this.apiEndpointStr = this.apiEndpoint.toString();
		}
	}
	// ==================================================
}
