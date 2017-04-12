package edu.cornell.gdiac.mistic;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import edu.cornell.gdiac.GDXRoot;
import edu.cornell.gdiac.GameCanvas;
import edu.cornell.gdiac.InputController;
import edu.cornell.gdiac.WorldController;
import edu.cornell.gdiac.util.FilmStrip;
import edu.cornell.gdiac.util.ScreenListener;

/**
 * Created by Nathaniel on 4/11/17.
 */
public class MenuController extends WorldController implements Screen {
    private static final String BACKGROUND = "mistic/spritesheet_menu.png";
    private FilmStrip menu;


    private int inputTimer = 10;
    private boolean timerGo = false;

    private AssetState menuAssetState = AssetState.EMPTY;
    private ScreenListener listener;

    public static int EXIT_TO_PLAY = 100;
    public static int EXIT_TO_LEVEL_SELECT = 101;
    public static int EXIT_TO_OPTIONS = 102;

    public void preLoadContent(AssetManager manager) {
        if (menuAssetState != AssetState.EMPTY) {
            return;
        }


        menuAssetState = AssetState.LOADING;

        manager.load(BACKGROUND, Texture.class);
        assets.add(BACKGROUND);

        super.preLoadContent(manager);
    }

    public void loadContent(AssetManager manager, GameCanvas canvas) {
        if (menuAssetState != AssetState.LOADING) {
            return;
        }

        menu = createFilmStrip(manager, BACKGROUND, 2, 2, 3);

    }

    public MenuController() {
        setDebug(false);
        setComplete(false);
        setFailure(false);
    }

    public void reset() {
        Vector2 gravity = new Vector2(world.getGravity() );
        objects.clear();
        addQueue.clear();
        world.dispose();
        setComplete(false);
        setFailure(false);
        world = new World(gravity,false);
    }

    public void update(float dt) {
        if (timerGo) {
            inputTimer--;
            if (inputTimer == 0) {
                timerGo = false;
                inputTimer = 10;
            }
        }
        float forcey= InputController.getInstance().getVertical();
        if (forcey < 0 && !timerGo) {
            timerGo = true;
            if (menu.getFrame() != 2) {
                inputTimer = inputTimer - 1;
                menu.setFrame(menu.getFrame() + 1);
            } else {
                menu.setFrame(0);
            }
        } else if (forcey > 0 && !timerGo) {
            timerGo = true;
            if (menu.getFrame() != 0) {
                menu.setFrame(menu.getFrame() - 1);
            } else {
                menu.setFrame(2);
            }
        }

    }
@Override
public void render(float dt) {
        super.render(dt);
        update(dt);
        draw(dt);
    boolean pressing = InputController.getInstance().didSecondary();
    if (pressing) {
        switch (menu.getFrame()) {
            case 0: listener.exitScreen(this, EXIT_TO_PLAY);
        }
    }
}


    public void draw(float dt) {
            canvas.clear();
            canvas.resetCamera();
            canvas.begin();
            canvas.draw(menu, Color.WHITE, 0, 0, canvas.getWidth() * 2, canvas.getHeight() * 2);
            canvas.end();

    }

    public void setScreenListener(ScreenListener listener) {
        this.listener = listener;
    }
}
