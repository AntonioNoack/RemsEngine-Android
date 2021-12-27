package me.anno.gpu;

import me.anno.ecs.prefab.Prefab;
import me.anno.ecs.prefab.change.Path;
import me.anno.io.files.FileReference;
import me.anno.io.files.InvalidRef;
import me.anno.io.zip.InnerFolder;
import me.anno.io.zip.InnerPrefabFile;
import me.anno.mesh.obj.OBJReader2;
import me.anno.ui.base.Panel;
import me.anno.utils.io.ResourceHelper;
import me.anno.utils.structures.maps.KeyPairMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.io.*;

import static org.lwjgl.opengl.GL11.*;

/**
 * Showcases how you can use multithreading in a GLFW application in order to
 * separate the (blocking) winproc handling from the render loop.
 *
 * @author Kai Burjack
 * <p>
 * modified by Antonio Noack
 * including all os natives has luckily only very few overhead :) (&lt; 1 MiB)
 * (e.g. add 1 to the pointer)
 */
public class GFXBase0x {

    // renderFrame0()
    // next
    // renderStep0()
    // GFX.gameInit.invoke();
    // loop: renderStep()
    // GFX.onShutdown.invoke();

    public static boolean enableVsync = true;
    private static int lastVsyncInterval = -1;

    public static void setVsyncEnabled(boolean enabled) {
        enableVsync = enabled;
    }

    public static void toggleVsync() {
        enableVsync = !enableVsync;
    }

    private static final Logger LOGGER = LogManager.getLogger(GFXBase0x.class);

    public String title = "X";

    public long window;
    public int width = 800;
    public int height = 700;
    public final Object glfwLock = new Object();
    public final Object openglLock = new Object();
    public boolean destroyed;

    public boolean isInFocus = false;
    public boolean isMinimized = false;
    public boolean needsRefresh = true;

    public float contentScaleX = 1f;
    public float contentScaleY = 1f;

    // public GLCapabilities capabilities;

    public Robot robot = null;

    public void loadRenderDoc() {
        // is RenderDoc supported for Android?? mmh..
        // must be executed before OpenGL-init
        /*String renderDocPath = DefaultConfig.INSTANCE.get("debug.renderdoc.path", "C:/Program Files/RenderDoc/renderdoc.dll");
        boolean renderDocEnabled = DefaultConfig.INSTANCE.get("debug.renderdoc.enabled", Build.INSTANCE.isDebug());
        if (renderDocEnabled) {
            try {
                // if renderdoc is install on linux, or given in the path, we could use it as well with loadLibrary()
                // at least this is the default location for RenderDoc
                if (new File(renderDocPath).exists()) {
                    System.load(renderDocPath);
                } else LOGGER.warn("Did not find RenderDoc, searched '" + renderDocPath + "'");
            } catch (Exception e) {
                LOGGER.warn("Could not initialize RenderDoc");
                e.printStackTrace();
            }
        }*/
    }

    public void run() {
        try {

            loadRenderDoc();

            init();
            windowLoop();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void init() {

        LOGGER.info("Using LWJGL Version ?");

        // Clock tick = new Clock();
        // todo error callback?
        // glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));
        // tick.stop("error callback");

        // GFXBase1.Companion.setIcon(window);

        robot = new Robot();

    }

    public void setTitle(String title) {
        newTitle = title;
    }

    private void setNewTitle(String title) {
        // not supported (?)
    }

    /*public void addCallbacks() {
        glfwSetKeyCallback(window, keyCallback = new GLFWKeyCallback() {
            public void invoke(long window, int key, int scancode, int action, int mods) {
                if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                    glfwSetWindowShouldClose(window, true);
            }
        });
        glfwSetFramebufferSizeCallback(window, fsCallback = new GLFWFramebufferSizeCallback() {
            public void invoke(long window, int w, int h) {
                if (w > 0 && h > 0 && (w != width || h != height)) {
                    StudioBase.Companion.addEvent(() -> {
                        width = w;
                        height = h;
                        Input.INSTANCE.invalidateLayout();
                        return Unit.INSTANCE;
                    });
                }
            }
        });

        glfwSetWindowFocusCallback(window, (long window, boolean isInFocus0) -> isInFocus = isInFocus0);
        glfwSetWindowIconifyCallback(window, (long window, boolean isMinimized0) -> {
            isMinimized = isMinimized0;
            // just be sure in case the OS/glfw don't send it
            if (isMinimized0) needsRefresh = true;
        });
        glfwSetWindowRefreshCallback(window, (long window) -> needsRefresh = true);

        // can we use that?
        // glfwSetWindowMaximizeCallback()


        float[] x = {1f};
        float[] y = {1f};
        glfwGetWindowContentScale(window, x, y);
        contentScaleX = x[0];
        contentScaleY = y[0];

        // todo when the content scale changes, we probably should scale our text automatically as well
        // this happens, when the user moved the window from a display with dpi1 to a display with different dpi
        glfwSetWindowContentScaleCallback(window, (long window, float xScale, float yScale) -> {
            LOGGER.info("Window Content Scale changed: " + xScale + " x " + yScale);
            contentScaleX = xScale;
            contentScaleY = yScale;
        });

    }*/

    public void requestAttention() {
        // not supported (?)
    }

    public void requestAttentionMaybe() {
        if (!isInFocus) {
            requestAttention();
        }
    }

    protected void forceUpdateVsync() {
        // not supported?
        int targetInterval = isInFocus ? enableVsync ? 1 : 0 : 2;
        // glfwSwapInterval(targetInterval);
        lastVsyncInterval = targetInterval;
    }

    private void updateVsync() {
        int targetInterval = isInFocus ? enableVsync ? 1 : 0 : 2;
        if (lastVsyncInterval != targetInterval) {
            // not supported?
            // glfwSwapInterval(targetInterval);
            lastVsyncInterval = targetInterval;
        }
    }

    /*private void runRenderLoop() {

        Clock tick = new Clock();

        // glfwMakeContextCurrent(window);
        // updateVsync();

        // tick.stop("Make context current + vsync");

        // capabilities = GL.createCapabilities();

        tick.stop("OpenGL initialization");

        setupDebugging();

        renderFrame0();

        glfwSwapBuffers(window);

        renderStep0();

        GFX.gameInit.invoke();

        long lastTime = System.nanoTime();

        while (!destroyed) {

            synchronized (openglLock) {
                renderStep();
            }

            synchronized (glfwLock) {
                if (!destroyed) {
                    glfwSwapBuffers(window);
                    updateVsync();
                }
            }

            if (!isInFocus || isMinimized) {

                // enforce 30 fps, because we don't need more
                // and don't want to waste energy
                long currentTime = System.nanoTime();
                long waitingTime = 30 - (currentTime - lastTime) / 1_000_000;
                lastTime = currentTime;

                if (waitingTime > 0) try {
                    // wait does not work, causes IllegalMonitorState exception
                    Thread.sleep(waitingTime);
                } catch (InterruptedException ignored) {
                }

            }
        }

        GFX.onShutdown.invoke();

    }
*/
    private void setupDebugging() {
        /*debugProc = GLUtil.setupDebugMessageCallback(
                new PrintStream(new OutputStream() {
                    // parse the message instead
                    // [LWJGL] OpenGL debug message
                    // ID: 0x1
                    // Source: compiler
                    // Type: other
                    // Severity: notification
                    // Message: ...
                    private final Logger LOGGER = LogManager.getLogger("LWJGL");
                    private String id, source, type, severity;
                    private StringBuilder line = new StringBuilder();

                    @Override
                    public void write(int i) {
                        switch (i) {
                            case '\r':
                                break;// idc
                            case '\n':
                                String info = line.toString().trim();
                                if (!info.startsWith("[LWJGL]")) {
                                    int index = info.indexOf(':');
                                    if (index > 0) {
                                        String key = info.substring(0, index).trim().toLowerCase();
                                        String value = info.substring(index + 1).trim();
                                        switch (key) {
                                            case "id":
                                                id = value;
                                                break;
                                            case "source":
                                                source = value;
                                                break;
                                            case "type":
                                                type = value;
                                                break;
                                            case "severity":
                                                severity = value;
                                                break;
                                            case "message":
                                                String printedMessage = value + " ID: " + id + " Source: " + source;
                                                if (!"NOTIFICATION".equals(severity))
                                                    printedMessage += " Severity: " + severity;
                                                switch (type == null ? "" : type.toLowerCase()) {
                                                    case "error":
                                                        LOGGER.error(printedMessage);
                                                        break;
                                                    case "other":
                                                        LOGGER.info(printedMessage);
                                                        break;
                                                    default:
                                                        printedMessage += " Type: " + type;
                                                        LOGGER.info(printedMessage);
                                                }
                                                id = null;
                                                source = null;
                                                type = null;
                                                severity = null;
                                                break;
                                        }
                                    } else if (!info.isEmpty()) {
                                        // awkward...
                                        LOGGER.info(info);
                                    }
                                } // else idc
                                // LOGGER.info(line.toString());
                                line = new StringBuilder();
                                break;
                            default:
                                final int maxLength = 500 - 3;
                                final int length = line.length();
                                if (length < maxLength) {
                                    line.append((char) i);
                                } else if (length == maxLength) {
                                    line.append("...");
                                }// else too many chars, we don't care ;)
                        }
                    }
                }));*/
    }

    public void renderStep0() {
        // RenderDoc is more complicated on Android, so skip it
        GFX.INSTANCE.checkIsGFXThread();
    }

    // can be set by the application
    public int frame0BackgroundColor = 0;
    public int frame0IconColor = 0x172040;

    public void renderFrame0() {

        // load icon.obj as file, and draw it using OpenGL 1.0

        int c = frame0BackgroundColor;
        glClearColor(((c >> 16) & 255) / 255f, ((c >> 8) & 255) / 255f, (c & 255) / 255f, 1f);
        glClear(GL_COLOR_BUFFER_BIT);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();

        float aspect = (float) width / height;
        glOrtho(-aspect, aspect, -1f, +1f, -1f, +1f);
        c = frame0IconColor;
        glColor3f(((c >> 16) & 255) / 255f, ((c >> 8) & 255) / 255f, (c & 255) / 255f);

        try {
            InputStream stream = ResourceHelper.INSTANCE.loadResource("icon.obj");
            OBJReader2 reader = new OBJReader2(stream, InvalidRef.INSTANCE);
            if (reader.getMeshesFolder().isInitialized()) {
                InnerFolder file = reader.getMeshesFolder().getValue();
                for (FileReference child : file.listChildren()) {
                    // we could use the name as color... probably a nice idea :)
                    Prefab prefab = ((InnerPrefabFile) child).getPrefab();
                    KeyPairMap<Path, String, Object> sets = prefab.getSets();
                    float[] positions = (float[]) sets.get(Path.Companion.getROOT_PATH(), "positions");
                    int[] indices = (int[]) sets.get(Path.Companion.getROOT_PATH(), "indices");
                    if (positions != null) {
                        glBegin(GL_TRIANGLES);
                        if (indices == null) {
                            for (int i = 0; i < positions.length; i += 3) {
                                glVertex2f(positions[i], positions[i + 1]);
                            }
                        } else {
                            for (int index : indices) {
                                int j = index * 3;
                                glVertex2f(positions[j], positions[j + 1]);
                            }
                        }
                        glEnd();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void renderStep() {

        glClear(GL_COLOR_BUFFER_BIT);

        float elapsed = 0.001667f;

        float aspect = (float) width / height;
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(-aspect, aspect, -1f, +1f, -1f, +1f);

        glMatrixMode(GL_MODELVIEW);
        glRotatef(elapsed * 10f, 0, 0, 1);
        glBegin(GL_QUADS);
        glVertex2f(-0.5f, -0.5f);
        glVertex2f(+0.5f, -0.5f);
        glVertex2f(+0.5f, +0.5f);
        glVertex2f(-0.5f, +0.5f);
        glEnd();

    }

    public static String projectName = "X";
    private String newTitle = null;
    boolean shouldClose = false;

    public Panel trapMousePanel;
    public float trapMouseRadius = 250f;

    public double mouseTargetX = -1.0, mouseTargetY = -1.0;

    public boolean isMouseTrapped() {
        return trapMousePanel != null && isInFocus && trapMousePanel == GFX.INSTANCE.getInFocus0();
    }

    public void moveMouseTo(float x, float y) {
        mouseTargetX = x;
        mouseTargetY = y;
    }

    public void moveMouseTo(double x, double y) {
        mouseTargetX = x;
        mouseTargetY = y;
    }

    void windowLoop() {

        /*Thread.currentThread().setName("GLFW");

        // Start new thread to have the OpenGL context current in and which does the rendering.
        new Thread(() -> {
            runRenderLoop();
            cleanUp();
        }).start();

        boolean cursorIsHidden = false;*/

        /*new Thread(() -> {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException ignored) { }
            requestExit();
        }).start();*/

        /*while (!shouldClose) {
            while (!glfwWindowShouldClose(window) && !shouldClose) {
                // update title, if necessary
                if (newTitle != null) {
                    setNewTitle(newTitle);
                    newTitle = null;
                }
                // trapping the mouse
                if (isMouseTrapped()) {
                    if (!cursorIsHidden) {
                        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_HIDDEN);
                        cursorIsHidden = true;
                    }
                    float x = Input.INSTANCE.getMouseX();
                    float y = Input.INSTANCE.getMouseY();
                    float centerX = GFX.INSTANCE.getWindowWidth() * 0.5f;
                    float centerY = GFX.INSTANCE.getWindowHeight() * 0.5f;
                    float dx = x - centerX;
                    float dy = y - centerY;
                    if (dx * dx + dy * dy > trapMouseRadius * trapMouseRadius) {
                        glfwSetCursorPos(window, centerX, centerY);
                    }
                } else if (cursorIsHidden) {
                    glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
                    cursorIsHidden = false;
                }
                if (mouseTargetX != -1.0 && mouseTargetY != -1.0) {
                    if (isInFocus) {
                        glfwSetCursorPos(window, mouseTargetX, mouseTargetY);
                    } else if (robot != null) {
                        int[] x = new int[1];
                        int[] y = new int[1];
                        glfwGetWindowPos(window, x, y);
                        robot.mouseMove((int) mouseTargetX + x[0], (int) mouseTargetY + y[0]);
                    }
                    mouseTargetX = -1.0;
                    mouseTargetY = -1.0;
                }
                // only happens, if keyboard or mouse is used
                glfwWaitEventsTimeout(1.0 / 240.0);// timeout, because otherwise it sleeps forever, until keyboard
                // or mouse input is received
            }
            // close tests
            if (DefaultConfig.INSTANCE.get("window.close.directly", false)) {
                break;
            } else {
                glfwSetWindowShouldClose(window, false);
                GFX.INSTANCE.addGPUTask(1, () -> {
                    Menu.INSTANCE.ask(new NameDesc("Close %1?", "", "ui.closeProgram")
                            .with("%1", projectName), () -> {
                        shouldClose = true;
                        glfwSetWindowShouldClose(window, true);
                        return null;
                    });
                    Input.INSTANCE.invalidateLayout();
                    GFX.INSTANCE.getWindowStack().peek().setAcceptsClickAway(false);
                    return null;
                });
            }
        }*/

    }

    public void requestExit() {
        // todo android request exit
    }

    public boolean isFramebufferTransparent() {
        return false;
    }

    /**
     * transparency of the whole window including decoration (buttons, icon and title)
     * window transparency is incompatible with transparent framebuffers!
     * may not succeed, test with getWindowTransparency()
     */
    public void setWindowOpacity(float opacity) {
        // not supported
    }

    /**
     * rendering special window shapes, e.g. a cloud
     * window transparency is incompatible with transparent framebuffers!
     * may not succeed, test with isFramebufferTransparent()
     */
    public void makeFramebufferTransparent() {
        // not supported
    }

    /**
     * transparency of the whole window including decoration (buttons, icon and title)
     */
    public float getWindowTransparency() {
        return 1f;
    }

    public void cleanUp() {
    }

    public static void main(String[] args) {
        new GFXBase0x().run();
    }

}