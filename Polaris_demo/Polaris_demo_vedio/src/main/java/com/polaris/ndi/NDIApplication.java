package com.polaris.ndi;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.polaris.container.ServerRunner;
import com.polaris.core.config.annotation.PolarisConfigurationExt;

@SpringBootApplication
@PolarisConfigurationExt("main.properties")
public class NDIApplication {
    public static void main(String[] args) throws Exception {
        ServerRunner.run(args,NDIApplication.class);
    }
}
