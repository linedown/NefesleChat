package ru.linedown.nefeslechat.ui.createchat;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CreateChatViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public CreateChatViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Здесь будет создание чата");
    }

    public LiveData<String> getText() {
        return mText;
    }
}