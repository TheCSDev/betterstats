package com.thecsdev.betterstats.api.client.gui.screen;

import com.thecsdev.betterstats.api.mcbs.controller.tab.McbsEditorFileTab;
import com.thecsdev.common.util.TUtils;
import com.thecsdev.commonmc.api.client.gui.screen.TScreenWrapper;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NonNull;

import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import static com.thecsdev.betterstats.BetterStats.LOGGER;

/**
 * {@link TScreenWrapper} implementation for {@link BetterStatsScreen}.
 */
final @ApiStatus.Internal class BetterStatsScreenWrapper extends TScreenWrapper<BetterStatsScreen>
{
	// ==================================================
	BetterStatsScreenWrapper(@NonNull BetterStatsScreen target) { super(target); }
	// ==================================================
	/**
	 * Opens {@link McbsEditorFileTab}s for drag-and-dropped statistics files.
	 */
	public final @Override void onFilesDrop(@NonNull List<Path> files)
	{
		//required prerequisites
		final var editor   = getTargetTScreen().getMcbsEditor();
		final var client   = getClient();
		final var executor = TUtils.getVirtualThreadPerTaskExecutor();

		//map files to individual tasks to leverage virtual threads
		final var tasks = files.stream()
				//filter out non-statistics files, as those cannot be handled
				.filter(file -> {
					final var name = file.toString().toLowerCase(Locale.ROOT);
					return name.endsWith(".json") || name.endsWith(".nbt");
				})
				//turn each file into its own loading task
				.map(file -> CompletableFuture.supplyAsync(() -> {
					try {
						return new McbsEditorFileTab(file);
					} catch (Exception e) {
						LOGGER.error("Failed to load file: {}", file, e);
						return null;
					}
				}, executor))
				.toList();

		//wait for all tasks to finish, then batch the UI update to the main thread
		CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0]))
				.thenRun(() ->
				{
					//collect all loaded tabs into a singular list
					final var loadedTabs = tasks.stream()
							.map(CompletableFuture::join)
							.filter(Objects::nonNull)
							.toList();

					//do nothing if the outcome is empty
					if(loadedTabs.isEmpty()) return;

					//add all tabs to the editor (must be done on the main thread)
					client.execute(() -> {
						for(final var tab : loadedTabs)
							editor.addTab(tab, true);
					});
				});
	}
	// ==================================================
}
