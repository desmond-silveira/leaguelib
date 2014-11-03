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
    NONE,
    /** Summoner's Rift unranked games. */
    NORMAL,
    /** Twisted Treeline unranked games. */
    NORMAL_3x3,
    /** Dominion/Crystal Scar games. */
    ODIN_UNRANKED,
    /** ARAM / Howling Abyss games. */
    ARAM_UNRANKED_5x5,
    /**
     * Summoner's Rift and Crystal Scar games played against Intro, Beginner, or
     * Intermediate AI.
     */
    BOT,
    /** Twisted Treeline games played against AI. */
    BOT_3x3,
    /** Summoner's Rift ranked solo queue games. */
    RANKED_SOLO_5x5,
    /** Twisted Treeline ranked team games. */
    RANKED_TEAM_3x3,
    /** Summoner's Rift ranked team games. */
    RANKED_TEAM_5x5,
    /** One for All games. */
    ONEFORALL_5x5,
    /** Snowdown Showdown 1x1 games. */
    FIRSTBLOOD_1x1,
    /** Snowdown Showdown 2x2 games. */
    FIRSTBLOOD_2x2,
    /** Hexakill games. */
    SR_6x6,
    /** Team Builder games. */
    CAP_5x5,
    /** Ultra Rapid Fire games. */
    URF,
    /** Ultra Rapid Fire games played against AI. */
    URF_BOT,
    /** Summoner's Rift games played against Nightmare AI. */
    NIGHTMARE_BOT,
    /** Ascension games. */
    ASCENSION,
    /** Twisted Treeline 6x6 Hexakill games. */
    HEXAKILL
}