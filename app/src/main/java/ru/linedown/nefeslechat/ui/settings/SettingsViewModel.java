package ru.linedown.nefeslechat.ui.settings;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SettingsViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public SettingsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Здесь в будущем будут настройки мессенджера");
    }

    public LiveData<String> getText() {
        return mText;
    }
}