//package edu.cornell.gdiac.mistic;
//
//import com.badlogic.gdx.ai.DefaultTimepiece;
//import com.badlogic.gdx.ai.steer.Proximity;
//import com.badlogic.gdx.ai.steer.Steerable;
//import com.badlogic.gdx.ai.steer.SteerableAdapter;
//import com.badlogic.gdx.ai.steer.SteeringAcceleration;
//import com.badlogic.gdx.ai.steer.behaviors.BlendedSteering;
//import com.badlogic.gdx.ai.steer.behaviors.PrioritySteering;
//import com.badlogic.gdx.ai.steer.behaviors.Seek;
//import com.badlogic.gdx.ai.utils.Location;
//import com.badlogic.gdx.math.MathUtils;
//import com.badlogic.gdx.math.Vector2;
//import com.badlogic.gdx.physics.box2d.World;
//
///**
// * Created by Nathaniel on 4/12/17.
// */
//public class AIControllerS {
//    DefaultTimepiece timePiece;
//
//    private Seek<Vector2> seekTarget;
//
//    EnemyWrapper enemyWrapper = new EnemyWrapper();
//    TargetWrapper targetWrapper = new TargetWrapper();
//    EmptyWrapper emptyWrapper = new EmptyWrapper();
//
//    EnemyProximity enemyProximity;
//
//    MonsterModel monster;
//    GorfModel gorf;
//    BoardModel board;
//
//    PrioritySteering<Vector2> monsterBehavior;
//
//    private SteeringAcceleration<Vector2> steering = new SteeringAcceleration<Vector2>(new Vector2(0, 0));
//    private boolean initialized = false;
//
//    public AIControllerS(MonsterModel monster, GorfModel gorf, BoardModel board) {
//        timePiece = new DefaultTimepiece();
//        seekTarget = new Seek<Vector2>(emptyWrapper);
//        this.monster = monster;
//        this.gorf = gorf;
//        this.monsterBehavior = new PrioritySteering<Vector2>(emptyWrapper);
//        monsterBehavior.add(seekTarget);
//        this.board = board;
//    }
//
//    public void update(float dt, World world) {
//        monster.setFY(0.0f);
//        monster.setFX(0.0f);
//        monster.applyForce();
//        timePiece.update(dt);
//
//        initializeSteering();
//
//        enemyWrapper.model = monster;
//        targetWrapper.model = gorf;
//
//        seekTarget.setTarget(targetWrapper);
//        seekTarget.setEnabled(true);
//
//        monsterBehavior.calculateSteering(steering);
//        applySteering(steering);
//
//    }
//
//    private void applySteering(SteeringAcceleration<Vector2> steering) {
//        Vector2 monsterPos = monster.getPosition();
//        int tileX = board.screenToBoardX(monsterPos.x * 8.0f);
//        int tileY = board.screenToBoardY(monsterPos.y * 8.0f);
//        if (board.isFog(tileX, tileY) || board.isFogSpawn(tileX, tileY)) {
//            monster.setFX(steering.linear.x * 6.0f);
//            monster.setFY(steering.linear.y * 6.0f);
//            monster.applyForce();
//        }
//
//        //enemyWrapper.model.setAngularVelocity(steering.angular);
//    }
//
//    private void initializeSteering() {
//        if (!initialized) {
//
//            seekTarget.setOwner(enemyWrapper);
//        }
//
//        initialized = true;
//    }
//
//    private class EnemyProximity implements Proximity<Vector2> {
//
//        World world;
//        SteeringAgent owner;
//
//        public EnemyProximity() {
//        }
//
//        @Override
//        public Steerable<Vector2> getOwner() {
//            return owner;
//        }
//
//        @Override
//        public void setOwner(Steerable<Vector2> owner) {
//            this.owner = (SteeringAgent) owner;
//        }
//
//        @Override
//        public int findNeighbors(com.badlogic.gdx.ai.steer.Proximity.ProximityCallback<Vector2> callback) {
//
//            int numNeighbors = 0;
//
//            return numNeighbors;
//        }
//
//    }
//
//    private class TargetWrapper implements Steerable<Vector2> {
//
//        GorfModel model;
//        boolean isTagged;
//        private Vector2 cacheVector = new Vector2();
//        private Vector2 zeroVector = new Vector2(0,0);
//
//        public TargetWrapper() { }
//
//        @Override
//        public Vector2 getPosition() {
//            cacheVector.x = model.getX();
//            cacheVector.y = model.getY();
//            return cacheVector;
//        }
//
//        @Override
//        public float getOrientation() {
//            return model.getAngle();
//        }
//
//        @Override
//        public void setOrientation(float orientation) {
//            model.getBody().setTransform(getPosition(), orientation);
//        }
//
//        @Override
//        public float vectorToAngle(Vector2 vector) {
//            return (float) Math.atan2(((Vector2) vector).y, ((Vector2) vector).x);
//        }
//
//        @Override
//        public Vector2 angleToVector(Vector2 outVector, float angle) {
//            ((Vector2) outVector).x = MathUtils.cos(angle);
//            ((Vector2) outVector).y = MathUtils.sin(angle);
//            return outVector;
//        }
//
//        @Override
//        public Location<Vector2> newLocation() {
//            return new SteerableAdapter<Vector2>();
//        }
//
//        @Override
//        public float getZeroLinearSpeedThreshold() {
//            return 0;
//        }
//
//        @Override
//        public void setZeroLinearSpeedThreshold(float value) { }
//
//        @Override
//        public float getMaxLinearSpeed() {
//            return 0;
//        }
//
//        @Override
//        public void setMaxLinearSpeed(float maxLinearSpeed) { }
//
//        @Override
//        public float getMaxLinearAcceleration() {
//            return 0;
//        }
//
//        @Override
//        public void setMaxLinearAcceleration(float maxLinearAcceleration) { }
//
//        @Override
//        public float getMaxAngularSpeed() {
//            return 0;
//        }
//
//        @Override
//        public void setMaxAngularSpeed(float maxAngularSpeed) { }
//
//        @Override
//        public float getMaxAngularAcceleration() {
//            return 0;
//        }
//
//        @Override
//        public void setMaxAngularAcceleration(float maxAngularAcceleration) { }
//
//        @Override
//        public Vector2 getLinearVelocity() {
//            return zeroVector;
//        }
//
//        @Override
//        public float getAngularVelocity() {
//            return 0;
//        }
//
//        @Override
//        public float getBoundingRadius() {
//            return model.getWidth();
//        }
//
//        @Override
//        public boolean isTagged() {
//            return isTagged;
//        }
//
//        @Override
//        public void setTagged(boolean tagged) {
//            isTagged = tagged;
//        }
//
//    }
//
//    private class EnemyWrapper implements Steerable<Vector2> {
//
//        MonsterModel model;
//        boolean isTagged;
//
//        private Vector2 cacheVector = new Vector2();
//
//        public EnemyWrapper() {
//        }
//
//        @Override
//        public Vector2 getPosition() {
//            cacheVector.x = model.getX();
//            cacheVector.y = model.getY();
//            return cacheVector;
//        }
//
//        @Override
//        public float getOrientation() {
//            return model.getBody().getAngle();
//        }
//
//        @Override
//        public void setOrientation(float orientation) {
//            model.getBody().setTransform(getPosition(), orientation);
//        }
//
//        @Override
//        public float vectorToAngle(Vector2 vector) {
//            return (float) Math.atan2(((Vector2) vector).y, ((Vector2) vector).x);
//        }
//
//        @Override
//        public Vector2 angleToVector(Vector2 outVector, float angle) {
//            ((Vector2) outVector).x = MathUtils.cos(angle);
//            ((Vector2) outVector).y = MathUtils.sin(angle);
//            return outVector;
//        }
//
//        @Override
//        public Location<Vector2> newLocation() {
//            return new SteerableAdapter<Vector2>();
//        }
//
//        @Override
//        public float getZeroLinearSpeedThreshold() {
//            return 0;
//        }
//
//        @Override
//        public void setZeroLinearSpeedThreshold(float value) { }
//
//        @Override
//        public float getMaxLinearSpeed() {
//            return 2.0f;
//        }
//
//        @Override
//        public void setMaxLinearSpeed(float maxLinearSpeed) { }
//
//        @Override
//        public float getMaxLinearAcceleration() {
//            return 2.0f;
//        }
//
//        @Override
//        public void setMaxLinearAcceleration(float maxLinearAcceleration) { }
//
//        @Override
//        public float getMaxAngularSpeed() {
//            return 2.0f;
//        }
//
//        public void setMaxAngularSpeed(float maxAngularSpeed) { }
//
//        @Override
//        public float getMaxAngularAcceleration() {
//            return 2.0f;
//        }
//
//        @Override
//        public void setMaxAngularAcceleration(float maxAngularAcceleration) { }
//
//        @Override
//        public Vector2 getLinearVelocity() {
//            return model.getLinearVelocity();
//        }
//
//        @Override
//        public float getAngularVelocity() {
//            return model.getAngularVelocity();
//        }
//
//        @Override
//        public float getBoundingRadius() {
//            return model.getWidth();
//        }
//
//        @Override
//        public boolean isTagged() {
//            return isTagged;
//        }
//
//        @Override
//        public void setTagged(boolean tagged) {
//            isTagged = tagged;
//        }
//
//    }
//
//    private class EmptyWrapper implements Steerable<Vector2> {
//
//        Vector2 zeroVector = new Vector2(0,0);
//
//        @Override
//        public Vector2 getPosition() {
//            return zeroVector;
//        }
//
//        @Override
//        public float getOrientation() {
//            return 0;
//        }
//
//        @Override
//        public void setOrientation(float orientation) { }
//
//        @Override
//        public float vectorToAngle(Vector2 vector) {
//            return 0;
//        }
//
//        @Override
//        public Vector2 angleToVector(Vector2 outVector, float angle) {
//            return zeroVector;
//        }
//
//        @Override
//        public Location<Vector2> newLocation() {
//            return new SteerableAdapter<Vector2>();
//        }
//
//        @Override
//        public float getZeroLinearSpeedThreshold() {
//            return 0;
//        }
//
//        @Override
//        public void setZeroLinearSpeedThreshold(float value) { }
//
//        @Override
//        public float getMaxLinearSpeed() {
//            return 0;
//        }
//
//        @Override
//        public void setMaxLinearSpeed(float maxLinearSpeed) { }
//
//        @Override
//        public float getMaxLinearAcceleration() {
//            return 0;
//        }
//
//        @Override
//        public void setMaxLinearAcceleration(float maxLinearAcceleration) { }
//
//        @Override
//        public float getMaxAngularSpeed() {
//            return 0;
//        }
//
//        @Override
//        public void setMaxAngularSpeed(float maxAngularSpeed) { }
//
//        @Override
//        public float getMaxAngularAcceleration() {
//            return 0;
//        }
//
//        @Override
//        public void setMaxAngularAcceleration(float maxAngularAcceleration) { }
//
//        @Override
//        public Vector2 getLinearVelocity() {
//            return zeroVector;
//        }
//
//        @Override
//        public float getAngularVelocity() {
//            return 0;
//        }
//
//        @Override
//        public float getBoundingRadius() {
//            return 0;
//        }
//
//        @Override
//        public boolean isTagged() {
//            return false;
//        }
//
//        @Override
//        public void setTagged(boolean tagged) { }
//
//    }
//
//}