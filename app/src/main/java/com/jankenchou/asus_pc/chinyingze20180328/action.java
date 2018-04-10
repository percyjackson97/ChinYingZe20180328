package com.jankenchou.asus_pc.chinyingze20180328;

/**
 * Created by Asus-PC on 3/29/2018.
 */

public class action {
    public String name;
    public String description;
    public String error;
    public int damage;
    public int heal;
    public int rock_own;
    public int paper_own;
    public int scissor_own;
    public int angry_own;
    public int rock_opponent;
    public int paper_opponent;
    public int scissor_opponent;
    public int angry_opponent;
    public int action_ID;
    public boolean stopBleeding;
    public boolean reverseHealing;
    public int bleed;

    public action(){

    }

    public action (String name, String description, String error, int damage, int heal, int rock_own, int paper_own, int scissor_own,
                   int angry_own, int rock_opponent, int paper_opponent, int scissor_opponent, int angry_opponent, int action_ID){
        this.name = name;
        this.description = description;
        this.error = error;
        this.damage = damage;
        this.heal = heal;
        this.rock_own = rock_own;
        this.paper_own= paper_own;
        this.scissor_own = scissor_own;
        this.angry_own = angry_own;
        this.rock_opponent = rock_opponent;
        this.paper_opponent = paper_opponent;
        this.scissor_opponent = scissor_opponent;
        this.angry_opponent = angry_opponent;
        this.action_ID = action_ID;
    }

    public void Cast (player caster, player target){
        caster.setHpHeal(heal);
        target.setHpDamage(damage);
        caster.setRock_charge(rock_own);
        caster.setPaper_charge(paper_own);
        caster.setScissor_charge(scissor_own);
        caster.setAngry_charge(angry_own);
        target.setRock_charge(rock_opponent);
        target.setPaper_charge(paper_opponent);
        target.setScissor_charge(scissor_opponent);
        target.setAngry_charge(angry_opponent);
        target.setBleed(bleed);
        caster.setStopBleed(stopBleeding);
        target.setReverseHeal(reverseHealing);
    }
}
