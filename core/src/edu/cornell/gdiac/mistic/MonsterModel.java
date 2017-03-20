package edu.cornell.gdiac.mistic;

import com.badlogic.gdx.physics.box2d.World;
import edu.cornell.gdiac.GameCanvas;
import edu.cornell.gdiac.obstacle.BoxObstacle;

/**
 * Created by beau on 3/18/17.
 */
public class MonsterModel extends BoxObstacle {

    /**
     * Creates a new monster at the given position.
     *
     * The size is expressed in physics units NOT pixels.  In order for
     * drawing to work properly, you MUST set the drawScale. The drawScale
     * converts the physics units to pixels.
     *
     * @param x  		Initial x position of the box center
     * @param y  		Initial y position of the box center
     * @param width		The object width in physics units
     * @param height	The object width in physics units
     */
    public MonsterModel(float x, float y, float width, float height) {
        super(x, y, width, height);
        setName("monster");
    }


    public boolean activatePhysics(World world) {
        // Get the box body from our parent class
        if (!super.activatePhysics(world)) {
            return false;
        }

        //#region INSERT CODE HERE
        // Insert code here to prevent the body from rotating
        this.setFixedRotation(true);
        //#endregion

        return true;
    }

    /**
     * Draws the physics object.
     *
     * @param canvas Drawing context
     */
    public void draw(GameCanvas canvas) {
        super.draw(canvas);
    }
}
