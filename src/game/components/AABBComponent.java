package game.components;

import engine.GameContainer;
import engine.Renderer;
import game.GameManager;
import game.GameObject;
import game.Physics;

public class AABBComponent extends Component {

	private GameObject parent;
	private int centerX, centerY;
	private int halfWidth, halfHeight;

	public AABBComponent(GameObject parent) {
		this.parent = parent;
		
		this.tag = "aabb";
	}

	@Override
	public void update(GameContainer gc, GameManager gm, float dt) {
		centerX = (int) (parent.getPositionX() + (parent.getWidth() / 2));
		centerY = (int) (parent.getPositionY() + (parent.getHeight() / 2) + (parent.getPaddingTop() / 2));
		halfWidth = parent.getWidth() - parent.getPadding();
		halfHeight = parent.getHeight() - parent.getPaddingTop() / 2;

		Physics.addAABBComponent(this);
	}

	@Override
    public void render(GameContainer gc, Renderer r) {

        int positionX = (int) parent.getPositionX();
        int positionY = (int) parent.getPositionY();
//       r.drawRect(parent.getWidth(), parent.getHeight(), positionX, positionY, 0xffffffff);
//        r.drawRect(halfHeight * 2, halfWidth * 2, centerX - halfWidth, centerY - halfHeight, 0xffffffff);

    }

	public GameObject getParent() {
		return parent;
	}

	public void setParent(GameObject parent) {
		this.parent = parent;
	}

	public int getCenterX() {
		return centerX;
	}

	public void setCenterX(int centerX) {
		this.centerX = centerX;
	}

	public int getCenterY() {
		return centerY;
	}

	public void setCenterY(int centerY) {
		this.centerY = centerY;
	}

	public int getHalfWidth() {
		return halfWidth;
	}

	public void setHalfWidth(int halfWidth) {
		this.halfWidth = halfWidth;
	}

	public int getHalfHeight() {
		return halfHeight;
	}

	public void setHalfHeight(int halfHeight) {
		this.halfHeight = halfHeight;
	}

}
