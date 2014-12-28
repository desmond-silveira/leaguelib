package com.achimala.leaguelib.models.masteries;

import com.gvaneyck.rtmp.encoding.TypedObject;

public class Talent {

    private String _name;
    private int _rank;
    private int _groupId;
    private int _id;

    public String getName() {
        return _name;
    }

    public Talent setName(String name) {
        this._name = name;
        return this;
    }

    public int getRank() {
        return _rank;
    }

    public Talent setRank(int rank) {
        this._rank = rank;
        return this;
    }

    public int getGroupId() {
        return _groupId;
    }

    public Talent setGroupId(int groupId) {
        this._groupId = groupId;
        return this;
    }

    public int getId() {
        return _id;
    }

    public Talent setId(int id) {
        this._id = id;
        return this;
    }

    public Talent(){

    }

    public Talent(TypedObject entry) {
        TypedObject talent = entry.getTO("talent");

        _name = talent.getString("name");
        _id = entry.getInt("talentId");
        _groupId = talent.getInt("talentGroupId");
        _rank = entry.getInt("rank");
    }


    public String toString()
    {
        return _name + "(" + _id + "): " + _rank;
    }

}
