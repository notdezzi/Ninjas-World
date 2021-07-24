package game.entities.player;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import engine.GameContainer;
import engine.Renderer;
import engine.gfx.ImageTile;
import game.GameManager;
import game.GameObject;
import game.components.AABBComponent;
import game.objects.Button;
import game.objects.Stone;

public class Player extends GameObject {

    public static int lives = 3;
    public static int coins = 0;

    ImageTile player = new ImageTile("/player/sprites/player-idle.png", GameManager.ts, GameManager.ts);
    ImageTile playerrunning = new ImageTile("/player/sprites/player-running.png", GameManager.ts, GameManager.ts);
    ImageTile playerjumpup = new ImageTile("/player/sprites/player-jump-up.png", GameManager.ts, GameManager.ts);
    ImageTile playerjumpdown = new ImageTile("/player/sprites/player-jump-down.png", GameManager.ts, GameManager.ts);
    ImageTile playerattack = new ImageTile("/player/sprites/player-attack.png", 32, GameManager.ts);
    ImageTile playerpushing = new ImageTile("/player/sprites/player-pushing.png", GameManager.ts, GameManager.ts);

    // anims
    private float anim = 0;
    private float runninganim = 0;
    private boolean oldgrounded = false;
    private boolean grounded = false;
    private boolean doublejumpused = false;
    static int tileX;
    static int tileY;
    private float offX = 0,
            offY = 0;

    private boolean pushing = false;
    @SuppressWarnings("unused")
    private int push = 0;

    public boolean colliding = false;

    private float speed = 100;

    private float jump = (float) -4.5;
    private float gravity = 10;

    private boolean falling = false;
    boolean facingleft = false;

    boolean movingright = false;
    boolean movingleft = false;
    double currentFrame = 0.0;

    private float fallDistance = 0;

    public Player(int posX, int posY) {

        this.tag = "player";
        Player.tileX = posX;
        Player.tileY = posY;
        this.offX = 0;
        this.offY = 0;
        this.positionX = posX + GameManager.ts;
        this.positionY = posY + GameManager.ts;
        this.width = GameManager.ts - 6;
        this.height = GameManager.ts - 6;
        padding = 4;
        paddingTop = 4;

        this.addComponent(new AABBComponent(this));
    }

    private int direction = 0;
    public static boolean attack = false;
    private float attackanim;
    public static int attackcooldown = 0;

    public static int getTileX() {
        return tileX;
    }

    public void setTileX(int tileX) {
        Player.tileX = tileX;
    }

    public static int getTileY() {
        return tileY;
    }

    public void setTileY(int tileY) {
        Player.tileY = tileY;
    }

    @Override
    public void update(GameContainer gc, GameManager gm, double dt) {
    	
    	if(lives == 0)
    	{
    		GameManager.level = 3;
    	}

        // Attack

        if (gc.getInput().isButtonDown(MouseEvent.BUTTON1) && attackcooldown == 0) {
            Sword.attack = true;
            attack = true;
            attackcooldown = 180;
        }
        if (attackcooldown > 0) {
            attackcooldown--;
        }
        if (attack) {
            attackanim += dt * 15;
            if (attackanim > 4) attackanim = 0;
        }
        if (!attack) {
            attackanim = 0;
        }

        if (gc.getInput().isButtonUp(MouseEvent.BUTTON1) || attackcooldown == 170) {
            attack = false;
        }

        // idle Anim

        anim += dt * 5;
        if (anim > 4) anim = 0;

        // Left and Right Movement
        if (gc.getInput().isKey(KeyEvent.VK_D) && movingright) {
            if (gm.getCollision(tileX + 1, tileY) || gm.getCollision(tileX + 1, tileY + (int) Math.signum((int) offY))) {
                offX += dt * speed;

                if (offX > padding) {
                    offX = padding;
                } else {
                    offX += dt * speed;
                }
            } else {
                offX += dt * speed;
            }
        }

        if (gc.getInput().isKey(KeyEvent.VK_A) && movingleft) {
            if (gm.getCollision(tileX - 1, tileY) || gm.getCollision(tileX - 1, tileY + (int) Math.signum((int) offY))) {
                offX -= dt * speed;

                if (offX < -padding) {
                    offX = -padding;
                } else {
                    offX -= dt * speed;
                }
            } else {
                offX -= dt * speed;
            }
        }
        // Left and Right Movement End

        // Jump
//        fallDistance += dt * gravity;
//
//        falling = fallDistance > 0;
//
//        offY += fallDistance;
//
//        if (fallDistance != 0) {
//            grounded = false;
//        }
//        if (fallDistance < 0) {
//            if (gm.getCollision(tileX, tileY - 1) || gm.getCollision(tileX + (int) Math.signum((int) Math.abs(offX) > padding ? offX : 0), tileY - 1) && offY < 0) {
//                fallDistance = 0;
//                offY = 0;
//            }
//        }
//
//        if (fallDistance > 0) {
//            if (gm.getCollision(tileX, tileY + 1) || gm.getCollision(tileX + (int) Math.signum((int) Math.abs(offX) > padding ? offX : 0), tileY + 1) && offY > 0 && falling) {
//                doublejumpused = false;
//                falling = false;
//                fallDistance = 0;
//                grounded = true;
//                offY = 0;
//            }
//        }
//
//        if (gc.getInput().isKeyDown(KeyEvent.VK_SPACE) && grounded) {
//            fallDistance = jump;
//            grounded = false;
//        }
//
//        if (gc.getInput().isKeyDown(KeyEvent.VK_SPACE) && falling && !doublejumpused) {
//            doublejumpused = true;
//            fallDistance = jump;
//            grounded = false;
//        }

        fallDistance += dt * gravity;
        falling = fallDistance > 0;
        
        if (fallDistance < 0)
        {
        	if (gm.getCollision(tileX, tileY - 1) || gm.getCollision(tileX + (int) Math.signum((int) Math.abs(offX) > padding ? offX : 0), tileY - 1) && offY < 0) {

                fallDistance = 0;
                offY = -paddingTop;
            }
        }
 
        if (fallDistance > 0)
        {
        	if (gm.getCollision(tileX, tileY + 1) || gm.getCollision(tileX + (int) Math.signum((int) Math.abs(offX) > padding ? offX : 0), tileY + 1) && offY > 0 && falling) {
            	doublejumpused = false;
              falling = false;
              fallDistance = 0;
              grounded = true;
              offY = 0;
            }
        }
 
        if (gc.getInput().isKeyDown(KeyEvent.VK_SPACE) && grounded)
        {
            fallDistance = jump;
            grounded = false;
        }
        if (gc.getInput().isKeyDown(KeyEvent.VK_SPACE) && falling && !doublejumpused)
        {
            doublejumpused = true;
            fallDistance = jump;
            grounded = false;
        }
 
        offY += fallDistance;
        
        
        // Jump End
        // Final position
        if (offY > GameManager.ts / 2) {
            tileY++;
            offY -= GameManager.ts;
        }

        if (offY < -GameManager.ts / 2) {
            tileY--;
            offY += GameManager.ts;
        }

        if (offX > GameManager.ts / 2) {
            tileX++;
            offX -= GameManager.ts;
        }

        if (offX < -GameManager.ts / 2) {
            tileX--;
            offX += GameManager.ts;
        }

        positionX = tileX * GameManager.ts + offX;
        positionY = tileY * GameManager.ts + offY;
        // Final position end
        if (gc.getInput().isKeyDown(KeyEvent.VK_D)) {
            movingright = true;
            movingleft = false;
        }

        if (gc.getInput().isKeyDown(KeyEvent.VK_A)) {
            movingleft = true;
            movingright = false;
        }

        if (gc.getInput().isKeyUp(KeyEvent.VK_D)) {
            movingright = false;
            currentFrame = 0;
            facingleft = false;
        }

        if (gc.getInput().isKeyUp(KeyEvent.VK_A)) {
            movingleft = false;
            currentFrame = 0;
            facingleft = true;
        }

        if (movingright || movingleft) {
            currentFrame += dt * speed / 4.5;
        }

        if (currentFrame >= 10) {
            currentFrame = 0;
        }

        if (!oldgrounded && grounded) {
            // land.play();
        }

        oldgrounded = grounded;

        if (gc.getInput().isKey(KeyEvent.VK_D)) {
            direction = 0;

            runninganim += dt * 7;
            if (runninganim > 6) runninganim = 0;
        } else if (gc.getInput().isKey(KeyEvent.VK_A)) {
            direction = 1;

            runninganim += dt * 7;
            if (runninganim > 6) runninganim = 0;
        } else {
            runninganim = 0;
        }

        colliding = false;
        this.updateComponents(gc, gm, (float) dt);

    }

    @Override
    public void render(GameContainer gc, Renderer r) {

        // Idle
        if (!movingleft && !movingright && grounded && !pushing && !attack) {
            r.drawImageTile(player, (int) positionX, (int) positionY, (int) anim, direction);
        }
        // Running
        if (movingleft && !movingright && grounded && !pushing && !attack) {
            r.drawImageTile(playerrunning, (int) positionX, (int) positionY, (int) runninganim, direction);
        }
        if (!movingleft && movingright && grounded && !pushing && !attack) {
            r.drawImageTile(playerrunning, (int) positionX, (int) positionY, (int) runninganim, direction);
        }

        // Attack
        if (attack && !pushing) {
            if (facingleft) {
                r.drawImageTile(playerattack, (int) positionX - 17, (int) positionY, (int) attackanim, direction);
            } else r.drawImageTile(playerattack, (int) positionX, (int) positionY, (int) attackanim, direction);
        }

        // Jump
        if (!grounded && !falling && !attack) {
            r.drawImageTile(playerjumpup, (int) positionX, (int) positionY, 0, direction);
        }
        if (!grounded && falling && !attack) {
            r.drawImageTile(playerjumpdown, (int) positionX, (int) positionY, 0, direction);
        }
        
        //		//Pushing
        //
        //		if (movingleft == true || movingright == true && grounded == true && attack == false && pushing == true) {
        //			r.drawImageTile(playerpushing, (int) positionX, (int) positionY, (int) pushanim, direction);
        //		}

        this.renderComponents(gc, r);
    }


    @Override
    public void collision(GameObject other) {

        if (other.getTag().equalsIgnoreCase("Stone")) {

            AABBComponent myC = (AABBComponent) this.findComponent("aabb");
            AABBComponent otherC = (AABBComponent) other.findComponent("aabb");

            if (myC.getCenterX() < otherC.getCenterX()) {
                int distance = (myC.getHalfWidth() + otherC.getHalfWidth()) - (otherC.getCenterX() - myC.getCenterX());
                offX -= distance;
                positionX -= distance;
                myC.setCenterX(myC.getCenterX() - distance);
                Stone.right++;
            }

            if (myC.getCenterX() > otherC.getCenterX()) {
                int distance = (myC.getHalfWidth() + otherC.getHalfWidth()) - (myC.getCenterX() - otherC.getCenterX());
                offX += distance;
                positionX += distance;
                myC.setCenterX(myC.getCenterX() + distance);
                Stone.left++;
            }
        }
        

        if (other.getTag().equalsIgnoreCase("StoneDoor") && !Button.buttonDown) {

            AABBComponent myC = (AABBComponent) this.findComponent("aabb");
            AABBComponent otherC = (AABBComponent) other.findComponent("aabb");

            if (myC.getCenterX() < otherC.getCenterX()) {
                int distance = (myC.getHalfWidth() + otherC.getHalfWidth()) - (otherC.getCenterX() - myC.getCenterX());
                offX -= distance;
                positionX -= distance;
                myC.setCenterX(myC.getCenterX() - distance);
            }

            if (myC.getCenterX() > otherC.getCenterX()) {
                int distance = (myC.getHalfWidth() + otherC.getHalfWidth()) - (myC.getCenterX() - otherC.getCenterX());
                offX += distance;
                positionX += distance;
                myC.setCenterX(myC.getCenterX() + distance);
            }

        }

        if (other.getTag().equalsIgnoreCase("Button")) {

            AABBComponent myC = (AABBComponent) this.findComponent("aabb");
            AABBComponent otherC = (AABBComponent) other.findComponent("aabb");

            if (Math.abs(myC.getCenterX() - otherC.getCenterX()) < myC.getHalfWidth() + otherC.getHalfWidth()) {
                if (myC.getCenterY() < otherC.getCenterY()) {
                    int distance = (myC.getHalfHeight() + otherC.getHalfHeight()) - (otherC.getCenterY() - myC.getCenterY());
                    offY -= distance;
                    positionY -= distance;
                    myC.setCenterY(myC.getCenterY() - distance);
                    fallDistance = 0;
                    grounded = true;
                }
            }
        }
        
        if (other.getTag().equalsIgnoreCase("Waterfall")) {

            AABBComponent myC = (AABBComponent) this.findComponent("aabb");
            AABBComponent otherC = (AABBComponent) other.findComponent("aabb");

            if (Math.abs(myC.getCenterX() - otherC.getCenterX()) < myC.getHalfWidth() + otherC.getHalfWidth()) {
                if (myC.getCenterY() < otherC.getCenterY()) {
                    int distance = (myC.getHalfHeight() + otherC.getHalfHeight()) - (otherC.getCenterY() - myC.getCenterY());
                    offY -= distance;
                    positionY -= distance;
                    myC.setCenterY(myC.getCenterY() - distance);
                    fallDistance = 0;
                    grounded = true;
                }
            }
        }

    }
}