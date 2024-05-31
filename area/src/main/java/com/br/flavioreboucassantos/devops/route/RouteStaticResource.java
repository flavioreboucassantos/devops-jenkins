package com.br.flavioreboucassantos.devops.route;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import io.quarkus.vertx.web.Route;
import io.quarkus.vertx.web.RoutingExchange;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * @author Flávio Rebouças Santos - flavioReboucasSantos@gmail.com
 */
@ApplicationScoped
public final class RouteStaticResource {

	private final String pathResources = "META-INF/resources/";
	private final String pathFile = pathResources + "index.html";
	private final InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(pathFile);
	private final String fileContent;

//	private final String pathToExclude = "api";

	public RouteStaticResource() throws IOException {
		fileContent = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
	}

	/*
	 * Only working for inclusions: regex not negating.
	 */

//	@Route(regex = "/\\/(?:.*\\/)(?!api).*/D", methods = Route.HttpMethod.GET, produces = "text/html")
//	public final void routeExcludeEverythingExcept(final RoutingExchange ex) throws IOException {
//		System.out.println(ex.context().normalizedPath());
//		ex.ok(fileContent);
//	}

	@Route(path = "", methods = Route.HttpMethod.GET, produces = "text/html")
	public final void pathArea(final RoutingExchange ex) throws IOException {
		ex.ok(fileContent);
	}

//	@Route(path = "/vr/:n", methods = Route.HttpMethod.GET, produces = "text/html")
//	public final void pathParamVr(final RoutingExchange ex) throws IOException {
//		ex.ok(fileContent);
//	}

	@Route(path = "/vr*", methods = Route.HttpMethod.GET, produces = "text/html")
	public final void pathAnyVr(final RoutingExchange ex) throws IOException {
		ex.ok(fileContent);
	}

}