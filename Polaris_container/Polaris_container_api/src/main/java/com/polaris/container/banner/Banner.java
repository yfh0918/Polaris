package com.polaris.container.banner;

public interface Banner {
	/**
	 * An enumeration of possible values for configuring the Banner.
	 */
	enum Mode {

		/**
		 * Disable printing of the banner.
		 */
		OFF,

		/**
		 * Print the banner to System.out.
		 */
		CONSOLE,

		/**
		 * Print the banner to the log file.
		 */
		LOG

	}
}
