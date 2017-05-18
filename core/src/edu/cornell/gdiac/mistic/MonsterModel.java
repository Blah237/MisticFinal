package edu.cornell.gdiac.mistic;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import edu.cornell.gdiac.GameCanvas;
import edu.cornell.gdiac.obstacle.BoxObstacle;
import edu.cornell.gdiac.obstacle.Obstacle;

/**
 * Created by beau on 3/18/17.
 */

public class MonsterModel extends BoxObstacle {

    /** The thrust factor to convert player input into thrust */
    private static final float DEFAULT_THRUST = 5.0f;
    /** The force to apply to this rocket */
    private Vector2 force;
    /** Cache object for transforming the force according the object angle */
    public Affine2 affineCache = new Affine2();

    /** The density of this rocket */
    private static final float DEFAULT_DENSITY  =  1.0f;
    /** The friction of this rocket */
    private static final float DEFAULT_FRICTION = 0.1f;
    /** The restitution of this rocket */
    private static final float DEFAULT_RESTITUTION = 0.4f;
    /** The thrust factor to convert player input into thrust */
    private static final int MONSTER_DEATH_TIMER = 50;
    private int monsterDeathTimer= MONSTER_DEATH_TIMER;
    private TextureRegion monsterDeadTex;
    public TextureRegion monsterTex;
    public boolean dead;
    public BoxObstacle deadmonster;
    public float deadx;
    public float deady;
    public boolean halved;
    private float CARCASS_TIMER=1000;
    private float carcassTimer=CARCASS_TIMER;
    /**
     * Creates a new monster at the given position.
     *
     * The size is expressed in physics units NOT pixels.  In order for
     * drawing to work properly, you MUST set the drawScale. The drawScale
     * converts the physics units to pixels.
     *
     * @param x  		Initial x position of the box center
     * @param y  		Initial y position of the box center
     * @param width		The object width in physics units
     * @param height	The object width in physics units
     */
    public MonsterModel(float x, float y, float width, float height, TextureRegion tex, TextureRegion deadtex) {
        super(x, y, width, height);
        setName("monster");
        setDensity(DEFAULT_DENSITY);
        setFriction(DEFAULT_FRICTION);
        setRestitution(DEFAULT_RESTITUTION);
        setTexture(tex);
        this.dead=false;
        force = new Vector2();
        deadmonster= new BoxObstacle(x,y,width,height);
        this.deadmonster.setTexture(deadtex);
        halved=false;

    }

    public void monsterDeathReset(){
        monsterDeathTimer=MONSTER_DEATH_TIMER;
    }
    public int getMonsterDeathTimer(){
        return monsterDeathTimer;
    }

    public void updateDeathTimer(){
        monsterDeathTimer--;
    }

    public void setDeadTexture(TextureRegion tex){
        this.monsterDeadTex=tex;
    }

    public void setHalved(boolean halved) {
        this.halved = halved;
    }
    public boolean getHalved() {return this.halved;}

    public boolean activatePhysics(World world) {
        // Get the box body from our parent class
        if (!super.activatePhysics(world)) {
            return false;
        }

        //#region INSERT CODE HERE
        // Insert code here to prevent the body from rotating
        this.setFixedRotation(true);
        //#endregion

        return true;
    }

    /**
     * Returns the x-component of the force applied to this rocket.
     *
     * Remember to modify the input values by the thrust amount before assigning
     * the value to force.
     *
     * @return the x-component of the force applied to this rocket.
     */
    public float getFX() {
        return force.x;
    }

    /**
     * Sets the x-component of the force applied to this rocket.
     *
     * Remember to modify the input values by the thrust amount before assigning
     * the value to force.
     *
     * @param value the x-component of the force applied to this rocket.
     */
    public void setFX(float value) {
        if (!dead){
            force.x = value;
        }
    }

    /**
     * Returns the y-component of the force applied to this rocket.
     *
     * Remember to modify the input values by the thrust amount before assigning
     * the value to force.
     *
     * @return the y-component of the force applied to this rocket.
     */
    public float getFY() {
        return force.y;
    }

    /**
     * Sets the x-component of the force applied to this rocket.
     *
     * Remember to modify the input values by the thrust amount before assigning
     * the value to force.
     *
     * @param value the x-component of the force applied to this rocket.
     */
    public void setFY(float value) {

        if(!dead){
            force.y = value;
        }
    }

    /**
     * Returns the amount of thrust that this rocket has.
     *
     * Multiply this value times the horizontal and vertical values in the
     * input controller to get the force.
     *
     * @return the amount of thrust that this rocket has.
     */
    public float getThrust() {
        return DEFAULT_THRUST;
    }

    /**
     * Applies the force to the body of this ship
     *
     * This method should be called after the force attribute is set.
     */
    public void applyForce() {
        if(!dead){
        if (!isActive()) {
            return;
        }
        }

        // Orient the force with rotation.
        affineCache.setToRotationRad(getAngle());
        affineCache.applyTo(force);

        //#region INSERT CODE HERE
        // Apply force to the rocket BODY, not the rocket
        // Apply input movement as velocity not force, so starting
        // and stopping is instantaneous
        getBody().setLinearVelocity(force);
        //#endregionx
    }


    /**
     * Draws the physics object.
     *
     * @param canvas Drawing context
     */
    public void draw(GameCanvas canvas) {

        super.draw(canvas);
    }
    public void drawDead(GameCanvas canvas){
        carcassTimer--;
        if(carcassTimer==1){
            carcassTimer=CARCASS_TIMER;
            dead=false;
        }
        canvas.draw(monsterDeadTex, deadx, deady);
    }
}
