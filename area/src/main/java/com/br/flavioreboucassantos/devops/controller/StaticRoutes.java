package com.br.flavioreboucassantos.devops.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import io.quarkus.vertx.web.Route;
import io.quarkus.vertx.web.RoutingExchange;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public final class StaticRoutes {

	final String basePath = "src/main/resources/META-INF/resources/";
	final String filePath = basePath + "index.html";
	final Path path = Paths.get(filePath);
	final String fileContent;

	public StaticRoutes() throws IOException {
		fileContent = Files.readString(path);
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