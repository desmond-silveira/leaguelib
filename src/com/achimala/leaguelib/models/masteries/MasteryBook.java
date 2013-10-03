package com.achimala.leaguelib.models.masteries;

import java.util.ArrayList;
import java.util.List;

import com.gvaneyck.rtmp.TypedObject;

public class MasteryBook {

    private List<MasteryPage> _pages = new ArrayList<MasteryPage>();

    public MasteryBook (TypedObject data) {

        Object[] objects = data.getArray("bookPages");

        for (Object pageObject : objects)
        {
            TypedObject page = (TypedObject) pageObject;
            _pages.add(new MasteryPage(page));
        }

    }

    public List<MasteryPage> getAllPages()
    {
        return _pages;
    }

    public MasteryPage getCurrentPage()
    {
        for (MasteryPage page : _pages)
        {
            if (page.isCurrent()) return page;
        }

        return null;
    }

}
