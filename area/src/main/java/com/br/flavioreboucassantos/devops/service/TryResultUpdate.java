package com.br.flavioreboucassantos.devops.service;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

public final record TryResultUpdate<E extends PanacheEntityBase>(
		boolean notFound,
		Exception exception,
		E updatedEntity) {
}
