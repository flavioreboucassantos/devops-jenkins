package com.br.flavioreboucassantos.devops.route;

public final class UsesOfOrigin {

	private long timeToResetMS = Long.MAX_VALUE;

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
