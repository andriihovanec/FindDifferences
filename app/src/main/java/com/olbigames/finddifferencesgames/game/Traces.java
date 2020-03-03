package com.olbigames.finddifferencesgames.game;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Random;

public class Traces {

public static final int COL = 200;

public int x;
public int y;
private float X[] = new float[COL];
private float Y[] = new float[COL];
private float speedX[] = new float[COL];
private float speedY[] = new float[COL];
private float timeLeft = 2500f;
public int difId;
public FloatBuffer uvsBuffer;
public FloatBuffer vectorBuffer;
public FloatBuffer color;
private float vector[] = new float[COL*2];

public Traces(int id, int xx, int yy) {
	x = xx;
	y = yy;
	difId = id;
	Random r = new Random();
	
	int i = 0;
	do {
		//x[i] = 0;
		//y[i] = 0;
		double rand1 = r.nextDouble() * 360f;
		double rand2 = r.nextDouble() * 40f;
		
		speedX[i] = (float) (Math.sin(rand1) * rand2);
		speedY[i] = (float) (Math.cos(rand1) * (10L + rand2)) - 35f + r.nextInt(20);
		//speedX[i] = (r.nextInt(601) - 300)/10f;
		//speedY[i] = (r.nextInt(1001) - 600)/10f;
		//vector[2*i] = speedX[i] + x;
		//vector[2*i+1] = speedY[i] + y;
		X[i] = x;
		Y[i] = y;
		//X[i] = x + (r.nextInt(301) - 150)/10f;
		//Y[i] = y + (r.nextInt(301) - 150)/10f;

		vector[2*i] = X[i] = X[i] + speedX[i] * 0.2f;
		vector[2*i+1] = Y[i] = Y[i] + speedY[i] * 0.2f;
		
		
		i++;
	} while (i < COL);//i < speedX.length 
	
	getVector();
	getColor();
	
	float[] uvs = new float[] {
			0.25f, 0.25f,
			0.25f, 0.5f,
			0.5f, 0.5f,
			0.5f, 0.25f
    };
	
	ByteBuffer ltb = ByteBuffer.allocateDirect(uvs.length * 4);
	ltb.order(ByteOrder.nativeOrder());
	uvsBuffer = ltb.asFloatBuffer();
	uvsBuffer.put(uvs);
	uvsBuffer.position(0);
	
}

public int col() {
	return COL;
}

public float gettime() {
	return 3000f - timeLeft;
}

public void getVector2(float time) {
	
	
	int i = 0;
	do {
		
		speedY[i] += 40f * time;
		if((Math.abs(speedY[i])) > 0 ){
			if( speedY[i] > 0){
				speedY[i] -= 15f * time;
			}else{
				speedY[i] += 15f * time;
			}
			
		}
		vector[2*i] = X[i] = X[i] + speedX[i] * time;
		vector[2*i+1] = Y[i] = Y[i] + speedY[i] * time;
		/*
		vector[4*i+2] = X[i] + speedX[i] * (time + 0.3f);
		vector[4*i+3] = Y[i] + speedY[i] * (time + 0.3f);
		*/
		i++;
	} while (i < COL);
	
	
	// The texture buffer
	ByteBuffer ltb = ByteBuffer.allocateDirect(vector.length * 4);
	ltb.order(ByteOrder.nativeOrder());
	vectorBuffer = ltb.asFloatBuffer();
	vectorBuffer.put(vector);
	vectorBuffer.position(0);
	
}

public void getVector() {
	
	// The texture buffer
	ByteBuffer ltb = ByteBuffer.allocateDirect(vector.length * 4);
	ltb.order(ByteOrder.nativeOrder());
	vectorBuffer = ltb.asFloatBuffer();
	vectorBuffer.put(vector);
	vectorBuffer.position(0);
	
}

public void getColor() {

	float[] col = new float[] {
			1.0f, 1.0f, 1.0f, 1.0f
    };
	ByteBuffer ltb = ByteBuffer.allocateDirect(col.length * 4);
	ltb.order(ByteOrder.nativeOrder());
	color = ltb.asFloatBuffer();
	color.put(col);
	color.position(0);
}

public float getX() {
	return x;
}

public float getY() {
	return y;
}


public boolean timeAdd(float time) {
	timeLeft -= time;
	if(timeLeft < 0f){
		return true;
	}
	getVector2(time/1000);
	return false;
}



}

