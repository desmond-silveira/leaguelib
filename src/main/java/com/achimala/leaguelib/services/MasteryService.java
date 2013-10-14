package com.achimala.leaguelib.services;

import com.achimala.leaguelib.connection.LeagueConnection;
import com.achimala.leaguelib.errors.LeagueException;
import com.achimala.leaguelib.models.LeagueSummoner;
import com.achimala.leaguelib.models.masteries.MasteryBook;
import com.achimala.util.Callback;
import com.gvaneyck.rtmp.TypedObject;

/**
 * This service is an addition to the existing services provided by leage lib.
 * @author Jerrod Lankford
 *
 */
public class MasteryService extends LeagueAbstractService {

    public MasteryService(LeagueConnection connection) {
        super(connection);
    }

    @Override
    public String getServiceName() {
        return "masteryBookService";
    }

    public void fillMasteryBook(LeagueSummoner summoner) throws LeagueException
    {
        TypedObject obj = call("getMasteryBook", new Object[] { summoner.getId() });
        summoner.setMasteryBook(new MasteryBook(obj.getTO("body")));

    }

    public void fillMasteryBook(final LeagueSummoner summoner, final Callback<LeagueSummoner> callback)
    {
        callAsynchronously("getMasteryBook", new Object[] { summoner.getId()}, new Callback<TypedObject>(){

            @Override
            public void onCompletion(TypedObject result) {
                summoner.setMasteryBook(new MasteryBook(result.getTO("body")));
                callback.onCompletion(summoner);
            }

            @Override
            public void onError(Exception ex) {
                callback.onError(ex);
            }

        });
    }
}
