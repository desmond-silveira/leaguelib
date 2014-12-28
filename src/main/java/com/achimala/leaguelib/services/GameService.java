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

import com.achimala.leaguelib.connection.LeagueConnection;
import com.achimala.leaguelib.errors.LeagueErrorCode;
import com.achimala.leaguelib.errors.LeagueException;
import com.achimala.leaguelib.models.LeagueGame;
import com.achimala.leaguelib.models.LeagueSummoner;
import com.achimala.util.Callback;
import com.gvaneyck.rtmp.encoding.TypedObject;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class GameService extends LeagueAbstractService {
    public GameService(LeagueConnection connection) {
        super(connection);
    }

    @Override
    public String getServiceName() {
        return "gameService";
    }

    // FIXME: Not sure if this is the right way to handle this
    @Override
    protected TypedObject handleResult(TypedObject result) throws LeagueException {
        if(result.get("result").equals("_error")) {
            String reason = result.toPrettyString();
            LeagueErrorCode code = LeagueErrorCode.getErrorCodeForException(reason);
            if(code == LeagueErrorCode.ACTIVE_GAME_NOT_FOUND || code == LeagueErrorCode.ACTIVE_GAME_NOT_SPECTATABLE)
                return null;
        }
        return super.handleResult(result);
    }

    private void createAndSetGame(LeagueSummoner summoner, TypedObject obj) {
        if(obj == null || obj.getTO("body") == null)
            summoner.setActiveGame(null);
        else {
            LeagueGame game = new LeagueGame(obj.getTO("body"), summoner);
            summoner.setActiveGame(game);
        }
    }

    /**
     * Fills {@code LeagueSummoner} with active game data.
     *
     * @param summoner {@code LeagueSummoner} with internal name
     * @throws LeagueException
     */
    public void fillActiveGameData(LeagueSummoner summoner) throws LeagueException {
        TypedObject obj = call("retrieveInProgressSpectatorGameInfo", new Object[] { summoner.getInternalName() });
        createAndSetGame(summoner, obj);
    }

    public void fillActiveGameData(final LeagueSummoner summoner, final Callback<LeagueSummoner> callback) {
        callAsynchronously("retrieveInProgressSpectatorGameInfo", new Object[] { summoner.getInternalName() }, new Callback<TypedObject>() {
            @Override
            public void onCompletion(TypedObject obj) {
                createAndSetGame(summoner, obj);
                callback.onCompletion(summoner);
            }

            @Override
            public void onError(Exception ex) {
                callback.onError(ex);
            }
        });
    }

    public Future<LeagueSummoner> fillActiveGameData(final LeagueSummoner summoner,
            ExecutorService executor) {
        return executor.submit(new Callable<LeagueSummoner>() {
            @Override
            public LeagueSummoner call() throws Exception {
                fillActiveGameData(summoner);
                return summoner;
            }
        });
    }
}