package com.olbigames.finddifferencesgames.renderer

import android.opengl.GLES20

object GraphicTools {
    // Program variables
    @JvmField
    var sp_Image = 0
    var sp_Point = 0
    /* SHADER Image
	 *
	 * This shader is for rendering 2D images straight from a texture
	 * No additional effects.
	 *
	 */
    const val vs_Image = "uniform mat4 uMVPMatrix;" +
            "attribute vec4 vPosition;" +
            "attribute vec2 a_texCoord;" +
            "varying vec2 v_texCoord;" +
            "void main() {" +
            "  gl_Position = uMVPMatrix * vPosition;" +
            "  v_texCoord = a_texCoord;" +
            "}"
    const val fs_Image = "precision mediump float;" +
            "varying vec2 v_texCoord;" +
            "uniform float vAlfa;" +
            "uniform sampler2D s_texture;" +
            "vec4 vColor = vec4(1.0,1.0,1.0,1.0);" +
            "void main() {" +
            "  vColor.w = vAlfa;" +
            "  gl_FragColor = vColor*texture2D( s_texture, v_texCoord );" +
            "}"
    const val vs_Point = "uniform mat4 uMVPMatrix;" +
            "attribute vec4 vPosition;" +
            "attribute vec2 a_texCoord;" +
            "varying vec2 v_texCoord;" +
            "uniform float time;" +
            "varying float time2;" +
            "void main() {" +
            "  gl_Position = uMVPMatrix * vPosition;" +
            "  gl_PointSize = 10.0 * (1.05 -(time)/3000.0);" +
            "  v_texCoord = a_texCoord;" +
            "  time2 = time;" +
            "}"
    const val fs_Point = "precision mediump float;" +
            "varying float time2;" +
            "varying vec2 v_texCoord;" +
            "uniform sampler2D s_texture;" +
            "vec4 vColor = vec4(1.0,1.0,0.0,1.0);" +
            "void main() {" +
            "  gl_FragColor = texture2D(s_texture, gl_PointCoord) ;" +
            "  gl_FragColor.xyz = vColor.xyz;}"

    fun loadShader(
        type: Int,
        shaderCode: String?
    ): Int {
        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        val shader = GLES20.glCreateShader(type)
        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode)
        GLES20.glCompileShader(shader)
        // return the shader
        return shader
    }
}