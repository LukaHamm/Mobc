package app.thecity.model;

import java.io.Serializable;

public class DeviceInfo implements Serializable {
    // Gerätename
    private String device;
    // E-Mail-Adresse des Benutzers
    private String email;
    // App-Version
    private String version;
    // Firebase Cloud Messaging-Registrierungs-ID
    private String regid;
    // Datum und Uhrzeit der Erstellung des Geräteinformationen-Eintrags
    private long date_create;

    public DeviceInfo() {
    }

    public DeviceInfo(String device, String email, String version, String regid, long date_create) {
        this.device = device;
        this.email = email;
        this.version = version;
        this.regid = regid;
        this.date_create = date_create;
    }

    // Getter und Setter für die privaten Variablen
    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getRegid() {
        return regid;
    }

    public void setRegid(String regid) {
        this.regid = regid;
    }

    public long getDate_create() {
        return date_create;
    }

    public void setDate_create(long date_create) {
        this.date_create = date_create;
    }
}
