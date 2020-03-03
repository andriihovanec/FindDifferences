package com.olbigames.finddifferencesgames.game;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Random;

public class HiddenHint {

	public static final int COL = 200;
	
	public boolean animShowing, finded;
    
	private float fx,fy;
    
    public float x,y,r;
    private float scale,scalenow;
    private float sX;
    private float sY;
    
    private float a;
    
    private float scsc[] = new float[COL];
    private float X[] = new float[COL];
    private float Y[] = new float[COL];
    private float speedX[] = new float[COL];
    private float speedY[] = new float[COL];
    
    private float vector[] = new float[COL*2];
    
    private float timeLeft, mScreenHeight;
    
    
    //float path[6];
    
    private boolean xleft, yabove;
    
    
    
    public HiddenHint(float finalX, float finalY, float radius, float finded, float xx, float yy, float sc, float mScreenHeight) {
    	
	    animShowing = false;
	    
	    this.mScreenHeight = mScreenHeight;

	    scalenow = scale = sc;
	    
	    timeLeft = 3000.0f;
	    
	    fx = finalX;
	    fy = finalY;
	    
	    if( finalX > xx ){
	        xleft = true;
	    }else{
	        xleft = false;
	    }
	    
	    if( finalY > yy ){
	        yabove = true;
	    }else{
	        yabove = false;
	    }
	    
	    x = xx;
	    y = yy;
	    r = radius;
	    if(finded == 0){
	    	this.finded = false;
    	}else{
	    	this.finded = true;
    	}
	    //scalenow = scale = sc;
	    
	    a = 3.0f;
	    sX = (finalX - xx) / 10000.0f;
	    sY = (finalY - yy) / 10000.0f;

		Random r = new Random();
		
	    int i = 0;
	    do {
	        
	        scsc[i] = r.nextFloat()*360.0f;
	        
	        float sc0 = r.nextFloat()*25.0f;
	        
	        speedX[i] = (float) (Math.sin(scsc[i]) * (sc0));
	        speedY[i] = (float) (Math.cos(scsc[i]) * (sc0));
	        
	        vector[2*i] = speedX[i] + x;
	        vector[2*i+1] = mScreenHeight - speedY[i] + y;
	        
	        X[i] = x ;
	        Y[i] = y ;
	        
	        i++;
	    } while (i < 200);
	    
    }
    
    public void setScale(float sc) {
    	scalenow = scale = sc;
    }
    
    public void startAnim() {
    	animShowing = true;
    }

    public float gettime() {
        return 3000.0f - timeLeft;
    }
    
    public int col() {
        return 200;
    }
    
    
    public void  getVector2(float time) {
        
        float rotation_speed = 2.5f;
        
        int i = 0;
        do {
            
            if( speedY[i] > 0.0f){
                speedY[i] -= 1.0f * time;
            }else{
                speedY[i] += 1.0f * time;
            }
            
            if( speedX[i] > 0.0f){
                speedX[i] -= 1.0f * time;
            }else{
                speedX[i] += 1.0f * time;
            }
            
            int dfs = (int) Math.floor(timeLeft/15.0f );
            if( dfs < i && i < dfs + 20.0 ){
                X[i] = x;
                Y[i] = y;
            }
            
            
            scsc[i] += time;
            
            vector[2*i] = (float) (X[i] - speedX[i] + 10.0 * Math.sin( rotation_speed * scsc[i]));
            vector[2*i+1] = mScreenHeight - (float) (Y[i] - speedY[i] + 10.0 * Math.cos( rotation_speed * scsc[i]));
            
            i++;
        } while (i < 200);
        
        
    }
    

    public FloatBuffer getvector() {
    	
    	ByteBuffer ltb = ByteBuffer.allocateDirect(vector.length * 4);
    	ltb.order(ByteOrder.nativeOrder());
    	FloatBuffer vectorBuffer = ltb.asFloatBuffer();
    	vectorBuffer.put(vector);
    	vectorBuffer.position(0);
    	
        return vectorBuffer;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getScale() {
        return scalenow;
    }
    
    

    public boolean timeAdd(float time) {
    	
	    timeLeft -= time;
	    if(timeLeft < 0.0f){
	        
	        animShowing = false;
	        
	        return true;
	    }
	    
	    if( ( xleft && x > fx ) || ( !xleft && fx > x ) || ( yabove && y > fy ) || ( !yabove && fy > y ) ){
	        x = 10000.0f;
	        y = 10000.0f;
	        return true;
	    }
	    
	    
	    if(timeLeft > 2500.0f)
	    {
	        float max_scale = 4.0f;
	        scalenow = max_scale + (scale - max_scale) * ((timeLeft - 2500.0f) / 500.0f);
	    }
	    
	    if(timeLeft < 2200.0f){
	    	
	        if(a < 12){
	            a*=1.1;
	        }
	        x += a * sX * time;
	        y += a * sY * time;
	        
	    }
	    
	    this.getVector2(time/1000.0f);
	    
	    return false;
	}

	public boolean isAnimShowing() {
	    return animShowing;
	}

}
