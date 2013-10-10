package com.achimala.leaguelib.services;

import java.util.EnumMap;
import java.util.Map;

import com.achimala.leaguelib.connection.LeagueConnection;
import com.achimala.leaguelib.errors.LeagueException;
import com.achimala.leaguelib.models.LeagueHonorType;
import com.achimala.leaguelib.models.LeagueSummoner;
import com.gvaneyck.rtmp.TypedObject;

public class ClientFacadeService extends LeagueAbstractService {

    public ClientFacadeService(LeagueConnection connection) {
        super(connection);
    }

    @Override
    public String getServiceName() {
        return "clientFacadeService";
    }

    public void fillHonorValues(LeagueSummoner summoner) throws LeagueException {
        TypedObject obj = call("callKudos", new Object[] {
                "{\"summonerId\":" + summoner.getId() + ",\"commandName\":\"TOTALS\"}" });

        if (obj == null || obj.getTO("body") == null) {
            summoner.setHonorValues(null);
            return;
        }

        String honorStringValues = obj.getTO("body").getString("value");

        honorStringValues = honorStringValues.split("\\[")[1];
        honorStringValues = honorStringValues.split("\\]")[0];
        String[] honorArrayValues = honorStringValues.split(",");

        Map<LeagueHonorType, Integer> honorMap =
                new EnumMap<LeagueHonorType, Integer>(LeagueHonorType.class);
        // The first value's meaning is unknown
        honorMap.put(LeagueHonorType.FRIENDLY,
                Integer.valueOf(honorArrayValues[1]));
        honorMap.put(LeagueHonorType.HELPFUL,
                Integer.valueOf(honorArrayValues[2]));
        honorMap.put(LeagueHonorType.TEAMWORK,
                Integer.valueOf(honorArrayValues[3]));
        honorMap.put(LeagueHonorType.HONORABLE_OPPONENT,
                Integer.valueOf(honorArrayValues[4]));

        summoner.setHonorValues(honorMap);
    }

}