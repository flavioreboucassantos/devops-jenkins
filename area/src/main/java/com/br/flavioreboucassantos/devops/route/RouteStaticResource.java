package com.br.flavioreboucassantos.devops.route;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import io.quarkus.vertx.web.Route;
import io.quarkus.vertx.web.RoutingExchange;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public final class RouteStaticResource {

	private final String basePath = "src/main/resources/META-INF/resources/";
	private final String filePath = basePath + "index.html";
	private final Path path = Paths.get(filePath);
	private final String fileContent;

//	private final String pathToExclude = "api";

	public RouteStaticResource() throws IOException {
		fileContent = Files.readString(path);
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

	@Route(path = "/form", methods = Route.HttpMethod.GET, produces = "text/html")
	public final void pathForm(final RoutingExchange ex) throws IOException {
		ex.ok(fileContent);
	}

	@Route(path = "/form/:n", methods = Route.HttpMethod.GET, produces = "text/html")
	public final void pathParamForm(final RoutingExchange ex) throws IOException {
		ex.ok(fileContent);
	}

}