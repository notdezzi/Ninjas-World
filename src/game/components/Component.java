package game.components;

import engine.GameContainer;
import engine.Renderer;
import game.GameManager;

public abstract class Component {

	protected String tag;

	public abstract void update(GameContainer gc, GameManager gm, float dt);

	public abstract void render(GameContainer gc, Renderer r);

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

}
