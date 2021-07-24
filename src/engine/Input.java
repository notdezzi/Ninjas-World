package engine;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class Input implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener {

	GameContainer gc;

	private final int num_keys = 600;
	private boolean keys[] = new boolean[num_keys];
	private boolean[] lastKeys = new boolean[num_keys];

	private final int num_buttons = 5;
	private boolean[] buttons = new boolean[num_buttons];
	private boolean[] lastButtons = new boolean[num_buttons];

	private int mouseX, mouseY;
	private int scroll;

	public Input(GameContainer gc) {
		this.gc = gc;

		for (int i = 0; i < num_keys; i++) {
			keys[i] = false;
		}

		for (int i = 0; i < num_buttons; i++) {
			buttons[i] = false;
		}

		mouseX = 0;
		mouseY = 0;
		scroll = 0;

		gc.getWindow().getCanvas().addKeyListener(this);
		gc.getWindow().getCanvas().addMouseListener(this);
		gc.getWindow().getCanvas().addMouseMotionListener(this);
		gc.getWindow().getCanvas().addMouseWheelListener(this);
	}

	

	public void update() {

		scroll = 0;

		System.arraycopy(keys, 0, lastKeys, 0, num_keys);

		System.arraycopy(buttons, 0, lastButtons, 0, num_buttons);
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		scroll = e.getWheelRotation();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		mouseX = (int) (e.getX() / gc.getScaleX());
		mouseY = (int) (e.getY() / gc.getScaleY());
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		mouseX = (int) (e.getX() / gc.getScaleX());
		mouseY = (int) (e.getY() / gc.getScaleY());
	}

	@Override
	public void mouseClicked(MouseEvent e) {

	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	@Override
	public void mousePressed(MouseEvent e) {
		buttons[e.getButton()] = true;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		buttons[e.getButton()] = false;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		keys[e.getKeyCode()] = true;
	}

	@Override
	public void keyReleased(KeyEvent e) {
		keys[e.getKeyCode()] = false;
	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	public boolean isKey(int keyCode) {
		return keys[keyCode];
	}

	public boolean isKeyUp(int keyCode) {
		return !keys[keyCode] && lastKeys[keyCode];
	}

	public boolean isKeyDown(int keyCode) {
		return keys[keyCode] && !lastKeys[keyCode];
	}

	public boolean isButton(int button) {
		return buttons[button];
	}

	public boolean isButtonUp(int button) {
		return !buttons[button] && lastButtons[button];
	}

	public boolean isButtonDown(int button) {
		return buttons[button] && !lastButtons[button];
	}

	public int getMouseX() {
		return mouseX;
	}

	public int getMouseY() {
		return mouseY;
	}

	public int getScroll() {
		return scroll;
	}

}
