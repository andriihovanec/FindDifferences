package com.olbigames.finddifferencesgames.game;

import android.graphics.Point;

public class Finger {
    public int ID;			// »дентификатор пальца
    public Point Now;
    public Point Before;
    public long wasDown;
    boolean enabled = false;		// Ѕыло ли уже сделано движение
    
    public Finger(int id, int x, int y){
        wasDown = System.currentTimeMillis();
        ID = id;
        Now = Before = new Point(x, y);
    }
    
    public void setNow(int x, int y){
        if(!enabled){
            enabled = true;
            Now = Before = new Point(x, y);
        }else{
            Before = Now;
            Now = new Point(x, y);
        }
    }
}