package de.perpetualmobile.sharedglcontexts;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLSurfaceView;
import android.opengl.GLU;

class DemoRenderer implements GLSurfaceView.Renderer {

    private Square square;
    private PerformanceTimer timer = new PerformanceTimer();
    public static int textureId = 0; // TextureLoader will set this later to the
                                     // shared texture id

    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        this.square = new Square();
    }

    public void onSurfaceChanged(GL10 gl, int width, int height) {
        if (height == 0) {
            height = 1;
        }

        gl.glEnable(GL10.GL_TEXTURE_2D);
        gl.glShadeModel(GL10.GL_SMOOTH);
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);
        gl.glClearDepthf(1.0f);
        gl.glEnable(GL10.GL_DEPTH_TEST);
        gl.glDepthFunc(GL10.GL_LEQUAL);

        // Really Nice Perspective Calculations
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);

        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();

        GLU.gluPerspective(gl, 45.0f, (float) width / (float) height, 0.1f, 100.0f);

        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    public void onDrawFrame(GL10 gl) {
        gl.glClearColor(0, 0, 0, 1.0f);
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

        gl.glLoadIdentity();
        gl.glTranslatef(0.0f, 0.0f, -5.0f);

        timer.start("rendering square");
        if (textureId == 0)
            gl.glColor4f(0.4f, 0.4f, 0.4f, 1f);
        else {
            gl.glColor4f(2f, 1f, 1f, 1f);
            gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId);
        }
        square.draw(gl);
        timer.stop();
        if (textureId != 0)
            timer.mute("rendering square");
    }
}