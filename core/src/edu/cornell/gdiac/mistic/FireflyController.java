package edu.cornell.gdiac.mistic;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import edu.cornell.gdiac.obstacle.BoxObstacle;
import edu.cornell.gdiac.util.FilmStrip;


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
    private int MAX_FIREFLIES=6;
    private int maxfireflies = MAX_FIREFLIES;
    public Firefly[] fireflies;
    private FilmStrip animate;

    public FireflyController(TextureRegion texture, Vector2 scale, BoardModel board){
        fireflies=new Firefly[maxfireflies];
        tex=texture;
        this.board=board;
        this.scale=scale;
    }

    public boolean update(GorfModel gorf){
        fogDeath();
        Firefly f=getFirefly(gorf);
        updateFireflyAnimation();
        if(f!=null){
            f.setDestroyed(true);
            return true;
        }
        return false;
    }

    public Firefly[] getFireflies(){
        return fireflies;
    }

    public void spawn(){
        int x= random(99);
        int y=random(99);
        //System.out.println("Firefly at: "+ x + ", "+y);
        BoardModel.Tile t= board.tiles[x][y];
        if(!(t.isWall || t.isFog)){
           create(t.fx,t.fy);
        }
    }

    public void fogDeath(){
        for(Firefly f : fireflies){
            if(f!=null){
                if (board.tiles[board.screenToBoardX(f.getX())][board.screenToBoardY(f.getY())].isFog){
                    f.setDestroyed(true);
                }
            }
        }
    }


    public Firefly getFirefly(GorfModel gorf){
        for(Firefly F : fireflies) {
            if (F!=null && !F.isDestroyed()) {
                float dx = Math.abs((F.getX() / scale.x) - gorf.getX());
                float dy = Math.abs((F.getY() / scale.y) - gorf.getY());
                if (dx < gorf.getWidth()/1.5 && dy < gorf.getHeight()/1.5) {
                    return F;
                }
            }
        }
            return null;
    }


    public void add(Firefly f){
        for(int i=0; i<fireflies.length; i++){
            if(fireflies[i]==null){
                fireflies[i]=f;
                return;
            }else if (fireflies[i].isDestroyed()){
                fireflies[i]=f;
                return;
            }
        }
    }

    public void create(float x, float y){
        Firefly f= new Firefly(x,y,tex);
        this.add(f);
    }



    public void updateFireflyAnimation(){
            for(Firefly f:fireflies){
                if(f!=null){f.fireflyAnimate();}
            }
    }
}
