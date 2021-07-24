package engine.gfx;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Image {
	private int w, h;
	int[] p;
	private boolean renderLast = false;
	private int lightBlock = 0;
	private boolean autoLighted = false;
	private boolean lightable = true;

	public boolean isLightable() {
		return lightable;
	}

	public void setLightable(boolean lightable) {
		this.lightable = lightable;
	}

	public boolean isAutoLighted() {
		return autoLighted;
	}

	public void setAutoLighted(boolean autoLighted) {
		this.autoLighted = autoLighted;
	}

	public int getLightBlock() {
		return lightBlock;
	}

	public void setLightBlock(int lightBlock) {
		this.lightBlock = lightBlock;
	}

	public boolean isRenderLast() {
		return renderLast;
	}

	public void setRenderLast(boolean renderLast) {
		this.renderLast = renderLast;
	}

	BufferedImage image = null;

	public Image(int[] p, int w, int h) {
		this.p = p;
		this.h = h;
		this.w = w;
	}

	public Image(String path) {
		try {
			image = ImageIO.read(Image.class.getResourceAsStream(path));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		w = image.getWidth();
		h = image.getHeight();

		p = image.getRGB(0, 0, w, h, null, 0, w);

		image.flush();
	}

	public int getW() {
		return w;
	}

	public void setW(int w) {
		this.w = w;
	}

	public int getH() {
		return h;
	}

	public void setH(int h) {
		this.h = h;
	}

	public int[] getP() {
		return p;
	}

	public void setP(int[] p) {
		this.p = p;
	}
}
