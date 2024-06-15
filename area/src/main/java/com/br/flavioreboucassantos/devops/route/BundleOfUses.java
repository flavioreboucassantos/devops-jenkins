package com.br.flavioreboucassantos.devops.route;

/**
 * @author Flávio Rebouças Santos - flavioReboucasSantos@gmail.com
 */
public final class BundleOfUses {
	public final UsesOfOrigin usesOfOrigin1of4;
	public final UsesOfOrigin usesOfOrigin2of4;
	public final UsesOfOrigin usesOfOrigin3of4;
	public final UsesOfOrigin usesOfOrigin4of4;

	public BundleOfUses(
			final UsesOfOrigin usesOfOrigin1of4,
			final UsesOfOrigin usesOfOrigin2of4,
			final UsesOfOrigin usesOfOrigin3of4,
			final UsesOfOrigin usesOfOrigin4of4) {
		this.usesOfOrigin1of4 = usesOfOrigin1of4;
		this.usesOfOrigin2of4 = usesOfOrigin2of4;
		this.usesOfOrigin3of4 = usesOfOrigin3of4;
		this.usesOfOrigin4of4 = usesOfOrigin4of4;
	}

	public final void reset(final long timeToResetMS) {
		usesOfOrigin1of4.reset(timeToResetMS);
		usesOfOrigin2of4.reset(timeToResetMS);
		usesOfOrigin3of4.reset(timeToResetMS);
		usesOfOrigin4of4.reset(timeToResetMS);
	}

}
