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
public class ThreadSystemLoad extends TimerTask {

	private final Logger LOG = LoggerFactory.getLogger(ThreadSystemLoad.class);

	static final Runtime runtime = Runtime.getRuntime();
	static final OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();

	static final long maxMemory = runtime.maxMemory();
	static final long totalMemory = runtime.totalMemory();
	static long freeMemory;
	static double systemLoadAverage;

	/**
	 * 
	 */
	private final AtomicInteger indexToReach;
	private int indexRunner;
	private final String[] listHost;
	private final int limitListHost;

	private final long intervalToResetMS;
	private long timeToResetMS = Long.MAX_VALUE;
	private final UsesOfOrigin[] mapUsesOfOriginByOrigin1of4 = new UsesOfOrigin[255];
	private final UsesOfOrigin[][] mapUsesOfOriginByOrigin2of4 = new UsesOfOrigin[255][255];
	private final UsesOfOrigin[][][] mapUsesOfOriginByOrigin3of4 = new UsesOfOrigin[255][255][255];
	private final Map<String, BundleOfUses> mapBundleOfUsesByOrigin4of4;

//	System.out.println(x);

	private final void updateTimeToResetMS() {
		if (timeToResetMS >= System.currentTimeMillis())
			timeToResetMS = System.currentTimeMillis() + intervalToResetMS;
	}

	private final UsesOfOrigin prepareAndGetItem(final UsesOfOrigin[] map, final int origin) {
		if (map[origin] == null) {
			final UsesOfOrigin newItem = new UsesOfOrigin(timeToResetMS);
			map[origin] = newItem;
			return newItem;
		} else
			return map[origin];
	}

	private final UsesOfOrigin[] prepareAndGet1D(final UsesOfOrigin[][] map, final int origin) {
		if (map[origin] == null) {
			final UsesOfOrigin[] newMap = new UsesOfOrigin[255];
			map[origin] = newMap;
			return newMap;
		} else
			return map[origin];
	}

	private final UsesOfOrigin[][] prepareAndGet2D(final UsesOfOrigin[][][] map, final int origin) {
		if (map[origin] == null) {
			final UsesOfOrigin[][] newMap = new UsesOfOrigin[255][255];
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

	private final void doWriteOfUse(final String origin4of4) {
//		System.out.println(host);

		final long timeToResetMS = this.timeToResetMS;

		if (mapBundleOfUsesByOrigin4of4.containsKey(origin4of4)) {
			final BundleOfUses bundleOfUses = mapBundleOfUsesByOrigin4of4.get(origin4of4);

			bundleOfUses.usesOfOrigin1of4.tryReset(timeToResetMS).uses++;
			bundleOfUses.usesOfOrigin2of4.tryReset(timeToResetMS).uses++;
			bundleOfUses.usesOfOrigin3of4.tryReset(timeToResetMS).uses++;
			bundleOfUses.usesOfOrigin4of4.tryReset(timeToResetMS).uses++;

		} else {
			final String[] origins123 = origin4of4.split("\\.");
			final int origin1of4 = Integer.valueOf(origins123[0]);
			final int origin2of4 = Integer.valueOf(origins123[1]);
			final int origin3of4 = Integer.valueOf(origins123[2]);

			final UsesOfOrigin usesOfOrigin1of4 = getUsesOfOrigin(origin1of4).tryReset(timeToResetMS);
			final UsesOfOrigin usesOfOrigin2of4 = getUsesOfOrigin(origin1of4, origin2of4).tryReset(timeToResetMS);
			final UsesOfOrigin usesOfOrigin3of4 = getUsesOfOrigin(origin1of4, origin2of4, origin3of4).tryReset(timeToResetMS);
			final UsesOfOrigin usesOfOrigin4of4 = new UsesOfOrigin(timeToResetMS);

			mapBundleOfUsesByOrigin4of4.put(origin4of4, new BundleOfUses(usesOfOrigin1of4, usesOfOrigin2of4, usesOfOrigin3of4, usesOfOrigin4of4));
		}

		final BundleOfUses bundleOfUses = mapBundleOfUsesByOrigin4of4.get(origin4of4);
		String info = "";
		info += origin4of4;
		info += " | " + bundleOfUses.usesOfOrigin1of4.uses;
		info += " | " + bundleOfUses.usesOfOrigin2of4.uses;
		info += " | " + bundleOfUses.usesOfOrigin3of4.uses;
		info += " | " + bundleOfUses.usesOfOrigin4of4.uses;
		System.out.println(info);
	}

	private final void doRunner() {
		final int indexToReach = this.indexToReach.get();
		while (indexRunner != indexToReach) {
			doWriteOfUse(listHost[indexRunner]);

			if (++indexRunner >= limitListHost)
				indexRunner = 0;
		}
	}

	public ThreadSystemLoad(
			final AtomicInteger indexHost,
			final String[] listHost,
			final Map<String, BundleOfUses> mapBundleOfUsesByOrigin4of4,
			final long intervalToResetMS) {
		this.indexToReach = indexHost;
		indexRunner = indexHost.get();
		this.listHost = listHost;
		limitListHost = listHost.length;

		this.mapBundleOfUsesByOrigin4of4 = mapBundleOfUsesByOrigin4of4;
		this.intervalToResetMS = intervalToResetMS;
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
}
