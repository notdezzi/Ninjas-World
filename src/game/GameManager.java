package game;

import java.awt.event.KeyEvent;
import java.util.ArrayList;

import engine.AbstractGame;
import engine.GameContainer;
import engine.Renderer;
import engine.gfx.Font;
import engine.gfx.Image;
import engine.gfx.ImageTile;
import engine.gfx.Light;
import game.entities.Enemies;
import game.entities.enemies.Mushroom;
import game.entities.enemies.Mushroom1;
import game.entities.player.Player;
import game.objects.Button;
import game.objects.Coin;
import game.objects.InvisBlock;
import game.objects.LevelDoor;
import game.objects.Stone;
import game.objects.StoneDoor;
import game.objects.Waterfall;

public class GameManager extends AbstractGame {

	public static final int ts = 16;

	private ArrayList<GameObject> objects = new ArrayList<>();
	private Camera camera;
	

	int selector = 0;
	
	public int maxlevels = 3;

	public int playcolor = 0xffffffff;
	public int optionscolor = 0xffffffff;
	public int creditcolor = 0xffffffff;
	public int selected = 0;
	public int selectedminus = 0;
	public int cooldown = 0;
	
	public static boolean optionstoggled = false;
	public static boolean creditstoggled = false;

	private boolean[] collision;
	private int lW, lH;

//	 private SoundClip music = new SoundClip("/audio/music.wav");

	public static int level = 0;

	Image background;
	Image foreground;
	Image tileset;
	Image menubackground = new Image("/gui/mainmenu/background-menu.png");
	Image coins = new Image("/gui/hud/coin.png");
	Image lives = new Image("/gui/hud/heart.png");
	ImageTile block = new ImageTile("/Textures/tileset.png", ts, ts);
	Image title = new Image("/Textures/title.png");
	Image playunselected = new Image("/gui/mainmenu/play-unselected1.png");
	Image playselected = new Image("/gui/mainmenu/play-selected.png");
	Image controlsunselected = new Image("/gui/mainmenu/controls-unselected.png");
	Image controlsselected = new Image("/gui/mainmenu/controls-selected.png");
	Image creditsunselected = new Image("/gui/mainmenu/credits-unselected.png");
	Image creditsselected = new Image("/gui/mainmenu/credits-selected.png");
	Image controls = new Image("/gui/mainmenu/controls.png");
	Image credits = new Image("/gui/mainmenu/credits.png");

	Image acd = new Image("/gui/hud/acd.png");

	Player player;
	Enemies enemy;
	Coin coin;
	Light light = new Light(200, 0xffffffff);
	LevelDoor door;
	Button button;
	Stone stone;
	StoneDoor stonedoor;
	InvisBlock invisblock;
	Waterfall waterfall;
	Mushroom mushroom;

	Mushroom1 mushroom1;

	public GameManager() {
		
		block.setLightBlock(Light.FULL);
//		music.setVolume(70);
		reloadentities();
	}

	public void reloadentities() {
		objects.clear();
		if(level == 1) {
			player = new Player(1, 17);
			objects.add(player);

			mushroom = new Mushroom(17, 6);
			objects.add(mushroom); 
			mushroom1 = new Mushroom1(23, 12);
			objects.add(mushroom1); 
			coin = new Coin(12, 1);
			objects.add(coin);
			coin = new Coin(5, 10);
			objects.add(coin);
			button = new Button(3,12);
			objects.add(button);
			stone = new Stone(8, 10);
			objects.add(stone);
			stonedoor = new StoneDoor(14, 4);
			objects.add(stonedoor);
			invisblock = new InvisBlock(25, 6);
			objects.add(invisblock);
			invisblock = new InvisBlock(14, 6);
			objects.add(invisblock);
			invisblock = new InvisBlock(19, 12);
			objects.add(invisblock);
			invisblock = new InvisBlock(28, 12);
			objects.add(invisblock);
			waterfall = new Waterfall(8,18);
			objects.add(waterfall);
			waterfall = new Waterfall(7,18);
			objects.add(waterfall);
			waterfall = new Waterfall(9,18);
			objects.add(waterfall);
			waterfall = new Waterfall(8,19);
			objects.add(waterfall);
			waterfall = new Waterfall(7,19);
			objects.add(waterfall);
			waterfall = new Waterfall(9,19);
			objects.add(waterfall);
			
			door = new LevelDoor(26, 18);
			objects.add(door);
			loadLevel(level);
			camera = new Camera("player");
			}
		
		if(level == 2) {
			player = new Player(1, 14);
			objects.add(player);
			enemy = new Enemies(17, 6);
			objects.add(enemy); 
			coin = new Coin(12, 1);
			objects.add(coin);
			coin = new Coin(5, 12);
			objects.add(coin);
			door = new LevelDoor(29, 14);
			objects.add(door);

			loadLevel(level);
			camera = new Camera("player");
			}
		if(level == 3) {
			player = new Player(1, 14);
			objects.add(player);
			door = new LevelDoor(29, 14);
			objects.add(door);

			loadLevel(level);
			camera = new Camera("player");
			}
	}
	
	
	public boolean getCollision(int x, int y) {
		if (x < 0 || x >= lW || y < 0 || y >= lH)
			return true;
		return collision[x + y * lW];

	}

	@Override
	public void init(GameContainer gc) {
		gc.getRenderer().setAmbient(0xfffffff);
	}
	
	@Override
	public void update(GameContainer gc, double dt) {

		if(level == maxlevels) {
			level = 0;
			Player.lives = 3;
		}
		
		if(optionstoggled == true) {
			
			if (gc.getInput().isKey(KeyEvent.VK_ESCAPE)) {
				optionstoggled = false;
			}
			
		}
		if(creditstoggled == true) {

			if (gc.getInput().isKey(KeyEvent.VK_ESCAPE)) {
				creditstoggled = false;
			}
			
		}
		
		if(level == 0) {
			Renderer r = new Renderer(gc);
			if (selector == 0 && cooldown == 0) {

				if (gc.getInput().isKeyDown(KeyEvent.VK_UP) || gc.getInput().isKeyDown(KeyEvent.VK_W))
				{
					selector = 2;
					cooldown = 15;
				}
				if (gc.getInput().isKeyDown(KeyEvent.VK_DOWN) || gc.getInput().isKeyDown(KeyEvent.VK_S))
				{
					 selector = 1;
					 cooldown = 15;
				}
				if(gc.getInput().isKeyDown(KeyEvent.VK_ENTER) || gc.getInput().isKeyDown(KeyEvent.VK_SPACE)) {
					level += 1;
					reloadentities();
					render(gc, r);
				}
			}
			if (selector == 1 && cooldown == 0) {
				if (gc.getInput().isKeyDown(KeyEvent.VK_DOWN) || gc.getInput().isKeyDown(KeyEvent.VK_S))
				{
					selector = 2;
					 cooldown = 15;
				}
				if (gc.getInput().isKeyDown(KeyEvent.VK_UP) || gc.getInput().isKeyDown(KeyEvent.VK_W))
				{
					selector = 0;
					 cooldown = 15;
				}
				if(gc.getInput().isKeyDown(KeyEvent.VK_ENTER) || gc.getInput().isKeyDown(KeyEvent.VK_SPACE)) {
					optionstoggled = true;
				}
			}
			if (selector == 2 && cooldown == 0) {
				if (gc.getInput().isKeyDown(KeyEvent.VK_DOWN) || gc.getInput().isKeyDown(KeyEvent.VK_S))
				{
					selector = 0;
					 cooldown = 15;
				}
				if (gc.getInput().isKeyDown(KeyEvent.VK_UP) || gc.getInput().isKeyDown(KeyEvent.VK_W))
				{
					selector = 1;
					 cooldown = 15;
				}
				if(gc.getInput().isKeyDown(KeyEvent.VK_ENTER) || gc.getInput().isKeyDown(KeyEvent.VK_SPACE)) {
					creditstoggled = true;
				}
			}
			if (cooldown != 0 && cooldown <= 15)
			{
				cooldown--;
				selected = 0;
			}
			
		}
		
		
		if(level >= 1) {
		
			
		for (int i = 0; i < objects.size(); i++) {
			GameObject go = objects.get(i);
			go.update(gc, this, dt);
			if (go.isDead()) {
				objects.remove(i);
				i--;
			}
		}
		Physics.update();

		camera.update(gc, this, dt);
		}
	}

	@Override
	public void render(GameContainer gc, Renderer r) {
		r.clear();


			if(creditstoggled == true)
			{
			r.drawImage(menubackground, 0, 0);

			r.drawFixedImage(credits,(384 / 2) - (credits.getW() / 2), (216 / 2) - (credits.getH() / 2));
			}
			if(optionstoggled == true)
			{
			r.drawImage(menubackground, 0, 0);
			r.drawFixedImage(controls,(384 / 2) - (controls.getW() / 2), (216 / 2) - (controls.getH() / 2));
			}
			if(level == 0 && !optionstoggled && !creditstoggled) {
			
			r.drawImage(menubackground, 0, 0);
			
			
			r.drawFixedImage(title, (384 / 2) - ( title.getW() /2 ), (216 / 2) - (title.getH() / 2) - 50);
			
			if (selector == 0) {
				r.drawFixedImage(playselected, 2 + (384 / 2) - (playselected.getW() / 2), (216 / 2) + (playselected.getH() + 5));
			}else {
				r.drawFixedImage(playunselected, 2 + (384 / 2) - (playunselected.getW() / 2), (216 / 2) + (playunselected.getH() + 5));
			}
			if (selector == 1) {
				r.drawFixedImage(controlsselected, 2 + (384 / 2) - (controlsselected.getW() / 2), (216 / 2) + (controlsselected.getH() + 30));
			}else {
				r.drawFixedImage(controlsunselected, 2 + (384 / 2) - (controlsunselected.getW() / 2), (216 / 2) + (controlsunselected.getH() + 30));
			}
			if (selector == 2) {
				r.drawFixedImage(creditsselected, 2 + (384 / 2) - (creditsselected.getW() / 2), (216 / 2) + (creditsselected.getH() + 55));
			}else {
				r.drawFixedImage(creditsunselected, 2 + (384 / 2) - (creditsunselected.getW() / 2), (216 / 2) + (creditsunselected.getH() + 55));
			}

			
		}
		
		if(level >= 1 && !optionstoggled && !creditstoggled) {
		camera.render(r);
		r.drawFixedImage(background, 0, 0);
//		 r.drawImage(foreground, 0, 0);

		r.drawImage(tileset, 0, 0);

//		for (int y = 0; y < lH; y++) {
//			for (int x = 0; x < lW; x++) {
//				if (collision[x + y * lW]) {
//					r.drawImageTile(block, x * ts, y * ts, 1, 0);
//				}
//			}
//		}

		for (GameObject go : objects) {
			go.render(gc, r);
		}

//		 r.drawLight(light, (int) player.getPositionX() + ts / 2, (int)
//		 player.getPositionY() + ts / 2);

		r.drawFixedImage(coins, 4, 22);
		r.drawFixedText("" + Player.coins + "", 14, 21, 0xf4f4f4f4, Font.COMICMED);
		r.drawFixedImage(lives, 2, 2);
		r.drawFixedText("" + Player.lives + "", 22, 3, 0xf4f4f4f4, Font.COMICBIG);
		r.drawFixedImage(acd, 2, 38);
		r.drawFixedText("" + Player.attackcooldown / 60 + "", 14, 35, 0xf4f4f4f4, Font.COMICMED);
		}
	}

	public int getlW() {
		return lW;
	}

	public void setlW(int lW) {
		this.lW = lW;
	}

	public int getlH() {
		return lH;
	}

	public void setlH(int lH) {
		this.lH = lH;
	}

	public void addObject(GameObject object) {
		objects.add(object);
	}

	public GameObject getObject(String tag) {
		for (GameObject object : objects) {
			if (object.getTag().toLowerCase().equals(tag.toLowerCase())) {
				return object;
			}
		}

		return null;
	}

	public void loadLevel(int l) {
		Image level = new Image("/levels/l" + l + ".png");
//		foreground = new Image("/levels/f" + l + ".png");
		background = new Image("/levels/b" + l + ".png");
		tileset = new Image("/levels/t" + l + ".png");
		lW = level.getW();
		lH = level.getH();
		collision = new boolean[lW * lH];

		for (int y = 0; y < lH; y++) {
			for (int x = 0; x < lW; x++) {
				if (level.getP()[x + y * lW] == -16777216) {
					collision[x + y * lW] = true;
				} else {
					collision[x + y * lW] = false;
				}
			}
		}
//		 music.loop();
	}

//	public SoundClip getMusic() {
//		return music;
//	}

	public static void main(String[] args) {
		GameManager gm = new GameManager();
		GameContainer gc = new GameContainer(gm);
		gc.setWidth(384);
		gc.setHeight(216);
		double factor = 2.5;
		gc.setScaleX(factor);
		gc.setScaleY(factor);
		gc.setTitle("Ninja's World - dev Build");
		gc.start();
	}

}
