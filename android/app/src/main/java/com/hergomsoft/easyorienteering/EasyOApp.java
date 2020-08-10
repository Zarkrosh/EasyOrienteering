package com.hergomsoft.easyorienteering;

import android.app.Application;

import androidx.multidex.MultiDexApplication;

import com.facebook.stetho.Stetho;

public class EasyOApp extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        setupStetho();
    }

    // DEBUG
    private void setupStetho() {
        // Create an InitializerBuilder
        Stetho.InitializerBuilder initializerBuilder = Stetho.newInitializerBuilder(this);
        // Enable Chrome DevTools
        initializerBuilder.enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this));
        // Enable command line interface
        initializerBuilder.enableDumpapp(Stetho.defaultDumperPluginsProvider(this));
        // Use the InitializerBuilder to generate an Initializer
        Stetho.Initializer initializer = initializerBuilder.build();
        // Initialize Stetho with the Initializer
        Stetho.initialize(initializer);
    }
}
