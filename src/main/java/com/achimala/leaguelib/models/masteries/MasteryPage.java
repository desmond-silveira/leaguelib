package com.achimala.leaguelib.models.masteries;

import java.util.ArrayList;
import java.util.List;

import com.gvaneyck.rtmp.encoding.TypedObject;

public class MasteryPage {

    public MasteryPage(TypedObject page) {
        _current = page.getBool("current");
        _name = page.getString("name");

        Object[] entries = page.getArray("talentEntries");
        for (Object entry : entries)
        {
            Talent talent = new Talent((TypedObject) entry);
            switch(talent.getGroupId())
            {
            case 1:
                _offense.add(talent);
                break;
            case 2:
                _defense.add(talent);
                break;
            case 3:
                _utility.add(talent);
                break;
            }
        }
    }

    private List<Talent> _defense = new ArrayList<Talent>();
    private List<Talent> _offense = new ArrayList<Talent>();
    private List<Talent> _utility = new ArrayList<Talent>();
    private boolean _current = false;
    private String _name;

    public List<Talent> getAllMasteries()
    {
        List<Talent> list = new ArrayList<Talent>();
        list.addAll(_offense);
        list.addAll(_defense);
        list.addAll(_utility);

        return list;
    }

    public int getRank(List<Talent> talents) {
        int i = 0;
        for (Talent talent : talents) {
            i += talent.getRank();
        }
        return i;
    }
    public int getOffenseRank() {
        return getRank(_offense);
    }
    public int getDefenseRank() {
        return getRank(_defense);
    }
    public int getUtilityRank() {
        return getRank(_utility);
    }
    public List<Talent> getOffense()
    {
        return _offense;
    }
    public List<Talent> getDefense()
    {
        return _defense;
    }
    public List<Talent> getUtility()
    {
        return _utility;
    }

    public boolean isCurrent()
    {
        return _current;
    }

    public String getName()
    {
        return _name;
    }
}
