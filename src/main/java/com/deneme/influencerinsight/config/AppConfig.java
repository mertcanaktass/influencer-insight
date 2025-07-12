package com.deneme.influencerinsight.config;

import lombok.NonNull;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

public class AppConfig implements ApplicationListener<ApplicationEvent> {

    @Override
    public void onApplicationEvent(@NonNull ApplicationEvent event) {

    }

    @Override
    public boolean supportsAsyncExecution() {
        return ApplicationListener.super.supportsAsyncExecution();
    }
}
