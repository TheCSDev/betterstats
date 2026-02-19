package com.thecsdev.betterstats.resource.dto;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import com.thecsdev.betterstats.BetterStats;
import com.thecsdev.betterstats.resource.dto.credits.CreditsSection;
import com.thecsdev.common.resource.ResourceRequest;
import com.thecsdev.common.resource.ResourceResolver;
import com.thecsdev.common.resource.protocol.HttpProtocolHandler;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import static com.mojang.realmsclient.util.JsonUtils.getBooleanOr;
import static com.mojang.realmsclient.util.JsonUtils.getStringOr;

/**
 * This class represents TheCSDev's REST-ful APIs specifically designed for this mod.
 * The API provides various endpoints that allow the mod to fetch and interact with
 * additional features and data beyond the standard statistics screen functionality.
 */
public final @ApiStatus.Experimental class BetterStatsRestAPI
{
	// ================================================== ==================================================
	//                                 BetterStatsRestAPI IMPLEMENTATION
	// ================================================== ==================================================
	private final @NotNull URI endpoint_uri;
	private final @NotNull URI news_uri;
	private final @NotNull URI credits_uri;
	// ==================================================
	private BetterStatsRestAPI(
			@NotNull URI endpoint_uri,
			@NotNull URI news_uri,
			@NotNull URI credits_uri) throws NullPointerException
	{
		this.endpoint_uri = Objects.requireNonNull(endpoint_uri);
		this.news_uri     = endpoint_uri.resolve(Objects.requireNonNull(news_uri));
		this.credits_uri  = endpoint_uri.resolve(Objects.requireNonNull(credits_uri));
	}
	// ==================================================
	public final @Override int hashCode() {
		return Objects.hash(this.endpoint_uri, this.news_uri, this.credits_uri);
	}
	// --------------------------------------------------
	public final @Override boolean equals(Object obj)
	{
		if(this == obj) return true;
		if(obj == null || getClass() != obj.getClass()) return false;
		final var other = (BetterStatsRestAPI) obj;
		return this.endpoint_uri.equals(other.endpoint_uri)
				&& this.news_uri.equals(other.news_uri)
				&& this.credits_uri.equals(other.credits_uri);
	}
	// ==================================================
	/**
	 * Returns the {@link URI} of the RESTful API endpoint used to fetch this
	 * {@link BetterStatsRestAPI}.
	 */
	public final @NotNull URI getEndpointURI() { return this.endpoint_uri; }

	/**
	 * Returns the {@link URI} of the "Credits" endpoint in this RESTful API.
	 */
	public final @NotNull URI getCreditsURI() { return this.credits_uri; }
	// ==================================================
	/**
	 * Fetches the "Credits" data from this RESTful API.
	 * @return A {@link CompletableFuture} that will complete with the fetched
	 *         {@link CreditsSection} instances.
	 */
	public final CompletableFuture<List<CreditsSection>> fetchCreditsAsync() {
		return fetchCreditsAsync(this.credits_uri);
	}

	/**
	 * Fetches the built-in "Credits" data from the classpath resource.
	 * @return A {@link CompletableFuture} that will complete with the fetched
	 *         {@link CreditsSection} instances.
	 */
	public static final CompletableFuture<List<CreditsSection>> fetchBuiltInCreditsAsync() {
		return fetchCreditsAsync(URI.create("classpath:/betterstats.credits.json"));
	}

	/**
	 * Fetches the "News" data from this RESTful API.
	 * @return A {@link CompletableFuture} that will complete with the fetched
	 *         {@link CreditsSection} instances.
	 */
	public final CompletableFuture<List<CreditsSection>> fetchNewsAsync() {
		return fetchCreditsAsync(this.news_uri);
	}
	// --------------------------------------------------
	/**
	 * Fetches the "Credits" data from the specified {@link URI}.
	 * @param creditsUri The {@link URI} to fetch the "Credits" data from.
	 * @return A {@link CompletableFuture} that will complete with the fetched
	 *         {@link CreditsSection} instances.
	 * @throws NullPointerException If the argument is {@code null}.
	 */
	public static final CompletableFuture<List<CreditsSection>> fetchCreditsAsync(@NotNull URI creditsUri)
	{
		//construct the resource request
		final var rssReq = addRestHeaders(new ResourceRequest.Builder(creditsUri))
				.add(HttpProtocolHandler.HEADER_HTTP_METHOD, "GET")
				.build();

		//fetch async
		return ResourceResolver.fetchAsync(rssReq).thenApply(rssRes ->
		{
			//assert that the status code is 200
			if(rssRes.getStatus() != 200)
				throw new IllegalStateException(
						"Failed to fetch credits - HTTP " + rssRes.getStatus());

			//assert response content-type
			final var contentType = rssRes.getFirst("content-type", "application/json");
			if(!contentType.contains("application/json"))
				throw new IllegalStateException("Invalid 'Content-Type' response | " + contentType);

			//parse json and credits
			final var json = new Gson().fromJson(new String(rssRes.getData()), JsonObject.class);
			return CreditsSection.CODEC.listOf()
					.decode(JsonOps.INSTANCE, json.get("sections"))
					.getOrThrow().getFirst();
		});
	}
	// ================================================== ==================================================
	//                                 'Static utilities' IMPLEMENTATION
	// ================================================== ==================================================
	/**
	 * The main instance of {@link BetterStatsRestAPI}.
	 */
	private static @Nullable CompletableFuture<BetterStatsRestAPI> INSTANCE;

	/**
	 * The "User-Agent" string to be used in HTTP requests.
	 */
	@SuppressWarnings("removal")
	private static final @NotNull String USER_AGENT =
			"BetterStats/" + BetterStats.getProperty("mod.version") +
			" (https://github.com/TheCSDev/betterstats; https://modrinth.com/project/n6PXGAoM; " +
			"https://curseforge.com/projects/667464) TheCSDev/1.0 (https://thecsdev.com/)";
	// ==================================================
	/**
	 * Fetches the REST API data from the endpoint specified in this mod's configuration.
	 * @return A {@link CompletableFuture} that will complete with the fetched
	 *         {@link BetterStatsRestAPI} instance.
	 */
	public static final CompletableFuture<BetterStatsRestAPI> fetchAsync()
	{
		//obtain the currently configured api-endpoint uri
		final var apiEndpoint = BetterStats.getConfig().getApiEndpoint();
		//if there's an existing main instance whose api-endpoint is outdated, update it
		if(INSTANCE == null || (INSTANCE.state() == Future.State.SUCCESS && !INSTANCE.resultNow().getEndpointURI().equals(apiEndpoint)))
			return (INSTANCE = fetchAsync(apiEndpoint));
		//return the main instance
		return INSTANCE;
	}

	/**
	 * Fetches the REST API data from the specified endpoint.
	 * @param apiEndpoint The endpoint {@link URI} to fetch the REST API data from.
	 * @return A {@link CompletableFuture} that will complete with the fetched
	 *         {@link BetterStatsRestAPI} instance.
	 * @throws NullPointerException If the argument is {@code null}.
	 */
	@ApiStatus.Internal
	public static final CompletableFuture<BetterStatsRestAPI> fetchAsync(
			@NotNull URI apiEndpoint) throws NullPointerException
	{
		//not null requirements
		Objects.requireNonNull(apiEndpoint);

		//construct the resource request
		final var rssReq = addRestHeaders(new ResourceRequest.Builder(apiEndpoint))
				.add(HttpProtocolHandler.HEADER_HTTP_METHOD, "GET")
				.build();

		//fetch async
		return ResourceResolver.fetchAsync(rssReq).thenApply(rssRes ->
		{
			//assert that the status code is 200
			if(rssRes.getStatus() != 200)
				throw new IllegalStateException(
						"Failed to fetch API - Status code " + rssRes.getStatus());

			//assert response content-type
			final var contentType = rssRes.getFirstOrThrow("content-type");
			if(!contentType.contains("application/json"))
				throw new IllegalStateException("Invalid 'Content-Type' response | " + contentType);

			//parse json and obtain the api object
			final var json = new Gson().fromJson(new String(rssRes.getData()), JsonObject.class)
					.getAsJsonObject("betterstats").getAsJsonObject("v5");

			//handle end-of-life
			if(getBooleanOr("eol", json, true)) {
				final var msg = getStringOr("eol_message", json, "The REST-ful API is unavailable at this time.");
				throw new UnsupportedOperationException(msg);
			}

			//return this once done
			return new BetterStatsRestAPI(
					apiEndpoint,
					Objects.requireNonNull(parseUri(json.get("news_uri")), "Missing 'news_uri' URI"),
					Objects.requireNonNull(parseUri(json.get("credits_uri")), "Missing 'credits_uri' URI")
			);
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
	/**
	 * Parses a {@link URI} from the specified {@link JsonElement}.
	 * @param json The JSON element to parse the {@link URI} from.
	 * @return The parsed {@link URI}, or {@code null} if the argument is {@code null}.
	 */
	@ApiStatus.Internal
	private static final @Nullable URI parseUri(@Nullable JsonElement json) {
		if(json == null || json.isJsonNull()) return null;
		try { return URI.create(json.getAsString()); } catch(Exception e) { return null; }
	}
	// ================================================== ==================================================
}
