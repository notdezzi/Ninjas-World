package game.objects;

import engine.GameContainer;
import engine.Renderer;
import engine.gfx.ImageTile;
import game.GameManager;
import game.GameObject;
import game.components.AABBComponent;
import game.entities.player.Player;

public class Coin extends GameObject {

	private float anim;
	private float pickanim;
	private boolean pickedup = false;
	private boolean hide = false;

	private int coinspickedup = 0;

	ImageTile coin;
	ImageTile coinpickup;

	@SuppressWarnings("unused")
	private int tileX, tileY;

	public Coin(int posX, int posY) {
		setTag("Coin");
		tileX = posX;
		tileY = posY;
		this.positionX = posX * GameManager.ts;
		this.positionY = posY * GameManager.ts;
		this.width = 8;
		this.height = 8;
		coin = new ImageTile("/Textures/coin-idle.png", 8, 8);
		coinpickup = new ImageTile("/Textures/coin-pickup.png", 8, 8);

		this.padding = 0;
		this.paddingTop = 0;

		this.addComponent(new AABBComponent(this));
	}

	@Override
	public void update(GameContainer gc, GameManager gm, double dt) {
		// Animation

		anim += dt * 5;
		if (anim > 6)
			anim = 0;

		if (coinspickedup != 0 && coinspickedup > 1 && !pickedup) {
			coinspickedup = 1;
			pickedup = true;
			hide = true;
			if (coinspickedup == 1) {
				Player.coins++;
			}

		}

		this.updateComponents(gc, gm, (float) dt);
	}

	public int getCoinspickedup() {
		return coinspickedup;
	}

	public void setCoinspickedup(int coinspickedup) {
		this.coinspickedup = coinspickedup;
	}

	@Override
	public void render(GameContainer gc, Renderer r) {
		if (!pickedup && !hide) {
			r.drawImageTile(coin, (int) positionX, (int) positionY, (int) anim, 0);
		}
		if (pickedup && !hide) {
			r.drawImageTile(coinpickup, (int) positionX, (int) positionY, (int) pickanim, 0);
		}

		this.renderComponents(gc, r);

	}

	@Override
	public void collision(GameObject other) {
		if (other.getTag().equals("player")) {
			coinspickedup++;
		}
	}

}
