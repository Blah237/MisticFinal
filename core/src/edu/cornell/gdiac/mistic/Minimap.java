package edu.cornell.gdiac.mistic;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import edu.cornell.gdiac.GameCanvas;
import com.badlogic.gdx.graphics.*;

import java.awt.*;
import java.util.LinkedList;

/**
 * Created by tkepler on 4/19/17.
 */
public class Minimap {
    /** size variables */
    private float width;
    private float height;
    private Vector2 Dimensions;
    private float tileWidth;
    private float tileHeight;

    /** data for drawing, wall=1, lantern=2, gorf=3 */
    public int[][] killMe;
    /** point data for walls */
    private LinkedList<Point> walls = new LinkedList<Point>();
    /** point data for lanterns */
    private LinkedList<Point> lanterns = new LinkedList<Point>();
    /** */

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public float getTileWidth() {
        return tileWidth;
    }

    public float getTileHeight() {
        return tileHeight;
    }

    /** constructor */
    public Minimap(float w, float h, int i, int j) {
        this.width = w;
        this.height = h;
        this.Dimensions = new Vector2(i,j);
        this.tileWidth = w/i;
        this.tileHeight = h/j;
        this.killMe = new int[i][j];
    }

    public void makeWallData() {
        for(int i=0; i<killMe.length; i++) {
            for(int j=killMe[i].length-1; j>=0; j--) {
                // add rectangle of tileWidth and tileHeight at point
                // (oX + (tileWidth * i), oY + (tileHeight * j)), of varying
                // color depending on the int value at killMe[i][j]
                if (killMe[i][j]==1) {
                    walls.add(new Point(i,j));
                } else if (killMe[i][j]==2) {
                    lanterns.add(new Point(i,j));
                } else if (killMe[i][j]==3) {
                    // do some shit here
                }
            }
        }
    }

    /** draw this minimap object */
    public void draw(GameCanvas canvas, TextureRegion background, float x, float y, float oX, float oY) {
            // draw rectangle of tileWidth and tileHeight at point
            // (oX + (tileWidth * p.x), oY + (tileHeight * p.y)), of varying
            // color depending on the int value at killMe[i][j]
            canvas.beginMinimapDraw(Color.WHITE,oX+(tileWidth*x),oY+(tileHeight*y),tileWidth*4,tileHeight*4);
    }
}
