package game.objects;

import engine.GameContainer;
import engine.Renderer;
import engine.gfx.Image;
import game.GameManager;
import game.GameObject;
import game.components.AABBComponent;

public class LevelDoor extends GameObject {

	Image Door = new Image("/Textures/Door.png");

	private boolean gotroughdoor = false;

	private int levelplusplus = 0;
	
	@SuppressWarnings("unused")
	private int tileX, tileY;

	public LevelDoor(int posX, int posY) {
		setTag("door");
		tileX = posX;
		tileY = posY;
		this.positionX = posX * GameManager.ts;
		this.positionY = posY * GameManager.ts - 9;
		this.width = 24 -7;
		this.height = 24 -7;

		this.padding = 5;
		this.paddingTop = 0;

		this.addComponent(new AABBComponent(this));
	}

	@SuppressWarnings("static-access")
	@Override
	public void update(GameContainer gc, GameManager gm, double dt) {

		
		if (levelplusplus != 0 && levelplusplus > 1 && !gotroughdoor) {
			levelplusplus = 1;
			gotroughdoor = true;
			if (levelplusplus == 1) {
				gm.level++;
				gm.loadLevel(gm.level);
				gm.reloadentities();
			}

		}
		
		this.updateComponents(gc, gm, (float) dt);
	}

	@Override
	public void render(GameContainer gc, Renderer r) {

		r.drawImage(Door, (int) positionX, (int) positionY);

		this.renderComponents(gc, r);
	}

	@Override
	public void collision(GameObject other) {

		if (other.getTag().equals("player")) {
			levelplusplus++;
		}

	}

}
