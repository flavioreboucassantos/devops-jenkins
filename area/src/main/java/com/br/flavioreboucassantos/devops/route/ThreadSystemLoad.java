package com.br.flavioreboucassantos.devops.route;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntUnaryOperator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Flávio Rebouças Santos - flavioReboucasSantos@gmail.com
 */
public final class ThreadSystemLoad extends TimerTask {

	private final Logger LOG = LoggerFactory.getLogger(ThreadSystemLoad.class);

	/**
	 * System Information
	 */
	private final Runtime runtime = Runtime.getRuntime();
	private final OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();

	private final long maxMemory = runtime.maxMemory();
	private final long totalMemory = runtime.totalMemory();
	private long freeMemory;
	private double systemLoadAverage;
	private int loadLevel;

	/**
	 * 
	 */
	private final AtomicInteger indexHostLightUse = new AtomicInteger(0);
	private int indexRunnerLightUse;
	private final int limitListHostLightUse = 10 * 1000;
	private final String[] listHostLightUse = new String[limitListHostLightUse];
	private final IntUnaryOperator updateFunctionIndexHostLightUse = i -> (++i >= limitListHostLightUse || i < 0) ? 0 : i;

	private final AtomicInteger indexHostHeavyUse = new AtomicInteger(0);
	private int indexRunnerHeavyUse;
	private final int limitListHostHeavyUse = 10 * 1000;
	private final String[] listHostHeavyUse = new String[limitListHostHeavyUse];
	private final IntUnaryOperator updateFunctionIndexHostHeavyUse = i -> (++i >= limitListHostHeavyUse || i < 0) ? 0 : i;

	private final long intervalToResetMS;
	private long timeToResetMS = Long.MIN_VALUE;
	private final UsesOfOrigin[] mapUsesOfOriginByOrigin1of4 = new UsesOfOrigin[256];
	private final UsesOfOrigin[][] mapUsesOfOriginByOrigin2of4 = new UsesOfOrigin[256][256];
	private final UsesOfOrigin[][][] mapUsesOfOriginByOrigin3of4 = new UsesOfOrigin[256][256][256];
	private final Map<String, BundleOfUses> mapBundleOfUsesByOrigin4of4 = new LinkedHashMap<String, BundleOfUses>();

	private final void setLoadLevel(final double systemLoadAverage) {
		if (systemLoadAverage <= 50) {
			loadLevel = 1;
		} else {
			loadLevel = 1;
		}
	}

	private final boolean allowFromOriginX(final int limitOfUsesOfOrigin, final UsesOfOrigin usesOfOrigin) {
		return limitOfUsesOfOrigin < 0 || usesOfOrigin.getUses() < limitOfUsesOfOrigin;
	}

	private final void printAllFrom(final Map<String, BundleOfUses> map) {
		LOG.info("------------------------------ printAllFrom ------------------------------");
		map.forEach((final String origin4of4, final BundleOfUses bundleOfUses) -> {
			String info = "";
			info += origin4of4;
			info += " (" + bundleOfUses.usesOfOrigin1of4.getUses();
			info += " | " + bundleOfUses.usesOfOrigin2of4.getUses();
			info += " | " + bundleOfUses.usesOfOrigin3of4.getUses();
			info += " | " + bundleOfUses.usesOfOrigin4of4.getUses();
			info += ")";
			LOG.info(info);
		});
	}

	private final void updateTimeToResetMS() {
//		printAllFrom(mapBundleOfUsesByOrigin4of4);
		if (System.currentTimeMillis() >= timeToResetMS) {
			timeToResetMS = System.currentTimeMillis() + intervalToResetMS;
//			LOG.info("------------------------------ !!! UPDATED TIME TO RESET !!! ------------------------------");
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

	private final void doWriteOfLightUse(final String origin4of4, final long timeToResetMS) {
		if (mapBundleOfUsesByOrigin4of4.containsKey(origin4of4)) {
			final BundleOfUses bundleOfUses = mapBundleOfUsesByOrigin4of4.get(origin4of4);

			bundleOfUses.usesOfOrigin1of4.incrementLightAndTryReset(timeToResetMS);
			bundleOfUses.usesOfOrigin2of4.incrementLightAndTryReset(timeToResetMS);
			bundleOfUses.usesOfOrigin3of4.incrementLightAndTryReset(timeToResetMS);
			bundleOfUses.usesOfOrigin4of4.incrementLightAndTryReset(timeToResetMS);

		} else {
			final String[] origins1234 = origin4of4.split("\\.");
			final int origin1of4 = Integer.valueOf(origins1234[0]);
			final int origin2of4 = Integer.valueOf(origins1234[1]);
			final int origin3of4 = Integer.valueOf(origins1234[2]);

			final UsesOfOrigin usesOfOrigin1of4 = getUsesOfOrigin(origin1of4).incrementLightAndTryReset(timeToResetMS);
			final UsesOfOrigin usesOfOrigin2of4 = getUsesOfOrigin(origin1of4, origin2of4).incrementLightAndTryReset(timeToResetMS);
			final UsesOfOrigin usesOfOrigin3of4 = getUsesOfOrigin(origin1of4, origin2of4, origin3of4).incrementLightAndTryReset(timeToResetMS);
			final UsesOfOrigin usesOfOrigin4of4 = new UsesOfOrigin().incrementLightAndTryReset(timeToResetMS);

			mapBundleOfUsesByOrigin4of4.put(origin4of4, new BundleOfUses(usesOfOrigin1of4, usesOfOrigin2of4, usesOfOrigin3of4, usesOfOrigin4of4));
		}
	}

	private final void doWriteOfHeavyUse(final String origin4of4, final long timeToResetMS) {
		if (mapBundleOfUsesByOrigin4of4.containsKey(origin4of4)) {
			final BundleOfUses bundleOfUses = mapBundleOfUsesByOrigin4of4.get(origin4of4);

			bundleOfUses.usesOfOrigin1of4.incrementHeavyAndTryReset(timeToResetMS);
			bundleOfUses.usesOfOrigin2of4.incrementHeavyAndTryReset(timeToResetMS);
			bundleOfUses.usesOfOrigin3of4.incrementHeavyAndTryReset(timeToResetMS);
			bundleOfUses.usesOfOrigin4of4.incrementHeavyAndTryReset(timeToResetMS);

		} else {
			final String[] origins1234 = origin4of4.split("\\.");
			final int origin1of4 = Integer.valueOf(origins1234[0]);
			final int origin2of4 = Integer.valueOf(origins1234[1]);
			final int origin3of4 = Integer.valueOf(origins1234[2]);

			final UsesOfOrigin usesOfOrigin1of4 = getUsesOfOrigin(origin1of4).incrementHeavyAndTryReset(timeToResetMS);
			final UsesOfOrigin usesOfOrigin2of4 = getUsesOfOrigin(origin1of4, origin2of4).incrementHeavyAndTryReset(timeToResetMS);
			final UsesOfOrigin usesOfOrigin3of4 = getUsesOfOrigin(origin1of4, origin2of4, origin3of4).incrementHeavyAndTryReset(timeToResetMS);
			final UsesOfOrigin usesOfOrigin4of4 = new UsesOfOrigin().incrementHeavyAndTryReset(timeToResetMS);

			mapBundleOfUsesByOrigin4of4.put(origin4of4, new BundleOfUses(usesOfOrigin1of4, usesOfOrigin2of4, usesOfOrigin3of4, usesOfOrigin4of4));
		}
	}

	private final void doRunnerWork() {
		final long timeToResetMS = this.timeToResetMS;

		/*
		 * Light Use
		 */
		final int indexHostLightUse = this.indexHostLightUse.get();
		while (indexRunnerLightUse != indexHostLightUse) {
			doWriteOfLightUse(listHostLightUse[indexRunnerLightUse], timeToResetMS);

			if (++indexRunnerLightUse >= limitListHostLightUse)
				indexRunnerLightUse = 0;
		}

		/*
		 * Heavy Use
		 */
		final int indexHostHeavyUse = this.indexHostHeavyUse.get();
		while (indexRunnerHeavyUse != indexHostHeavyUse) {
			doWriteOfHeavyUse(listHostHeavyUse[indexRunnerHeavyUse], timeToResetMS);

			if (++indexRunnerHeavyUse >= limitListHostHeavyUse)
				indexRunnerHeavyUse = 0;
		}
	}

	public ThreadSystemLoad(final long intervalToResetMS) {
		this.intervalToResetMS = intervalToResetMS;
	}

	public final void addHostLightUse(final String host) {
		// LOG.info(host);
		listHostLightUse[indexHostLightUse.getAndUpdate(updateFunctionIndexHostLightUse)] = host;
	}

	public final void addHostHeavyUse(final String host) {
		// LOG.info(host);
		listHostHeavyUse[indexHostHeavyUse.getAndUpdate(updateFunctionIndexHostHeavyUse)] = host;
	}

	@Override
	public void run() {
		try {

			updateTimeToResetMS();
			doRunnerWork();

			freeMemory = runtime.freeMemory();

			systemLoadAverage = operatingSystemMXBean.getSystemLoadAverage();
			setLoadLevel(systemLoadAverage);

//			String info = "";
//			info += "maxMemory: " + String.valueOf(maxMemory);
//			info += " | totalMemory: " + String.valueOf(totalMemory);
//			info += " | freeMemory: " + String.valueOf(freeMemory);
//			info += " | systemLoadAverage: " + String.valueOf(systemLoadAverage);
//			info += " | loadLevel: " + String.valueOf(loadLevel);

//			//LOG.info(info);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Asynchronous and Multithreading.
	 * 
	 * @param host
	 * @param mapLimitOfUses
	 * @return
	 */
	public final boolean allowUseFromOrigins1234(final String host, final int[][] mapLimitOfUses) {
		final int loadLevel = this.loadLevel;
		if (loadLevel == 0) // policy
			return true;

		final BundleOfUses bundleOfUses = mapBundleOfUsesByOrigin4of4.get(host);
		if (bundleOfUses == null)
			return true;

		// LOG.info(">>>>>>>>> RETURN ALLOWED FROM ORIGINS 1, 2, 3, 4 >>>>>>>>>");

		if (!allowFromOriginX(mapLimitOfUses[3][loadLevel], bundleOfUses.usesOfOrigin4of4))
			return false;

		// LOG.info("ALLOWED FROM ORIGIN 4of4");

		if (!allowFromOriginX(mapLimitOfUses[2][loadLevel], bundleOfUses.usesOfOrigin3of4))
			return false;

		// LOG.info("ALLOWED FROM ORIGIN 3of4");

		if (!allowFromOriginX(mapLimitOfUses[1][loadLevel], bundleOfUses.usesOfOrigin2of4))
			return false;

		// LOG.info("ALLOWED FROM ORIGIN 2of4");

		if (!allowFromOriginX(mapLimitOfUses[0][loadLevel], bundleOfUses.usesOfOrigin1of4))
			return false;

		// LOG.info("ALLOWED FROM ORIGIN 1of4");

		return true;
	}
}
