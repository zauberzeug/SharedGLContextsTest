package de.perpetualmobile.sharedglcontexts;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.EGL14;
import android.opengl.GLES11;
import android.opengl.GLES20;
import android.opengl.GLUtils;

// This class holds the shared context magic and is based on informations gathered 
// from the following sources:
//
//   http://www.khronos.org/message_boards/showthread.php/9029-Loading-textures-in-a-background-thread-on-Android
//   http://www.khronos.org/message_boards/showthread.php/5843-Texture-Sharing
//   http://stackoverflow.com/questions/14062803/why-is-eglmakecurrent-failing-with-egl-bad-match
public class TextureLoader extends Thread {

    private EGLContext textureContext;
    private EGL10 egl;
    private EGLConfig eglConfig;
    private EGLDisplay display;
    private PerformanceTimer timer = new PerformanceTimer();
    private Context androidContext;

    public TextureLoader(EGL10 egl, EGLContext renderContext, EGLDisplay display,
            EGLConfig eglConfig, Context androidContext) {
        this.egl = egl;
        this.display = display;
        this.eglConfig = eglConfig;
        this.androidContext = androidContext;

        textureContext = egl.eglCreateContext(display, eglConfig, renderContext, null);
    }

    public void run() {
        int pbufferAttribs[] = { EGL10.EGL_WIDTH, 1, EGL10.EGL_HEIGHT, 1, EGL14.EGL_TEXTURE_TARGET,
                EGL14.EGL_NO_TEXTURE, EGL14.EGL_TEXTURE_FORMAT, EGL14.EGL_NO_TEXTURE,
                EGL10.EGL_NONE };

        EGLSurface localSurface = egl.eglCreatePbufferSurface(display, eglConfig, pbufferAttribs);

        egl.eglMakeCurrent(display, localSurface, localSurface, textureContext);

        sleep(); // delay loading texture to demonstrate threaded loading

        timer.start("loading texture");
        // TODO replace static variable with message passing to inform the
        // renderer about the new texture
        DemoRenderer.textureId = loadTexture(R.drawable.waterfalls);
        timer.stop();
    }

    private void sleep() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
        }
    }

    private int loadTexture(final int resourceId) {
        final int[] textureHandle = new int[1];

        GLES11.glGenTextures(1, textureHandle, 0);
        if (textureHandle[0] != 0) {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false; // No pre-scaling

            // Read in the resource
            final Bitmap bitmap = BitmapFactory.decodeResource(androidContext.getResources(),
                    resourceId, options);

            // Bind to the texture in OpenGL
            GLES11.glBindTexture(GLES11.GL_TEXTURE_2D, textureHandle[0]);

            // Set filtering
            GLES11.glTexParameteri(GLES11.GL_TEXTURE_2D, GLES11.GL_TEXTURE_MIN_FILTER,
                    GLES11.GL_NEAREST);
            GLES11.glTexParameteri(GLES11.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER,
                    GLES11.GL_NEAREST);

            // Load the bitmap into the bound texture.
            GLUtils.texImage2D(GLES11.GL_TEXTURE_2D, 0, bitmap, 0);

            // Recycle the bitmap, since its data has been loaded into OpenGL.
            bitmap.recycle();
        }

        if (textureHandle[0] == 0) {
            throw new RuntimeException("Error loading texture: " + GLES11.glGetError() + ", "
                    + GLES11.glGetString(GLES11.glGetError()));
        }

        return textureHandle[0];
    }
}
