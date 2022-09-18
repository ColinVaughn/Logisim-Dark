/* Copyright (c) 2010, Carl Burch. License information is located in the
 * logisim_src.Main source code and at www.cburch.com/logisim/. */

package logisim_src.util;

class Strings {
	static LocaleManager source
		= new LocaleManager("src/resources/logisim", "util");

	public static LocaleManager getLocaleManager() {
		return source;
	}
	public static String get(String key) {
		return source.get(key);
	}
	public static StringGetter getter(String key) {
		return source.getter(key);
	}
}
