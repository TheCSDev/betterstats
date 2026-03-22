package com.thecsdev.betterstats.resource.dto;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import com.sun.jna.Platform;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;
import com.thecsdev.betterstats.BetterStats;
import com.thecsdev.betterstats.resource.dto.credits.CreditsSection;
import com.thecsdev.common.resource.ResourceRequest;
import com.thecsdev.common.resource.ResourceResolver;
import com.thecsdev.common.resource.protocol.HttpProtocolHandler;
import com.thecsdev.common.util.TUtils;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import static com.mojang.realmsclient.util.JsonUtils.getBooleanOr;
import static com.mojang.realmsclient.util.JsonUtils.getStringOr;
import static java.util.concurrent.CompletableFuture.supplyAsync;

/**
 * This class represents TheCSDev's REST-ful APIs specifically designed for this mod.
 * The API provides various endpoints that allow the mod to fetch and interact with
 * additional features and data beyond the standard statistics screen functionality.
 */
public final @ApiStatus.Internal class BetterStatsRestAPI
{
	// ================================================== ==================================================
	//                                 BetterStatsRestAPI IMPLEMENTATION
	// ================================================== ==================================================
	private final @NotNull URI endpoint_uri;
	private final @NotNull URI credits_uri;
	private final @NotNull URI news_uri;
	// ==================================================
	private BetterStatsRestAPI(
			@NotNull URI endpoint_uri,
			@NotNull URI credits_uri,
			@NotNull URI news_uri) throws NullPointerException
	{
		this.endpoint_uri = Objects.requireNonNull(endpoint_uri);
		this.credits_uri  = endpoint_uri.resolve(Objects.requireNonNull(credits_uri));
		this.news_uri     = endpoint_uri.resolve(Objects.requireNonNull(news_uri));
	}
	// ==================================================
	public final @Override int hashCode() {
		return Objects.hash(this.endpoint_uri, this.credits_uri, this.news_uri);
	}
	// --------------------------------------------------
	public final @Override boolean equals(Object obj)
	{
		if(this == obj) return true;
		if(obj == null || getClass() != obj.getClass()) return false;
		final var other = (BetterStatsRestAPI) obj;
		return this.endpoint_uri.equals(other.endpoint_uri)
				&& this.credits_uri.equals(other.credits_uri)
				&& this.news_uri.equals(other.news_uri);
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

	/**
	 * Returns the {@link URI} of the "News" endpoint in this RESTful API.
	 */
	public final @NotNull URI getNewsURI() { return this.news_uri; }
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
	 * Fetches the "News" data from this RESTful API.
	 * @return A {@link CompletableFuture} that will complete with the fetched
	 *         {@link CreditsSection} {@link CreditsSection} instances.
	 */
	public final CompletableFuture<List<CreditsSection>> fetchNewsAsync() {
		return fetchCreditsAsync(this.news_uri);
	}

	/**
	 * Fetches the built-in "Credits" data from the classpath resource.
	 * @return A {@link CompletableFuture} that will complete with the fetched
	 *         {@link CreditsSection} instances.
	 */
	public static final CompletableFuture<List<CreditsSection>> fetchBuiltInCreditsAsync() {
		return fetchCreditsAsync(URI.create("classpath:/betterstats.credits.json"));
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
			"https://curseforge.com/projects/667464) TheCSDev/1.0 (https://thecsdev.com/; " +
			"https://github.com/TheCSDev) 20260321/1.0";
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
					Objects.requireNonNull(parseUri(json.get("credits_uri")), "Missing 'credits_uri' URI"),
					Objects.requireNonNull(parseUri(json.get("news_uri")), "Missing 'news_uri' URI"));
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

		//critically necessary header values
		builder.add("user-agent", USER_AGENT);

		//telemetry data for security and rate-limit/ban enforcement
		//(refusal of service may take place if these are absent)
		//(hashed to protect user's privacy)
		if(RateLimitingInfo.getNicMacHash() instanceof String nicMacHash)
			builder.add("x-betterstats-device-mac", nicMacHash);
		if(RateLimitingInfo.getMachineGuidHash() instanceof String machineGuidHash)
			builder.add("x-betterstats-device-guid", machineGuidHash);

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
	//                                   RateLimitingInfo IMPLEMENTATION
	// ================================================== ==================================================
	/**
	 * Provides telemetry information used by the REST-ful API.
	 */
	public static final @ApiStatus.Internal class RateLimitingInfo
	{
		// ==================================================
		/**
		 * {@link CompletableFuture} computing the NIC-MAC hash on a separate thread
		 * to avoid overlading the main thread.
		 */
		private static final CompletableFuture<String> NIC_MAC_HASH = supplyAsync(() -> {
			final var mac = computeNicMacAddr();
			if(mac == null) return null;
			final var salt = "You are awesome".getBytes(StandardCharsets.UTF_8);
			return TUtils.str2pbkdf2(mac, salt, 310000); //for user privacy
		});

		/**
		 * {@link CompletableFuture} computing the 'MachineGuid' hash on a separate
		 * thread to avoid overlading the main thread.
		 */
		private static final CompletableFuture<String> MACHINE_GUID_HASH = supplyAsync(() -> {
			final var guid = computeMachineGuid();
			if(guid == null) return null;
			final var salt = "In fact, everyone is".getBytes(StandardCharsets.UTF_8);
			return TUtils.str2pbkdf2(guid, salt, 1000); //for user privacy
		});
		// ==================================================
		private RateLimitingInfo() {}
		public static final void init() {/*triggers class loading*/}
		// ==================================================
		/**
		 * Returns the network-interface-card's MAC address, or {@code null}
		 * if it could not be obtained.
		 */
		public static final @Nullable String getNicMacHash() {
			if(NIC_MAC_HASH.state() == Future.State.SUCCESS)
				return NIC_MAC_HASH.resultNow();
			return null;
		}

		/**
		 * Returns the 'MachineGuid' from the Windows Registry, or {@code null}
		 * if the OS is not 'Windows' or it could not be obtained.
		 */
		public static final @Nullable String getMachineGuidHash() {
			if(MACHINE_GUID_HASH.state() == Future.State.SUCCESS)
				return MACHINE_GUID_HASH.resultNow();
			return null;
		}
		// ==================================================
		/**
		 * Computes the network-interface-card's MAC address and returns it,
		 * or {@code null} if the attempt fails.
		 */
		private static final @Nullable String computeNicMacAddr()
		{
			try(final var socket = new DatagramSocket())
			{
				//obtain the network interface
				socket.connect(InetAddress.getByName("1.1.1.1"), 53);
				final var netInt = NetworkInterface.getByInetAddress(socket.getLocalAddress());

				//obtain the hardware address
				byte[] mac;
				if (netInt == null || (mac = netInt.getHardwareAddress()) == null)
					return null;

				//convert the hardware address to hexadecimal
				final var sj = new StringJoiner(":");
				for (final byte b : mac) sj.add(String.format("%02X", b));
				return sj.toString();
			}
			catch (Exception e) { return null; }
		}

		/**
		 * Obtains and returns the 'MachineGuid' from the Windows Registry.
		 * On other operating systems, returns {@code null}.
		 */
		private static final @Nullable String computeMachineGuid() throws RuntimeException
		{
			//'Windows Registry' is not available on non-windows devices
			if(!Platform.isWindows()) return null;
			//obtain the machine-id from the registry
			try {
				return Advapi32Util.registryGetStringValue(
						WinReg.HKEY_LOCAL_MACHINE,
						"SOFTWARE\\Microsoft\\Cryptography",
						"MachineGuid");
			} catch (Throwable exc) { return null; }
		}
		// ==================================================
	}
	// ================================================== ==================================================
}
