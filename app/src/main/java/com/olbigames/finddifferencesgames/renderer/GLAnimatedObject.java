package com.olbigames.finddifferencesgames.renderer;

import android.opengl.GLES20;
import android.opengl.Matrix;

public class GLAnimatedObject {

	float x, y, toX, toY, xx, yy, bannerHeight;
	RectangleImage ri;
	long duration_milis, elapsed;
	boolean animating;
	
    public GLAnimatedObject(float x, float y, RectangleImage ri, float bannerHeight) {
    	this.x = x;
    	this.y = y;
    	this.ri = ri;
		this.bannerHeight = bannerHeight;
    	animating = false;
    	
    }

    public void moveWithShade( float toX, float toY, long duration_milis){
    	this.toX = toX;
    	this.toY = toY;
    	this.duration_milis = duration_milis;
    	
    	xx = toX - x;
    	yy = toY - y;
    }
    
    public void start(){
    	animating = true;
    }

    public void update( long elapsed){
    	if(animating){
    		this.elapsed += elapsed;
    		float d = ((float) elapsed / (float) duration_milis );
    		this.x += xx * d;
    		this.y += yy * d;
    		
        	if(this.elapsed > duration_milis){
            	animating = false;
        	}
    	}

    }
    
    public void draw( float[] mvpMatrix, float[] mtrxView, float[] mtrxProjection, float[] mMVPMatrix, float alpha){
    	if(animating){
	        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE);
    		
        	if(duration_milis - this.elapsed < 500L){
        		alpha =  ((float) duration_milis - (float) this.elapsed) / 500.0f;
        	}
    		Matrix.setIdentityM(mvpMatrix, 0);
	        Matrix.translateM(mvpMatrix, 0, x, y, 0.0f);
	        Matrix.scaleM(mvpMatrix, 0, bannerHeight, bannerHeight, 1);
			Matrix.multiplyMM(mMVPMatrix, 0, mtrxView, 0, mvpMatrix, 0);
		    Matrix.multiplyMM(mMVPMatrix, 0, mtrxProjection, 0, mMVPMatrix, 0);
    		ri.draw(mMVPMatrix, alpha);
		    GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
    	}
    }
}
