package com.olbigames.finddifferencesgames.renderer;


import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Для отрисовки Прямоугольных картинок	***** Минимальный MAX размер текстуры 1024x1024
 */
public class RectangleImage {

    private final FloatBuffer vertexBuffer;
    private final ShortBuffer drawListBuffer;
    public FloatBuffer uvBuffer;
    private ByteBuffer bb2;
    private float uvs[];
    private float uvX1;
    private float uvX2;
    private float uvY1;
    private float uvY2;
    private int mPositionHandle;
    private int mTexCoordLoc0;
    private int mSamplerLoc0;
    private int mAlfa;
    private int mMVPMatrixHandle;
    
    private static final int[] textureIds = new int[] {
        GLES20.GL_TEXTURE0,
        GLES20.GL_TEXTURE1,
        GLES20.GL_TEXTURE2,
        GLES20.GL_TEXTURE3,
        GLES20.GL_TEXTURE4,
        GLES20.GL_TEXTURE5,
        GLES20.GL_TEXTURE6,
        GLES20.GL_TEXTURE7,
        GLES20.GL_TEXTURE8,
        GLES20.GL_TEXTURE9,
        GLES20.GL_TEXTURE10
    };
    private int gl_tex_number;

    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;
    static float squareCoords[] = {
            -0.5f,  0.5f, 0.0f,   // top left
            -0.5f, -0.5f, 0.0f,   // bottom left
             0.5f, -0.5f, 0.0f,   // bottom right
             0.5f,  0.5f, 0.0f }; // top right

    private final short drawOrder[] = { 0, 1, 2, 0, 2, 3 }; // order to draw vertices

    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    float color[] = { 0.2f, 0.709803922f, 0.898039216f, 1.0f };

    /**
     * Sets up the drawing object data for use in an OpenGL ES context.
     */
    public RectangleImage(float sCoords[], Bitmap bmp, int tex_number) {
    	
    	gl_tex_number = tex_number;
    	
    	squareCoords = sCoords;
        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
        // (# of coordinate values * 4 bytes per float)
                squareCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(squareCoords);
        vertexBuffer.position(0);

        // initialize byte buffer for the draw list
        ByteBuffer dlb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 2 bytes per short)
                drawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);
        
        uvX1 = 0.0f;
        uvX2 = 1.0f;
        uvY1 = 0.0f;
        uvY2 = 1.0f;
		// Create our UV coordinates.
        
		uvs = new float[] {
				0.0f, 0.0f,
				0.0f, 1.0f,
				1.0f, 1.0f,
				1.0f, 0.0f
	    };

		bb2 = ByteBuffer.allocateDirect(uvs.length * 4);
		bb2.order(ByteOrder.nativeOrder());
		toBuffer();

		// Generate Textures, if more needed, alter these numbers.
		int[] texturenames = new int[1];
		GLES20.glGenTextures(1, texturenames, 0);

		GLES20.glActiveTexture(textureIds[gl_tex_number]);
		
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texturenames[0]);
		
		// Set filtering
		// new
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
		// ---
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        
        // Load the bitmap into the bound texture.
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);

        mPositionHandle = GLES20.glGetAttribLocation(GraphicTools.sp_Image, "vPosition");
	    mTexCoordLoc0 = GLES20.glGetAttribLocation(GraphicTools.sp_Image, "a_texCoord" );
        mMVPMatrixHandle = GLES20.glGetUniformLocation(GraphicTools.sp_Image, "uMVPMatrix");
        mSamplerLoc0 = GLES20.glGetUniformLocation (GraphicTools.sp_Image, "s_texture" );
        mAlfa = GLES20.glGetUniformLocation (GraphicTools.sp_Image, "vAlfa" );
    }

    /**
     * Encapsulates the OpenGL ES instructions for drawing this shape.
     *
     * @param mvpMatrix - The Model View Project matrix in which to draw
     * this shape.
     */

    public void draw(float[] mvpMatrix) {
    	this.draw(mvpMatrix, 1.0f);
    }
    
    public void toBuffer() {
    	
    	if(uvX1<0.0f){
    		uvX2 -= uvX1;
    		uvX1 = 0.0f;
    	}
    	if(uvX2>1.0f){
    		uvX1 -= uvX2-1.0f;
    		uvX2 = 1.0f;
    	}
    	if(uvY1<0.0f){
    		uvY2 -= uvY1;
    		uvY1 = 0.0f;
    	}
    	if(uvY2>1.0f){
    		uvY1 -= uvY2-1.0f;
    		uvY2 = 1.0f;
    	}

		// Create our UV coordinates.
    	uvs = new float[] {
    			uvX1, uvY1,
    			uvX1, uvY2,
    			uvX2, uvY2,
    			uvX2, uvY1
	    };

		// The texture buffer
		bb2.clear();
		uvBuffer = bb2.asFloatBuffer();
		uvBuffer.put(uvs);
		uvBuffer.position(0);
    }
    
    public float getScale() {
    	return uvX2 - uvX1;
    }
    
    public float getTransX() {
    	return uvX1;
    }
    public float getTransY() {
    	return uvY1;
    }

    public void scale(float i) {
		float ii = i/1000f;
		if ( i > 0f){
    	if ( (uvX2 - uvX1 + ii + ii) < 1.0f){// Отдаляем
        	uvX1 -= ii;
        	uvX2 += ii;
        	uvY1 -= ii;
        	uvY2 += ii;
    	}else{
            uvX1 = 0.0f;
            uvX2 = 1.0f;
            uvY1 = 0.0f;
            uvY2 = 1.0f;
    	}
		}
    	if ( i < 0f & (uvX2 - uvX1  + ii  + ii) > 0.4f){// Приближаем
        	uvX1 -= ii;
        	uvX2 += ii;
        	uvY1 -= ii;
        	uvY2 += ii;
    	}
    	toBuffer();
    }
    
    public void translate(float x, float y) {

		float ii = x/1000f;
    	if ( x > 0f & uvX2+ii < 1.0f){// Вправо
        	uvX1 += ii;
        	uvX2 += ii;
    	}
    	if ( x < 0f & uvX1+ii > 0.0f){// Влево
        	uvX1 += ii;
        	uvX2 += ii;
    	}
		ii = y/1000f;
    	if ( y > 0f & uvY2+ii < 1.0f){// Вверх
        	uvY1 += ii;
        	uvY2 += ii;
    	}
    	if ( y < 0f & uvY1+ii > 0.0f){// Вниз
        	uvY1 += ii;
        	uvY2 += ii;
    	}

    	toBuffer();
    }

    public void reset() {
        uvX1 = 0.0f;
        uvX2 = 1.0f;
        uvY1 = 0.0f;
        uvY2 = 1.0f;
    	toBuffer();
    }
    
    public void draw(float[] mvpMatrix, float alpha) {
        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer( mPositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);

	    GLES20.glEnableVertexAttribArray ( mTexCoordLoc0 );
	    GLES20.glVertexAttribPointer ( mTexCoordLoc0, 2, GLES20.GL_FLOAT, false, 0, uvBuffer);

        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);

        GLES20.glUniform1i ( mSamplerLoc0, gl_tex_number);
        GLES20.glUniform1f(mAlfa, alpha);

        // Draw the square
        GLES20.glDrawElements(
                GLES20.GL_TRIANGLES, drawOrder.length,
                GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mTexCoordLoc0);
    }

}
