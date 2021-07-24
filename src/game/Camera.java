package game;

import engine.GameContainer;
import engine.Renderer;

public class Camera {
	private GameObject target;
	private String targetTag = null;
	private float offX, offY;

	public Camera(String tag) {
		this.targetTag = tag;
	}

	public void update(GameContainer gc, GameManager gm, double dt) {
		if (target == null) {
			target = gm.getObject(targetTag);
		}
		if (target == null) {
			return;
		}

		float targetX = (target.getPositionX() + target.getWidth() / 2) - gc.getWidth() / 2;
		float targetY = (target.getPositionY() + target.getHeight() / 2) - gc.getHeight() / 2;

		offX -= dt * (int) (offX - targetX) * 5;
		offY -= dt * (int) (offY - targetY) * 5;

		if (offX < 0)
			offX = 0;
		if (offY < 0)
			offY = 0;
		if (offX + gc.getWidth() > gm.getlW() * GameManager.ts)
			offX = gm.getlW() * GameManager.ts - gc.getWidth();
		if (offY + gc.getHeight() > gm.getlH() * GameManager.ts)
			offY = gm.getlH() * GameManager.ts - gc.getHeight();
	}

	public void render(Renderer r) {
		r.setCamX((int) offX);
		r.setCamY((int) offY);
	}

	public GameObject getTarget() {
		return target;
	}

	public void setTarget(GameObject target) {
		this.target = target;
	}

	public String getTargetTag() {
		return targetTag;
	}

	public void setTargetTag(String targetTag) {
		this.targetTag = targetTag;
	}

	public float getOffX() {
		return offX;
	}

	public void setOffX(float offX) {
		this.offX = offX;
	}

	public float getOffY() {
		return offY;
	}

	public void setOffY(float offY) {
		this.offY = offY;
	}
}
