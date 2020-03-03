package com.olbigames.finddifferencesgames.game;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.os.Build;
import com.olbigames.finddifferencesgames.R;
import com.olbigames.finddifferencesgames.helper.SQLiteHelper;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Vector;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GameRenderer implements Renderer {

    // Our matrices
    private final float[] mtrxProjection = new float[16];
    private final float[] mtrxView = new float[16];
    private final float[] mtrxProjectionAndView = new float[16];

    // Our matrices
    private final float[] mtrxProjection2 = new float[16];
    private final float[] mtrxView2 = new float[16];
    private final float[] mtrxProjectionAndView2 = new float[16];

    // Geometric variables
    public static float vertices[];
    public static float vertices2[];
    public static float vertices3[];
    public static float vertices4[];
    public static float vertices5[];
    public static float vertices6[];
    private static float vertices7[];
    public static short indices[];
    public static float uvs[];
    public FloatBuffer vertexBuffer;
    public ShortBuffer drawListBuffer;
    public FloatBuffer uvBuffer;


    private float picScale;

    private float w;
    private float h;
    private float xOts;
    private float yOts;

    private float lineSize = 10f;

    // Our screenresolution
    private float mScreenWidth = 1280;
    private float mScreenHeight = 720;

    private float banner_height;

    // Misc
    Context mContext;
    long mLastTime;
    int mProgram;

    //----My
    public static float bg_vertices[];
    public static short bg_indices[];
    public static float bg_uvs[];
    public FloatBuffer bg_vertexBuffer;
    public ShortBuffer bg_drawListBuffer;
    public FloatBuffer bg_uvBuffer;

    /**
     * Выделяем массив для хранения объединеной матрицы. Она будет передана в программу шейдера.
     */
    private float[] mMVPMatrix = new float[16];
    private float[] mModelMatrix = new float[16];

    RectangleImage rect1;
    RectangleImage rect2;
    RectangleImage rect3;
    RectangleImage rect4;
    RectangleImage rect5;
    RectangleImage rect6;
    RectangleImage rect7;

    private Vector<Traces> traces = new Vector<Traces>();
    int particlesTexId;
    private int pointTime;
    private int pointTexCoordLoc;
    private int pointSamplerLoc;
    private int pointPositionHandle;
    private int pointMVPMatrixHandle;

    private SQLiteHelper db;
    private Differences differences;

    float picW;
    float picH;

    //+++++++++++++++++++++++++++++++++++++++++++++++++++++++
    int fboId;
    int fboTex;
    int fboId2;
    int fboTex2;
    //+++++++++++++++++++++++++++++++++++++++++++++++++++++++

    int level;

    SoundPool sounds;
    int sbeep;
    float volumeLevel = 1f;
    //-------
    private boolean showHiddenHint = false;
    private HiddenHint hiddenHint;
    private boolean ishiddenHintAnimShowing = false;
    private float hintSize;
    private float hintY;
    private float hintX;
    private GLAnimatedObject plus_one;

    public GameRenderer(Context c, int width, int height, int bh, SQLiteHelper db0, int lvl, float vl) {
        volumeLevel = vl;
        mScreenWidth = width;
        mScreenHeight = height;
        banner_height = bh;

        mContext = c;
        mLastTime = System.currentTimeMillis() + 100;

        db = db0;
        differences = new Differences();
        db.getDifferences(differences, lvl);
        //-------HIDDEN HINT------
        HiddenHintData hhd = db.getHiddenHint(lvl);
        if (hhd.f == 0.0f) {
            if (hhd.f == 0) {
                showHiddenHint = true;
            } else {
                showHiddenHint = false;
            }
            hintX = hhd.x;
            hintY = hhd.y;
            hintSize = hhd.r;
        }
        //-------HIDDEN HINT END------
        level = lvl;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            createNewSoundPool();
        } else {
            createOldSoundPool();
        }

        sbeep = sounds.load(mContext, R.raw.beep, 1);
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected void createNewSoundPool() {
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        sounds = new SoundPool.Builder()
                .setMaxStreams(5)
                .setAudioAttributes(attributes)
                .build();
    }

    @SuppressWarnings("deprecation")
    protected void createOldSoundPool() {
        sounds = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // Set the clear color to black
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1);

        // Для прозрачности PNG
        GLES20.glEnable(GLES20.GL_BLEND);
        //GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        // Create the shaders, images
        int vertexShader2 = riGraphicTools.loadShader(GLES20.GL_VERTEX_SHADER, riGraphicTools.vs_Point);
        int fragmentShader2 = riGraphicTools.loadShader(GLES20.GL_FRAGMENT_SHADER, riGraphicTools.fs_Point);

        riGraphicTools.sp_Point = GLES20.glCreateProgram();             // create empty OpenGL ES Program
        GLES20.glAttachShader(riGraphicTools.sp_Point, vertexShader2);   // add the vertex shader to program
        GLES20.glAttachShader(riGraphicTools.sp_Point, fragmentShader2); // add the fragment shader to program
        GLES20.glLinkProgram(riGraphicTools.sp_Point);                  // creates OpenGL ES program executables

        GLES20.glUseProgram(riGraphicTools.sp_Point);

        pointTime = GLES20.glGetUniformLocation(riGraphicTools.sp_Point, "time");
        pointTexCoordLoc = GLES20.glGetUniformLocation(riGraphicTools.sp_Point, "a_texCoord");
        pointSamplerLoc = GLES20.glGetUniformLocation(riGraphicTools.sp_Point, "s_texture");
        pointPositionHandle = GLES20.glGetAttribLocation(riGraphicTools.sp_Point, "vPosition");
        pointMVPMatrixHandle = GLES20.glGetUniformLocation(riGraphicTools.sp_Point, "uMVPMatrix");

        // Create the shaders, images
        int vertexShader = riGraphicTools.loadShader(GLES20.GL_VERTEX_SHADER, riGraphicTools.vs_Image);
        int fragmentShader = riGraphicTools.loadShader(GLES20.GL_FRAGMENT_SHADER, riGraphicTools.fs_Image);

        riGraphicTools.sp_Image = GLES20.glCreateProgram();             // create empty OpenGL ES Program
        GLES20.glAttachShader(riGraphicTools.sp_Image, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(riGraphicTools.sp_Image, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(riGraphicTools.sp_Image);                  // creates OpenGL ES program executables

        // Set our shader programm
        GLES20.glUseProgram(riGraphicTools.sp_Image);

        BitmapFactory.Options dimensions = new BitmapFactory.Options();
        dimensions.inScaled = false;

        Bitmap bmp = null; //MyExpansionHelper.loadPic(mContext, level - 1, 0, 1);

        picW = bmp.getWidth();
        picH = bmp.getHeight();

        float picAspectRatio = picW / picH;

        if (mScreenHeight < mScreenWidth) {
            float h1 = mScreenHeight - banner_height;
            float w1 = h1 * picAspectRatio;
            if (w1 > (mScreenWidth - lineSize) / 2) {
                w = (mScreenWidth - lineSize) / 2;
                h = w / picAspectRatio;
                xOts = 0f;
                //--
                yOts = (mScreenHeight - banner_height - h) / 2;
                //--
            } else {
                w = w1;
                h = h1;
                xOts = (mScreenWidth - lineSize) / 2 - w;
                yOts = 0f;
            }
        } else {
            float h1 = (mScreenHeight - 2 * banner_height - lineSize) / 2;
            float w1 = h1 * picAspectRatio;
            if (w1 > mScreenWidth) {
                w = mScreenWidth;
                h = w / picAspectRatio;
                yOts = (mScreenHeight - 2 * banner_height - lineSize) / 2 - h;
                xOts = 0f;
            } else {
                w = w1;
                h = h1;
                xOts = (mScreenWidth - w) / 2;
                yOts = 0;
            }
        }

        picScale = h / picH;

        if (mScreenHeight < mScreenWidth) {
            vertices = new float[]
                    {xOts, yOts + h, 0.0f,
                            xOts, yOts, 0.0f,
                            xOts + w, yOts, 0.0f,
                            xOts + w, yOts + h, 0.0f
                    };
        } else {
            vertices = new float[]
                    {xOts, yOts + h, 0.0f,
                            xOts, yOts, 0.0f,
                            xOts + w, yOts, 0.0f,
                            xOts + w, yOts + h, 0.0f
                    };
        }

        rect1 = new RectangleImage(vertices, bmp, 0);

        //+++++++++++++++++++++++++++++++++++++++++++++++++++++++

        int[] temp = new int[1];
        //generate fbo id
        GLES20.glGenFramebuffers(1, temp, 0);
        fboId = temp[0];
        //generate texture

        GLES20.glGenTextures(1, temp, 0);
        fboTex = temp[0];

        //int fboTex = GLES20.GL_TEXTURE1;//-----MY

        //generate render buffer
        GLES20.glGenRenderbuffers(1, temp, 0);
        int renderBufferId = temp[0];
        //Bind Frame buffer
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fboId);
        //Bind texture
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, fboTex);
        //Define texture parameters

        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, (int) picW, (int) picH, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        //Bind render buffer and define buffer dimension
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, renderBufferId);
        GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT16, (int) picW, (int) picH);
        //Attach texture FBO color attachment
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, fboTex, 0);
        //Attach render buffer to depth attachment
        GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT, GLES20.GL_RENDERBUFFER, renderBufferId);
        //we are done, reset
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

        //+++++++++++++++++++++++++++++++++++++++++++++++++++++++

        if (mScreenHeight < mScreenWidth) {
            vertices2 = new float[]
                    {mScreenWidth - xOts - w, yOts + h, 0.0f,
                            mScreenWidth - xOts - w, yOts, 0.0f,
                            mScreenWidth - xOts, yOts, 0.0f,
                            mScreenWidth - xOts, yOts + h, 0.0f
                    };
        } else {
            vertices2 = new float[]
                    {xOts, mScreenHeight - 2 * banner_height - yOts, 0.0f,
                            xOts, mScreenHeight - 2 * banner_height - h - yOts, 0.0f,
                            xOts + w, mScreenHeight - 2 * banner_height - h - yOts, 0.0f,
                            xOts + w, mScreenHeight - 2 * banner_height - yOts, 0.0f
                    };
        }


        Bitmap bmp2 = null; //MyExpansionHelper.loadPic(mContext, level - 1, 0, 2);
        rect2 = new RectangleImage(vertices2, bmp2, 1);

//+++++++++++++++++++++++++++++++++++++++++++++++++++++++

        temp = new int[1];
//generate fbo id
        GLES20.glGenFramebuffers(1, temp, 0);
        fboId2 = temp[0];
//generate texture

        GLES20.glGenTextures(1, temp, 0);
        fboTex2 = temp[0];

//int fboTex = GLES20.GL_TEXTURE1;//-----MY

//generate render buffer
        GLES20.glGenRenderbuffers(1, temp, 0);
        int renderBufferId2 = temp[0];
//Bind Frame buffer
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fboId2);
//Bind texture
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, fboTex2);
//Define texture parameters

        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, (int) picW, (int) picH, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
//Bind render buffer and define buffer dimension
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, renderBufferId2);
        GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT16, (int) picW, (int) picH);
//Attach texture FBO color attachment
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, fboTex2, 0);
//Attach render buffer to depth attachment
        GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT, GLES20.GL_RENDERBUFFER, renderBufferId2);
//we are done, reset
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

        vertices4 = new float[]
                {0f, 0f, 0.0f,
                        0f, picH, 0.0f,
                        picW, picH, 0.0f,
                        picW, 0f, 0.0f
                };

        rect4 = new RectangleImage(vertices4, bmp, 4);

        vertices5 = new float[]
                {0f, 0f, 0.0f,
                        0f, picH, 0.0f,
                        picW, picH, 0.0f,
                        picW, 0f, 0.0f
                };
        rect5 = new RectangleImage(vertices5, bmp2, 5);
        bmp.recycle();
        bmp2.recycle();

//+++++++++++++++++++++++++++++++++++++++++++++++++++++++

        vertices3 = new float[]
                {-1f, 1f, 0.0f,
                        -1f, -1f, 0.0f,
                        1f, -1f, 0.0f,
                        1f, 1f, 0.0f
                };
        int id = mContext.getResources().getIdentifier("raw/circle", null, mContext.getPackageName());
        bmp = BitmapFactory.decodeResource(mContext.getResources(), id);
        rect3 = new RectangleImage(vertices3, bmp, 2);
        bmp.recycle();

        //----------------------------------particlesImage
        id = mContext.getResources().getIdentifier("raw/particles", null, mContext.getPackageName());
        bmp = BitmapFactory.decodeResource(mContext.getResources(), id, dimensions);

        // Generate Textures, if more needed, alter these numbers.
        int[] texturenames = new int[1];
        GLES20.glGenTextures(1, texturenames, 0);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE6);
        particlesTexId = texturenames[0];
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, particlesTexId);

        // Set filtering
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

        // Load the bitmap into the bound texture.
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);

        bmp.recycle();
        //----------------------------------particlesImageEnd


        //------------Hidden Hint-------------
        vertices6 = new float[]
                {
                        -1f, -1f, 0.0f,
                        1f, -1f, 0.0f,
                        1f, 1f, 0.0f,
                        -1f, 1f, 0.0f
                };
        vertices7 = new float[]
                {-1f, 1f, 0.0f,
                        -1f, -1f, 0.0f,
                        1f, -1f, 0.0f,
                        1f, 1f, 0.0f
                };
        id = mContext.getResources().getIdentifier("raw/hidden_hint", null, mContext.getPackageName());
        bmp = BitmapFactory.decodeResource(mContext.getResources(), id);
        rect6 = new RectangleImage(vertices6, bmp, 7);
        bmp.recycle();
        id = mContext.getResources().getIdentifier("raw/plus_one", null, mContext.getPackageName());
        bmp = BitmapFactory.decodeResource(mContext.getResources(), id);
        rect7 = new RectangleImage(vertices7, bmp, 8);
        bmp.recycle();

        plus_one = new GLAnimatedObject(mScreenWidth - 1.5f * banner_height, 0.0f, rect7, banner_height / 2);
        plus_one.moveWithShade(mScreenWidth - 1.5f * banner_height, 1.5f * banner_height, 1500L);
        //------------Hidden Hint End---------

//+++++++++++++++++++++++++++++++++++++++++++++++++++++++
        // Redo the Viewport, making it fullscreen.
        GLES20.glViewport(0, 0, (int) picW, (int) picH);

        // Clear our matrices
        for (int i = 0; i < 16; i++) {
            mtrxProjection2[i] = 0.0f;
            mtrxView2[i] = 0.0f;
            mtrxProjectionAndView2[i] = 0.0f;
        }

        // Setup our screen width and height for normal sprite translation.
        Matrix.orthoM(mtrxProjection2, 0, 0f, picW, 0.0f, picH, 0, 50);

        // Set the camera position (View matrix)
        Matrix.setLookAtM(mtrxView2, 0, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        // Calculate the projection and view transformation
        Matrix.multiplyMM(mtrxProjectionAndView2, 0, mtrxProjection2, 0, mtrxView2, 0);
//+++++++++++++++++++++++++++++++++++++++++++++++++++++++
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // We need to know the current width and height.
        mScreenWidth = width;
        mScreenHeight = height;
        // Redo the Viewport, making it fullscreen.
        GLES20.glViewport(0, 0, (int) mScreenWidth, (int) mScreenHeight);

        // Clear our matrices
        for (int i = 0; i < 16; i++) {
            mtrxProjection[i] = 0.0f;
            mtrxView[i] = 0.0f;
            mtrxProjectionAndView[i] = 0.0f;
        }

        // Setup our screen width and height for normal sprite translation.
        Matrix.orthoM(mtrxProjection, 0, 0f, mScreenWidth, 0.0f, mScreenHeight, 0, 50);

        // Set the camera position (View matrix)
        Matrix.setLookAtM(mtrxView, 0, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        // Calculate the projection and view transformation
        Matrix.multiplyMM(mtrxProjectionAndView, 0, mtrxProjection, 0, mtrxView, 0);
    }


    public void onPause() {
        /* Do stuff to pause the renderer */
    }

    public void onResume() {
        /* Do stuff to resume the renderer */
        mLastTime = System.currentTimeMillis();
    }

    @Override
    public void onDrawFrame(GL10 unused) {
        long now = System.currentTimeMillis();
        if (mLastTime > now) return;
        long elapsed = now - mLastTime;

        differences.update_anim(elapsed);
        for (int i = 0; i < traces.size(); i++) {
            boolean destroy = traces.elementAt(i).timeAdd(elapsed);
            if (destroy) traces.remove(i);
        }

        plus_one.update(elapsed);

        if (showHiddenHint) {
            if (ishiddenHintAnimShowing) {
                if (hiddenHint.timeAdd(elapsed)) {
                    showHiddenHint = false;
                    plus_one.start();
                }

            }
        }
        Render(mtrxProjectionAndView);
        mLastTime = now;
    }

    private void drawInTexture(int toFboId, RectangleImage rect) {

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, toFboId);
        GLES20.glViewport(0, 0, (int) picW, (int) picH);

        GLES20.glClearColor(.0f, .0f, .0f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        GLES20.glUseProgram(riGraphicTools.sp_Image);

        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mtrxView2, 0, mModelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mtrxProjection2, 0, mMVPMatrix, 0);
        rect.draw(mMVPMatrix, 1.0f);

        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        //----Draw Hidden Hint Start-------
        if (showHiddenHint) {
            if (!ishiddenHintAnimShowing) {

                //GLKMatrix4 modelViewMatrix9 = GLKMatrix4Translate(modelViewMatrix, picW - hintX, hintY, 0.0f);

                Matrix.setIdentityM(mModelMatrix, 0);
                Matrix.translateM(mModelMatrix, 0, hintX, hintY, 0.0f);
                Matrix.scaleM(mModelMatrix, 0, hintSize, hintSize, 1);
                Matrix.multiplyMM(mMVPMatrix, 0, mtrxView2, 0, mModelMatrix, 0);
                Matrix.multiplyMM(mMVPMatrix, 0, mtrxProjection2, 0, mMVPMatrix, 0);
                rect6.draw(mMVPMatrix, 1.0f);
            }
        }

        //----Draw Hidden Hint End---------

        for (int i = 0; i < differences.count; i++) {
            if (differences.finded[i] == 1) {

                Matrix.setIdentityM(mModelMatrix, 0);
                Matrix.translateM(mModelMatrix, 0, differences.x[i], differences.y[i], 0.0f);
                Matrix.scaleM(mModelMatrix, 0, differences.r[i], differences.r[i], 1);
                Matrix.multiplyMM(mMVPMatrix, 0, mtrxView2, 0, mModelMatrix, 0);
                Matrix.multiplyMM(mMVPMatrix, 0, mtrxProjection2, 0, mMVPMatrix, 0);
                rect3.draw(mMVPMatrix, differences.getAlpha(i));

            }
        }
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        GLES20.glUseProgram(riGraphicTools.sp_Point);

        if (traces.size() > 0) {

            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE);

            Matrix.setIdentityM(mModelMatrix, 0);
            Matrix.multiplyMM(mMVPMatrix, 0, mtrxView2, 0, mModelMatrix, 0);
            Matrix.multiplyMM(mMVPMatrix, 0, mtrxProjection2, 0, mMVPMatrix, 0);

            int w = 0;
            do {

                GLES20.glEnableVertexAttribArray(pointTexCoordLoc);
                GLES20.glVertexAttribPointer(pointTexCoordLoc, 2, GLES20.GL_FLOAT, false, 0, traces.elementAt(w).uvsBuffer);

                GLES20.glUniform1i(pointSamplerLoc, 6);

                GLES20.glEnableVertexAttribArray(pointPositionHandle);
                GLES20.glVertexAttribPointer(pointPositionHandle, 2, GLES20.GL_FLOAT, false, 0, traces.elementAt(w).vectorBuffer);


                GLES20.glUniform1f(pointTime, traces.elementAt(w).gettime());
                GLES20.glUniformMatrix4fv(pointMVPMatrixHandle, 1, false, mMVPMatrix, 0);


                GLES20.glDrawArrays(GLES20.GL_POINTS, 0, traces.elementAt(w).col());

                GLES20.glDisableVertexAttribArray(pointPositionHandle);
                GLES20.glDisableVertexAttribArray(pointTexCoordLoc);

                w++;
            } while (w < traces.size());

            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        }
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
    }

    private void Render(float[] m) {
        drawInTexture(fboId, rect4);
        drawInTexture(fboId2, rect5);

// Redo the Viewport, making it fullscreen.
        GLES20.glViewport(0, 0, (int) mScreenWidth, (int) mScreenHeight);

//+++++++++++++++++++++++++++++++++++++++++++++++++++++++

        // clear Screen and Depth Buffer, we have set the clear color as black.
        GLES20.glClearColor(.0f, .0f, .0f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        //GLES20.glReadPixels(x, y, width, height, format, type, pixels)
        GLES20.glUseProgram(riGraphicTools.sp_Image);

        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mtrxView, 0, mModelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mtrxProjection, 0, mMVPMatrix, 0);

//+++++++++++++++++++++++++++++++++++++++++++++++++++++++
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, fboTex);
//+++++++++++++++++++++++++++++++++++++++++++++++++++++++  

        rect1.draw(mMVPMatrix);

//+++++++++++++++++++++++++++++++++++++++++++++++++++++++
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, fboTex2);
//+++++++++++++++++++++++++++++++++++++++++++++++++++++++  

        rect2.draw(mMVPMatrix);

//+++++++++++++++++++++++++++++++++++++++++++++++++++++++  
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
//+++++++++++++++++++++++++++++++++++++++++++++++++++++++  


//----Draw Hidden Hint Start-------
        if (showHiddenHint) {
            if (ishiddenHintAnimShowing) {

                //Log.e("Draw Hidden Hint", "Draw Hidden Hint");


                //----Draw Hidden Hint traces

                GLES20.glUseProgram(riGraphicTools.sp_Point);
                GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE);

                //float[] array2 = hiddenHint.getvector();

                Matrix.setIdentityM(mModelMatrix, 0);
                Matrix.multiplyMM(mMVPMatrix, 0, mtrxView, 0, mModelMatrix, 0);
                Matrix.multiplyMM(mMVPMatrix, 0, mtrxProjection, 0, mMVPMatrix, 0);

                GLES20.glEnableVertexAttribArray(pointTexCoordLoc);
                GLES20.glVertexAttribPointer(pointTexCoordLoc, 2, GLES20.GL_FLOAT, false, 0, rect6.uvBuffer);

                GLES20.glUniform1i(pointSamplerLoc, 6);

                GLES20.glEnableVertexAttribArray(pointPositionHandle);
                GLES20.glVertexAttribPointer(pointPositionHandle, 2, GLES20.GL_FLOAT, false, 0, hiddenHint.getvector());

                GLES20.glUniform1f(pointTime, 1500.0f + (hiddenHint.gettime() / 2.0f));
                GLES20.glUniformMatrix4fv(pointMVPMatrixHandle, 1, false, mMVPMatrix, 0);

                GLES20.glDrawArrays(GLES20.GL_POINTS, 0, hiddenHint.col());

                GLES20.glDisableVertexAttribArray(pointPositionHandle);
                GLES20.glDisableVertexAttribArray(pointTexCoordLoc);

                GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
                GLES20.glUseProgram(riGraphicTools.sp_Image);

                //----Draw Hidden Hint traces end


                hintX = hiddenHint.getX();
                hintY = hiddenHint.getY();

                Matrix.setIdentityM(mModelMatrix, 0);
                Matrix.translateM(mModelMatrix, 0, hintX, mScreenHeight - hintY, 0.0f);
                Matrix.scaleM(mModelMatrix, 0, hintSize * hiddenHint.getScale(), hintSize * hiddenHint.getScale(), 1);
                Matrix.rotateM(mModelMatrix, 0, 270.0f, 0, 0, 1.0f);
                //modelViewMatrix9 = GLKMatrix4Rotate(modelViewMatrix9, 3.14f, 0, 0, 1);
                Matrix.multiplyMM(mMVPMatrix, 0, mtrxView, 0, mModelMatrix, 0);
                Matrix.multiplyMM(mMVPMatrix, 0, mtrxProjection, 0, mMVPMatrix, 0);
                rect6.draw(mMVPMatrix, 1.0f);

            }
        }
        plus_one.draw(mModelMatrix, mtrxView, mtrxProjection, mMVPMatrix, 1.0f);
//----Draw Hidden Hint End---------
    }

    public void Touched(float x, float y) {

        y = mScreenHeight - y;

        int side = 0;
        float xx = -1f;
        float yy = -1f;
        if (vertices[3] < x && vertices[9] > x && vertices[4] < y && vertices[10] > y) {
            xx = (x - vertices[3]) / picScale;
            yy = (y - vertices[4]) / picScale;
            side = 1;
        }
        if (vertices2[3] < x && vertices2[9] > x && vertices2[4] < y && vertices2[10] > y) {
            xx = (x - vertices2[3]) / picScale;
            yy = (y - vertices2[4]) / picScale;
            side = 2;
        }

        if (xx != -1f & rect1 != null) {
            xx = rect1.getTransX() * picW + xx * rect1.getScale();
            yy = rect1.getTransY() * picH + (picH - yy) * rect1.getScale();

            int result = differences.check((int) xx, (int) (yy));
            if (result != -1) {
                sounds.play(sbeep, volumeLevel, volumeLevel, 0, 0, 1.0f);
                db.setDifferences(result);
                traces.add(new Traces(result, differences.getXid(result), differences.getYid(result)));//добавляем эффект
            } else {
                if (showHiddenHint) {
                    if (hintX - hintSize < xx && xx < hintX + hintSize && hintY - hintSize < yy && yy < hintY + hintSize) {

                        float gx = 0;
                        float gy = 0;
                        if (side == 2) {
                            gx = (vertices[3] + (((hintX - rect1.getTransX() * picW) / rect1.getScale()) * picScale));
                            gy = ((((hintY - rect1.getTransY() * picH) / rect1.getScale()) * picScale) + vertices[4]);
                            //gy = (vertices[10] - (((picH - hintY - rect1.getTransY()*picH) / rect1.getScale()) * picScale));
                        } else if (side == 1) {
                            gx = (vertices2[3] + (((hintX - rect1.getTransX() * picW) / rect1.getScale()) * picScale));
                            gy = ((((hintY - rect1.getTransY() * picH) / rect1.getScale()) * picScale) + vertices2[4]);// + banner_height
                        }
                        hiddenHint = new HiddenHint(mScreenWidth - 1.5f * banner_height, mScreenHeight, hintSize, 0, gx, gy, picScale / rect1.getScale(), mScreenHeight);
                        this.hiddenHintFounded();
                    }
                }
            }
        }
    }

    public void hiddenHintFounded() {
        db.addHint(1);
        db.setHiddenHintFinded(level);

        ishiddenHintAnimShowing = true;
        hiddenHint.startAnim();
        sounds.play(sbeep, volumeLevel, volumeLevel, 0, 0, 1.0f);
    }


    public void doMove(float x, float y) {
        if (rect1 != null) {
            rect1.translate(x, y);
        }
        if (rect2 != null) {
            rect2.translate(x, y);
        }
    }

    public void doScale(float x) {
        if (rect1 != null) {
            rect1.scale(x);
        }
        if (rect2 != null) {
            rect2.scale(x);
        }
    }

    public void useHint() {
        rect1.reset();
        rect2.reset();
        int id = differences.getRandomDif();
        if (id != -1) {
            differences.find(id);
            db.setDifferences(id);
            db.subtractOneHint();
            traces.add(new Traces(id, differences.getXid(id), differences.getYid(id)));//добавляем эффект
            sounds.play(sbeep, volumeLevel, volumeLevel, 0, 0, 1.0f);
        }
    }


    public static void checkGlError(String glOperation) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            throw new RuntimeException(glOperation + ": glError " + error);
        }
    }

    public void Finish() {
        GLES20.glFinish();
    }
}