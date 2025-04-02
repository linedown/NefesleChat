package ru.linedown.nefeslechat.ui.self;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SelfViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public SelfViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Здесь будут ваши сообщения в избранном");
    }

    public LiveData<String> getText() {
        return mText;
    }
}