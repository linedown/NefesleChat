package ru.linedown.nefeslechat.ui.search;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SearchViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public SearchViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Здесь будет поиск людей в месседжере");
    }

    public LiveData<String> getText() {
        return mText;
    }
}