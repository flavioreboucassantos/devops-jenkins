package com.br.flavioreboucassantos.devops.service;

public abstract class ServiceBase {

	/**
	 *
	 * @param id
	 * @return true if id == null or <= 0
	 */
	public boolean tryIdBadRequest(Long id) {
		return id == null || id <= 0;
	}

	/**
	 *
	 * @param id1
	 * @param id2
	 * @return true if id1.equals(id2)
	 */
	public boolean tryIdConflict(Long id1, Long id2) {
		return id1.equals(id2);
	}

}
