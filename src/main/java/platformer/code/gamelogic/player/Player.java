package platformer.code.gamelogic.player;

import java.awt.Color;
import java.awt.Graphics;

import platformer.code.gameengine.PhysicsObject;
import platformer.code.gameengine.graphics.MyGraphics;
import platformer.code.gameengine.hitbox.RectHitbox;
import platformer.code.gamelogic.Main;
import platformer.code.gamelogic.level.Level;
import platformer.code.gamelogic.tiles.Tile;

public class Player extends PhysicsObject{
	public float walkSpeed = 400;
	public float jumpPower = 1350;

	private boolean isJumping = false;
	private boolean isInWater = false;
	private boolean isInGas = false;
	private boolean hasDoubleJump = false;
	private boolean doubleJumpUsed = false;
	private long doubleJumpPowerUpTime = 0;

	public Player(float x, float y, Level level) {
	
		super(x, y, level.getLevelData().getTileSize(), level.getLevelData().getTileSize(), level);
		int offset =(int)(level.getLevelData().getTileSize()*0.1); //hitbox is offset by 10% of the player size.
		this.hitbox = new RectHitbox(this, offset,offset, width -offset, height - offset);
	}

	@Override
	public void update(float tslf) {
		// Check if double jump power-up has expired
		if(hasDoubleJump && doubleJumpPowerUpTime != 0 && System.currentTimeMillis() - doubleJumpPowerUpTime > 10000) {
			hasDoubleJump = false;
			doubleJumpPowerUpTime = 0;
			doubleJumpUsed = false;
		}

		movementVector.x = 0;
		if(PlayerInput.isLeftKeyDown()) {
			movementVector.x = -walkSpeed;
		}
		if(PlayerInput.isRightKeyDown()) {
			movementVector.x = +walkSpeed;
		}

		// Handle jumping with double jump capability
		if(PlayerInput.isJumpKeyDown() && !isJumping) {
			movementVector.y = -jumpPower;
			isJumping = true;
		} else if(PlayerInput.isJumpKeyDown() && hasDoubleJump && !doubleJumpUsed && isJumping) {
			movementVector.y = -jumpPower;
			doubleJumpUsed = true;
		}

		// Apply buoyancy in water (reduced gravity)
		if(isInWater) {
			// Allow upward swim control in water (give player a gentle lift)
			if(PlayerInput.isJumpKeyDown()) {
				movementVector.y = -300;
			}
		}

		super.update(tslf);

		isJumping = true;
		if(collisionMatrix[BOT] != null) {
			isJumping = false;
			doubleJumpUsed = false;
		}
	}

	@Override
	public void draw(Graphics g) {
		g.setColor(Color.YELLOW);
		MyGraphics.fillRectWithOutline(g, (int)getX(), (int)getY(), width, height);
		
		if(Main.DEBUGGING) {
			for (int i = 0; i < closestMatrix.length; i++) {
				Tile t = closestMatrix[i];
				if(t != null) {
					g.setColor(Color.RED);
					g.drawRect((int)t.getX(), (int)t.getY(), t.getSize(), t.getSize());
				}
			}
		}
		
		hitbox.draw(g);
	}
	
	// Getters and setters for water/gas/power-up mechanics
	public void setInWater(boolean inWater) {
		this.isInWater = inWater;
	}
	
	public boolean isInWater() {
		return isInWater;
	}
	
	public void setInGas(boolean inGas) {
		this.isInGas = inGas;
	}
	
	public boolean isInGas() {
		return isInGas;
	}
	
	public void activateDoubleJump() {
		this.hasDoubleJump = true;
		this.doubleJumpUsed = false;
		this.doubleJumpPowerUpTime = System.currentTimeMillis();
	}
	
	public void deactivateDoubleJump() {
		this.hasDoubleJump = false;
		this.doubleJumpUsed = false;
		this.doubleJumpPowerUpTime = 0;
	}
	
	public boolean hasDoubleJumpPower() {
		return hasDoubleJump;
	}
}
