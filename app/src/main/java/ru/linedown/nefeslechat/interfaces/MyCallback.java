package ru.linedown.nefeslechat.interfaces;

public interface MyCallback<T> {
    void onSuccess(T result);
    void onError(String errorMessage);
}
