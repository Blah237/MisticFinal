package edu.cornell.gdiac.mistic;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import edu.cornell.gdiac.GameCanvas;

import java.nio.ByteBuffer;


public class FogController {
	PolygonSpriteBatch batch;
	Texture wall;
	TextureRegion fboRegion;
	String vertexShader;
	String fragmentShader;
	ShaderProgram shader;
	FrameBuffer fboA;
	FrameBuffer fboB;
	FrameBuffer temp;
	float[] fogBoard;
	float[] newFogBoard;
	float[] reachBoard;
	int[][] elementBoard;
	Array<Float> lanterns;
	float[] lanternsA;
	float fogReachX;
	float fogReachY;
	float fogReach;
	int fogDelay;
	Vector2 fogOrigin;
	private final int FBO_SIZE = 1024;
	private final int NX = 35;
	private final int NY = 35;
	private final int FOG_DELAY = 20;
	int spreadType;
	float thickness;
	float spreadCount;
	float spreadCountX;
	float spreadCountY;
	int numFireflies;

	OrthographicCamera cam;
	FPSLogger logger = new FPSLogger();


	public FogController() {
		wall = new Texture("mistic/backgroundresize.png");
		fogOrigin = new Vector2(200,100);
		fogBoard = new float[NX*NY];
		reachBoard = new float[NX*NY];
		lanterns = new Array<Float>();
		lanterns.add(5f);
		lanterns.add(10f);
		lanternsA = new float[lanterns.size];
//		lanternsA = lanterns.toArray();
		for (int i=0; i<lanterns.size; i++) {
			lanternsA[i] = lanterns.get(i).floatValue();
		}
		System.out.println(lanterns.size/2);
		int cx = (int)fogOrigin.x/(Gdx.graphics.getWidth()/NX);
		int cy = (int)fogOrigin.y/(Gdx.graphics.getHeight()/NY);
		fogBoard[cy*NX+cx] = 1.1f;
		reachBoard[cy*NX+cx] = 1;
		elementBoard = new int[NX][NY];
		for (int k = 0; k<NX; k++) {
			elementBoard[k][20] = 1;
		}
		fogReachX = 0;
		fogReachY = 0;
		fogReach = 0;
		fogDelay = 0;
		spreadType = -1;
		thickness = 1;
		numFireflies = 3;

		vertexShader = Gdx.files.internal("mistic/shaders/fog.vert.glsl").readString();
		fragmentShader = Gdx.files.internal("mistic/shaders/fog.frag.glsl").readString();
		shader = new ShaderProgram(vertexShader, fragmentShader);

		if (!shader.isCompiled()) {
			System.err.println(shader.getLog());
			System.exit(0);
		}

		if (shader.getLog().length()!=0)
			System.out.println(shader.getLog());

		shader.pedantic = false;

		shader.begin();
		shader.setUniform1fv("fogBoard", fogBoard, 0, NX*NY);
//		shader.setUniform1fv("reachBoard", reachBoard, 0, NX*NY);
		shader.setUniformf("dim", Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
//		shader.setUniformf("fogReachVec", fogReachX, fogReachY);
		shader.setUniformf("fogReach", fogReach);
//		shader.setUniformf("camOrigin", new Vector2(100,100));
		shader.setUniformf("fogOrigin", fogOrigin);
		shader.setUniform2fv("lanterns", lanternsA, 0, lanterns.size);
		shader.setUniformi("numLanterns", lanterns.size/2);
		shader.setUniformi("numFireflies", numFireflies);
		shader.end();

//		fboA = new FrameBuffer(Format.RGBA8888, FBO_SIZE, FBO_SIZE, false);
//		fboB = new FrameBuffer(Format.RGBA8888, FBO_SIZE, FBO_SIZE, false);
//		fboRegion = new TextureRegion(fboA.getColorBufferTexture());
//		fboRegion.flip(false, true);

		cam = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam.setToOrtho(false);


//		batch = new SpriteBatch();
//		batch.setShader(shader);

//		batch.setShader(shader);
//		resizeBatch(FBO_SIZE, FBO_SIZE);

//		fboA.begin();
//		batch.begin();
//		batch.draw(wall, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
//		batch.flush();
//
//		fboA.end();
//		batch.end();
//		batch.setShader(shader);
	}

	public void resize(int width, int height) {
		cam.setToOrtho(false, width, height);
		batch.setProjectionMatrix(cam.combined);
		//bind the shader, then set the uniform, then unbind the shader
		shader.begin();
		shader.setUniformf("dim", width, height);
		shader.end();
	}

	void resizeBatch(int width, int height) {
		cam.setToOrtho(false, width, height);
		batch.setProjectionMatrix(cam.combined);
	}

	public void draw(GameCanvas canvas) {



//		fboA = new FrameBuffer(Format.RGBA8888, FBO_SIZE, FBO_SIZE, false);
//		fboRegion = new TextureRegion(fboA.getColorBufferTexture());
//		fboRegion.flip(false, true);
//
//		fboA.begin();
//		batch.begin();
//		batch.draw(wall, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
//		batch.flush();
//		fboA.end();


		System.out.println("rendering");
		if (fogDelay <= 0) {
			updateFog();
			fogDelay = FOG_DELAY;
//			fogReach++;
			thickness++;
//			System.out.println(fogReach);
//			System.out.println(fogBoard[(int)(Math.floor(fogOrigin.y/Gdx.graphics.getHeight()*NY)+Math.floor(fogOrigin.y/Gdx.graphics.getWidth()*NX)+fogReach)]);
		}
		else {
			fogDelay--;
		}
		fogReach+=(1f/FOG_DELAY);
//		fogReachX+=(1f/FOG_DELAY * spreadCountX/spreadCount);
//		fogReachY+=(1f/FOG_DELAY * spreadCountY/spreadCount);
//		System.out.println(fogReachX);
//		System.out.println(fogReachY);
//		System.out.println();
//		System.out.println(fogReach);
//		updateFog();


//		fboB.begin();

		batch = canvas.getSpriteBatch();
		batch.setShader(shader);

		shader.begin();
		shader.setUniformf("fogReach", fogReach);
		shader.end();

//		Gdx.gl.glClearColor(0, 0, 0, 0);
//		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.begin();
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//		fboRegion.setTexture(fboA.getColorBufferTexture());
//		fboRegion.flip(false, true);
//		batch.draw(fboRegion, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		// use -1 to ignore.. somebody should fix this in LibGDX :\
//		batch.setBlendFunction(-1, -1);

		// setup our alpha blending to avoid blending twice
//		Gdx.gl20.glBlendFuncSeparate(GL20.GL_SRC_ALPHA,
//				GL20.GL_ONE_MINUS_SRC_ALPHA, GL20.GL_ONE, GL20.GL_ONE);


		batch.enableBlending();

		batch.draw(wall, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
//		batch.flush();
//		fboB.end();

//		batch.setShader(shader);
//		resizeBatch(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

//		fboRegion.setTexture(fboB.getColorBufferTexture());
//		fboRegion.flip(false, true);
//		batch.draw(wall, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		batch.end();

//		temp = fboA;
//		fboA = fboB;
//		fboB = temp;

		logger.log();
	}

	private void updateFog() {
		spreadCount = 0;
		spreadCountX = 0;
		spreadCountY = 0;
		newFogBoard = fogBoard.clone();
		for (int i = 0; i < NX; i++) {
			for (int j = 0; j < NY; j++) {
//				fogBoard[j*NX+i] = 1;
				if (fogBoard[j*NX+i] == 1.1f) {
					spreadFog(i,j);
					spreadCount++;
					if (spreadType == 0 || spreadType == 1 || spreadType == 2) {
						spreadCountX++;
					}
					if (spreadType == 0 || spreadType == 1 || spreadType == 3) {
						spreadCountY++;
					}
				}
			}
		}
		fogBoard = newFogBoard;
//		fogReach++;
//		System.out.println(fogBoard/[21*NX+2]);

		shader.begin();
		shader.setUniform1fv("fogBoard", fogBoard, 0, NX*NY);
//		shader.setUniform1fv("reachBoard", reachBoard, 0, NX*NY);
//		shader.setUniformf("fogReach", fogReach);
		shader.end();
	}

	private void spreadFog(int x, int y) {
		spreadType = MathUtils.random(0,2);
		if (spreadType == 0) {
			newFogBoard[y * NX + x] = 1 - elementBoard[x][y];

			int x1 = x - 1;
			int x2 = x + 1;
			int y1 = y - 1;
			int y2 = y + 1;

			if (x1 == -1) {
				x1 = NX - 1;
			} else if (x2 == NX) {
				x2 = 0;
			}
			if (y1 == -1) {
				y1 = NY - 1;
			} else if (y2 == NY) {
				y2 = 0;
			}

			newFogBoard[y1 * NX + x] = Math.max(newFogBoard[y1 * NX + x], 1.1f * (1 - elementBoard[x][y1]));
			newFogBoard[y2 * NX + x] = Math.max(newFogBoard[y2 * NX + x], 1.1f * (1 - elementBoard[x][y2]));
			newFogBoard[y * NX + x1] = Math.max(newFogBoard[y * NX + x1], 1.1f * (1 - elementBoard[x1][y]));
			newFogBoard[y * NX + x2] = Math.max(newFogBoard[y * NX + x2], 1.1f * (1 - elementBoard[x2][y]));
		}
		else if (spreadType == 1){
			int ii, jj;
			for (int i = x - 1; i <= x + 1; i++) {
				for (int j = y - 1; j <= y + 1; j++) {
					ii = i;
					jj = j;
					if (i == -1) {
						ii = NX - 1;
					} else if (i == NX) {
						ii = 0;
					}
					if (j == -1) {
						jj = NY - 1;
					} else if (j == NY) {
						jj = 0;
					}


					if (i == x && j == y) {
						newFogBoard[j * NX + i] = 1 - elementBoard[ii][jj];
					} else {
						newFogBoard[jj * NX + ii] = Math.max(newFogBoard[jj * NX + ii], 1.1f * (1 - elementBoard[ii][jj]));
						//					if(elementBoard[ii][jj]==1) {
						//						System.out.println("Obstacle here");
						//						System.out.println("fogBoard["+ii+"]["+jj+"] is now "+fogBoard[jj*NX+ii]);
						//					}
						//					newFogBoard[jj * NX + ii] = Math.max(newFogBoard[jj * NX + ii], .5f);
					}
				}
			}
		}
//		else if (spreadType == 2){
//			newFogBoard[y * NX + x] = 1 - elementBoard[x][y];
//
//			int x1 = x - 1;
//			int x2 = x + 1;
//
//			if (x1 == -1) {
//				x1 = NX - 1;
//			} else if (x2 == NX) {
//				x2 = 0;
//			}
//
//			newFogBoard[y * NX + x1] = Math.max(newFogBoard[y * NX + x1], 1.1f * (1 - elementBoard[x1][y]));
//			newFogBoard[y * NX + x2] = Math.max(newFogBoard[y * NX + x2], 1.1f * (1 - elementBoard[x2][y]));
//		} else {
//			newFogBoard[y * NX + x] = 1 - elementBoard[x][y];
//
//			int y1 = y - 1;
//			int y2 = y + 1;
//
//			if (y1 == -1) {
//				y1 = NY - 1;
//			} else if (y2 == NY) {
//				y2 = 0;
//			}
//
//			newFogBoard[y1 * NX + x] = Math.max(newFogBoard[y1 * NX + x], 1.1f * (1 - elementBoard[x][y1]));
//			newFogBoard[y2 * NX + x] = Math.max(newFogBoard[y2 * NX + x], 1.1f * (1 - elementBoard[x][y2]));
//		}
	}

	public float[] getFogBoard() {
		return fogBoard;
	}

}