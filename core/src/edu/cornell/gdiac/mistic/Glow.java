package edu.cornell.gdiac.mistic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import edu.cornell.gdiac.GameCanvas;

import java.util.ArrayList;

public class Glow {
    String vertexShader;
    String familiarFragmentShader;
    String lanternBackFragmentShader;
    String lanternFrontFragmentShader;
    String gorfFragmentShader;
    ShaderProgram familiarShader;
    ShaderProgram lanternBackShader;
    ShaderProgram lanternFrontShader;
    ShaderProgram gorfShader;

    Vector2 res;
    Vector2 screenDim;
    private final float zoom = .59f;
    Vector2 scale;

    public Glow(GameCanvas canvas, Rectangle screensize, Vector2 scale){
        vertexShader = Gdx.files.internal("mistic/shaders/fog.vert.glsl").readString();
        familiarFragmentShader = Gdx.files.internal("mistic/shaders/familiar.frag.glsl").readString();
        lanternBackFragmentShader = Gdx.files.internal("mistic/shaders/lantern_back.frag.glsl").readString();
        lanternFrontFragmentShader = Gdx.files.internal("mistic/shaders/lantern_front.frag.glsl").readString();
        gorfFragmentShader = Gdx.files.internal("mistic/shaders/gorfglow.frag.glsl").readString();

        familiarShader = new ShaderProgram(vertexShader, familiarFragmentShader);
        lanternBackShader = new ShaderProgram(vertexShader, lanternBackFragmentShader);
        lanternFrontShader = new ShaderProgram(vertexShader, lanternFrontFragmentShader);
        gorfShader = new ShaderProgram(vertexShader, gorfFragmentShader);

        res = new Vector2(canvas.getWidth(), canvas.getHeight());

        initShader(familiarShader);
        initShader(lanternBackShader);
        initShader(lanternFrontShader);
        initShader(gorfShader);

        this.scale = scale;
        screenDim = new Vector2(screensize.getWidth(), screensize.getHeight());
    }

    public void initShader(ShaderProgram shader) {
        if (!shader.isCompiled()) {
            System.err.println(shader.getLog());
            System.exit(0);
        }

        if (shader.getLog().length()!=0) {
            shader.getLog();
        }
        shader.pedantic = false;

        shader.begin();
        shader.setUniformf("res", res.x, res.y);
        shader.end();
    }

    public void prepShader(GorfModel gorf, Familiar familiar, ArrayList<Lantern> lanterns, Firefly[] fireflies, float nFireflies){
        Vector2 gorfPos = new Vector2(gorf.getX() * scale.x, gorf.getY() * scale.y);		// in pixels

        ArrayList<Lantern> litLanterns = new ArrayList<Lantern>();
        for (int i=0; i<lanterns.size(); i++) {
            if (lanterns.get(i).lit) {
                litLanterns.add(lanterns.get(i));
            }
        }
        float[] lanternsPos = new float[litLanterns.size()*2];
        for (int j=0; j<litLanterns.size(); j++) {
            lanternsPos[2*j] = (litLanterns.get(j).getX() * scale.x + scale.x/2f - (gorfPos.x - zoom * res.x / 2.0f)) / (zoom * res.x);
            lanternsPos[2*j + 1] = (litLanterns.get(j).getY() * scale.y + scale.y/2f - (gorfPos.y - zoom * res.y / 2.0f)) / (zoom * res.y);
        }

        Vector2 familiarPos = new Vector2((familiar.getX() * scale.x + scale.x/2f - (gorfPos.x - zoom * res.x / 2.0f)) / (zoom * res.x), (familiar.getY() * scale.y + scale.y/2f - (gorfPos.y - zoom * res.y / 2.0f)) / (zoom * res.y));

        float gorfRadius = 0;
        if (nFireflies > 0) {
            gorfRadius = .4f * (1f - (float) Math.exp(-nFireflies / 2f));
        }

        familiarShader.begin();
        familiarShader.setUniformf("familiarPos", familiarPos.x, familiarPos.y);
        familiarShader.end();

        lanternBackShader.begin();
        lanternBackShader.setUniform2fv("lanternsPos", lanternsPos, 0, lanternsPos.length);
        lanternBackShader.setUniformi("numLanterns", litLanterns.size());
        lanternBackShader.end();

        lanternFrontShader.begin();
        lanternFrontShader.setUniform2fv("lanternsPos", lanternsPos, 0, lanternsPos.length);
        lanternFrontShader.setUniformi("numLanterns", litLanterns.size());
        lanternFrontShader.end();

        gorfShader.begin();
        gorfShader.setUniformf("radius", gorfRadius+.3f);
        gorfShader.end();
    }

    public void draw(GameCanvas canvas, TextureRegion texRegion, Vector2 pos) {
        //System.out.println(canvas.getHeight());
        PolygonSpriteBatch batch = canvas.getSpriteBatch();
//		batch.setShader(shader);

//		batch.begin();
//		Gdx.gl.glClearColor(0, 0, 0, 0);
//		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//
        batch.enableBlending();
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        batch.draw(texRegion, pos.x, pos.y, screenDim.x, screenDim.y);
    }

    public ShaderProgram getFamiliarShader() {
        return familiarShader;
    }
    public ShaderProgram getLanternBackShader() { return  lanternBackShader; }
    public ShaderProgram getLanternFrontShader() { return  lanternFrontShader; }
    public ShaderProgram getGorfShader() { return gorfShader; }
}
