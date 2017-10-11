package com.github.ontruck;

public enum MopedState {
	Manual,
	CruiseControl,
	AdaptiveCruiseControl,
	Platooning;

	public static MopedState fromString(String string) {
		if ("cruisecontrol".equals(string.toLowerCase()) ||
		    "cc".equals(string.toLowerCase())) {
			return CruiseControl;
		} else if ("adaptivecruisecontrol".equals(string.toLowerCase()) ||
		           "acc".equals(string.toLowerCase())) {
			return AdaptiveCruiseControl;
		} else if ("platooning".equals(string.toLowerCase())) {
			return Platooning;
		}

		return Manual;
	}
}
