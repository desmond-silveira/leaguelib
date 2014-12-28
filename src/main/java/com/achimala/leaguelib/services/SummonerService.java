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

import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import com.achimala.leaguelib.connection.LeagueConnection;
import com.achimala.leaguelib.errors.LeagueErrorCode;
import com.achimala.leaguelib.errors.LeagueException;
import com.achimala.leaguelib.models.LeagueSummoner;
import com.achimala.leaguelib.models.LeagueSummonerProfileInfo;
import com.achimala.leaguelib.models.SummonerSpell;
import com.achimala.leaguelib.models.runes.RuneBook;
import com.achimala.util.Callback;
import com.gvaneyck.rtmp.encoding.TypedObject;

public class SummonerService extends LeagueAbstractService {
    public SummonerService(LeagueConnection connection) {
        super(connection);
    }

    @Override
    public String getServiceName() {
        return "summonerService";
    }

    private LeagueSummoner getSummonerFromResult(TypedObject obj, String name) throws LeagueException {
        if(obj.getTO("body") == null)
            throw new LeagueException(LeagueErrorCode.SUMMONER_NOT_FOUND, "Summoner " + name + " not found.", name);
        return new LeagueSummoner(obj.getTO("body"), getConnection().getServer());
    }

    private String[] getNamesFromResult(TypedObject obj) {
        Object[] names = obj.getArray("body");
        if(names == null)
            return null;
        return Arrays.copyOf(names, names.length, String[].class);
    }

    public String[] getSummonerNames(Object[] summonerIds) throws LeagueException {
        TypedObject obj = call("getSummonerNames", new Object[] { summonerIds });
        return getNamesFromResult(obj);
    }

    public void getSummonerNames(final Object[] summonerIds, final Callback<String[]> callback) {
        callAsynchronously("getSummonerNames", new Object[] { summonerIds }, new Callback<TypedObject>() {
            @Override
            public void onCompletion(TypedObject obj) {
                try {
                    callback.onCompletion(getNamesFromResult(obj));
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

    public String getSummonerName(int summonerId) throws LeagueException {
        String[] names = getSummonerNames(new Object[] { summonerId });
        if(names == null || names.length != 1)
            return null;
        return names[0];
    }

    public void getSummonerName(int summonerId, final Callback<String> callback) {
        getSummonerNames(new Object[] { summonerId }, new Callback<String[]>() {
            @Override
            public void onCompletion(String[] names) {
                if(names == null || names.length != 1)
                    callback.onCompletion(null);
                else
                    callback.onCompletion(names[0]);
            }
            @Override
            public void onError(Exception ex) {
                callback.onError(ex);
            }
        });
    }

    public LeagueSummoner getSummonerByName(String name) throws LeagueException {
        TypedObject obj = call("getSummonerByName", new Object[] { name });
        return getSummonerFromResult(obj, name);
    }

    public void getSummonerByName(final String name, final Callback<LeagueSummoner> callback) {
        callAsynchronously("getSummonerByName", new Object[] { name }, new Callback<TypedObject>() {
            @Override
            public void onCompletion(TypedObject obj) {
                try {
                    callback.onCompletion(getSummonerFromResult(obj, name));
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

    public Future<LeagueSummoner> getSummonerByName(final String name, ExecutorService executor) {
        return executor.submit(new Callable<LeagueSummoner>() {
            @Override
            public LeagueSummoner call() throws Exception {
                return getSummonerByName(name);
            }
        });
    }

    /**
     * Fills {@code LeagueSummoner} with internalName, accountId, name,
     * profileIcon, summonerId, and summonerLevel, and runes.  Also available
     * are experience, IP, mastery points, season history, and default summoner
     * spells per game type.
     *
     * @param summoner {@code LeagueSummoner} with account id
     * @throws LeagueException
     */
    public void fillPublicSummonerData(LeagueSummoner summoner) throws LeagueException {
        TypedObject obj = call("getAllPublicSummonerDataByAccount", new Object[] { summoner.getAccountId() });
        TypedObject body = obj.getTO("body");
        summoner.setProfileInfo(new LeagueSummonerProfileInfo(body));
        summoner.setRuneBook(new RuneBook(body));
        TypedObject summonerSpells = body.getTO("summonerDefaultSpells")
                .getTO("summonerDefaultSpellMap").getTO("CLASSIC");
        summoner.setSummonerSpells(
                SummonerSpell.getSpellById(summonerSpells.getInt("spell1Id")),
                SummonerSpell.getSpellById(summonerSpells.getInt("spell2Id")));
        summoner.setLevel(body.getTO("summonerLevel").getInt("summonerLevel"));
    }

    public void fillPublicSummonerData(final LeagueSummoner summoner, final Callback<LeagueSummoner> callback) {
        callAsynchronously("getAllPublicSummonerDataByAccount", new Object[] { summoner.getAccountId() }, new Callback<TypedObject>() {
            @Override
            public void onCompletion(TypedObject obj) {
                try {
                    TypedObject body = obj.getTO("body");
                    summoner.setProfileInfo(new LeagueSummonerProfileInfo(body));
                    summoner.setRuneBook(new RuneBook(body));
                    TypedObject summonerSpells = body.getTO("summonerDefaultSpells")
                            .getTO("summonerDefaultSpellMap").getTO("CLASSIC");
                    summoner.setSummonerSpells(
                            SummonerSpell.getSpellById(summonerSpells.getInt("spell1Id")),
                            SummonerSpell.getSpellById(summonerSpells.getInt("spell2Id")));
                    summoner.setLevel(body.getTO("summonerLevel").getInt("summonerLevel"));
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

    public Future<LeagueSummoner> fillPublicSummonerData(final LeagueSummoner summoner,
            ExecutorService executor) {
        return executor.submit(new Callable<LeagueSummoner>() {
            @Override
            public LeagueSummoner call() throws Exception {
                fillPublicSummonerData(summoner);
                return summoner;
            }
        });
    }
}