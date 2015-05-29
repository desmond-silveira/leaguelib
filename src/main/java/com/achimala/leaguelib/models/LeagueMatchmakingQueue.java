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

/**
 * @see <a href="https://developer.riotgames.com/docs/game-constants">Game Constants</a>
 */
public enum LeagueMatchmakingQueue {
    /** Custom games. */
    NONE ("Custom Game"),
    /** Summoner's Rift unranked games. */
    NORMAL ("Normal 5v5"),
    /** Twisted Treeline unranked games. */
    NORMAL_3x3 ("Normal 3v3"),
    /** Dominion/Crystal Scar games. */
    ODIN_UNRANKED ("The Crystal Scar"),
    /** ARAM / Howling Abyss games. */
    ARAM_UNRANKED_5x5 ("Howling Abyss"),
    /**
     * Summoner's Rift and Crystal Scar games played against Intro, Beginner, or
     * Intermediate AI.
     */
    BOT ("Co-op vs. AI 5v5"),
    /** Twisted Treeline games played against AI. */
    BOT_3x3 ("Co-op vs. AI 3v3"),
    /** Summoner's Rift ranked solo queue games. */
    RANKED_SOLO_5x5 ("Ranked Solo 5v5"),
    /** Twisted Treeline ranked team games. */
    RANKED_TEAM_3x3 ("Ranked Team 3v3"),
    /** Summoner's Rift ranked team games. */
    RANKED_TEAM_5x5 ("Ranked Team 5v5"),
    /** One for All games. */
    ONEFORALL_5x5("ONEFORALL_5x5"),
    /** Snowdown Showdown 1x1 games. */
    FIRSTBLOOD_1x1("FIRSTBLOOD_1x1"),
    /** Snowdown Showdown 2x2 games. */
    FIRSTBLOOD_2x2("FIRSTBLOOD_2x2"),
    /** Hexakill games. */
    SR_6x6("SR_6x6"),
    /** Team Builder games. */
    CAP_5x5("CAP_5x5"),
    /** Ultra Rapid Fire games. */
    URF("URF"),
    /** Ultra Rapid Fire games played against AI. */
    URF_BOT("URF_BOT"),
    /** Summoner's Rift games played against Nightmare AI. */
    NIGHTMARE_BOT("NIGHTMARE_BOT"),
    /** Ascension games. */
    ASCENSION("ASCENSION"),
    /** Twisted Treeline 6x6 Hexakill games. */
    HEXAKILL("HEXAKILL"),
    /** King Poro games. */
    KING_PORO_5X5("KING_PORO_5x5"),
    /** Nemesis games. */
    COUNTER_PICK("COUNTER_PICK");
    
    private String _displayName;

    private LeagueMatchmakingQueue(String displayName) {
        _displayName = displayName;
    }

    public String getDisplayName() {
        return _displayName;
    }
}