package com.thecsdev.betterstats.api.mcbs.controller.tab;

import com.thecsdev.betterstats.api.mcbs.controller.McbsEditor;
import com.thecsdev.betterstats.resource.BLanguage;
import com.thecsdev.betterstats.resource.dto.BetterStatsRestAPI;
import com.thecsdev.betterstats.resource.dto.credits.CreditsSection;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static com.thecsdev.betterstats.resource.dto.BetterStatsRestAPI.fetchBuiltInCreditsAsync;
import static com.thecsdev.commonmc.resource.TComponent.gui;

/**
 * The main "homepage" tab that is selected by default when an {@link McbsEditor}
 * instance is created. This is the "entrypoint" tab.
 */
public final class McbsEditorHomepageTab extends McbsEditorTab
{
	// ==================================================
	public static final McbsEditorHomepageTab INSTANCE = new McbsEditorHomepageTab();
	// --------------------------------------------------
	private @NotNull CompletableFuture<List<CreditsSection>> credits;
	// ==================================================
	private McbsEditorHomepageTab() { refresh(); }
	// ==================================================
	public final @Override int hashCode() { return System.identityHashCode(this); }
	public final @Override boolean equals(@Nullable Object obj) { return this == obj; }
	// ==================================================
	public final @Override @NotNull Component getDisplayName() {
		return gui("icon/news").append(" ").append(BLanguage.gui_menubar_view_homepage());
	}
	// --------------------------------------------------
	/**
	 * Returns the future that will complete (or has completed) with the
	 * {@link CreditsSection}s information to be displayed on the homepage tab.
	*/
	public final @NotNull CompletableFuture<List<CreditsSection>> getCreditsAsync() { return credits; }
	// ==================================================
	/**
	 * Refreshes the news and credits information by (re/)fetching from the
	 * REST-ful APIs.
	 */
	public final @ApiStatus.Internal void refresh()
	{
		//fetch the REST-ful API
		final var api = BetterStatsRestAPI.fetchAsync();
		//fetch credits from the REST-ful API and built-in classpath
		this.credits  = api
				.thenCompose(BetterStatsRestAPI::fetchCreditsAsync)
				.exceptionally(e -> List.of())
				.thenCombine(fetchBuiltInCreditsAsync(), (list1, list2) ->
						Stream.concat(list1.stream(), list2.stream()).toList());
	}
	// ==================================================
}
