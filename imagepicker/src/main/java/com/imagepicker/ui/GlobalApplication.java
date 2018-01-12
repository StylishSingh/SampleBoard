package com.imagepicker.ui;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.imagepicker.dagger.component.AppComponent;
import com.imagepicker.dagger.component.DaggerAppComponent;
import com.imagepicker.dagger.module.ApplicationModule;

/**
 * author by Anuj Sharma on 9/27/2017.
 */

public class GlobalApplication extends Application {

    private static AppComponent appComponent;


    @Override
    public void onCreate() {
        super.onCreate();
        appComponent = DaggerAppComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();
        appComponent.inject(this);

//        Fresco.initialize(this);
        ImagePipelineConfig config = ImagePipelineConfig.newBuilder(this)
                .setDownsampleEnabled(true)
                .build();
        Fresco.initialize(this, config);

    }

    public AppComponent getAppComponent() {
        return appComponent;
    }
}
