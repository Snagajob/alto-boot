/**
 * Created by jason.kowalewski on 8/31/17.
 */

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@EnableAutoConfiguration
@ComponentScan({ "api", "alto", "data" })
@SpringBootApplication
public class AltoApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(AltoApplication.class, args);
    }
}
