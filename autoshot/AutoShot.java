import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import javax.imageio.ImageIO;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import engine.AbstractGame;
import engine.GameContainer;
import engine.Input;
import game.GameManager;
import game.entities.player.Player;
import engine.Window;

/**
 * Automated screenshot + gameplay recorder for Ninjas-World.
 *
 * Runs the real game window, injects synthetic key/mouse events into the
 * engine's Input listener, captures pixel-perfect frames straight from the
 * engine framebuffer (after each completed render pass), and walks a script:
 *
 *   main menu -> controls -> credits -> play -> level 1 bot run (GIF frames)
 *   -> level 2 stills -> exit.
 *
 * Output:
 *   shots/*.png        native-res stills (upscale with ffmpeg -vf scale=...:flags=neighbor)
 *   shots/gif/*.png    level-1 frames at native res, 15 fps (tick % 4)
 *
 * Run:  java -cp "out;res" AutoShot
 */
public class AutoShot {

    static final Path SHOTS = Paths.get("shots");
    static final Path GIFDIR = SHOTS.resolve("gif");

    static GameManager gm;
    static Director director;
    static boolean level2Mode = false;

    // input requests, consumed by ShotGC on the game thread
    static final Set<Integer> heldKeys = new HashSet<>();
    static final Set<Integer> pendingTaps = new HashSet<>(); // queued by director, injected next tick
    static boolean mousePress = false, mouseRelease = false;

    // capture requests
    static boolean captureShot = false;
    static String shotName = null;
    static boolean recordGif = false;
    static int gifIndex = 0;
    static boolean done = false;

    /** Director: scripts the run, wrapped around the real GameManager. */
    static class Director extends AbstractGame {
        int tick = 0;

        // bot state
        boolean jumpArmed = true;
        boolean wasGrounded = true;
        int tyAtJump = -1;
        float lastPX = -1;
        int stuckTicks = 0;
        int retries = 0;

        // phase state
        int level1Start = -1, level2Start = -1;
        boolean shotAttack = false, shotLevel1 = false;
        boolean shotLevel2 = false, shotLevel2Door = false;

        @Override
        public void init(GameContainer gc) {
            gm.init(gc);
        }

        void tap(int kc) {
            // queued here (inside game.update); ShotGC injects it at the next tick start
            pendingTaps.add(kc);
        }

        void shot(String name) {
            captureShot = true;
            shotName = name;
        }

        @Override
        public void update(GameContainer gc, double dt) {
            tick++;
            int t = tick;

            if (level2Mode) {
                // no gameplay: load level 2 directly, shoot spawn + door views
                if (t == 30) {
                    gm.level = 2;
                    gm.loadLevel(2);
                    gm.reloadentities();
                }
                if (t == 170) shot("level2");
                if (t == 175) teleportNearDoor();
                if (t == 290) shot("level2-door");
                if (t == 300) done = true;
                gm.update(gc, dt);
                return;
            }

            // ---- scripted menu walkthrough ----
            switch (t) {
                case 90:  shot("menu"); break;
                case 100: tap(KeyEvent.VK_S); break;       // selector -> Controls
                case 135: tap(KeyEvent.VK_ENTER); break;   // open controls
                case 185: shot("controls"); break;
                case 195: tap(KeyEvent.VK_ESCAPE); break;
                case 225: tap(KeyEvent.VK_S); break;       // selector -> Credits
                case 255: tap(KeyEvent.VK_ENTER); break;   // open credits
                case 300: shot("credits"); break;
                case 310: tap(KeyEvent.VK_ESCAPE); break;
                case 340: tap(KeyEvent.VK_S); break;       // selector -> Play
                case 370: tap(KeyEvent.VK_ENTER); break;   // play -> level 1
            }

            int level = GameManager.level;

            // ---- level 1: bot run + GIF ----
            if (level == 1) {
                if (level1Start < 0) level1Start = t;
                int lt = t - level1Start;

                if (lt == 3) recordGif = true;

                // short recon walk left, then turn and attack for the action shot
                if (lt < 45) heldKeys.add(KeyEvent.VK_A);
                else heldKeys.remove(KeyEvent.VK_A);

                if (lt == 55) mousePress = true;
                if (lt == 64 && !shotAttack) { shot("level1-attack"); shotAttack = true; }
                if (lt == 75) mouseRelease = true;

                // then run right with jump logic until the door ends the level
                if (lt >= 85) {
                    heldKeys.add(KeyEvent.VK_D);
                    botJump(gc);
                }
            } else if (level == 0 && level1Start > 0 && level2Start < 0) {
                // died and cycled back to the menu: retry (Play is still selected)
                if (retries < 2) {
                    retries++;
                    level1Start = -1;
                    shotAttack = false;
                    recordGif = false;
                    gifIndex = 0;
                    heldKeys.clear();
                    stuckTicks = 0;
                    lastPX = -1;
                    tap(KeyEvent.VK_ENTER);
                    System.out.println("retry " + retries);
                } else {
                    done = true;
                }
            }

            // ---- level 2: stills only (bot not needed; avoid the door -> l3.png crash) ----
            if (level == 2) {
                if (level2Start < 0) {
                    level2Start = t;
                    heldKeys.clear();      // stop walking
                    recordGif = false;     // GIF covers level 1 only
                    pendingTaps.clear();
                }
                int lt = t - level2Start;

                if (lt == 50 && !shotLevel2) { shot("level2"); shotLevel2 = true; }
                if (lt == 55) teleportNearDoor();
                // camera needs a moment to catch up after the teleport
                if (lt == 120 && !shotLevel2Door) { shot("level2-door"); shotLevel2Door = true; }
                if (lt == 130) done = true;
            }

            if (t > 3600) done = true; // safety net, 60 s

            // the actual game simulation runs after the director's decisions
            gm.update(gc, dt);
        }

        /**
         * Hold D jump logic: hop walls and gaps (waterfalls drain lives on contact,
         * so any missing floor tile just ahead triggers a jump), double-jump in
         * mid-fall as backup, and force an escape jump if position stops changing.
         */
        void botJump(GameContainer gc) {
            int tx = Player.getTileX(), ty = Player.getTileY();
            boolean groundBelow = gm.getCollision(tx, ty + 1) || gm.getCollision(tx, ty + 2);
            boolean wallAhead = gm.getCollision(tx + 1, ty) || gm.getCollision(tx + 1, ty - 1);
            boolean gapAhead = !gm.getCollision(tx + 1, ty + 1);

            if (groundBelow) {
                wasGrounded = true;
                jumpArmed = true;
                if (wallAhead || gapAhead) {
                    tap(KeyEvent.VK_SPACE);
                    tyAtJump = ty;
                }
            } else {
                // airborne: double jump only once we are actually falling past the takeoff tile
                boolean falling = tyAtJump >= 0 && ty > tyAtJump;
                if ((wallAhead || gapAhead) && jumpArmed && falling) {
                    tap(KeyEvent.VK_SPACE);
                    jumpArmed = false;
                }
                if (wasGrounded && !wallAhead && !gapAhead) {
                    wasGrounded = false; // walked off a ledge
                    tyAtJump = ty;
                }
            }

            // unstuck: if D is held but x has barely moved, jump (then double-jump)
            float px = gm.getObject("player").getPositionX();
            if (Math.abs(px - lastPX) < 0.5f) {
                stuckTicks++;
            } else {
                stuckTicks = 0;
                lastPX = px;
            }
            if (stuckTicks == 50) {
                tap(KeyEvent.VK_SPACE);
            } else if (stuckTicks >= 75) {
                tap(KeyEvent.VK_SPACE);
                stuckTicks = 0;
                lastPX = -1;
            }
        }

        /** Place the player next to the level-2 door without walking into it. */
        void teleportNearDoor() throws RuntimeException {
            try {
                Object player = gm.getObject("player");
                ((Player) player).setTileX(25);
                ((Player) player).setTileY(14);
                for (String f : new String[] { "offX", "offY", "fallDistance" }) {
                    Field field = Player.class.getDeclaredField(f);
                    field.setAccessible(true);
                    field.setFloat(player, 0f);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void render(GameContainer gc, engine.Renderer r) {
            gm.render(gc, r);
        }
    }

    /** GameContainer that injects input before each tick and captures after each render. */
    static class ShotGC extends GameContainer {
        final Set<Integer> pressedNow = new HashSet<>();
        final Set<Integer> tappedNow = new HashSet<>();

        ShotGC(AbstractGame g) {
            super(g);
        }

        void press(int kc) {
            KeyEvent e = new KeyEvent(getWindow().getCanvas(), KeyEvent.KEY_PRESSED,
                    System.currentTimeMillis(), 0, kc, KeyEvent.CHAR_UNDEFINED);
            for (java.awt.event.KeyListener l : getWindow().getCanvas().getKeyListeners()) {
                l.keyPressed(e);
            }
        }

        void release(int kc) {
            KeyEvent e = new KeyEvent(getWindow().getCanvas(), KeyEvent.KEY_RELEASED,
                    System.currentTimeMillis(), 0, kc, KeyEvent.CHAR_UNDEFINED);
            for (java.awt.event.KeyListener l : getWindow().getCanvas().getKeyListeners()) {
                l.keyReleased(e);
            }
        }

        void mouse(boolean down) {
            MouseEvent e = new MouseEvent(getWindow().getCanvas(),
                    down ? MouseEvent.MOUSE_PRESSED : MouseEvent.MOUSE_RELEASED,
                    System.currentTimeMillis(), 0, 10, 10, 1, false, MouseEvent.BUTTON1);
            for (java.awt.event.MouseListener l : getWindow().getCanvas().getMouseListeners()) {
                if (down) {
                    l.mousePressed(e);
                } else {
                    l.mouseReleased(e);
                }
            }
        }

        @Override
        public void update() {
            // presses land before game.update reads them this tick
            for (int k : heldKeys) {
                if (pressedNow.add(k)) press(k);
            }
            for (int k : pendingTaps) {
                press(k);
                tappedNow.add(k);
            }
            pendingTaps.clear();
            if (mousePress) {
                mouse(true);
                mousePress = false;
            }
            super.update();
            // releases after the tick so the game sees clean down/up edges
            for (int k : tappedNow) {
                release(k);
            }
            tappedNow.clear();
            for (int k : new HashSet<>(pressedNow)) {
                if (!heldKeys.contains(k)) {
                    release(k);
                    pressedNow.remove(k);
                }
            }
            if (mouseRelease) {
                mouse(false);
                mouseRelease = false;
            }
        }

        @Override
        public void render() {
            super.render(); // full pass: render + lighting process + blit
            if (captureShot && shotName != null) {
                save(getWindow().getImage(), SHOTS.resolve(shotName + ".png"));
                captureShot = false;
                shotName = null;
            }
            if (recordGif && GameManager.level == 1 && gifIndex < 900
                    && director.tick % 4 == 0) { // 15 fps, level-1 frames only
                save(getWindow().getImage(), GIFDIR.resolve(String.format("%04d.png", gifIndex++)));
            }
            if (done) {
                System.out.println("AutoShot done: " + gifIndex + " gif frames");
                System.exit(0);
            }
        }

        void save(BufferedImage src, Path path) {
            try {
                BufferedImage out = new BufferedImage(src.getWidth(), src.getHeight(),
                        BufferedImage.TYPE_INT_RGB);
                out.getGraphics().drawImage(src, 0, 0, null);
                ImageIO.write(out, "png", path.toFile());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Files.createDirectories(GIFDIR);
        level2Mode = args.length > 0 && args[0].equals("level2");
        gm = new GameManager();
        director = new Director();
        ShotGC gc = new ShotGC(director);
        gc.setWidth(384);
        gc.setHeight(216);
        double factor = 2.5;
        gc.setScaleX(factor);
        gc.setScaleY(factor);
        gc.setTitle("Far Meadow - AutoShot");
        gc.start();
    }
}
