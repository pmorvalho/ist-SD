package org.komparator.security;

import java.util.Map;
import java.util.TreeMap;

public class KomparatorSecurityManager {
	private String wsName;
	
	/*Fault Tolerance -- For Mediator Client use*/
	private int idCounter = 1; //operation ID
	private String clientId;
	
	/*Fault Tolerance -- For Mediator use*/
	private Map<String,Integer> idMap = new TreeMap<String,Integer>();
	private boolean duplicated = false;
	private String mostRecentClientId;
	private int mostRecentOpId;
	
	/* Private constructor prevents instantiation from other classes */
	private KomparatorSecurityManager() {
	}
	
	/**
	 * SingletonHolder is loaded on the first execution of
	 * Singleton.getInstance() or the first access to SingletonHolder.INSTANCE,
	 * not before.
	 */
	private static class SingletonHolder {
		private static final KomparatorSecurityManager INSTANCE = new KomparatorSecurityManager();
	}

	public static synchronized KomparatorSecurityManager getInstance() {
		return SingletonHolder.INSTANCE;
	}
	
	public static String getWsName() {
		return getInstance().wsName;
	}

	public static void setWsName(String wsName) {
		getInstance().wsName = wsName;
	}

	public static int getIdCounter() {
		return getInstance().idCounter;
	}

	public static void setIdCounter(int idCounter) {
		getInstance().idCounter = idCounter;
	}
	
	public static void incIdCounter() {
		getInstance().idCounter++;
	}

	public static Map<String, Integer> getIdMap() {
		return getInstance().idMap;
	}

	public static boolean isDuplicated() {
		return getInstance().duplicated;
	}

	public static void setDuplicated(boolean duplicated) {
		getInstance().duplicated = duplicated;
	}

	public static String getMostRecentClientId() {
		return getInstance().mostRecentClientId;
	}

	public static void setMostRecentClientId(String mostRecentClientId) {
		getInstance().mostRecentClientId = mostRecentClientId;
	}

	public static int getMostRecentOpId() {
		return getInstance().mostRecentOpId;
	}

	public static void setMostRecentOpId(int mostRecentOpId) {
		getInstance().mostRecentOpId = mostRecentOpId;
	}

	public static String getClientId() {
		return getInstance().clientId;
	}

	public static void setClientId(String clientId) {
		getInstance().clientId = clientId;
	}
}
