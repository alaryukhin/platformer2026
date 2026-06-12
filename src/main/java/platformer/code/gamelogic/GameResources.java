package platformer.code.gamelogic;

import java.awt.image.BufferedImage;

import platformer.code.gameengine.loaders.ImageLoader;
import platformer.code.gameengine.loaders.Tileset;
import platformer.code.gameengine.loaders.TilesetLoader;

public final class GameResources {

	public static Tileset tileset;
	
	public static BufferedImage enemy;
	public static BufferedImage anya;
	
	public static void load() {
		try {
			tileset = TilesetLoader.loadTileset("src/main/java/platformer/gfx/tileset.txt", ImageLoader.loadImage("src/main/java/platformer/gfx/tileset.png"));
			
			enemy = ImageLoader.loadImage("src/main/java/platformer/gfx/Enemy.png");
			anya = ImageLoader.loadImage("src/main/java/platformer/gfx/Anya.png");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
