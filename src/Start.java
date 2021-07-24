import engine.GameContainer;
import game.GameManager;

public class Start {

	public static void main(String[] args) {
		GameManager gm = new GameManager();
		GameContainer gc = new GameContainer(gm);
		gc.setWidth(384);
		gc.setHeight(216);
		double factor = 2.5;
		gc.setScaleX(factor);
		gc.setScaleY(factor);
		gc.setTitle("Far Meadow - dev Build");
		gc.start();
	}

}
