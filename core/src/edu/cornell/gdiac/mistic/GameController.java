/*
 * RocketWorldController.java
 *
 * This is one of the files that you are expected to modify. Please limit changes to
 * the regions that say INSERT CODE HERE.
 *
 * Author: Walker M. White
 * Based on original PhysicsDemo Lab by Don Holden, 2007
 * LibGDX version, 2/6/2015
 */
package edu.cornell.gdiac.mistic;

import com.badlogic.gdx.math.*;
import com.badlogic.gdx.assets.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.physics.box2d.*;

import edu.cornell.gdiac.InputController;
import edu.cornell.gdiac.WorldController;
import edu.cornell.gdiac.obstacle.BoxObstacle;
import edu.cornell.gdiac.obstacle.Obstacle;
import edu.cornell.gdiac.obstacle.PolygonObstacle;
import edu.cornell.gdiac.util.*;

import javax.swing.*;
import java.util.ArrayList;
import java.util.LinkedList;
import static com.badlogic.gdx.math.MathUtils.random;
import edu.cornell.gdiac.mistic.Lantern;
/**
 * Gameplay specific controller for the rocket lander game.
 *
 * You will notice that asset loading is not done with static methods this time.
 * Instance asset loading makes it easier to process our game modes in a loop, which
 * is much more scalable. However, we still want the assets themselves to be static.
 * This is the purpose of our AssetState variable; it ensures that multiple instances
 * place nicely with the static assets.
 */
public class GameController extends WorldController implements ContactListener {
    /** Reference to the rocket texture */
    private static final String GORF_TEXTURE = "mistic/gorf.png";
    private static final String BACKGROUND = "mistic/backgroundresize.png";
    private static final String FIRE_FLY= "mistic/firefly.png";
    private static final String FOG_TEXTURE = "mistic/fog.png";
    private static final String FIRE_TRACK="mistic/fireflysprite.png";
    private static final String MONSTER_TEXTURE = "mistic/monster01.png";

    /** The reference for the afterburner textures */
    /** Reference to the crate image assets */
    private static final String LIT_LANTERN = "mistic/lit.png";
    private static final String UNLIT_LANTERN = "mistic/unlit.png";
    /** Texture assets for the rocket */
    private TextureRegion gorfTexture;
    private TextureRegion backgroundTexture;
    private TextureRegion fireflyTexture;
    private TextureRegion fogTexture;
    private TextureRegion fireflyTrack;
    private TextureRegion monsterTexture;

    /** Texture assets for the crates */
    private TextureRegion litTexture;
    private TextureRegion unlitTexture;

    /** Track asset loading from all instances and subclasses */
    private AssetState rocketAssetState = AssetState.EMPTY;

    /**
     * Preloads the assets for this controller.
     *
     * To make the game modes more for-loop friendly, we opted for nonstatic loaders
     * this time.  However, we still want the assets themselves to be static.  So
     * we have an AssetState that determines the current loading state.  If the
     * assets are already loaded, this method will do nothing.
     *
     * @param manager Reference to global asset manager.
     */
    public void preLoadContent(AssetManager manager) {
        if (rocketAssetState != AssetState.EMPTY) {
            return;
        }

        rocketAssetState = AssetState.LOADING;

        //Background
        manager.load(BACKGROUND, Texture.class);
        assets.add(BACKGROUND);
        //Firefly
        manager.load(FIRE_FLY, Texture.class);
        assets.add(FIRE_FLY);
        //Lantern
        manager.load(LIT_LANTERN,Texture.class);
        manager.load(UNLIT_LANTERN,Texture.class);
        assets.add(LIT_LANTERN);
        assets.add(UNLIT_LANTERN);
        //Fog
        manager.load(FOG_TEXTURE, Texture.class);
        assets.add(FOG_TEXTURE);
        // Ship textures
        manager.load(GORF_TEXTURE, Texture.class);
        assets.add(GORF_TEXTURE);
        manager.load(FIRE_TRACK,Texture.class);
        assets.add(FIRE_TRACK);

        // Monster textures
        manager.load(MONSTER_TEXTURE, Texture.class);
        assets.add(MONSTER_TEXTURE);

        /**
         // An Example of loading sounds
         manager.load(MAIN_FIRE_SOUND, Sound.class);
         assets.add(MAIN_FIRE_SOUND);
         manager.load(LEFT_FIRE_SOUND, Sound.class);
         assets.add(LEFT_FIRE_SOUND);
         manager.load(RGHT_FIRE_SOUND, Sound.class);
         assets.add(RGHT_FIRE_SOUND);
         manager.load(COLLISION_SOUND, Sound.class);
         assets.add(COLLISION_SOUND);
         **/
        super.preLoadContent(manager);
    }

    /**
     * Loads the assets for this controller.
     *
     * To make the game modes more for-loop friendly, we opted for nonstatic loaders
     * this time.  However, we still want the assets themselves to be static.  So
     * we have an AssetState that determines the current loading state.  If the
     * assets are already loaded, this method will do nothing.
     *
     * @param manager Reference to global asset manager.
     */
    public void loadContent(AssetManager manager) {
        if (rocketAssetState != AssetState.LOADING) {
            return;
        }


        litTexture=createTexture(manager,LIT_LANTERN,false);
        unlitTexture=createTexture(manager,UNLIT_LANTERN,false);
        gorfTexture = createTexture(manager,GORF_TEXTURE,false);
        fireflyTexture = createTexture(manager,FIRE_FLY,false);
        fogTexture = createTexture(manager,FOG_TEXTURE,true);
        backgroundTexture = createTexture(manager,BACKGROUND,false);
        fireflyTrack=createTexture(manager,FIRE_TRACK,false);
        monsterTexture = createTexture(manager, MONSTER_TEXTURE, false);
        SoundController sounds = SoundController.getInstance();

        super.loadContent(manager);
        rocketAssetState = AssetState.COMPLETE;
    }

    // Physics constants for initialization
    /** Density of non-crate objects */
    private static final float BASIC_DENSITY   = 0.0f;
    /** Density of the crate objects */
    private static final float CRATE_DENSITY   = 1.0f;
    /** Friction of non-crate objects */
    private static final float BASIC_FRICTION  = 0.1f;
    /** Friction of the crate objects */
    private static final float CRATE_FRICTION  = 0.3f;
    /** Collision restitution for all objects */
    private static final float BASIC_RESTITUTION = 0.1f;
    /** Threshold for generating sound on collision */
    private static final float SOUND_THRESHOLD = 1.0f;
    private int countdown = 120;
    FireflyController fireflyController;

    // the number of fireflies Gorf is holding
    private static int firefly_count;
    //ticks
    private static int ticks;
    private static final int FIREFLY_DEATH_TIMcrER = 5;
    private AIController ai;
    private static BoardModel tileBoard;
    private static boolean DEAD;


    // Other game objects
    /** The initial rocket position */
    private static Vector2 GORF_POS = new Vector2(14, 8);

    // Physics objects for the game
    /** Reference to the rocket/player avatar */
    public GorfModel gorf;
    /** Reference to the monster */
    private MonsterModel monster;
    /** Arraylist of Lantern objects */
    public ArrayList<Lantern> Lanterns = new ArrayList<Lantern>();

     private FogController fog;
    private boolean[][] board;
    private boolean[][] fogBoard;
    private float BW = DEFAULT_WIDTH;
    private float BH = DEFAULT_HEIGHT;
    private int UNITS_W = (int)(BW*3);
    private int UNITS_H = (int)(BH*3);
    private float UW = BW / UNITS_W;
    private float UH = BH / UNITS_H;
    private static int FOG_DELAY = 50;
    private static int FIREFLY_DELAY = 150;
    private int fogDelay = FOG_DELAY;
    private int fireflyDelay = FIREFLY_DELAY;



    /**
     * Creates and initialize a new instance of the rocket lander game
     *
     * The game has default gravity and other settings
     */
    public GameController() {
        setDebug(false);
        setComplete(false);
        setFailure(false);
        world.setContactListener(this);
        this.fireflyController=new FireflyController(fireflyTexture, scale,tileBoard);
        this.firefly_count = 0;
        this.ticks = 0;
        this.DEAD = false;

    }

    /**
     * Resets the status of the game so that we can play again.
     *
     * This method disposes of the world and creates a new one.
     */
    public void reset() {
        Vector2 gravity = new Vector2(world.getGravity() );
        for(Obstacle obj : objects) {
            obj.deactivatePhysics(world);
        }
        objects.clear();
        addQueue.clear();
        world.dispose();
        Lanterns = new ArrayList<Lantern>();
        fireflyController = new FireflyController(fireflyTexture, scale,tileBoard);
        this.firefly_count = 2;
        world = new World(gravity,false);
        world.setContactListener(this);
        setComplete(false);
        setFailure(false);
        populateLevel();
        countdown=120;

    }

    private void populateLevel() {



        /**
         * Create Gorf
         */
        float dwidth  = gorfTexture.getRegionWidth()/scale.x;
        float dheight = gorfTexture.getRegionHeight()/scale.y;
        gorf = new GorfModel(GORF_POS.x, GORF_POS.y, dwidth*0.75f, dheight*0.75f);
        gorf.setDrawScale(scale);
        gorf.setTexture(gorfTexture);
        addObject(gorf);


        /**
         * The GameController functions for Gorf-Lantern interactions
         * This includes code for incrementing and decrementing Gorf's firefly counter
         * And adds lanterns to the GameController object pool.
         */

        float w = 9;
        float h = 12;
        //createMonster(w, h);

        Rectangle screenSize = new Rectangle(0, 0, canvas.getWidth()*2, canvas.getHeight()*2);
        this.tileBoard = new BoardModel(100, 100, screenSize);

        tileBoard.tiles[20][40].isFogSpawn=true;

        tileBoard.tiles[0][55].isLantern=true;
        tileBoard.tiles[50][30].isLantern=true;
        tileBoard.tiles[50][70].isLantern=true;
        tileBoard.tiles[25][90].isLantern=true;

        for(int i=0;i<10;i++){
            tileBoard.tiles[0][i].isWall=true;
        }
        for(int i=30;i<50;i++){
            tileBoard.tiles[0][i].isWall=true;
        }
        for(int i=0;i<50;i++){
            tileBoard.tiles[i][0].isWall=true;
        }

        for(int i=0;i<50;i++){
            tileBoard.tiles[i][70].isWall=true;
        }
        for(int i=0;i<50;i++){
            tileBoard.tiles[15][i].isWall=true;
        }
        for(int i=0;i<20;i++){
            tileBoard.tiles[30][i].isWall=true;
        }
        for(int i=30;i<50;i++){
            tileBoard.tiles[30][i].isWall=true;
        }
        for(int i=30;i<50;i++){
            tileBoard.tiles[i][50].isWall=true;
        }
        for(int i=50;i<100;i++){
            tileBoard.tiles[70][i].isWall=true;
        }
        for(int i=70;i<80;i++){
            tileBoard.tiles[i][50].isWall=true;
        }
        for(int i=50;i<100;i++){
            tileBoard.tiles[i][10].isWall=true;
        }
        for(int i=10;i<30;i++){
            tileBoard.tiles[50][i].isWall=true;
        }
        for(int i=70;i<100;i++){
            tileBoard.tiles[i][30].isWall=true;
        }
        for(int i=30;i<85;i++){
            tileBoard.tiles[i][90].isWall=true;
        }

        // for loop for tile walls
        for (BoardModel.Tile[] ta: tileBoard.tiles) {
            for(BoardModel.Tile t :ta) {
                if(t.isLantern){
                    createLantern(tileBoard.getTileCenterX(t)/scale.x,
                            tileBoard.getTileCenterY(t)/scale.y);
                }
                if (t.isWall) {
                    earthTile.setRegionHeight((int)(tileBoard.getTileHeight()));
                    earthTile.setRegionWidth((int)(tileBoard.getTileWidth()));
                    BoxObstacle po = new BoxObstacle(tileBoard.getTileCenterX(t)/scale.x,
                            tileBoard.getTileCenterY(t)/scale.y, earthTile.getRegionWidth()/scale.x,
                            earthTile.getRegionHeight()/scale.y);

                    po.setBodyType(BodyDef.BodyType.StaticBody);
                    po.setDensity(BASIC_DENSITY);
                    po.setFriction(BASIC_FRICTION);
                    po.setRestitution(BASIC_RESTITUTION);
                    po.setDrawScale(scale);
                    po.setTexture(earthTile);
                    addObject(po);
                   /** System.out.println("Tile: " + t.x + ", " + t.y + ", Center: " + tileBoard.getTileCenterX(t) / scale.x +
                            ", " + tileBoard.getTileCenterY(t) / scale.y+ ", Corner: " + t.fx / scale.x +
                            ", " + t.fy / scale.y );
                    System.out.println("Object size:"+po.getWidth() +", "+po.getHeight() +". Texture Size: "
                            + earthTile.getRegionWidth() +", "+ earthTile.getRegionHeight());*/
                }
            }
            fireflyController=new FireflyController(fireflyTexture,scale,tileBoard);
        }

         this.ai = new AIController(monster, tileBoard, gorf, scale);

         fog = new FogController(tileBoard, canvas, screenSize, 2.0f);
    }

    private void createMonster(float x, float y) {
        TextureRegion texture = monsterTexture;
        float dwidth  = texture.getRegionWidth()/scale.x;
        float dheight = texture.getRegionHeight()/scale.y;
        MonsterModel monster = new MonsterModel(x, y, dwidth, dheight);
        this.monster = monster;
        monster.setDensity(CRATE_DENSITY);
        monster.setFriction(CRATE_FRICTION);
        monster.setRestitution(BASIC_RESTITUTION);
        monster.setDrawScale(scale);
        monster.setName("monster");
        monster.setTexture(texture);
        addObject(monster);
        monster.getBody().setUserData("monster");
    }


    private boolean complete(ArrayList<Lantern> al){
        for(Lantern l : Lanterns){
            if(!l.lit) return false;
        }
        return true;
    }

    //Get the lantern at this position
    private Lantern getLantern(float x, float y){
        int xi= (int)x;
        int yi=(int)y;
        for(Lantern l : Lanterns){
            if ((Math.abs((int)l.getX() - xi ) < gorfTexture.getRegionWidth()/scale.x)
                    && (Math.abs((int)l.getY() - yi ) < gorfTexture.getRegionHeight()/scale.y))return l;
        }
        return null;
    }

    void toggle(Lantern l) {
        if (l.lit) {
            firefly_count++;
            l.setTexture(unlitTexture);
            l.toggleLantern();
        } else {
            if (firefly_count >= 1) {
                firefly_count = firefly_count - 1;
                l.setTexture(litTexture);
                l.toggleLantern();
            }
        }

    }

    private void createLantern(float x, float y){
        Lantern l = new Lantern(x,y,unlitTexture,litTexture,scale);
        l.setTexture(unlitTexture);
        Lanterns.add(l);
        addObject(l.object);
    }





    /**
     * The core gameplay loop of this world.
     *
     * This method contains the specific update code for this mini-game. It does
     * not handle collisions, as those are managed by the parent class WorldController.
     * This method is called after input is read, but before collisions are resolved.
     * The very last thing that it should do is apply forces to the appropriate objects.
     *
     * @param dt Number of seconds since last animation frame
     */

    public void update(float dt) {

        //#region INSERT CODE HERE
        // Read from the input and add the force to the rocket model
        // Then apply the force using the method you modified in RocketObject

        boolean pressing = InputController.getInstance().didSecondary();
        if(pressing){

                Lantern l = getLantern(gorf.getX(), gorf.getY());
                if (l!=null){
                    toggle(l);
            }
        }

        float forcex = InputController.getInstance().getHorizontal();
        float forcey= InputController.getInstance().getVertical();
        float moveacc = gorf.getThrust();
        this.gorf.setFX(forcex*moveacc);
        this.gorf.setFY(forcey*moveacc);
        gorf.applyForce();
        wrapInBounds(gorf);

//        ai.setInput();
//        float forceXMonster = ai.getHorizontal();
//        float forceYMonster = ai.getVertical();
//        float monsterthrust = monster.getThrust();
//        this.monster.setFX(forceXMonster * monsterthrust);
//        this.monster.setFY(forceYMonster * monsterthrust);
//        monster.applyForce();



        if (random(500)==10) {
            fireflyController.spawn();
        }

        SoundController.getInstance().update();

        if(fireflyController.update(gorf)){
            firefly_count++;
        }

        /**
         for (Body b : scheduledForRemoval) {
         b.getWorld().destroyBody(b);
         fireflyObjects.remove(b);
         for (BoxObstacle o : fireflyObjectsO) {
         if (b == o.getBody()) {
         objects.remove(o);
         }
         }
         }*/

        fog.update(gorf,Lanterns,canvas,scale,tileBoard);

    }

    /**
     * Function to tell if Gorf (rocket) is off screen and to wrap him around, with a
     * 0.1f position buffer
     *
     * @param rocket   Gorf character
     */
    private void wrapInBounds(GorfModel rocket) {
        if (!inBounds(rocket)) {
            Vector2 currentPos = rocket.getPosition();
            if (currentPos.x<=bounds.getX()) {
                rocket.setPosition(bounds.getX()+(bounds.getWidth()*2)-0.1f,currentPos.y);
            } else if (currentPos.x>=bounds.getX()+(bounds.getWidth()*2)) {
                rocket.setPosition(bounds.getX()+0.1f,currentPos.y);
            }
            if (currentPos.y<=bounds.getY()) {
                rocket.setPosition(currentPos.x,bounds.getY()+(bounds.getHeight()*2)-0.1f);
            } else if (currentPos.y>=bounds.getY()+(bounds.getHeight()*2)) {
                rocket.setPosition(currentPos.x,bounds.getY()+0.1f);
            }
        }
    }

    public void draw(float dt) {
        canvas.clear();

        // Draw background unscaled.
        fog.draw(canvas, firefly_count);

        canvas.begin();
        canvas.draw(backgroundTexture, Color.WHITE, 0, 0,canvas.getWidth()*2,canvas.getHeight()*2);

        canvas.draw(fireflyTrack,gorf.getPosition().x * scale.x,gorf.getPosition().y * scale.y);
        displayFont.setColor(Color.WHITE);
        canvas.drawText(Integer.toString(firefly_count),displayFont,(gorf.getPosition().x * scale.x)+50.0f,gorf.getPosition().y*scale.y + 40.0f);

        // Draw background on all sides and diagonals for wrap illusion
        canvas.draw(backgroundTexture, Color.WHITE, 0, canvas.getHeight()*2,canvas.getWidth()*2,canvas.getHeight()*2);
        canvas.draw(backgroundTexture, Color.WHITE, canvas.getWidth()*2, canvas.getHeight()*2,canvas.getWidth()*2,canvas.getHeight()*2);
        canvas.draw(backgroundTexture, Color.WHITE, 0, -canvas.getHeight()*2,canvas.getWidth()*2,canvas.getHeight()*2);
        canvas.draw(backgroundTexture, Color.WHITE, canvas.getWidth()*2, -canvas.getHeight()*2,canvas.getWidth()*2,canvas.getHeight()*2);
        canvas.draw(backgroundTexture, Color.WHITE, canvas.getWidth()*2, 0,canvas.getWidth()*2,canvas.getHeight()*2);
        canvas.draw(backgroundTexture, Color.WHITE, -canvas.getWidth()*2, -canvas.getHeight()*2,canvas.getWidth()*2,canvas.getHeight()*2);
        canvas.draw(backgroundTexture, Color.WHITE, -canvas.getWidth()*2, 0,canvas.getWidth()*2,canvas.getHeight()*2);
        canvas.draw(backgroundTexture, Color.WHITE, -canvas.getWidth()*2, canvas.getHeight()*2,canvas.getWidth()*2,canvas.getHeight()*2);

        canvas.end();

        // now redraw objects on surrounding canvases
        canvas.begin(gorf.getPosition().add(0,-bounds.getHeight()*2));
        for(Obstacle obj : objects) {if(obj.isActive()){obj.draw(canvas);}}
        for(Firefly f : fireflyController.fireflies) {if(!f.isDestroyed()){f.getObject().draw(canvas);}}
        canvas.end();
        canvas.begin(gorf.getPosition().add(0,bounds.getHeight()*2));
        for(Obstacle obj : objects) {if(obj.isActive()){obj.draw(canvas);}}
        for(Firefly f : fireflyController.fireflies) {if(!f.isDestroyed()){f.getObject().draw(canvas);}}
        canvas.end();
        canvas.begin(gorf.getPosition().add(bounds.getWidth()*2,0));
        for(Obstacle obj : objects) {if(obj.isActive()){obj.draw(canvas);}}
        for(Firefly f : fireflyController.fireflies) {if(!f.isDestroyed()){f.getObject().draw(canvas);}}
        canvas.end();
        canvas.begin(gorf.getPosition().add(-bounds.getWidth()*2,0));
        for(Obstacle obj : objects) {if(obj.isActive()){obj.draw(canvas);}}
        for(Firefly f : fireflyController.fireflies) {if(!f.isDestroyed()){f.getObject().draw(canvas);}}
        canvas.end();

        //diagonal canvases
        canvas.begin(gorf.getPosition().add(bounds.getWidth()*2,-bounds.getHeight()*2));
        for(Obstacle obj : objects) {if(obj.isActive()){obj.draw(canvas);}}
        for(Firefly f : fireflyController.fireflies) {if(!f.isDestroyed()){f.getObject().draw(canvas);}}
        canvas.end();
        canvas.begin(gorf.getPosition().add(bounds.getWidth()*2,bounds.getHeight()*2));
        for(Obstacle obj : objects) {if(obj.isActive()){obj.draw(canvas);}}
        for(Firefly f : fireflyController.fireflies) {if(!f.isDestroyed()){f.getObject().draw(canvas);}}
        canvas.end();
        canvas.begin(gorf.getPosition().add(-bounds.getWidth()*2,-bounds.getHeight()*2));
        for(Obstacle obj : objects) {if(obj.isActive()){obj.draw(canvas);}}
        for(Firefly f : fireflyController.fireflies) {if(!f.isDestroyed()){f.getObject().draw(canvas);}}
        canvas.end();
        canvas.begin(gorf.getPosition().add(-bounds.getWidth()*2,bounds.getHeight()*2));
        for(Obstacle obj : objects) {if(obj.isActive()){obj.draw(canvas);}}
        for(Firefly f : fireflyController.fireflies) {if(!f.isDestroyed()){f.getObject().draw(canvas);}}
        canvas.end();

        // main canvas
        canvas.begin(gorf.getPosition());
        for(Obstacle obj : objects) {if(obj.isActive()){obj.draw(canvas);}}
        for(Firefly f : fireflyController.fireflies) {if(!f.isDestroyed()){f.getObject().draw(canvas);}}
        canvas.end();

        canvas.begin();
        if (complete(Lanterns)) {
            if (countdown > 0) {
                String vic = "Victory!";
                displayFont.setColor(Color.PURPLE);
                canvas.drawText(vic, displayFont, canvas.getWidth()/4, canvas.getHeight()/2);
                countdown --;
            } else if (countdown==0) {
                this.setComplete(true);
            }
        }

        if (DEAD) {
            if (countdown > 0) {
                String vic = "Game Over!";
                displayFont.setColor(Color.PURPLE);
                canvas.drawText(vic, displayFont, canvas.getWidth()/4, canvas.getHeight()/2);
                countdown --;
            } else if (countdown==0) {
                DEAD = false;
                this.setComplete(true);
            }
        }

        if (isDebug()) {
            canvas.beginDebug();
            for(Obstacle obj : objects) {
                obj.drawDebug(canvas);
            }
            canvas.endDebug();
            canvas.endDebug();
        }

        canvas.end();
    }

    /// CONTACT LISTENER METHODS
    /**
     * Callback method for the start of a collision
     *
     * This method is called when we first get a collision between two objects.  We use
     * this method to test if it is the "right" kind of collision.  In particular, we
     * use it to test if we made it to the win door.
     *
     * @param contact The two bodies that collided
     */
    public void beginContact(Contact contact) {
        Body body1 = contact.getFixtureA().getBody();
        Body body2 = contact.getFixtureB().getBody();


        if (body1.getUserData() == "monster" && body2 == gorf.getBody()) {
            this.DEAD = true;
        }
        if (body1 == gorf.getBody() && body2.getUserData() == "monster") {
            this.DEAD = true;
        }}



        //if (ticks % FIREFLY_DEATH_TIMER == 0 && ticks != 0 && body1 == gorf.getBody() && body2.getUserData() == "fog") {
           // if (firefly_count > 0) {
             //   firefly_count = firefly_count - 1;
           // }
        //} else if (ticks % FIREFLY_DEATH_TIMER == 0 && ticks != 0 && body2 == gorf.getBody() && body1.getUserData() == "fog") {
           // if (firefly_count > 0) {
            //    firefly_count = firefly_count - 1;
           // }
       // }
   // }

    /**
     * Callback method for the start of a collision
     *
     * This method is called when two objects cease to touch.  We do not use it.
     */
    public void endContact(Contact contact) {}

    private Vector2 cache = new Vector2();

    /** Unused ContactListener method */
    public void postSolve(Contact contact, ContactImpulse impulse) {}

    /**
     * Handles any modifications necessary before collision resolution
     *
     * This method is called just before Box2D resolves a collision.  We use this method
     * to implement sound on contact, using the algorithms outlined similar to those in
     * Ian Parberry's "Introduction to Game Physics with Box2D".
     *
     * However, we cannot use the proper algorithms, because LibGDX does not implement
     * b2GetPointStates from Box2D.  The danger with our approximation is that we may
     * get a collision over multiple frames (instead of detecting the first frame), and
     * so play a sound repeatedly.  Fortunately, the cooldown hack in SoundController
     * prevents this from happening.
     *
     * @param  contact  	The two bodies that collided
     * @param  oldManifold  	The collision manifold before contact
     */

    public void preSolve(Contact contact, Manifold oldManifold) {
        float speed = 0;

        // Use Ian Parberry's method to compute a speed threshold
        Body body1 = contact.getFixtureA().getBody();
        Body body2 = contact.getFixtureB().getBody();
        WorldManifold worldManifold = contact.getWorldManifold();
        Vector2 wp = worldManifold.getPoints()[0];
        cache.set(body1.getLinearVelocityFromWorldPoint(wp));
        cache.sub(body2.getLinearVelocityFromWorldPoint(wp));
        speed = cache.dot(worldManifold.getNormal());

    }
}
