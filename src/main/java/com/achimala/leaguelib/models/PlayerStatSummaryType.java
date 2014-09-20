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

/**
 * @see <a href="https://developer.riotgames.com/docs/game-constants">Game Constants</a>
 */
public enum PlayerStatSummaryType {
    /** Summoner's Rift unranked games. */
    Unranked,
    /** Twisted Treeline unranked games. */
    Unranked3x3,
    /** Dominion/Crystal Scar games. */
    OdinUnranked,
    /** ARAM / Howling Abyss games. */
    AramUnranked5x5,
    /** Summoner's Rift and Crystal Scar games played against AI. */
    CoopVsAI,
    /** Twisted Treeline games played against AI. */
    CoopVsAI3x3,
    /** Summoner's Rift ranked solo queue games. */
    RankedSolo5x5,
    /** Twisted Treeline ranked team games. */
    RankedTeam3x3,
    /** Summoner's Rift ranked team games. */
    RankedTeam5x5,
    /** One for All games. */
    OneForAll5x5,
    /** Snowdown Showdown 1x1 games. */
    FirstBlood1x1,
    /** Snowdown Showdown 2x2 games. */
    FirstBlood2x2,
    /** Hexakill games. */
    SummonersRift6x6,
    /** Team Builder games. */
    CAP5x5,
    /** Ultra Rapid Fire games. */
    URF,
    /** Ultra Rapid Fire games played against AI. */
    URFBots,
    /** Summoner's Rift games played against Nightmare AI. */
    NightmareBot,
    /** Ascension games. */
    Ascension,
    CAP1x1,
    CoopVsAI5x5,
    RankedPremade3x3,
    RankedPremade5x5,
    AramUnranked1x1,
    AramUnranked2x2,
    AramUnranked3x3,
    AramUnranked6x6
}