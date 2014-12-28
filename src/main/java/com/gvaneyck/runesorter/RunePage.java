package com.gvaneyck.runesorter;

import java.util.HashMap;
import java.util.Map;

import com.gvaneyck.rtmp.encoding.TypedObject;

/**
 * Stores rune page info
 * 
 * @author Gvaneyck
 */
public class RunePage implements Comparable<RunePage> {
    public Integer pageId;
    public String name;
    public Boolean current;
    public TypedObject page;
    
    public Map<Integer, Rune> pageContents;

    public RunePage(TypedObject page) {
        name = page.getString("name");
        pageId = page.getInt("pageId");
        current = page.getBool("current");
        this.page = page;
    }
    
    public void copy(RunePage target) {
        name = target.name;
        page = target.page;
        pageContents = target.pageContents;
    }

    public void swap(RunePage target) {
        String tempName = target.name;
        target.name = name;
        name = tempName;

        TypedObject tempPage = target.page;
        target.page = page;
        page = tempPage;

        Boolean tempCurrent = target.current;
        target.current = current;
        current = tempCurrent;
        
        Map<Integer, Rune> tempContents = target.pageContents;
        target.pageContents = pageContents;
        pageContents = tempContents;
    }

    public TypedObject getSavePage(long summId) {
        TypedObject ret = new TypedObject("com.riotgames.platform.summoner.spellbook.SpellBookPageDTO");

        Object[] slots = page.getArray("slotEntries");
        Object[] saveSlots = new Object[slots.length];
        for (int i = 0; i < slots.length; i++) {
            TypedObject temp = (TypedObject)slots[i];
            TypedObject slot = new TypedObject("com.riotgames.platform.summoner.spellbook.SlotEntry");
            slot.put("runeSlotId", temp.get("runeSlotId"));
            slot.put("runeId", temp.get("runeId"));
            slot.put("futureData", null);
            slot.put("dataVersion", null);
            saveSlots[i] = slot;
        }
        ret.put("slotEntries", TypedObject.makeArrayCollection(saveSlots));

        ret.put("pageId", pageId);
        ret.put("name", name);
        ret.put("current", current);
        ret.put("summonerId", summId);
        ret.put("createDate", null);
        ret.put("futureData", null);
        ret.put("dataVersion", null);

        return ret;
    }
    
    public Map<Integer, Rune> getPageContents() {
        if (pageContents == null) {
            pageContents = new HashMap<Integer, Rune>();
            Object[] entries = page.getArray("slotEntries");
            for (Object o : entries) {
                Rune r = new Rune((TypedObject)o);
                if (pageContents.containsKey(r.id))
                    pageContents.get(r.id).quantity++;
                else
                    pageContents.put(r.id, r);
            }
        }
        
        return pageContents;
    }

    public int compareTo(RunePage page) {
        return pageId.compareTo(page.pageId);
    }

    public String toString() {
        return name;
    }
}
