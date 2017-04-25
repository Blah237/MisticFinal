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
import com.badlogic.gdx.audio.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.*;
import edu.cornell.gdiac.GameCanvas;
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
import org.lwjgl.Sys;
//import org.lwjgl.Sys;

/**
 * Gameplay specific controller for the Mistic game.
 *
 * You will notice that asset loading is not done with static methods this time.
 * Instance asset loading makes it easier to process our game modes in a loop, which
 * is much more scalable. However, we still want the assets themselves to be static.
 * This is the purpose of our AssetState variable; it ensures that multiple instances
 * place nicely with the static assets.
 */
public class GameController extends WorldController implements ContactListener {
    /** Reference to the rocket texture */
    private static final String[] GORF_TEXTURES = {"mistic/gorfs/gorfD.png","mistic/gorfs/gorfDL.png","mistic/gorfs/gorfDR.png",
            "mistic/gorfs/gorfL.png","mistic/gorfs/gorfR.png","mistic/gorfs/gorfB.png","mistic/gorfs/gorfBL.png",
            "mistic/gorfs/gorfBR.png"};
    private static final String HAT_TEXTURE = "mistic/gorfs/gorftop.png";
    private static final String BACKGROUND = "mistic/backgroundresize.png";
    private static final String FIRE_FLY= "mistic/firefly.png";
    private static final String FIRE_TRACK="mistic/fireflyicon.png";
    private static final String MONSTER_TEXTURE = "mistic/enemyplaceholder.png";
    private static final String[] MIST_WALLS= {"mistic/mistblock/mistblock1.png",
            "mistic/mistblock/mistblock2.png", "mistic/mistblock/mistblock3.png", "mistic/mistblock/mistblock4.png",
            "mistic/mistblock/mistblock5.png", "mistic/mistblock/mistblock6.png", "mistic/mistblock/mistblock7.png",
            "mistic/mistblock/mistblock8.png", "mistic/mistblock/mistblock9.png", "mistic/mistblock/mistblock10.png",
            "mistic/mistblock/mistblock11.png", "mistic/mistblock/mistblock12.png", "mistic/mistblock/mistblock13.png",
            "mistic/mistblock/mistblock14.png","mistic/mistblock/mistblock15.png", "mistic/mistblock/mistblock16.png",
            "mistic/mistblock/mistblock17.png","mistic/mistblock/mistblock18.png", "mistic/mistblock/mistblock19.png",
            "mistic/mistblock/mistblock20.png","mistic/mistblock/mistblock21.png", "mistic/mistblock/mistblock22.png",
            "mistic/mistblock/mistblock23.png","mistic/mistblock/mistblock24.png", "mistic/mistblock/mistblock25.png",
            "mistic/mistblock/mistblock26.png","mistic/mistblock/mistblock27.png", "mistic/mistblock/mistblock28.png",
            "mistic/mistblock/mistblock29.png","mistic/mistblock/mistblock30.png"};

    private static final String[] TREES = { "mistic/environment/tree1.png","mistic/environment/tree2.png",
            "mistic/environment/tree3.png","mistic/environment/tree4.png"
    };
    private static final String[] TREETOPS = { "mistic/environment/tree1top.png","mistic/environment/tree2top.png",
            "mistic/environment/tree3top.png","mistic/environment/tree4top.png"
    };
    private static final String[] ROCKS = { "mistic/environment/rock1.png","mistic/environment/rock2.png",
            "mistic/environment/rock3.png"
    };
    private static final String[] ROCKTOPS = { "mistic/environment/rock1top.png","mistic/environment/rock2top.png",
            "mistic/environment/rock3top.png"
    };
    private static final String[] FAMILIARS={
            "mistic/familiars/cat.png","mistic/familiars/chicken.png","mistic/familiars/hedgehog.png",
            "mistic/familiars/tortoise.png",
    };
    /** The reference for the afterburner textures */
    /** Reference to the crate image assets */
    private static final String LIT_LANTERN = "mistic/lit.png";
    private static final String UNLIT_LANTERN = "mistic/unlit.png";
    private static final String LITTOP_LANTERN = "mistic/littop.png";
    private static final String UNLITTOP_LANTERN = "mistic/unlittop.png";
    private static final String FIREFLY_ANIMATE="mistic/spritesheet_firefly.png";
    //private static final String FIREFLY_ANIMATE = "mistic/spritesheet_firefly_menu.png";

    /** Texture references for the HUD */
    private static final String HUD_WINDOW_TEXTURE = "mistic/hud_window.png";
    private static final String HUD_WHITE_FIREFLY_TEXTURE = "mistic/white_firefly.png";
    private static final String HUD_WHITE_NUMBER_X = "mistic/numbers_white/numbers_x.png";
    private static final String HUD_WHITE_NUMBER_0 = "mistic/numbers_white/numbers_0.png";
    private static final String HUD_WHITE_NUMBER_1 = "mistic/numbers_white/numbers_1.png";
    private static final String HUD_WHITE_NUMBER_2 = "mistic/numbers_white/numbers_2.png";
    private static final String HUD_WHITE_NUMBER_3 = "mistic/numbers_white/numbers_3.png";
    private static final String HUD_WHITE_NUMBER_4 = "mistic/numbers_white/numbers_4.png";
    private static final String HUD_WHITE_NUMBER_5 = "mistic/numbers_white/numbers_5.png";
    private static final String HUD_WHITE_NUMBER_6 = "mistic/numbers_white/numbers_6.png";
    private static final String HUD_WHITE_NUMBER_7 = "mistic/numbers_white/numbers_7.png";
    private static final String HUD_WHITE_NUMBER_8 = "mistic/numbers_white/numbers_8.png";
    private static final String HUD_WHITE_NUMBER_9 = "mistic/numbers_white/numbers_9.png";
    private static final String HUD_WHITE_NUMBER_SLASH = "mistic/numbers_white/numbers_slash.png";
    private static final String HUD_PAW_ANIMATE = "mistic/spritesheet_paw.png";

    /** The SoundController, Music and sfx */
    SoundController sounds = SoundController.getInstance();
    private static final String A_PEACE_SONG = "sounds/A_Peace_DEMO2.mp3";
    private static final String B_MARSH_SONG = "sounds/B_Marsh_DEMO2.mp3";

    private TextureRegion fireflyAnimation;
    private FilmStrip pawAnimation;

    /** Texture assets for the rocket */
    private TextureRegion gorfHat;
    private TextureRegion backgroundTexture;
    private TextureRegion fogTexture;
    private TextureRegion fireflyTrack;
    private TextureRegion monsterTexture;
    private TextureRegion[] gorfTextures = new TextureRegion[GORF_TEXTURES.length];
    private TextureRegion[] mistwalls = new TextureRegion[MIST_WALLS.length];
    private TextureRegion[] familiarTex = new TextureRegion[FAMILIARS.length];
    private TextureRegion[] trees = new TextureRegion[TREES.length];
    private TextureRegion[] rocks = new TextureRegion[ROCKS.length];
    private TextureRegion[] treetops= new TextureRegion[TREETOPS.length];
    private TextureRegion[] rocktops= new TextureRegion[ROCKTOPS.length];
    /** Texture assets for the crates */
    private TextureRegion litTexture;
    private TextureRegion unlitTexture;
    private TextureRegion litTextureTop;
    private TextureRegion unlitTextureTop;

    /** Texture assets for HUD **/
    private TextureRegion HUDWindow;
    private TextureRegion HUDWhiteFirefly;
    private TextureRegion HUDWhiteNumber_x;
    private TextureRegion HUDWhiteNumber_0;
    private TextureRegion HUDWhiteNumber_1;
    private TextureRegion HUDWhiteNumber_2;
    private TextureRegion HUDWhiteNumber_3;
    private TextureRegion HUDWhiteNumber_4;
    private TextureRegion HUDWhiteNumber_5;
    private TextureRegion HUDWhiteNumber_6;
    private TextureRegion HUDWhiteNumber_7;
    private TextureRegion HUDWhiteNumber_8;
    private TextureRegion HUDWhiteNumber_9;
    private TextureRegion HUDWhiteNumber_slash;

    /** Track asset loading from all instances and subclasses */
    private AssetState rocketAssetState = AssetState.EMPTY;

    Rectangle screenSize;

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
        manager.load(LITTOP_LANTERN,Texture.class);
        manager.load(UNLITTOP_LANTERN, Texture.class);
        assets.add(LIT_LANTERN);
        assets.add(UNLIT_LANTERN);
        assets.add(LITTOP_LANTERN);
        assets.add(UNLITTOP_LANTERN);
        // Ship textures

        manager.load(HAT_TEXTURE, Texture.class);
        assets.add(HAT_TEXTURE);
        manager.load(FIRE_TRACK,Texture.class);
        assets.add(FIRE_TRACK);

        // Monster textures
        manager.load(MONSTER_TEXTURE, Texture.class);
        assets.add(MONSTER_TEXTURE);

        manager.load(FIREFLY_ANIMATE,Texture.class);
        assets.add(FIREFLY_ANIMATE);

        //HUD textures
        manager.load(HUD_WINDOW_TEXTURE,Texture.class);
        assets.add(HUD_WINDOW_TEXTURE);

        manager.load(HUD_WHITE_FIREFLY_TEXTURE, Texture.class);
        assets.add(HUD_WHITE_FIREFLY_TEXTURE);

        manager.load(HUD_WHITE_NUMBER_0, Texture.class);
        assets.add(HUD_WHITE_NUMBER_0);

        manager.load(HUD_WHITE_NUMBER_1, Texture.class);
        assets.add(HUD_WHITE_NUMBER_1);

        manager.load(HUD_WHITE_NUMBER_2, Texture.class);
        assets.add(HUD_WHITE_NUMBER_2);

        manager.load(HUD_WHITE_NUMBER_3, Texture.class);
        assets.add(HUD_WHITE_NUMBER_3);

        manager.load(HUD_WHITE_NUMBER_4, Texture.class);
        assets.add(HUD_WHITE_NUMBER_4);

        manager.load(HUD_WHITE_NUMBER_5, Texture.class);
        assets.add(HUD_WHITE_NUMBER_5);

        manager.load(HUD_WHITE_NUMBER_6, Texture.class);
        assets.add(HUD_WHITE_NUMBER_6);

        manager.load(HUD_WHITE_NUMBER_7, Texture.class);
        assets.add(HUD_WHITE_NUMBER_7);

        manager.load(HUD_WHITE_NUMBER_8, Texture.class);
        assets.add(HUD_WHITE_NUMBER_8);

        manager.load(HUD_WHITE_NUMBER_9, Texture.class);
        assets.add(HUD_WHITE_NUMBER_9);

        manager.load(HUD_WHITE_NUMBER_X, Texture.class);
        assets.add(HUD_WHITE_NUMBER_X);

        manager.load(HUD_WHITE_NUMBER_SLASH, Texture.class);
        assets.add(HUD_WHITE_NUMBER_SLASH);

        manager.load(HUD_PAW_ANIMATE, Texture.class);
        assets.add(HUD_PAW_ANIMATE);


        for(String m : MIST_WALLS){
            manager.load(m, Texture.class);
            assets.add(m);
        }

        for(String f : FAMILIARS){
            manager.load(f, Texture.class);
            assets.add(f);
        }

        for(String f : ROCKS){
            manager.load(f, Texture.class);
            assets.add(f);
        }
        for(String f : TREES){
            manager.load(f, Texture.class);
            assets.add(f);
        }
        for(String f : TREETOPS){
            manager.load(f, Texture.class);
            assets.add(f);
        }
        for(String f : ROCKTOPS){
            manager.load(f, Texture.class);
            assets.add(f);
        }
        for(String f : GORF_TEXTURES){
            manager.load(f, Texture.class);
            assets.add(f);
        }

        //music files
        manager.load(A_PEACE_SONG, Sound.class);
        assets.add(A_PEACE_SONG);
        manager.load(B_MARSH_SONG, Sound.class);
        assets.add(B_MARSH_SONG);

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
    public void loadContent(AssetManager manager, GameCanvas canvas) {
        if (rocketAssetState != AssetState.LOADING) {
            return;
        }

        litTexture=createTexture(manager,LIT_LANTERN,false);
        unlitTexture=createTexture(manager,UNLIT_LANTERN,false);
        litTextureTop=createTexture(manager,LITTOP_LANTERN,false);
        unlitTextureTop=createTexture(manager,UNLITTOP_LANTERN,false);


        //gorfHat = createTexture(manager,HAT_TEXTURE,false);
        backgroundTexture = createTexture(manager,BACKGROUND,false);
        fireflyTrack=createTexture(manager,FIRE_TRACK,false);
        monsterTexture = createTexture(manager, MONSTER_TEXTURE, false);

        HUDWindow = createTexture(manager, HUD_WINDOW_TEXTURE, false);
        HUDWhiteFirefly = createTexture(manager, HUD_WHITE_FIREFLY_TEXTURE, false);
        HUDWhiteNumber_0 = createTexture(manager, HUD_WHITE_NUMBER_0, false);
        HUDWhiteNumber_1 = createTexture(manager, HUD_WHITE_NUMBER_1, false);
        HUDWhiteNumber_2 = createTexture(manager, HUD_WHITE_NUMBER_2, false);
        HUDWhiteNumber_3 = createTexture(manager, HUD_WHITE_NUMBER_3, false);
        HUDWhiteNumber_4 = createTexture(manager, HUD_WHITE_NUMBER_4, false);
        HUDWhiteNumber_5 = createTexture(manager, HUD_WHITE_NUMBER_5, false);
        HUDWhiteNumber_6 = createTexture(manager, HUD_WHITE_NUMBER_6, false);
        HUDWhiteNumber_7 = createTexture(manager, HUD_WHITE_NUMBER_7, false);
        HUDWhiteNumber_8 = createTexture(manager, HUD_WHITE_NUMBER_8, false);
        HUDWhiteNumber_9 = createTexture(manager, HUD_WHITE_NUMBER_9, false);
        HUDWhiteNumber_x = createTexture(manager, HUD_WHITE_NUMBER_X, false);
        HUDWhiteNumber_slash = createTexture(manager, HUD_WHITE_NUMBER_SLASH, false);
        pawAnimation = createFilmStrip(manager, HUD_PAW_ANIMATE, 1, 2, 2);



        fireflyAnimation=createTexture(manager, FIREFLY_ANIMATE, false);

        for(int i=0;i<MIST_WALLS.length;i++){
            mistwalls[i]= createTexture(manager, MIST_WALLS[i], false);
        }
        for(int i=0;i<FAMILIARS.length;i++){
            familiarTex[i]= createTexture(manager,FAMILIARS[i], false);
        }
        for(int i=0;i<ROCKS.length;i++){
            rocks[i]= createTexture(manager,ROCKS[i], false);
        }
        for(int i=0;i<TREES.length;i++){
            trees[i]= createTexture(manager,TREES[i], false);
        }
        for(int i=0;i<TREETOPS.length;i++){
            treetops[i]= createTexture(manager,TREETOPS[i], false);
        }
        for(int i=0;i<ROCKTOPS.length;i++){
            rocktops[i]= createTexture(manager,ROCKTOPS[i], false);
        }
        for(int i=0; i<GORF_TEXTURES.length;i++){
            gorfTextures[i] = createTexture(manager,GORF_TEXTURES[i],false);
        }


        // allocate sounds
        sounds.allocate(manager,A_PEACE_SONG);
        sounds.allocate(manager,B_MARSH_SONG);

        super.loadContent(manager, canvas);
        tileBoard=super.getTileBoard();
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

    ArrayList<Obstacle> edgewalls = new ArrayList<Obstacle>();
    //HUD Stuff
    int pawTimer = 60;
    boolean pawTimerStart = false;

    // the number of fireflies Gorf is holding
    private static int firefly_count;
    private AIControllerS ai;
    private static BoardModel tileBoard;
    private static boolean DEAD;

    // Other game objects
    public Vector2 gorfStart = new Vector2();
    // Physics objects for the game
    /** Reference to the rocket/player avatar */
    public GorfModel gorf;
    public BoxObstacle hat;
    /** Reference to the monster */
    public ArrayList<MonsterModel> monster;


    private Familiar familiars;

    private FogController fog;
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
    private FrameBuffer fbo2;
    private TextureRegion fboRegion2;
    private FrameBuffer fbo3;
    private TextureRegion fboRegion3;


    /** All the wall objects in the world. */

    protected PooledList<Obstacle> walls  = new PooledList<Obstacle>();
    protected PooledList<Obstacle> lanterns  = new PooledList<Obstacle>();
    /** All the "tops" of rocks and trees*/
    private PooledList<EnvAsset> landmarks = new PooledList<EnvAsset>();
    /** All the non-wall objects in the world. */
    protected PooledList<Obstacle> underFog  = new PooledList<Obstacle>();





    /** Arraylist of Lantern objects */
    ArrayList<Lantern> Lanterns=new ArrayList<Lantern>();


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
        this.fireflyController=new FireflyController(fireflyAnimation, scale,tileBoard);
        this.firefly_count = 2;
        this.DEAD = false;
        this.fireflyDeathTimer=0;
        this.monster = new ArrayList<MonsterModel>();

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

        fireflyController = new FireflyController(fireflyAnimation, scale,tileBoard);
        this.firefly_count = 2;
        this.fireflyDeathTimer=0;
        world = new World(gravity,false);
        world.setContactListener(this);
        Lanterns=new ArrayList<Lantern>();
        landmarks.clear();
        walls.clear();
        underFog.clear();
        lanterns.clear();
        setComplete(false);
        setFailure(false);
        populateLevel();
        familiars.reset();
        ai = new AIControllerS(monster, gorf, tileBoard);
        countdown=120;

        fbo = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth()*2, Gdx.graphics.getHeight()*2, false);
        fboRegion = new TextureRegion(fbo.getColorBufferTexture(), Gdx.graphics.getWidth()*2, Gdx.graphics.getHeight()*2);
        fboRegion.flip(false, true);

        fbo2 = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth()*2, Gdx.graphics.getHeight()*2, false);
        fboRegion2 = new TextureRegion(fbo2.getColorBufferTexture(), Gdx.graphics.getWidth()*2, Gdx.graphics.getHeight()*2);
        fboRegion2.flip(false, true);

        fbo3 = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth()*2, Gdx.graphics.getHeight()*2, false);
        fboRegion3 = new TextureRegion(fbo3.getColorBufferTexture(), Gdx.graphics.getWidth()*2, Gdx.graphics.getHeight()*2);
        fboRegion3.flip(false, true);

        for (BoardModel.Tile[] ta: tileBoard.tiles) {
            for (BoardModel.Tile t : ta) {
                t.isFog = false;
            }
        }

        // Stop all existing instances, and then re-play
        //if (sounds.isActive("A")) {sounds.stop("A");}
        sounds.stop("B");
        sounds.play("A",A_PEACE_SONG,false);
        sounds.play("B",B_MARSH_SONG,true);
    }

    private void populateLevel() {
        /**
         * The GameController functions for Gorf-Lantern interactions
         * This includes code for incrementing and decrementing Gorf's firefly counter
         * And adds lanterns to the GameController object pool.
         */

        // Initializer
        ArrayList<BoardModel.Tile> familiarPositions=new ArrayList<BoardModel.Tile>();

        for (BoardModel.Tile[] ta: tileBoard.tiles) {
            for(BoardModel.Tile t :ta) {
                if (t.isFogSpawn) {
                    createMonster(tileBoard.getTileCenterX(t) / scale.x, tileBoard.getTileCenterY(t) / scale.y);
                }
                if (t.isLantern) {
                    Lantern l = new Lantern(tileBoard.getTileCenterX(t) / scale.x,
                            tileBoard.getTileCenterY(t) / scale.y,unlitTexture,litTexture, unlitTextureTop,
                            unlitTextureTop,scale);
                    l.setTexture(unlitTexture);
                    Lanterns.add(l);
                    addObject(l.object);
                    lanterns.add(l.object);
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
                    if(t.x<3 || t.y<3 || t.x>tileBoard.getWidth()-4 || t.y>tileBoard.getHeight()-4 ){
                        edgewalls.add(po);
                    } else {
                        walls.add(po);
                    }
                }
                if (t.hasFamiliar) {
                    familiarPositions.add(t);
                }
                if (t.isGorfStart) {
                    gorfStart = new Vector2(tileBoard.getTileCenterX(t)/scale.x, tileBoard.getTileCenterY(t)/scale.y);
                }
                if(t.hasRock !=0){
                    int num = t.hasRock-1;
                    EnvAsset rock = new EnvAsset(tileBoard.getTileCenterX(t) / scale.x,
                            tileBoard.getTileCenterY(t) / scale.y, rocks[num], rocktops[num],false, num, scale);
                    landmarks.add(rock);
                    addObject(rock.getObject());
                }
                if(t.hasTree!=0){
                    int num = t.hasTree-1;
                    EnvAsset tree = new EnvAsset(tileBoard.getTileCenterX(t) / scale.x,
                            tileBoard.getTileCenterY(t) / scale.y, trees[num], treetops[num],true, num, scale);
                    landmarks.add(tree);
                    addObject(tree.getObject());

                }
            }
        }

        /**
         * Create Gorf
         */
        float dwidth  = gorfTextures[0].getRegionWidth()/(scale.x);
        float dheight = gorfTextures[0].getRegionHeight()/(scale.y*3);
        gorf = new GorfModel(gorfStart.x, gorfStart.y, dwidth*0.75f, dheight*0.75f,gorfTextures);
        gorf.setDrawScale(scale);
        //gorf.setTexture(gorfTexture);
        addObject(gorf);
//        overFog.add(0, gorf);

        fireflyController=new FireflyController(fireflyAnimation,scale,tileBoard);

        Vector2[] familiarVectors= new Vector2[familiarPositions.size()];
        for(int k=0;k<familiarPositions.size();k++){
            familiarVectors[k]= new Vector2(familiarPositions.get(k).fx/scale.x,familiarPositions.get(k).fy/scale.y);
        }
        if(familiarVectors.length!=0) {
            familiars = new Familiar(familiarTex, familiarVectors, scale);
            addObject(familiars.object);
            underFog.add(familiars.object);
        }

        //this.ai = new AIController(monster, tileBoard, gorf, scale);
        this.ai = new AIControllerS(monster, gorf, tileBoard);
        fog = new FogController(tileBoard, canvas, super.screenSize, 2.0f, scale);


    }

    private void createMonster(float x, float y) {
        TextureRegion texture = monsterTexture;
        float dwidth  = texture.getRegionWidth()/(scale.x*2);
        float dheight = texture.getRegionHeight()/(scale.y*2);
        MonsterModel monster = new MonsterModel(x, y, dwidth, dheight);
        monster.setDensity(CRATE_DENSITY);
        monster.setFriction(CRATE_FRICTION);
        monster.setRestitution(BASIC_RESTITUTION);
        monster.setDrawScale(scale);
        monster.setName("monster");
        monster.setTexture(texture);
        this.monster.add(monster);
        addObject(monster);
//        underFog.add(monster);
        monster.getBody().setUserData("monster");
    }

    //Get the lantern at this position
    private Lantern getLantern(float x, float y){
        int xi= (int)x;
        int yi=(int)y;
        for(Lantern l : Lanterns){
            if ((Math.abs((int)l.getX() - xi ) < gorfTextures[0].getRegionWidth()/scale.x)
                    && (Math.abs((int)l.getY() - yi ) < gorfTextures[0].getRegionHeight()/scale.y))return l;
        }
        return null;
    }

    void toggle(Lantern l) {
        if (l.lit) {
            firefly_count+=2;
        } else {
            if (firefly_count >= 2) {
                firefly_count = firefly_count - 2;
            }
        }
        l.toggleLantern();
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
        int f = familiars.getNumFam();
        familiars.update(gorf);
        int f2 = familiars.getNumFam();
        if (f2 > f) {
            pawAnimation.setFrame(1);
            pawTimerStart = true;
        }

        if (pawTimerStart == true) {
            pawTimer = pawTimer - 1;
            if (pawTimer == 0) {
                pawAnimation.setFrame(0);
                pawTimer = 60;
                pawTimerStart = false;
            }
        }

        float Gorfx= gorf.getPosition().x * scale.x;
        float Gorfy= gorf.getPosition().y * scale.y;

        BoardModel.Tile gorftile= tileBoard.tiles[tileBoard.screenToBoardX(Gorfx)][tileBoard.screenToBoardY(Gorfy)];        // NOTE: got an ArrayIndexOutOfBoundsException at some obscure tile?
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

        fog.update(gorf,Lanterns,tileBoard, dt);

        float forcex = InputController.getInstance().getHorizontal();
        float forcey= InputController.getInstance().getVertical();
        float moveacc = gorf.getThrust();

        // make all movement equispeed
        Vector2 temp = new Vector2(forcex*moveacc,forcey*moveacc);
        if (temp.len()>gorf.getThrust()) {
            temp = temp.setLength(gorf.getThrust());
        }

        this.gorf.setFX(temp.x);
        this.gorf.setFY(temp.y);
        gorf.applyForce();
        gorf.updateTexture();
        wrapInBounds(gorf);

        gorf.setCollidingX(false);
        gorf.setCollidingY(false);

        ai.update(dt, world);

        //ai.setInput();
        //float forceXMonster = ai.getHorizontal();
        //float forceYMonster = ai.getVertical();
        //float monsterthrust = monster.getThrust();
        //this.monster.setFX(forceXMonster * monsterthrust);
        //this.monster.setFY(forceYMonster * monsterthrust);
        //monster.applyForce();

        firefly_counter++;
        if (firefly_counter==75) {
            firefly_counter=0;
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

//        System.out.println(tileBoard.isFog(tileBoard.screenToBoardX(gorf.getX()*scale.x), tileBoard.screenToBoardY(gorf.getY()*scale.y)));
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

        // Draw background on all sides and diagonals for wrap illusion
        canvas.resetCamera();
        canvas.getSpriteBatch().setShader(fog.getShader());

        // main canvas

        fbo.begin();
        canvas.clear();
        canvas.setShader(null);
        canvas.begin();
        canvas.setBlendState(GameCanvas.BlendState.ALPHA_BLEND);
        for(Obstacle obj : walls) {if(obj.isActive()){obj.draw(canvas);}}
        canvas.end();
        fbo.end();

        canvas.setShader(null);
        fog.prepShader(firefly_count);

        canvas.clear();
//        canvas.begin(gorf.getPosition());
        canvas.setBlendState(GameCanvas.BlendState.NO_PREMULT);

        canvas.begin(gorf.getPosition());
        canvas.draw(backgroundTexture, Color.WHITE, 0, canvas.getHeight()*2, canvas.getWidth()*2,canvas.getHeight()*2);
        canvas.draw(backgroundTexture, Color.WHITE, canvas.getWidth()*2, canvas.getHeight()*2, canvas.getWidth()*2,canvas.getHeight()*2);
        canvas.draw(backgroundTexture, Color.WHITE, 0, -canvas.getHeight()*2, canvas.getWidth()*2,canvas.getHeight()*2);
        canvas.draw(backgroundTexture, Color.WHITE, canvas.getWidth()*2, -canvas.getHeight()*2, canvas.getWidth()*2,canvas.getHeight()*2);
        canvas.draw(backgroundTexture, Color.WHITE, canvas.getWidth()*2, 0, canvas.getWidth()*2,canvas.getHeight()*2);
        canvas.draw(backgroundTexture, Color.WHITE, -canvas.getWidth()*2, -canvas.getHeight()*2, canvas.getWidth()*2,canvas.getHeight()*2);
        canvas.draw(backgroundTexture, Color.WHITE, -canvas.getWidth()*2, 0, canvas.getWidth()*2,canvas.getHeight()*2);
        canvas.draw(backgroundTexture, Color.WHITE, -canvas.getWidth()*2, canvas.getHeight()*2, canvas.getWidth()*2,canvas.getHeight()*2);
        canvas.draw(backgroundTexture, Color.WHITE, 0, 0, canvas.getWidth()*2,canvas.getHeight()*2);
        canvas.end();

        // Draw under fog
        if (gorf.getY() > DEFAULT_HEIGHT / 2f) {
            canvas.begin(gorf.getPosition().add(0,-bounds.getHeight()*2));
            canvas.setShader(null);
            for(Obstacle mon : monster) {if(mon.isActive()){mon.draw(canvas);}}
            for(EnvAsset env : landmarks){env.drawfull(canvas);}
            for(Obstacle obj : underFog) {if(obj.isActive()){obj.draw(canvas);}}
            for(Firefly f : fireflyController.fireflies) {if(f!=null &&!f.isDestroyed()){f.draw(canvas);}};
            canvas.setShader(fog.getShader());
            fog.draw(canvas, backgroundTexture, new Vector2(0,0));
            canvas.end();
        }
        if (gorf.getX() > DEFAULT_WIDTH / 2f && gorf.getY() > DEFAULT_HEIGHT / 2f) {
            canvas.begin(gorf.getPosition().add(-bounds.getWidth()*2,-bounds.getHeight()*2));
            canvas.setShader(null);
            for(Obstacle mon : monster) {if(mon.isActive()){mon.draw(canvas);}}
            for(EnvAsset env : landmarks){env.drawfull(canvas);}
            for(Obstacle obj : underFog) {if(obj.isActive()){obj.draw(canvas);}}
            for(Firefly f : fireflyController.fireflies) {if(f!=null &&!f.isDestroyed()){f.draw(canvas);}};
            canvas.setShader(fog.getShader());
            fog.draw(canvas, backgroundTexture, new Vector2(0,0));
            canvas.end();
        }
        if (gorf.getY() < DEFAULT_HEIGHT / 2f) {
            canvas.begin(gorf.getPosition().add(0,bounds.getHeight()*2));
            canvas.setShader(null);
            for(Obstacle mon : monster) {if(mon.isActive()){mon.draw(canvas);}}
            for(EnvAsset env : landmarks){env.drawfull(canvas);}
            for(Obstacle obj : underFog) {if(obj.isActive()){obj.draw(canvas);}}
            for(Firefly f : fireflyController.fireflies) {if(f!=null &&!f.isDestroyed()){f.draw(canvas);}};
            canvas.setShader(fog.getShader());
            fog.draw(canvas, backgroundTexture, new Vector2(0,0));
            canvas.end();
        }
        if (gorf.getX() > DEFAULT_WIDTH / 2f && gorf.getY() < DEFAULT_HEIGHT / 2f) {
            canvas.begin(gorf.getPosition().add(-bounds.getWidth()*2,bounds.getHeight()*2));
            canvas.setShader(null);
            for(Obstacle mon : monster) {if(mon.isActive()){mon.draw(canvas);}}
            for(EnvAsset env : landmarks){env.drawfull(canvas);}
            for(Obstacle obj : underFog) {if(obj.isActive()){obj.draw(canvas);}}
            for(Firefly f : fireflyController.fireflies) {if(f!=null &&!f.isDestroyed()){f.draw(canvas);}};
            canvas.setShader(fog.getShader());
            fog.draw(canvas, backgroundTexture, new Vector2(0,0));
            canvas.end();
        }
        if (gorf.getX() > DEFAULT_WIDTH / 2f) {
            canvas.begin(gorf.getPosition().add(-bounds.getWidth()*2,0));
            canvas.setShader(null);
            for(Obstacle mon : monster) {if(mon.isActive()){mon.draw(canvas);}}
            for(EnvAsset env : landmarks){env.drawfull(canvas);}
            for(Obstacle obj : underFog) {if(obj.isActive()){obj.draw(canvas);}}
            for(Firefly f : fireflyController.fireflies) {if(f!=null &&!f.isDestroyed()){f.draw(canvas);}};
            canvas.setShader(fog.getShader());
            fog.draw(canvas, backgroundTexture, new Vector2(0,0));
            canvas.end();
        }
        if (gorf.getX() < DEFAULT_WIDTH / 2f && gorf.getY() < DEFAULT_HEIGHT / 2f) {
            canvas.begin(gorf.getPosition().add(bounds.getWidth()*2,bounds.getHeight()*2));
            canvas.setShader(null);
            for(Obstacle mon : monster) {if(mon.isActive()){mon.draw(canvas);}}
            for(EnvAsset env : landmarks){env.drawfull(canvas);}
            for(Obstacle obj : underFog) {if(obj.isActive()){obj.draw(canvas);}}
            for(Firefly f : fireflyController.fireflies) {if(f!=null &&!f.isDestroyed()){f.draw(canvas);}};
            canvas.setShader(fog.getShader());
            fog.draw(canvas, backgroundTexture, new Vector2(0,0));
            canvas.end();
        }
        if (gorf.getX() < DEFAULT_WIDTH / 2f) {
            canvas.begin(gorf.getPosition().add(bounds.getWidth()*2,0));
            canvas.setShader(null);
            for(Obstacle mon : monster) {if(mon.isActive()){mon.draw(canvas);}}
            for(EnvAsset env : landmarks){env.drawfull(canvas);}
            for(Obstacle obj : underFog) {if(obj.isActive()){obj.draw(canvas);}}
            for(Firefly f : fireflyController.fireflies) {if(f!=null &&!f.isDestroyed()){f.draw(canvas);}};
            canvas.setShader(fog.getShader());
            fog.draw(canvas, backgroundTexture, new Vector2(0,0));
            canvas.end();
        }
        if (gorf.getX() < DEFAULT_WIDTH / 2f && gorf.getY() > DEFAULT_HEIGHT / 2f) {
            canvas.begin(gorf.getPosition().add(bounds.getWidth()*2,-bounds.getHeight()*2));
            canvas.setShader(null);
            for(Obstacle mon : monster) {if(mon.isActive()){mon.draw(canvas);}}
            for(EnvAsset env : landmarks){env.drawfull(canvas);}
            for(Obstacle obj : underFog) {if(obj.isActive()){obj.draw(canvas);}}
            for(Firefly f : fireflyController.fireflies) {if(f!=null &&!f.isDestroyed()){f.draw(canvas);}};
            canvas.setShader(fog.getShader());
            fog.draw(canvas, backgroundTexture, new Vector2(0,0));
            canvas.end();
        }


        // Draw over fog
        canvas.setShader(null);

        if (gorf.getY() > DEFAULT_HEIGHT / 2f) {
            canvas.begin(gorf.getPosition().add(0,-bounds.getHeight()*2));
            for (Obstacle obj : lanterns) { if (obj.isActive()) { obj.draw(canvas); }}
            canvas.draw(fboRegion, 0, 0);
            for (Obstacle obj : edgewalls) { if (obj.isActive()) { obj.draw(canvas); }}
            for(EnvAsset env : landmarks){env.drawtop(canvas);}
            canvas.end();
        }
        if (gorf.getX() > DEFAULT_WIDTH / 2f && gorf.getY() > DEFAULT_HEIGHT / 2f) {
            canvas.begin(gorf.getPosition().add(-bounds.getWidth()*2,-bounds.getHeight()*2));
            for (Obstacle obj : lanterns) { if (obj.isActive()) { obj.draw(canvas); }}
            canvas.draw(fboRegion, 0, 0);
            for (Obstacle obj : edgewalls) { if (obj.isActive()) { obj.draw(canvas); }}
            for(EnvAsset env : landmarks){env.drawtop(canvas);}

            canvas.end();
        }
        if (gorf.getY() < DEFAULT_HEIGHT / 2f) {
            canvas.begin(gorf.getPosition().add(0,bounds.getHeight()*2));
            for (Obstacle obj : lanterns) { if (obj.isActive()) { obj.draw(canvas); }}
            canvas.draw(fboRegion, 0, 0);
            for (Obstacle obj : edgewalls) { if (obj.isActive()) { obj.draw(canvas); }}
            for(EnvAsset env : landmarks){env.drawtop(canvas);}
            canvas.end();
        }
        if (gorf.getX() > DEFAULT_WIDTH / 2f && gorf.getY() < DEFAULT_HEIGHT / 2f) {
            canvas.begin(gorf.getPosition().add(-bounds.getWidth()*2,bounds.getHeight()*2));
            for (Obstacle obj : lanterns) { if (obj.isActive()) { obj.draw(canvas); }}
            canvas.draw(fboRegion, 0, 0);
            for (Obstacle obj : edgewalls) { if (obj.isActive()) { obj.draw(canvas); }}
            for(EnvAsset env : landmarks){env.drawtop(canvas);}
            canvas.end();
        }
        if (gorf.getX() > DEFAULT_WIDTH / 2f) {
            canvas.begin(gorf.getPosition().add(-bounds.getWidth()*2,0));
            for (Obstacle obj : lanterns) { if (obj.isActive()) { obj.draw(canvas); }}
            canvas.draw(fboRegion, 0, 0);
            for (Obstacle obj : edgewalls) { if (obj.isActive()) { obj.draw(canvas); }}
            for(EnvAsset env : landmarks){env.drawtop(canvas);}
            canvas.end();
        }
        if (gorf.getX() < DEFAULT_WIDTH / 2f && gorf.getY() < DEFAULT_HEIGHT / 2f) {
            canvas.begin(gorf.getPosition().add(bounds.getWidth()*2,bounds.getHeight()*2));

            for (Obstacle obj : lanterns) { if (obj.isActive()) { obj.draw(canvas); }}
            canvas.draw(fboRegion, 0, 0);
            for (Obstacle obj : edgewalls) { if (obj.isActive()) { obj.draw(canvas); }}
            for(EnvAsset env : landmarks){env.drawtop(canvas);}
            canvas.end();
        }
        if (gorf.getX() < DEFAULT_WIDTH / 2f) {
            canvas.begin(gorf.getPosition().add(bounds.getWidth()*2,0));
            for (Obstacle obj : lanterns) { if (obj.isActive()) { obj.draw(canvas); }}
            canvas.draw(fboRegion, 0, 0);
            for (Obstacle obj : edgewalls) { if (obj.isActive()) { obj.draw(canvas); }}
            for(EnvAsset env : landmarks){env.drawtop(canvas);}
            canvas.end();
        }
        if (gorf.getX() < DEFAULT_WIDTH / 2f && gorf.getY() > DEFAULT_HEIGHT / 2f) {
            canvas.begin(gorf.getPosition().add(bounds.getWidth()*2,-bounds.getHeight()*2));
            for (Obstacle obj : lanterns) { if (obj.isActive()) { obj.draw(canvas); }}
            canvas.draw(fboRegion, 0, 0);
            for (Obstacle obj : edgewalls) { if (obj.isActive()) { obj.draw(canvas); }}
            for(EnvAsset env : landmarks){env.drawtop(canvas);}
            canvas.end();
        }

        // Main canvas, over and under fog
        canvas.begin(gorf.getPosition());
        canvas.setShader(null);
        for(Obstacle mon : monster) {if(mon.isActive()){mon.draw(canvas);}}
        for(Obstacle obj : underFog) {if(obj.isActive()){obj.draw(canvas);}}
        for(EnvAsset env : landmarks){env.drawfull(canvas);}
        for(Firefly f : fireflyController.fireflies) {if(f!=null &&!f.isDestroyed()){f.draw(canvas);}};
        canvas.setShader(fog.getShader());
        fog.draw(canvas, backgroundTexture, new Vector2(0,0));
        canvas.setShader(null);
        for (Obstacle obj : lanterns) { if (obj.isActive()) { obj.draw(canvas); }}
        gorf.draw(canvas);


        canvas.draw(fboRegion, 0, 0);
        for (Obstacle obj : edgewalls) { if (obj.isActive()) { obj.draw(canvas); }}
        for (Lantern l : Lanterns){l.drawtop(canvas);}
        for(EnvAsset env : landmarks){env.drawtop(canvas);}
        canvas.end();

        // UI
        canvas.begin(gorf.getPosition());

        displayFont.setColor(Color.WHITE);
        canvas.draw(HUDWindow, gorf.getPosition().x * scale.x + 85.0f, gorf.getPosition().y * scale.y + 105.0f);
        canvas.draw(HUDWhiteFirefly, gorf.getPosition().x * scale.x + 117.0f, gorf.getPosition().y * scale.y + 122.0f);
        canvas.draw(HUDWhiteNumber_x, gorf.getPosition().x * scale.x + 150.0f, gorf.getPosition().y * scale.y + 129.0f);
        canvas.draw(pawAnimation, gorf.getPosition().x * scale.x + 200.0f, gorf.getPosition().y * scale.y + 125.0f);
        //canvas.draw(HUDWhiteNumber_x, ai.next_move.x * scale.x, ai.next_move.y * scale.y);
        canvas.draw(HUDWhiteNumber_slash, gorf.getPosition().x * scale.x + 254.0f, gorf.getPosition().y * scale.y + 127.0f);

        if (firefly_count / 10.0 < 1) {

            switch (firefly_count) {
                case 0: canvas.draw(HUDWhiteNumber_0, gorf.getPosition().x * scale.x + 162.0f, gorf.getPosition().y * scale.y + 127.0f);
                    break;
                case 1: canvas.draw(HUDWhiteNumber_1, gorf.getPosition().x * scale.x + 162.0f, gorf.getPosition().y * scale.y + 127.0f);
                    break;
                case 2: canvas.draw(HUDWhiteNumber_2, gorf.getPosition().x * scale.x + 162.0f, gorf.getPosition().y * scale.y + 127.0f);
                    break;
                case 3: canvas.draw(HUDWhiteNumber_3, gorf.getPosition().x * scale.x + 162.0f, gorf.getPosition().y * scale.y + 127.0f);
                    break;
                case 4: canvas.draw(HUDWhiteNumber_4, gorf.getPosition().x * scale.x + 162.0f, gorf.getPosition().y * scale.y + 127.0f);
                    break;
                case 5: canvas.draw(HUDWhiteNumber_5, gorf.getPosition().x * scale.x + 162.0f, gorf.getPosition().y * scale.y + 127.0f);
                    break;
                case 6: canvas.draw(HUDWhiteNumber_6, gorf.getPosition().x * scale.x + 162.0f, gorf.getPosition().y * scale.y + 127.0f);
                    break;
                case 7: canvas.draw(HUDWhiteNumber_7, gorf.getPosition().x * scale.x + 162.0f, gorf.getPosition().y * scale.y + 127.0f);
                    break;
                case 8: canvas.draw(HUDWhiteNumber_8, gorf.getPosition().x * scale.x + 162.0f, gorf.getPosition().y * scale.y + 127.0f);
                    break;
                case 9: canvas.draw(HUDWhiteNumber_9, gorf.getPosition().x * scale.x + 162.0f, gorf.getPosition().y * scale.y + 127.0f);
            }
        } else {
            int tensplace = firefly_count/10;
            int onesplace = firefly_count % 10;

            switch (tensplace) {
                case 1: canvas.draw(HUDWhiteNumber_1, gorf.getPosition().x * scale.x + 161.0f, gorf.getPosition().y * scale.y + 126.0f);
                    break;
                case 2: canvas.draw(HUDWhiteNumber_2, gorf.getPosition().x * scale.x + 161.0f, gorf.getPosition().y * scale.y + 126.0f);
                    break;
                case 3: canvas.draw(HUDWhiteNumber_3, gorf.getPosition().x * scale.x + 161.0f, gorf.getPosition().y * scale.y + 126.0f);
                    break;
                case 4: canvas.draw(HUDWhiteNumber_4, gorf.getPosition().x * scale.x + 161.0f, gorf.getPosition().y * scale.y + 126.0f);
                    break;
                case 5: canvas.draw(HUDWhiteNumber_5, gorf.getPosition().x * scale.x + 161.0f, gorf.getPosition().y * scale.y + 126.0f);
                    break;
                case 6: canvas.draw(HUDWhiteNumber_6, gorf.getPosition().x * scale.x + 161.0f, gorf.getPosition().y * scale.y + 126.0f);
                    break;
                case 7: canvas.draw(HUDWhiteNumber_7, gorf.getPosition().x * scale.x + 161.0f, gorf.getPosition().y * scale.y + 126.0f);
                    break;
                case 8: canvas.draw(HUDWhiteNumber_8, gorf.getPosition().x * scale.x + 161.0f, gorf.getPosition().y * scale.y + 126.0f);
                    break;
                case 9: canvas.draw(HUDWhiteNumber_9, gorf.getPosition().x * scale.x + 161.0f, gorf.getPosition().y * scale.y + 126.0f);
            }

            switch (onesplace) {
                case 0: canvas.draw(HUDWhiteNumber_0, gorf.getPosition().x * scale.x + 172.0f, gorf.getPosition().y * scale.y + 126.0f);
                    break;
                case 1: canvas.draw(HUDWhiteNumber_1, gorf.getPosition().x * scale.x + 172.0f, gorf.getPosition().y * scale.y + 126.0f);
                    break;
                case 2: canvas.draw(HUDWhiteNumber_2, gorf.getPosition().x * scale.x + 172.0f, gorf.getPosition().y * scale.y + 126.0f);
                    break;
                case 3: canvas.draw(HUDWhiteNumber_3, gorf.getPosition().x * scale.x + 172.0f, gorf.getPosition().y * scale.y + 126.0f);
                    break;
                case 4: canvas.draw(HUDWhiteNumber_4, gorf.getPosition().x * scale.x + 172.0f, gorf.getPosition().y * scale.y + 126.0f);
                    break;
                case 5: canvas.draw(HUDWhiteNumber_5, gorf.getPosition().x * scale.x + 172.0f, gorf.getPosition().y * scale.y + 126.0f);
                    break;
                case 6: canvas.draw(HUDWhiteNumber_6, gorf.getPosition().x * scale.x + 172.0f, gorf.getPosition().y * scale.y + 126.0f);
                    break;
                case 7: canvas.draw(HUDWhiteNumber_7, gorf.getPosition().x * scale.x + 172.0f, gorf.getPosition().y * scale.y + 126.0f);
                    break;
                case 8: canvas.draw(HUDWhiteNumber_8, gorf.getPosition().x * scale.x + 172.0f, gorf.getPosition().y * scale.y + 126.0f);
                    break;
                case 9: canvas.draw(HUDWhiteNumber_9, gorf.getPosition().x * scale.x + 172.0f, gorf.getPosition().y * scale.y + 126.0f);
                    break;
            }
        }

        if (familiars.getNumFam() / 10.0 < 1) {

            switch (familiars.getNumFam()) {
                case 0: canvas.draw(HUDWhiteNumber_0, gorf.getPosition().x * scale.x + 240.0f, gorf.getPosition().y * scale.y + 127.0f);
                    break;
                case 1: canvas.draw(HUDWhiteNumber_1, gorf.getPosition().x * scale.x + 240.0f, gorf.getPosition().y * scale.y + 127.0f);
                    break;
                case 2: canvas.draw(HUDWhiteNumber_2, gorf.getPosition().x * scale.x + 240.0f, gorf.getPosition().y * scale.y + 127.0f);
                    break;
                case 3: canvas.draw(HUDWhiteNumber_3, gorf.getPosition().x * scale.x + 240.0f, gorf.getPosition().y * scale.y + 127.0f);
                    break;
                case 4: canvas.draw(HUDWhiteNumber_4, gorf.getPosition().x * scale.x + 240.0f, gorf.getPosition().y * scale.y + 127.0f);
                    break;
                case 5: canvas.draw(HUDWhiteNumber_5, gorf.getPosition().x * scale.x + 240.0f, gorf.getPosition().y * scale.y + 127.0f);
                    break;
                case 6: canvas.draw(HUDWhiteNumber_6, gorf.getPosition().x * scale.x + 240.0f, gorf.getPosition().y * scale.y + 127.0f);
                    break;
                case 7: canvas.draw(HUDWhiteNumber_7, gorf.getPosition().x * scale.x + 240.0f, gorf.getPosition().y * scale.y + 127.0f);
                    break;
                case 8: canvas.draw(HUDWhiteNumber_8, gorf.getPosition().x * scale.x + 240.0f, gorf.getPosition().y * scale.y + 127.0f);
                    break;
                case 9: canvas.draw(HUDWhiteNumber_9, gorf.getPosition().x * scale.x + 240.0f, gorf.getPosition().y * scale.y + 127.0f);
                    break;
            }
        }

        switch(familiars.getPosList().length) {
            case 0: canvas.draw(HUDWhiteNumber_0, gorf.getPosition().x * scale.x + 267.0f, gorf.getPosition().y * scale.y + 127.0f);
                break;
            case 1: canvas.draw(HUDWhiteNumber_1, gorf.getPosition().x * scale.x + 267.0f, gorf.getPosition().y * scale.y + 127.0f);
                break;
            case 2: canvas.draw(HUDWhiteNumber_2, gorf.getPosition().x * scale.x + 267.0f, gorf.getPosition().y * scale.y + 127.0f);
                break;
            case 3: canvas.draw(HUDWhiteNumber_3, gorf.getPosition().x * scale.x + 267.0f, gorf.getPosition().y * scale.y + 127.0f);
                break;
            case 4: canvas.draw(HUDWhiteNumber_4, gorf.getPosition().x * scale.x + 267.0f, gorf.getPosition().y * scale.y + 127.0f);
                break;
            case 5: canvas.draw(HUDWhiteNumber_5, gorf.getPosition().x * scale.x + 267.0f, gorf.getPosition().y * scale.y + 127.0f);
                break;
            case 6: canvas.draw(HUDWhiteNumber_6, gorf.getPosition().x * scale.x + 267.0f, gorf.getPosition().y * scale.y + 127.0f);
                break;
            case 7: canvas.draw(HUDWhiteNumber_7, gorf.getPosition().x * scale.x + 267.0f, gorf.getPosition().y * scale.y + 127.0f);
                break;
            case 8: canvas.draw(HUDWhiteNumber_8, gorf.getPosition().x * scale.x + 267.0f, gorf.getPosition().y * scale.y + 127.0f);
                break;
            case 9: canvas.draw(HUDWhiteNumber_9, gorf.getPosition().x * scale.x + 267.0f, gorf.getPosition().y * scale.y + 127.0f);
                break;
        }
        canvas.end();

        // minimap
        canvas.begin(gorf.getPosition());
        // draw background texture
        canvas.draw(backgroundTexture,Color.WHITE,
                gorf.getPosition().x * scale.x + 115.0f,
                gorf.getPosition().y * scale.y - 155.0f,
                super.getMinimap().getWidth(),
                super.getMinimap().getHeight());
        // draw custom level's minimap
        canvas.draw(super.getMinimap().getTexture(),Color.WHITE,
                gorf.getPosition().x * scale.x + 115.0f,
                gorf.getPosition().y * scale.y - 155.0f,
                super.getMinimap().getWidth(),
                super.getMinimap().getHeight());
        canvas.end();
        canvas.begin(gorf.getPosition());
        // draw gorf moving representation
        super.getMinimap().draw(canvas,
                gorf.getPosition().x,
                gorf.getPosition().y,
                gorf.getPosition().x * scale.x + 115.0f,
                gorf.getPosition().y * scale.y - 155.0f);
        canvas.end();

        canvas.begin();
        // PLACEHOLDER--will be replaced by Victory screen
        if (familiars.collectAll) {
            if (countdown > 0) {
                String vic = "Victory!";
                displayFont.setColor(Color.PURPLE);
                canvas.drawText(vic, displayFont, gorf.getPosition().x * scale.x - 200.0f, gorf.getPosition().y * scale.y);
                countdown --;
            } else if (countdown==0) {
                this.setComplete(true);
            }
        }

        //PLACEHOLDER--will be replaced by game over screen
        if (DEAD) {
            if (countdown > 0) {
                String vic = "Game Over!";
                displayFont.setColor(Color.PURPLE);
                canvas.drawText(vic, displayFont, gorf.getPosition().x * scale.x - 200.0f, gorf.getPosition().y * scale.y);
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
            //canvas.endDebug();
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
        WorldManifold worldManifold = contact.getWorldManifold();

        if (body1.getUserData() == "monster" && body2 == gorf.getBody()) {
            this.DEAD = true;
        }
        if (body1 == gorf.getBody() && body2.getUserData() == "monster") {
            this.DEAD = true;
        }

        if (body1 == gorf.getBody() || body2 == gorf.getBody()) {
            if (worldManifold.getNormal().y == 0f) {
                gorf.setCollidingX(true);
            } else if (worldManifold.getNormal().x == 0f) {
                gorf.setCollidingY(true);
            }
        }

//        if (body1 == gorf.getBody() || body2 == gorf.getBody()) {
//            if (gorf.isColliding()) {
//                gorf.setCollidingTwice(true);
//            } else {
//                gorf.setColliding(true);
//            }
//        }
    }


    /**
     * Callback method for the start of a collision
     *
     * This method is called when two objects cease to touch.
     */
    public void endContact(Contact contact) {
//        System.out.println("ENDING CONTACT");
//        Body body1 = contact.getFixtureA().getBody();
//        Body body2 = contact.getFixtureB().getBody();
//        WorldManifold worldManifold = contact.getWorldManifold();
//
//        if (body1 == gorf.getBody() || body2 == gorf.getBody()) {
//            if (worldManifold.getNormal().y == 0f) {
//                gorf.setCollidingX(false);
//            } else if (worldManifold.getNormal().x == 0f) {
//                gorf.setCollidingY(false);
//            }
//        }
    }

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
