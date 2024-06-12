package com.br.flavioreboucassantos.devops.route;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntUnaryOperator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.runtime.StartupEvent;
import io.quarkus.vertx.web.Route;
import io.quarkus.vertx.web.RouteFilter;
import io.quarkus.vertx.web.RoutingExchange;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.net.SocketAddress;
import io.vertx.ext.web.RoutingContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;

/**
 * @author Flávio Rebouças Santos - flavioReboucasSantos@gmail.com
 */
@ApplicationScoped
public final class RouteAndFilter {

	private final Logger LOG = LoggerFactory.getLogger(RouteAndFilter.class);

	/*
	 * Filter
	 */
	private final AtomicInteger indexHost = new AtomicInteger(0);
	private final int limitListHost = 50 * 1000;
	private final String[] listHost = new String[limitListHost];

	private final IntUnaryOperator updateFunctionIndexHost = i -> (++i >= limitListHost || i < 0) ? 0 : i;

	private final long intervalToThreadSystemLoadMS = 3 * 1000;
	private final long intervalToResetMS = 5 * 1000;
	private final Map<String, BundleOfUses> mapBundleOfUsesByOrigin4of4 = new LinkedHashMap<String, BundleOfUses>();

	/*
	 * Static Resource
	 */
	private final String pathResources = "META-INF/resources/";
	private final String pathFile = pathResources + "index.html";
	private final InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(pathFile);
	private final String fileContent;

//	System.out.println(x);
//	int counter;
	private final void addHost(final String host) {
		listHost[indexHost.getAndUpdate(updateFunctionIndexHost)] = host;
	}

	public RouteAndFilter() throws IOException {
		fileContent = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
	}

	public final void onStart(@Observes StartupEvent ev) {
//		LOG.info("onStart");
		// live reload don't destroy this thread
		new Timer()
				.schedule(new ThreadSystemLoad(indexHost, listHost, mapBundleOfUsesByOrigin4of4, intervalToResetMS), intervalToThreadSystemLoadMS,
						intervalToThreadSystemLoadMS);
	}

	@Route(path = "", methods = Route.HttpMethod.GET, produces = "text/html")
	public final void pathRoot(final RoutingExchange ex) throws IOException {
		ex.ok(fileContent);
	}

//	@Route(path = "/vr/:n", methods = Route.HttpMethod.GET, produces = "text/html")
	@Route(path = "/vr*", methods = Route.HttpMethod.GET, produces = "text/html")
	public final void pathAnyVr(final RoutingExchange ex) throws IOException {
		ex.ok(fileContent);
	}

	@RouteFilter(100)
	public final void myFilter(final RoutingContext rc) {
		final SocketAddress remoteAddress = rc.request().remoteAddress();
		final HttpServerResponse response = rc.response();

//		addHost(remoteAddress.host());		
		for (int i1of = 127; i1of < 128; i1of++) {
			for (int i2of = 0; i2of < 1; i2of++) {
				for (int i3of = 0; i3of < 1; i3of++) {
					for (int i4of = 1; i4of < 4; i4of++) {
						addHost(i1of + "." + i2of + "." + i3of + "." + i4of);
					}
				}
			}
		}

//		response.putHeader("X-Header", String.format("intercepting the request from %s:%s", remoteAddress.host(), remoteAddress.port()));
		rc.next();

//		response.setStatusCode(Response.Status.TOO_MANY_REQUESTS.getStatusCode());
//		rc.end();		
	}

}