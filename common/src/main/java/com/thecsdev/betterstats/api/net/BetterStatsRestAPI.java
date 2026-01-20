package com.thecsdev.betterstats.api.net;

import com.thecsdev.betterstats.BetterStats;
import com.thecsdev.common.resource.ResourceRequest;
import com.thecsdev.common.resource.ResourceResolver;
import com.thecsdev.common.resource.protocol.HttpProtocolHandler;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * This class represents TheCSDev's REST-ful APIs specifically designed for this mod.
 * The API provides various endpoints that allow the mod to fetch and interact with
 * additional features and data beyond the standard statistics screen functionality.
 */
@ApiStatus.Experimental
@SuppressWarnings({"UnstableApiUsage", "removal"})
public final class BetterStatsRestAPI
{
	// ==================================================
	/**
	 * The default cached instance of {@link BetterStatsRestAPI}, if fetched before.
	 */
	private static @Nullable BetterStatsRestAPI DEFAULT;

	/**
	 * The "User-Agent" string to be used in HTTP requests.
	 */
	private static final @NotNull String USER_AGENT =
			"BetterStats/" + BetterStats.getProperty("mod.version") +
			" (https://github.com/TheCSDev/betterstats; https://modrinth.com/project/n6PXGAoM; " +
			"https://curseforge.com/projects/667464) TheCSDev/1.0 (https://thecsdev.com/)";
	// ==================================================
	private final @NotNull URI apiEndpoint;
	// ==================================================
	private BetterStatsRestAPI(@NotNull URI apiEndpoint) throws NullPointerException
	{
		this.apiEndpoint = Objects.requireNonNull(apiEndpoint);
	}
	// ==================================================
	/**
	 * Returns the {@link URI} of the RESTful API endpoint used to fetch this
	 * {@link BetterStatsRestAPI}.
	 */
	public final @NotNull URI getApiEndpointURI() { return this.apiEndpoint; }
	// ==================================================
	/**
	 * Fetches the REST API data from the endpoint specified in this mod's configuration.
	 * @return A {@link CompletableFuture} that will complete with the fetched
	 *         {@link BetterStatsRestAPI} instance.
	 */
	public static final CompletableFuture<BetterStatsRestAPI> fetchAsync()
	{
		//return the fetched instance if already fetched before
		final var apiEndpoint = BetterStats.getConfig().getApiEndpoint();
		if(DEFAULT != null && DEFAULT.getApiEndpointURI().equals(apiEndpoint))
			return CompletableFuture.completedFuture(DEFAULT);

		//fetch a new instance otherwise
		return fetchAsync(apiEndpoint).thenApply(api -> DEFAULT = api);
	}

	/**
	 * Fetches the REST API data from the specified endpoint.
	 * @param apiEndpoint The endpoint {@link URI} to fetch the REST API data from.
	 * @return A {@link CompletableFuture} that will complete with the fetched
	 *         {@link BetterStatsRestAPI} instance.
	 * @throws NullPointerException If the argument is {@code null}.
	 */
	public static final CompletableFuture<BetterStatsRestAPI> fetchAsync(
			@NotNull URI apiEndpoint) throws NullPointerException
	{
		//not null requirements
		Objects.requireNonNull(apiEndpoint);

		//construct the resource request
		final var rssReq = addRestHeaders(new ResourceRequest.Builder(apiEndpoint)
				.add(HttpProtocolHandler.HEADER_HTTP_METHOD, "GET"))
				.build();

		//fetch async
		return ResourceResolver.fetchAsync(rssReq).thenApply(rssRes ->
		{
			//assert response content-type
			if(!rssRes.getFirstOrThrow("content-type").contains("application/json"))
				throw new IllegalStateException("Invalid 'Content-Type' response");

			//parse and obtain json data
			/*final var json = new Gson().fromJson(new String(rssRes.getData()), JsonObject.class)
					.getAsJsonObject("betterstats").getAsJsonObject("v5")*/;

			//return this once done
			return new BetterStatsRestAPI(apiEndpoint);
		});
	}
	// --------------------------------------------------
	/**
	 * Adds necessary headers to the specified {@link ResourceRequest.Builder}.
	 * @param builder The builder to add headers to.
	 * @return The same builder instance, for chaining.
	 * @throws NullPointerException If the argument is {@code null}.
	 */
	@ApiStatus.Internal
	@Contract("_ -> param1")
	private static final @NotNull ResourceRequest.Builder addRestHeaders(
			@NotNull ResourceRequest.Builder builder) throws NullPointerException
	{
		//not null requirements
		Objects.requireNonNull(builder);

		//populate the builder with header values
		builder.add("user-agent", USER_AGENT);

		//return the builder
		return builder;
	}
	// ==================================================
}
