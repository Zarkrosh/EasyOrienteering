package com.hergomsoft.easyoapi;

import java.util.TimeZone;
import javax.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EasyOrienteeringApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(EasyOrienteeringApiApplication.class, args);
    }
    
    @PostConstruct
    void started() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

}
