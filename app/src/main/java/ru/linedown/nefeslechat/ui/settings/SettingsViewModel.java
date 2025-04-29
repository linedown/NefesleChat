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
        private String groupOrAcademicTitle;
        private String academicDegree;

        public SettingsData(String fio, String status, String role, String mail, String groupOrAcademicTitle){
            this.fio = fio;
            this.status = status;
            this.role = role;
            this.mail = mail;
            this.groupOrAcademicTitle = groupOrAcademicTitle;
        }

        public SettingsData(String fio, String status, String role, String mail, String groupOrAcademicTitle, String academicDegree){
            this.fio = fio;
            this.status = status;
            this.role = role;
            this.mail = mail;
            this.groupOrAcademicTitle = groupOrAcademicTitle;
            this.academicDegree = academicDegree;
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

        public String getAcademicDegree() {
            return academicDegree;
        }

        public void setAcademicDegree(String academicDegree) {
            this.academicDegree = academicDegree;
        }

        public String getGroupOrAcademicTitle() {
            return groupOrAcademicTitle;
        }

        public void setGroupOrAcademicTitle(String groupOrAcademicTitle) {
            this.groupOrAcademicTitle = groupOrAcademicTitle;
        }

        public void setRole(String role) {
            this.role = role;
        }
    }

    public SettingsViewModel() {
        mSettings = new MutableLiveData<>();
    }

    public LiveData<SettingsData> getSettings() {
        return mSettings;
    }

    public void updateSettings(SettingsData newSettings) {
        mSettings.setValue(newSettings);
    }

}