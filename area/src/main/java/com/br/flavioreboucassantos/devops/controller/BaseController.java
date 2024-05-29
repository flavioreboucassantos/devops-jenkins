package com.br.flavioreboucassantos.devops.controller;

import java.util.concurrent.TimeUnit;

import jakarta.ws.rs.core.Response;

/**
 * @author Flávio Rebouças Santos - flavioReboucasSantos@gmail.com
 */
public abstract class BaseController {

	protected final void sleep(final long seconds) {
		try {
			TimeUnit.SECONDS.sleep(seconds);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

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
	 * @return Response.ResponseBuilder with Response.Status.NOT_FOUND
	 */
	protected final Response.ResponseBuilder disappointedFind() {
		return Response.status(Response.Status.NOT_FOUND);
	}

	/**
	 * 
	 * @return Response.ResponseBuilder with Response.Status.NOT_MODIFIED
	 */
	protected final Response.ResponseBuilder disappointedUpdate() {
		return Response.status(Response.Status.NOT_MODIFIED);
	}

}
