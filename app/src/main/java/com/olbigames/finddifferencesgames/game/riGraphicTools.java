package com.olbigames.finddifferencesgames.game;

import android.opengl.GLES20;

public class riGraphicTools {

	// Program variables
	public static int sp_Image;
	public static int sp_Point;
	
	
	/* SHADER Image
	 * 
	 * This shader is for rendering 2D images straight from a texture
	 * No additional effects.
	 * 
	 */
	public static final String vs_Image =
		"uniform mat4 uMVPMatrix;" +
		"attribute vec4 vPosition;" +
		"attribute vec2 a_texCoord;" +
		"varying vec2 v_texCoord;" +
	    "void main() {" +
	    "  gl_Position = uMVPMatrix * vPosition;" +
	    "  v_texCoord = a_texCoord;" +
	    "}";
	
	public static final String fs_Image =
		"precision mediump float;" +
	    "varying vec2 v_texCoord;" +
	    "uniform float vAlfa;" +
        "uniform sampler2D s_texture;" +
		"vec4 vColor = vec4(1.0,1.0,1.0,1.0);" +
	    "void main() {" +
	    "  vColor.w = vAlfa;" +
	    "  gl_FragColor = vColor*texture2D( s_texture, v_texCoord );" +
	    //"  gl_FragColor = vec4(0.4,0.4,0.4,1.0)*texture2D( s_texture, v_texCoord );" +

	    //"  gl_FragColor = vColor*texture2D( s_texture, v_texCoord );" +
	    "}"; 

	

	public static final String vs_Point =
		"uniform mat4 uMVPMatrix;" +
		"attribute vec4 vPosition;" +
		"attribute vec2 a_texCoord;" +
		"varying vec2 v_texCoord;" +
		"uniform float time;" +
		"varying float time2;" +
	    "void main() {" +
		//"v_texCoord;" +
	    "  gl_Position = uMVPMatrix * vPosition;" +
	    "  gl_PointSize = 10.0 * (1.05 -(time)/3000.0);" +
	    "  v_texCoord = a_texCoord;" +
	    "  time2 = time;" +
	    "}";
	
	public static final String fs_Point =
		"precision mediump float;" +
		"varying float time2;" +
		"varying vec2 v_texCoord;" +
		//"varying mediump vec4 vColor2;" +
        "uniform sampler2D s_texture;" +
        "vec4 vColor = vec4(1.0,1.0,0.0,1.0);" +
		//"uniform sampler2D t_texture;" +
		"void main() {" +
        //"  gl_FragColor = vColor*texture2D( s_texture, v_texCoord );" +
        //"  gl_FragColor = vColor*texture2D( s_texture, v_texCoord );" +
        /*
		"  vColor2 = texture2D(s_texture, gl_PointCoord);" +
		"  vColor.w = vColor2.w;" +
		"  gl_FragColor = vColor;" +
		*/

		"  gl_FragColor = texture2D(s_texture, gl_PointCoord) ;" +
		"  gl_FragColor.xyz = vColor.xyz;" +
		/*
		"  gl_FragColor = texture2D(s_texture, gl_PointCoord) * (1.0 -(time2)/6000.0) ;" +
		"  gl_FragColor.xyz = vColor.xyz;" +
		*/
		//"  gl_FragColor = vColor*texture2D(s_texture, gl_PointCoord);" +
		"}"; 

	
	
	/*
    private final String vertexShaderCode =
            // This matrix member variable provides a hook to manipulate
            // the coordinates of the objects that use this vertex shader
            "uniform mat4 uMVPMatrix;" +
            "attribute vec4 vPosition;" +
            "void main() {" +
            // The matrix must be included as a modifier of gl_Position.
            // Note that the uMVPMatrix factor *must be first* in order
            // for the matrix multiplication product to be correct.
            "  gl_Position = uMVPMatrix * vPosition;" +
            "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
            "uniform vec4 vColor;" +
            "void main() {" +
            "  gl_FragColor = vColor;" +
            "}";
	*/
	public static int loadShader(int type, String shaderCode){

	    // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
	    // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
	    int shader = GLES20.glCreateShader(type);

	    // add the source code to the shader and compile it
	    GLES20.glShaderSource(shader, shaderCode);
	    GLES20.glCompileShader(shader);
	    
	    // return the shader
	    return shader;
	}
}
