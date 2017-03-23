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

    // Since these appear only once, we do not care about the magic numbers.
    // In an actual game, this information would go in a data file.
    // Wall vertices
    private static final float[] WALL1 = { 0.0f, 18.0f, 16.0f, 18.0f, 16.0f, 17.0f,
            8.0f, 15.0f,  1.0f, 17.0f,  2.0f,  7.0f,
            3.0f,  5.0f,  3.0f,  1.0f, 16.0f,  1.0f,
            16.0f,  0.0f,  0.0f,  0.0f};
    private static final float[] WALL2 = {32.0f, 18.0f, 32.0f,  0.0f, 16.0f,  0.0f,
            16.0f,  1.0f, 31.0f,  1.0f, 30.0f, 10.0f,
            31.0f, 16.0f, 16.0f, 17.0f, 16.0f, 18.0f};
    private static final float[] WALL3 = { 4.0f, 10.5f,  8.0f, 10.5f,
            8.0f,  9.5f,  4.0f,  9.5f};

    FireflyController fireflyController;
    int Firefly_start=4;


    // the number of fireflies Gorf is holding
    private static int firefly_count;
    //ticks
    private static int ticks;
    private static final int FIREFLY_DEATH_TIMER = 5;
    private AIController ai;
    private static BoardModel tileBoard;
    private static boolean DEAD;


    // Other game objects
    /** The initial rocket position */
    private static Vector2 ROCK_POS = new Vector2(9, 8);
    /** The goal door position */
    private static Vector2 GOAL_POS = new Vector2( 6, 12);

    // Physics objects for the game
    /** Reference to the goalDoor (for collision detection) */
    private BoxObstacle goalDoor;
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
        this.fireflyController=new FireflyController(fireflyTexture, scale);
        this.firefly_count = 0;
        initBoard();
        initFogBoard();
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
        fireflyController = new FireflyController(fireflyTexture, scale);
        for(Obstacle obj : objects) {
            obj.deactivatePhysics(world);
        }
        objects.clear();
        addQueue.clear();
        world.dispose();
        Lanterns = new ArrayList<Lantern>();
        initBoard();
        initFogBoard();
        this.firefly_count = 2;
        world = new World(gravity,false);
        world.setContactListener(this);
        setComplete(false);
        setFailure(false);
        populateLevel();
        countdown=120;

    }

    /**
     * Lays out the game geography.
     */
    private void makeWall(PolygonObstacle po, String pname) {
        po.setBodyType(BodyDef.BodyType.StaticBody);
        po.setDensity(BASIC_DENSITY);
        po.setFriction(BASIC_FRICTION);
        po.setRestitution(BASIC_RESTITUTION);
        po.setDrawScale(scale);
        po.setTexture(earthTile);
        po.setName(pname);
        addObject(po);
    }

    private void initBoard() {
        board = new boolean[UNITS_W][UNITS_H];
    }

    private void initFogBoard() {
        fogBoard = new boolean[UNITS_W][UNITS_H];
    }

    private ArrayList<Vector2> getIndices(float idxX, float idxY, int n) {
        ArrayList<Vector2> indices = new ArrayList<Vector2>();
        for (int j=-n+1; j<n; j++) {
            for (int i=-n+1; i<n; i++) {
                if (j>0) {
                    if (i<0) {
                        indices.add(new Vector2(Math.max(0,idxX+i), Math.min(idxY+j,UNITS_H-1)));
                    } else if (i>0) {
                        indices.add(new Vector2(Math.min(idxX+i,UNITS_W-1), Math.min(idxY+j,UNITS_H-1)));
                    } else {
                        indices.add(new Vector2(Math.min(Math.max(0, idxX), UNITS_W - 1), Math.min(idxY + j, UNITS_H - 1)));
                    }
                } else if (j<0) {
                    if (i<0) {
                        indices.add(new Vector2(Math.max(0,idxX+i), Math.max(0,idxY+j)));
                    } else if (i>0) {
                        indices.add(new Vector2(Math.min(idxX+i,UNITS_W-1), Math.max(0,idxY+j)));
                    } else {
                        indices.add(new Vector2(Math.min(Math.max(0, idxX), UNITS_W - 1), Math.max(0,idxY+j)));
                    }
                }
                else {
                    if (i<0) {
                        indices.add(new Vector2(Math.max(0,idxX+i), Math.min(Math.max(0,idxY),UNITS_H-1)));
                    } else if (i>0) {
                        indices.add(new Vector2(Math.min(idxX+i,UNITS_W-1), Math.min(Math.max(0,idxY),UNITS_H-1)));
                    } else {
                        indices.add(new Vector2(Math.min(Math.max(0, idxX), UNITS_W - 1), Math.min(Math.max(0,idxY),UNITS_H-1)));
                    }
                }
            }
//				indices.add(new Vector2(Math.max(0,idxX-i), Math.min(idxY+i,UNITS_H-1)));
//				indices.add(new Vector2(Math.min(Math.max(0,idxX),UNITS_W-1), Math.min(idxY+i,UNITS_H-1)));
//				indices.add(new Vector2(Math.min(idxX+i,UNITS_W-1), Math.min(idxY+i,UNITS_H-1)));
//				indices.add(new Vector2(Math.max(0,idxX-i), Math.min(Math.max(0,idxY),UNITS_H-1)));
//				indices.add(new Vector2(Math.min(idxX+i,UNITS_W-1), Math.min(Math.max(0,idxY),UNITS_H-1)));
//				indices.add(new Vector2(Math.max(0,idxX-i), Math.max(0,idxY-i)));
//				indices.add(new Vector2(Math.min(Math.max(0,idxX),UNITS_W-1), Math.max(0,idxY-i)));
//				indices.add(new Vector2(Math.min(idxX+i,UNITS_W-1), Math.max(0,idxY-i)));
        }
//		Vector2[] indices = {
//				new Vector2(Math.max(0,idxX-1), Math.min(idxY+1,UNITS_H-1)),
//				new Vector2(Math.min(Math.max(0,idxX),UNITS_W-1), Math.min(idxY+1,UNITS_H-1)),
//				new Vector2(Math.min(idxX+1,UNITS_W-1), Math.min(idxY+1,UNITS_H-1)),
//				new Vector2(Math.max(0,idxX-1), Math.min(Math.max(0,idxY),UNITS_H-1)),
//				new Vector2(Math.min(idxX+1,UNITS_W-1), Math.min(Math.max(0,idxY),UNITS_H-1)),
//				new Vector2(Math.max(0,idxX-1), Math.max(0,idxY-1)),
//				new Vector2(Math.min(Math.max(0,idxX),UNITS_W-1), Math.max(0,idxY-1)),
//				new Vector2(Math.min(idxX+1,UNITS_W-1), Math.max(0,idxY-1)),
//
//				new Vector2(Math.max(0,idxX-2), Math.min(idxY+2,UNITS_H-1)),
//				new Vector2(Math.min(Math.max(0,idxX),UNITS_W-1), Math.min(idxY+2,UNITS_H-1)),
//				new Vector2(Math.min(idxX+2,UNITS_W-1), Math.min(idxY+2,UNITS_H-1)),
//				new Vector2(Math.max(0,idxX-2), Math.min(Math.max(0,idxY),UNITS_H-1)),
//				new Vector2(Math.min(idxX+2,UNITS_W-1), Math.min(Math.max(0,idxY),UNITS_H-1)),
//				new Vector2(Math.max(0,idxX-2), Math.max(0,idxY-2)),
//				new Vector2(Math.min(Math.max(0,idxX),UNITS_W-1), Math.max(0,idxY-2)),
//				new Vector2(Math.min(idxX+2,UNITS_W-1), Math.max(0,idxY-2))
//		};

        return indices;
    }

    private void populateLevel() {
        // Add level goal
        float dwidth  = goalTile.getRegionWidth()/scale.x;
        float dheight = goalTile.getRegionHeight()/scale.y;
        //addObject(goalDoor);

        // {top left corner (LR),top left corner (UD),top right corner(LR),top right corner(UD),
        // bottom right corner(LR),bottom right corner(UD),bottom left corner(LR),bottom left corner(UD)}
        // height current gorf fits through walls: 3
        ArrayList<PolygonObstacle> Polylist = new ArrayList<PolygonObstacle>();
        final float[] wallH = {1.0f, 3.0f, 4.0f, 3.0f, 4.0f, 2.8f, 1.0f, 2.8f};
        final float[] wallV = {1.0f, 4.0f, 1.2f, 4.0f, 1.2f, 1.5f, 1.0f, 1.5f};
        final float[] wallDp = {3.7f, 3.5f, 4.0f, 3.5f, 1.5f, 1.5f, 1.2f, 1.5f};
        final float[] wallDn = {3.7f, 1.5f, 4.0f, 1.5f, 1.5f, 3.5f, 1.2f, 3.5f};

        // horizontal walls
        PolygonObstacle wall1 = new PolygonObstacle(wallH, -1, 6.5f);
        Polylist.add(wall1);
        PolygonObstacle wall2 = new PolygonObstacle(wallH, -1, 2.5f);
        Polylist.add(wall2);
        PolygonObstacle wall11 = new PolygonObstacle(wallH, -1, -0.5f);
        Polylist.add(wall11);
        PolygonObstacle wall12 = new PolygonObstacle(wallH, 1.2f, -0.5f);
        Polylist.add(wall12);
        PolygonObstacle wall13 = new PolygonObstacle(wallH, 1.2f, 2.5f);
        Polylist.add(wall13);
        PolygonObstacle wall3 = new PolygonObstacle(wallH, 28, 6.5f);
        Polylist.add(wall3);
        PolygonObstacle wall4 = new PolygonObstacle(wallH, 28, 2.5f);
        Polylist.add(wall4);
        PolygonObstacle wall22 = new PolygonObstacle(wallH, 14, 12.5f);
        Polylist.add(wall22);
        PolygonObstacle wall23 = new PolygonObstacle(wallH, 17f, 12.5f);
        Polylist.add(wall23);
        PolygonObstacle wall34 = new PolygonObstacle(wallH, 14.3f, -1.5f);
        Polylist.add(wall34);
        PolygonObstacle wall35 = new PolygonObstacle(wallH, 4f, 2.5f);
        Polylist.add(wall35);

        // vertical walls
        PolygonObstacle wall5 = new PolygonObstacle(wallV, 28, 1.5f);
        Polylist.add(wall5);
        PolygonObstacle wall6 = new PolygonObstacle(wallV, 25, 1.5f);
        Polylist.add(wall6);
        PolygonObstacle wall7 = new PolygonObstacle(wallV, 22.4f, -3);
        Polylist.add(wall7);
        PolygonObstacle wall8 = new PolygonObstacle(wallV, 7, 14);
        Polylist.add(wall8);
        PolygonObstacle wall9 = new PolygonObstacle(wallV, 4, -1.5f);
        Polylist.add(wall9);
        PolygonObstacle wall10 = new PolygonObstacle(wallV, 7, -1.5f);
        Polylist.add(wall10);
        PolygonObstacle wall20 = new PolygonObstacle(wallV, 14, 14f);
        Polylist.add(wall20);
        PolygonObstacle wall25 = new PolygonObstacle(wallV, 28, 8f);
        Polylist.add(wall25);
        PolygonObstacle wall26 = new PolygonObstacle(wallV, 28, 10.5f);
        Polylist.add(wall26);
        PolygonObstacle wall28 = new PolygonObstacle(wallV, 11.7f, 7.8f);
        Polylist.add(wall28);
        PolygonObstacle wall29 = new PolygonObstacle(wallV, 11.7f, 5.8f);
        Polylist.add(wall29);
        PolygonObstacle wall30 = new PolygonObstacle(wallV, 14.8f, 5.5f);
        Polylist.add(wall30);
        PolygonObstacle wall33 = new PolygonObstacle(wallV, 14.3f, -2.5f);
        Polylist.add(wall33);
        PolygonObstacle wall36 = new PolygonObstacle(wallV, 7f, 1f);
        Polylist.add(wall36);
        PolygonObstacle wall37 = new PolygonObstacle(wallV, 7f, 1.5f);
        Polylist.add(wall37);
//
//		// diagonal positive walls
//		PolygonObstacle wall14 = new PolygonObstacle(wallDp, 1.5f, 8f);
//		Polylist.add(wall14);
//		PolygonObstacle wall17 = new PolygonObstacle(wallDp, 22.2f, -0.5f);
//		Polylist.add(wall17);
//		PolygonObstacle wall21 = new PolygonObstacle(wallDp, 14.5f, 8f);
//		Polylist.add(wall21);
//		PolygonObstacle wall24 = new PolygonObstacle(wallDp, 22.2f, 12f);
//		Polylist.add(wall24);
//
//		// diagonal negative walls
//		PolygonObstacle wall15 = new PolygonObstacle(wallDn, 4f, 8f);
//		Polylist.add(wall15);
//		PolygonObstacle wall16 = new PolygonObstacle(wallDn, 6.8f, 12f);
//		Polylist.add(wall16);
//		PolygonObstacle wall18 = new PolygonObstacle(wallDn, 17f, 8f);
//		Polylist.add(wall18);
//		PolygonObstacle wall19 = new PolygonObstacle(wallDn, 19.7f, 12f);
//		Polylist.add(wall19);
//		PolygonObstacle wall27 = new PolygonObstacle(wallDn, 9.03f, 10.3f);
//		Polylist.add(wall27);
//		PolygonObstacle wall31 = new PolygonObstacle(wallDn, 11.5f, 0f);
//		Polylist.add(wall31);
//		PolygonObstacle wall32 = new PolygonObstacle(wallDn, 14.7f, 3.5f);
//		Polylist.add(wall32);

        for ( PolygonObstacle i : Polylist) {
            makeWall(i,"wall"+i.toString());




            float[] points = i.getPoints();
            float x0 = points[0] + i.getX();
            float y0 = points[1] + i.getY();
            float x1 = points[2] + i.getX();
            float y1 = points[3] + i.getY();
            float x2 = points[4] + i.getX();
            float y2 = points[5] + i.getY();
            float dy = y1-y2;
            float dx = x2-x1;
            int xUnit = -1;
            int yUnit = -1;
            if (x1-x0-.2f < .01) {
                for (float y = y2; y < y1; y += UH) {
                    xUnit = (int) Math.min(Math.max(0, Math.floor(x0 / BW * UNITS_W)), UNITS_W - 1);
                    yUnit = (int) Math.min(Math.max(0, Math.floor(y / BH * UNITS_H)), UNITS_H - 1);
                    board[xUnit][yUnit] = true;

                    ArrayList<Vector2> indices = getIndices(xUnit, yUnit,2);

                    for (int j=0; j<indices.size(); j++) {
                        board[(int)indices.get(j).x][(int)indices.get(j).y] = true;
                    }
                }
                for (float x=x0; x<x1; x+=UW) {
                    xUnit = (int) Math.min(Math.max(0, Math.floor(x / BW * UNITS_W)), UNITS_W-1);
                    yUnit = (int) Math.min(Math.max(0, Math.floor(y2 / BH * UNITS_H)), UNITS_H-1);
                    board[xUnit][yUnit] = true;

                    ArrayList<Vector2> indices = getIndices(xUnit, yUnit, 2);

                    for (int j=0; j<indices.size(); j++) {
                        board[(int)indices.get(j).x][(int)indices.get(j).y] = true;
                    }
                }

            } else {
                float m = -dy / dx;
                float y;
                if (x1 > x2) {
                    float temp = x1;
                    x1 = x2;
                    x2 = temp;
                }
                for (float x = x1; x < x2; x += UW) {
                    y = y2 + m * (x - x1);

                    xUnit = (int) Math.min(Math.max(0, Math.floor(x / BW * UNITS_W)), UNITS_W-1);
                    yUnit = (int) Math.min(Math.max(0, Math.floor(y / BH * UNITS_H)), UNITS_H-1);
                    board[xUnit][yUnit] = true;

                    ArrayList<Vector2> indices = getIndices(xUnit, yUnit, 2);

                    for (int j=0; j<indices.size(); j++) {
                        board[(int)indices.get(j).x][(int)indices.get(j).y] = true;
                    }
                }
            }

        }

        /**
         * Initialize Lantern locations
         */
        createLantern(5f,9.5f);
        createLantern(13,7f);
        createLantern(16,15);
        createLantern(26f,14);
        createLantern(5,5);


        /**
         * Spawn some initial fireflies
         */
        for (int ii = 0; ii < Firefly_start; ii ++) {
            createFirefly(canvas.getHeight(),canvas.getWidth());
        }

        /**
         * Create Gorf
         */
        dwidth  = gorfTexture.getRegionWidth()/scale.x;
        dheight = gorfTexture.getRegionHeight()/scale.y;
        gorf = new GorfModel(ROCK_POS.x, ROCK_POS.y, dwidth, dheight);
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
        createMonster(w, h);

        Rectangle screenSize = new Rectangle(0, 0, canvas.getWidth()*scale.x, canvas.getHeight()*scale.y);
        this.tileBoard = new BoardModel(1000, 1000, screenSize);
        this.ai = new AIController(monster, tileBoard, gorf, scale);

        fog = new FogController(400,150,Lanterns);
    }
//
//	private void createFirefly(float x,float y){
//		TextureRegion texture = fireflyTexture;
//		float dwidth  = texture.getRegionWidth()/scale.x;
//		float dheight = texture.getRegionHeight()/scale.y;
//		BoxObstacle box = new BoxObstacle(x, y, dwidth, dheight);
//		box.setDensity(CRATE_DENSITY);
//		box.setFriction(CRATE_FRICTION);
//		box.setRestitution(BASIC_RESTITUTION);
//		box.setName("firefly"+x+y);
//		box.setDrawScale(scale);
//		box.setTexture(texture);
//		addObject(box);
//		box.getBody().setUserData("firefly");
//		fireflyObjects.add(box.getBody());
//		fireflyObjectsO.add(box);
//	}

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

    private void toggleLatern(float x, float y) {
        Lantern l= getLantern(x,y);
        if(l!=null) {
            toggle(l);
        }
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
            if ((Math.abs((int)l.getX() - xi ) < 3)
                    && (Math.abs((int)l.getY() - yi ) < 3))return l;
        }
        return null;
    }

    void toggle(Lantern l) {
        if (l.lit) {
            firefly_count++;
            l.setTexture(unlitTexture);
        } else {
            l.setTexture(litTexture);
            if (firefly_count >= 1) {
                firefly_count = firefly_count - 1;
            }
        }
        l.toggleLantern();
    }

    private void createLantern(float x, float y){
        Lantern l = new Lantern(x,y,unlitTexture,litTexture,scale);
        l.setTexture(unlitTexture);
        Lanterns.add(l);
        addObject(l.object);
    }

    private void createFirefly(float x, float y){
        fireflyController.spawn(x,y);
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
            Lantern l=getLantern(gorf.getX(),gorf.getY());
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

        ai.setInput();
        float forceXMonster = ai.getHorizontal();
        float forceYMonster = ai.getVertical();
        float monsterthrust = monster.getThrust();
        this.monster.setFX(forceXMonster * monsterthrust);
        this.monster.setFY(forceYMonster * monsterthrust);
        monster.applyForce();



        if (random(250)==7) {
            createFirefly(canvas.getHeight(),canvas.getWidth());

        }

        int xUnit = (int) Math.min(Math.max(0, Math.floor(gorf.getX() / BW * UNITS_W)), UNITS_W-1);
        int yUnit = (int) Math.min(Math.max(0, Math.floor(gorf.getY() / BH * UNITS_H)), UNITS_H-1);

        if (fogBoard[xUnit][yUnit]) {
            if (fireflyDelay == 0) {
                if (firefly_count > 0) {
                    firefly_count--;
                }
                fireflyDelay = FIREFLY_DELAY;
            } else {
                fireflyDelay--;
            }
        } else { fireflyDelay = FIREFLY_DELAY; }

        SoundController.getInstance().update();

        if(fireflyController.update(gorf.getX(),gorf.getY())){
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
                rocket.setPosition(bounds.getX()+bounds.getWidth()-0.1f,currentPos.y);
            } else if (currentPos.x>=bounds.getX()+bounds.getWidth()) {
                rocket.setPosition(bounds.getX()+0.1f,currentPos.y);
            }
            if (currentPos.y<=bounds.getY()) {
                rocket.setPosition(currentPos.x,bounds.getY()+bounds.getHeight()-0.1f);
            } else if (currentPos.y>=bounds.getY()+bounds.getHeight()) {
                rocket.setPosition(currentPos.x,bounds.getY()+0.1f);
            }
        }
    }

    public void draw(float dt) {
        canvas.clear();

        canvas.begin();
        canvas.draw(backgroundTexture, Color.WHITE, 0, 0,canvas.getWidth(),canvas.getHeight());
        canvas.end();

        fog.draw(canvas, Lanterns, gorf, firefly_count);

        canvas.begin();
        canvas.draw(fireflyTrack,0,0);
        displayFont.setColor(Color.WHITE);
        canvas.drawText(Integer.toString(firefly_count),displayFont,50,50);
        canvas.end();

        // Draw background on all sides and diagonals for wrap illusion
        canvas.begin();
        canvas.draw(backgroundTexture, Color.WHITE, 0, canvas.getHeight(),canvas.getWidth(),canvas.getHeight());
        canvas.draw(backgroundTexture, Color.WHITE, canvas.getWidth(), canvas.getHeight(),canvas.getWidth(),canvas.getHeight());
        canvas.draw(backgroundTexture, Color.WHITE, 0, -canvas.getHeight(),canvas.getWidth(),canvas.getHeight());
        canvas.draw(backgroundTexture, Color.WHITE, canvas.getWidth(), -canvas.getHeight(),canvas.getWidth(),canvas.getHeight());
        canvas.draw(backgroundTexture, Color.WHITE, canvas.getWidth(), 0,canvas.getWidth(),canvas.getHeight());
        canvas.draw(backgroundTexture, Color.WHITE, -canvas.getWidth(), -canvas.getHeight(),canvas.getWidth(),canvas.getHeight());
        canvas.draw(backgroundTexture, Color.WHITE, -canvas.getWidth(), 0,canvas.getWidth(),canvas.getHeight());
        canvas.draw(backgroundTexture, Color.WHITE, -canvas.getWidth(), canvas.getHeight(),canvas.getWidth(),canvas.getHeight());
        canvas.end();

        // now draw objects on current canvas that centered Gorf is actually in
        canvas.begin(gorf.getPosition());
        for(Obstacle obj : objects) {
            if(obj.isActive()){
                obj.draw(canvas);
            }
        }
        for(Firefly f : fireflyController.fireflies) {
            if(!f.isDestroyed()){
                f.getObject().draw(canvas);
            }
        }
        canvas.end();

        if (complete(Lanterns)) {
            if (countdown > 0) {
                canvas.begin();
                String vic = "Victory!";
                displayFont.setColor(Color.PURPLE);
                canvas.drawText(vic, displayFont, canvas.getWidth()/4, canvas.getHeight()/2);
                canvas.end();
                countdown --;
            } else if (countdown==0) {
                this.setComplete(true);
            }
        }

        if (DEAD) {
            if (countdown > 0) {
                canvas.begin();
                String vic = "Game Over!";
                displayFont.setColor(Color.PURPLE);
                canvas.drawText(vic, displayFont, canvas.getWidth()/4, canvas.getHeight()/2);
                canvas.end();
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
        }



        if (ticks % FIREFLY_DEATH_TIMER == 0 && ticks != 0 && body1 == gorf.getBody() && body2.getUserData() == "fog") {
            if (firefly_count > 0) {
                firefly_count = firefly_count - 1;
            }
        } else if (ticks % FIREFLY_DEATH_TIMER == 0 && ticks != 0 && body2 == gorf.getBody() && body1.getUserData() == "fog") {
            if (firefly_count > 0) {
                firefly_count = firefly_count - 1;
            }
        }
    }

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
