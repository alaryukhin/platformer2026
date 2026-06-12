package platformer.code.gamelogic.enemies;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import platformer.code.gameengine.PhysicsObject;
import platformer.code.gameengine.hitbox.RectHitbox;
import platformer.code.gamelogic.GameResources;
import platformer.code.gamelogic.level.Level;

/**
 * AnyaEnemy - A stationary enemy that shoots projectiles at the player
 * Anya stands in one place and fires projectiles in the direction of the player
 */
public class AnyaEnemy extends PhysicsObject {
	private BufferedImage image;
	private float shootCooldown = 0;
	private float shootInterval = 1.5f; // seconds between shots
	private float detectionRange = 400f; // how far away to detect and shoot the player
	private Level level;
	
	public AnyaEnemy(float x, float y, Level level) {
		super(x, y, (int)(level.getLevelData().getTileSize()), (int)(level.getLevelData().getTileSize()), level);
		this.level = level;
		// Set hitbox to be slightly smaller than the sprite for better collision
		int offset = (int)(level.getLevelData().getTileSize() * 0.1);
		this.hitbox = new RectHitbox(this, offset, offset, width - offset, height - offset);
		this.image = GameResources.anya;
		
		// Don't move - disable gravity effects for stationary enemy
		movementVector.x = 0;
		movementVector.y = 0;
	}
	
	@Override
	public void update(float tslf) {
		// Anya doesn't move, but we still need to update hitbox position
		position.x = position.x;
		position.y = position.y;
		
		// Update shoot cooldown
		shootCooldown -= tslf;
	}
	
	@Override
	public void draw(Graphics g) {
		g.drawImage(image, (int)position.x, (int)position.y, width, height, null);
		hitbox.draw(g);
	}
	
	/**
	 * Check if Anya should shoot and return a projectile if so
	 * Returns null if not shooting this frame
	 */
	public Projectile tryShoot() {
		// Check if player is in range and cooldown has elapsed
		if (shootCooldown <= 0 && level.player != null) {
			float playerX = level.player.getX() + level.player.getWidth() / 2;
			float playerY = level.player.getY() + level.player.getHeight() / 2;
			float anyaX = position.x + width / 2;
			float anyaY = position.y + height / 2;
			
			float distX = playerX - anyaX;
			float distY = playerY - anyaY;
			float distance = (float) Math.sqrt(distX * distX + distY * distY);
			
			// If player is within detection range, shoot!
			if (distance < detectionRange && distance > 0) {
				shootCooldown = shootInterval;
				// Create projectile at Anya's position, aimed at player
				return new Projectile(anyaX, anyaY, distX, distY);
			}
		}
		
		return null;
	}
	
	/**
	 * Set the shoot interval (in seconds) - useful for difficulty tuning
	 */
	public void setShootInterval(float interval) {
		this.shootInterval = interval;
	}
	
	/**
	 * Set the detection range (in pixels) - useful for difficulty tuning
	 */
	public void setDetectionRange(float range) {
		this.detectionRange = range;
	}
}
