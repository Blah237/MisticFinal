package edu.cornell.gdiac.mistic;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import edu.cornell.gdiac.obstacle.BoxObstacle;

import java.util.ArrayList;

import static com.badlogic.gdx.utils.JsonValue.ValueType.object;

/**
 * Created by beau on 3/20/17.
 *
 */
public class Familiar {

    Vector2 scale;
    Vector2 position;
    boolean collected;
    BoxObstacle object;
    TextureRegion tex;



    private static final float DENSITY = 1.0f;
    private static final float FRICTION  = 0.3f;
    private static final float RESTITUTION = 0.1f;

    public Familiar(float x, float y, TextureRegion texture, Vector2 scale){
        this.scale=scale;
        this.position=new Vector2(x,y);
        this.tex=texture;
        this.object = new BoxObstacle(x,y,tex.getRegionWidth()/(scale.x),tex.getRegionHeight()/(scale.y));
        object.setDensity(DENSITY);
        object.setFriction(FRICTION);
        object.setRestitution(RESTITUTION);
        object.setBodyType(BodyDef.BodyType.StaticBody);
        object.setName("familiar");
        object.setDrawScale(scale);
    }



    public void setTexture(TextureRegion tex){
        this.object.setTexture(tex);
    }

    public void setCollected(boolean bool) {
        this.collected=bool;
    }

    public float getX(){
        return this.position.x;
    }

    public float getY(){
        return this.position.y;
    }
}
