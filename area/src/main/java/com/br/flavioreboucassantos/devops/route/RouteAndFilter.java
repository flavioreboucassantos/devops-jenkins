package com.br.flavioreboucassantos.devops.route;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import io.quarkus.vertx.web.Route;
import io.quarkus.vertx.web.RouteFilter;
import io.quarkus.vertx.web.RoutingExchange;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.ws.rs.core.Response;

/**
 * @author Flávio Rebouças Santos - flavioReboucasSantos@gmail.com
 */
@ApplicationScoped
public final class RouteAndFilter extends ThreadSystemLoad {

	static private final long intervalToResetMS = 10 * 1000;
	static private final long intervalToThreadSystemLoadMS = 1 * 1000;

	static public final int weightHeavyUse = 5; // Light Use is 1

	private final Logger LOG = LoggerFactory.getLogger(RouteAndFilter.class);

	/*
	 * Filter
	 */
	private Timer timerThreadSystemLoad;

	private final int[][] mapLimitOfUsesByOriginByLoadLevel = {
			// [0][] = Origin 1 of 4
			{
					70000, // [0][0] Load Level 0
					35 // [0][1] Load Level 1
			},
			// [1][] = Origin 2 of 4
			{
					7000, // [1][0] Load Level 0
					35 // [1][1] Load Level 1
			},
			// [2][] = Origin 3 of 4
			{
					700, // [2][0] Load Level 0
					35 // [2][1] Load Level 1
			},
			// [3][] = Origin 4 of 4
			{
					70, // [3][0] Load Level 0
					35 // [3][1] Load Level 1
			},
	};

	private int simulationMassiveStep = 0;

	/*
	 * Static Resource
	 */
	private final String pathResources = "META-INF/resources/";
	private final String pathFile = pathResources + "index.html";
	private final InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(pathFile);
	private final String fileContent;

//	System.out.println(x);	

	private final void walkForAddHost(int[] test1of, int[] test2of, int[] test3of, int[] test4of) {
		for (int i1of = test1of[0]; i1of <= test1of[0] + test1of[1]; i1of++) {
			for (int i2of = test2of[0]; i2of <= test2of[0] + test2of[1]; i2of++) {
				for (int i3of = test3of[0]; i3of <= test3of[0] + test3of[1]; i3of++) {
					for (int i4of = test4of[0]; i4of <= test4of[0] + test4of[1]; i4of++) {
						addAllowedHostHeavyUse(i1of + "." + i2of + "." + i3of + "." + i4of);
					}
				}
			}
		}
	}

	private final void walkForAddHost(int repetition, int[] test1of, int[] test2of, int[] test3of, int[] test4of) {
		for (int i = 0; i < repetition; i++)
			walkForAddHost(test1of, test2of, test3of, test4of);
	}

	private final void forAddHost(int repetition, int i1of, int i2of, int i3of, int i4of) {
		for (int i = 0; i < repetition; i++)
			addAllowedHostHeavyUse(i1of + "." + i2of + "." + i3of + "." + i4of);
	}

	private final void simulationMassiveRequest_1() {
		// LOG.info("simulationMassiveRequest_1: imply DENY from 4of4");

		final int simulationMassiveStep = this.simulationMassiveStep++;

		// LOG.info("STEP: " + simulationMassiveStep);

		switch (simulationMassiveStep) {
		case 0:
			forAddHost(7, 127, 0, 0, 1); // major (35 | 35 | 35 | 35) = imply DENY from 4of4
			break;
		default:
			forAddHost(1, 127, 0, 0, 1); // validate found: nothing allowed.
			break;
		}
	}

	private final void simulationMassiveRequest_2() {
		// LOG.info("simulationMassiveRequest_2: imply DENY from 3of4");

		final int simulationMassiveStep = this.simulationMassiveStep++;

		// LOG.info("STEP: " + simulationMassiveStep);

		switch (simulationMassiveStep) {
		case 0:
			forAddHost(6, 127, 0, 0, 1); // major (30 | 30 | 30 | 30) = imply ALLOW from 1234
			break;
		case 1:
			forAddHost(1, 127, 0, 0, 255); // major (35 | 35 | 35 | 30) = imply DENY from 3of4
			break;
		default:
			forAddHost(1, 127, 0, 0, 255); // validate found: ALLOWED FROM ORIGIN 4of4
			break;
		}
	}

	private final void simulationMassiveRequest_3() {
		// LOG.info("simulationMassiveRequest_3: imply DENY from 2of4");

		final int simulationMassiveStep = this.simulationMassiveStep++;

		// LOG.info("STEP: " + simulationMassiveStep);

		switch (simulationMassiveStep) {
		case 0:
			forAddHost(5, 127, 0, 0, 1);
			forAddHost(1, 127, 0, 0, 255); // major (30 | 30 | 30 | 25) = imply ALLOW from 1234
			break;
		case 1:
			forAddHost(1, 127, 0, 255, 255); // major (35 | 35 | 30 | 25) = imply DENY from 2of4
			break;
		default:
			forAddHost(1, 127, 0, 0, 1); // validate found: ALLOWED FROM ORIGIN 3of4
			break;
		}
	}

	private final void simulationMassiveRequest_4() {
		// LOG.info("simulationMassiveRequest_4: imply DENY from 1of4");

		final int simulationMassiveStep = this.simulationMassiveStep++;

		// LOG.info("STEP: " + simulationMassiveStep);

		switch (simulationMassiveStep) {
		case 0:
			forAddHost(4, 127, 0, 0, 1);
			forAddHost(1, 127, 0, 0, 255);
			forAddHost(1, 127, 0, 255, 255); // major (30 | 30 | 25 | 20) = imply ALLOW from 1234
			break;
		case 1:
			forAddHost(1, 127, 255, 255, 255); // major (35 | 30 | 25 | 20) = imply DENY from 1of4
			break;
		default:
			forAddHost(1, 127, 0, 0, 1); // validate found: ALLOWED FROM ORIGIN 2of4
			break;
		}
	}

	private final void filterLight(final RoutingContext rc, final int[][] mapLimitOfUsesByOriginByLoadLevel) {
		final String host = rc.request().remoteAddress().host();

		if (allowUseFromOrigins1234(host, mapLimitOfUsesByOriginByLoadLevel)) {
			addAllowedHostLightUse(host);

			rc.next();
		} else {
			setDeniedHost(host); // NOT SIMULATION

			final HttpServerResponse response = rc.response();
			response.setStatusCode(Response.Status.TOO_MANY_REQUESTS.getStatusCode());
			rc.end();
		}
	}

	private final void filterHeavy(final RoutingContext rc, final int[][] mapLimitOfUsesByOriginByLoadLevel) {
		final String host = rc.request().remoteAddress().host();

		if (allowUseFromOrigins1234(host, mapLimitOfUsesByOriginByLoadLevel)) {
//			simulationMassiveRequest_1();
//			simulationMassiveRequest_2();
//			simulationMassiveRequest_3();
//			simulationMassiveRequest_4();

			addAllowedHostHeavyUse(host);

			rc.next();
		} else {
			setDeniedHost(host); // NOT SIMULATION

			final HttpServerResponse response = rc.response();
			response.setStatusCode(Response.Status.TOO_MANY_REQUESTS.getStatusCode());
			rc.end();
		}
	}

	/*
	 * PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC
	 * PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC
	 * PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC
	 * PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC
	 * PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC
	 */

	public RouteAndFilter() throws IOException {
		super(intervalToResetMS);
		fileContent = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
	}

	public final void onStart(@Observes StartupEvent ev) {
		// (!) The Live Reload will not destroy this thread.
		timerThreadSystemLoad = new Timer();
		timerThreadSystemLoad.schedule(this, intervalToThreadSystemLoadMS, intervalToThreadSystemLoadMS);
	}

	public final void onStop(@Observes ShutdownEvent ev) {
		// (!) Destroy timerThreadSystemLoad.
		timerThreadSystemLoad.cancel();
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
//		final SocketAddress remoteAddress = rc.request().remoteAddress();
//		final HttpServerResponse response = rc.response();
//		addHost(remoteAddress.host());
//		response.putHeader("X-Header", String.format("intercepting the request from %s:%s", remoteAddress.host(), remoteAddress.port()));
//		rc.next();
//		response.setStatusCode(Response.Status.TOO_MANY_REQUESTS.getStatusCode());
//		rc.end();

		final String pathParam = rc.pathParams().get("*");
		if (pathParam.isEmpty() || !pathParam.startsWith("api")) {
			filterLight(rc, mapLimitOfUsesByOriginByLoadLevel);
		} else
			filterHeavy(rc, mapLimitOfUsesByOriginByLoadLevel);
	}

}