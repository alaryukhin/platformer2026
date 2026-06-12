package platformer.code.gamelogic.level;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import platformer.code.gameengine.PhysicsObject;
import platformer.code.gameengine.graphics.Camera;
import platformer.code.gameengine.loaders.Mapdata;
import platformer.code.gameengine.loaders.Tileset;
import platformer.code.gamelogic.GameResources;
import platformer.code.gamelogic.Main;
import platformer.code.gamelogic.enemies.AnyaEnemy;
import platformer.code.gamelogic.enemies.Enemy;
import platformer.code.gamelogic.enemies.Projectile;
import platformer.code.gamelogic.player.Player;
import platformer.code.gamelogic.tiledMap.Map;
import platformer.code.gamelogic.tiles.Flag;
import platformer.code.gamelogic.tiles.Flower;
import platformer.code.gamelogic.tiles.Gas;
import platformer.code.gamelogic.tiles.SolidTile;
import platformer.code.gamelogic.tiles.Spikes;
import platformer.code.gamelogic.tiles.Tile;
import platformer.code.gamelogic.tiles.Water;
import platformer.code.gamelogic.tiles.PowerUp;

public class Level {

	private LevelData leveldata;
	private Map map;
	private Enemy[] enemies;
	public static Player player;
	private Camera camera;

	private boolean active;
	private boolean playerDead;
	private boolean playerWin;

	private ArrayList<Enemy> enemiesList = new ArrayList<>();
	private ArrayList<AnyaEnemy> anyaEnemiesList = new ArrayList<>();
	private ArrayList<Flower> flowers = new ArrayList<>();
	private ArrayList<Projectile> projectiles = new ArrayList<>();

	private List<PlayerDieListener> dieListeners = new ArrayList<>();
	private List<PlayerWinListener> winListeners = new ArrayList<>();

	private Mapdata mapdata;
	private int width;
	private int height;
	private int tileSize;
	private Tileset tileset;
	public static float GRAVITY = 70;
	
	// Tracking variables for water and gas mechanics
	private long gasExposureStartTime = 0;
	private static final long GAS_DEATH_TIME = 5000; // 5 seconds

	public Level(LevelData leveldata) {
		this.leveldata = leveldata;
		mapdata = leveldata.getMapdata();
		width = mapdata.getWidth();
		height = mapdata.getHeight();
		tileSize = mapdata.getTileSize();
		restartLevel();
	}

	public LevelData getLevelData(){
		return leveldata;
	}

	public void restartLevel() {
		int[][] values = mapdata.getValues();
		Tile[][] tiles = new Tile[width][height];

		for (int x = 0; x < width; x++) {
			int xPosition = x;
			for (int y = 0; y < height; y++) {
				int yPosition = y;

				tileset = GameResources.tileset;

				tiles[x][y] = new Tile(xPosition, yPosition, tileSize, null, false, this);
				if (values[x][y] == 0)
					tiles[x][y] = new Tile(xPosition, yPosition, tileSize, null, false, this); // Air
				else if (values[x][y] == 1)
					tiles[x][y] = new SolidTile(xPosition, yPosition, tileSize, tileset.getImage("Solid"), this);

				else if (values[x][y] == 2)
					tiles[x][y] = new Spikes(xPosition, yPosition, tileSize, Spikes.HORIZONTAL_DOWNWARDS, this);
				else if (values[x][y] == 3)
					tiles[x][y] = new Spikes(xPosition, yPosition, tileSize, Spikes.HORIZONTAL_UPWARDS, this);
				else if (values[x][y] == 4)
					tiles[x][y] = new Spikes(xPosition, yPosition, tileSize, Spikes.VERTICAL_LEFTWARDS, this);
				else if (values[x][y] == 5)
					tiles[x][y] = new Spikes(xPosition, yPosition, tileSize, Spikes.VERTICAL_RIGHTWARDS, this);
				else if (values[x][y] == 6)
					tiles[x][y] = new SolidTile(xPosition, yPosition, tileSize, tileset.getImage("Dirt"), this);
				else if (values[x][y] == 7)
					tiles[x][y] = new SolidTile(xPosition, yPosition, tileSize, tileset.getImage("Grass"), this);
				else if (values[x][y] == 8)
					enemiesList.add(new Enemy(xPosition*tileSize, yPosition*tileSize, this)); // TODO: objects vs tiles
				else if (values[x][y] == 22)
					anyaEnemiesList.add(new AnyaEnemy(xPosition*tileSize, yPosition*tileSize, this)); // Anya shooting enemy
				else if (values[x][y] == 9)
					tiles[x][y] = new Flag(xPosition, yPosition, tileSize, tileset.getImage("Flag"), this);
				else if (values[x][y] == 10) {
					tiles[x][y] = new Flower(xPosition, yPosition, tileSize, tileset.getImage("Flower1"), this, 1);
					flowers.add((Flower) tiles[x][y]);
				} else if (values[x][y] == 11) {
					tiles[x][y] = new Flower(xPosition, yPosition, tileSize, tileset.getImage("Flower2"), this, 2);
					flowers.add((Flower) tiles[x][y]);
				} else if (values[x][y] == 12)
					tiles[x][y] = new SolidTile(xPosition, yPosition, tileSize, tileset.getImage("Solid_down"), this);
				else if (values[x][y] == 13)
					tiles[x][y] = new SolidTile(xPosition, yPosition, tileSize, tileset.getImage("Solid_up"), this);
				else if (values[x][y] == 14)
					tiles[x][y] = new SolidTile(xPosition, yPosition, tileSize, tileset.getImage("Solid_middle"), this);
				else if (values[x][y] == 15)
					tiles[x][y] = new Gas(xPosition, yPosition, tileSize, tileset.getImage("GasOne"), this, 1);
				else if (values[x][y] == 16)
					tiles[x][y] = new Gas(xPosition, yPosition, tileSize, tileset.getImage("GasTwo"), this, 2);
				else if (values[x][y] == 17)
					tiles[x][y] = new Gas(xPosition, yPosition, tileSize, tileset.getImage("GasThree"), this, 3);
				else if (values[x][y] == 18)
					tiles[x][y] = new Water(xPosition, yPosition, tileSize, tileset.getImage("Falling_water"), this, 0);
				else if (values[x][y] == 19)
					tiles[x][y] = new Water(xPosition, yPosition, tileSize, tileset.getImage("Full_water"), this, 3);
				else if (values[x][y] == 20)
					tiles[x][y] = new Water(xPosition, yPosition, tileSize, tileset.getImage("Half_water"), this, 2);
				else if (values[x][y] == 21)
					tiles[x][y] = new Water(xPosition, yPosition, tileSize, tileset.getImage("Quarter_water"), this, 1);

				else if (values[x][y] == 23){
					tiles[x][y] = new PowerUp(xPosition, yPosition, tileSize, tileset.getImage("Flower1"), this, 1);
				}
			}

		}
		enemies = new Enemy[enemiesList.size()];
		map = new Map(width, height, tileSize, tiles);
		camera = new Camera(Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT, 0, map.getFullWidth(), map.getFullHeight());
		for (int i = 0; i < enemiesList.size(); i++) {
			enemies[i] = new Enemy(enemiesList.get(i).getX(), enemiesList.get(i).getY(), this);
		}
		player = new Player(leveldata.getPlayerX() * map.getTileSize(), leveldata.getPlayerY() * map.getTileSize(),
				this);
		camera.setFocusedObject(player);

		active = true;
		playerDead = false;
		playerWin = false;
		gasExposureStartTime = 0;
	}

	public void onPlayerDeath() {
		active = false;
		playerDead = true;
		throwPlayerDieEvent();
	}

	public void onPlayerWin() {
		active = false;
		playerWin = true;
		throwPlayerWinEvent();
	}

	public void update(float tslf) {
		if (active) {
			// Update the player
			player.update(tslf);

			// Player death
			if (map.getFullHeight() + 100 < player.getY())
				onPlayerDeath();
			if (player.getCollisionMatrix()[PhysicsObject.BOT] instanceof Spikes)
				onPlayerDeath();
			if (player.getCollisionMatrix()[PhysicsObject.TOP] instanceof Spikes)
				onPlayerDeath();
			if (player.getCollisionMatrix()[PhysicsObject.LEF] instanceof Spikes)
				onPlayerDeath();
			if (player.getCollisionMatrix()[PhysicsObject.RIG] instanceof Spikes)
				onPlayerDeath();
			
			// ========== WATER AND GAS MECHANICS ==========
			boolean inWater = false;
			boolean inGas = false;
			int tileSize = map.getTileSize();
			int minX = Math.max(0, (int) (player.getHitbox().getX() / tileSize) - 1);
			int maxX = Math.min(map.getWidth() - 1, (int) ((player.getHitbox().getX() + player.getHitbox().getWidth()) / tileSize) + 1);
			int minY = Math.max(0, (int) (player.getHitbox().getY() / tileSize) - 1);
			int maxY = Math.min(map.getHeight() - 1, (int) ((player.getHitbox().getY() + player.getHitbox().getHeight()) / tileSize) + 1);

			for (int x = minX; x <= maxX; x++) {
				for (int y = minY; y <= maxY; y++) {
					Tile tile = map.getTiles()[x][y];
					if (tile == null)
						continue;
					if (tile.getHitbox() == null)
						continue;
					if (tile.getHitbox().isIntersecting(player.getHitbox())) {
						if (tile instanceof Water) {
							inWater = true;
						}
						if (tile instanceof Gas) {
							inGas = true;
						}
					}
					if (inWater && inGas)
						break;
				}
				if (inWater && inGas)
					break;
			}

			player.setInWater(inWater);
			
			
			
			// Track gas exposure time
			if(inGas) {
				if(gasExposureStartTime == 0) {
					gasExposureStartTime = System.currentTimeMillis();
				}
				if (System.currentTimeMillis() - gasExposureStartTime > GAS_DEATH_TIME) {
					onPlayerDeath();
				}
			} else {
				gasExposureStartTime = 0;
			}
			player.setInGas(inGas);

			for (int i = 0; i < flowers.size(); i++) {
				if (flowers.get(i).getHitbox().isIntersecting(player.getHitbox())) {
					if(flowers.get(i).getType() == 1)
						water(flowers.get(i).getCol(), flowers.get(i).getRow(), map, 3);
					else
						addGas(flowers.get(i).getCol(), flowers.get(i).getRow(), map, 20, new ArrayList<Gas>());
					flowers.remove(i);
					i--;
				}
			}
			
			// ========== POWER-UP MECHANIC ==========
			// Check for power-up collisions
			Tile[][] tiles = map.getTiles();
			for(int x = 0; x < tiles.length; x++) {
				for(int y = 0; y < tiles[0].length; y++) {
					Tile tile = tiles[x][y];
					if(tile instanceof PowerUp) {
						if(tile.getHitbox().isIntersecting(player.getHitbox())) {
							PowerUp powerUp = (PowerUp) tile;
							if(powerUp.getPowerType() == 1) {
								player.activateDoubleJump();
							}
							// Remove the power-up tile
							map.addTile(x, y, new Tile(x, y, map.getTileSize(), null, false, this));
						}
					}
				}
			}

			// Update the enemies
			for (int i = 0; i < enemies.length; i++) {
				enemies[i].update(tslf);
				if (player.getHitbox().isIntersecting(enemies[i].getHitbox())) {
					onPlayerDeath();
				}
			}
			
			// Update Anya enemies and handle their projectiles
			for (int i = 0; i < anyaEnemiesList.size(); i++) {
				AnyaEnemy anya = anyaEnemiesList.get(i);
				anya.update(tslf);
				
				// Check if player collides with Anya
				if (player.getHitbox().isIntersecting(anya.getHitbox())) {
					onPlayerDeath();
				}
				
				// Try to shoot a projectile
				Projectile projectile = anya.tryShoot();
				if (projectile != null) {
					projectiles.add(projectile);
				}
			}
			
			// Update projectiles and check for collisions
			for (int i = 0; i < projectiles.size(); i++) {
				Projectile projectile = projectiles.get(i);
				projectile.update(tslf);
				
				// Check if projectile hit the player
				if (projectile.getHitbox().isIntersecting(player.getHitbox())) {
					onPlayerDeath();
					projectiles.remove(i);
					i--;
					continue;
				}
				
				// Remove projectiles that go off screen
				if (projectile.shouldRemove(map.getFullWidth(), map.getFullHeight())) {
					projectiles.remove(i);
					i--;
				}
			}

			// Update the map
			map.update(tslf);

			// Update the camera
			camera.update(tslf);
		}
	}
	
	
	//#############################################################################################################
	//Your code goes here! 
	//Please make sure you read the rubric/directions carefully and implement the solution recursively!
	private void water(int col, int row, Map map, int fullness) {
		Tile[][] tiles = map.getTiles();
		int width = tiles.length;
		int height = tiles[0].length;

		if (col < 0 || col >= width || row < 0 || row >= height)
			return;

		Tile current = tiles[col][row];
		if (current != null && current.isSolid())
			return;

		String imageName;
		switch (fullness) {
		case 3:
			imageName = "Full_water";
			break;
		case 2:
			imageName = "Half_water";
			break;
		case 1:
			imageName = "Quarter_water";
			break;
		default:
			imageName = "Falling_water";
		}

		Water w = new Water(col, row, tileSize, tileset.getImage(imageName), this, fullness);
		map.addTile(col, row, w);

		// Check if we can flow down
		boolean canFlowDown = false;
		if (row + 1 < height) {
			Tile below = tiles[col][row + 1];
			if (below != null && !below.isSolid()) {
				//if block below is empty and the block two below is solid, we can flow down but we should also make sure to fill the current block fully instead of flowing down with falling water
				if(row+2 < height && tiles[col][row+2].isSolid()) {
					water(col, row + 1, map, 3);
				}
				else{
					water(col, row + 1, map, 0);
				}
				return;
			}
		}

		// Check if we're at the bottom of the map
		if (row == height - 1) {
			// At bottom - don't flow left or right, just make full water block
			return;
		}

		int nextFullness = fullness > 1 ? fullness - 1 : 1;

		if (col - 1 >= 0) {
			Tile left = tiles[col - 1][row];
			boolean leftCanFill = left != null && !left.isSolid();
			if (leftCanFill) {
				if (left instanceof Water) {
					leftCanFill = ((Water) left).getFullness() < nextFullness;
				}
				if (leftCanFill)
					water(col - 1, row, map, nextFullness);
			}
		}

		if (col + 1 < width) {
			Tile right = tiles[col + 1][row];
			boolean rightCanFill = right != null && !right.isSolid();
			if (rightCanFill) {
				if (right instanceof Water) {
					rightCanFill = ((Water) right).getFullness() < nextFullness;
				}
				if (rightCanFill)
					water(col + 1, row, map, nextFullness);
			}
		}
	}


	public void draw(Graphics g) {
		g.translate((int) -camera.getX(), (int) -camera.getY());

		// Draw the map
		for (int x = 0; x < map.getWidth(); x++) {
			for (int y = 0; y < map.getHeight(); y++) {
				Tile tile = map.getTiles()[x][y];
				if (tile == null)
					continue;

				if (tile instanceof Gas) {
					Gas gas = (Gas) tile;
					int neighbourGasCount = 0;
					for (int i = -1; i <= 1; i++) {
						for (int j = -1; j <= 1; j++) {
							int nx = x + i;
							int ny = y + j;
							if (nx >= 0 && nx < map.getWidth() && ny >= 0 && ny < map.getHeight()) {
								Tile n = map.getTiles()[nx][ny];
								if (n instanceof Gas)
									neighbourGasCount++;
							}
						}
					}

					// choose image/intensity based on how many gas tiles are nearby (including self)
					if (neighbourGasCount >= 5) {
						gas.setIntensity(3);
						tile.setImage(tileset.getImage("GasThree"));
					} else if (neighbourGasCount >= 2) {
						gas.setIntensity(2);
						tile.setImage(tileset.getImage("GasTwo"));
					} else {
						gas.setIntensity(1);
						tile.setImage(tileset.getImage("GasOne"));
					}
				}

				if (camera.isVisibleOnCamera(tile.getX(), tile.getY(), tile.getSize(), tile.getSize()))
					tile.draw(g);
			}
		}

		// Draw the enemies
		for (int i = 0; i < enemies.length; i++) {
			enemies[i].draw(g);
		}
		
		// Draw Anya enemies
		for (int i = 0; i < anyaEnemiesList.size(); i++) {
			anyaEnemiesList.get(i).draw(g);
		}
		
		// Draw projectiles
		for (int i = 0; i < projectiles.size(); i++) {
			projectiles.get(i).draw(g);
		}

		// Draw the player
		player.draw(g);

		// used for debugging
		if (Camera.SHOW_CAMERA)
			camera.draw(g);

		g.translate((int) +camera.getX(), (int) +camera.getY());
	}

	// Adds gas tiles until the number of squares are filled or there is no more room
	// Gas expands into all adjacent cells in a fixed order: up, up-left, up-right, left, right, down-left, down-right, down
	private void addGas(int col, int row, Map map, int numSquaresToFill, ArrayList<Gas> placedThisRound) {
        int count = 1; // starting tile counts toward the fill target
        Gas start = new Gas(col, row, tileSize, tileset.getImage("GasOne"), this, 1);
        map.addTile(col, row, start);
        placedThisRound.add(start);
        int i = 0;
        
        int[][] offsets = {
            {0, -1},  // up
            {-1, -1}, // up-left
            {1, -1},  // up-right
            {-1, 0},  // left
            {1, 0},   // right
            {-1, 1},  // down-left
            {1, 1},   // down-right
            {0, 1}    // down
        };
        
        while (i < placedThisRound.size() && count < numSquaresToFill) {
            Gas cur = placedThisRound.get(i);
            int c = cur.getCol();
            int r = cur.getRow();
            
            for (int[] offset : offsets) {
                if (count >= numSquaresToFill) {
                    break;
                }
                int nextC = c + offset[0];
                int nextR = r + offset[1];
                if (nextC < 0 || nextR < 0 || nextC >= map.getTiles().length || nextR >= map.getTiles()[nextC].length) {
                    continue;
                }
                Tile nextTile = map.getTiles()[nextC][nextR];
                if (nextTile == null || nextTile.isSolid() || nextTile instanceof Gas || !nextTile.getClass().equals(Tile.class)) {
                    continue;
                }
                Gas newG = new Gas(nextC, nextR, tileSize, tileset.getImage("GasOne"), this, 1);
                map.addTile(nextC, nextR, newG);
                placedThisRound.add(newG);
                count++;
            }
            i++;
        }
    }

	public void throwPlayerDieEvent() {
		for (PlayerDieListener playerDieListener : dieListeners) {
			playerDieListener.onPlayerDeath();
		}
	}

	public void addPlayerDieListener(PlayerDieListener listener) {
		dieListeners.add(listener);
	}

	// ------------------------Win-Listener
	public void throwPlayerWinEvent() {
		for (PlayerWinListener playerWinListener : winListeners) {
			playerWinListener.onPlayerWin();
		}
	}

	public void addPlayerWinListener(PlayerWinListener listener) {
		winListeners.add(listener);
	}

	// ---------------------------------------------------------Getters
	public boolean isActive() {
		return active;
	}

	public boolean isPlayerDead() {
		return playerDead;
	}

	public boolean isPlayerWin() {
		return playerWin;
	}

	public Map getMap() {
		return map;
	}

	public Player getPlayer() {
		return player;
	}
}