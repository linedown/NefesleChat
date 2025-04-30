package ru.linedown.nefeslechat.interfaces;

import java.util.List;

import ru.linedown.nefeslechat.classes.UserInListDTO;

public interface MyCallbackForUserList {
    void onSuccess(List<UserInListDTO> result);
    void onError(String errorMessage);
}
