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
import edu.cornell.gdiac.WorldController;

import java.nio.ByteBuffer;
import java.util.ArrayList;


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
	float[][] fogBoard;
	float[][] newFogBoard;
	float[] fogBoardCam;
	float[] reachBoard;
	BoardModel tileBoard;
	float[][] elementBoard;
	float[][] elementBoardCam;
	ArrayList<Lantern> lanterns;
	float[] lanternsA;
	float fogReachX;
	float fogReachY;
	float fogReach;
	int fogDelay;
	Vector2 fogOrigin;
	Vector2 gorfPos;
	private final int FBO_SIZE = 1024;

	private final int FOG_DELAY = 60;
	int spreadType;
	float thickness;
	float spreadCount;
	float spreadCountX;
	float spreadCountY;
//    private float BW = DEFAULT_WIDTH;
//    private float BH = DEFAULT_HEIGHT;
	private final int NX = 35;
	private final int NY = 35;

	private final int BW = 64;
	private final int BH = 36;

	private int WX;
	private int WY;



	OrthographicCamera cam;
	FPSLogger logger = new FPSLogger();


	public FogController(int x, int y, ArrayList<Lantern> lanterns, BoardModel tileBoard) {
		wall = new Texture("mistic/backgroundresize.png");
		fogOrigin = new Vector2(x,y);

		this.tileBoard = tileBoard;

		WX = tileBoard.getWidth();
		WY = tileBoard.getHeight();
		fogBoard = new float[WX][WY];
		fogBoardCam = new float[NX*NY];

		elementBoard = new float[WX][WY];

		int ox = (int)(fogOrigin.x / Gdx.graphics.getWidth() * WX);
		int oy = (int)(fogOrigin.y / Gdx.graphics.getHeight() * WY);

		fogBoard[ox][oy] = 1.1f;


		for (int i=0; i<WX; i++) {
			for (int j=0; j<WY; j++) {
				if (tileBoard.isWall(j,i)) {
					elementBoard[i][j] = .9f;
				} else if (tileBoard.isLantern(j,i)) {
					elementBoard[i][j] = .5f;
				}
			}
		}

//		for (int i=0; i<WX; i++) {
//			elementBoard[(int)WY/4][i] = .9f;
//		}


//		int cx = (int) Math.floor(i / WX * NX);
//		int cy = (int) Math.floor(i / WY * NY);


//		for (int i = 0; i<lanterns.size(); i++) {
//			int lx = (int)Math.floor(lanterns.get(i).getX() / BW * NX);
//			int ly = (int)Math.floor(lanterns.get(i).getY() / BH * NY);
//
//			elementBoard[lx][ly] = 1;
//			elementBoard[lx][ly+1] = 1;
//			elementBoard[lx][ly-1] = 1;
//		}


		fogReachX = 0;
		fogReachY = 0;
		fogReach = 0;
		fogDelay = 0;
		spreadType = -1;
		thickness = 1;

		vertexShader = Gdx.files.internal("mistic/fog.vert.glsl").readString();
		fragmentShader = Gdx.files.internal("mistic/fog.frag.glsl").readString();
		shader = new ShaderProgram(vertexShader, fragmentShader);

		if (!shader.isCompiled()) {
			System.err.println(shader.getLog());
			System.exit(0);
		}

		if (shader.getLog().length()!=0)
			System.out.println(shader.getLog());

		shader.pedantic = false;

		shader.begin();
		shader.setUniform1fv("fogBoard", fogBoardCam, 0, NX*NY);
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

//		cam = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
//		cam.setToOrtho(false);


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

	public void draw(GameCanvas canvas, int numFireflies) {


		batch = canvas.getSpriteBatch();
		batch.setShader(shader);

//        System.out.println(numLanterns);
//		System.out.println(lanternsA[0]);
//		System.out.println(lanternsA[1]);
		shader.begin();
		shader.setUniform1fv("fogBoard", fogBoardCam, 0, NX*NY);
		shader.setUniformf("fogReach", fogReach);
//        shader.setUniform2fv("lanterns", lanternsA, 0, numLanterns*2);
//		shader.setUniformi("numLanterns", numLanterns);
        shader.setUniformi("numFireflies", numFireflies);
//        shader.setUniformf("gorfPos", gorf.getX() / BW, gorf.getY() / BH);
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

	public void update(ArrayList<Lantern> lanterns, GorfModel gorf, BoardModel tileBoard) {
		int numLanterns = 0;

		lanternsA = new float[lanterns.size()*2];
		for (int i=0; i<lanterns.size(); i++) {
			if (lanterns.get(i).lit) {
				lanternsA[numLanterns*2] = lanterns.get(i).getX() / BW;
				lanternsA[numLanterns*2+1] = lanterns.get(i).getY() / BH;
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

		gorfPos = new Vector2(gorf.getX() / BW * WX, gorf.getY() / BH * WY);

//		elementBoardCam = new float[NX][NY];
//		for (int i=0; i<NX; i++) {
//			for (int j=0; j<NY; j++) {
//				elementBoardCam[i][j] = elementBoard[startTileX+i][startTileY+j];
//			}
//		}

		int boardTilesPerCamViewX = (int)Math.floor(.75f * WX);
		int boardTilesPerCamViewY = (int)Math.floor(.75f * WY);

		int startTileX = (int)(gorfPos.x - (boardTilesPerCamViewX + 1) / 2);
		int startTileY = (int)(gorfPos.y - (boardTilesPerCamViewY + 1) / 2);

		fogBoardCam = new float[NX*NY];
		for (int i=0; i<boardTilesPerCamViewX; i++) {
			for (int j=0; j<boardTilesPerCamViewY; j++) {
				int camTileX = (int)((float)i/boardTilesPerCamViewX * NX);
				int camTileY = (int)((float)j/boardTilesPerCamViewY * NY);
				if (startTileX+i > 0 && startTileY+j > 0 && startTileX+i < WX && startTileY+j < WY) {
					if (fogBoardCam[camTileY * NX + camTileX] != 0) {
						fogBoardCam[camTileY * NX + camTileX] = Math.min(fogBoardCam[camTileY * NX + camTileX], fogBoard[startTileX + i][startTileY + j]);
					} else {
						fogBoardCam[camTileY * NX + camTileX] = fogBoard[startTileX + i][startTileY + j];
					}
				}
			}
		}
	}

	private void updateFog() {
		spreadCount = 0;
		spreadCountX = 0;
		spreadCountY = 0;
		newFogBoard = fogBoard.clone();
		for (int i = 0; i < WX; i++) {
			for (int j = 0; j < WY; j++) {
//				fogBoard[j*NX+i] = 1;
				if (fogBoard[i][j] == 1.1f) {
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

//		shader.begin();
//		shader.setUniform1fv("fogBoard", fogBoard, 0, NX*NY);
//		shader.setUniform1fv("reachBoard", reachBoard, 0, NX*NY);
//		shader.setUniformf("fogReach", fogReach);
//		shader.end();
	}

	private void spreadFog(int x, int y) {
		spreadType = MathUtils.random(0,2);
		if (spreadType == 0) {
			newFogBoard[x][y] = 1 - elementBoard[x][y];

			int x1 = x - 1;
			int x2 = x + 1;
			int y1 = y - 1;
			int y2 = y + 1;

			if (x1 == -1) {
				x1 = WX - 1;
			} else if (x2 == WX) {
				x2 = 0;
			}
			if (y1 == -1) {
				y1 = WY - 1;
			} else if (y2 == WY) {
				y2 = 0;
			}

			newFogBoard[x][y1] = Math.max(newFogBoard[x][y1], 1.1f * (1 - elementBoard[x][y1]));
			newFogBoard[x][y2] = Math.max(newFogBoard[x][y2], 1.1f * (1 - elementBoard[x][y2]));
			newFogBoard[x1][y] = Math.max(newFogBoard[x1][y], 1.1f * (1 - elementBoard[x1][y]));
			newFogBoard[x2][y] = Math.max(newFogBoard[x2][y], 1.1f * (1 - elementBoard[x2][y]));

			if (newFogBoard[x][y1] == 1) {
				tileBoard.setFog(x,y1);
			}

			if (fogBoard[x][y2] == 1) {
				tileBoard.setFog(x,y2);
			}

			if (fogBoard[x1][y] == 1) {
				tileBoard.setFog(x1,y);
			}

			if (fogBoard[x2][y] == 1) {
				tileBoard.setFog(x2,y);
			}
		}
		else if (spreadType == 1){
			int ii, jj;
			for (int i = x - 1; i <= x + 1; i++) {
				for (int j = y - 1; j <= y + 1; j++) {
					ii = i;
					jj = j;
					if (i == -1) {
						ii = WX - 1;
					} else if (i == WX) {
						ii = 0;
					}
					if (j == -1) {
						jj = WY - 1;
					} else if (j == WY) {
						jj = 0;
					}


					if (i == x && j == y) {
						newFogBoard[i][j] = 1 - elementBoard[ii][jj];
					} else {
						newFogBoard[ii][jj] = Math.max(newFogBoard[ii][jj], 1.1f * (1 - elementBoard[ii][jj]));
						//					if(elementBoard[ii][jj]==1) {
						//						System.out.println("Obstacle here");
						//						System.out.println("fogBoard["+ii+"]["+jj+"] is now "+fogBoard[jj*WX+ii]);
						//					}
						//					newFogBoard[jj * WX + ii] = Math.max(newFogBoard[jj * WX + ii], .5f);
					}

					if (fogBoard[ii][jj] == 1) {
						tileBoard.setFog(ii,jj);
					}
				}
			}
		}
//		else if (spreadType == 2){
//			newFogBoard[y * WX + x] = 1 - elementBoard[x][y];
//
//			int x1 = x - 1;
//			int x2 = x + 1;
//
//			if (x1 == -1) {
//				x1 = WX - 1;
//			} else if (x2 == WX) {
//				x2 = 0;
//			}
//
//			newFogBoard[y * WX + x1] = Math.max(newFogBoard[y * WX + x1], 1.1f * (1 - elementBoard[x1][y]));
//			newFogBoard[y * WX + x2] = Math.max(newFogBoard[y * WX + x2], 1.1f * (1 - elementBoard[x2][y]));
//		} else {
//			newFogBoard[y * WX + x] = 1 - elementBoard[x][y];
//
//			int y1 = y - 1;
//			int y2 = y + 1;
//
//			if (y1 == -1) {
//				y1 = WY - 1;
//			} else if (y2 == WY) {
//				y2 = 0;
//			}
//
//			newFogBoard[y1 * WX + x] = Math.max(newFogBoard[y1 * WX + x], 1.1f * (1 - elementBoard[x][y1]));
//			newFogBoard[y2 * WX + x] = Math.max(newFogBoard[y2 * WX + x], 1.1f * (1 - elementBoard[x][y2]));
//		}
	}


}
