package com.leelen.cloud;

import com.leelen.cloud.annotations.EnableTimerWheel;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableTimerWheel
public class TimerWheelApplication {

    public static void main(String[] args) {
        SpringApplication.run(TimerWheelApplication.class, args);
    }

}
