package com.br.flavioreboucassantos.devops.service;

import java.util.function.Consumer;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;

/**
 * @param <E> Entity extends {@link PanacheEntityBase}
 * 
 * @author Flávio Rebouças Santos
 */
public abstract class ServiceBase<E extends PanacheEntityBase> implements PanacheRepository<E> {

	@Transactional
	protected final TryResultUpdate<E> tryCommitUpdate(final long id, final Consumer<E> consumerEdit) {
		/*
		 * findById
		 */
		E entity;
		if ((entity = findById(id)) == null)
			return new TryResultUpdate<E>(true, null, null);

		consumerEdit.accept(entity);

		return new TryResultUpdate<E>(false, null, entity);
	}

	/**
	 * 
	 * @param entity
	 * @return if return.persistedEntity() == null then it failed and threw return.persistenceException()
	 */
	@Transactional
	public final TryResultPersist<E> tryPersist(final E entity) {
		try {
			entity.persist();
			return new TryResultPersist<E>(null, entity);

		} catch (final PersistenceException e) {

			return new TryResultPersist<E>(e, null);
		}
	}

	public final TryResultUpdate<E> tryUpdate(final long id, final Consumer<E> consumerEdit) {
		try {
			return tryCommitUpdate(id, consumerEdit);

		} catch (final Exception e) {

			return new TryResultUpdate<E>(false, e, null);
		}
	}

	/**
	 *
	 * @param id
	 * @return true if <= 0
	 */
	public final boolean tryIdBadRequest(final long id) {
		return id <= 0;
	}

	/**
	 *
	 * @param id
	 * @return true if id == null or <= 0
	 */
	public final boolean tryIdBadRequest(final Long id) {
		return id == null || id <= 0;
	}

	/**
	 *
	 * @param id1
	 * @param id2
	 * @return true if id1 == id2
	 */
	public final boolean tryIdConflict(final long id1, final long id2) {
		return id1 == id2;
	}

	/**
	 *
	 * @param id1
	 * @param id2
	 * @return true if id1.equals(id2)
	 */
	public final boolean tryIdConflict(final Long id1, final Long id2) {
		return id1.equals(id2);
	}

}
