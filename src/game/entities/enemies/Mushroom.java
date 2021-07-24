package game.entities.enemies;

import engine.GameContainer;
import engine.Renderer;
import engine.gfx.ImageTile;
import game.GameManager;
import game.GameObject;
import game.components.AABBComponent;
import game.entities.player.Player;
import game.objects.Button;

public class Mushroom extends GameObject {

	ImageTile mushroomwalkright = new ImageTile("/enemies/mushroom/mushroom-walk-right.png", 16, 16);
	ImageTile mushroomwalkleft = new ImageTile("/enemies/mushroom/mushroom-walk-left.png", 16, 16);

	private float runninganim = 0;
	private boolean oldgrounded = false;
	private boolean grounded = false;
	private static int tileX;
	private static int tileY;
	private float offX = 0, offY = 0;
	
	public boolean colliding = false;

	private float speed = 25;
	
	private int cooldown = 0;
	private int lives = 0;


	private float anim;
	private float walkanim;
	private int direction = 0;

	protected int playerhit = 0;
	protected int playerdmg = 0;
	private int x = 1;

	boolean facingleft = false;

	boolean movingright = true;
	boolean movingleft = false;
	double currentFrame = 0.0;

	public Mushroom(int posX, int posY) {

		this.tag = "Mushroom";
		Mushroom.tileX = posX;
		Mushroom.tileY = posY;
		this.offX = 0;
		this.offY = 0;
		this.positionX = posX + GameManager.ts;
		this.positionY = posY + GameManager.ts;
		this.width = GameManager.ts -4;
		this.height = GameManager.ts -4;
		padding = 4;
		paddingTop = 4;

		this.addComponent(new AABBComponent(this));

	}

	
	@Override
	public void update(GameContainer gc, GameManager gm, double dt) {
		// Left and Right Movement
		if (movingright) {
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

		if (movingleft) {
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

				
				 // Final position
		        if (offY > GameManager.ts / 2)
		        {
		            tileY++;
		            offY -= GameManager.ts;
		        }
		 
		        if (offY < -GameManager.ts / 2)
		        {
		            tileY--;
		            offY += GameManager.ts;
		        }
		 
		        if (offX > GameManager.ts / 2)
		        {
		            tileX++;
		            offX -= GameManager.ts;
		        }
		 
		        if (offX < -GameManager.ts / 2)
		        {
		            tileX--;
		            offX += GameManager.ts;
		        }
		 
		        positionX = tileX * GameManager.ts + offX;
		        positionY = tileY * GameManager.ts + offY;
		        // Final position end
			
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

				if (movingright) {
					direction = 0;

					runninganim += dt * 7;
					if (runninganim > 6)
						runninganim = 0;
				} else if (movingleft) {
					direction = 1;

					
					
				runninganim += dt * 7;
				if (runninganim > 6)
					runninganim = 0;
				} else {
					runninganim = 0;
				}

		// Animation
		anim += dt * 5;
		if (anim > 15)
			anim = 0;

		walkanim += dt * 5;
		if (walkanim > 15)
			walkanim = 0;

		if (playerhit != 0 && playerhit > 1 && x == 0) {
			playerdmg = 1;

			if (playerdmg == 1) {
				Player.lives--;
			}
		}
		
		if (true) {
			x = 120;
		}
		if (x > 0) {
			x--;
		}
		if (x == 0) {
		}
		
		if (lives != 0 && lives > 1 && cooldown == 0) {
			lives = 1;
			cooldown = 60;
			if (lives == 1) {
				Player.lives--; 
			}

		}
		if (cooldown != 0 && cooldown <= 60)
		{
			cooldown--;
			lives = 0;
		}
		
		this.updateComponents(gc, gm, (float) dt);
	}

	@Override
	public void render(GameContainer gc, Renderer r) {
		if (direction == 0) {
			r.drawImageTile(mushroomwalkright, (int) positionX, (int) positionY, (int) walkanim, 0);
		} else if (direction == 1) {
			r.drawImageTile(mushroomwalkleft, (int) positionX, (int) positionY, (int) anim, 0);
		}
		this.renderComponents(gc, r);

	}

	@Override
	public void collision(GameObject other) {
		if(other.getTag().equalsIgnoreCase("StoneDoor") && !Button.buttonDown)
		{
			
			AABBComponent myC = (AABBComponent) this.findComponent("aabb");
			AABBComponent otherC = (AABBComponent) other.findComponent("aabb");
            
                if (myC.getCenterX() < otherC.getCenterX())
                {
                    int distance = (myC.getHalfWidth() + otherC.getHalfWidth()) - (otherC.getCenterX() - myC.getCenterX());
                    offX -= distance;
                    positionX -= distance;
                    myC.setCenterX(myC.getCenterX() - distance);
                    movingright = false;
                    movingleft = true;
                    direction = 1;
                }
 
                
                if (myC.getCenterX() > otherC.getCenterX())
                {
                    int distance = (myC.getHalfWidth() + otherC.getHalfWidth()) - (myC.getCenterX() - otherC.getCenterX());
                    offX += distance;
                    positionX += distance;
                    myC.setCenterX(myC.getCenterX() + distance);
                    movingright = true;
                    movingleft = false;
                    direction = 0;
                }
                
            }
		
		if(other.getTag().equalsIgnoreCase("InvisBlock"))
		{
			
			AABBComponent myC = (AABBComponent) this.findComponent("aabb");
			AABBComponent otherC = (AABBComponent) other.findComponent("aabb");
			
			
			if (myC.getCenterX() < otherC.getCenterX())
            {
                int distance = (myC.getHalfWidth() + otherC.getHalfWidth()) - (otherC.getCenterX() - myC.getCenterX());
                offX -= distance;
                positionX -= distance;
                myC.setCenterX(myC.getCenterX() - distance);
                movingright = false;
                movingleft = true;
                direction = 1;
            }

            
            if (myC.getCenterX() > otherC.getCenterX())
            {
                int distance = (myC.getHalfWidth() + otherC.getHalfWidth()) - (myC.getCenterX() - otherC.getCenterX());
                offX += distance;
                positionX += distance;
                myC.setCenterX(myC.getCenterX() + distance);
                movingright = true;
                movingleft = false;
                direction = 0;
            }
		}
		if(other.getTag().equalsIgnoreCase("player") && !Player.attack)
		{
			lives++;
		}
		
	}

}
