package fi.samppa.client;

import java.util.UUID;

public class PasswordData {
    public String password, username, site;
    public UUID uuid;

    public PasswordData(String password, String username, String site, UUID uuid) {
        this.password = password;
        this.username = username;
        this.site = site;
        this.uuid = uuid;
    }
}
