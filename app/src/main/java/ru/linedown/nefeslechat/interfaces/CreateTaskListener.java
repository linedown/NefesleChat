package ru.linedown.nefeslechat.interfaces;

public interface CreateTaskListener {
    void onApplyCreate(String taskText);
    void onCancelCreate();
}
