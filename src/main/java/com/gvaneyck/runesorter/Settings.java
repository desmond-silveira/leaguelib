package com.gvaneyck.runesorter;

import com.gvaneyck.rtmp.ServerInfo;

public class Settings {
    public String username;
    public String password;
    public String version;
    public ServerInfo region;
    
    public Settings(String username, String password, String version, ServerInfo region) {
        this.username = username;
        this.password = password;
        this.version = version;
        this.region = region;
    }
}
