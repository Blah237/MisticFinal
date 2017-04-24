package edu.cornell.gdiac.mistic;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
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
    private static final String FIREFLY = "mistic/spritesheet_firefly_menu.png";
    private FilmStrip firefly;

    private int inputTimer = 20;
    private boolean timerGo = false;

    private int firflyAnimateTimer = 15;

    private AssetState menuAssetState = AssetState.EMPTY;
    private ScreenListener listener;

    public static final int EXIT_TO_PLAY = 100;
    public static final int EXIT_TO_LEVEL_SELECT = 101;
    public static final int EXIT_TO_OPTIONS = 102;

    public void preLoadContent(AssetManager manager) {
        if (menuAssetState != AssetState.EMPTY) {
            return;
        }


        menuAssetState = AssetState.LOADING;

        manager.load(BACKGROUND, Texture.class);
        assets.add(BACKGROUND);

        manager.load(FIREFLY, Texture.class);
        assets.add(FIREFLY);

        super.preLoadContent(manager);
    }

    public void loadContent(AssetManager manager, GameCanvas canvas) {
        if (menuAssetState != AssetState.LOADING) {
            return;
        }

        menu = createFilmStrip(manager, BACKGROUND, 2, 2, 3);
        firefly = createFilmStrip(manager, FIREFLY, 1, 15, 15);

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
                inputTimer = 20;
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
    if (pressing) {
        switch (menu.getFrame()) {
            case 0: listener.exitScreen(this, EXIT_TO_PLAY); break;
            case 1: listener.exitScreen(this, EXIT_TO_LEVEL_SELECT); break;
        }
    }
}


    public void draw(float dt) {
        canvas.clear();
        canvas.resetCamera();
        canvas.begin();
        canvas.draw(menu, Color.WHITE, 0, 0, canvas.getWidth() * 2, canvas.getHeight() * 2);
            switch (menu.getFrame()) {
                case 0: canvas.draw(firefly, canvas.getWidth() / 2.0f - 270.0f, canvas.getHeight() / 2.0f + 140.0f);
                    break;
                case 1: canvas.draw(firefly, canvas.getWidth() / 2.0f - 270.0f, canvas.getHeight() / 2.0f + 15.0f);
                    break;
                case 2: canvas.draw(firefly, canvas.getWidth() / 2.0f - 270.0f, canvas.getHeight() / 2.0f - 110.0f);
                    break;
            }
            if (firflyAnimateTimer == 1) {
                if (firefly.getFrame() != firefly.getSize() - 1) {
                    firefly.setFrame(firefly.getFrame() + 1);
                } else {
                    firefly.setFrame(0);
                }
            }

            canvas.end();

    }

    public void setScreenListener(ScreenListener listener) {
        this.listener = listener;
    }
}
