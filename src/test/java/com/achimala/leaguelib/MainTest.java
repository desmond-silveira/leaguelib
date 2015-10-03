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

package com.achimala.leaguelib;

import com.achimala.leaguelib.connection.LeagueAccount;
import com.achimala.leaguelib.connection.LeagueConnection;
import com.achimala.leaguelib.errors.LeagueException;
import com.achimala.leaguelib.models.*;
import com.achimala.util.Callback;
import com.gvaneyck.rtmp.ServerInfo;

import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

// This tests pretty much everything. It downloads as much information as it can about a given summoner and displays it.
// You'll have to use a League of Legends account to pull the data from the servers.
// NOTE: You must pass following commandline arguments to the program: <AccountName> <AccountPassword> <Summoner to lookup>
// Summoner to lookup can have spaces in name, just write it as it is.
public class MainTest {
    private static int count = 0;
    private static ReentrantLock lock = new ReentrantLock();
    private static Condition done = lock.newCondition();

    private static void incrementCount() {
        lock.lock();
        count++;
        // System.out.println("+ count = " + count);
        lock.unlock();
    }

    private static void decrementCount() {
        lock.lock();
        count--;
        if(count == 0) {
            done.signal();
        }
        // System.out.println("- count = " + count);
        lock.unlock();
    }

    private static String getSummonerToLookupFromCommandLine(String[] args)
    {
        String name = "";
        for(int i = 2; i < args.length; i++)
            name += args[i] + " ";
        return name.substring(0, name.length() -1);
    }

    public static void main(String[] args) throws Exception {
        final LeagueConnection c = new LeagueConnection(ServerInfo.NA);
        c.getAccountQueue().addAccount(new LeagueAccount(

        ServerInfo.NA, "5.8.xx", "statstrats", args[0]));
        final String SUMMONER_TO_LOOK_UP = "dhalsim2";

        Map<LeagueAccount, LeagueException> exceptions = c.getAccountQueue().connectAll();
        if(exceptions != null) {
            for(LeagueAccount account : exceptions.keySet()) {
                System.out.println(account + " error: " + exceptions.get(account));
            }
            return;
        }

        lock.lock();
        incrementCount();
        c.getSummonerService().getSummonerByName(SUMMONER_TO_LOOK_UP, new Callback<LeagueSummoner>() {
            @Override
            public void onCompletion(LeagueSummoner summoner) {
                lock.lock();

                System.out.println(summoner.getName() + ":");
                System.out.println("    accountID:  " + summoner.getAccountId());
                System.out.println("    summonerID: " + summoner.getId());

                incrementCount();
                System.out.println("Getting profile data...");
                c.getSummonerService().fillPublicSummonerData(summoner, new Callback<LeagueSummoner>() {
                    @Override
                    public void onCompletion(LeagueSummoner summoner) {
                        lock.lock();
                        System.out.println("Profile:");
                        System.out.println("    S1: "
                                + summoner.getProfileInfo().getSeasonOneTier());
                        System.out.println("    S2: "
                                + summoner.getProfileInfo().getSeasonTwoTier());
                        System.out.println("Summoner Spell 1: "
                                + summoner.getSummonerSpell1());
                        System.out.println("Summoner Spell 2: "
                                + summoner.getSummonerSpell2());
                        System.out.println();
                        System.out.flush();
                        decrementCount();
                        lock.unlock();
                    }

                    @Override
                    public void onError(Exception ex) {
                        lock.lock();
                        System.out.println(ex.getMessage());
                        decrementCount();
                        lock.unlock();
                    }
                });

                incrementCount();
                System.out.println("Getting leagues data...");
                c.getLeaguesService().fillSoloQueueLeagueData(summoner, new Callback<LeagueSummoner>() {
                    @Override
                    public void onCompletion(LeagueSummoner summoner) {
                        lock.lock();
                        LeagueSummonerLeagueStats stats = summoner.getLeagueStats();
                        if(stats != null) {
                            System.out.println("League:");
                            System.out.println("    Name: " + stats.getLeagueName());
                            System.out.println("    Tier: " + stats.getTier());
                            System.out.println("    Rank: " + stats.getRank());
                            System.out.println("    Wins: " + stats.getWins());
                            System.out.println("    ~Elo: " + stats.getApproximateElo());
                        } else {
                            System.out.println("NOT IN LEAGUE");
                        }
                        System.out.println();
                        System.out.flush();
                        decrementCount();
                        lock.unlock();
                    }

                    @Override
                    public void onError(Exception ex) {
                        lock.lock();
                        System.out.println(ex.getMessage());
                        decrementCount();
                        lock.unlock();
                    }
                });

                incrementCount();
                System.out.println("Getting champ data...");
                c.getPlayerStatsService().fillRankedStats(summoner, new Callback<LeagueSummoner>() {
                    @Override
                    public void onCompletion(LeagueSummoner summoner) {
                        lock.lock();
                        for (LeagueChampion champ
                                : summoner.getRankedStats().getAllPlayedChampions()) {
                            System.out.println("Has played " + champ.getName());
                        }

                        LeagueChampion champ = LeagueChampion.getChampionWithName("Udyr");
                        Map<LeagueRankedStatType, Integer> stats =
                                summoner.getRankedStats().getAllStatsForChampion(champ);
                        if(stats == null) {
                            System.out.println("No stats for " + champ);
                        } else {
                            System.out.println("All stats for " + champ + ":");
                            for(LeagueRankedStatType type : LeagueRankedStatType.values()) {
                                System.out.println("    " + type + " = " + stats.get(type));
                            }
                            System.out.println();
                        }
                        System.out.flush();
                        decrementCount();
                        lock.unlock();
                    }

                    @Override
                    public void onError(Exception ex) {
                        lock.lock();
                        System.out.println(ex.getMessage());
                        decrementCount();
                        lock.unlock();
                    }
                });

                incrementCount();
                System.out.println("Getting game data...");
                c.getGameService().fillActiveGameData(summoner, new Callback<LeagueSummoner>() {
                    @Override
                    public void onCompletion(LeagueSummoner summoner) {
                        lock.lock();
                        LeagueGame game = summoner.getActiveGame();
                        if (game != null) {
                            System.out.println("PLAYER TEAM (" + game.getPlayerTeamType() + "):");
                            for (LeagueSummoner sum : game.getPlayerTeam()) {
                                System.out.println("    " + sum);
                            }
                            System.out.println("ENEMY TEAM (" + game.getEnemyTeamType() + "):");
                            for(LeagueSummoner sum : game.getEnemyTeam()) {
                                System.out.println("    " + sum);
                            }
                            System.out.println("PLAYER TEAM BANS:");
                            for(LeagueChampion champion
                                    : game.getBannedChampionsForTeam(game.getPlayerTeamType())) {
                                System.out.println("    " + champion.getName());
                            }
                            System.out.println("ENEMY TEAM BANS:");
                            for(LeagueChampion champion
                                    : game.getBannedChampionsForTeam(game.getEnemyTeamType())) {
                                System.out.println("    " + champion.getName());
                            }
                        } else {
                            System.out.println("NOT IN GAME");
                        }
                        System.out.println();
                        System.out.flush();
                        decrementCount();
                        lock.unlock();
                    }

                    @Override
                    public void onError(Exception ex) {
                        lock.lock();
                        if(ex instanceof LeagueException) {
                            System.out.println(((LeagueException)ex).getErrorCode());
                        } else {
                            System.out.println(ex.getMessage());
                        }
                        decrementCount();
                        lock.unlock();
                    }
                });

                decrementCount();
                lock.unlock();
            }
            @Override
            public void onError(Exception ex) {
                lock.lock();
                ex.printStackTrace();
                decrementCount();
                lock.unlock();
            }
        });

        System.out.println("Out here, waiting for it to finish");
        done.await();
        // c.getInternalRTMPClient().join();
        System.out.println("Client joined, terminating");
        lock.unlock();
    }
}