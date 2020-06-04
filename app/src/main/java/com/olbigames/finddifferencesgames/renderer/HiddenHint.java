package com.olbigames.finddifferencesgames.renderer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Random;

public class HiddenHint {

	public static final int COL = 200;
	
	public boolean animShowing, finded;
    
	private float screenWidth, screenHeight;
    
    public float hintCoordinateAxisX, hintCoordinateAxisY, hintRadius;
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
    
    private float timeLeft;
    
    
    //float path[6];
    
    private boolean xleft, yabove;
    
    
    
    public HiddenHint(
    		float screenWidth,
			float screenHeight,
			float hintRadius,
			float founded,
			float hintCoordinateAxisX,
			float hintCoordinateAxisY,
			float pictureScale
	) {
    	
	    animShowing = false;

	    scalenow = scale = pictureScale;
	    
	    timeLeft = 3000.0f;
	    
	    this.screenWidth = screenWidth;
	    this.screenHeight = screenHeight;

	    if( screenWidth > hintCoordinateAxisX ){
	        xleft = true;
	    }else{
	        xleft = false;
	    }
	    
	    if( screenHeight > hintCoordinateAxisY ){
	        yabove = true;
	    }else{
	        yabove = false;
	    }

		this.hintCoordinateAxisX = hintCoordinateAxisX;
		this.hintCoordinateAxisY = hintCoordinateAxisY;
		this.hintRadius = hintRadius;
	    if(founded == 0){
	    	this.finded = false;
    	}else{
	    	this.finded = true;
    	}

	    a = 3.0f;
	    sX = (screenWidth - hintCoordinateAxisX) / 10000.0f;
	    sY = (screenHeight - hintCoordinateAxisY) / 10000.0f;

		Random random = new Random();
		
	    int i = 0;
	    do {
	        
	        scsc[i] = random.nextFloat()*360.0f;
	        
	        float sc0 = random.nextFloat()*25.0f;
	        
	        speedX[i] = (float) (Math.sin(scsc[i]) * (sc0));
	        speedY[i] = (float) (Math.cos(scsc[i]) * (sc0));
	        
	        vector[2*i] = speedX[i] + this.hintCoordinateAxisX;
	        vector[2*i+1] = screenHeight - speedY[i] + this.hintCoordinateAxisY;
	        
	        X[i] = this.hintCoordinateAxisX;
	        Y[i] = this.hintCoordinateAxisY;
	        
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
                X[i] = hintCoordinateAxisX;
                Y[i] = hintCoordinateAxisY;
            }
            
            
            scsc[i] += time;
            
            vector[2*i] = (float) (X[i] - speedX[i] + 10.0 * Math.sin( rotation_speed * scsc[i]));
            vector[2*i+1] = screenHeight - (float) (Y[i] - speedY[i] + 10.0 * Math.cos( rotation_speed * scsc[i]));
            
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

    public float getHintCoordinateAxisX() {
        return hintCoordinateAxisX;
    }

    public float getHintCoordinateAxisY() {
        return hintCoordinateAxisY;
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
	    
	    if( ( xleft && hintCoordinateAxisX > screenWidth) || ( !xleft && screenWidth > hintCoordinateAxisX) || ( yabove && hintCoordinateAxisY > screenHeight) || ( !yabove && screenHeight > hintCoordinateAxisY) ){
	        hintCoordinateAxisX = 10000.0f;
	        hintCoordinateAxisY = 10000.0f;
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
	        hintCoordinateAxisX += a * sX * time;
	        hintCoordinateAxisY += a * sY * time;
	        
	    }
	    
	    this.getVector2(time/1000.0f);
	    
	    return false;
	}

	public boolean isAnimShowing() {
	    return animShowing;
	}

}
