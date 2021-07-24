package engine;

import java.awt.image.DataBufferInt;
import java.util.ArrayList;
import java.util.Collections;

import engine.gfx.Font;
import engine.gfx.Image;
import engine.gfx.ImageTile;
import engine.gfx.Light;
import engine.gfx.LightRequest;
import engine.gfx.RenderRequest;

public class Renderer {

	ArrayList<RenderRequest> toRenderLast = new ArrayList<>();
	ArrayList<LightRequest> lights = new ArrayList<>();

	private int pW, pH;
	private int[] p;
	private int[] zb;
	private int[] lm;
	private int[] lb;
	private boolean[] autoLight;
	private boolean[] lightable;
	private int camX, camY;

	private int ambient = 0xff343434;
	private int menuambient = 0xffffffff;

	private int zdepth = 0;

	boolean processing = false;

	public Renderer(GameContainer gc) {
		pW = gc.getWidth();
		pH = gc.getHeight();
		p = ((DataBufferInt) gc.getWindow().getImage().getRaster().getDataBuffer()).getData();
		zb = new int[p.length];
		lm = new int[p.length];
		lb = new int[p.length];
		autoLight = new boolean[p.length];
		lightable = new boolean[p.length];
	}


	public void setLightable(int x, int y, boolean val) {
		lightable[x + y * pW] = val;
	}

	public void setAutoLight(int x, int y, boolean val) {
		autoLight[x + y * pW] = val;
	}

	public void setLightMap(int x, int y, int value) {

		if (x < 0 || x >= pW || y < 0 || y >= pH) {
			return;
		}

		if (!lightable[x + y * pW]) {
			return;
		}

		int baseColor = lm[x + y * pW];

		int maxRed = Math.max((baseColor >> 16) & 0xff, (value >> 16) & 0xff);
		int maxGreen = Math.max((baseColor >> 8) & 0xff, (value >> 8) & 0xff);
		int maxBlue = Math.max(baseColor & 0xff, (value & 0xff));

		lm[x + y * pW] = (maxRed << 16 | maxGreen << 8 | maxBlue);
	}

	public void setLightBlock(int x, int y, int value) {

		if (x < 0 || x >= pW || y < 0 || y >= pH) {
			return;
		}
		lb[x + y * pW] = value;
	}

	public void setPixel(int x, int y, int value) {

		int index = x + y * pW;

		if ((x < 0 || x >= pW || y < 0 || y >= pH) || ((value >> 24) & 0xff) == 0 || zb[index] > zdepth) {
			return;
		}

		zb[index] = zdepth;

		int alpha = (value >> 24) & 0xff;

		if (alpha < 255) {
			int pixelColor = p[index];

			int red = ((pixelColor >> 16) & 0xff)
					- (int) ((((pixelColor >> 16) & 0xff) - ((value >> 16) & 0xff)) * (alpha / 255f));

			int green = ((pixelColor >> 8) & 0xff)
					- (int) ((((pixelColor >> 8) & 0xff) - ((value >> 8) & 0xff)) * (alpha / 255f));

			int blue = (pixelColor & 0xff) - (int) (((pixelColor & 0xff) - (value & 0xff)) * (alpha / 255f));

			p[index] = (red << 16 | green << 8 | blue);
		} else {
			p[index] = value;
		}
	}

	public void drawText(String text, int x, int y, int color, Font font) {
		int offset = 0;

		x -= camX;
		y -= camY;

		for (int i = 0; i < text.length(); i++) {
			int unicode = text.codePointAt(i);

			for (int j = 0; j < font.getImage().getH(); j++) {
				for (int k = 0; k < font.getWidths()[unicode]; k++) {
					if (font.getImage().getP()[(k + font.getOffsets()[unicode])
							+ j * font.getImage().getW()] == 0xffffffff) {
						setPixel(k + x + offset, j + y, color);
					}
				}
			}
			offset += font.getWidths()[unicode];
		}
	}

	public void drawFixedText(String text, int x, int y, int color, Font font) {
		int offset = 0;

		for (int i = 0; i < text.length(); i++) {
			int unicode = text.codePointAt(i);

			for (int j = 0; j < font.getImage().getH(); j++) {
				for (int k = 0; k < font.getWidths()[unicode]; k++) {
					if (font.getImage().getP()[(k + font.getOffsets()[unicode])
							+ j * font.getImage().getW()] == 0xffffffff) {
						setPixel(k + x + offset, j + y, color);
					}
				}
			}
			offset += font.getWidths()[unicode];
		}
	}

	public void drawRect(int width, int height, int x, int y, int color) {
		x -= camX;
		y -= camY;

		for (int i = 0; i <= width; i++) {
			setPixel(i + x, y, color);
			setPixel(i + x, y + height, color);
		}

		for (int i = 0; i <= width; i++) {
			setPixel(x, y + i, color);
			setPixel(x + width, y + i, color);
		}
	}

	public void drawFillRect(int width, int height, int offX, int offY, int color) {
		offX -= camX;
		offY -= camY;

		int newW = width;
		int newH = height;
		if (offX > pW || offY > pH || offX + newW < 0 || offY + newH < 0) {
			return;
		}
		int newX = 0;
		int newY = 0;
		if (offX + newW > pW)
			newW = newW - ((newW + offX) - (pW));
		if (offY + newH > pH)
			newH = newH - ((newH + offY) - (pH));
		if (offX < 0)
			newX = -offX;
		if (offY < 0)
			newY = -offY;

		for (int y = newY; y < newH; y++) {
			for (int x = newX; x < newW; x++) {
				setPixel(x + offX, y + offY, color);
			}
		}
	}

	public void process() {
		processing = true;

		Collections.sort(toRenderLast, (i0, i1) -> {
			if (i0.zdepth > i1.zdepth) {
				return 1;
			} else if (i0.zdepth < i1.zdepth) {
				return -1;
			}
			return 0;
		});

		for (RenderRequest ir : toRenderLast) {
			zdepth = ir.zdepth;
			drawImage(ir.image, ir.offX, ir.offY);
		}

		for (LightRequest ir : lights) {
			drawLight(ir.light, ir.offX, ir.offY);
		}

		for (int i = 0; i < p.length; i++) {
			int l = lm[i];
			double r = ((l >> 16) & 0xff) / 255.0;
			double g = ((l >> 8) & 0xff) / 255.0;
			double b = (l & 0xff) / 255.0;

			p[i] = (int) (((p[i] >> 16) & 0xff) * r) << 16 | (int) (((p[i] >> 8) & 0xff) * g) << 8
					| (int) ((p[i] & 0xff) * b);

		}

		toRenderLast.clear();
		lights.clear();
		processing = false;
	}

	public void drawImage(Image im, int offX, int offY) {
		if (im.isRenderLast() && !processing) {
			toRenderLast.add(new RenderRequest(im, zdepth, offX, offY));
			return;
		}
		offX -= camX;
		offY -= camY;
		int newW = im.getW();
		int newH = im.getH();
		if (offX > pW || offY > pH || offX + newW < 0 || offY + newH < 0) {
			return;
		}
		int newX = 0;
		int newY = 0;
		if (offX + newW > pW)
			newW = newW - ((newW + offX) - (pW));
		if (offY + newH > pH)
			newH = newH - ((newH + offY) - (pH));
		if (offX < 0)
			newX = -offX;
		if (offY < 0)
			newY = -offY;
		for (int y = newY; y < newH; y++) {
			for (int x = newX; x < newW; x++) {
				setPixel(x + offX, y + offY, im.getP()[x + y * im.getW()]);
				setLightBlock(x + offX, y + offY, im.getLightBlock());
				setAutoLight(x + offX, y + offY, im.isAutoLighted());
				setLightable(x + offX, y + offY, im.isLightable());
			}
		}
	}

	public void drawFixedImage(Image im, int offX, int offY) {
		if (im.isRenderLast() && !processing) {
			toRenderLast.add(new RenderRequest(im, zdepth, offX, offY));
			return;
		}
		int newW = im.getW();
		int newH = im.getH();
		if (offX > pW || offY > pH || offX + newW < 0 || offY + newH < 0) {
			return;
		}
		int newX = 0;
		int newY = 0;
		if (offX + newW > pW)
			newW = newW - ((newW + offX) - (pW));
		if (offY + newH > pH)
			newH = newH - ((newH + offY) - (pH));
		if (offX < 0)
			newX = -offX;
		if (offY < 0)
			newY = -offY;
		for (int y = newY; y < newH; y++) {
			for (int x = newX; x < newW; x++) {
				setPixel(x + offX, y + offY, im.getP()[x + y * im.getW()]);
				setLightBlock(x + offX, y + offY, im.getLightBlock());
				setAutoLight(x + offX, y + offY, im.isAutoLighted());
				setLightable(x + offX, y + offY, im.isLightable());
			}
		}
	}

	public void drawImageTile(ImageTile im, int offX, int offY, int tileX, int tileY) {
		offX -= camX;
		offY -= camY;
		if (im.isRenderLast() && !processing) {
			toRenderLast.add(new RenderRequest(im.getImage(tileX, tileY), zdepth, offX, offY));
			return;
		}

		int newW = im.getTw();
		int newH = im.getTh();
		if (offX > pW || offY > pH || offX + newW < 0 || offY + newH < 0) {
			return;
		}
		int newX = 0;
		int newY = 0;
		if (offX + newW > pW)
			newW = newW - ((newW + offX) - (pW));
		if (offY + newH > pH)
			newH = newH - ((newH + offY) - (pH));
		if (offX < 0)
			newX = -offX;
		if (offY < 0)
			newY = -offY;
		for (int y = newY; y < newH; y++) {
			for (int x = newX; x < newW; x++) {
				setPixel(x + offX, y + offY,
						im.getP()[(x + tileX * im.getTw()) + (y + tileY * im.getTh()) * im.getW()]);
				setLightBlock(x + offX, y + offY, im.getLightBlock());
			}
		}
	}

	public void drawLight(Light light, int offX, int offY) {
		offX -= camX / 2;
		offY -= camY / 2;

		if (!processing) {
			lights.add(new LightRequest(light, offX, offY));
			return;
		}

		for (int i = 0; i <= light.getDiameter(); i++) {
			drawLightLine(light, light.getRadius(), light.getRadius(), i, 0, offX, offY);
			drawLightLine(light, light.getRadius(), light.getRadius(), i, light.getDiameter(), offX, offY);
			drawLightLine(light, light.getRadius(), light.getRadius(), 0, i, offX, offY);
			drawLightLine(light, light.getRadius(), light.getRadius(), light.getDiameter(), i, offX, offY);
		}
	}

	private void drawLightLine(Light l, int x0, int y0, int x1, int y1, int offX, int offY) {
		int dx = Math.abs(x1 - x0);
		int dy = Math.abs(y1 - y0);

		int sx = x0 < x1 ? 1 : -1;
		int sy = y0 < y1 ? 1 : -1;

		int err = dx - dy;
		int er2;

		while (true) {
			int screenX = x0 - l.getRadius() + offX;
			int screenY = y0 - l.getRadius() + offY;

			int lightColor = l.getLightValue(x0, y0);

			if (lightColor == 0) {
				return;
			}

			if (screenX < 0 || screenX >= pW || screenY < 0 || screenY >= pH) {

			} else {
				if (lb[screenX + screenY * pW] == Light.FULL) {
					return;
				}
			}

			setLightMap(screenX, screenY, lightColor);

			if (x0 == x1 && y0 == y1)
				break;

			er2 = 2 * err;

			if (er2 > -1 * dy) {
				err -= dy;
				x0 += sx;
			}

			if (er2 < dx) {
				err += dx;
				y0 += sy;
			}
		}
	}

	public void clear() {
		for (int x = 0; x < p.length; x++) {
			p[x] = 0;
			zb[x] = 0;
			if (autoLight[x]) {
				lm[x] = 0xffffffff;
			} else {
				lm[x] = ambient;
			}
			lb[x] = Light.NONE;
			lightable[x] = true;
			autoLight[x] = false;
		}
	}

	public void menuclear() {
		for (int x = 0; x < p.length; x++) {
			p[x] = 0;
			zb[x] = 0;
			if (autoLight[x]) {
				lm[x] = 0xffffffff;
			} else {
				lm[x] = menuambient;
			}
			lb[x] = Light.NONE;
			lightable[x] = true;
			autoLight[x] = false;
		}
	}

	public int getZdepth() {
		return zdepth;
	}

	public void setZdepth(int zdepth) {
		this.zdepth = zdepth;
	}

	public void setAmbient(int ambient) {
		this.ambient = ambient;
	}

	public int getCamX() {
		return camX;
	}

	public void setCamX(int camX) {
		this.camX = camX;
	}

	public int getCamY() {
		return camY;
	}

	public void setCamY(int camY) {
		this.camY = camY;
	}

}
