package com.achimala.leaguelib.models;

public enum SummonerSpell {
Null(-1),
cleanse(1),
clairvoyance(2),
exhaust(3),
flash(4),
ghost(6),
heal(7),
revive(10),
smite(11),
teleport(12),
clarity(13),
ignite(14),
garrison(17),
barrier(21);

private int id;
private SummonerSpell(int spellId) {
id = spellId;
}

public int getId() {
return id;
}

public static SummonerSpell getSpellById(int id) {
for (SummonerSpell s : SummonerSpell.values()) {
if (s.getId() == id) return s;
}

return null;
}
// @@summoner_spells = {1 => 'cleanse',
//
// 2 => 'clairvoyance',
//
// 3 => 'exhaust',
//
// 4 => 'flash',
//
// 6 => 'ghost',
//
// 7 => 'heal',
//
// 10 => 'revive',
//
// 11 => 'smite',
//
// 12 => 'teleport',
//
// 13 => 'clarity',
//
// 14 => 'ignite',
//
// 17 => 'garrison',
//
// 21 => 'barrier'}
//
//
}