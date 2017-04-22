package edu.cornell.gdiac.mistic;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import edu.cornell.gdiac.GameCanvas;
import com.badlogic.gdx.graphics.*;

import java.awt.*;
import java.util.LinkedList;
import java.util.StringTokenizer;

/**
 * Created by tkepler on 4/19/17.
 */
public class Minimap {
    /** size variables */
    private float width;
    private float height;
    private float tileWidth;
    private float tileHeight;

    /** Texture for the minimap */
    private TextureRegion minimapTexture;

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

    public TextureRegion getTexture() {
        return minimapTexture;
    }

    public void setTexture(TextureRegion t) {
        this.minimapTexture = t;
    }

    /** constructor */
    public Minimap(float w, float h, int i, int j, TextureRegion t) {
        this.width = w;
        this.height = h;
        this.tileWidth = w/i*3;
        this.tileHeight = h/j*4;
        this.minimapTexture = t;
    }

    /** draw this minimap object */
    public void draw(GameCanvas canvas, float x, float y, float oX, float oY) {
            // draw rectangle of tileWidth and tileHeight at point
            // (oX + (tileWidth * p.x), oY + (tileHeight * p.y)), of varying
            // color depending on the int value at killMe[i][j]
            canvas.beginMinimapDraw(new Color(0x66e7d2ff),
                    oX+(tileWidth*(x/7.92f)),oY+(tileHeight*(y/5.94f)),
                    tileWidth,tileHeight);
    }
}
