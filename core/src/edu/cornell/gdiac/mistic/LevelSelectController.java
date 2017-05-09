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
    private TextureRegion menu;
    private TextureRegion background;
    private TextureRegion gorf;

    private static final int LEVEL_CAP = 12;

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

    public final String level1 = "jsons/BETA_basic_blockfog.json";
    public final String level2 = "jsons/BETA_easy_containfog_1fam.json";
    public final String level3 = "jsons/level3line.json";
    public final String level4 = "jsons/BETA_two_sides_3fam.json";
    public final String level5 = "jsons/BETA_rooms2.json";

    public final String level1minimap = "minimaps/BETA_basic_blockfog.png";
    public final String level2minimap = "minimaps/BETA_easy_containfog_1fam.png";
    public final String level3minimap = "minimaps/BETA_two_sides.png";
    public final String level4minimap = "minimaps/BETA_two_sides_3fam.png";
    public final String level5minimap = "minimaps/BETA_rooms2.png";



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


        super.preLoadContent(manager);
    }

    public void loadContent(AssetManager manager, GameCanvas canvas) {
        if (menuAssetState != AssetState.LOADING) {
            return;
        }

        menu = createTexture(manager, BACKGROUND, false);
        background = createTexture(manager, BACKGROUND_OVERLAY, false);
        gorf = createTexture(manager, GORF_TEXTURE, false);

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
            case 1: canvas.draw(gorf, 391, 720); break;
            case 2: canvas.draw(gorf, 660, 700); break;
            case 3: canvas.draw(gorf, 330, 385); break;
            case 4: canvas.draw(gorf, 590, 210); break;
            case 5: canvas.draw(gorf, 882, 325); break;
            case 6: canvas.draw(gorf, 825, 593); break;
            case 7: canvas.draw(gorf, 1094, 678); break;
            case 8: canvas.draw(gorf, 1305, 827); break;
            case 9: canvas.draw(gorf, 1132, 465); break;
            case 10: canvas.draw(gorf, 1420, 284); break;
            case 11: canvas.draw(gorf, 1661, 421); break;
            case 12: canvas.draw(gorf, 1666, 751); break;
        }

        canvas.end();
    }

    public int getLevel() {
        return level;
    }

    public void setScreenListener(ScreenListener listener) {
        this.listener = listener;
    }

}
