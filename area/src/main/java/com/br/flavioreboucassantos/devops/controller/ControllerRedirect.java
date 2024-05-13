package com.br.flavioreboucassantos.devops.controller;

import java.net.URI;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("")
public final class ControllerRedirect {

	final Response responseStaticContent = Response.temporaryRedirect(URI.create("/")).build();

	@GET
	public Response redirect() {
		return responseStaticContent;
	}

}
