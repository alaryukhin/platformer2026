package platformer.code.gamelogic.tiles;

import java.awt.image.BufferedImage;

import platformer.code.gameengine.hitbox.RectHitbox;
import platformer.code.gamelogic.level.Level;

public class PowerUp extends Tile{

	private int powerType; // 1 = Double Jump
	
	public PowerUp(float x, float y, int size, BufferedImage image, Level level, int powerType) {
		super(x, y, size, image, false, level);
		this.powerType = powerType;
		this.hitbox = new RectHitbox(x*size , y*size, 0, 10, size, size);
	}
	
	public int getPowerType() {
		return powerType;
	}
}
