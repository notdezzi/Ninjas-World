package game;

import java.util.ArrayList;

import engine.GameContainer;
import engine.Renderer;
import game.components.Component;

public abstract class GameObject {

	protected String tag;
	public float positionX;
	public float positionY;
	protected boolean dead = false;

	protected int padding, paddingTop;
	protected int width, height;

	public int getPadding() {
		return padding;
	}

	public void setPadding(int padding) {
		this.padding = padding;
	}

	public int getPaddingTop() {
		return paddingTop;
	}

	public void setPaddingTop(int paddingTop) {
		this.paddingTop = paddingTop;
	}

	protected ArrayList<Component> components = new ArrayList<>();

	public abstract void update(GameContainer gc, GameManager gm, double dt);

	public abstract void render(GameContainer gc, Renderer r);

	public abstract void collision(GameObject other);

	public void updateComponents(GameContainer gc, GameManager gm, float dt) {
		for (Component c : components) {
			c.update(gc, gm, dt);
		}
	}

	public void renderComponents(GameContainer gc, Renderer r) {
		for (Component c : components) {
			c.render(gc, r);
		}
	}

	public void addComponent(Component c) {
		components.add(c);
	}

	public void removeComponent(String tag) {

		components.removeIf(component -> component.getTag().equalsIgnoreCase(tag));

	}

	public Component findComponent(String tag) {
		for (Component component : components) {
			if (component.getTag().equalsIgnoreCase(tag)) {
				return component;
			}
		}
		return null;

	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public float getPositionX() {
		return positionX;
	}

	public void setPositionX(float positionX) {
		this.positionX = positionX;
	}

	public float getPositionY() {
		return positionY;
	}

	public void setPositionY(float positionY) {
		this.positionY = positionY;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public boolean isDead() {
		return dead;
	}

	public void setDead(boolean dead) {
		this.dead = dead;
	}
}
