import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.imageio.ImageIO;

class ImageRotator {
	private final BufferedImage originalImage;

	public ImageRotator(BufferedImage originalImage) {
		this.originalImage = originalImage;
	}

	private double toRadians(int deg) {
		return Math.PI * deg / 180.0;
	}

	public BufferedImage getRotatedImage(int deg) {
		AffineTransform transform = new AffineTransform();

		int halfWidth = originalImage.getWidth() / 2;
		int halfHeight = originalImage.getHeight() / 2;

		transform.rotate(toRadians(deg), halfWidth, halfHeight);
		// This is optional if you want to make it to spin around the center instead of rotating the whole image
		// transform.translate(-halfWidth, -halfHeight);

		AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
		BufferedImage rotated = op.filter(originalImage, null);
		return rotated;
	}

	private static void logAndExit(String msg) {
		System.err.println(msg);
		System.exit(1);
	}

	private static int parseDegrees(String deg) {
		try {
			return Integer.parseInt(deg);
		} catch (NumberFormatException e) {
			logAndExit("Error parsing degrees: " + e.getMessage());
		}
		return 0; // unreachable
	}

	public static void main(String[] args) {
		if (args.length < 2) {
			logAndExit("Usage: java ImageRotator <image_path> <degrees>");
		}

		String imageToLoad = args[0];
		int degrees = parseDegrees(args[1]);

		BufferedImage img = null;
		try {
			Path p = Path.of(imageToLoad);
			img = ImageIO.read(Files.newInputStream(p));
		} catch (IOException e) {
			logAndExit("Error reading image: " + e.getMessage());
		}
		
		ImageRotator rotator = new ImageRotator(img);
		BufferedImage rotated = rotator.getRotatedImage(degrees);
		System.out.println("Rotated image created successfully!");

		try {
			ImageIO.write(rotated, "png", Files.newOutputStream(Path.of("rotated.png")));
		} catch (IOException e) {
			logAndExit("Error writing image: " + e.getMessage());
		}
	}
}
