package com.deneme.influencerinsight;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class InfluencerInsightApplication {

    public static void main(String[] args) {
        SpringApplication.run(InfluencerInsightApplication.class, args);
    }

}
