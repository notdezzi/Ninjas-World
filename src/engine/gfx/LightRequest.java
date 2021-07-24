package engine.gfx;

public class LightRequest {
	public Light light;
	public int offX, offY;

	public LightRequest(Light light, int offX, int offY) {
		this.light = light;
		this.offX = offX;
		this.offY = offY;
	}
}
