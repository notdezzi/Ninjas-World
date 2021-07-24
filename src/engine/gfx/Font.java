package engine.gfx;

public class Font {

	public static final Font COMICBIG = new Font("/fonts/comic_big.png");
	public static final Font COMICMED = new Font("/fonts/comic_med.png");
	public static final Font COMICSMALL = new Font("/fonts/comic_small.png");


	private Image image;
	private int[] offsets;
	private int[] widths;

	public Font(String path) {
		image = new Image(path);
		offsets = new int[256];
		widths = new int[256];

		int unicode = 0;

		for (int i = 0; i < image.getW(); i++) {
			if (image.getP()[i] == 0xff0000ff) {
				offsets[unicode] = i;
			}
			if (image.getP()[i] == 0xffffff00) {
				widths[unicode] = i - offsets[unicode];
				unicode++;
			}

		}
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	public int[] getOffsets() {
		return offsets;
	}

	public void setOffsets(int[] offsets) {
		this.offsets = offsets;
	}

	public int[] getWidths() {
		return widths;
	}

	public void setWidths(int[] widths) {
		this.widths = widths;
	}
}
