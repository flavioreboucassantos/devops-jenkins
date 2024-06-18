package com.br.flavioreboucassantos.devops.route;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntUnaryOperator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Flávio Rebouças Santos - flavioReboucasSantos@gmail.com
 */
public abstract class ThreadSystemLoad extends TimerTask {

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
	private final AtomicInteger indexProducerLightUse = new AtomicInteger(0);
	private int indexConsumerLightUse;
	private final int limitListHostLightUse = 10 * 1000;
	private final String[] listHostLightUse = new String[limitListHostLightUse];
	private final IntUnaryOperator updateFunctionIndexProducerLightUse = i -> (++i >= limitListHostLightUse || i < 0) ? 0 : i;

	private final AtomicInteger indexProducerHeavyUse = new AtomicInteger(0);
	private int indexConsumerHeavyUse;
	private final int limitListHostHeavyUse = 10 * 1000;
	private final String[] listHostHeavyUse = new String[limitListHostHeavyUse];
	private final IntUnaryOperator updateFunctionIndexProducerHeavyUse = i -> (++i >= limitListHostHeavyUse || i < 0) ? 0 : i;

	private final long intervalToResetMS;
	private long timeToResetMS = Long.MIN_VALUE;
	private final UsesOfOrigin[] mapUsesOfOriginByOrigin1of4 = new UsesOfOrigin[256];
	private final UsesOfOrigin[][] mapUsesOfOriginByOrigin2of4 = new UsesOfOrigin[256][256];
	private final UsesOfOrigin[][][] mapUsesOfOriginByOrigin3of4 = new UsesOfOrigin[256][256][256];
	private final Map<String, BundleOfUses> mapBundleOfUsesByOrigin4of4 = new ConcurrentHashMap<String, BundleOfUses>();

	private final Map<String, BundleOfUses> mapDeniedHostByOrigin4of4 = new ConcurrentHashMap<String, BundleOfUses>();

	private final void setLoadLevel(final double systemLoadAverage) {
		if (systemLoadAverage <= 50) {
			loadLevel = 1;
		} else {
			loadLevel = 1;
		}
	}

	private final void updateSystemInformation() {
		freeMemory = runtime.freeMemory();

		systemLoadAverage = operatingSystemMXBean.getSystemLoadAverage();
		setLoadLevel(systemLoadAverage);

//		String info = "";
//		info += "maxMemory: " + String.valueOf(maxMemory);
//		info += " | totalMemory: " + String.valueOf(totalMemory);
//		info += " | freeMemory: " + String.valueOf(freeMemory);
//		info += " | systemLoadAverage: " + String.valueOf(systemLoadAverage);
//		info += " | loadLevel: " + String.valueOf(loadLevel);
//		LOG.info(info);
	}

	private final void updateTimeToResetOfDeniedHost(final long timeToResetMS) {
//		LOG.info("FOUND " + mapDeniedHostByOrigin4of4.size() + " DENIED HOST(S)");
		mapDeniedHostByOrigin4of4.entrySet().forEach(e -> e.getValue().reset(timeToResetMS));
		mapDeniedHostByOrigin4of4.clear();
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
			updateTimeToResetOfDeniedHost(timeToResetMS);
//			LOG.info("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! --- UPDATED TIME TO RESET --- !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		}
	}

	/*
	 * prepareAndGet...
	 */

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
	 * getUsesOfOrigin
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

	private final void doConsumerWork() {
		final long timeToResetMS = this.timeToResetMS;

		/*
		 * Light Use
		 */
		final int indexProducerLightUse = this.indexProducerLightUse.get();
		while (indexConsumerLightUse != indexProducerLightUse) {
			doWriteOfLightUse(listHostLightUse[indexConsumerLightUse], timeToResetMS);

			if (++indexConsumerLightUse >= limitListHostLightUse)
				indexConsumerLightUse = 0;
		}

		/*
		 * Heavy Use
		 */
		final int indexProducerHeavyUse = this.indexProducerHeavyUse.get();
		while (indexConsumerHeavyUse != indexProducerHeavyUse) {
			doWriteOfHeavyUse(listHostHeavyUse[indexConsumerHeavyUse], timeToResetMS);

			if (++indexConsumerHeavyUse >= limitListHostHeavyUse)
				indexConsumerHeavyUse = 0;
		}
	}

	/*
	 * PROTECTED PROTECTED PROTECTED PROTECTED PROTECTED PROTECTED PROTECTED PROTECTED PROTECTED PROTECTED PROTECTED PROTECTED PROTECTED PROTECTED
	 * PROTECTED PROTECTED PROTECTED PROTECTED PROTECTED PROTECTED PROTECTED PROTECTED PROTECTED PROTECTED PROTECTED PROTECTED PROTECTED PROTECTED
	 * PROTECTED PROTECTED PROTECTED PROTECTED PROTECTED PROTECTED PROTECTED PROTECTED PROTECTED PROTECTED PROTECTED PROTECTED PROTECTED PROTECTED
	 * PROTECTED PROTECTED PROTECTED PROTECTED PROTECTED PROTECTED PROTECTED PROTECTED PROTECTED PROTECTED PROTECTED PROTECTED PROTECTED PROTECTED
	 * PROTECTED PROTECTED PROTECTED PROTECTED PROTECTED PROTECTED PROTECTED PROTECTED PROTECTED PROTECTED PROTECTED PROTECTED PROTECTED PROTECTED
	 */

	/**
	 * Asynchronous and Multithreading.
	 * 
	 * @param host
	 */
	protected final void addAllowedHostLightUse(final String host) {
		// LOG.info(host);
		listHostLightUse[indexProducerLightUse.getAndUpdate(updateFunctionIndexProducerLightUse)] = host;
	}

	/**
	 * Asynchronous and Multithreading.
	 * 
	 * @param host
	 */
	protected final void addAllowedHostHeavyUse(final String host) {
		// LOG.info(host);
		listHostHeavyUse[indexProducerHeavyUse.getAndUpdate(updateFunctionIndexProducerHeavyUse)] = host;
	}

	/**
	 * Asynchronous and Multithreading.
	 * 
	 * @param host
	 */
	protected final void setDeniedHost(final String host) {
		// LOG.info(host);
		mapDeniedHostByOrigin4of4.computeIfAbsent(host, (final String k) -> mapBundleOfUsesByOrigin4of4.get(k));
	}

	/**
	 * Asynchronous and Multithreading.
	 * 
	 * @param host
	 * @param mapLimitOfUses
	 * @return
	 */
	protected final boolean allowUseFromOrigins1234(final String host, final int[][] mapLimitOfUses) {
		final BundleOfUses bundleOfUses = mapBundleOfUsesByOrigin4of4.get(host);
		if (bundleOfUses == null)
			return true;

		final int loadLevel = this.loadLevel;

//		LOG.info(">>>>>>>>> RETURN ALLOWED FROM ORIGINS 1, 2, 3, 4 >>>>>>>>>");

		if (bundleOfUses.usesOfOrigin4of4.getUses() >= mapLimitOfUses[3][loadLevel])
			return false;

//		LOG.info("ALLOWED FROM ORIGIN 4of4");

		if (bundleOfUses.usesOfOrigin3of4.getUses() >= mapLimitOfUses[2][loadLevel])
			return false;

//		LOG.info("ALLOWED FROM ORIGIN 3of4");

		if (bundleOfUses.usesOfOrigin2of4.getUses() >= mapLimitOfUses[1][loadLevel])
			return false;

//		LOG.info("ALLOWED FROM ORIGIN 2of4");

		if (bundleOfUses.usesOfOrigin1of4.getUses() >= mapLimitOfUses[0][loadLevel])
			return false;

//		LOG.info("ALLOWED FROM ORIGIN 1of4");

		return true;
	}

	/*
	 * PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC
	 * PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC
	 * PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC
	 * PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC
	 * PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC PUBLIC
	 */

	public ThreadSystemLoad(final long intervalToResetMS) {
		this.intervalToResetMS = intervalToResetMS;
		updateSystemInformation();

	}

	@Override
	public void run() {
		try {

			updateTimeToResetMS();
			doConsumerWork();
			updateSystemInformation();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
