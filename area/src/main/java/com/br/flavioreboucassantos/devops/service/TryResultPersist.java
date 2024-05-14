package com.br.flavioreboucassantos.devops.service;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.PersistenceException;

public final record TryResultPersist<E extends PanacheEntityBase>(
		PersistenceException persistenceException,
		E persistedEntity) {
}
