package com.gvaneyck.runesorter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gvaneyck.rtmp.encoding.TypedObject;

public class Rune {
    public int id;
    public String name;
    public int tier;
    public int quantity;
    public List<RuneEffect> runeEffects = new ArrayList<RuneEffect>();
    
    public static final Map<String, String> effectTranslations;
    
    static {
        effectTranslations = new HashMap<String, String>();
        effectTranslations.put("FlatArmorMod", "Armor");
        effectTranslations.put("FlatCritChanceMod", "Crit Chance");
        effectTranslations.put("FlatCritDamageMod", "Crit Damage");
        effectTranslations.put("FlatEnergyPoolMod", "Energy");
        effectTranslations.put("FlatEnergyRegenMod", "Energy/5");
        effectTranslations.put("FlatHPPoolMod", "HP");
        effectTranslations.put("FlatHPRegenMod", "HP/5");
        effectTranslations.put("FlatMagicDamageMod", "AP");
        effectTranslations.put("FlatMPPoolMod", "MP");
        effectTranslations.put("FlatMPRegenMod", "MP/5");
        effectTranslations.put("FlatPhysicalDamageMod", "Damage");
        effectTranslations.put("FlatSpellBlockMod", "MR");
        effectTranslations.put("PercentAttackSpeedMod", "AS");
        effectTranslations.put("PercentEXPBonus", "XP");
        effectTranslations.put("PercentHPPoolMod", "HP");
        effectTranslations.put("PercentLifeStealMod", "Life Steal");
        effectTranslations.put("PercentMovementSpeedMod", "MS");
        effectTranslations.put("PercentSpellVampMod", "Spell Vamp");
        effectTranslations.put("rFlatArmorModPerLevel", "Armor");
        effectTranslations.put("rFlatArmorPenetrationMod", "Armor Pen");
        effectTranslations.put("rFlatEnergyModPerLevel", "Energy");
        effectTranslations.put("rFlatEnergyRegenModPerLevel", "Energy/5");
        effectTranslations.put("rFlatGoldPer10Mod", "Gold/10");
        effectTranslations.put("rFlatHPModPerLevel", "HP");
        effectTranslations.put("rFlatHPRegenModPerLevel", "HP/5");
        effectTranslations.put("rFlatMagicDamageModPerLevel", "AP");
        effectTranslations.put("rFlatMagicPenetrationMod", "Magic Pen");
        effectTranslations.put("rFlatMPModPerLevel", "MP");
        effectTranslations.put("rFlatMPRegenModPerLevel", "MP/5"); //PP 8.9
        effectTranslations.put("rFlatPhysicalDamageModPerLevel", "Damage");
        effectTranslations.put("rFlatSpellBlockModPerLevel", "MR");
        effectTranslations.put("rPercentCooldownMod", "Cooldown");
        effectTranslations.put("rPercentCooldownModPerLevel", "Cooldown");
        effectTranslations.put("rPercentTimeDeadMod", "Time Dead");
    }
    
    public Rune(TypedObject rune) {
        id = rune.getInt("runeId");
        name = rune.getTO("rune").getString("name");
        tier = rune.getTO("rune").getInt("tier");
    
        if (rune.containsKey("quantity"))
            quantity = rune.getInt("quantity");
        else
            quantity = 1;

        Object[] effectList = rune.getTO("rune").getArray("itemEffects");
        for (Object o : effectList) {
            TypedObject effectInfo = (TypedObject)o;
            runeEffects.add(new RuneEffect(Double.parseDouble(effectInfo.getString("value")), effectInfo.getTO("effect").getString("name")));
        }
    }
    
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append(name);
        buffer.append(": ");
        
        for (RuneEffect effect : runeEffects) {
            double multiplier = 1;
            String percent = "";
            String at18 = "";
            if (effect.effectName.contains("Percent")) {
                multiplier *= 100;
                percent = "%";
            }
            if (effect.effectName.contains("PerLevel")) {
                multiplier *= 18;
                at18 = " at 18";
            }
            if (effect.effectName.contains("Regen")) {
                multiplier *= 5;
            }
    
            buffer.append(String.format("%+.2f%s %s%s", effect.value * quantity * multiplier, percent, translateEffect(effect.effectName), at18));
        }

        return buffer.toString();
    }
    
    public static String translateEffect(String effect) {
        return effectTranslations.get(effect);
    }
}
