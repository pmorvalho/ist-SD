package org.komparator.security;

public class KomparatorSecurityManager {
	private String wsName;
	
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
}
