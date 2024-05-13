package com.br.flavioreboucassantos.devops.service;

import com.br.flavioreboucassantos.devops.dto.DtoArea;
import com.br.flavioreboucassantos.devops.entity.EntityArea;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;

@ApplicationScoped
public final class ServiceArea extends ServiceBase {

	@Transactional
	public TryResultPersist<EntityArea> tryPersist(final DtoArea dtoArea) {
		try {

			final EntityArea entityArea = new EntityArea(dtoArea);
			entityArea.persist();
			return new TryResultPersist<EntityArea>(null, entityArea);

		} catch (final PersistenceException e) {

			return new TryResultPersist<EntityArea>(e);
		}
	}

	@Transactional
	public boolean tryDelete(final long idArea) {
		return EntityArea.deleteById(idArea);
	}

	/**
	 * How to Uses:
	 * 
	 * <pre>
	 * <code>
	 * Model model;
	 * if ((model = service.tryFind(id)) == null)
	 *      return disappointedFind().build();
	 * </code>
	 * </pre>
	 *
	 * @param id
	 * @return
	 */
	public EntityArea tryFindById(final long idArea) {
		return EntityArea.findById(idArea);
	}

}
