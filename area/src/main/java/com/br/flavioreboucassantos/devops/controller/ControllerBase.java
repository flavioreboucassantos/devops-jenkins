package com.br.flavioreboucassantos.devops.controller;

import jakarta.ws.rs.core.Response;

public abstract class ControllerBase {

	/**
	 *
	 * @return Response.ResponseBuilder with Response.Status.BAD_REQUEST
	 */
	protected final Response.ResponseBuilder disappointedIdBadRequest() {
		return Response.status(Response.Status.BAD_REQUEST);
	}

	/**
	 *
	 * @return Response.ResponseBuilder with Response.Status.CONFLICT
	 */
	protected final Response.ResponseBuilder disappointedIdConflict() {
		return Response.status(Response.Status.CONFLICT);
	}

	/**
	 *
	 * @return Response.ResponseBuilder with Response.Status.NOT_MODIFIED
	 */
	protected final Response.ResponseBuilder disappointedPersist() {
		return Response.status(Response.Status.NOT_MODIFIED);
	}

	/**
	 *
	 * @return Response.ResponseBuilder with Response.Status.FORBIDDEN
	 */
	protected final Response.ResponseBuilder disappointedFind() {
		return Response.status(Response.Status.FORBIDDEN);
	}

	/**
	 * 
	 * @return Response.ResponseBuilder with Response.Status.NOT_MODIFIED
	 */
	protected final Response.ResponseBuilder disappointedUpdate() {
		return Response.status(Response.Status.NOT_MODIFIED);
	}

}
