/*
 *  This file is part of LeagueLib.
 *  LeagueLib is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  LeagueLib is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with LeagueLib.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.achimala.leaguelib.models;

import com.achimala.leaguelib.models.masteries.MasteryBook;
import com.achimala.leaguelib.models.runes.RuneBook;
import com.gvaneyck.rtmp.ServerInfo;
import com.gvaneyck.rtmp.encoding.TypedObject;

import java.util.List;
import java.util.Comparator;
import java.util.Collections;
import java.util.Map;

public class LeagueSummoner {
    private int _id=-1, _accountId=-1;
    private int _profileIconId=0, _level=0;
    private String _name, _internalName;
    private boolean _isBot = false;
    private ServerInfo _server;
    private LeagueSummonerProfileInfo _profileInfo;
    private LeagueSummonerLeagueStats _leagueStats;
    private LeagueSummonerRankedStats _rankedStats;
    private LeagueSummonerPlayerStats _playerStats;
    private List<MatchHistoryEntry> _matchHistory;
    private MasteryBook _masteryBook;
    private RuneBook _runeBook;
    private LeagueGame _activeGame;
    private SummonerSpell _summonerSpell1,_summonerSpell2;
    private int _lastSelectedSkin = 0;
    private double _teamParticipantId;
    private Map<LeagueHonorType, Integer> _honorValues;

    public LeagueSummoner() {
        _profileInfo = new LeagueSummonerProfileInfo();
    }

    // public LeagueSummoner(int id, String name) {
    //     this();
    //     _id = id;
    //     _name = name;
    // }

    public LeagueSummoner(TypedObject obj, ServerInfo server) {
        this(obj, server, false);
    }

    // The isGamePlayer flag exists because Riot uses the key accountId when the summoner is in a Game DTO
    // (when it's returned from gameService.retrieveInProgressSpectatorGameInfo)
    // But when it's returned via summonerService it's called acctId
    public LeagueSummoner(TypedObject obj, ServerInfo server, boolean isGamePlayer) {
        this();
        if(isGamePlayer) {
            _isBot = obj.type.equals("com.riotgames.platform.game.BotParticipant");
            this.setTeamParticipantId(obj.getDouble("teamParticipantId"));
        }
        _name = obj.getString(isGamePlayer ? "summonerName" : "name");
        _internalName = obj.getString(isGamePlayer ? "summonerInternalName" : "internalName");
        if(!_isBot) {
            _id = obj.getInt("summonerId");
            _accountId = obj.getInt(isGamePlayer ? "accountId" : "acctId");
            _profileIconId = obj.getInt("profileIconId");
            _server = server;
            if(!isGamePlayer)
                _level = obj.getInt("summonerLevel");
        }
    }

    public void setId(int id) {
        _id = id;
    }

    public void setAccountId(int id) {
        _accountId = id;
    }

    public void setTeamParticipantId(Double id) {
        if (id == null) {
            id = -1.0;
        }
        _teamParticipantId = id;
    }
    public void setName(String name) {
        _name = name;
    }

    public void setInternalName(String name) {
        _internalName = name;
    }

    public void setProfileIconId(int id) {
        _profileIconId = id;
    }

    public void setLevel(int level) {
        _level = level;
    }

    public void setProfileInfo(LeagueSummonerProfileInfo info) {
        _profileInfo = info;
    }

    public void setLeagueStats(LeagueSummonerLeagueStats stats) {
        _leagueStats = stats;
    }

    public void setRankedStats(LeagueSummonerRankedStats stats) {
        _rankedStats = stats;
    }

    public void setPlayerStats(LeagueSummonerPlayerStats stats) {
        _playerStats = stats;
    }

    public void setMatchHistory(List<MatchHistoryEntry> matchHistory) {
        _matchHistory = matchHistory;
        Collections.sort(_matchHistory, new Comparator<MatchHistoryEntry>() {
            @Override
            public int compare(MatchHistoryEntry match1, MatchHistoryEntry match2) {
                return match1.getCreationDate().compareTo(match2.getCreationDate());
            }
        });
    }

    public void setActiveGame(LeagueGame game) {
        _activeGame = game;
    }

    public void setIsBot(boolean bot) {
        _isBot = bot;
    }

    public void setServer(ServerInfo server) {
        _server = server;
    }

    public int getId() {
        return _id;
    }

    public int getAccountId() {
        return _accountId;
    }

    public double getTeamParticipantId() {
        return _teamParticipantId;
    }

    public String getName() {
        return _name;
    }

    public String getInternalName() {
        return _internalName;
    }

    public int getProfileIconId() {
        return _profileIconId;
    }

    public int getLevel() {
        return _level;
    }

    public LeagueSummonerProfileInfo getProfileInfo() {
        return _profileInfo;
    }

    public LeagueSummonerLeagueStats getLeagueStats() {
        return _leagueStats;
    }

    public LeagueSummonerRankedStats getRankedStats() {
        return _rankedStats;
    }

    public LeagueSummonerPlayerStats getPlayerStats() {
        return _playerStats;
    }

    public List<MatchHistoryEntry> getMatchHistory() {
        return _matchHistory;
    }

    public MatchHistoryEntry getMostRecentMatch() {
        if(_matchHistory != null && _matchHistory.size() > 0)
            return _matchHistory.get(0);
        return null;
    }

    public LeagueGame getActiveGame() {
        return _activeGame;
    }

    public boolean isBot() {
        return _isBot;
    }

    public ServerInfo getServer() {
        return _server;
    }

    @Override
    public String toString() {
        return "<Summoner " + _name + " (#" + _id + ")>";
    }

    public boolean isEqual(Object other) {
        return (other instanceof LeagueSummoner && ((LeagueSummoner)other).getId() == _id);
    }

    public void setMasteryBook(MasteryBook book) {
        _masteryBook = book;
    }

    public MasteryBook getMasteryBook() {
        return _masteryBook;
    }

    public void setRuneBook(RuneBook book) {
        _runeBook = book;
    }

    public RuneBook getRuneBook() {
        return _runeBook;
    }

    public void setSummonerSpells(SummonerSpell s1, SummonerSpell s2) {
        _summonerSpell1 = s1;
        _summonerSpell2 = s2;
    }

    public SummonerSpell getSummonerSpell1() {
        return _summonerSpell1;
    }

    public SummonerSpell getSummonerSpell2() {
        return _summonerSpell2;
    }

    public void setLastSelectedSkin(int skin) {
        _lastSelectedSkin = skin;
    }

    public int getLastSelectedSkin() {
        return _lastSelectedSkin;
    }

    public Map<LeagueHonorType, Integer> getHonorValues() {
        return _honorValues;
    }

    public void setHonorValues(Map<LeagueHonorType, Integer> _honorValues) {
        this._honorValues = _honorValues;
    }
}