package edu.cornell.gdiac.mistic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by tbkepler on 3/18/17.
 */
public class BoardModel {

    /**
     * Each tile on the board has a set of attributes associated with it.
     * Tiles cover the whole world
     */
    public static class Tile {
        /** Is this a maze wall block */
        public boolean isWall;
        /** Is this a lantern block */
        public boolean isLantern;
        /** Is this tile a fog spawn point */
        public boolean isFogSpawn;
        /** Is this tile a fog spawn point */
        public boolean isFog;
        /** Is there a familiar here */
        public boolean hasFamiliar;
        /** Has this tile been visited (Only including for possible AI purposes) */
        public boolean visited;
        /** Has this tile been set as a goal*/
        public boolean goal;
        /** X and Y index of this tile from bottom left corner (in number of tiles) */
        public int y;
        public int x;
        /**The X and Y coordinates of the bottom left corner on the screen*/
        public float fy;
        public float fx;

        public Tile(){
            this.isWall=false;
            this.isLantern=false;
            this.isFogSpawn=false;
            this.isFog=false;
            this.hasFamiliar=false;
        }
    }

    // Instance attributes
    /** The board width (in number of tiles) */
    private int width;
    /** The board height (in number of tiles) */
    private int height;
    /** The tile grid (with above dimensions) */
    public Tile[][] tiles;
    /** Height and width of a single tile in relation to the world bounds */
    private float tileHeight;
    private float tileWidth;

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
        this.tileHeight = (screenDimensions.height/height);
        this.tileWidth = (screenDimensions.width/width);
        this.tiles = new Tile[width][height];
       // System.out.println("Canvas Size: "+ screenDimensions.width + ", "+screenDimensions.height+ ". Tile width: "+tileWidth + ", "+tileHeight);
        for (int i = 0; i < width; i++) {
            for(int j=0;j<height;j++){
                Tile t=new Tile();
                t.y=j;
                t.x=i;
                t.fx=i*tileWidth;
                t.fy=j*tileHeight;
                tiles[i][j]=t;
                //System.out.println("Tile: "+t.x+", "+t.y+". Pixel: "+t.fx + ", "+t.fy);
            }
        }
    }

    /**
     * Resets the values of all the tiles on screen, also indexes all tiles.
     */
    public void resetTiles() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Tile tile = getTile(x, y);
                tile.x = x;
                tile.y = y;
                tile.isWall=false;
                tile.isLantern=false;
                tile.isFogSpawn=false;
                tile.isFog=false;
                tile.hasFamiliar=false;
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
        return tiles[x][y];
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
     * Returns x coordinate of the center of this tile object
     *
     * @param tile this tile object to find center of
     * @return     X coordinate of tile center
     */
    public float getTileCenterX(Tile tile) {

        return tile.fx+(tileWidth/2);
    }

    /**
     * Returns y coordinate of the center of this tile object
     *
     * @param tile this tile object to find center of
     * @return     Y coordinate of tile center
     */
    public float getTileCenterY(Tile tile) {

        return tile.fy+(tileHeight/2);
    }

    /**
     * Returns the board tile index for a screen position.
     *
     * @param x Screen position x
     *
     * @return the board cell index for a screen position.
     */
    public int screenToBoardX(float x) {
        int intX = (int)(x/tileWidth);
        return intX;
    }

    /**
     * Returns the board tile index for a screen position.
     *
     * @param y Screen position y
     *
     * @return the board cell index for a screen position.
     */
    public int screenToBoardY(float y) {
        int intY = (int)(y/tileHeight);
        return intY;
    }

    /**
     * Returns true if the tile is a goal.
     *
     * A tile position that is not on the board will always evaluate to false.
     *
     * @param x The x index for the Tile cell
     * @param y The y index for the Tile cell
     *
     * @return true if the tile is a goal.
     */
    public boolean isGoal(int x, int y) {
        if (!inBounds(x, y)) {
            return false;
        }

        return getTile(x, y).goal;
    }

    /**
     * Returns true if the tile is a fog.
     *
     * A tile position that is not on the board will always evaluate to false.
     *
     * @param x The x index for the Tile cell
     * @param y The y index for the Tile cell
     *
     * @return true if the tile is a goal.
     */
    public boolean isFog(int x, int y) {
        if (!inBounds(x, y)) {
            return false;
        }

        return getTile(x, y).isFog;
    }

    public boolean isWall(int x, int y) {
        if (!inBounds(x, y)) {
            return false;
        }

        return getTile(x, y).isWall;
    }

    /**
     * Returns true if the tile is a fog.
     *
     * A tile position that is not on the board will always evaluate to false.
     *
     * @param x The x index for the Tile cell
     * @param y The y index for the Tile cell
     *
     * @return true if the tile is a goal.
     */
    public void setFog(int x, int y) {
        if (!inBounds(x,y)) {
            Gdx.app.error("Board", "Illegal tile "+x+","+y, new IndexOutOfBoundsException());
            return;
        }
        getTile(x, y).isFog = true;
    }


    /**
     * Marks a tile as a goal.
     *
     * A marked tile will return true for isGoal(), until a call to clearMarks().
     *
     * @param x The x index for the Tile cell
     * @param y The y index for the Tile cell
     */
    public void setGoal(int x, int y) {
        if (!inBounds(x,y)) {
            Gdx.app.error("Board", "Illegal tile "+x+","+y, new IndexOutOfBoundsException());
            return;
        }
        getTile(x, y).goal = true;
    }

    /**
     * Marks a tile as visited.
     *
     * A marked tile will return true for isVisited(), until a call to clearMarks().
     *
     * @param x The x index for the Tile cell
     * @param y The y index for the Tile cell
     */
    public void setVisited(int x, int y) {
        if (!inBounds(x,y)) {
            Gdx.app.error("Board", "Illegal tile "+x+","+y, new IndexOutOfBoundsException());
            return;
        }
        getTile(x, y).visited = true;
    }

    /**
     * Returns true if the tile has been visited.
     *
     * A tile position that is not on the board will always evaluate to false.
     *
     * @param x The x index for the Tile cell
     * @param y The y index for the Tile cell
     *
     * @return true if the tile has been visited.
     */
    public boolean isVisited(int x, int y) {
        if (!inBounds(x, y)) {
            return false;
        }

        return getTile(x, y).visited;
    }

    /**
     * Returns true if a tile location is safe (i.e. there is a tile there)
     *
     * @param x The x index for the Tile cell
     * @param y The y index for the Tile cell
     *
     * @return true if a screen location is safe
     */
    public boolean isSafeAt(int x, int y) {
        Tile the_tile = getTile(x, y);
        return x >= 0 && y >= 0 && x < width && y < height
                && !(the_tile.isWall);
    }


    /**
     * Clears all marks on the board.
     *
     * This method should be done at the beginning of any pathfinding round.
     */
    public void clearMarks() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Tile state = getTile(x, y);
                state.visited = false;
                state.goal = false;
            }
        }
    }
}