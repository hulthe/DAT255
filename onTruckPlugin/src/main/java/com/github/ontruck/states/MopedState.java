package com.github.ontruck.states;

import java.util.Locale;

public enum MopedState {
	Manual,
	CruiseControl,
	AdaptiveCruiseControl,
	Platooning;

	private static final Locale locale = Locale.UK;

	public static MopedState fromString(String string) {
		if ("cruisecontrol".equals(string.toLowerCase(locale)) ||
		    "cc".equals(string.toLowerCase(locale))) {
			return CruiseControl;
		} else if ("adaptivecruisecontrol".equals(string.toLowerCase(locale)) ||
		           "acc".equals(string.toLowerCase(locale))) {
			return AdaptiveCruiseControl;
		} else if ("platooning".equals(string.toLowerCase(locale))) {
			return Platooning;
		}

		return Manual;
	}
}
