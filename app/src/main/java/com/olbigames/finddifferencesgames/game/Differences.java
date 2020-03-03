package com.olbigames.finddifferencesgames.game;

import java.util.ArrayList;
import java.util.Collections;


public class Differences {

    public int count = 0;

    public int id[] = new int[10];
    public int x[] = new int[10];
    public int y[] = new int[10];
    public int r[] = new int[10];
    public int finded[] = new int[10];
    public int hint[] = new int[10];
    public float anim[] = new float[10];

    public int xNow[] = new int[10];
    public int yNow[] = new int[10];
    public int rNow[] = new int[10];


    public void setID(int id) {
        this.id[this.count] = id;
        this.anim[this.count] = 0.0f;
    }

    public void setX(int x) {
        this.x[this.count] = x;
    }

    public void setY(int y) {
        this.y[this.count] = y;
    }

    public void setR(int r) {
        this.r[this.count] = r;
    }

    public void setFinded(int finded) {
        this.finded[this.count] = finded;
        this.count++;
    }

    public void refresh() {

    }

    public int getXid(int id) {
        for (int i = 0; i < count; i++) {
            if (this.id[i] == id) {
                return x[i];
            }
        }
        return 0;
    }

    public int getYid(int id) {
        for (int i = 0; i < count; i++) {
            if (this.id[i] == id) {
                return y[i];
            }
        }
        return 0;
    }

    public int check(int xx, int yy) {
        for (int i = 0; i < count; i++) {
            if (finded[i] == 0) {
                int xi = x[i];
                int yi = y[i];
                int ri = r[i];
                if (ri < 35) {
                    ri = 35;
                }
                ri *= ri;

                int d = (xx - xi) * (xx - xi) + (yy - yi) * (yy - yi);

                if (d < ri * 1.5f) {//----------
                    finded[i] = 1;
                    anim[i] = 1000.0f;
                    return id[i];
                }

            }
        }
        return -1;

    }

    public void find(int id) {
        for (int i = 0; i < count; i++) {
            if (this.id[i] == id) {
                finded[i] = 1;
                anim[i] = 1000.0f;
            }
        }
    }

    public void update_anim(float time) {
        for (int i = 0; i < count; i++) {
            if (anim[i] != 0.0f & anim[i] > time) {
                anim[i] -= time;
            } else {
                anim[i] = 0.0f;
            }
        }
    }

    public float getAlpha(int i) {
        if (anim[i] == 0.0f) {
            return 1.0f;
        } else {
            float alpha = 1.0f - (anim[i] / 1000.0f);
            return alpha;
        }
    }

    public int getRandomDif() {
        ArrayList<Integer> idArr = new ArrayList<Integer>();
        //int idArr[] = new int[10];
        for (int i = 0; i < count; i++) {
            if (finded[i] == 0) {
                idArr.add(id[i]);
            }
        }
        if (idArr.size() == 0) {
            return -1;
        }
        Collections.shuffle(idArr);

        return idArr.get(0);
    }

}
