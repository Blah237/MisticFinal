package edu.cornell.gdiac.mistic;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import edu.cornell.gdiac.obstacle.BoxObstacle;

/**
 * Created by beau on 3/18/17.
 */
public class FireflyController {

    private static final float FIREFLY_DENSITY = 1.0f;
    private static final float FIREFLY_FRICTION  = 0.3f;
    private static final float FIREFLY_RESTITUTION = 0.1f;


    public void createFireflies{
        private void createFirefly(float x,float y,){
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

    }

}
