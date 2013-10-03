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

import com.gvaneyck.rtmp.TypedObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Date;

public class MatchHistoryEntry implements PlayerList {
    int _gameId;
    private String _gameType;
    private int _summonerSpell1;
    private int _summonerSpell2;
    private boolean _leaver;
    private boolean _afk;
    private boolean _premadeTeam;
    private Date _createDate;
    private LeagueMatchmakingQueue _queue;
    private List<LeagueSummoner> _playerTeam, _enemyTeam;
    private Map<Integer, LeagueChampion> _playerChampionSelections;
    private Map<MatchHistoryStatType, Integer> _stats;

    public MatchHistoryEntry() {
    }

    public MatchHistoryEntry(TypedObject obj, LeagueSummoner primarySummoner) {
        _gameId = obj.getInt("gameId");
        _gameType = obj.getString("gameType");
        _leaver = obj.getBool("leaver");
        _afk = obj.getBool("afk");
        _premadeTeam = obj.getBool("premadeTeam");
        _summonerSpell1 = obj.getInt("spell1");
        _summonerSpell2 = obj.getInt("spell2");
        _createDate = (Date)obj.get("createDate");
        _queue = LeagueMatchmakingQueue.valueOf(obj.getString("queueType"));

        _playerTeam = new ArrayList<LeagueSummoner>();
        _enemyTeam = new ArrayList<LeagueSummoner>();
        _playerChampionSelections = new HashMap<Integer, LeagueChampion>();
        _stats = new HashMap<MatchHistoryStatType, Integer>();

        // (for some unknown reason, sometimes the "summonerId" key is 0 in the data returned from Riot)
        // This is the only reason we have to pass the primary summoner into this constructor
        // ...which is pretty dumb
        _playerChampionSelections.put(primarySummoner.getId(), LeagueChampion.getChampionWithId(obj.getInt("championId")));

        int playerTeamId = obj.getInt("teamId");

        // Riot doesn't include this person in the "fellow players" list, which I suppose makes sense
        _playerTeam.add(primarySummoner);
        for(Object playerObj : obj.getArray("fellowPlayers")) {
            TypedObject player = (TypedObject)playerObj;
            LeagueSummoner summoner = new LeagueSummoner();
            summoner.setId(player.getInt("summonerId"));
            _playerChampionSelections.put(summoner.getId(), LeagueChampion.getChampionWithId(player.getInt("championId")));
            if(player.getInt("teamId") == playerTeamId)
                _playerTeam.add(summoner);
            else
                _enemyTeam.add(summoner);
        }

        for(Object statObj : obj.getArray("statistics")) {
            TypedObject stat = (TypedObject)statObj;
            MatchHistoryStatType type = MatchHistoryStatType.valueOf(stat.getString("statType"));
            _stats.put(type, stat.getInt("value"));
        }
    }

    public void setGameId(int id) {
        _gameId = id;
    }

    public void setGameType(String type) {
        _gameType = type;
    }

    public void setIsLeaver(boolean leaver) {
        _leaver = leaver;
    }

    public void setIsAfk(boolean afk) {
        _afk = afk;
    }

    public void setPremadeTeam(boolean premadeTeam) {
        _premadeTeam = premadeTeam;
    }

    public void setCreationDate(Date date) {
        _createDate = date;
    }

    public void setQueue(LeagueMatchmakingQueue queue) {
        _queue = queue;
    }

    public int getGameId() {
        return _gameId;
    }

    public String getGameType() {
        return _gameType;
    }

    public boolean isLeaver() {
        return _leaver;
    }

    public boolean isAfk() {
        return _afk;
    }

    public boolean isPremadeTeam() {
        return _premadeTeam;
    }

    public int getSummonerSpell2() {
        return _summonerSpell2;
    }

    public int getSummonerSpell1() {
        return _summonerSpell1;
    }

    public Date getCreationDate() {
        return _createDate;
    }

    public LeagueMatchmakingQueue getQueue() {
        return _queue;
    }

    @Override
    public List<LeagueSummoner> getPlayerTeam() {
        return _playerTeam;
    }

    @Override
    public List<LeagueSummoner> getEnemyTeam() {
        return _enemyTeam;
    }

    @Override
    public List<LeagueSummoner> getAllPlayers() {
        List<LeagueSummoner> players = new ArrayList<LeagueSummoner>(_playerTeam);
        players.addAll(_enemyTeam);
        return players;
    }

    @Override
    public LeagueChampion getChampionSelectionForSummoner(LeagueSummoner summoner) {
        return _playerChampionSelections.get(summoner.getId());
    }

    public int getStat(MatchHistoryStatType type) {
        Integer stat = _stats.get(type);
        if(stat == null)
            return 0;
        return stat.intValue();
    }

    public Map<MatchHistoryStatType, Integer> getAllStats() {
        return _stats;
    }
}