/*
 * GameController.java
 * Copyright Mishka 2017
 *
 */
package edu.cornell.gdiac.mistic;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.assets.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.*;
import edu.cornell.gdiac.util.*;

import edu.cornell.gdiac.InputController;
import edu.cornell.gdiac.WorldController;
import edu.cornell.gdiac.obstacle.BoxObstacle;
import edu.cornell.gdiac.obstacle.Obstacle;
import edu.cornell.gdiac.obstacle.PolygonObstacle;


import javax.swing.*;
import javax.xml.soap.Text;
import java.util.ArrayList;
import java.util.HashMap;
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
    private static final String FIRE_TRACK="mistic/fireflyicon.png";
    private static final String MONSTER_TEXTURE = "mistic/monster01.png";
    private static final String[] MIST_WALLS= {"mistic/mistblock/mistblock1.png",
            "mistic/mistblock/mistblock2.png", "mistic/mistblock/mistblock3.png", "mistic/mistblock/mistblock4.png",
            "mistic/mistblock/mistblock5.png", "mistic/mistblock/mistblock6.png", "mistic/mistblock/mistblock7.png",
            "mistic/mistblock/mistblock8.png", "mistic/mistblock/mistblock9.png", "mistic/mistblock/mistblock10.png",
            "mistic/mistblock/mistblock11.png", "mistic/mistblock/mistblock12.png", "mistic/mistblock/mistblock13.png",
            "mistic/mistblock/mistblock14.png","mistic/mistblock/mistblock15.png", "mistic/mistblock/mistblock16.png"};

    private static final String[] FAMILIARS={
            "mistic/familiars/cat.png","mistic/familiars/chicken.png","mistic/familiars/hedgehog.png",
            "mistic/familiars/tortoise.png",
    };
    /** The reference for the afterburner textures */
    /** Reference to the crate image assets */
    private static final String LIT_LANTERN = "mistic/lit.png";
    private static final String UNLIT_LANTERN = "mistic/unlit.png";
    private static final String FIREFLY_ANIMATE="mistic/firefly-sprite.png";


    private FilmStrip fireflyAnimation;


    /** Texture assets for the rocket */
    private TextureRegion gorfTexture;
    private TextureRegion backgroundTexture;
    private TextureRegion fireflyTexture;
    private TextureRegion fogTexture;
    private TextureRegion fireflyTrack;
    private TextureRegion monsterTexture;
    private TextureRegion[] mistwalls = new TextureRegion[MIST_WALLS.length];
    private TextureRegion[] familiarTex = new TextureRegion[FAMILIARS.length];
    /** Texture assets for the crates */
    private TextureRegion litTexture;
    private TextureRegion unlitTexture;

    /** Track asset loading from all instances and subclasses */
    private AssetState rocketAssetState = AssetState.EMPTY;
    /** The reader to process JSON files */
    private JsonReader jsonReader;
    /** The JSON defining the level model */
    private JsonValue levelFormat;

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

        manager.load(FIREFLY_ANIMATE,Texture.class);
        assets.add(FIREFLY_ANIMATE);
        //Json Reader
        jsonReader = new JsonReader();

        //mist wall textures
        for(String m : MIST_WALLS){
            manager.load(m, Texture.class);
            assets.add(m);
        }

        for(String f : FAMILIARS){
            manager.load(f, Texture.class);
            assets.add(f);
        }

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
        litTexture.setRegion( litTexture.getRegionX()-3, litTexture.getRegionY()+20, litTexture.getRegionWidth(), litTexture.getRegionHeight()+22);
        unlitTexture.setRegion(unlitTexture.getRegionX()-3,unlitTexture.getRegionY()+20,unlitTexture.getRegionWidth(),unlitTexture.getRegionHeight()+22);
        gorfTexture = createTexture(manager,GORF_TEXTURE,false);
        fireflyTexture = createTexture(manager,FIRE_FLY,false);
        fogTexture = createTexture(manager,FOG_TEXTURE,true);
        backgroundTexture = createTexture(manager,BACKGROUND,false);
        fireflyTrack=createTexture(manager,FIRE_TRACK,false);
        monsterTexture = createTexture(manager, MONSTER_TEXTURE, false);

        fireflyAnimation=createFilmStrip(manager, FIREFLY_ANIMATE, 1, Firefly.FRAMES,Firefly.FRAMES);

        for(int i=0;i<MIST_WALLS.length;i++){
            mistwalls[i]= createTexture(manager, MIST_WALLS[i], false);
        }
        for(int i=0;i<FAMILIARS.length;i++){
            familiarTex[i]= createTexture(manager,FAMILIARS[i], false);
        }
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
    private Familiar familiars;


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
    private static int FIREFLY_DELAY = 100;
    private int fogDelay = FOG_DELAY;
    private int fireflyDelay = FIREFLY_DELAY;
    private int fireflyDeathTimer;
    private int firefly_counter=0;

    private FrameBuffer fbo;
    private TextureRegion fboRegion;
    OrthographicCamera cam;



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
        this.DEAD = false;
        this.fireflyDeathTimer=0;
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
        this.fireflyDeathTimer=0;
        world = new World(gravity,false);
        world.setContactListener(this);
        setComplete(false);
        setFailure(false);
        populateLevel();
        countdown=120;

        fbo = new FrameBuffer(Pixmap.Format.RGBA8888, (int)(Gdx.graphics.getWidth()*3), (int)(Gdx.graphics.getHeight()*3), false);
        fboRegion = new TextureRegion(fbo.getColorBufferTexture(), (int)(Gdx.graphics.getWidth()*3), (int)(Gdx.graphics.getHeight()*3));
        fboRegion.flip(false, true);

        cam = new OrthographicCamera(Gdx.graphics.getWidth(),
                Gdx.graphics.getHeight());
        cam.setToOrtho(false);

    }

    private void populateLevel() {
        // Set the path for the level json HERE
        // NOTE: Tiled_Demo's 1-3 will NOT COMPILE
        levelFormat = jsonReader.parse(Gdx.files.internal("jsons/Tiled_Demo_4.json"));

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

        // get every texture's group id in the json and map it to it's actual object's name
        HashMap<Integer,Character> textureIDs = new HashMap<Integer,Character>();
        JsonValue tilesets = levelFormat.get("tilesets").child();
        while (tilesets!=null) {
            textureIDs.put(tilesets.get("firstgid").asInt(),tilesets.get("name").asChar());
            tilesets = tilesets.next();
        }

        // initialize BoardModel
        Rectangle screenSize = new Rectangle(0, 0, canvas.getWidth()*2, canvas.getHeight()*2);
        this.tileBoard = new BoardModel(levelFormat.get("width").asInt(), levelFormat.get("height").asInt(), screenSize);

        // get json data as array
        int[] maze = levelFormat.get("layers").get(1).get("data").asIntArray();

        // for loop for adding info from json data array to the board model
        int i = 0; int j = 0;
        for (int t : maze) {
            if (t!=0&&textureIDs.containsKey(t)) {
                Character c = textureIDs.get(t);
                switch (c) {
                    case 'w':
                        tileBoard.tiles[i][j].isWall=true;
                        break;
                    case 'l':
                        tileBoard.tiles[i][j].isLantern=true;
                        break;
                    case 'g':
                        // SPAWN GORF HERE LATER!!!
                        break;
                    case 'f':
                        tileBoard.tiles[i][j].isFogSpawn=true;
                        break;
                    case 'x':
                        tileBoard.tiles[i][j].hasFamiliar=true;
                        break;
                    default:
                        break;
                }
            }

            // increment the counters
            if (i<99) {i++;} else {i=0;}
            if (i==0) {j++;}
        }

        // Initializer
        ArrayList<BoardModel.Tile> familiarPositions=new ArrayList<BoardModel.Tile>();

        for (BoardModel.Tile[] ta: tileBoard.tiles) {
            for(BoardModel.Tile t :ta) {
                if (t.isLantern) {
                    createLantern(tileBoard.getTileCenterX(t) / scale.x,
                            tileBoard.getTileCenterY(t) / scale.y);
                }
                if (t.isWall) {

                    int wall_i = random(mistwalls.length - 1);
                    TextureRegion mistwall = mistwalls[wall_i];

                    BoxObstacle po = new BoxObstacle(tileBoard.getTileCenterX(t) / scale.x,
                            tileBoard.getTileCenterY(t) / scale.y, tileBoard.getTileWidth() / scale.x,
                            tileBoard.getTileHeight() / scale.y);
                    po.setBodyType(BodyDef.BodyType.StaticBody);
                    po.setDensity(BASIC_DENSITY);
                    po.setFriction(BASIC_FRICTION);
                    po.setRestitution(BASIC_RESTITUTION);
                    po.setDrawScale(scale);
                    po.setTexture(mistwall);
                    addObject(po);
                }
                if (t.hasFamiliar) {
                    familiarPositions.add(t);
                }
            }

        }
        fireflyController=new FireflyController(fireflyTexture,scale,tileBoard);
        fireflyController.setFireflyAnimation(fireflyAnimation);
        Vector2[] familiarVectors= new Vector2[familiarPositions.size()];
        for(int k=0;k<familiarPositions.size();k++){
            familiarVectors[k]= new Vector2(familiarPositions.get(k).fx/scale.x,familiarPositions.get(k).fy/scale.y);
        }
        if(familiarVectors.length!=0) {
            familiars = new Familiar(familiarTex, familiarVectors, scale);
            addObject(familiars.object);
        }

        this.ai = new AIController(monster, tileBoard, gorf, scale);

         fog = new FogController(tileBoard, canvas, screenSize, 2.0f, scale);
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

        familiars.update(gorf);

        float Gorfx= gorf.getPosition().x * scale.x;
        float Gorfy= gorf.getPosition().y * scale.y;
        BoardModel.Tile gorftile= tileBoard.tiles[tileBoard.screenToBoardX(Gorfx)][tileBoard.screenToBoardY(Gorfy)];
        boolean inFog=gorftile.isFog;

        if (inFog){
            fireflyDeathTimer+=1;
            if(fireflyDeathTimer>fireflyDelay){
                if(firefly_count!=0) {
                    firefly_count -= 1;
                }
                fireflyDeathTimer=0;
            }
        }
        if(!inFog){
            fireflyDeathTimer=0;
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

        firefly_counter++;
        if (firefly_counter==200) {
            firefly_counter=0;
            fireflyController.spawn();
        }

        SoundController.getInstance().update();

        if(fireflyController.update(gorf)){
            firefly_count++;
        }
        fireflyController.updateFireflyAnimation(true);
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

        fog.update(gorf,Lanterns,tileBoard);

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
//        canvas.begin();
//        canvas.draw(backgroundTexture, Color.WHITE, 0, 0,canvas.getWidth()*2,canvas.getHeight()*2);
//        canvas.end();

//        fog.draw(canvas, firefly_count);



//        fbo.begin();
//        Gdx.gl.glClearColor(0, 0, 0, 0);
//        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

//        cam.setToOrtho(false, fbo.getWidth(), fbo.getHeight());
//        canvas.getSpriteBatch().setProjectionMatrix(cam.combined);

//        fbo.begin();

//        canvas.setWidth(fbo.getWidth());
//        canvas.setHeight(fbo.getHeight());

//        cam.setToOrtho(false, canvas.getWidth(), canvas.getHeight());
//        canvas.getSpriteBatch().setProjectionMatrix(cam.combined);



//        canvas.getSpriteBatch().setShader(null);    // this is causing fog shading to not wrap

        // Draw background on all sides and diagonals for wrap illusion

        if (gorf.getY() > DEFAULT_HEIGHT / 2f) {
            canvas.resetCamera();
            canvas.getSpriteBatch().setShader(null);
            fbo.begin();
            canvas.clear();
            canvas.begin();
            canvas.draw(backgroundTexture, Color.WHITE, 0, 0, canvas.getWidth() * 2, canvas.getHeight() * 2);
            for (Obstacle obj : objects) {
                if (obj.isActive()) {
                    obj.draw(canvas);
                }
            }
            for (Firefly f : fireflyController.fireflies) {
                if (f != null && !f.isDestroyed()) {
                    f.getObject().draw(canvas);
                }
            }
            canvas.end();
            fbo.end();
            canvas.setShader(fog.getShader());
            fog.prepShader(firefly_count);
            canvas.begin(gorf.getPosition());
            fog.draw(canvas, fboRegion, new Vector2(0, canvas.getHeight() * 2));
            canvas.end();
        }

        if (gorf.getX() > DEFAULT_WIDTH / 2f && gorf.getY() > DEFAULT_HEIGHT / 2f) {
            canvas.resetCamera();
            canvas.getSpriteBatch().setShader(null);
            fbo.begin();
            canvas.clear();
            canvas.begin();
            canvas.draw(backgroundTexture, Color.WHITE, 0, 0, canvas.getWidth() * 2, canvas.getHeight() * 2);
            for (Obstacle obj : objects) {
                if (obj.isActive()) {
                    obj.draw(canvas);
                }
            }
            for (Firefly f : fireflyController.fireflies) {
                if (f != null && !f.isDestroyed()) {
                    f.getObject().draw(canvas);
                }
            }
            canvas.end();
            fbo.end();
            canvas.setShader(fog.getShader());
            fog.prepShader(firefly_count);
            canvas.begin(gorf.getPosition());
            fog.draw(canvas, fboRegion, new Vector2(canvas.getWidth() * 2, canvas.getHeight() * 2));
            canvas.end();
        }

        if (gorf.getY() < DEFAULT_HEIGHT / 2f) {
            canvas.resetCamera();
            canvas.getSpriteBatch().setShader(null);
            fbo.begin();
            canvas.clear();
            canvas.begin();
            canvas.draw(backgroundTexture, Color.WHITE, 0, 0, canvas.getWidth() * 2, canvas.getHeight() * 2);
            for (Obstacle obj : objects) {
                if (obj.isActive()) {
                    obj.draw(canvas);
                }
            }
            for (Firefly f : fireflyController.fireflies) {
                if (f != null && !f.isDestroyed()) {
                    f.getObject().draw(canvas);
                }
            }
            canvas.end();
            fbo.end();
            canvas.setShader(fog.getShader());
            fog.prepShader(firefly_count);
            canvas.begin(gorf.getPosition());
            fog.draw(canvas, fboRegion, new Vector2(0, -canvas.getHeight() * 2));
            canvas.end();
        }

        if (gorf.getX() > DEFAULT_WIDTH / 2f && gorf.getY() < DEFAULT_HEIGHT / 2f) {
            canvas.resetCamera();
            canvas.getSpriteBatch().setShader(null);
            fbo.begin();
            canvas.clear();
            canvas.begin();
            canvas.draw(backgroundTexture, Color.WHITE, 0, 0, canvas.getWidth() * 2, canvas.getHeight() * 2);
            for (Obstacle obj : objects) {
                if (obj.isActive()) {
                    obj.draw(canvas);
                }
            }
            for (Firefly f : fireflyController.fireflies) {
                if (f != null && !f.isDestroyed()) {
                    f.getObject().draw(canvas);
                }
            }
            canvas.end();
            fbo.end();
            canvas.setShader(fog.getShader());
            fog.prepShader(firefly_count);
            canvas.begin(gorf.getPosition());
            fog.draw(canvas, fboRegion, new Vector2(canvas.getWidth() * 2, -canvas.getHeight() * 2));
            canvas.end();
        }

        if (gorf.getX() > DEFAULT_WIDTH / 2f) {
            canvas.resetCamera();
            canvas.getSpriteBatch().setShader(null);
            fbo.begin();
            canvas.clear();
            canvas.begin();
            canvas.draw(backgroundTexture, Color.WHITE, 0, 0, canvas.getWidth() * 2, canvas.getHeight() * 2);
            for (Obstacle obj : objects) {
                if (obj.isActive()) {
                    obj.draw(canvas);
                }
            }
            for (Firefly f : fireflyController.fireflies) {
                if (f != null && !f.isDestroyed()) {
                    f.getObject().draw(canvas);
                }
            }
            canvas.end();
            fbo.end();
            canvas.setShader(fog.getShader());
            fog.prepShader(firefly_count);
            canvas.begin(gorf.getPosition());
            fog.draw(canvas, fboRegion, new Vector2(canvas.getWidth() * 2, 0));
            canvas.end();
        }

        if (gorf.getX() < DEFAULT_WIDTH / 2f && gorf.getY() < DEFAULT_HEIGHT / 2f) {
            canvas.resetCamera();
            canvas.getSpriteBatch().setShader(null);
            fbo.begin();
            canvas.clear();
            canvas.begin();
            canvas.draw(backgroundTexture, Color.WHITE, 0, 0, canvas.getWidth() * 2, canvas.getHeight() * 2);
            for (Obstacle obj : objects) {
                if (obj.isActive()) {
                    obj.draw(canvas);
                }
            }
            for (Firefly f : fireflyController.fireflies) {
                if (f != null && !f.isDestroyed()) {
                    f.getObject().draw(canvas);
                }
            }
            canvas.end();
            fbo.end();
            canvas.setShader(fog.getShader());
            fog.prepShader(firefly_count);
            canvas.begin(gorf.getPosition());
            fog.draw(canvas, fboRegion, new Vector2(-canvas.getWidth() * 2, -canvas.getHeight() * 2));
            canvas.end();
        }

        if (gorf.getX() < DEFAULT_WIDTH / 2f) {
            canvas.resetCamera();
            canvas.getSpriteBatch().setShader(null);
            fbo.begin();
            canvas.clear();
            canvas.begin();
            canvas.draw(backgroundTexture, Color.WHITE, 0, 0, canvas.getWidth() * 2, canvas.getHeight() * 2);
            for (Obstacle obj : objects) {
                if (obj.isActive()) {
                    obj.draw(canvas);
                }
            }
            for (Firefly f : fireflyController.fireflies) {
                if (f != null && !f.isDestroyed()) {
                    f.getObject().draw(canvas);
                }
            }
            canvas.end();
            fbo.end();
            canvas.setShader(fog.getShader());
            fog.prepShader(firefly_count);
            canvas.begin(gorf.getPosition());
            fog.draw(canvas, fboRegion, new Vector2(-canvas.getWidth() * 2, 0));
            canvas.end();
        }

        if (gorf.getX() < DEFAULT_WIDTH / 2f && gorf.getY() > DEFAULT_HEIGHT / 2f) {
            canvas.resetCamera();
            canvas.getSpriteBatch().setShader(null);
            fbo.begin();
            canvas.clear();
            canvas.begin();
            canvas.draw(backgroundTexture, Color.WHITE, 0, 0, canvas.getWidth() * 2, canvas.getHeight() * 2);
            for (Obstacle obj : objects) {
                if (obj.isActive()) {
                    obj.draw(canvas);
                }
            }
            for (Firefly f : fireflyController.fireflies) {
                if (f != null && !f.isDestroyed()) {
                    f.getObject().draw(canvas);
                }
            }
            canvas.end();
            fbo.end();
            canvas.setShader(fog.getShader());
            fog.prepShader(firefly_count);
            canvas.begin(gorf.getPosition());
            fog.draw(canvas, fboRegion, new Vector2(-canvas.getWidth() * 2, canvas.getHeight() * 2));
            canvas.end();
        }

        
        // main canvas
        canvas.resetCamera();
        canvas.getSpriteBatch().setShader(null);
        Gdx.gl.glDisable(GL20.GL_SCISSOR_TEST);
        fbo.begin();
        canvas.clear();
        canvas.begin();
        canvas.draw(backgroundTexture, Color.WHITE, 0, 0, canvas.getWidth()*2,canvas.getHeight()*2);
        for(Obstacle obj : objects) {if(obj.isActive()){obj.draw(canvas);}}
        for(Firefly f : fireflyController.fireflies) {if(f!=null &&!f.isDestroyed()){f.getObject().draw(canvas);

        }}
        canvas.end();
        fbo.end();
        canvas.setShader(fog.getShader());
        fog.prepShader(firefly_count);
        canvas.begin(gorf.getPosition());
        fog.draw(canvas, fboRegion, new Vector2(0, 0));
        canvas.end();

        canvas.resetCamera();
        canvas.getSpriteBatch().setShader(null);
        canvas.begin(gorf.getPosition());

        canvas.draw(fireflyTrack,gorf.getPosition().x * scale.x,gorf.getPosition().y * scale.y);
        displayFont.setColor(Color.WHITE);
        canvas.drawText(Integer.toString(firefly_count),displayFont,(gorf.getPosition().x * scale.x)+50.0f,gorf.getPosition().y*scale.y + 40.0f);

        canvas.end();
        
        canvas.begin();
        if (familiars.collectAll) {
            if (countdown > 0) {
                String vic = "Victory!";
                displayFont.setColor(Color.PURPLE);
                canvas.drawText(vic, displayFont, canvas.getWidth(), canvas.getHeight()/2);
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
