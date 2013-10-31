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


package com.achimala.leaguelib.services;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import com.achimala.leaguelib.connection.LeagueConnection;
import com.achimala.leaguelib.errors.LeagueException;
import com.achimala.leaguelib.models.LeagueCompetitiveSeason;
import com.achimala.leaguelib.models.LeagueRankedStatType;
import com.achimala.leaguelib.models.LeagueSummoner;
import com.achimala.leaguelib.models.LeagueSummonerPlayerStats;
import com.achimala.leaguelib.models.LeagueSummonerRankedStats;
import com.achimala.leaguelib.models.MatchHistoryEntry;
import com.achimala.util.Callback;
import com.gvaneyck.rtmp.TypedObject;

public class PlayerStatsService extends LeagueAbstractService {
    private final String SUMMONERS_RIFT = "CLASSIC";

    public PlayerStatsService(LeagueConnection connection) {
        super(connection);
    }

    @Override
    public String getServiceName() {
        return "playerStatsService";
    }

    public void fillPlayerStats(LeagueSummoner summoner) throws LeagueException {
        TypedObject obj = call(
                "retrievePlayerStatsByAccountId",
                new Object[] { summoner.getAccountId(), 3 });
        summoner.setPlayerStats(new LeagueSummonerPlayerStats(obj.getTO("body")));
    }

    public void fillPlayerStats(final LeagueSummoner summoner,
            final Callback<LeagueSummoner> callback) {
        callAsynchronously("retrievePlayerStatsByAccountId", new Object[] {
                summoner.getAccountId(), 3 },
                new Callback<TypedObject>() {
                    public void onCompletion(TypedObject obj) {
                        try {
                            summoner.setPlayerStats(new LeagueSummonerPlayerStats(obj.getTO("body")));
                            callback.onCompletion(summoner);
                        } catch (Exception ex) {
                            callback.onError(ex);
                        }
                    }

                    public void onError(Exception ex) {
                        callback.onError(ex);
                    }
                });
    }

    /**
     * Fills {@code LeagueSummoner} with ranked game statistics grouped by
     * champion.
     *
     * @param summoner
     * @throws LeagueException
     * @see {@link LeagueRankedStatType}
     */
    public void fillRankedStats(LeagueSummoner summoner) throws LeagueException {
        TypedObject obj = call("getAggregatedStats", new Object[] { summoner.getAccountId(), SUMMONERS_RIFT, 3 });
        summoner.setRankedStats(new LeagueSummonerRankedStats(obj.getTO("body")));
    }

    public void fillRankedStats(final LeagueSummoner summoner, final Callback<LeagueSummoner> callback) {
        callAsynchronously("getAggregatedStats", new Object[] { summoner.getAccountId(), SUMMONERS_RIFT, 3 }, new Callback<TypedObject>() {
            @Override
            public void onCompletion(TypedObject obj) {
                try {
                    summoner.setRankedStats(new LeagueSummonerRankedStats(obj.getTO("body")));
                    callback.onCompletion(summoner);
                } catch(Exception ex) {
                    callback.onError(ex);
                }
            }

            @Override
            public void onError(Exception ex) {
                callback.onError(ex);
            }
        });
    }

    public Future<LeagueSummoner> fillRankedStats(final LeagueSummoner summoner,
            ExecutorService executor) {
        return executor.submit(new Callable<LeagueSummoner>() {
            @Override
            public LeagueSummoner call() throws Exception {
                fillRankedStats(summoner);
                return summoner;
            }
        });
    }

    private List<MatchHistoryEntry> getMatchHistoryEntriesFromResult(TypedObject obj, LeagueSummoner summoner) {
        Object[] games = obj.getTO("body").getArray("gameStatistics");
        if(games == null || games.length == 0)
            // No match history available; return an empty list
            return new ArrayList<MatchHistoryEntry>();
        List<MatchHistoryEntry> recentGames = new ArrayList<MatchHistoryEntry>();
        for(Object game : games)
            recentGames.add(new MatchHistoryEntry((TypedObject)game, summoner));
        return recentGames;
    }

    public void fillMatchHistory(LeagueSummoner summoner) throws LeagueException {
        // IMPORTANT: Riot doesn't provide the summoner names of fellow players, only IDs
        // This means that after calling fillMatchHistory, the match history of the summoner is populated
        // but each match history entry's players only have valid IDs!
        // You have to call SummonerService->getSummonerNames to batch resolve the IDs to names
        // TODO: Automate this process somehow...
        TypedObject obj = call("getRecentGames", new Object[] { summoner.getAccountId() });
        summoner.setMatchHistory(getMatchHistoryEntriesFromResult(obj, summoner));
    }

    public void fillMatchHistory(final LeagueSummoner summoner, final Callback<LeagueSummoner> callback) {
        callAsynchronously("getRecentGames", new Object[] { summoner.getAccountId() }, new Callback<TypedObject>() {
            @Override
            public void onCompletion(TypedObject obj) {
                summoner.setMatchHistory(getMatchHistoryEntriesFromResult(obj, summoner));
                callback.onCompletion(summoner);
            }
            @Override
            public void onError(Exception ex) {
                callback.onError(ex);
            }
        });
    }

    public Future<LeagueSummoner> fillMatchHistroy(final LeagueSummoner summoner,
            ExecutorService executor) {
        return executor.submit(new Callable<LeagueSummoner>() {
            @Override
            public LeagueSummoner call() throws Exception {
                fillMatchHistory(summoner);
                return summoner;
            }
        });
    }
}