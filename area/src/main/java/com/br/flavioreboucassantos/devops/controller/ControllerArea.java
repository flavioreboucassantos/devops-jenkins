package com.br.flavioreboucassantos.devops.controller;

import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.br.flavioreboucassantos.devops.dto.DtoArea;
import com.br.flavioreboucassantos.devops.entity.EntityArea;
import com.br.flavioreboucassantos.devops.service.ServiceArea;
import com.br.flavioreboucassantos.devops.service.TryResultPersist;
import com.br.flavioreboucassantos.devops.service.TryResultUpdate;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api")
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

		LOG.info("--- Entrando createArea ---");

		/*
		 * tryPersist
		 */
		TryResultPersist<EntityArea> tryResultPersist;
		if ((tryResultPersist = serviceArea.tryPersist(new EntityArea(dtoArea))).persistedEntity() == null)
			return disappointedPersist().build();

		return Response.status(Response.Status.CREATED).entity(tryResultPersist.persistedEntity()).build();
	}

	@DELETE
	@Path("/{id}")
	@Transactional
	public Response removeArea(final @PathParam("id") long idArea) {

		LOG.info("--- Entrando removeArea: {} ---", idArea);

		/*
		 * deleteById
		 */
		if (serviceArea.deleteById(idArea))
			return Response.ok().build();
		else
			return disappointedFind().entity("removeArea-id_NOT_FOUND").build();
	}

	@GET
	@Path("/{id}")
	public Response findByIdArea(final @PathParam("id") long idArea) {

		LOG.info("--- Entrando findByIdArea: {} ---", idArea);

		/*
		 * findById
		 */
		EntityArea entityArea;
		if ((entityArea = serviceArea.findById(idArea)) == null)
			return disappointedFind().entity("findByIdArea-id_NOT_FOUND").build();

		return Response.ok(entityArea).build();
	}

	@PUT
	@Path("/{id}")
	public Response updateByIdArea(final @PathParam("id") long idArea, final DtoArea dtoArea) {

		LOG.info("--- Entrando updateByIdArea: {} ---", idArea);

		final Consumer<EntityArea> consumerEdit = (final EntityArea entityArea) -> {
			entityArea.rawData = dtoArea.rawData();
			entityArea.uniqueData = dtoArea.uniqueData();
			entityArea.highlighted = dtoArea.highlighted();
		};

		TryResultUpdate<EntityArea> tryResultUpdate = serviceArea.tryUpdate(idArea, consumerEdit);
		if (tryResultUpdate.notFound())
			return disappointedFind().entity("updateByIdArea-id_NOT_FOUND").build();

		if (tryResultUpdate.updatedEntity() == null)
			return disappointedUpdate().build();

		return Response.ok(tryResultUpdate.updatedEntity()).build();
	}

	@GET
	@Path("/")
	public Response findAllArea() {

		LOG.info("--- Entrando findAllArea ---");

		return Response.ok(EntityArea.findAll().list()).build();
	}

}
