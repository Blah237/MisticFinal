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
import java.util.ArrayList;


public class FogController extends GameController {
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
	ArrayList<Lantern> lanterns;
	float[] lanternsA;
	float fogReachX;
	float fogReachY;
	float fogReach;
	int fogDelay;
	Vector2 fogOrigin;
	private final int FBO_SIZE = 1024;

	private final int FOG_DELAY = 80;
	int spreadType;
	float thickness;
	float spreadCount;
	float spreadCountX;
	float spreadCountY;
	int numFireflies;
    private float BW = DEFAULT_WIDTH;
    private float BH = DEFAULT_HEIGHT;
	private final int NX = 35;
	private final int NY = 35;

	OrthographicCamera cam;
	FPSLogger logger = new FPSLogger();


	public FogController(int x, int y, ArrayList<Lantern> lanterns) {
		wall = new Texture("mistic/backgroundresize.png");
		fogOrigin = new Vector2(x,y);
		fogBoard = new float[NX*NY];

		int cx = (int)fogOrigin.x/(Gdx.graphics.getWidth()/NX);
		int cy = (int)fogOrigin.y/(Gdx.graphics.getHeight()/NY);

        fogBoard[cy*NX+cx] = 1.1f;
        elementBoard = new int[NX][NY];

		for (int i = 0; i<lanterns.size(); i++) {
			int lx = (int)Math.floor(lanterns.get(i).getX() / BW * NX);
			int ly = (int)Math.floor(lanterns.get(i).getY() / BH * NY);

			elementBoard[lx][ly] = 1;
			elementBoard[lx][ly+1] = 1;
			elementBoard[lx][ly-1] = 1;
		}

//		for (int k=0; k<NX/10-1; k++) {
//			elementBoard[k][NY / 2+1] = 1;
//		}
//
//		for (int a=1; a<6; a++) {
//			elementBoard[NX / 10 + a-2][NY / 2 + a] = 1;
//		}
//
//		elementBoard[NX / 10 + 3][NY / 2 + 4] = 1;
//		elementBoard[NX / 10 + 4][NY / 2 + 3] = 1;
//		elementBoard[NX / 10 + 5][NY / 2 + 2] = 1;
//
//		for(int b=0; b<NX/5-1; b++) {
//			elementBoard[b][NY / 10 + 1] = 1;
//		}
//
//		for(int b=0; b<NX/5+2; b++) {
//			elementBoard[b][NY / 10 + 7] = 1;
//		}
//
//		for(int b=0; b<NY/10+1; b++) {
//			elementBoard[NX/5-2][b] = 1;
//		}
//
//		for(int b=0; b<NY/10+7; b++) {
//			elementBoard[NX/5+1][b] = 1;
//		}
//
//		elementBoard[NX/2-1][0] = 1;
//		elementBoard[NX/2-1][1] = 1;
//		elementBoard[NX/2-1][2] = 1;
//
//		for(int b=0; b<4; b++) {
//			elementBoard[NX/2+b][2] = 1;
//		}
//
//		elementBoard[NX/2-1][3] = 1;
//		elementBoard[NX/2-2][4] = 1;
//		elementBoard[NX/2-3][5] = 1;
//
//		elementBoard[NX/2+3][10] = 1;
//		elementBoard[NX/2+2][11] = 1;
//		elementBoard[NX/2+1][12] = 1;
//		elementBoard[NX/2][13] = 1;
//
//		elementBoard[NX/2][14] = 1;
//		elementBoard[NX/2][15] = 1;
//		elementBoard[NX/2][16] = 1;
//		elementBoard[NX/2][17] = 1;
//
//		elementBoard[NX/2+3][21] = 1;
//		elementBoard[NX/2+2][20] = 1;
//		elementBoard[NX/2+1][19] = 1;
//		elementBoard[NX/2][18] = 1;
//
//		elementBoard[NX/2+4][20] = 1;
//		elementBoard[NX/2+5][19] = 1;
//		elementBoard[NX/2+6][18] = 1;
//
//
//		for(int b=0; b<5; b++) {
//			elementBoard[NX/4][NY-b-1] = 1;
//		}
//
//		for(int b=0; b<6; b++) {
//			elementBoard[NX/4+b][NY-6-b] = 1;
//		}
//
//		for(int b=0; b<10; b++) {
//			elementBoard[NX/4+5][NY-11-b] = 1;
//		}
//
//		for(int b=0; b<6; b++) {
//			elementBoard[NX/2-1][NY-b-1] = 1;
//		}
//
//		for(int b=0; b<7; b++) {
//			elementBoard[NX/2+b][NY-6] = 1;
//		}
//
//		elementBoard[NX/2+6][NY-7] = 1;
//		elementBoard[NX/2+7][NY-7] = 1;
//		elementBoard[NX/2+7][NY-8] = 1;
//		elementBoard[NX/2+8][NY-8] = 1;
//		elementBoard[NX/2+8][NY-9] = 1;
//		elementBoard[NX/2+9][NY-9] = 1;
//
//		elementBoard[NX/2+11][NY-6] = 1;
//		elementBoard[NX/2+11][NY-7] = 1;
//		elementBoard[NX/2+10][NY-7] = 1;
//		elementBoard[NX/2+10][NY-8] = 1;
//		elementBoard[NX/2+9][NY-8] = 1;
//
//		for(int b=0; b<10; b++) {
//			elementBoard[NX/2+14][NY-8-b] = 1;
//		}
//
//		for(int b=0; b<4; b++) {
//			elementBoard[NX-1-b][NY-17] = 1;
//		}
//
//		for(int b=0; b<4; b++) {
//			elementBoard[NX-1-b][NY/4+2] = 1;
//		}
//
//		for(int b=0; b<5; b++) {
//			elementBoard[NX-4][NY/4+2-b] = 1;
//		}
//
//		for(int b=0; b<5; b++) {
//			elementBoard[NX-7][NY/4+2-b] = 1;
//		}
//
//		elementBoard[NX-7][NY/4-3] = 1;
//		elementBoard[NX-8][NY/4-3] = 1;
//		elementBoard[NX-8][NY/4-4] = 1;
//		elementBoard[NX-9][NY/4-4] = 1;
//		elementBoard[NX-9][NY/4-5] = 1;
//		elementBoard[NX-10][NY/4-5] = 1;
//
//		for(int b=0; b<3; b++) {
//			elementBoard[NX-10][b] = 1;
//		}

//		for (int k = 0; k<NX; k++) {
//		    for (int l = 0; l<NY; l++) {
//		        if
//                elementBoard[k][20] = 1;
//            }
//		}

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

	public void draw(GameCanvas canvas, ArrayList<Lantern> lanterns, GorfModel gorf) {
	    int numLanterns = 0;
        lanternsA = new float[lanterns.size()*2];
        for (int i=0; i<lanterns.size(); i++) {
            if (lanterns.get(i).lit) {
                System.out.println(lanterns.get(i).getX());
                lanternsA[numLanterns*2] = lanterns.get(i).getX() / BW;
                lanternsA[numLanterns*2+1] = lanterns.get(i).getY() / BH;
                System.out.println(lanternsA[i*2]);
                System.out.println(1-lanternsA[i*2+1]);
                numLanterns++;
            }
        }

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

        System.out.println(numLanterns);
		System.out.println(lanternsA[0]);
		System.out.println(lanternsA[1]);
		shader.begin();
		shader.setUniformf("fogReach", fogReach);
        shader.setUniform2fv("lanterns", lanternsA, 0, numLanterns*2);
		shader.setUniformi("numLanterns", numLanterns);
        shader.setUniformi("numFireflies", numFireflies);
        shader.setUniformf("gorfPos", gorf.getX() / BW, gorf.getY() / BH);
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