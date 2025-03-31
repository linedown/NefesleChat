package ru.linedown.nefeslechat.ui.settings;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SettingsViewModel extends ViewModel {

    private final MutableLiveData<SettingsData> mSettings;

    public static class SettingsData{
        private String fio;
        private String status;
        private String phoneStr;
        private String mail;

        public SettingsData(String fio, String status, String phoneStr, String mail){
            this.fio = fio;
            this.status = status;
            this.phoneStr = phoneStr;
            this.mail = mail;
        }

        public String getMail() {
            return mail;
        }

        public void setMail(String mail) {
            this.mail = mail;
        }

        public String getPhoneStr() {
            return phoneStr;
        }

        public void setPhoneStr(String phoneStr) {
            this.phoneStr = phoneStr;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getFio() {
            return fio;
        }

        public void setFio(String fio) {
            this.fio = fio;
        }
    }

    public SettingsViewModel() {
        mSettings = new MutableLiveData<>();
        mSettings.setValue(new SettingsData("Тестовый Пример Фио", "В сети", "+7 913 567 87 53", "test1006@gmail.com"));
    }

    public LiveData<SettingsData> getSettings() {
        return mSettings;
    }

    public void updateSettings(SettingsData newSettings) {
        mSettings.setValue(newSettings);
    }

}