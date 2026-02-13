package com.thecsdev.betterstats.resources;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import com.thecsdev.betterstats.BetterStats;
import com.thecsdev.common.resource.ResourceRequest;
import com.thecsdev.common.resource.ResourceResolver;
import com.thecsdev.common.resource.protocol.HttpProtocolHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
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
	public final CompletableFuture<Credits> fetchCreditsAsync() {
		return fetchCreditsAsync(this.credits_uri);
	}

	/**
	 * Fetches the built-in "Credits" data from the classpath resource.
	 * @return A {@link CompletableFuture} that will complete with the fetched
	 *         {@link CreditsSection} instances.
	 */
	public static final CompletableFuture<Credits> fetchBuiltInCreditsAsync() {
		return fetchCreditsAsync(URI.create("classpath:/betterstats.credits.json"));
	}

	/**
	 * Fetches the "News" data from this RESTful API.
	 * @return A {@link CompletableFuture} that will complete with the fetched
	 *         {@link CreditsSection} instances.
	 */
	public final CompletableFuture<Credits> fetchNewsAsync() {
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
	public static final CompletableFuture<Credits> fetchCreditsAsync(@NotNull URI creditsUri)
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
			return Credits.fromJson(json);
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
	 * Parses a {@link Component} from the specified {@link JsonElement}.
	 * @param json The JSON element to parse the component from.
	 * @return The parsed component, or an empty component if the argument is {@code null}.
	 */
	@ApiStatus.Internal
	private static final @Nullable Component parseComponent(@Nullable JsonElement json)
	{
		try {
			if(json == null || json.isJsonNull()) return null;
			else if(json.isJsonPrimitive()) return Component.literal(json.getAsString());
			else if(json.isJsonObject()) {
				final var result = ComponentSerialization.CODEC.parse(JsonOps.INSTANCE, json.getAsJsonObject());
				return result.getOrThrow();
			}
			else throw new IllegalArgumentException("Unsupported JSON element type: " + json.getClass().getSimpleName());
		} catch(Exception e) {
			return null;
		}
	}

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
	//                                       CreditsEntry IMPLEMENTATION
	// ================================================== ==================================================
	/**
	 * Represents an entity that can be credited in a "Credits" GUI of this mod.
	 */
	public static final class CreditsEntry
	{
		// ==================================================
		private final @Nullable URI       avatar_uri;
		private final @NotNull  Component name;
		private final @Nullable Component summary;
		private final @Nullable URI       homepage_uri;
		// --------------------------------------------------
		private final int _hashCode;
		// ==================================================
		public CreditsEntry(
				@Nullable URI       avatar_uri,
				@NotNull  Component name,
				@Nullable Component summary,
				@Nullable URI       homepage_uri) throws NullPointerException
		{
			this.avatar_uri    = avatar_uri;
			this.name          = Objects.requireNonNull(name);
			this.summary       = summary;
			this.homepage_uri  = homepage_uri;
			this._hashCode     = Objects.hash(avatar_uri, name, summary, homepage_uri);
		}
		// ==================================================
		public int hashCode() { return this._hashCode; }
		public boolean equals(@Nullable Object obj) {
			if(this == obj) return true;
			if(obj == null || getClass() != obj.getClass()) return false;
			final var other = (CreditsEntry) obj;
			return Objects.equals(this.avatar_uri, other.avatar_uri)
					&& this.name.equals(other.name)
					&& Objects.equals(this.summary, other.summary)
					&& Objects.equals(this.homepage_uri, other.homepage_uri);
		}
		// ==================================================
		/**
		 * The {@link URI} of the "profile picture" that is associated with
		 * the credited entity.
		 */
		public final @Nullable URI getAvatarURI() { return this.avatar_uri; }

		/**
		 * User-friendly display name of the credited entity.
		 */
		public final @NotNull Component getName() { return this.name; }

		/**
		 * A short biography or description of the credited entity.
		 */
		public final @Nullable Component getSummary() { return this.summary; }

		/**
		 * The {@link URI} of the homepage or main website of the credited entity.
		 */
		public final @Nullable URI getHomepageURI() { return this.homepage_uri; }
		// ==================================================
		/**
		 * Creates a {@link CreditsEntry} instance from the specified JSON object.
		 * @param json The JSON object to parse the credits entry from.
		 * @return The created {@link CreditsEntry} instance.
		 * @throws NullPointerException If the argument is {@code null}.
		 */
		@Contract("_ -> new")
		public static final CreditsEntry fromJson(@NotNull JsonObject json)
				throws NullPointerException
		{
			Objects.requireNonNull(json);
			try {
				final @Nullable var avatar_uri   = parseUri(json.get("avatar_uri"));
				final @NotNull  var name         = Objects.requireNonNull(parseComponent(json.get("name")));
				final @Nullable var summary      = parseComponent(json.get("summary"));
				final @Nullable var homepage_uri = parseUri(json.get("homepage_uri"));
				return new CreditsEntry(avatar_uri, name, summary, homepage_uri);
			} catch(Exception e) {
				throw new RuntimeException("Failed to parse a 'credits entry' from JSON", e);
			}
		}
		// ==================================================
	}
	// ================================================== ==================================================
	//                                            Section IMPLEMENTATION
	// ================================================== ==================================================
	/**
	 * Represents a section in the "Credits" GUI of this mod, which
	 * contains multiple {@link CreditsEntry} instances.
	 */
	public static final class CreditsSection
	{
		// ==================================================
		private final @NotNull  Component                name;
		private final @Nullable Component                summary;
		private final @NotNull  Collection<CreditsEntry> entries;
		// ==================================================
		public CreditsSection(
				@NotNull  Component                name,
				@Nullable Component                summary,
				@NotNull  Collection<CreditsEntry> entries) throws NullPointerException
		{
			this.name    = Objects.requireNonNull(name);
			this.summary = summary;
			this.entries = Objects.requireNonNull(entries);
		}
		// ==================================================
		/**
		 * The user-friendly name of this credits section.
		 */
		public final @NotNull Component getName() { return this.name; }

		/**
		 * A brief summary or description of this credits section.
		 */
		public final @Nullable Component getSummary() { return this.summary; }

		/**
		 * A collection of {@link CreditsEntry} instances that belong to
		 * this section.
		 */
		public final @NotNull Collection<CreditsEntry> getEntries() { return this.entries; }
		// ==================================================
		/**
		 * Creates a {@link CreditsSection} instance from the specified JSON object.
		 * @param json The JSON object to parse the credits section from.
		 * @return The created {@link CreditsSection} instance.
		 * @throws NullPointerException If the argument is {@code null}.
		 */
		@Contract("_ -> new")
		public static final CreditsSection fromJson(@NotNull JsonObject json) throws NullPointerException
		{
			Objects.requireNonNull(json);
			try {
				final @NotNull  var name    = Objects.requireNonNull(parseComponent(json.get("name")));
				final @Nullable var summary = parseComponent(json.get("summary"));
				final @NotNull  var entries = json.getAsJsonArray("entries").asList().stream()
						.map(el -> CreditsEntry.fromJson(el.getAsJsonObject()))
						.toList();
				return new CreditsSection(name, summary, entries);
			} catch(Exception e) {
				throw new RuntimeException("Failed to parse a 'credits section' from JSON", e);
			}
		}
		// ==================================================
	}
	// ================================================== ==================================================
	//                                            Credits IMPLEMENTATION
	// ================================================== ==================================================
	/**
	 * Represents the complete "Credits" data structure used in the
	 * "Credits" GUI of this mod.
	 */
	public static final class Credits
	{
		// ==================================================
		/**
		 * {@link Credits} instance that has no "credits" data.
		 */
		public static final Credits EMPTY = new Credits(List.of());
		// ==================================================
		private final @NotNull Collection<CreditsSection> sections;
		// ==================================================
		public Credits(@NotNull Collection<CreditsSection> sections) throws NullPointerException {
			this.sections = Objects.requireNonNull(sections);
		}
		// ==================================================
		/**
		 * A {@link Collection} of all {@link CreditsSection} instances contained
		 * in these {@link Credits}.
		 */
		public final @NotNull Collection<CreditsSection> getSections() { return this.sections; }
		// ==================================================
		/**
		 * Creates a {@link Credits} instance from the specified JSON object.
		 * @param json The JSON object to parse the credits from.
		 * @return The created {@link Credits} instance.
		 * @throws NullPointerException If the argument is {@code null}.
		 */
		@Contract("_ -> new")
		public static final Credits fromJson(@NotNull JsonObject json) throws NullPointerException
		{
			Objects.requireNonNull(json);
			try {
				final @NotNull var sections = json.getAsJsonArray("sections").asList().stream()
						.map(el -> CreditsSection.fromJson(el.getAsJsonObject()))
						.toList();
				return new Credits(sections);
			} catch(Exception e) {
				throw new RuntimeException("Failed to parse 'credits' from JSON", e);
			}
		}
		// --------------------------------------------------
		/**
		 * Merges two {@link Credits} instances into a new one.
		 * @param arg1 The first {@link Credits} instance.
		 * @param arg2 The second {@link Credits} instance.
		 * @throws NullPointerException If an arguments is {@code null}.
		 */
		@Contract("_, _ -> new")
		public static final Credits merge(@NotNull Credits arg1, @NotNull Credits arg2)
				throws NullPointerException
		{
			//not null requirements
			Objects.requireNonNull(arg1);
			Objects.requireNonNull(arg2);

			//some optimization
			if(arg1 == EMPTY || arg1.getSections().isEmpty())      return arg2;
			else if(arg2 == EMPTY || arg2.getSections().isEmpty()) return arg1;

			//merge sections
			final var mergedSections = new ArrayList<CreditsSection>(
					arg1.getSections().size() + arg2.getSections().size());
			mergedSections.addAll(arg1.getSections());
			mergedSections.addAll(arg2.getSections());

			//return new credits instance
			return new Credits(mergedSections);
		}
		// ==================================================
	}
	// ================================================== ==================================================
}
