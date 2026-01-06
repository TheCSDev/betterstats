package com.thecsdev.betterstats;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.thecsdev.commonmc.api.config.ModConfig;

import static com.thecsdev.betterstats.BetterStats.MOD_ID;

/**
 * The configuration {@link ModConfig} for the {@link BetterStats} mod.
 */
public final class BetterStatsConfig extends ModConfig
{
	// ==================================================
	private @Expose @SerializedName("common-registerCommands")     boolean commands         = true;
	private @Expose @SerializedName("client-allowChatPsaMessages") boolean allowChatPsa     = true;
	private @Expose @SerializedName("client-guiMobsFollowCursor")  boolean mobsFollowCursor = true;
	// ==================================================
	public BetterStatsConfig() { super(MOD_ID); }
	// ==================================================
	/**
	 * Whether this mod's commands are to be registered.
	 */
	public final boolean canRegisterCommands() { return this.commands; }

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
}
