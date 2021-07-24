package game.objects;

import engine.GameContainer;
import engine.Renderer;
import engine.gfx.ImageTile;
import game.GameManager;
import game.GameObject;
import game.components.AABBComponent;
import game.entities.player.Player;

public class Waterfall extends GameObject {

	@SuppressWarnings("unused")
	private int tileX, tileY;
	
	ImageTile waterfallidle;

	private float anim;
	@SuppressWarnings("unused")
	private int cooldown = 0;
	@SuppressWarnings("unused")
	private int lives = 0;
	
	public Waterfall(int posX, int posY) {
		setTag("Waterfall");
		tileX = posX;
		tileY = posY;
		this.positionX = posX * GameManager.ts;
		this.positionY = posY * GameManager.ts + 4;
		this.width = 16;
		this.height = 24;
		waterfallidle = new ImageTile("/Textures/waterfall.png",16 ,24);

		this.padding = 0;
		this.paddingTop = 12;

		this.addComponent(new AABBComponent(this));
	}

	
	@Override
	public void update(GameContainer gc, GameManager gm, double dt) {
		anim += dt * 5;
		if (anim > 4)
			anim = 0;
		
//		if (lives != 0 && lives > 1 && cooldown == 0) {
//			lives = 1;
//			cooldown = 60;
//			if (lives == 1) {
//				Player.lives--; 
//			}
//
//		}
//		if (cooldown != 0 && cooldown <= 60)
//		{
//			cooldown--;
//			lives = 0;
//		}
		
		if(lives == 60) {
			Player.lives--;
			lives = 0;
		}
		this.updateComponents(gc, gm, (float) dt);
	}

	@Override
	public void render(GameContainer gc, Renderer r) {
		r.drawImageTile(waterfallidle, (int) positionX, (int) positionY, (int) anim, 0);

		this.renderComponents(gc, r);
	}

	@Override
	public void collision(GameObject other) {
		
		if(other.getTag().equalsIgnoreCase("player"))
		{
			lives++;
		}
		
	}

}
