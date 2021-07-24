package game.objects;

import engine.GameContainer;
import engine.Renderer;
import engine.gfx.Image;
import game.GameManager;
import game.GameObject;
import game.components.AABBComponent;

public class Button extends GameObject {

	Image buttonup = new Image("/Textures/button.png");
	Image buttondown = new Image("/Textures/button_pressed.png");
	
	public static int colliding = 0;
	public static boolean buttonDown = false;
	
	public Button(int posX, int posY) {
		setTag("button");
		this.positionX = posX * GameManager.ts;
		this.positionY = posY * GameManager.ts - 4;
		this.width = 16;
		this.height = 4;
		this.padding = 6;
		this.paddingTop = 2;

		this.addComponent(new AABBComponent(this));
	}
	
	@Override
	public void update(GameContainer gc, GameManager gm, double dt) {
		
		
		buttonDown = false;
		if (colliding == 180)
			colliding = 1;

		if (colliding != 0) {
			buttonDown = true;
		}

		this.updateComponents(gc, gm, (float) dt);
	}

	@Override
	public void render(GameContainer gc, Renderer r) {
		
		
		if(buttonDown) {
			r.drawImage(buttondown, (int) positionX, (int) positionY);
		}
		if(!buttonDown) {
			r.drawImage(buttonup, (int) positionX, (int) positionY);
		}

		this.renderComponents(gc, r);
	}

	@Override
	public void collision(GameObject other) {
		if (other.getTag().equalsIgnoreCase("Stone")) {
			colliding++;
		
	}else if(other.getTag().equalsIgnoreCase("Player")){
		
	} else{
			colliding = 0;
			StoneDoor.opening = 160;
		}
		
	}

}
