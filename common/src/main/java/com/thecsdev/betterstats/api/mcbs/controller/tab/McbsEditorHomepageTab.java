package com.thecsdev.betterstats.api.mcbs.controller.tab;

import com.thecsdev.betterstats.api.mcbs.controller.McbsEditor;
import com.thecsdev.betterstats.net.BetterStatsRestAPI;
import com.thecsdev.betterstats.net.BetterStatsRestAPI.Credits;
import com.thecsdev.betterstats.resources.BSSLang;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

import static com.thecsdev.betterstats.net.BetterStatsRestAPI.fetchBuiltInCreditsAsync;

/**
 * The main "homepage" tab that is selected by default when an {@link McbsEditor}
 * instance is created. This is the "entrypoint" tab.
 */
public final class McbsEditorHomepageTab extends McbsEditorTab
{
	// ==================================================
	public static final McbsEditorHomepageTab INSTANCE = new McbsEditorHomepageTab();
	// --------------------------------------------------
	private final @NotNull CompletableFuture<Credits> news;
	private final @NotNull CompletableFuture<Credits> credits;
	// ==================================================
	private McbsEditorHomepageTab()
	{
		//fetch the REST-ful API
		final var api = BetterStatsRestAPI.fetchAsync();
		//fetch news from the REST-ful API
		this.news     = api.thenCompose(BetterStatsRestAPI::fetchNewsAsync);
		//fetch credits from the REST-ful API and built-in classpath
		this.credits  = api
				.thenCompose(BetterStatsRestAPI::fetchCreditsAsync)
				.exceptionally(e -> Credits.EMPTY)
				.thenCombine(fetchBuiltInCreditsAsync(), Credits::merge);
	}
	// ==================================================
	public final @Override int hashCode() { return System.identityHashCode(this); }
	public final @Override boolean equals(@Nullable Object obj) { return this == obj; }
	// ==================================================
	public final @Override @NotNull Component getDisplayName() {
		return BSSLang.gui_menubar_view_homepage();
	}
	// --------------------------------------------------
	/**
	 * Returns the future that will complete (or has completed) with the
	 * {@link Credits} information containing news to be displayed on the
	 * homepage tab.
	*/
	public final @NotNull CompletableFuture<Credits> getNewsAsync() { return news; }

	/**
	 * Returns the future that will complete (or has completed) with the
	 * {@link Credits} information to be displayed on the homepage tab.
	*/
	public final @NotNull CompletableFuture<Credits> getCreditsAsync() { return credits; }
	// ==================================================
}
