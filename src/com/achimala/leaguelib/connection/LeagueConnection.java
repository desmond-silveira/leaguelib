package com.achimala.leaguelib.connection;

import com.gvaneyck.rtmp.LoLRTMPSClient;
import com.achimala.leaguelib.services.*;
import com.achimala.leaguelib.errors.*;
import java.io.IOException;

public class LeagueConnection {
    private LeagueServer _server=null;
    private LoLRTMPSClient _rtmpClient=null;
    private SummonerService _summonerService=null;
    private LeaguesService _leaguesService=null;
    
    public LeagueConnection(LeagueServer server) {
        _server = server;
    }
    
    public void setCredentials(String username, String password, String clientVersion) {
        if(_rtmpClient != null) {
            if(_rtmpClient.isConnected())
                _rtmpClient.close();
        }
        _rtmpClient = new LoLRTMPSClient(_server.getServerCode(), clientVersion, username, password);
    }
    
    public void connect() throws LeagueException {
        if(_rtmpClient == null)
            throw new LeagueException(LeagueErrorCode.AUTHENTICATION_ERROR, "Missing authentication credentials for connection to server " + _server);
        try {
            if(_rtmpClient.isConnected()) {
                if(_rtmpClient.isLoggedIn())
                    return;
                else
                    _rtmpClient.login();
            } else
                _rtmpClient.connectAndLogin();
        } catch(IOException ex) {
            throw new LeagueException(LeagueErrorCode.NETWORK_ERROR, ex.getMessage());
        }
    }
    
    public boolean isConnected() {
        return (_rtmpClient == null || _rtmpClient.isLoggedIn());
    }
    
    public String toString() {
        return String.format("<LeagueConnection:%s (%s)>", _server.getServerCode(), isConnected() ? "Online" : "Offline");
    }
    
    public LoLRTMPSClient getInternalRTMPClient() {
        return _rtmpClient;
    }
    
    //// Services
    
    public SummonerService getSummonerService() {
        if(_summonerService == null)
            _summonerService = new SummonerService(this);
        return _summonerService;
    }
    
    public LeaguesService getLeaguesService() {
        if(_leaguesService == null)
            _leaguesService = new LeaguesService(this);
        return _leaguesService;
    }
}