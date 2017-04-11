package edu.cornell.gdiac.mistic;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import edu.cornell.gdiac.obstacle.BoxObstacle;

/**
 * This is a class to store the important information for a single firefly.
 * The class is lightweight, and mainly getters and setters for location and deletion status.
 * Logic and memory management is in Firefly Controller.
 */
public class Firefly{
    private Vector2 position;
    /** If the firefly should be scheduled for deletion*/
    private boolean destroyed;
    private BoxObstacle object;

    private static final float FIREFLY_DENSITY = 1.0f;
    private static final float FIREFLY_FRICTION  = 0.3f;
    private static final float FIREFLY_RESTITUTION = 0.1f;
    private Vector2 scale = new Vector2(1f,1f);

    public Firefly(float x, float y, TextureRegion texture) {
        position = new Vector2(x,y);
        destroyed= false;
        object = new BoxObstacle(x,y,texture.getRegionWidth()/scale.x,texture.getRegionHeight()/scale.y);
        object.setDensity(FIREFLY_DENSITY);
        object.setFriction(FIREFLY_FRICTION);
        object.setRestitution(FIREFLY_RESTITUTION);
        object.setName("firefly"+x+y);
        object.setDrawScale(scale);
        object.setTexture(texture);
    }


    public void setPosition(float x, float y){
        this.position.set(x,y);
    }

    public BoxObstacle getObject(){return this.object;}

    public Vector2 getPosition(){
        return this.position;
    }


    public float getX(){return position.x;}

    public float getY(){return position.y;}

    public void setX(float v){this.position.x = v;}

    public void setY(float v){this.position.y = v;}

    public boolean isDestroyed(){return this.destroyed;}

    public void setDestroyed(boolean b){this.destroyed=b;}

}
