package com.example.promdemo;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Gauge;
import io.prometheus.client.exporter.PushGateway;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class App {

    public static void main(String[] args) throws Exception {
        CollectorRegistry registry = new CollectorRegistry();
        Gauge duration = Gauge.build()
                .name("my_batch_job_duration_seconds")
                .help("Duration of my batch job in seconds.")
                .register(registry);
        Gauge.Timer durationTimer = duration.startTimer();
        try {
            int d = new Random().nextInt(10);
            System.out.println("sleep "+ String.valueOf(d));
            TimeUnit.SECONDS.sleep(d);
            Gauge lastSuccess = Gauge.build()
                    .name("my_batch_job_last_success")
                    .help("Last time my batch job succeeded, in unixtime.")
                    .register(registry);
            lastSuccess.setToCurrentTime();
        } catch (Exception e) {

        } finally {
            durationTimer.setDuration();
            PushGateway pg = new PushGateway("192.168.1.254:9091");
            pg.pushAdd(registry, "my_batch_job");
        }

    }
}
