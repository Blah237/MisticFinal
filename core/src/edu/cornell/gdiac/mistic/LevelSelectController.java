package edu.cornell.gdiac.mistic;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import edu.cornell.gdiac.GameCanvas;
import edu.cornell.gdiac.InputController;
import edu.cornell.gdiac.WorldController;
import edu.cornell.gdiac.util.FilmStrip;
import edu.cornell.gdiac.util.ScreenListener;

/**
 * Created by Nathaniel on 4/22/17.
 */
public class LevelSelectController extends WorldController implements Screen {
    private static final String GORF_TEXTURE = "mistic/gorfright.png";
    private static final String BACKGROUND = "mistic/levelmockup.png";
    private static final String BACKGROUND_OVERLAY = "mistic/levelmockup_wood.png";
    private static final String WHITE_MIST = "mistic/whitemist.png";
    private static final String PURPLE_MIST = "mistic/purplemist.png";


    private TextureRegion menu;
    private TextureRegion background;
    private TextureRegion gorf;
    private TextureRegion whiteMist;
    private TextureRegion purpleMist;

    public static final int LEVEL_CAP = 12;

    private int inputTimer = 20;
    private boolean timerGo = true;
    private static int level;

    private int firflyAnimateTimer = 15;

    private AssetState menuAssetState = AssetState.EMPTY;
    private ScreenListener listener;

    public static final int EXIT_TO_PLAY = 100;
    public static final int EXIT_TO_LEVEL_SELECT = 101;
    public static final int EXIT_TO_OPTIONS = 102;
    public static final int EXIT_TO_MENU = 103;

    public String jsonFileName;

    public static final String level1 = "final_release/level1_unfamiliar.json";
    public static final String level2 = "final_release/level2_thefish.json";
    public static final String level3 = "final_release/level3_lumina.json";
    public static final String level4 = "final_release/level4_hourglass.json";
    public static final String level5 = "final_release/level5_cross.json";
    public static final String level6 = "final_release/level6_rooms.json";
    public static final String level7 = "final_release/level7_misty.json";

    public static final String level1minimap = "minimaps/level1.png";
    public static final String level2minimap = "minimaps/level2.png";
    public static final String level3minimap = "minimaps/level3.png";
    public static final String level4minimap = "minimaps/level4_cross.png";
    public static final String level5minimap = "minimaps/level4_cross.png";
    public static final String level6minimap = "minimaps/level5_rooms.png";
    public static final String level7minimap = "minimaps/level6_misty.png";

    public static boolean level1complete = false;
    public static boolean level2complete = false;
    public static boolean level3complete = false;
    public static boolean level4complete = false;
    public static boolean level5complete = false;
    public static boolean level6complete = false;
    public static boolean level7complete = false;
    public static boolean level8complete = false;
    public static boolean level9complete = false;
    public static boolean level10complete = false;
    public static boolean level11complete = false;
    public static boolean level12complete = false;




    public void preLoadContent(AssetManager manager) {
        if (menuAssetState != AssetState.EMPTY) {
            return;
        }


        menuAssetState = AssetState.LOADING;

        manager.load(BACKGROUND, Texture.class);
        assets.add(BACKGROUND);

        manager.load(BACKGROUND_OVERLAY, Texture.class);
        assets.add(BACKGROUND_OVERLAY);

        manager.load(GORF_TEXTURE, Texture.class);
        assets.add(GORF_TEXTURE);

        manager.load(WHITE_MIST, Texture.class);
        assets.add(WHITE_MIST);

        manager.load(PURPLE_MIST, Texture.class);
        assets.add(PURPLE_MIST);


        super.preLoadContent(manager);
    }

    public void loadContent(AssetManager manager, GameCanvas canvas) {
        if (menuAssetState != AssetState.LOADING) {
            return;
        }

        menu = createTexture(manager, BACKGROUND, false);
        background = createTexture(manager, BACKGROUND_OVERLAY, false);
        gorf = createTexture(manager, GORF_TEXTURE, false);
        whiteMist = createTexture(manager, WHITE_MIST, false);
        purpleMist = createTexture(manager, PURPLE_MIST, false);

    }

    public LevelSelectController() {
        setDebug(false);
        setComplete(false);
        setFailure(false);
        this.level = 1;
        this.jsonFileName = level1;
    }

    public void reset() {
        Vector2 gravity = new Vector2(world.getGravity() );
        objects.clear();
        addQueue.clear();
        world.dispose();
        setComplete(false);
        setFailure(false);
        timerGo = true;
        world = new World(gravity,false);
    }

    public void update(float dt) {
        if (timerGo) { //code to slow down multiple inputs and not register all of them
            inputTimer--;
            if (inputTimer == 0) {
                timerGo = false;
                inputTimer = 20;
            }
        }
        float forcex= InputController.getInstance().getHorizontal();
        boolean pressing = InputController.getInstance().didEnter();
        boolean back = InputController.getInstance().didExit();
        boolean enter = InputController.getInstance().didEnter();
        //increase level when player presses right arrow key
        if (forcex > 0 && !timerGo) {
            timerGo = true;
            if (level != LEVEL_CAP ) {
                level++;
            } else {
                level = 1;
            }
        } else if (forcex < 0 && !timerGo) { //decrease level when player presses left arrow key
            timerGo = true;
            if (level != 1) {
                level--;
            } else {
                level = LEVEL_CAP;
            }
        }
        if (back) {
            listener.exitScreen(this, EXIT_TO_MENU); //exit to menu when player presses escape
        } else if (enter && !timerGo) {
            timerGo = true;
            switch (level) {
                case 1: WorldController.MINIMAP_FILE = level1minimap;
                        WorldController.JSON_FILE = level1; break;
                case 2: WorldController.MINIMAP_FILE = level2minimap;
                        WorldController.JSON_FILE = level2; break;
                case 3: WorldController.MINIMAP_FILE = level3minimap;
                        WorldController.JSON_FILE = level3; break;
                case 4: WorldController.MINIMAP_FILE = level4minimap;
                        WorldController.JSON_FILE = level4; break;
                case 5: WorldController.MINIMAP_FILE = level5minimap;
                        WorldController.JSON_FILE = level5; break;
                case 6: WorldController.MINIMAP_FILE = level6minimap;
                    WorldController.JSON_FILE = level6; break;
                case 7: WorldController.MINIMAP_FILE = level7minimap;
                    WorldController.JSON_FILE = level7; break;
            }
            listener.exitScreen(this, this.EXIT_TO_PLAY);
        }
        firflyAnimateTimer--;
        if (firflyAnimateTimer == 0) {
            firflyAnimateTimer = 20;
        }

    }
    @Override
    public void render(float dt) {
        super.render(dt);
        update(dt);
        draw(dt);
        boolean pressing = InputController.getInstance().didSecondary();
    }


    public void draw(float dt) {
        canvas.clear();
        canvas.resetCamera();
        canvas.begin();
        canvas.draw(background, Color.WHITE, 0, 0, canvas.getWidth() * 2, canvas.getHeight() * 2);
        canvas.draw(menu, Color.WHITE, 0, 0, canvas.getWidth() * 2, canvas.getHeight() * 2);

        switch (level) {
            case 1: canvas.draw(gorf, 450, 680); break;
            case 2: canvas.draw(gorf, 720, 635); break;
            case 3: canvas.draw(gorf, 400, 375); break;
            case 4: canvas.draw(gorf, 670, 210); break;
            case 5: canvas.draw(gorf, 920, 300); break;
            case 6: canvas.draw(gorf, 870, 560); break;
            case 7: canvas.draw(gorf, 1120, 670); break;
            case 8: canvas.draw(gorf, 1312, 821); break;
            case 9: canvas.draw(gorf, 1155, 460); break;
            case 10: canvas.draw(gorf, 1425, 279); break;
            case 11: canvas.draw(gorf, 1659, 415); break;
            case 12: canvas.draw(gorf, 1666, 741); break;
        }

        canvas.end();
    }

    public static int getLevel() {
        return level;
    }

    public static void setLevel(int the_level) {level = the_level;
        switch (level) {
            case 1: WorldController.MINIMAP_FILE = level1minimap;
                WorldController.JSON_FILE = level1; break;
            case 2: WorldController.MINIMAP_FILE = level2minimap;
                WorldController.JSON_FILE = level2; break;
            case 3: WorldController.MINIMAP_FILE = level3minimap;
                WorldController.JSON_FILE = level3; break;
            case 4: WorldController.MINIMAP_FILE = level4minimap;
                WorldController.JSON_FILE = level4; break;
            case 5: WorldController.MINIMAP_FILE = level5minimap;
                WorldController.JSON_FILE = level5; break;
            case 6: WorldController.MINIMAP_FILE = level6minimap;
                WorldController.JSON_FILE = level6; break;
            case 7: WorldController.MINIMAP_FILE = level7minimap;
                WorldController.JSON_FILE = level7; break;
        }}

    public void setScreenListener(ScreenListener listener) {
        this.listener = listener;
    }

}
