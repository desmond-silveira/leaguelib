package com.gvaneyck.runesorter;

import java.util.HashMap;
import java.util.Map;

import com.gvaneyck.rtmp.encoding.ObjectMap;
import com.gvaneyck.rtmp.encoding.TypedObject;

/**
 * Stores mastery page info
 * 
 * @author Gvaneyck
 */
public class MasteryPage implements Comparable<MasteryPage> {
    public Integer pageId;
    public String name;
    public Boolean current;
    public Object[] talents;

    public MasteryPage(TypedObject page) {
        name = page.getString("name");
        pageId = page.getInt("pageId");
        current = page.getBool("current");
        talents = page.getArray("talentEntries");
    }

    public void copy(MasteryPage target) {
        name = target.name;
        talents = target.talents;
    }

    public void swap(MasteryPage target) {
        String tempName = target.name;
        target.name = name;
        name = tempName;

        Object[] tempTalents = target.talents;
        target.talents = talents;
        talents = tempTalents;

        Boolean tempCurrent = target.current;
        target.current = current;
        current = tempCurrent;
    }

    public TypedObject getSavePage(long summId) {
        TypedObject ret = new TypedObject("com.riotgames.platform.summoner.masterybook.MasteryBookPageDTO");

        Object[] saveTalents = new Object[talents.length];
        for (int i = 0; i < talents.length; i++) {
            TypedObject temp = (TypedObject)talents[i];
            TypedObject slot = new TypedObject("com.riotgames.platform.summoner.masterybook.TalentEntry");
            slot.put("talentId", temp.get("talentId"));
            slot.put("rank", temp.get("rank"));
            slot.put("futureData", null);
            slot.put("dataVersion", null);
            saveTalents[i] = slot;
        }
        ret.put("talentEntries", TypedObject.makeArrayCollection(saveTalents));

        ret.put("pageId", pageId);
        ret.put("name", name);
        ret.put("current", current);
        ret.put("summonerId", summId);
        ret.put("createDate", null);
        ret.put("futureData", null);
        ret.put("dataVersion", null);

        return ret;
    }

    public int compareTo(MasteryPage page) {
        return pageId.compareTo(page.pageId);
    }

    public String toString() {
        return name;
    }
    
    public void validate() {
        Map<Integer, Talent> data = new HashMap<Integer, Talent>();
        for (Object o : talents) {
            ObjectMap temp = (ObjectMap)o;
            Talent t = new Talent();
            t.id = temp.getInt("talentId");
            t.desc = temp.getMap("talent").getString("level1Desc");
            t.preReq = temp.getMap("talent").getInt("prereqTalentGameCode");
            t.row = temp.getMap("talent").getInt("talentRowId");
            t.rank = temp.getInt("rank");
            t.max = temp.getMap("talent").getInt("maxRank");
        }
        
        int totalPoints = 0;
        for (Talent t : data.values()) {
            totalPoints += t.rank;
        }
        if (totalPoints > 30) {
            System.out.println(name + ": Too many points (" + totalPoints + ")");
        }
        
        for (Talent t : data.values()) {
            if (t.rank > t.max) {
                System.out.println(name + ": Too many points in " + t.desc + " (" + t.rank + " of " + t.max + ")");
            }
        }
        
        for (Talent t : data.values()) {
            if (t.rank < 0) {
                System.out.println(name + ": Negative points in " + t.desc + " (" + t.rank + ")");
            }
        }
        
        for (Talent t : data.values()) {
            if (t.preReq != null) {
                Talent preReq = data.get(t.preReq);
                if (preReq.rank != preReq.max) {
                    System.out.println(name + ": " + t.desc + " does not have the prereq filled (" + preReq.desc + ")");
                }
            }
        }
        
        int[] pointsPerRow = new int[18];
        for (Talent t : data.values()) {
            pointsPerRow[t.row - 1] += t.rank;
        }
        
        for (int i = 0; i < 3; i++) {
            int pts = 0;
            for (int j = 0; j < 6; j++) {
                if (pts < j * 4) {
                    for (int q = j + 1; q < 6; q++) {
                        if (pointsPerRow[i*6+q] > 0) {
                            System.out.println(name + ": Not enough points in tree " + i + " row " + j);
                            break;
                        }
                    }
                }
                pts += pointsPerRow[i*6+j];
            }
        }
    }
}
