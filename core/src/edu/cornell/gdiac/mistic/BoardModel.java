package edu.cornell.gdiac.mistic;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by tbkepler on 3/18/17.
 */
public class BoardModel {

    /**
     * Each tile on the board has a set of attributes associated with it.
     * Tiles cover the whole world
     */
    private static class Tile {
        /** Is this a maze wall block */
        public boolean isWall = false;
        /** Is this a lantern block */
        public boolean isLantern = false;
        /** Is this tile a fog spawn point */
        public boolean isFogSpawn = false;
        /** Is there a familiar here */
        public boolean hasFamiliar = false;
        /** Has this tile been visited (Only including for possible AI purposes) */
        public boolean visited = false;
        /** X and Y index of this tile from bottom left corner (in number of tiles) */
        public int x = 0;
        public int y = 0;
    }

    // Instance attributes
    /** The board width (in number of tiles) */
    private int width;
    /** The board height (in number of tiles) */
    private int height;
    /** The tile grid (with above dimensions) */
    private Tile[] tiles;
    /** Height and width of a single tile in relation to the world bounds */
    public float tileHeight = 0;
    public float tileWidth = 0;

    /**
     * Creates a new board of the given size
     *
     * @param width            Board width in tiles
     * @param height           Board height in tiles
     * @param screenDimensions Board width and height in screen dimensions
     */
    public BoardModel(int width, int height, Rectangle screenDimensions) {
        this.width = width;
        this.height = height;
        this.tileHeight = (screenDimensions.y/height);
        this.tileWidth = (screenDimensions.x/width);
        for (int ii = 0; ii < tiles.length; ii++) {
            tiles[ii] = new Tile();
        }
        resetTiles();
    }

    /**
     * Resets the values of all the tiles on screen, also indexes all tiles.
     */
    public void resetTiles() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Tile tile = getTile(x, y);
                tile.isWall = false;
                tile.isLantern = false;
                tile.isFogSpawn = false;
                tile.hasFamiliar = false;
                tile.visited = false;
                tile.x = x;
                tile.y = y;
            }
        }
    }

    /**
     * Returns the tile state for the given position (INTERNAL USE ONLY)
     *
     * Returns null if that position is out of bounds.
     *
     * @return the tile state for the given position
     */
    private Tile getTile(int x, int y) {
        if (!inBounds(x, y)) {
            return null;
        }
        return tiles[x * height + y];
    }

    /**
     * Returns true if the given position is a valid tile
     *
     * @param x The x index for the Tile cell
     * @param y The y index for the Tile cell
     *
     * @return true if the given position is a valid tile
     */
    public boolean inBounds(int x, int y) {
        return x >= 0 && y >= 0 && x < width && y < height;
    }

    /**
     * Returns the number of tiles horizontally across the board.
     *
     * @return the number of tiles horizontally across the board.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Returns the number of tiles vertically across the board.
     *
     * @return the number of tiles vertically across the board.
     */
    public int getHeight() {
        return height;
    }

    /**
     * Returns the tile height of this board's tiles
     *
     * @return height of one tile
     */
    public float getTileHeight() { return tileHeight; }

    /**
     * Returns the tile width of this board's tiles
     *
     * @return width of one tile
     */
    public float getTileWidth() { return tileWidth; }

    /**
     * Returns the board tile index for a screen position.
     *
     * @param f Screen position coordinate
     *
     * @return the board cell index for a screen position.
     */
    public int screenToBoardX(Vector2 position) {
        int intX = (int)(position.x/tileWidth);
        return intX;
    }

    /**
     * Returns the board tile index for a screen position.
     *
     * @param f Screen position coordinate
     *
     * @return the board cell index for a screen position.
     */
    public int screenToBoardY(Vector2 position) {
        int intY = (int)(position.y/tileHeight);
        return intY;
    }
}
