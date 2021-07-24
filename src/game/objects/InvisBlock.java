package game.objects;

import engine.GameContainer;
import engine.Renderer;
import game.GameManager;
import game.GameObject;
import game.components.AABBComponent;

public class InvisBlock extends GameObject {


	public InvisBlock(int posX, int posY) {
		setTag("InvisBlock");
		this.positionX = posX * GameManager.ts;
		this.positionY = posY * GameManager.ts;
		this.width = 10;
		this.height = 10;

		this.padding = 0;
		this.paddingTop = 0;

		this.addComponent(new AABBComponent(this));
	}

	@Override
	public void update(GameContainer gc, GameManager gm, double dt) {
		// Animation

		this.updateComponents(gc, gm, (float) dt);
	}

	@Override
	public void render(GameContainer gc, Renderer r) {
		

		this.renderComponents(gc, r);

	}

	@Override
	public void collision(GameObject other) {
	}

	}