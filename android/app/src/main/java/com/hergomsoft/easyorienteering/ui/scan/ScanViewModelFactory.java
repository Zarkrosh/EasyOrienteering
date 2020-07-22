package com.hergomsoft.easyorienteering.ui.scan;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class ScanViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    @NonNull
    private final Application application;

    public ScanViewModelFactory(@NonNull Application application) {
        this.application = application;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass == ScanViewModel.class) {
            return (T) new ScanViewModel(application);
        }
        return null;
    }
}
