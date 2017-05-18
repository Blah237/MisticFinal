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
    String familiarBackFragmentShader;
    String familiarFrontFragmentShader;
    String lanternBackFragmentShader;
    String lanternFrontFragmentShader;
    String gorfFragmentShader;
    String fireflyFragmentShader;
    ShaderProgram familiarBackShader;
    ShaderProgram familiarFrontShader;
    ShaderProgram lanternBackShader;
    ShaderProgram lanternFrontShader;
    ShaderProgram gorfShader;
    ShaderProgram fireflyShader;

    Vector2 res;
    Vector2 screenDim;
    private final float zoom = .59f;
    Vector2 scale;

    Vector2 indicatorDir;
    float indicatorStrength;

    public Glow(GameCanvas canvas, Rectangle screensize, Vector2 scale){
        vertexShader = Gdx.files.internal("mistic/shaders/fog.vert.glsl").readString();
        familiarBackFragmentShader = Gdx.files.internal("mistic/shaders/familiar_back.frag.glsl").readString();
        familiarFrontFragmentShader = Gdx.files.internal("mistic/shaders/familiar_front.frag.glsl").readString();
        lanternBackFragmentShader = Gdx.files.internal("mistic/shaders/lantern_back.frag.glsl").readString();
        lanternFrontFragmentShader = Gdx.files.internal("mistic/shaders/lantern_front.frag.glsl").readString();
        gorfFragmentShader = Gdx.files.internal("mistic/shaders/gorfglow.frag.glsl").readString();
        fireflyFragmentShader = Gdx.files.internal("mistic/shaders/fireflies.frag.glsl").readString();

        familiarBackShader = new ShaderProgram(vertexShader, familiarBackFragmentShader);
        familiarFrontShader = new ShaderProgram(vertexShader, familiarFrontFragmentShader);
        lanternBackShader = new ShaderProgram(vertexShader, lanternBackFragmentShader);
        lanternFrontShader = new ShaderProgram(vertexShader, lanternFrontFragmentShader);
        gorfShader = new ShaderProgram(vertexShader, gorfFragmentShader);
        fireflyShader = new ShaderProgram(vertexShader, fireflyFragmentShader);

        res = new Vector2(canvas.getWidth(), canvas.getHeight());

        initShader(familiarBackShader);
        initShader(familiarFrontShader);
        initShader(lanternBackShader);
        initShader(lanternFrontShader);
        initShader(gorfShader);
        initShader(fireflyShader);

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
        for (Lantern l : lanterns) {
            if (l.lit) {
                litLanterns.add(l);
            }
        }
        float[] lanternsPos = new float[litLanterns.size()*2];
        float[] offsets = new float[litLanterns.size()*2];
        for (int i=0; i<litLanterns.size(); i++) {
            lanternsPos[2*i] = (litLanterns.get(i).getX() * scale.x + scale.x/2f - (gorfPos.x - zoom * res.x / 2.0f)) / (zoom * res.x);
            lanternsPos[2*i + 1] = (litLanterns.get(i).getY() * scale.y + scale.y/2f - (gorfPos.y - zoom * res.y / 2.0f)) / (zoom * res.y);

            offsets[2*i] = (litLanterns.get(i).getX() * scale.x + scale.x/2f) / (zoom * res.x);
            offsets[2*i + 1] = (litLanterns.get(i).getY() * scale.y + scale.y/2f) / (zoom * res.y);
        }


        float gorfRadius = 0;
        if (nFireflies > 0) {
            gorfRadius = .4f * (1f - (float) Math.exp(-nFireflies / 2f));
        }

        Vector2 familiarPos = new Vector2((familiar.getX() * scale.x + scale.x/2f - (gorfPos.x - zoom * res.x / 2.0f)) / (zoom * res.x), (familiar.getY() * scale.y + scale.y/2f - (gorfPos.y - zoom * res.y / 2.0f)) / (zoom * res.y));

        ArrayList<Firefly> spawnedFireflies = new ArrayList<Firefly>();
        for (Firefly f : fireflies) {
            if (!f.isDestroyed()) {
                spawnedFireflies.add(f);
            }
        }

        float[] firefliesPos = new float[spawnedFireflies.size()*2];
        for (int j=0; j<spawnedFireflies.size(); j++) {
            firefliesPos[2*j] = (spawnedFireflies.get(j).getX() - (gorfPos.x - zoom * res.x / 2.0f)) / (zoom * res.x);
            firefliesPos[2*j + 1] = (spawnedFireflies.get(j).getY() - (gorfPos.y - zoom * res.y / 2.0f)) / (zoom * res.y);
        }

//        float dirX = Math.min(familiarPos.x - gorfPos.x, gorfPos.x - (screenDim.x - familiarPos.x));
//        float dirY = Math.min(familiarPos.y - gorfPos.y, gorfPos.y - (screenDim.y - familiarPos.y));
//        indicatorDir = new Vector2(dirX, dirY);
//        indicatorStrength = indicatorDir.len() / (new Vector2(screenDim.x/2f, screenDim.y/2f)).len();

        familiarBackShader.begin();
        familiarBackShader.setUniformf("familiarPos", familiarPos.x, familiarPos.y);
        familiarBackShader.end();

        familiarFrontShader.begin();
        familiarFrontShader.setUniformf("familiarPos", familiarPos.x, familiarPos.y);
        familiarFrontShader.end();

        lanternBackShader.begin();
        lanternBackShader.setUniform2fv("lanternsPos", lanternsPos, 0, lanternsPos.length);
        lanternBackShader.setUniformi("numLanterns", litLanterns.size());
//        lanternBackShader.setUniformf("offset", leftOffset, botOffset);
        lanternBackShader.end();

        lanternFrontShader.begin();
        lanternFrontShader.setUniform2fv("lanternsPos", lanternsPos, 0, lanternsPos.length);
        lanternFrontShader.setUniformi("numLanterns", litLanterns.size());
//        lanternFrontShader.setUniformf("offset", leftOffset, botOffset);
        lanternFrontShader.end();

        gorfShader.begin();
        gorfShader.setUniformf("radius", gorfRadius+.2f);
        gorfShader.end();

        fireflyShader.begin();
        fireflyShader.setUniform2fv("fireflies", firefliesPos, 0, firefliesPos.length);
        fireflyShader.setUniformi("numFireflies", spawnedFireflies.size());
        fireflyShader.end();
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

    public ShaderProgram getFamiliarBackShader() { return familiarBackShader; }
    public ShaderProgram getFamiliarFrontShader() { return familiarFrontShader; }
    public ShaderProgram getLanternBackShader() { return  lanternBackShader; }
    public ShaderProgram getLanternFrontShader() { return  lanternFrontShader; }
    public ShaderProgram getGorfShader() { return gorfShader; }
    public ShaderProgram getFireflyShader() { return fireflyShader; }
}