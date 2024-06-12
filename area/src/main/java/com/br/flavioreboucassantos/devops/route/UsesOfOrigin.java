package com.br.flavioreboucassantos.devops.route;

/**
 * @author Flávio Rebouças Santos - flavioReboucasSantos@gmail.com
 */
public final class UsesOfOrigin {

	private long timeToResetMS = Long.MIN_VALUE;

	public int uses = 1; // first use

	public UsesOfOrigin(final long timeToResetMS) {
		this.timeToResetMS = timeToResetMS;
	}

	final public UsesOfOrigin tryReset(final long timeToResetMS) {
		if (timeToResetMS > this.timeToResetMS) {
			uses = 0;
			this.timeToResetMS = timeToResetMS;
		}
		return this;
	}
}
