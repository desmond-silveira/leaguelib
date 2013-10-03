package com.achimala.leaguelib.tests;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.achimala.leaguelib.connection.LeagueAccount;
import com.achimala.leaguelib.connection.LeagueConnection;
import com.achimala.leaguelib.connection.LeagueServer;
import com.achimala.leaguelib.errors.LeagueException;
import com.achimala.leaguelib.models.LeagueChampion;
import com.achimala.leaguelib.models.LeagueGame;
import com.achimala.leaguelib.models.LeagueRankedStatType;
import com.achimala.leaguelib.models.LeagueSummoner;
import com.achimala.leaguelib.models.LeagueSummonerLeagueStats;

public class FuturesTests {

    private static ThreadPoolExecutor threadPool = new ThreadPoolExecutor(10, 30, 10, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(30), new TestThreadFactory());

    private static class TestThreadFactory implements ThreadFactory {

        int count = 0;

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            t.setName("Test-Thread-" + count++);
            t.setDaemon(true);
            return t;
        }

    }

    public static void main(String[] args) throws Exception {
        final LeagueConnection c = new LeagueConnection(LeagueServer.NORTH_AMERICA);
        c.getAccountQueue().addAccount(new LeagueAccount(LeagueServer.NORTH_AMERICA, "3.10.xx", args[0], args[1]));

        Map<LeagueAccount, LeagueException> exceptions = c.getAccountQueue().connectAll();
        if(exceptions != null) {
            for(LeagueAccount account : exceptions.keySet())
                System.out.println(account + " error: " + exceptions.get(account));
            return;
        }



        Future<LeagueSummoner> initialSummoner = c.getSummonerService().getSummonerByName("Mazorlion", threadPool);
        // Retrieve the initial summoner so more work can be done
        LeagueSummoner summoner = initialSummoner.get();

        System.out.println(summoner.getName() + ":");
        System.out.println("    accountID:  " + summoner.getAccountId());
        System.out.println("    summonerID: " + summoner.getId());
        System.out.println();

        // Send out all the requests because each there's no reason they cant be running at the same time
        Future<LeagueSummoner> summonerWithPublicData = c.getSummonerService().fillPublicSummonerData(summoner, threadPool);
        Future<LeagueSummoner> summonerWithGameInfo = c.getGameService().fillActiveGameData(summoner, threadPool);
        Future<LeagueSummoner> summonerWithRankedStats = c.getPlayerStatsService().fillRankedStats(summoner, threadPool);
        Future<LeagueSummoner> summonerWithSoloQueue = c.getLeaguesService().fillSoloQueueLeagueData(summoner, threadPool);

        summoner = summonerWithPublicData.get();
        System.out.println("Profile:");
        System.out.println("    S1: " + summoner.getProfileInfo().getSeasonOneTier());
        System.out.println("    S2: " + summoner.getProfileInfo().getSeasonTwoTier());
        System.out.println();

        summoner = summonerWithSoloQueue.get();
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

        summoner = summonerWithRankedStats.get();
        LeagueChampion champ = LeagueChampion.getChampionWithName("Elise");
        Map<LeagueRankedStatType, Integer> statsMap = summoner.getRankedStats().getAllStatsForChampion(champ);
        if(stats == null) {
            System.out.println("No stats for " + champ);
        } else {
            System.out.println("All stats for " + champ + ":");
            for(LeagueRankedStatType type : LeagueRankedStatType.values())
                System.out.println("    " + type + " = " + statsMap.get(type));
        }

        System.out.println();

        summoner = summonerWithGameInfo.get();
        if(summoner.getActiveGame() != null) {
            LeagueGame game = summoner.getActiveGame();
            System.out.println("PLAYER TEAM (" + game.getPlayerTeamType() + "):");
            for(LeagueSummoner sum : summoner.getActiveGame().getPlayerTeam())
                System.out.println("    " + sum);
            System.out.println("ENEMY TEAM (" + game.getEnemyTeamType() + "):");
            for(LeagueSummoner sum : summoner.getActiveGame().getEnemyTeam())
                System.out.println("    " + sum);
            System.out.println("PLAYER TEAM BANS:");
            for(LeagueChampion champion : game.getBannedChampionsForTeam(game.getPlayerTeamType()))
                System.out.println("    " + champion.getName());
            System.out.println("ENEMY TEAM BANS:");
            for(LeagueChampion champion : game.getBannedChampionsForTeam(game.getEnemyTeamType()))
                System.out.println("    " + champion.getName());
        } else {
            System.out.println("NOT IN GAME");
        }


        System.out.println();
        System.out.flush();
    }
}
