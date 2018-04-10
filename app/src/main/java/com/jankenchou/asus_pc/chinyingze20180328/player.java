package com.jankenchou.asus_pc.chinyingze20180328;

/**
 * Created by Asus-PC on 3/29/2018.
 */

public class player {
    public int hp;
    public int rock_charge;
    public int paper_charge;
    public int scissor_charge;
    public int angry_charge;
    public int bleed;
    public int damage;
    public int heal;
    public boolean stopBleed;
    public boolean reverseHeal;

    public player(){

    }

    public player(int hp, int rock_charge, int paper_charge, int scissor_charge, int angry_charge, int bleed){
        this.hp = hp;
        this.rock_charge = rock_charge;
        this.paper_charge = paper_charge;
        this.scissor_charge = scissor_charge;
        this.angry_charge = angry_charge;
        this.bleed = bleed;
    }

    public void setHpDamage(int change){
        hp -= change;
        damage += change;
    }

    public void setHpHeal(int change){
        hp += change;
        heal += change;
    }

    public void setRock_charge (int charge){
        rock_charge += charge;
    }

    public void setPaper_charge (int charge){
        paper_charge += charge;
    }

    public void setScissor_charge (int charge){
        scissor_charge += charge;
    }

    public void setAngry_charge (int charge){
        angry_charge += charge;
    }

    public void setBleed (int bleeding){bleed += bleeding;}

    public void setStopBleed (boolean stop){stopBleed = stop;}

    public void setReverseHeal (boolean reverse){reverseHeal = reverse;}

    public void execute(action skill, player target){
        skill.Cast(this, target);
    }
}
