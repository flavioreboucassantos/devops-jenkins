package com.br.flavioreboucassantos.devops.service;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.PersistenceException;

public record TryResultPersist<T extends PanacheEntityBase>(
		PersistenceException persistenceException,
		T persistedEntity) {

	public TryResultPersist(final PersistenceException e) {
		this(e, null);
	}
}
