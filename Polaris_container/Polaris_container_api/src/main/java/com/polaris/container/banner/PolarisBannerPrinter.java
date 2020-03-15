package com.polaris.container.banner;

public class PolarisBannerPrinter {
	
	private static String[] BANNER = { "", 
			"__________      .__               .__        ",
			"\\______   \\____ |  | _____ _______|__| ______",
			" |     ___/  _ \\|  | \\__  \\\\_  __ \\  |/  ___/",
			" |    |  (  <_> )  |__/ __ \\|  | \\/  |\\___ \\ ", 
			" |____|   \\____/|____(____  /__|  |__/____  >",
			"                          \\/              \\/ " };
	
	private static final String POLARIS = " :: Polaris :: ";
	private static String VERSION = "1.0.0-SNAPSHOT";
	
	public static void setBanner(String[] banner) {
		BANNER = banner;
	}
	public static void setVersion(String version) {
		VERSION = version;
	}
	public static void print() {
		String mode = System.getProperty("project.banner.mode","on");
		if ("on".equals(mode)) {
			for (String line : BANNER) {
				System.out.println(line);
			}
			String version = " (v" + VERSION + ")";
			System.out.println(POLARIS + "       " + version);
		}
	}
}
