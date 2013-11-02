package de.perpetualmobile.sharedglcontexts;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;

import android.app.Activity;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.Window;

public class MainActivity extends Activity {

    private GLSurfaceView mGLView;
    private static Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.context = this;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mGLView = new DemoSurfaceView(this);
        setContentView(mGLView);
    }
}

class DemoSurfaceView extends GLSurfaceView {
    DemoRenderer mRenderer;

    public DemoSurfaceView(final Context context) {
        super(context);

        setEGLContextFactory(new EGLContextFactory() {

            @Override
            public void destroyContext(EGL10 egl, EGLDisplay display, EGLContext context) {
                egl.eglDestroyContext(display, context);
            }

            @Override
            public EGLContext createContext(final EGL10 egl, final EGLDisplay display,
                    final EGLConfig eglConfig) {

                EGLContext renderContext = egl.eglCreateContext(display, eglConfig,
                        EGL10.EGL_NO_CONTEXT, null);

                new TextureLoader(egl, renderContext, display, eglConfig, getContext()).start();

                return renderContext;
            }
        });

        mRenderer = new DemoRenderer();
        setRenderer(mRenderer);

    }
}