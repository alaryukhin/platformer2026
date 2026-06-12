package platformer.code.gamelogic.enemies;

import java.awt.Color;
import java.awt.Graphics;

import platformer.code.gameengine.GameObject;
import platformer.code.gameengine.hitbox.RectHitbox;

/**
 * Projectile class represents bullets shot by Anya enemy
 * Moves in a straight line and can collide with the player
 */
public class Projectile extends GameObject {
	private float velocityX;
	private float velocityY;
	private RectHitbox hitbox;
	private static final int SIZE = 8;
	private static final float PROJECTILE_SPEED = 300f; // pixels per second
	
	public Projectile(float x, float y, float directionX, float directionY) {
		super(x, y, SIZE, SIZE);
		
		// Normalize direction and apply speed
		float length = (float) Math.sqrt(directionX * directionX + directionY * directionY);
		if (length > 0) {
			velocityX = (directionX / length) * PROJECTILE_SPEED;
			velocityY = (directionY / length) * PROJECTILE_SPEED;
		} else {
			velocityX = PROJECTILE_SPEED;
			velocityY = 0;
		}
		
		this.hitbox = new RectHitbox(this, 0, 0, SIZE, SIZE);
	}
	
	@Override
	public void update(float tslf) {
		// Update position based on velocity
		position.x += velocityX * tslf;
		position.y += velocityY * tslf;
		// keep hitbox in sync with position
		if (hitbox != null) hitbox.update();
	}
	
	@Override
	public void draw(Graphics g) {
		// Draw projectile as a small red circle
		g.setColor(Color.RED);
		g.fillOval((int)position.x - SIZE/2, (int)position.y - SIZE/2, SIZE, SIZE);
		g.setColor(Color.DARK_GRAY);
		g.drawOval((int)position.x - SIZE/2, (int)position.y - SIZE/2, SIZE, SIZE);
	}
	
	public RectHitbox getHitbox() {
		return hitbox;
	}
	
	/**
	 * Check if projectile has gone off-screen or too far away
	 * @param mapWidth width of the map in pixels
	 * @param mapHeight height of the map in pixels
	 * @return true if projectile should be removed
	 */
	public boolean shouldRemove(float mapWidth, float mapHeight) {
		return position.x < -50 || position.x > mapWidth + 50 || 
		       position.y < -50 || position.y > mapHeight + 50;
	}
}
