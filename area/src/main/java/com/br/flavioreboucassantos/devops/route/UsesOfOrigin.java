package com.br.flavioreboucassantos.devops.route;

/**
 * @author Flávio Rebouças Santos - flavioReboucasSantos@gmail.com
 */
public final class UsesOfOrigin {

	private long timeToResetMS = Long.MIN_VALUE;

	private int uses = 0;

	final public UsesOfOrigin incrementAndTryReset(final long timeToResetMS) {
		if (timeToResetMS > this.timeToResetMS) {
			this.timeToResetMS = timeToResetMS;
			uses = 1;			
		} else
			uses++;
		return this;
	}
	
	final public int getUses() {
		return uses;
	}
}
