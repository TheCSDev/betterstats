package com.thecsdev.betterstats.api.mcbs.controller.tab;

import com.thecsdev.betterstats.api.mcbs.controller.McbsEditor;
import com.thecsdev.betterstats.resources.BSSLang;
import com.thecsdev.betterstats.resources.BetterStatsRestAPI;
import com.thecsdev.betterstats.resources.BetterStatsRestAPI.Credits;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

import static com.thecsdev.betterstats.resources.BetterStatsRestAPI.fetchBuiltInCreditsAsync;
import static com.thecsdev.commonmc.resources.TComponent.gui;

/**
 * The main "homepage" tab that is selected by default when an {@link McbsEditor}
 * instance is created. This is the "entrypoint" tab.
 */
public final class McbsEditorHomepageTab extends McbsEditorTab
{
	// ==================================================
	public static final McbsEditorHomepageTab INSTANCE = new McbsEditorHomepageTab();
	// --------------------------------------------------
	private @NotNull CompletableFuture<Credits> news;
	private @NotNull CompletableFuture<Credits> credits;
	// ==================================================
	private McbsEditorHomepageTab() { refresh(); }
	// ==================================================
	public final @Override int hashCode() { return System.identityHashCode(this); }
	public final @Override boolean equals(@Nullable Object obj) { return this == obj; }
	// ==================================================
	public final @Override @NotNull Component getDisplayName() {
		return gui("icon/news").append(" ").append(BSSLang.gui_menubar_view_homepage());
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
	/**
	 * Refreshes the news and credits information by (re/)fetching from the
	 * REST-ful APIs.
	 */
	public final @ApiStatus.Internal void refresh()
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
}
