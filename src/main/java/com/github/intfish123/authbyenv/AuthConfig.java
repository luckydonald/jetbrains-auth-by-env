package com.github.intfish123.authbyenv;

import java.util.Objects;

public class AuthConfig {
    private String usernameKey;
    private String passwordKey;

    public AuthConfig(String usernameKey, String passwordKey) {
        this.usernameKey = usernameKey;
        this.passwordKey = passwordKey;
    }

    public String getUsernameKey() {
        return usernameKey;
    }

    public void setUsernameKey(String usernameKey) {
        this.usernameKey = usernameKey;
    }

    public String getPasswordKey() {
        return passwordKey;
    }

    public void setPasswordKey(String passwordKey) {
        this.passwordKey = passwordKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AuthConfig)) return false;
        AuthConfig that = (AuthConfig) o;
        return usernameKey.equals(that.usernameKey) && passwordKey.equals(that.passwordKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(usernameKey, passwordKey);
    }
}
