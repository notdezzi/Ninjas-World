package engine.gfx;

public class ImageTile extends Image {

	private int tw, th;

	public ImageTile(String path, int w, int h) {
		super(path);
		tw = w;
		th = h;
	}

	public Image getImage(int tileX, int tileY) {
		int[] p = new int[tw * th];

		for (int y = 0; y < th; y++) {
			for (int x = 0; x < tw; x++) {
				p[x + y * tw] = this.getP()[(x + tileX * tw) + (y + tileY * th) * this.getW()];
			}
		}

		return new Image(p, tw, th);
	}

	public int getTw() {
		return tw;
	}

	public void setTw(int tw) {
		this.tw = tw;
	}

	public int getTh() {
		return th;
	}

	public void setTh(int th) {
		this.th = th;
	}

}
