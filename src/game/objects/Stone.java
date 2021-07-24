package game.objects;

import engine.GameContainer;
import engine.Renderer;
import engine.gfx.Image;
import game.GameManager;
import game.GameObject;
import game.components.AABBComponent;

public class Stone extends GameObject {

	Image stone = new Image("/Textures/Stone.png");

	private boolean oldgrounded = false;
	private boolean grounded = false;
	public static int tileX;
	public static int tileY;
	private float offX = 0, offY = 0;
	
	public static boolean thisisatest = false;
	
	private float gravity = 15;

	private float speed = 15;
	
	private boolean falling = false;
	double currentFrame = 0.0;

	public static int left = 0;
	public static int right = 0;
	
	private float fallDistance = 0;

	@SuppressWarnings("static-access")
	public Stone(int posX, int posY) {

		this.tag = "stone";
		this.tileX = posX;
		this.tileY = posY;
		this.offX = 1;
		this.offY = 1;
		this.positionX = posX + GameManager.ts;
		this.positionY = posY + GameManager.ts;
		this.width = GameManager.ts - 6;
		this.height = GameManager.ts - 6;
		padding = 2;
		paddingTop = 3;

		this.addComponent(new AABBComponent(this));
	}
	
	@Override
	public void update(GameContainer gc, GameManager gm, double dt) {
		
		if(left == 1) {
			if (gm.getCollision(tileX - 1, tileY)
					|| gm.getCollision(tileX - 1, tileY + (int) Math.signum((int) offY))) {
				offX -= dt * speed;

				if (offX < -padding) {
					offX = -padding;
				} else {
					offX -= dt * speed;
				}
			} else {
				offX -= dt * speed;
			}
			left = 0;
		}
		if(right == 1) {
			if (gm.getCollision(tileX + 1, tileY)
					|| gm.getCollision(tileX + 1, tileY + (int) Math.signum((int) offY))) {
				offX += dt * speed;

				if (offX > padding) {
					offX = padding;
				} else {
					offX += dt * speed;
				}
			} else {
				offX += dt * speed;
			}
			right = 0;
		}

		// Jump
		fallDistance += dt * gravity;


		falling = fallDistance > 0;
		offY += fallDistance;

		if (fallDistance < 0) {
			if (gm.getCollision(tileX, tileY - 1) && offY < 0) {
				fallDistance = 0;
				offY = 0;
			}
		}

		if (fallDistance > 0) {
			if (gm.getCollision(tileX, tileY + 1) && offY > 0 && falling) {
				falling = false;
				fallDistance = 0;
				grounded = true;
				offY = 0;
			}
		}
		if (fallDistance != 0) {
			grounded = false;
		}

		if (offY > GameManager.ts / 2) {
			offY -= GameManager.ts;
			tileY++;
		} else if (offY < -GameManager.ts / 2) {
			offY += GameManager.ts;
			tileY--;
		}
		if (offX > GameManager.ts / 2) {
			offX -= GameManager.ts;
			tileX++;
		} else if (offX < -GameManager.ts / 2) {
			offX += GameManager.ts;
			tileX--;
		}

		if (currentFrame >= 10) {
			currentFrame = 0;
		}

		positionX = tileX * GameManager.ts + offX;
		positionY = tileY * GameManager.ts + offY;

		if (!oldgrounded && grounded) {
			// land.play();
		}

		oldgrounded = grounded;
		

		this.updateComponents(gc, gm, (float) dt);

	}


	@Override
	public void render(GameContainer gc, Renderer r) {
		r.drawImage(stone, (int) positionX, (int) positionY);
		

		this.renderComponents(gc, r);
	}

	@Override
	public void collision(GameObject other) {
		if(other.getTag().equalsIgnoreCase("Button"))
		{
			AABBComponent myC = (AABBComponent) this.findComponent("aabb");
			AABBComponent otherC = (AABBComponent) other.findComponent("aabb");
			
			
            
            	 if (Math.abs(myC.getCenterX() - otherC.getCenterX()) < myC.getHalfWidth() + otherC.getHalfWidth())
                 {
                     if (myC.getCenterY() < otherC.getCenterY())
                     {
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