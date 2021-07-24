package engine.gfx;

public class RenderRequest {
	public Image image;
	public int zdepth, offX, offY;

	public RenderRequest(Image img, int zd, int x, int y) {
		image = img;
		offX = x;
		offY = y;
		zdepth = zd;
	}
}
