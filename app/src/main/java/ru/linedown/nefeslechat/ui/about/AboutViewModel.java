package ru.linedown.nefeslechat.ui.about;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AboutViewModel extends ViewModel {
    private final MutableLiveData<String> mText;

    public AboutViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Здесь будет информация о мессенджере");
    }

    public LiveData<String> getText() {
        return mText;
    }
}