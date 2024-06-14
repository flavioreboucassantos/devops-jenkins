package com.br.flavioreboucassantos.devops.route;

/**
 * @author Flávio Rebouças Santos - flavioReboucasSantos@gmail.com
 */
public final class UsesOfOrigin {

	private long timeToResetMS = Long.MIN_VALUE;

	private int uses = 0;

	public final UsesOfOrigin incrementLightAndTryReset(final long timeToResetMS) {
		if (timeToResetMS > this.timeToResetMS) {
			this.timeToResetMS = timeToResetMS;
			uses = 1; // first use
		} else
			uses++;
		return this;
	}

	public final UsesOfOrigin incrementHeavyAndTryReset(final long timeToResetMS) {
		if (timeToResetMS > this.timeToResetMS) {
			this.timeToResetMS = timeToResetMS;
			uses = RouteAndFilter.weightHeavyUse; // first use
		} else
			uses += RouteAndFilter.weightHeavyUse;
		return this;
	}

	public final int getUses() {
		return uses;
	}
}
