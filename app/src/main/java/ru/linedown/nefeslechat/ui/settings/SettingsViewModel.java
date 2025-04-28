package ru.linedown.nefeslechat.ui.settings;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SettingsViewModel extends ViewModel {

    private final MutableLiveData<SettingsData> mSettings;

    public static class SettingsData{
        private String fio;
        private String status;
        private String role;
        private String mail;

        public SettingsData(String fio, String status, String role, String mail){
            this.fio = fio;
            this.status = status;
            this.role = role;
            this.mail = mail;
        }

        public String getMail() {
            return mail;
        }

        public void setMail(String mail) {
            this.mail = mail;
        }

        public String getRole() {
            return role;
        }

        public void setPhoneStr(String role) {
            this.role = role;
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
        mSettings.setValue(new SettingsData("Тестовый Пример Фио", "В сети", "Шаблонная роль", "test1006@gmail.com"));
    }

    public LiveData<SettingsData> getSettings() {
        return mSettings;
    }

    public void updateSettings(SettingsData newSettings) {
        mSettings.setValue(newSettings);
    }

}