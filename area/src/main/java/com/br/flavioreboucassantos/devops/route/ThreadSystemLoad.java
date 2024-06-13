package com.br.flavioreboucassantos.devops.route;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Flávio Rebouças Santos - flavioReboucasSantos@gmail.com
 */
public final class ThreadSystemLoad extends TimerTask {

	/**
	 * System Information
	 */
	static public final Runtime runtime = Runtime.getRuntime();
	static public final OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();

	static public final long maxMemory = runtime.maxMemory();
	static public final long totalMemory = runtime.totalMemory();
	static public long freeMemory;
	static public double systemLoadAverage;

	private final Logger LOG = LoggerFactory.getLogger(ThreadSystemLoad.class);

	/**
	 * 
	 */
	private final AtomicInteger indexToReach;
	private int indexRunner;
	private final String[] listHost;
	private final int limitListHost;

	private final long intervalToResetMS;
	private long timeToResetMS = Long.MIN_VALUE;
	private final UsesOfOrigin[] mapUsesOfOriginByOrigin1of4 = new UsesOfOrigin[256];
	private final UsesOfOrigin[][] mapUsesOfOriginByOrigin2of4 = new UsesOfOrigin[256][256];
	private final UsesOfOrigin[][][] mapUsesOfOriginByOrigin3of4 = new UsesOfOrigin[256][256][256];
	private final Map<String, BundleOfUses> mapBundleOfUsesByOrigin4of4;
	private final int[][] mapLimitOfUsesByOriginByLoadLevel;

	private final int getLoadLevel() {
		if (systemLoadAverage <= 50) {
			return 1;
		} else {
			return 1;
		}
	}
	
	private final boolean allowFromOriginX(final int limitOfUsesOfOrigin, final UsesOfOrigin usesOfOrigin, final long timeToResetMS) {
		return limitOfUsesOfOrigin < 0 || usesOfOrigin.getUsesAndTryReset(timeToResetMS) < limitOfUsesOfOrigin;
	}

	private final void printAllFrom(final Map<String, BundleOfUses> map, final long timeToResetMS) {
		LOG.info("------------------------------ printAllFrom ------------------------------");
		map.forEach((final String origin4of4, final BundleOfUses bundleOfUses) -> {
			String info = "";
			info += origin4of4;
			info += " (" + bundleOfUses.usesOfOrigin1of4.getUsesAndTryReset(timeToResetMS);
			info += " | " + bundleOfUses.usesOfOrigin2of4.getUsesAndTryReset(timeToResetMS);
			info += " | " + bundleOfUses.usesOfOrigin3of4.getUsesAndTryReset(timeToResetMS);
			info += " | " + bundleOfUses.usesOfOrigin4of4.getUsesAndTryReset(timeToResetMS);
			info += ")";
			LOG.info(info);
		});
	}

	private final void updateTimeToResetMS() {
		printAllFrom(mapBundleOfUsesByOrigin4of4, timeToResetMS);
		if (System.currentTimeMillis() >= timeToResetMS) {
			timeToResetMS = System.currentTimeMillis() + intervalToResetMS;
			LOG.info("RESETED");
		}
	}

	private final UsesOfOrigin prepareAndGetItem(final UsesOfOrigin[] map, final int origin) {
		if (map[origin] == null) {
			final UsesOfOrigin newItem = new UsesOfOrigin();
			map[origin] = newItem;
			return newItem;
		} else
			return map[origin];
	}

	private final UsesOfOrigin[] prepareAndGet1D(final UsesOfOrigin[][] map, final int origin) {
		if (map[origin] == null) {
			final UsesOfOrigin[] newMap = new UsesOfOrigin[256];
			map[origin] = newMap;
			return newMap;
		} else
			return map[origin];
	}

	private final UsesOfOrigin[][] prepareAndGet2D(final UsesOfOrigin[][][] map, final int origin) {
		if (map[origin] == null) {
			final UsesOfOrigin[][] newMap = new UsesOfOrigin[256][256];
			map[origin] = newMap;
			return newMap;
		} else
			return map[origin];
	}

	/*
	 * 
	 */

	private final UsesOfOrigin getUsesOfOrigin(final int origin1of4) {
		return prepareAndGetItem(mapUsesOfOriginByOrigin1of4, origin1of4);
	}

	private final UsesOfOrigin getUsesOfOrigin(final int origin1of4, final int origin2of4) {
		return prepareAndGetItem(prepareAndGet1D(mapUsesOfOriginByOrigin2of4, origin1of4), origin2of4);
	}

	private final UsesOfOrigin getUsesOfOrigin(final int origin1of4, final int origin2of4, final int origin3of4) {
		return prepareAndGetItem(prepareAndGet1D(prepareAndGet2D(mapUsesOfOriginByOrigin3of4, origin1of4), origin2of4), origin3of4);
	}

	private final void doWriteOfUse(final String origin4of4, final long timeToResetMS) {
		if (mapBundleOfUsesByOrigin4of4.containsKey(origin4of4)) {
			final BundleOfUses bundleOfUses = mapBundleOfUsesByOrigin4of4.get(origin4of4);

			bundleOfUses.usesOfOrigin1of4.incrementAndTryReset(timeToResetMS);
			bundleOfUses.usesOfOrigin2of4.incrementAndTryReset(timeToResetMS);
			bundleOfUses.usesOfOrigin3of4.incrementAndTryReset(timeToResetMS);
			bundleOfUses.usesOfOrigin4of4.incrementAndTryReset(timeToResetMS);

		} else {
			final String[] origins1234 = origin4of4.split("\\.");
			final int origin1of4 = Integer.valueOf(origins1234[0]);
			final int origin2of4 = Integer.valueOf(origins1234[1]);
			final int origin3of4 = Integer.valueOf(origins1234[2]);

			final UsesOfOrigin usesOfOrigin1of4 = getUsesOfOrigin(origin1of4).incrementAndTryReset(timeToResetMS);
			final UsesOfOrigin usesOfOrigin2of4 = getUsesOfOrigin(origin1of4, origin2of4).incrementAndTryReset(timeToResetMS);
			final UsesOfOrigin usesOfOrigin3of4 = getUsesOfOrigin(origin1of4, origin2of4, origin3of4).incrementAndTryReset(timeToResetMS);
			final UsesOfOrigin usesOfOrigin4of4 = new UsesOfOrigin().incrementAndTryReset(timeToResetMS);

			mapBundleOfUsesByOrigin4of4.put(origin4of4, new BundleOfUses(usesOfOrigin1of4, usesOfOrigin2of4, usesOfOrigin3of4, usesOfOrigin4of4));
		}

//		final BundleOfUses bundleOfUses = mapBundleOfUsesByOrigin4of4.get(origin4of4);
//		String info = "";
//		info += origin4of4;
//		info += " | " + bundleOfUses.usesOfOrigin1of4.getUsesAndTryReset(timeToResetMS);
//		info += " | " + bundleOfUses.usesOfOrigin2of4.getUsesAndTryReset(timeToResetMS);
//		info += " | " + bundleOfUses.usesOfOrigin3of4.getUsesAndTryReset(timeToResetMS);
//		info += " | " + bundleOfUses.usesOfOrigin4of4.getUsesAndTryReset(timeToResetMS);
//		LOG.info(info);
	}

	private final void doRunner() {
		final long timeToResetMS = this.timeToResetMS;

		final int indexToReach = this.indexToReach.get();
		while (indexRunner != indexToReach) {
			doWriteOfUse(listHost[indexRunner], timeToResetMS);

			if (++indexRunner >= limitListHost)
				indexRunner = 0;
		}
	}

	public ThreadSystemLoad(
			final AtomicInteger indexHost,
			final String[] listHost,
			final Map<String, BundleOfUses> mapBundleOfUsesByOrigin4of4,
			final long intervalToResetMS,
			final int[][] mapLimitOfUsesByOriginByLoadLevel) {
		this.indexToReach = indexHost;
		indexRunner = indexHost.get();
		this.listHost = listHost;
		limitListHost = listHost.length;

		this.mapBundleOfUsesByOrigin4of4 = mapBundleOfUsesByOrigin4of4;
		this.intervalToResetMS = intervalToResetMS;
		this.mapLimitOfUsesByOriginByLoadLevel = mapLimitOfUsesByOriginByLoadLevel;
	}

	@Override
	public void run() {
		try {

			updateTimeToResetMS();
			doRunner();

			freeMemory = runtime.freeMemory();

			systemLoadAverage = operatingSystemMXBean.getSystemLoadAverage();

			String info = "";
			info += "maxMemory: " + String.valueOf(maxMemory);
			info += " | totalMemory: " + String.valueOf(totalMemory);
			info += " | freeMemory: " + String.valueOf(freeMemory);
			info += " | systemLoadAverage: " + String.valueOf(systemLoadAverage);

//			LOG.info(info);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}	

	public final boolean allowFromOrigins1234(final String host) {
		final int loadLevel = getLoadLevel();
		if (loadLevel == 0) // policy
			return true;

		final long timeToResetMS = this.timeToResetMS;

		final BundleOfUses bundleOfUses = mapBundleOfUsesByOrigin4of4.get(host);
		if (bundleOfUses == null)
			return true;

		if (!allowFromOriginX(mapLimitOfUsesByOriginByLoadLevel[3][loadLevel], bundleOfUses.usesOfOrigin4of4, timeToResetMS))
			return false;

		if (!allowFromOriginX(mapLimitOfUsesByOriginByLoadLevel[2][loadLevel], bundleOfUses.usesOfOrigin3of4, timeToResetMS))
			return false;

		if (!allowFromOriginX(mapLimitOfUsesByOriginByLoadLevel[1][loadLevel], bundleOfUses.usesOfOrigin2of4, timeToResetMS))
			return false;

		if (!allowFromOriginX(mapLimitOfUsesByOriginByLoadLevel[0][loadLevel], bundleOfUses.usesOfOrigin1of4, timeToResetMS))
			return false;

		return true;
	}
}
