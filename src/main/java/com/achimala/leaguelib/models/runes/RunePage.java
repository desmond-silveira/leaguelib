package com.achimala.leaguelib.models.runes;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import com.gvaneyck.rtmp.TypedObject;

public class RunePage {

    private int _pageId;
    private String _name;
    private boolean _current;

    List<Rune> _quints = new ArrayList<Rune>();
    List<Rune> _marks = new ArrayList<Rune>();
    List<Rune> _seals = new ArrayList<Rune>();
    List<Rune> _glyphs= new ArrayList<Rune>();

    public RunePage(TypedObject page)
    {
        _pageId = page.getInt("pageId");
        _name = page.getString("name");
        _current = page.getBool("current");

        Object[] entries = page.getArray("slotEntries");

        for (Object entry : entries)
        {
            int runeId = ((TypedObject) entry).getInt("runeId");
            Rune rune = Rune.getRuneWithId(runeId);

            switch (rune.getRuneType()){

            case MARK:
                _marks.add(rune);
                break;
            case SEAL:
                _seals.add(rune);
                break;
            case GLYPH:
                _glyphs.add(rune);
                break;
            case QUINTESSENCE:
                _quints.add(rune);
                break;
            }
        }
    }

    public List<Rune> getMarks(){
        return _marks;
    }

    public List<Rune> getSeals(){
        return _seals;
    }

    public List<Rune> getGlyphs(){
        return _glyphs;
    }

    public List<Rune> getQuints()
    {
        return _quints;
    }

    public Map<RuneStatType, Double> getStatTypes() {
        Map<RuneStatType, Double> stats = new EnumMap<>(RuneStatType.class);
        for (Rune rune : getAllRunes()) {
            Double d = stats.get(rune.getStatType());
            if (d == null) {
                d = 0.0;
            }
            stats.put(rune.getStatType(), rune.getStatAmount() + d);
        }
        return stats;
    }
    public List<Rune> getAllRunes()
    {
        List<Rune> runes = new ArrayList<Rune>();
        runes.addAll(_marks);
        runes.addAll(_seals);
        runes.addAll(_glyphs);
        runes.addAll(_quints);

        return runes;
    }

    public boolean isCurrent()
    {
        return _current;
    }

    public String getName(){
        return _name;
    }

    public int getPageId()
    {
        return _pageId;
    }
}
