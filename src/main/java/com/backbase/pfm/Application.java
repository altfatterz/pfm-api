package com.backbase.pfm;

import com.backbase.pfm.docs.SwaggerConfig;
import com.backbase.pfm.rest.AppConfig;
import com.backbase.pfm.rest.WebConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@EnableAutoConfiguration
@Import({AppConfig.class, WebConfiguration.class, SwaggerConfig.class })
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
