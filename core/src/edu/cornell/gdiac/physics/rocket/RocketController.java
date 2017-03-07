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
package edu.cornell.gdiac.physics.rocket;

import com.badlogic.gdx.math.*;
import com.badlogic.gdx.assets.*;
import com.badlogic.gdx.audio.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.physics.box2d.*;

import edu.cornell.gdiac.util.*;
import edu.cornell.gdiac.physics.*;
import edu.cornell.gdiac.physics.obstacle.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedList;
import static com.badlogic.gdx.math.MathUtils.random;
/**
 * Gameplay specific controller for the rocket lander game.
 *
 * You will notice that asset loading is not done with static methods this time.  
 * Instance asset loading makes it easier to process our game modes in a loop, which 
 * is much more scalable. However, we still want the assets themselves to be static.
 * This is the purpose of our AssetState variable; it ensures that multiple instances
 * place nicely with the static assets.
 */
public class RocketController extends WorldController implements ContactListener {
	/** Reference to the rocket texture */
	private static final String ROCK_TEXTURE = "mistic/gorf.png";
	private static final String BACKGROUND = "mistic/backgroundresize.png";
	private static final String FIRE_FLY= "mistic/firefly.png";
	private static final String FOG_TEXTURE = "mistic/fog.png";

	/** The reference for the afterburner textures  */
	private static final String MAIN_FIRE_TEXTURE = "rocket/flames.png";
	private static final String RGHT_FIRE_TEXTURE = "rocket/flames-right.png";
	private static final String LEFT_FIRE_TEXTURE = "rocket/flames-left.png";
	/** Reference to the crate image assets */
	private static final String CRATE_PREF = "rocket/crate0";
	/** How many crate assets we have */
	private static final int MAX_CRATES = 2;

	/** The asset for the collision sound */
	private static final String  COLLISION_SOUND = "rocket/bump.mp3";
	/** The asset for the main afterburner sound */
	private static final String  MAIN_FIRE_SOUND = "rocket/afterburner.mp3";
	/** The asset for the right afterburner sound */
	private static final String  RGHT_FIRE_SOUND = "rocket/sideburner-right.mp3";
	/** The asset for the left afterburner sound */
	private static final String  LEFT_FIRE_SOUND = "rocket/sideburner-left.mp3";

	/** Texture assets for the rocket */
	private TextureRegion rocketTexture;
	private TextureRegion backgroundTexture;
	private TextureRegion fireflyTexture;
	private TextureRegion fogTexture;
	/** Texture filmstrip for the main afterburner */
	private FilmStrip mainTexture;
	/** Texture filmstrip for the main afterburner */
	private FilmStrip leftTexture;
	/** Texture filmstrip for the main afterburner */
	private FilmStrip rghtTexture;

	/** Texture assets for the crates */
	private TextureRegion[] crateTextures = new TextureRegion[MAX_CRATES];
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
		for (int ii = 0; ii < crateTextures.length; ii++) {
			manager.load(CRATE_PREF + (ii + 1) +".png", Texture.class);
			assets.add(CRATE_PREF + (ii + 1) +".png");
		}
		//Background
		manager.load(BACKGROUND, Texture.class);
		assets.add(BACKGROUND);
		//Firefly
		manager.load(FIRE_FLY, Texture.class);
		assets.add(FIRE_FLY);
		//Fog
		manager.load(FOG_TEXTURE, Texture.class);
		assets.add(FOG_TEXTURE);
		// Ship textures
		manager.load(ROCK_TEXTURE, Texture.class);
		assets.add(ROCK_TEXTURE);
		manager.load(MAIN_FIRE_TEXTURE, Texture.class);
		assets.add(MAIN_FIRE_TEXTURE);
		manager.load(LEFT_FIRE_TEXTURE, Texture.class);
		assets.add(LEFT_FIRE_TEXTURE);
		manager.load(RGHT_FIRE_TEXTURE, Texture.class);
		assets.add(RGHT_FIRE_TEXTURE);

		// Ship sounds
		manager.load(MAIN_FIRE_SOUND, Sound.class);
		assets.add(MAIN_FIRE_SOUND);
		manager.load(LEFT_FIRE_SOUND, Sound.class);
		assets.add(LEFT_FIRE_SOUND);
		manager.load(RGHT_FIRE_SOUND, Sound.class);
		assets.add(RGHT_FIRE_SOUND);
		manager.load(COLLISION_SOUND, Sound.class);
		assets.add(COLLISION_SOUND);

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

		for (int ii = 0; ii < crateTextures.length; ii++) {
			String filename = CRATE_PREF + (ii + 1) +".png";
			crateTextures[ii] = createTexture(manager,filename,false);
		}

		rocketTexture = createTexture(manager,ROCK_TEXTURE,false);
		fireflyTexture = createTexture(manager,FIRE_FLY,false);
		fogTexture = createTexture(manager,FOG_TEXTURE,true);
		mainTexture  = createFilmStrip(manager,MAIN_FIRE_TEXTURE,1,RocketModel.FIRE_FRAMES,RocketModel.FIRE_FRAMES);
		leftTexture  = createFilmStrip(manager,LEFT_FIRE_TEXTURE,1,RocketModel.FIRE_FRAMES,RocketModel.FIRE_FRAMES);
		rghtTexture  = createFilmStrip(manager,RGHT_FIRE_TEXTURE,1,RocketModel.FIRE_FRAMES,RocketModel.FIRE_FRAMES);
		backgroundTexture = createTexture(manager,BACKGROUND,false);
		SoundController sounds = SoundController.getInstance();
		sounds.allocate(manager,MAIN_FIRE_SOUND);
		sounds.allocate(manager,LEFT_FIRE_SOUND);
		sounds.allocate(manager,RGHT_FIRE_SOUND);
		sounds.allocate(manager,COLLISION_SOUND);

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

	// The positions of the fireflies
	private static final float[] Fireflies = { 14.5f, 2f, 2.5f,  9.75f, 17.5f,  15.75f};

	// the list of firefly objects' bodies
	private static ArrayList<Body> fireflyObjects = new ArrayList<Body>();
	// the list of firefly objects
	private static ArrayList<BoxObstacle> fireflyObjectsO = new ArrayList<BoxObstacle>();
	// the list of bodies that need to be removed at the end of this step
	private static LinkedList<Body> scheduledForRemoval;
	// the number of fireflies Gorf is holding
	private static int firefly_count;
	//ticks
	private static int ticks;

	// Other game objects
	/** The initial rocket position */
	private static Vector2 ROCK_POS = new Vector2(9, 8);
	/** The goal door position */
	private static Vector2 GOAL_POS = new Vector2( 6, 12);

	// Physics objects for the game
	/** Reference to the goalDoor (for collision detection) */
	private BoxObstacle goalDoor;
	/** Reference to the rocket/player avatar */
	private RocketModel rocket;
	private ArrayList<Lantern> Lanterns = new ArrayList<Lantern>();
	private BoxFog fog;
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
	public RocketController() {
		setDebug(false);
		setComplete(false);
		setFailure(false);
		world.setContactListener(this);
		this.scheduledForRemoval = new LinkedList<Body>();
		this.firefly_count = 0;
		initBoard();
		initFogBoard();
		this.ticks = 0;
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
		initBoard();
		initFogBoard();
		this.firefly_count = 0;
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
	private void makeWall(PolygonObstacle po,String pname) {
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

	private void initFog(float xpos, float ypos) {
		float[] points = {0.0f, UH, UW, UH, UW, 0.0f, 0.0f, 0.0f};
		int xUnit = (int)Math.floor(xpos / BW * UNITS_W);
		int yUnit = (int)Math.floor(ypos / BH * UNITS_H);
		Vector2 idx = new Vector2(xUnit, yUnit);

		fog = new BoxFog(points,xpos,ypos,idx);

		fog.setDrawScale(scale);
		fog.setTexture(fogTexture);
		fog.setBodyType(BodyDef.BodyType.StaticBody);
		Filter filter = new Filter();
		filter.categoryBits = 0x0002;
		filter.maskBits = 0x0004;
		fog.setFilterData(filter);

		fog.setSleepingAllowed(true);
		fog.setAwake(false);

		addObject(fog);
		fog.getBody().setUserData("fog");
	}

	private void initFogParticle(float xpos, float ypos) {
		float[] points = {0.0f, UH, UW, UH, UW, 0.0f, 0.0f, 0.0f};
		int xUnit = (int)Math.floor(xpos / BW * UNITS_W);
		int yUnit = (int)Math.floor(ypos / BH * UNITS_H);
		Vector2 idx = new Vector2(xUnit, yUnit);
		BoxFogParticle particle;

		particle = new BoxFogParticle(points,xpos,ypos,idx);
		fog.addParticle(particle);

		particle.setDrawScale(scale);
		fog.setBodyType(BodyDef.BodyType.StaticBody);
		particle.setTexture(fogTexture);
		Filter filter = new Filter();
		filter.categoryBits = 0x0002;
		filter.maskBits = 0x0004;
		particle.setFilterData(filter);

		fog.setSleepingAllowed(true);
		fog.setAwake(false);

		addObject(particle);
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
		goalDoor = new BoxObstacle(GOAL_POS.x,GOAL_POS.y,dwidth,dheight);
		goalDoor.setBodyType(BodyDef.BodyType.StaticBody);
		goalDoor.setDensity(0.0f);
		goalDoor.setFriction(0.0f);
		goalDoor.setRestitution(0.0f);
		goalDoor.setSensor(true);
		goalDoor.setDrawScale(scale);
		goalDoor.setTexture(goalTile);

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

		// diagonal positive walls
		PolygonObstacle wall14 = new PolygonObstacle(wallDp, 1.5f, 8f);
		Polylist.add(wall14);
		PolygonObstacle wall17 = new PolygonObstacle(wallDp, 22.2f, -0.5f);
		Polylist.add(wall17);
		PolygonObstacle wall21 = new PolygonObstacle(wallDp, 14.5f, 8f);
		Polylist.add(wall21);
		PolygonObstacle wall24 = new PolygonObstacle(wallDp, 22.2f, 12f);
		Polylist.add(wall24);

		// diagonal negative walls
		PolygonObstacle wall15 = new PolygonObstacle(wallDn, 4f, 8f);
		Polylist.add(wall15);
		PolygonObstacle wall16 = new PolygonObstacle(wallDn, 6.8f, 12f);
		Polylist.add(wall16);
		PolygonObstacle wall18 = new PolygonObstacle(wallDn, 17f, 8f);
		Polylist.add(wall18);
		PolygonObstacle wall19 = new PolygonObstacle(wallDn, 19.7f, 12f);
		Polylist.add(wall19);
		PolygonObstacle wall27 = new PolygonObstacle(wallDn, 9.03f, 10.3f);
		Polylist.add(wall27);
		PolygonObstacle wall31 = new PolygonObstacle(wallDn, 11.5f, 0f);
		Polylist.add(wall31);
		PolygonObstacle wall32 = new PolygonObstacle(wallDn, 14.7f, 3.5f);
		Polylist.add(wall32);

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
			} else if (dx == 0) {
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

		initFog(10,8);

		// Create ground pieces
//		PolygonObstacle obj;
//		obj = new PolygonObstacle(WALL1, 0, 0);
//		obj.setBodyType(BodyDef.BodyType.StaticBody);
//		obj.setDensity(BASIC_DENSITY);
//		obj.setFriction(BASIC_FRICTION);
//		obj.setRestitution(BASIC_RESTITUTION);
//		obj.setDrawScale(scale);
//		obj.setTexture(earthTile);
//		obj.setName("wall1");
//		addObject(obj);
//
//		obj = new PolygonObstacle(WALL2, 0, 0);
//		obj.setBodyType(BodyDef.BodyType.StaticBody);
//		obj.setDensity(BASIC_DENSITY);
//		obj.setFriction(BASIC_FRICTION);
//		obj.setRestitution(BASIC_RESTITUTION);
//		obj.setDrawScale(scale);
//		obj.setTexture(earthTile);
//		obj.setName("wall2");
//		addObject(obj);
//
//		obj = new PolygonObstacle(WALL3, 0, 0);
//		obj.setBodyType(BodyDef.BodyType.StaticBody);
//		obj.setDensity(BASIC_DENSITY);
//		obj.setFriction(BASIC_FRICTION);
//		obj.setRestitution(BASIC_RESTITUTION);
//		obj.setDrawScale(scale);
//		obj.setTexture(earthTile);
//		obj.setName("wall3");
//		addObject(obj);

		createLantern(5f,9.5f);
		createLantern(13,7f);
		createLantern(16,15);
		createLantern(26f,14);
		createLantern(5,5);


		//Create fireflies
		for (int ii = 0; ii < Fireflies.length; ii += 2) {
			createFirefly(Fireflies[ii],Fireflies[ii+1]);
		}

		// Create the rocket avatar
		dwidth  = rocketTexture.getRegionWidth()/scale.x;
		dheight = rocketTexture.getRegionHeight()/scale.y;
		rocket = new RocketModel(ROCK_POS.x, ROCK_POS.y, dwidth, dheight);
		rocket.setDrawScale(scale);
		rocket.setTexture(rocketTexture);
		rocket.setBurnerStrip(RocketModel.Burner.MAIN,  mainTexture);
		rocket.setBurnerStrip(RocketModel.Burner.LEFT,  leftTexture);
		rocket.setBurnerStrip(RocketModel.Burner.RIGHT,  rghtTexture);

		// Add the sound names
		rocket.setBurnerSound(RocketModel.Burner.MAIN,  MAIN_FIRE_SOUND);
		rocket.setBurnerSound(RocketModel.Burner.LEFT,  LEFT_FIRE_SOUND);
		rocket.setBurnerSound(RocketModel.Burner.RIGHT,  RGHT_FIRE_SOUND);
		addObject(rocket);
	}

	private void createFirefly(float x,float y){
		TextureRegion texture = fireflyTexture;
		float dwidth  = texture.getRegionWidth()/scale.x;
		float dheight = texture.getRegionHeight()/scale.y;
		BoxObstacle box = new BoxObstacle(x, y, dwidth, dheight);
		box.setDensity(CRATE_DENSITY);
		box.setFriction(CRATE_FRICTION);
		box.setRestitution(BASIC_RESTITUTION);
		box.setName("firefly"+x+y);
		box.setDrawScale(scale);
		box.setTexture(texture);
		addObject(box);
		box.getBody().setUserData("firefly");
		fireflyObjects.add(box.getBody());
		fireflyObjectsO.add(box);
	}

	private void toggleLatern(float x, float y){
		Lantern l= getLantern(x,y);
		if(l!=null) {
			l.toggle();
		}
	}

	private boolean complete(ArrayList<Lantern> al){
		for(Lantern l : Lanterns){
			if(!l.lit) return false;
		}
		return true;
	}

	//Get the latern at this position
	private Lantern getLantern(float x, float y){
		int xi= (int)x;
		int yi=(int)y;

		for(Lantern l : Lanterns){
			if ((Math.abs((int)l.x - xi ) < 3)
					&& (Math.abs((int)l.y - yi ) < 3))return l;
		}
		return null;
	}

	class Lantern{
		float x;
		float y;
		BoxObstacle bo;
		boolean lit;

		Lantern(float cx, float cy, BoxObstacle o){
		x=cx;
		y=cy;
		bo=o;
		lit=false;
		}

		void toggle(){
			if(lit){
				this.bo.setTexture(crateTextures[1]);
				lit=false;
				firefly_count++;

				ArrayList<Vector2> indices = getIndices(x,y,3);

				for (int i=0; i<indices.size(); i++) {
					board[(int)indices.get(i).x][(int)indices.get(i).y] = false;
				}

			}else {
				if (firefly_count >=1) {
					this.bo.setTexture(crateTextures[0]);
					lit = true;
					firefly_count = firefly_count - 1;

					int xUnit = (int) Math.min(Math.max(0, Math.floor(x / BW * UNITS_W)), UNITS_W-1);
					int yUnit = (int) Math.min(Math.max(0, Math.floor(y / BH * UNITS_H)), UNITS_H-1);
					board[xUnit][yUnit] = true;

					ArrayList<Vector2> indices = getIndices(xUnit,yUnit,7);
					for (int i=0; i<indices.size(); i++) {
						board[(int)indices.get(i).x][(int)indices.get(i).y] = true;
//						clearedBoard[(int)indices.get(i).x][(int)indices.get(i).y] = true;
					}
				}
			}
		}
	}
	private void createLantern(float x, float y){
		TextureRegion texture = crateTextures[1];
		float dwidth  = texture.getRegionWidth()/scale.x;
		float dheight = texture.getRegionHeight()/scale.y;
		BoxObstacle box = new BoxObstacle(x, y, dwidth, dheight);

		box.setDensity(CRATE_DENSITY);
		box.setFriction(CRATE_FRICTION);
		box.setRestitution(BASIC_RESTITUTION);
		box.setBodyType(BodyDef.BodyType.StaticBody);
		box.setName("lantern");
		box.setDrawScale(scale);
		box.setTexture(texture);
		Filter filter = new Filter();
		filter.categoryBits = 0x0002;
		filter.maskBits = 0x0004;
		box.setFilterData(filter);
		Lantern l = new Lantern(x,y,box);
		Lanterns.add(l);
		addObject(box);
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
			toggleLatern(rocket.getX(),rocket.getY());
		}
		float forcex = InputController.getInstance().getHorizontal();
		float forcey= InputController.getInstance().getVertical();
		float rocketthrust = rocket.getThrust();
		this.rocket.setFX(forcex * rocketthrust);
		this.rocket.setFY(forcey * rocketthrust);
		rocket.applyForce();
		wrapInBounds(rocket);

		if (random(250)==7) {
			createFirefly(random(40), random(20));
		}



		if (fogDelay == 0) {
			BoardTuple fogBoards = fog.expand(board, fogBoard);
			fogBoard = fogBoards.a;
			boolean[][] newFogBoard = fogBoards.b;

			for (int j = 0; j < UNITS_H; j++) {
				for (int i = 0; i < UNITS_W; i++) {
					if (newFogBoard[i][j]) {
						initFogParticle(i * UW, j * UH);
					}
				}
			}
			fogDelay = FOG_DELAY;
		} else {
			fogDelay--;
		}

		int xUnit = (int) Math.min(Math.max(0, Math.floor(rocket.getX() / BW * UNITS_W)), UNITS_W-1);
		int yUnit = (int) Math.min(Math.max(0, Math.floor(rocket.getY() / BH * UNITS_H)), UNITS_H-1);

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

		// checkInBounds here!!!
		//#endregion

		// Animate the three burners

		//updateBurner(RocketModel.Burner.MAIN, rocket.getFY() > 1);
		//updateBurner(RocketModel.Burner.LEFT, rocket.getFX() > 1);
		//updateBurner(RocketModel.Burner.RIGHT, rocket.getFX() < -1);
		ticks++;
		if (ticks > 50) {
			ticks = 0;
		}
		// If we use sound, we must remember this.
		SoundController.getInstance().update();

		for (Body b : scheduledForRemoval) {
			b.getWorld().destroyBody(b);
			fireflyObjects.remove(b);
			for (BoxObstacle o : fireflyObjectsO) {
				if (b == o.getBody()) {
					objects.remove(o);
				}
			}
		}

		scheduledForRemoval.clear();
	}

	/**
	 * Updates that animation for a single burner
	 *
	 * This method is here instead of the the rocket model because of our philosophy
	 * that models should always be lightweight.  Animation includes sounds and other
	 * assets that we do not want to process in the model
	 *+
	 * @param  burner   The rocket burner to animate
	 * @param  on       Whether to turn the animation on or off
	 */
	private void updateBurner(RocketModel.Burner burner, boolean on) {
		String sound = rocket.getBurnerSound(burner);
		if (on) {
			rocket.animateBurner(burner, true);
			if (!SoundController.getInstance().isActive(sound)) {
				SoundController.getInstance().play(sound, sound, true);
			}
		} else {
			rocket.animateBurner(burner, false);
			if (SoundController.getInstance().isActive(sound)) {
				SoundController.getInstance().stop(sound);
			}
		}
	}

	/**
	 * Function to tell if Gorf (rocket) is off screen and to wrap him around, with a
	 * 0.1f position buffer
	 *
	 * @param rocket   Gorf character
	 */
	private void wrapInBounds(RocketModel rocket) {
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

		// Draw background unscaled.
		canvas.begin();
		canvas.draw(backgroundTexture, Color.WHITE, 0, 0,canvas.getWidth(),canvas.getHeight());
		String message = "Fireflies Held: " + firefly_count;
		displayFont.setColor(Color.YELLOW);
		canvas.drawText(message, displayFont, 5.0f, canvas.getHeight()-5.0f);
		canvas.end();

		canvas.begin();
		for(Obstacle obj : objects) {
			obj.draw(canvas);
		}
		canvas.end();

		if(complete(Lanterns)){
			if(countdown > 0){
				canvas.begin();
				String vic = "Victory!";
				displayFont.setColor(Color.PURPLE);
				canvas.drawText(vic, displayFont, canvas.getWidth()/4, canvas.getHeight()/2);
				canvas.end();
				countdown --;
			}else if(countdown==0){
				this.setComplete(true);
			}

		}

		if (isDebug()) {
			canvas.beginDebug();
			for(Obstacle obj : objects) {
				obj.drawDebug(canvas);
			}
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

		if(body1 == rocket.getBody() && body2.getUserData() == "firefly") {
			scheduledForRemoval.addLast(body2);
			firefly_count++;
		} else if(body2 == rocket.getBody() && body1.getUserData() == "firefly") {
			scheduledForRemoval.addLast(body1);
			firefly_count++;
		}
		if (ticks % 5 == 0 && ticks != 0 && body1 == rocket.getBody() && body2.getUserData() == "fog") {
			if (firefly_count > 0) {
				firefly_count = firefly_count - 1;
			}
		} else if (ticks % 5 == 0 && ticks != 0 && body2 == rocket.getBody() && body1.getUserData() == "fog") {
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