package cc.fyre.proton.pidgin;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class PidginUser {

    @Getter private String host;
    @Getter private Integer port;
    @Getter private String password;


    public boolean requiresAuth() {
        return this.password != null && !this.password.equals("");
    }
}
