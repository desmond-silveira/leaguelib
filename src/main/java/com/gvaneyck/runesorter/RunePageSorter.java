package com.gvaneyck.runesorter;

import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import com.gvaneyck.rtmp.LoLRTMPSClient;
import com.gvaneyck.rtmp.encoding.TypedObject;
import com.gvaneyck.util.Callback;
import com.gvaneyck.util.ConsoleWindow;
import com.gvaneyck.util.Tuple;

public class RunePageSorter {
    
    public static SorterWindow sorterWindow;
    public static ConsoleWindow consoleWindow;
    public static SettingsWindow settingsWindow;

    public static LoLRTMPSClient client;

    public static List<RunePage> runePages;
    public static List<MasteryPage> masteryPages = new ArrayList<MasteryPage>();
    public static Map<Integer, Rune> runeInventory = new HashMap<Integer, Rune>();
    
    public static List<RunePage> runePages2;
    public static List<MasteryPage> masteryPages2;

    public static long acctId = 0;
    public static long summId = 0;
    public static int summLevel = 0;

    public static void main(String[] args) {
        sorterWindow = new SorterWindow();
        consoleWindow = new ConsoleWindow(SorterWindow.MIN_WIDTH, 0);
        settingsWindow = new SettingsWindow("config.txt");
        
        sorterWindow.addWindowFocusListener(new WindowFocusListener() {
            public void windowLostFocus(WindowEvent e) { }
            
            public void windowGainedFocus(WindowEvent e) {
                if (settingsWindow.isVisible()) {
                    settingsWindow.toFront();
                    settingsWindow.requestFocus();
                }
            }
        });
        
        sorterWindow.addWindowListener(new WindowListener() {
            public void windowOpened(WindowEvent e) {}
            public void windowClosing(WindowEvent e) {}
            public void windowIconified(WindowEvent e) {}
            public void windowDeiconified(WindowEvent e) {}
            public void windowActivated(WindowEvent e) {}
            public void windowDeactivated(WindowEvent e) {}

            public void windowClosed(WindowEvent e) {
                client.close();
            }
        });
        
        sorterWindow.runeSorterListener = new Callback() {
            public void callback(Object data) {
                sortRunes();
            }
        };

        sorterWindow.masterySorterListener = new Callback() {
            public void callback(Object data) {
                sortMasteries();
            }
        };

        sorterWindow.runeSwapListener = new Callback() {
            public void callback(Object data) {
                Tuple t = (Tuple)data;
                RunePage current = runePages.get((Integer)t.obj1);
                current.swap(runePages.get((Integer)t.obj2));
                saveRunePages();
            }
        };

        sorterWindow.masterySwapListener = new Callback() {
            public void callback(Object data) {
                Tuple t = (Tuple)data;
                MasteryPage current = masteryPages.get((Integer)t.obj1);
                current.swap(masteryPages.get((Integer)t.obj2));
                saveMasteryPages();
            }
        };

        sorterWindow.runeSelectListener = new Callback() {
            public void callback(Object data) {
                selectRunePage((Integer)data);
            }
        };
        
        sorterWindow.masterySelectListener = new Callback() {
            public void callback(Object data) {
            	selectMasteryPage((Integer)data);
            }
        };
        
        sorterWindow.searchListener = new Callback() {
            public void callback(Object data) {
                search((String)data);
            }
        };
        
        sorterWindow.runeSelectListener2 = new Callback() {
            public void callback(Object data) {
                selectRunePage2((Integer)data);
            }
        };
        
        sorterWindow.masterySelectListener2 = new Callback() {
            public void callback(Object data) {
                selectMasteryPage2((Integer)data);
            }
        };
        
        sorterWindow.copyRuneListener = new Callback() {
            public void callback(Object data) {
                Tuple t = (Tuple)data;
                copyRunePage((Integer)t.obj1, (Integer)t.obj2);
            }
        };

        sorterWindow.copyMasteryListener = new Callback() {
            public void callback(Object data) {
                Tuple t = (Tuple)data;
                copyMasteryPage((Integer)t.obj1, (Integer)t.obj2);
            }
        };

        setupClient();
    }

    public static void setupClient() {
        settingsWindow.setVisible(true);
        
        while (settingsWindow.isVisible()) {
            try { Thread.sleep(10); } catch (Exception e) { }
        }
        
        // Connect
        Settings settings = settingsWindow.getSettings();
        if (settings.version.isEmpty() || settings.username.isEmpty() || settings.password.isEmpty()) {
            System.out.println("Missing required information, restart the program to enter the information");
            return;
        }
        
        client = new LoLRTMPSClient(settings.region, settings.version, settings.username, settings.password);
        
        try {
            client.connectAndLogin();
            initPages();
        }
        catch (IOException e) {
            System.out.println("Error connecting to server: ");
            e.printStackTrace();
            
            Thread t = new Thread() {
                public void run() {
                    setupClient();
                }
            };
            t.start();
        }
    }

    public static void initPages() {
        try {
            // Get the account and summoner ID
            int id = client.invoke("clientFacadeService", "getLoginDataPacketForUser", new Object[] {});
            TypedObject result = client.getResult(id);
            TypedObject data = result.getTO("data").getTO("body").getTO("allSummonerData");
            TypedObject summoner = data.getTO("summoner");
            acctId = summoner.getLong("acctId");
            summId = summoner.getLong("sumId");

            // Get the summoner level
            TypedObject summonerLevel = data.getTO("summonerLevelAndPoints");
            summLevel = summonerLevel.getInt("summonerLevel");
            
            // Get our rune inventory
            id = client.invoke("summonerRuneService", "getSummonerRuneInventory", new Object[] { summId });
            result = client.getResult(id);
            Object[] runeList = result.getTO("data").getTO("body").getArray("summonerRunes");
            for (Object rune : runeList) {
                Rune r = new Rune((TypedObject)rune);
                runeInventory.put(r.id, r);
            }

            // Get our rune pages
            runePages = getRunePages(summId);
            Collections.sort(runePages);
            sorterWindow.updateRunePages(runePages);

            // Get our mastery pages
            id = client.invoke("summonerService", "getAllSummonerDataByAccount", new Object[] { acctId });
            Object[] masteryBookPages = client.getResult(id).getTO("data").getTO("body").getTO("masteryBook").getArray("bookPages");
            for (Object o : masteryBookPages)
                masteryPages.add(new MasteryPage((TypedObject)o));
            Collections.sort(masteryPages);
            sorterWindow.updateMasteryPages(masteryPages);

            sorterWindow.enableButtons();
        }
        catch (IOException e) {
            client.close();

            System.out.println("Failed to get account information:");
            e.printStackTrace();
            System.out.println();
            System.out.println("Restart the program to try again.");
        }
    }
    
    public static long getSummonerId(String summoner) throws IOException {
        int id = client.invoke("summonerService", "getSummonerByName", new Object[] { summoner });
        TypedObject result = client.getResult(id);
        if (!result.getTO("data").containsKey("body"))
            return 0;

        return result.getTO("data").getTO("body").getLong("summonerId");
    }
    
    public static List<RunePage> getRunePages(long summonerId) throws IOException {
        int id = client.invoke("spellBookService", "getSpellBook", new Object[] { summonerId });
        Object[] runeBookPages = client.getResult(id).getTO("data").getTO("body").getArray("bookPages");
        
        List<RunePage> result = new ArrayList<RunePage>();
        for (Object o : runeBookPages)
            result.add(new RunePage((TypedObject)o));
        
        return result;
    }
    
    public static List<MasteryPage> getMasteryPages(long summonerId) throws IOException {
        int id = client.invoke("masteryBookService", "getMasteryBook", new Object[] { summonerId });
        Object[] masteryBookPages = client.getResult(id).getTO("data").getTO("body").getArray("bookPages");
        
        List<MasteryPage> result = new ArrayList<MasteryPage>();
        for (Object o : masteryBookPages) {
            MasteryPage temp = new MasteryPage((TypedObject)o);
            result.add(temp);
            temp.validate();
        }
        
        return result;
    }
    
    public static void sortRunes() {
        List<Integer> pageIds = new ArrayList<Integer>();
        for (RunePage page : runePages)
            pageIds.add(page.pageId);

        Collections.sort(runePages, new Comparator<RunePage>() {
            public int compare(RunePage page1, RunePage page2) {
                return page1.name.compareTo(page2.name);
            }
        });

        for (int i = 0; i < runePages.size(); i++)
            runePages.get(i).pageId = pageIds.get(i);

        saveRunePages();
    }
    
    public static void sortMasteries() {
        List<Integer> pageIds = new ArrayList<Integer>();
        for (MasteryPage page : masteryPages)
            pageIds.add(page.pageId);

        Collections.sort(masteryPages, new Comparator<MasteryPage>() {
            public int compare(MasteryPage page1, MasteryPage page2) {
                return page1.name.compareTo(page2.name);
            }
        });

        for (int i = 0; i < masteryPages.size(); i++)
            masteryPages.get(i).pageId = pageIds.get(i);

        saveMasteryPages();
    }
    
    public static void selectRunePage(int index) {
        if (index == -1 || index >= runePages.size())
            sorterWindow.setInfo1("");
        else
            sorterWindow.setInfo1(formatRunePage(runePages.get(index)));
    }

    public static void selectMasteryPage(int index) {
        if (index == -1 || index >= masteryPages.size())
            sorterWindow.setInfo1("");
        else
            sorterWindow.setInfo1(masteryPages.get(index).name);
    }

    public static void saveRunePages() {
        try {
            TypedObject[] pages2 = new TypedObject[runePages.size()];
            TypedObject currentPage = null;
            for (int i = 0; i < runePages.size(); i++) {
                pages2[i] = runePages.get(i).getSavePage(summId);
                if (runePages.get(i).current)
                    currentPage = pages2[i];
            }

            TypedObject args = new TypedObject("com.riotgames.platform.summoner.spellbook.SpellBookDTO");
            args.put("bookPages", TypedObject.makeArrayCollection(pages2));

            if (currentPage != null)
                args.put("defaultPage", currentPage);

            TypedObject sort = new TypedObject();
            sort.put("unique", false);
            sort.put("compareFunction", null);
            TypedObject fields = new TypedObject();
            fields.put("caseInsensitive", false);
            fields.put("name", "pageId");
            fields.put("numeric", null);
            fields.put("compareFunction", null);
            fields.put("descending", false);
            sort.put("fields", new Object[] { fields });
            args.put("sortByPageId", sort);

            args.put("summonerId", summId);
            args.put("dateString", null);
            args.put("futureData", null);
            args.put("dataVersion", null);

            int id = client.invoke("spellBookService", "saveSpellBook", new Object[] { args });
            TypedObject result = client.getResult(id);
            if (result.get("result").equals("_error"))
                System.out.println("Error changing rune page order");

            sorterWindow.updateRunePages(runePages);
        }
        catch (IOException e) {
            client.close();

            System.out.println("Failed to update Rune Pages:");
            e.printStackTrace();
            System.out.println();
            System.out.println("Restart the program to try again.");
        }
    }

    public static void saveMasteryPages() {
        try {
            TypedObject[] masteries2 = new TypedObject[masteryPages.size()];
            for (int i = 0; i < masteryPages.size(); i++)
                masteries2[i] = masteryPages.get(i).getSavePage(summId);

            TypedObject args = new TypedObject("com.riotgames.platform.summoner.masterybook.MasteryBookDTO");
            args.put("bookPages", TypedObject.makeArrayCollection(masteries2));

            TypedObject sort = new TypedObject();
            sort.put("unique", false);
            sort.put("compareFunction", null);
            TypedObject fields = new TypedObject();
            fields.put("caseInsensitive", false);
            fields.put("name", "pageId");
            fields.put("numeric", null);
            fields.put("compareFunction", null);
            fields.put("descending", false);
            sort.put("fields", new Object[] { fields });
            args.put("sortByPageId", sort);

            args.put("summonerId", summId);
            args.put("dateString", null);
            args.put("futureData", null);
            args.put("dataVersion", null);

            int id = client.invoke("masteryBookService", "saveMasteryBook", new Object[] { args });
            TypedObject result = client.getResult(id);
            if (result.get("result").equals("_error"))
                System.out.println(result + "\nError changing mastery page order");

            sorterWindow.updateMasteryPages(masteryPages);
        }
        catch (IOException e) {
            client.close();

            System.out.println("Failed to update Mastery Pages:");
            e.printStackTrace();
            System.out.println();
            System.out.println("Restart the program to try again.");
        }
    }
    
    public static void search(String player) {
        if (!client.isLoggedIn())
            return;
        
        try {
        	long summonerId = getSummonerId(player);
        	if (summonerId == 0) {
                sorterWindow.updateRunePages2(runePages2);
                System.out.println("No player found with summoner name " + player);
                return;
        	}

        	runePages2 = getRunePages(summonerId);
            Collections.sort(runePages2);
            sorterWindow.updateRunePages2(runePages2);

        	masteryPages2 = getMasteryPages(summonerId);
            Collections.sort(masteryPages2);
            sorterWindow.updateMasteryPages2(masteryPages2);
        }
        catch (IOException e) {
            System.out.println("Error retrieving rune pages for " + player);
            e.printStackTrace();
        }
    }
    
    public static void selectRunePage2(int index) {
        if (runePages2 == null || index == -1 || index >= runePages2.size())
            sorterWindow.setInfo2("");
        else
            sorterWindow.setInfo2(formatRunePage(runePages2.get(index)));
    }
    
    public static void selectMasteryPage2(int index) {
        if (masteryPages2 == null || index == -1 || index >= masteryPages2.size())
            sorterWindow.setInfo2("");
        else
            sorterWindow.setInfo2(masteryPages2.get(index).name);
    }
    
    private static String formatRunePage(RunePage page) {
        Map<Integer, Rune> pageContents = page.getPageContents();
        StringBuilder buffer = new StringBuilder();
        buffer.append("<html><body>");
        buffer.append("Name: ");
        buffer.append(page.name);
        buffer.append("<br/>");
        
        Map<String, RuneEffect> contents2 = new HashMap<String, RuneEffect>();
        for (int key : pageContents.keySet()) {
            Rune r = pageContents.get(key);
            for (RuneEffect effect : r.runeEffects) {
                String name = effect.effectName;
                if (contents2.containsKey(name)) {
                    RuneEffect effect2 = contents2.get(name);
                    effect2.value += r.quantity * effect.value;
                }
                else {
                    contents2.put(name, new RuneEffect(r.quantity * effect.value, name));
                }
            }
        }
         
        for (RuneEffect r : contents2.values()) {
            double multiplier = 1;
            String percent = "";
            String at18 = "";
            if (r.effectName.contains("Percent")) {
                multiplier *= 100;
                percent = "%";
            }
            if (r.effectName.contains("PerLevel")) {
                multiplier *= 18;
                at18 = " at 18";
            }
            if (r.effectName.contains("Regen")) {
                multiplier *= 5;
            }

            buffer.append(String.format("%+.2f%s %s%s", r.value * multiplier, percent, Rune.translateEffect(r.effectName), at18));
            buffer.append("<br/>");
        }
        
        buffer.append("</body></html>");
        
        return buffer.toString();
    }
    
    public static void copyRunePage(int p1, int p2) {
        RunePage mine = runePages.get(p1);
        RunePage theirs = runePages2.get(p2);
        
        // Make sure we have enough runes
        Map<Integer, Rune> requiredRunes = theirs.getPageContents();
        for (Rune entry : requiredRunes.values()) {
            if (!runeInventory.containsKey(entry.id)
                || runeInventory.get(entry.id).quantity < entry.quantity) {
                JOptionPane.showMessageDialog(sorterWindow, "You don't have enough " + entry.name + " runes", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        
        // Get a name for this page
        String name = (String)JOptionPane.showInputDialog(
                sorterWindow,
                "Name the page:",
                "Copy Rune Page",
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                theirs.name);
        
        if (name == null)
            return;
        
        // Copy and save
        mine.copy(theirs);
        mine.name = name;
        saveRunePages();
        selectRunePage(p1);
    }

    public static void copyMasteryPage(int p1, int p2) {
        MasteryPage mine = masteryPages.get(p1);
        MasteryPage theirs = masteryPages2.get(p2);
        
        // Get a name for this page
        String name = (String)JOptionPane.showInputDialog(
                sorterWindow,
                "Name the page:",
                "Copy Rune Page",
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                theirs.name);
        
        if (name == null)
            return;
        
        // Copy and save
        mine.copy(theirs);
        mine.name = name;
        saveMasteryPages();
        selectMasteryPage(p1);
    }
}
