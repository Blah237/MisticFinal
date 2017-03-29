package edu.cornell.gdiac.mistic;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import edu.cornell.gdiac.obstacle.BoxObstacle;


import java.lang.reflect.Array;
import java.util.ArrayList;

import static com.badlogic.gdx.math.MathUtils.degreesToRadians;
import static com.badlogic.gdx.math.MathUtils.random;
import edu.cornell.gdiac.mistic.BoardModel;
/**
 * This class is the Firefly pool that handles allocation and deletion of
 * firefly objects. On its update loop new firelies are spawned if  certain
 * time has elapsed, and collected firefly objects are removed from memory.
 * Also controls the small up-down motion for static fireflies.
 */
public class FireflyController {

    private static final float FIREFLY_DENSITY = 1.0f;
    private static final float FIREFLY_FRICTION  = 0.3f;
    private static final float FIREFLY_RESTITUTION = 0.1f;
    private TextureRegion tex;
    private Vector2 scale;
    BoardModel.Tile[][] tiles;
    /**The time between firefly spawns*/
    private float SPAWN_TIME;
    protected ArrayList<Firefly> fireflies;
    protected ArrayList<Body> fireflyBodies;
    protected ArrayList<Body> garbage;

    public FireflyController(TextureRegion texture, Vector2 scale, BoardModel tboard){
        tiles=tboard.tiles;
        fireflies=new ArrayList<Firefly>();
        fireflyBodies=new ArrayList<Body>();
        garbage= new ArrayList<Body>();
        tex=texture;
        this.scale=scale;
    }

    public boolean update(float x, float y){
        Firefly f=getFirefly(x,y);
        if(f!=null&&!f.isDestroyed()){
            f.setDestroyed();
            return true;
        }
        return false;
    }

    public void resetGarbage(){
        garbage=new ArrayList<Body>();
    }

    public Firefly spawn(float height, float width){
        float x= random(width);
        float y=random(height);
        Firefly f = create(x,y);
        //fireflyBodies.add(ob.getBody());
        return f;
    }

    public Firefly getFirefly(float x, float y){
        for(Firefly F : fireflies){
            float dx= Math.abs((F.getX()/scale.x)-x);
            float dy= Math.abs((F.getY()/scale.y)-y);
            if (dx < 2.5f && dy < 2f){
                return F;
            }
        }
        return null;
    }



    public Firefly create(float x, float y){
        Firefly f= new Firefly(x,y,tex);
        fireflies.add(f);
        return f;
    }

    public Firefly getFirefly(Body b){
        for(Firefly f : fireflies){
            if(f.getObject().getBody()==b){
                return f;
            }
        }
        return null;
    }



}
