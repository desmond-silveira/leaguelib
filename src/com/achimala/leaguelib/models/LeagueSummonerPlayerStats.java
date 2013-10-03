/*
* This file is part of LeagueLib.
* LeagueLib is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* LeagueLib is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with LeagueLib. If not, see <http://www.gnu.org/licenses/>.
*/

package com.achimala.leaguelib.models;

import java.util.HashMap;
import java.util.Map;

import com.gvaneyck.rtmp.TypedObject;

public class LeagueSummonerPlayerStats {

private HashMap<PlayerStatSummaryType, Map<PlayerStatType, Integer>> _stats;

    public LeagueSummonerPlayerStats() {
    }

    public LeagueSummonerPlayerStats(TypedObject obj) {
     _stats = new HashMap<PlayerStatSummaryType, Map<PlayerStatType, Integer>>();
     TypedObject summaries = obj.getTO("playerStatSummaries");
         for(Object o : summaries.getArray("playerStatSummarySet")) {
             TypedObject to = (TypedObject)o;
             PlayerStatSummaryType queue = PlayerStatSummaryType.valueOf(to.getString("playerStatSummaryTypeString"));
             if(!_stats.containsKey(queue))
                 _stats.put(queue, new HashMap<PlayerStatType, Integer>());
             _stats.get(queue).put(PlayerStatType.WINS, to.getInt("wins"));
             _stats.get(queue).put(PlayerStatType.LOSSES, to.getInt("losses"));
             _stats.get(queue).put(PlayerStatType.LEAVES, to.getInt("leaves"));
             _stats.get(queue).put(PlayerStatType.RATING, to.getInt("rating"));
             _stats.get(queue).put(PlayerStatType.MAX_RATING, to.getInt("maxRating"));
         }
    }

    public int getStat(PlayerStatSummaryType summaryType, PlayerStatType statType) {
     Map <PlayerStatType, Integer> queue = _stats.get(summaryType);
     return queue.get(statType);
    }
}