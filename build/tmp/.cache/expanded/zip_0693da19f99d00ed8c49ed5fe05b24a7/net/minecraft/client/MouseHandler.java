package net.minecraft.client;

import com.mojang.blaze3d.Blaze3D;
import com.mojang.blaze3d.platform.InputConstants;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.util.Mth;
import net.minecraft.util.SmoothDouble;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFWDropCallback;

@OnlyIn(Dist.CLIENT)
public class MouseHandler {
    private final Minecraft minecraft;
    private boolean isLeftPressed;
    private boolean isMiddlePressed;
    private boolean isRightPressed;
    private double xpos;
    private double ypos;
    private int fakeRightMouse;
    private int activeButton = -1;
    private boolean ignoreFirstMove = true;
    private int clickDepth;
    private double mousePressedTime;
    private final SmoothDouble smoothTurnX = new SmoothDouble();
    private final SmoothDouble smoothTurnY = new SmoothDouble();
    private double accumulatedDX;
    private double accumulatedDY;
    private double accumulatedScrollX;
    private double accumulatedScrollY;
    private double lastMouseEventTime = Double.MIN_VALUE;
    private boolean mouseGrabbed;

    public MouseHandler(Minecraft p_91522_) {
        this.minecraft = p_91522_;
    }

    private void onPress(long p_91531_, int p_91532_, int p_91533_, int p_91534_) {
        if (p_91531_ == this.minecraft.getWindow().getWindow()) {
            if (this.minecraft.screen != null) {
                this.minecraft.setLastInputType(InputType.MOUSE);
            }

            boolean flag = p_91533_ == 1;
            if (Minecraft.ON_OSX && p_91532_ == 0) {
                if (flag) {
                    if ((p_91534_ & 2) == 2) {
                        p_91532_ = 1;
                        ++this.fakeRightMouse;
                    }
                } else if (this.fakeRightMouse > 0) {
                    p_91532_ = 1;
                    --this.fakeRightMouse;
                }
            }

            int i = p_91532_;
            if (flag) {
                if (this.minecraft.options.touchscreen().get() && this.clickDepth++ > 0) {
                    return;
                }

                this.activeButton = i;
                this.mousePressedTime = Blaze3D.getTime();
            } else if (this.activeButton != -1) {
                if (this.minecraft.options.touchscreen().get() && --this.clickDepth > 0) {
                    return;
                }

                this.activeButton = -1;
            }

            if (net.neoforged.neoforge.client.ClientHooks.onMouseButtonPre(p_91532_, p_91533_, p_91534_)) return;
            boolean[] aboolean = new boolean[]{false};
            if (this.minecraft.getOverlay() == null) {
                if (this.minecraft.screen == null) {
                    if (!this.mouseGrabbed && flag) {
                        this.grabMouse();
                    }
                } else {
                    double d0 = this.xpos * (double)this.minecraft.getWindow().getGuiScaledWidth() / (double)this.minecraft.getWindow().getScreenWidth();
                    double d1 = this.ypos * (double)this.minecraft.getWindow().getGuiScaledHeight() / (double)this.minecraft.getWindow().getScreenHeight();
                    Screen screen = this.minecraft.screen;
                    if (flag) {
                        screen.afterMouseAction();
                        Screen.wrapScreenError(() -> {
                            aboolean[0] = net.neoforged.neoforge.client.ClientHooks.onScreenMouseClickedPre(screen, d0, d1, i);
                            if (!aboolean[0]) {
                                aboolean[0] = screen.mouseClicked(d0, d1, i);
                                aboolean[0] = net.neoforged.neoforge.client.ClientHooks.onScreenMouseClickedPost(screen, d0, d1, i, aboolean[0]);
                            }
                        }, "mouseClicked event handler", screen.getClass().getCanonicalName());
                    } else {
                        Screen.wrapScreenError(() -> {
                            aboolean[0] = net.neoforged.neoforge.client.ClientHooks.onScreenMouseReleasedPre(screen, d0, d1, i);
                            if (!aboolean[0]) {
                                aboolean[0] = screen.mouseReleased(d0, d1, i);
                                aboolean[0] = net.neoforged.neoforge.client.ClientHooks.onScreenMouseReleasedPost(screen, d0, d1, i, aboolean[0]);
                            }
                        }, "mouseReleased event handler", screen.getClass().getCanonicalName());
                    }
                }
            }

            if (!aboolean[0] && this.minecraft.screen == null && this.minecraft.getOverlay() == null) {
                if (i == 0) {
                    this.isLeftPressed = flag;
                } else if (i == 2) {
                    this.isMiddlePressed = flag;
                } else if (i == 1) {
                    this.isRightPressed = flag;
                }

                KeyMapping.set(InputConstants.Type.MOUSE.getOrCreate(i), flag);
                if (flag) {
                    if (this.minecraft.player.isSpectator() && i == 2) {
                        this.minecraft.gui.getSpectatorGui().onMouseMiddleClick();
                    } else {
                        KeyMapping.click(InputConstants.Type.MOUSE.getOrCreate(i));
                    }
                }
            }
            net.neoforged.neoforge.client.ClientHooks.onMouseButtonPost(p_91532_, p_91533_, p_91534_);
        }
    }

    private void onScroll(long p_91527_, double p_91528_, double p_91529_) {
        if (p_91527_ == Minecraft.getInstance().getWindow().getWindow()) {
            boolean flag = this.minecraft.options.discreteMouseScroll().get();
            double d0 = this.minecraft.options.mouseWheelSensitivity().get();
            double d1 = (flag ? Math.signum(p_91528_) : p_91528_) * d0;
            double d2 = (flag ? Math.signum(p_91529_) : p_91529_) * d0;
            if (this.minecraft.getOverlay() == null) {
                if (this.minecraft.screen != null) {
                    double d3 = this.xpos * (double)this.minecraft.getWindow().getGuiScaledWidth() / (double)this.minecraft.getWindow().getScreenWidth();
                    double d4 = this.ypos * (double)this.minecraft.getWindow().getGuiScaledHeight() / (double)this.minecraft.getWindow().getScreenHeight();
                    if (!net.neoforged.neoforge.client.ClientHooks.onScreenMouseScrollPre(this, this.minecraft.screen, d1, d2)) {
                        if (!this.minecraft.screen.mouseScrolled(d3, d4, d1, d2)) {
                            net.neoforged.neoforge.client.ClientHooks.onScreenMouseScrollPost(this, this.minecraft.screen, d1, d2);
                        }
                    }
                    this.minecraft.screen.afterMouseAction();
                } else if (this.minecraft.player != null) {
                    if (this.accumulatedScrollX != 0.0 && Math.signum(d1) != Math.signum(this.accumulatedScrollX)) {
                        this.accumulatedScrollX = 0.0;
                    }

                    if (this.accumulatedScrollY != 0.0 && Math.signum(d2) != Math.signum(this.accumulatedScrollY)) {
                        this.accumulatedScrollY = 0.0;
                    }

                    this.accumulatedScrollX += d1;
                    this.accumulatedScrollY += d2;
                    int j = (int)this.accumulatedScrollX;
                    int i = (int)this.accumulatedScrollY;
                    if (j == 0 && i == 0) {
                        return;
                    }

                    this.accumulatedScrollX -= (double)j;
                    this.accumulatedScrollY -= (double)i;
                    int k = i == 0 ? -j : i;
                    if (net.neoforged.neoforge.client.ClientHooks.onMouseScroll(this, d1, d2)) return;
                    if (this.minecraft.player.isSpectator()) {
                        if (this.minecraft.gui.getSpectatorGui().isMenuActive()) {
                            this.minecraft.gui.getSpectatorGui().onMouseScrolled(-k);
                        } else {
                            float f = Mth.clamp(this.minecraft.player.getAbilities().getFlyingSpeed() + (float)i * 0.005F, 0.0F, 0.2F);
                            this.minecraft.player.getAbilities().setFlyingSpeed(f);
                        }
                    } else {
                        this.minecraft.player.getInventory().swapPaint((double)k);
                    }
                }
            }
        }
    }

    private void onDrop(long p_91540_, List<Path> p_91541_) {
        if (this.minecraft.screen != null) {
            this.minecraft.screen.onFilesDrop(p_91541_);
        }
    }

    public void setup(long p_91525_) {
        InputConstants.setupMouseCallbacks(
            p_91525_,
            (p_91591_, p_91592_, p_91593_) -> this.minecraft.execute(() -> this.onMove(p_91591_, p_91592_, p_91593_)),
            (p_91566_, p_91567_, p_91568_, p_91569_) -> this.minecraft.execute(() -> this.onPress(p_91566_, p_91567_, p_91568_, p_91569_)),
            (p_91576_, p_91577_, p_91578_) -> this.minecraft.execute(() -> this.onScroll(p_91576_, p_91577_, p_91578_)),
            (p_91536_, p_91537_, p_91538_) -> {
                Path[] apath = new Path[p_91537_];
    
                for(int i = 0; i < p_91537_; ++i) {
                    apath[i] = Paths.get(GLFWDropCallback.getName(p_91538_, i));
                }
    
                this.minecraft.execute(() -> this.onDrop(p_91536_, Arrays.asList(apath)));
            }
        );
    }

    private void onMove(long p_91562_, double p_91563_, double p_91564_) {
        if (p_91562_ == Minecraft.getInstance().getWindow().getWindow()) {
            if (this.ignoreFirstMove) {
                this.xpos = p_91563_;
                this.ypos = p_91564_;
                this.ignoreFirstMove = false;
            }

            Screen screen = this.minecraft.screen;
            if (screen != null && this.minecraft.getOverlay() == null) {
                double d0 = p_91563_ * (double)this.minecraft.getWindow().getGuiScaledWidth() / (double)this.minecraft.getWindow().getScreenWidth();
                double d1 = p_91564_ * (double)this.minecraft.getWindow().getGuiScaledHeight() / (double)this.minecraft.getWindow().getScreenHeight();
                Screen.wrapScreenError(() -> screen.mouseMoved(d0, d1), "mouseMoved event handler", screen.getClass().getCanonicalName());
                if (this.activeButton != -1 && this.mousePressedTime > 0.0) {
                    double d2 = (p_91563_ - this.xpos)
                        * (double)this.minecraft.getWindow().getGuiScaledWidth()
                        / (double)this.minecraft.getWindow().getScreenWidth();
                    double d3 = (p_91564_ - this.ypos)
                        * (double)this.minecraft.getWindow().getGuiScaledHeight()
                        / (double)this.minecraft.getWindow().getScreenHeight();
                    Screen.wrapScreenError(() -> {
                        if (net.neoforged.neoforge.client.ClientHooks.onScreenMouseDragPre(screen, d0, d1, this.activeButton, d2, d3)) return;
                        if (screen.mouseDragged(d0, d1, this.activeButton, d2, d3)) return;
                        net.neoforged.neoforge.client.ClientHooks.onScreenMouseDragPost(screen, d0, d1, this.activeButton, d2, d3);
                    }, "mouseDragged event handler", screen.getClass().getCanonicalName());
                }

                screen.afterMouseMove();
            }

            this.minecraft.getProfiler().push("mouse");
            if (this.isMouseGrabbed() && this.minecraft.isWindowActive()) {
                this.accumulatedDX += p_91563_ - this.xpos;
                this.accumulatedDY += p_91564_ - this.ypos;
            }

            this.turnPlayer();
            this.xpos = p_91563_;
            this.ypos = p_91564_;
            this.minecraft.getProfiler().pop();
        }
    }

    public void turnPlayer() {
        double d0 = Blaze3D.getTime();
        double d1 = d0 - this.lastMouseEventTime;
        this.lastMouseEventTime = d0;
        if (this.isMouseGrabbed() && this.minecraft.isWindowActive()) {
            var event = net.neoforged.neoforge.client.ClientHooks.getTurnPlayerValues(this.minecraft.options.sensitivity().get(), this.minecraft.options.smoothCamera);
            double d4 = event.getMouseSensitivity() * 0.6F + 0.2F;
            double d5 = d4 * d4 * d4;
            double d6 = d5 * 8.0;
            double d2;
            double d3;
            if (event.getCinematicCameraEnabled()) {
                double d7 = this.smoothTurnX.getNewDeltaValue(this.accumulatedDX * d6, d1 * d6);
                double d8 = this.smoothTurnY.getNewDeltaValue(this.accumulatedDY * d6, d1 * d6);
                d2 = d7;
                d3 = d8;
            } else if (this.minecraft.options.getCameraType().isFirstPerson() && this.minecraft.player.isScoping()) {
                this.smoothTurnX.reset();
                this.smoothTurnY.reset();
                d2 = this.accumulatedDX * d5;
                d3 = this.accumulatedDY * d5;
            } else {
                this.smoothTurnX.reset();
                this.smoothTurnY.reset();
                d2 = this.accumulatedDX * d6;
                d3 = this.accumulatedDY * d6;
            }

            this.accumulatedDX = 0.0;
            this.accumulatedDY = 0.0;
            int i = 1;
            if (this.minecraft.options.invertYMouse().get()) {
                i = -1;
            }

            this.minecraft.getTutorial().onMouse(d2, d3);
            if (this.minecraft.player != null) {
                this.minecraft.player.turn(d2, d3 * (double)i);
            }
        } else {
            this.accumulatedDX = 0.0;
            this.accumulatedDY = 0.0;
        }
    }

    public boolean isLeftPressed() {
        return this.isLeftPressed;
    }

    public boolean isMiddlePressed() {
        return this.isMiddlePressed;
    }

    public boolean isRightPressed() {
        return this.isRightPressed;
    }

    public double xpos() {
        return this.xpos;
    }

    public double ypos() {
        return this.ypos;
    }

    public double getXVelocity() {
        return this.accumulatedDX;
    }

    public double getYVelocity() {
        return this.accumulatedDY;
    }

    public void setIgnoreFirstMove() {
        this.ignoreFirstMove = true;
    }

    public boolean isMouseGrabbed() {
        return this.mouseGrabbed;
    }

    public void grabMouse() {
        if (this.minecraft.isWindowActive()) {
            if (!this.mouseGrabbed) {
                if (!Minecraft.ON_OSX) {
                    KeyMapping.setAll();
                }

                this.mouseGrabbed = true;
                this.xpos = (double)(this.minecraft.getWindow().getScreenWidth() / 2);
                this.ypos = (double)(this.minecraft.getWindow().getScreenHeight() / 2);
                InputConstants.grabOrReleaseMouse(this.minecraft.getWindow().getWindow(), 212995, this.xpos, this.ypos);
                this.minecraft.setScreen(null);
                this.minecraft.missTime = 10000;
                this.ignoreFirstMove = true;
            }
        }
    }

    public void releaseMouse() {
        if (this.mouseGrabbed) {
            this.mouseGrabbed = false;
            this.xpos = (double)(this.minecraft.getWindow().getScreenWidth() / 2);
            this.ypos = (double)(this.minecraft.getWindow().getScreenHeight() / 2);
            InputConstants.grabOrReleaseMouse(this.minecraft.getWindow().getWindow(), 212993, this.xpos, this.ypos);
        }
    }

    public void cursorEntered() {
        this.ignoreFirstMove = true;
    }
}
