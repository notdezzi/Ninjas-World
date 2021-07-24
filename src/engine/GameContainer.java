package engine;

public class GameContainer implements Runnable {
	int frameCount = 0;
	double time = 0;
	int fpss = 0;

	private boolean render = false;
	private int width = 320, height = 240;

	private double globalScale = 2.5;
	private double scaleX = globalScale, scaleY = globalScale;

	private String title = "Ninjas World - dev Build";

	private Thread thread;
	private Window window;
	private Renderer renderer;
	private Input input;
	private AbstractGame game;

	private boolean running = false;
	public static double fps = 60;
	public static double update_lock = 1.0 / fps;

	public GameContainer(AbstractGame game) {
		this.game = game;
	}

	public void start() {
		thread = new Thread(this);
		window = new Window(this);
		renderer = new Renderer(this);
		input = new Input(this);
		thread.start();
	}

	public void stop() {

	}

	@Override
	public void run() {
		running = true;

		double then = System.nanoTime() / 1000000000d;
		double now = 0;
		double dt = 0;
		double passed = 0;

		game.init(this);

		while (running) {
			render = false;
			now = System.nanoTime() / 1000000000.0;
			dt = now - then;
			then = now;
			time += dt;
			passed += dt;
			while (passed >= update_lock) {
				render = true;
				passed -= update_lock;
				update();
			}
			if (render) {
				render();
			} else {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		}
		dispose();
	}

	public void update() {
		if (time >= 1) {
			time = 0;
			fpss = frameCount;
			frameCount = 0;
		}
		game.update(this, update_lock);
		input.update();
	}

	public void render() {
		frameCount++;
		game.render(this, renderer);
		renderer.process();
		// renderer.drawFixedText("fps: "+fpss, 0, 0, 0xff00ffff, Font.SMALL);
		window.update();
	}

	public void dispose() {

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

	public double getScaleX() {
		return scaleX;
	}

	public void setScaleX(double scaleX) {
		this.scaleX = scaleX;
	}

	public double getScaleY() {
		return scaleY;
	}

	public void setScaleY(double scaleY) {
		this.scaleY = scaleY;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Window getWindow() {
		return window;
	}

	public Input getInput() {
		return input;
	}

	public Renderer getRenderer() {
		return renderer;
	}

}
