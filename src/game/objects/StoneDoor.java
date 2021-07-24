package game.objects;

import engine.GameContainer;
import engine.Renderer;
import engine.gfx.ImageTile;
import game.GameManager;
import game.GameObject;
import game.components.AABBComponent;

public class StoneDoor extends GameObject {
	public static int tileX;
	public static int tileY;

	@SuppressWarnings("unused")
	private float offX, offY;
	
	private float anim;
	private float openanim;	
	public static int opening = 160;
	private boolean opened = false;
	
	ImageTile Stonedooropening = new ImageTile("/Textures/stone-door-open.png", 16, 48);
	ImageTile StonedoorIdle = new ImageTile("/Textures/stone-door-idle.png", 16, 48);
	
	@SuppressWarnings("static-access")
	public StoneDoor(int posX, int posY) {

		this.tag = "StoneDoor";
		this.tileX = posX;
		this.tileY = posY;
		this.offX = 0;
		this.offY = 0;
		this.positionX = posX * GameManager.ts + 8;
		this.positionY = posY * GameManager.ts;
		this.width = 16;
		this.height = 32;
		padding = 10;
		paddingTop = 0;

		this.addComponent(new AABBComponent(this));
	}

	@Override
	public void update(GameContainer gc, GameManager gm, double dt) {
		
		anim += dt * 5;
		if (anim > 10)
			anim = 0;
		
		if(Button.buttonDown && opening > 70) {
				opening--;
				
			openanim += dt * 8;
			if (openanim > 14)
				openanim = 0;
		}
		if (opening == 70) {

			opened = true;
		}
		
		this.updateComponents(gc, gm, (float) dt);
	}

	@Override
	public void render(GameContainer gc, Renderer r) {
		if(!Button.buttonDown)
		{
			r.drawImageTile(StonedoorIdle, (int) positionX, (int) positionY, (int) anim, 0);
		}
		else if(!opened)
		{
			r.drawImageTile(Stonedooropening, (int) positionX, (int) positionY, (int) openanim, 0);
		}
		this.renderComponents(gc, r);
	}

	@Override
	public void collision(GameObject other) {
	}

}
