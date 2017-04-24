/*
 * RocketModel.java
 *
 * This is one of the files that you are expected to modify. Please limit changes to 
 * the regions that say INSERT CODE HERE.
 *
 * Note how this class combines physics and animation.  This is a good template
 * for models in your game.
 *
 * Author: Walker M. White
 * Based on original PhysicsDemo Lab by Don Holden, 2007
 * LibGDX version, 2/6/2015
 */
package edu.cornell.gdiac.mistic;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.physics.box2d.*;

import com.badlogic.gdx.utils.Array;
import edu.cornell.gdiac.GameCanvas;
import edu.cornell.gdiac.WorldController;
import edu.cornell.gdiac.obstacle.BoxObstacle;
import edu.cornell.gdiac.util.*;

/**
 * Player avatar for the rocket lander game.
 *
 * Note that this class returns to static loading.  That is because there are
 * no other subclasses that we might loop through.
 */
public class GorfModel extends BoxObstacle {
	// Default physics values
	/** The density of this rocket */
	private static final float DEFAULT_DENSITY  =  1.0f;
	/** The friction of this rocket */
	private static final float DEFAULT_FRICTION = 0.1f;
	/** The restitution of this rocket */
	private static final float DEFAULT_RESTITUTION = 0.4f;
	/** The thrust factor to convert player input into thrust */
	private static final float DEFAULT_THRUST = 25.0f;

	/** The force to apply to this rocket */
	private Vector2 force;

	/** Cache object for transforming the force according the object angle */
	public Affine2 affineCache = new Affine2();
	/** Cache object for left afterburner origin */
	public Vector2 leftOrigin = new Vector2();
	/** Cache object for right afterburner origin */
	public Vector2 rghtOrigin = new Vector2();

	/** Boolean if Gorf is currently colliding **/
	boolean isCollidingX;
	boolean isCollidingY;
	boolean isCollidingTwice;
	float lastFX;
	float lastFY;
	float currFX = 0;
	float currFY = 0;

	/**
	 * Returns the force applied to this rocket.
	 *
	 * This method returns a reference to the force vector, allowing it to be modified.
	 * Remember to modify the input values by the thrust amount before assigning
	 * the value to force.
	 *
	 * @return the force applied to this rocket.
	 */
	public Vector2 getForce() {
		return force;
	}

	/**
	 * Why the hell hasn't this been implemented yet
	 *
	 * @param value the new Gorf force vector
	 */
	public void setForce(Vector2 value) {
		force = value;
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
		force.x = value;
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
		force.y = value;
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
	 * Creates a new rocket at the origin.
	 *
	 * The size is expressed in physics units NOT pixels.  In order for
	 * drawing to work properly, you MUST set the drawScale. The drawScale
	 * converts the physics units to pixels.
	 *
	 * @param width		The object width in physics units
	 * @param height	The object width in physics units
	 */
	public GorfModel(float width, float height) {
		this(0,0,width,height);
	}

	/**
	 * Creates a new rocket at the given position.
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
	public GorfModel(float x, float y, float width, float height) {
		super(x,y,width,height);
		//getTexture().setRegion(getTexture().getRegionX(),getTexture().getRegionY(),getTexture().getRegionWidth(),getTexture().getRegionHeight());

		force = new Vector2();
		setDensity(DEFAULT_DENSITY);
		setDensity(DEFAULT_DENSITY);
		setFriction(DEFAULT_FRICTION);
		setRestitution(DEFAULT_RESTITUTION);
		setName("gorf");
	}

	/**
	 * Creates the physics Body(s) for this object, adding them to the world.
	 *
	 * This method overrides the base method to keep your ship from spinning.
	 *
	 * @param world Box2D world to store body
	 *
	 * @return true if object allocation succeeded
	 */
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
	 * Applies the force to the body of this ship
	 *
	 * This method should be called after the force attribute is set.
	 */
	public void applyForce() {
		if (!isActive()) {
			return;
		}

		// Orient the force with rotation.
		affineCache.setToRotationRad(getAngle());
		affineCache.applyTo(force);

		//#region INSERT CODE HERE
		// Apply force to the rocket BODY, not the rocket
		// Apply input movement as velocity not force, so starting
		// and stopping is instantaneous
		body.setLinearVelocity(force);
		//#endregionx
	}

	public void update(float delta) {
		super.update(delta);
		lastFX = currFX;
		lastFY = currFY;
		currFX = getFX();
		currFY = getFY();
	}

	public void setCollidingX(boolean bool) {
		isCollidingX = bool;
	}
	public void setCollidingY(boolean bool) { isCollidingY = bool; }

	public void drawHat(GameCanvas canvas, TextureRegion tex, Vector2 scale){
		BoxObstacle hat = new BoxObstacle(this.getWidth(),this.getHeight());
		hat.setX(this.getX()*scale.x);
		hat.setY(this.getY()*scale.y);
		this.draw(canvas);
	}



//	public void setCollidingTwice(boolean bool) { isCollidingTwice = bool; }

	public boolean isCollidingX() {
		return isCollidingX;
	}
	public boolean isCollidingY() { return isCollidingY; }

//	public boolean isCollidingTwice() { return isCollidingTwice; }

	public float getLastFX() {
		return lastFX;
	}

	public float getLastFY() {
		return lastFY;
	}
}