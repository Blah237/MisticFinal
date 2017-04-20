package edu.cornell.gdiac.mistic;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import edu.cornell.gdiac.GameCanvas;
import com.badlogic.gdx.graphics.*;

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

    /** constructor */
    public Minimap(float w, float h, int i, int j) {
        this.width = w;
        this.height = h;
        this.Dimensions = new Vector2(i,j);
        this.tileWidth = w/i;
        this.tileHeight = h/j;
        this.killMe = new int[i][j];
    }

    /** draw this minimap object */
    public void draw(GameCanvas canvas, TextureRegion background, float oX, float oY) {
        canvas.draw(background,Color.WHITE,oX,oY,width,height);
        for(int i=0; i<killMe.length; i++) {
            for(int j=killMe[i].length-1; j>=0; j--) {
                // draw rectangle of tileWidth and tileHeight at point
                // (oX + (tileWidth * i), oY + (tileHeight * j)), of varying
                // color depending on the int value at killMe[i][j]
                if (killMe[i][j]==1) {
                    Rectangle r = new Rectangle(oX+(tileWidth*i),oY+(tileHeight*j),tileWidth,tileHeight);
                    canvas.draw();
                } else if (killMe[i][j]==2) {

                } else if (killMe[i][j]==3) {

                }
            }
        }
    }
}
