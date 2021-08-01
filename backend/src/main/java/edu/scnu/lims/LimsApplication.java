package edu.scnu.lims;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;

/**
 * @author ZhuangJieYing
 */
@SpringBootApplication()
public class LimsApplication {

    public static void main(String[] args) {
        SpringApplication.run(LimsApplication.class, args);

        File file = new File(System.getProperty("user.home") + "/lims/images");
        file.mkdirs();
    }

}
