package com.polaris.container.banner;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.polaris.core.Constant;
import com.polaris.core.config.reader.launcher.ConfLauncherReaderStrategy;
import com.polaris.core.util.FileUtil;
import com.polaris.core.util.StringUtil;

public class BannerPrinter {
	private static final Logger logger = LoggerFactory.getLogger(BannerPrinter.class);
	private static final String BANNER_FILE = "banner.txt";
	private static String[] BANNER = { "", 
			"__________      .__               .__        ",
			"\\______   \\____ |  | _____ _______|__| ______",
			" |     ___/  _ \\|  | \\__  \\\\_  __ \\  |/  ___/",
			" |    |  (  <_> )  |__/ __ \\|  | \\/  |\\___ \\ ", 
			" |____|   \\____/|____(____  /__|  |__/____  >",
			"                          \\/              \\/ ",
			" :: Polaris ::           v1.0.0-SNAPSHOT"
			};
	
	
	public static void print(Banner.Mode bannerMode) {
		
		try {
			String contents = FileUtil.read(ConfLauncherReaderStrategy.INSTANCE.getInputStream(BANNER_FILE));
			if (StringUtil.isNotEmpty(contents)) {
				BANNER = contents.split(Constant.LINE_SEP);
			}
        } catch (IOException e) {
        	logger.error("ERROR:",e);
        }
		
		if (bannerMode != Banner.Mode.OFF) {
			if (bannerMode == Banner.Mode.CONSOLE) {
				for (String line : BANNER) {
					System.out.println(line);
				}
			} else {
				for (String line : BANNER) {
					logger.info(line);
				}
			}
			
		}
	}
}
