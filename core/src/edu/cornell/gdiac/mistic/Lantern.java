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
public class Lantern {

    Vector2 scale;
    Vector2 position;
    boolean lit;
    BoxObstacle object;
    TextureRegion litTex;
    TextureRegion unlitTex;



    private static final float LAMP_DENSITY = 1.0f;
    private static final float LAMP_FRICTION  = 0.3f;
    private static final float LAMP_RESTITUTION = 0.1f;

    public Lantern(float x, float y, TextureRegion unlitTexture, TextureRegion litTexture, Vector2 scale){
        this.scale=scale;
        this.position=new Vector2(x,y);
        this.lit=false;
        this.unlitTex=unlitTexture;
        this.litTex=unlitTexture;
        this.object = new BoxObstacle(x,y,unlitTexture.getRegionWidth()/scale.x,unlitTexture.getRegionHeight()/scale.y);
        object.setDensity(LAMP_DENSITY);
        object.setFriction(LAMP_FRICTION);
        object.setRestitution(LAMP_RESTITUTION);
        object.setBodyType(BodyDef.BodyType.StaticBody);
        object.setName("lantern"+x+y);
        object.setDrawScale(scale);
    }

    public void setTexture(TextureRegion tex){
        this.object.setTexture(tex);
    }

    public void toggleLantern() {
        if (lit) {
            lit = false;
        } else {
            lit = true;
        }
    }

    public float getX(){
        return this.position.x;
    }

    public float getY(){
        return this.position.y;
    }
}
