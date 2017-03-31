package edu.cornell.gdiac.mistic;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import edu.cornell.gdiac.obstacle.BoxObstacle;


import java.lang.reflect.Array;
import java.util.ArrayList;

import static com.badlogic.gdx.math.MathUtils.degreesToRadians;
import static com.badlogic.gdx.math.MathUtils.random;

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
    BoardModel board;
    private TextureRegion tex;
    private Vector2 scale;
    /**The time between firefly spawns*/
    private float SPAWN_TIME;
    protected ArrayList<Firefly> fireflies;
    protected ArrayList<Body> fireflyBodies;
    protected ArrayList<Body> garbage;

    public FireflyController(TextureRegion texture, Vector2 scale, BoardModel board){
        fireflies=new ArrayList<Firefly>();
        fireflyBodies=new ArrayList<Body>();
        garbage= new ArrayList<Body>();
        tex=texture;
        this.board=board;
        this.scale=scale;
    }

    public boolean update(GorfModel gorf){
        Firefly f=getFirefly(gorf);
        if(f!=null&&!f.isDestroyed()){
            f.setDestroyed();
            return true;
        }
        return false;
    }

    public void resetGarbage(){
        garbage=new ArrayList<Body>();
    }

    public Firefly spawn(){
        int x= random(100);
        int y=random(100);
        BoardModel.Tile t= board.tiles[x][y];
        if(!t.isWall){
            Firefly f = create(t.fx,t.fy);
            return f;
        }
        return spawn();
    }

    public Firefly getFirefly(GorfModel gorf){
        for(Firefly F : fireflies){
            float dx= Math.abs((F.getX()/scale.x)-gorf.getX());
            float dy= Math.abs((F.getY()/scale.y)-gorf.getY());
            if (dx < gorf.getWidth() && dy < gorf.getHeight()){
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
