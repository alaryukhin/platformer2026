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
import platformer.code.gamelogic.enemies.Enemy;
import platformer.code.gamelogic.player.Player;
import platformer.code.gamelogic.tiledMap.Map;
import platformer.code.gamelogic.tiles.Flag;
import platformer.code.gamelogic.tiles.Flower;
import platformer.code.gamelogic.tiles.Gas;
import platformer.code.gamelogic.tiles.SolidTile;
import platformer.code.gamelogic.tiles.Spikes;
import platformer.code.gamelogic.tiles.Tile;
import platformer.code.gamelogic.tiles.Water;

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
	private ArrayList<Flower> flowers = new ArrayList<>();

	private List<PlayerDieListener> dieListeners = new ArrayList<>();
	private List<PlayerWinListener> winListeners = new ArrayList<>();

	private Mapdata mapdata;
	private int width;
	private int height;
	private int tileSize;
	private Tileset tileset;
	public static float GRAVITY = 70;

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
				else if (values[x][y] == 22)
					tiles[x][y] = new SolidTile(xPosition, yPosition, tileSize, tileset.getImage("Anya"), this);
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

			for (int i = 0; i < flowers.size(); i++) {
				if (flowers.get(i).getHitbox().isIntersecting(player.getHitbox())) {
					if(flowers.get(i).getType() == 1)
						water(flowers.get(i).getCol(), flowers.get(i).getRow(), map, 3);
//					else
//						addGas(flowers.get(i).getCol(), flowers.get(i).getRow(), map, 20, new ArrayList<Gas>());
					flowers.remove(i);
					i--;
				}
			}

			// Update the enemies
			for (int i = 0; i < enemies.length; i++) {
				enemies[i].update(tslf);
				if (player.getHitbox().isIntersecting(enemies[i].getHitbox())) {
					onPlayerDeath();
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

		if (current instanceof Water) {
			Water existing = (Water) current;
			if (existing.getFullness() >= fullness)
				return;
		}

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
			if (below != null && !below.isSolid() && !(below instanceof Water)) {
				canFlowDown = true;
			}
		}

		if (canFlowDown) {
			// Flow down with same fullness
			water(col, row + 1, map, fullness);
			return;
		}

		// Check if we're at the bottom of the map
		if (row == height - 1) {
			// At bottom - don't flow left or right, just make full water block
			return;
		}

		// We hit a solid or water below, so make this a full water block and flow left/right
		if (fullness != 3) {
			w.setIntensity(3);
			w.setImage(tileset.getImage("Full_water"));
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

		// Draw the player
		player.draw(g);

		// used for debugging
		if (Camera.SHOW_CAMERA)
			camera.draw(g);

		g.translate((int) +camera.getX(), (int) +camera.getY());
	}

	//Adds gas tiles until the requisite number of squares are filled or there is no more room 
	private void addGas(int col, int row, Map map, int numSquaresToFill, ArrayList<Gas> placedThisRound) {
		Tile[][] tiles = map.getTiles();
		int w = map.getWidth();
		int h = map.getHeight();

		if (numSquaresToFill <= 0)
			return;

		ArrayList<Gas> frontier = new ArrayList<>();

		// try to place initial gas at origin
		if (col >= 0 && col < w && row >= 0 && row < h) {
			Tile cur = tiles[col][row];
			boolean canPlace = (cur == null) || (!cur.isSolid() && !(cur instanceof Water) && !(cur instanceof Gas));
			if (canPlace && numSquaresToFill > 0) {
				Gas g = new Gas(col, row, tileSize, tileset.getImage("GasOne"), this, 0);
				map.addTile(col, row, g);
				tiles[col][row] = g;
				placedThisRound.add(g);
				frontier.add(g);
				numSquaresToFill--;
			}
		}

		// iteratively expand while we still need to place tiles
		while (numSquaresToFill > 0 && !frontier.isEmpty()) {
			ArrayList<Gas> next = new ArrayList<>();
			for (Gas origin : frontier) {
				int c = origin.getCol();
				int r = origin.getRow();
				int[][] dirs = { {0, -1}, {-1, 0}, {1, 0}, {0, 1} }; // up, left, right, down
				for (int[] d : dirs) {
					int nc = c + d[0];
					int nr = r + d[1];
					if (nc < 0 || nc >= w || nr < 0 || nr >= h)
						continue;
					Tile t = tiles[nc][nr];
					if (t != null && (t.isSolid() || t instanceof Water || t instanceof Gas))
						continue;

					// place gas here
					Gas g = new Gas(nc, nr, tileSize, tileset.getImage("GasOne"), this, 0);
					map.addTile(nc, nr, g);
					tiles[nc][nr] = g;
					placedThisRound.add(g);
					next.add(g);
					numSquaresToFill--;
					if (numSquaresToFill <= 0)
						break;
				}
				if (numSquaresToFill <= 0)
					break;
			}
			if (next.isEmpty())
				break; // no more room to expand
			frontier = next;
		}
	}

	// --------------------------Die-Listener
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