/* Copyright (c) 2010, Carl Burch. License information is located in the
 * logisim_src.Main source code and at www.cburch.com/logisim/. */

package logisim_src.tools;

import logisim_src.util.LocaleManager;
import logisim_src.util.StringGetter;

class Strings {
	private static LocaleManager source
		= new LocaleManager("src/resources/logisim", "tools");

	public static String get(String key) {
		return source.get(key);
	}
	public static StringGetter getter(String key) {
		return source.getter(key);
	}
	public static StringGetter getter(String key, StringGetter arg) {
		return source.getter(key, arg);
	}
}
