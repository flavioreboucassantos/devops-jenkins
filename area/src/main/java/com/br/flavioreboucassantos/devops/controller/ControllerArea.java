package com.br.flavioreboucassantos.devops.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.br.flavioreboucassantos.devops.dto.DtoArea;
import com.br.flavioreboucassantos.devops.entity.EntityArea;
import com.br.flavioreboucassantos.devops.service.ServiceArea;
import com.br.flavioreboucassantos.devops.service.TryResultPersist;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/form")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public final class ControllerArea extends ControllerBase {

	private final Logger LOG = LoggerFactory.getLogger(ControllerArea.class);

	private final ServiceArea serviceArea;

	@Inject
	public ControllerArea(final ServiceArea serviceArea) {
		this.serviceArea = serviceArea;
	}

	@POST
	public Response createArea(final DtoArea dtoArea) {

		LOG.info("--- Entrando Create Area ---");

		TryResultPersist<EntityArea> tryResultPersist;

		if ((tryResultPersist = serviceArea.tryPersist(dtoArea)).persistedEntity() == null)
			return disappointedPersist().entity("createArea-NOT_MODIFIED").build();

		return Response.status(Response.Status.CREATED).entity(tryResultPersist.persistedEntity()).build();
	}

	@DELETE
	@Path("/{id}")
	public Response removeArea(final @PathParam("id") long idArea) {

		LOG.info("--- Entrando Remove Area ---");

		if (serviceArea.tryDelete(idArea))
			return Response.ok().build();
		else
			return disappointedFind().entity("removeArea-id-NOT_FOUND").build();
	}
	
	@GET
	@Path("/{id}")
	public Response findArea(final @PathParam("id") long idArea) {

		LOG.info("--- Entrando Find Area ---");

		EntityArea entityArea;
		if ((entityArea = serviceArea.tryFindById(idArea)) == null)
			return disappointedFind().entity("findArea-id-NOT_FOUND").build();

		return Response.ok(entityArea).build();
	}

}
