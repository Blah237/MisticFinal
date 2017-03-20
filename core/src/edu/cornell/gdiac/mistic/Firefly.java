package edu.cornell.gdiac.mistic;

import com.badlogic.gdx.math.Vector2;

/**
 * This is a class to store the important information for a single firefly.
 * The class is lightweight, and mainly getters and setters for location and deletion status.
 * Logic and memory management is in Firefly Controller.
 */
public class Firefly {
    private static final float FIREFLY_DENSITY = 1.0f;
    private static final float FIREFLY_FRICTION  = 0.3f;
    private static final float FIREFLY_RESTITUTION = 0.1f;
    private Vector2 position;
    /** If the firefly should be scheduled for deletion*/
    private boolean collected;

    public Firefly() {
        position = new Vector2();
        collected= false;
    }

    public void setPosition(float x, float y){
        this.position.set(x,y);
    }

    public Vector2 getPosition(){
        return this.position;
    }

    public float getX(){return position.x;}

    public float getY(){return position.y;}

    public void setX(float v){this.position.x = v;}

    public void setY(float v){this.position.y = v;}

    public boolean isCollected(){return this.collected;}

    public void collect(){this.collected=true;}

}
