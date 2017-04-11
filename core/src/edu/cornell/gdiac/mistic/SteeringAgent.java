package edu.cornell.gdiac.mistic;

import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.badlogic.gdx.ai.steer.SteeringBehavior;
import com.badlogic.gdx.ai.tests.steer.box2d.Box2dLocation;
import com.badlogic.gdx.ai.tests.steer.box2d.Box2dSteeringUtils;
import com.badlogic.gdx.ai.utils.Location;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Nathaniel on 4/1/17.
 */
public class SteeringAgent implements Steerable<Vector2> {

    private enum FSMState {
        /** The monster just spawned */
        SPAWN,
        /** The monster is patrolling around without a target */
        WANDER,
        /** The ship has a target*/
        CHASE,
    }

    private static final SteeringAcceleration<Vector2> steeringOutput =
            new SteeringAcceleration<Vector2>(new Vector2());

    Vector2 position;
    float orientation;
    Vector2 linearVelocity;
    float angularVelocity;
    float maxSpeed;
    boolean independentFacing;
    private float zeroLinearSpeedThreshold;
    SteeringBehavior<Vector2> steeringBehavior;
    public MonsterModel ai;

    private float boundingRadius;
    private boolean tagged;
    private float maxLinearAcceleration;
    private float maxAngularAcceleration;
    private float maxLinearSpeed;
    private float maxAngularSpeed;

    public static float calculateOrientationFromLinearVelocity (Steerable<Vector2> character) {
        // If we haven't got any velocity, then we can do nothing.
        if (character.getLinearVelocity().isZero(character.getZeroLinearSpeedThreshold()))
            return character.getOrientation();

        return character.vectorToAngle(character.getLinearVelocity());
    }
    /* Here you should implement missing methods inherited from Steerable */


   // public void update (float delta) {
       // if (steeringBehavior != null) {
            // Calculate steering acceleration
          //  steeringBehavior.calculateSteering(steeringOutput);

            /*
             * Here you might want to add a motor control layer filtering steering accelerations.
             *
             * For instance, a car in a driving game has physical constraints on its movement:
             * - it cannot turn while stationary
             * - the faster it moves, the slower it can turn (without going into a skid)
             * - it can brake much more quickly than it can accelerate
             * - it only moves in the direction it is facing (ignoring power slides)
             */

            // Apply steering acceleration to move this agent
           // applySteering(steeringOutput, delta);
      //  }
   // }

    //private void applySteering (SteeringAcceleration<Vector2> steering, float time) {
        // Update position and linear velocity. Velocity is trimmed to maximum speed
       // this.position.mulAdd(linearVelocity, time);
       // this.linearVelocity.mulAdd(steering.linear, time).limit(this.getMaxLinearSpeed());

        // Update orientation and angular velocity
       // if (independentFacing) {
           // this.orientation += angularVelocity * time;
          //  this.angularVelocity += steering.angular * time;
       // } else {
            // For non-independent facing we have to align orientation to linear velocity
          //  float newOrientation = calculateOrientationFromLinearVelocity(this);
          //  if (newOrientation != this.orientation) {
           //     this.angularVelocity = (newOrientation - this.orientation) * time;
            //    this.orientation = newOrientation;
          //  }
      //  }
   // }

    public SteeringBehavior<Vector2> getSteeringBehavior() {
        return steeringBehavior;
    }

    public void setSteeringBehavior(SteeringBehavior<Vector2> steeringBehavior) {
        this.steeringBehavior = steeringBehavior;
    }

    public void setBehavior(FSMState behavior) {

    }

    @Override
    public Vector2 getLinearVelocity() {
        return ai.getBody().getLinearVelocity();
    }

    @Override
    public float getAngularVelocity() {
        return ai.getBody().getAngularVelocity();
    }

    @Override
    public boolean isTagged() {
        return tagged;
    }

    @Override
    public void setTagged(boolean tagged) {
        this.tagged = tagged;
    }

    @Override
    public Vector2 getPosition() {
        return ai.getBody().getPosition();
    }

    @Override
    public float getOrientation() {
        return ai.getBody().getAngle();
    }

    @Override
    public void setOrientation(float orientation) {
        ai.getBody().setTransform(getPosition(), orientation);
    }

    @Override
    public float vectorToAngle(Vector2 vector) {
        return Box2dSteeringUtils.vectorToAngle(vector);
    }

    @Override
    public Vector2 angleToVector(Vector2 outVector, float angle) {
        return Box2dSteeringUtils.angleToVector(outVector, angle);
    }

    @Override
    public Location<Vector2> newLocation() {
        return new Box2dLocation();
    }

    @Override
    public float getZeroLinearSpeedThreshold() {
        return zeroLinearSpeedThreshold;
    }

    @Override
    public void setZeroLinearSpeedThreshold(float value) {
        this.zeroLinearSpeedThreshold = value;
    }

    @Override
    public float getMaxLinearSpeed() {
        return maxLinearSpeed;
    }

    @Override
    public void setMaxLinearSpeed(float maxLinearSpeed) {
        this.maxLinearSpeed = maxLinearSpeed;
    }

    @Override
    public float getMaxLinearAcceleration() {
        return maxLinearAcceleration;
    }

    @Override
    public void setMaxLinearAcceleration(float maxLinearAcceleration) {
        this.maxLinearAcceleration = maxLinearAcceleration;
    }

    @Override
    public float getMaxAngularSpeed() {
        return maxAngularSpeed;
    }

    @Override
    public void setMaxAngularSpeed(float maxAngularSpeed) {
        this.maxAngularSpeed = maxAngularSpeed;
    }

    @Override
    public float getMaxAngularAcceleration() {
        return maxAngularAcceleration;
    }

    @Override
    public void setMaxAngularAcceleration(float maxAngularAcceleration) {
        this.maxAngularAcceleration = maxAngularAcceleration;
    }

    @Override
    public float getBoundingRadius() {
        return boundingRadius;
    }
}
