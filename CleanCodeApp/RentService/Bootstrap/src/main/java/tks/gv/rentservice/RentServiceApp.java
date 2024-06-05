package tks.gv.rentservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

@SpringBootApplication(exclude = {MongoAutoConfiguration.class, UserDetailsServiceAutoConfiguration.class})
public class RentServiceApp {

    public static void main(String[] args) {
        SpringApplication.run(RentServiceApp.class, args);
    }
}
