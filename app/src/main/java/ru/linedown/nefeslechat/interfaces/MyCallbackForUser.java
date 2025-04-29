package ru.linedown.nefeslechat.interfaces;

import ru.linedown.nefeslechat.classes.UserDetailsDTO;

public interface MyCallbackForUser {
    void onSuccess(UserDetailsDTO result);
    void onError(String errorMessage);
}
