package ru.linedown.nefeslechat.ui.chats;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ChatsViewModel extends ViewModel {
    private final MutableLiveData<String> mText;

    public ChatsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Здесь будет список чатов");
    }

    public LiveData<String> getText() {
        return mText;
    }
}