package com.achimala.leaguelib.models.runes;

import java.util.ArrayList;
import java.util.List;

import com.gvaneyck.rtmp.TypedObject;

public class RuneBook {

    ArrayList<RunePage> _pages = new ArrayList<RunePage>();

    /**
     * body > spellbook > bookpages

bookpages[0] > pageid/name/current

bookpages[0] > rune > itemid/name/tier

bookpages[0] > rune > itemEffects >
     * @param data
     */
    public RuneBook(TypedObject body){

        TypedObject spellBook = body.getTO("spellBook");
        Object[] bpObjects = spellBook.getArray("bookPages");

        for (Object bpObject : bpObjects){
            RunePage page = new RunePage((TypedObject) bpObject);
            _pages.add(page);
        }
    }

    public RunePage getCurrentPage() {
        for (RunePage page : _pages) {
            if (page.isCurrent()) return page;
        }

        return null;
    }

    public List<RunePage> getRunePages(){
        return _pages;
    }
}
