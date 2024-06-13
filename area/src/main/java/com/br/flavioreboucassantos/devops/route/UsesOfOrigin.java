package com.br.flavioreboucassantos.devops.route;

/**
 * @author Flávio Rebouças Santos - flavioReboucasSantos@gmail.com
 */
public final class UsesOfOrigin {

	private long timeToResetMS = Long.MIN_VALUE;

	private int uses = 0;

	public final UsesOfOrigin incrementAndTryReset(final long timeToResetMS) {
		if (timeToResetMS > this.timeToResetMS) {
			this.timeToResetMS = timeToResetMS;
			uses = 1;
		} else
			uses++;
		return this;
	}

	public final int getUsesAndTryReset(final long timeToResetMS) {
		if (timeToResetMS > this.timeToResetMS) {
			this.timeToResetMS = timeToResetMS;
			uses = 0;
			return 0;
		}
		return uses;
	}
}
