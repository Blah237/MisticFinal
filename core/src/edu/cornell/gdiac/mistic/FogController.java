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
	String vertexShader;
	String fragmentShader;
	ShaderProgram shader;
	float[][] fogBoard;
	float[][] newFogBoard;
	float[] fogBoardCam;
	BoardModel tileBoard;
	float[][] elementBoard;
	float fogReachX;
	float fogReachY;
	float fogReach;
	int fogDelay;
	Vector2 fogOrigin;
	Vector2 gorfPos;

	private final int FOG_DELAY = 60;
	int spreadType;
	float thickness;
	float spreadCount;
	float spreadCountX;
	float spreadCountY;
	private final int NX = 50;
	private final int NY = 50;

	private int WX;
	private int WY;

	private float tileW;
	private float tileH;
	private float cellW;
	private float cellH;
	private float boardLeftOffset;
	private float boardBotOffset;

	int boardTilesPerCamViewX;
	int boardTilesPerCamViewY;

	float[] litLanternsA;

	float fogOriginCamX;
	float fogOriginCamY;

	private final float BOUNDARY = 1.1f;
	private final float FOG = 1.0f;
	private final float WALL = 0.9f;
	private final float LANTERN = 0.5f;


//	OrthographicCamera cam;
	FPSLogger logger = new FPSLogger();


	public FogController(int x, int y, BoardModel tileBoard, GameCanvas canvas) {
		wall = new Texture("mistic/backgroundresize.png");
		fogOrigin = new Vector2(x,y);

		this.tileBoard = tileBoard;

		WX = tileBoard.getWidth();
		WY = tileBoard.getHeight();

		tileW = canvas.getWidth() / (float)WX;
		tileH = canvas.getHeight() / (float)WY;

		boardTilesPerCamViewX = (int)Math.floor(.5f * WX) + 1;
		boardTilesPerCamViewY = (int)Math.floor(.5f * WY) + 1;

		cellW = boardTilesPerCamViewX*tileW / (float)NX;
		cellH = boardTilesPerCamViewY*tileH / (float)NY;

		fogBoard = new float[WX][WY];
		fogBoardCam = new float[NX*NY];

		elementBoard = new float[WX][WY];

		for (int i=0; i<WX; i++) {
			for (int j=0; j<WY; j++) {
				if (tileBoard.isWall(j,i)) {
					elementBoard[i][j] = WALL;
				} else if (tileBoard.isLantern(j,i)) {
					elementBoard[i][j] = LANTERN;
				} else if (tileBoard.isFogSpawn(j,i)) {
					fogOrigin = new Vector2(j,i);
				}
			}
		}

		fogBoard[(int)fogOrigin.x][(int)fogOrigin.y] = BOUNDARY;

		litLanternsA = new float[0];

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
		shader.setUniformf("dim", NX*cellW/.5f, NY*cellH/.5f);		// should be NX*cellW? aka graphics width...?
		shader.setUniformf("res", canvas.getWidth(), canvas.getHeight());
		shader.end();
	}

	public void resize(int width, int height) {
//		cam.setToOrtho(false, width, height);
//		batch.setProjectionMatrix(cam.combined);
		//bind the shader, then set the uniform, then unbind the shader
		shader.begin();
		shader.setUniformf("res", width, height);
		shader.end();
	}

	public void draw(GameCanvas canvas, int numFireflies) {
		batch = canvas.getSpriteBatch();
		batch.setShader(shader);

		shader.begin();
		shader.setUniform1fv("fogBoard", fogBoardCam, 0, NX*NY);
		shader.setUniformf("fogReach", fogReach);
        shader.setUniform2fv("lanterns", litLanternsA, 0, litLanternsA.length);
		shader.setUniformi("numLanterns", litLanternsA.length/2);
        shader.setUniformi("numFireflies", numFireflies);
		shader.setUniformf("fogOrigin", fogOriginCamX, fogOriginCamY);
		shader.setUniformf("leftOffset", boardLeftOffset);
		shader.setUniformf("botOffset", boardBotOffset);
		shader.end();

		batch.begin();
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.enableBlending();

		batch.draw(wall, 0, 0, canvas.getWidth(), canvas.getHeight());

		batch.end();

		logger.log();
	}

	public void update(GorfModel gorf, ArrayList<Lantern> lanterns, GameCanvas canvas, Vector2 scale) {
		fogOriginCamX = (fogOrigin.x / WX * canvas.getWidth() - (gorf.getX() * scale.x - .5f * canvas.getWidth() / 2.0f)) / (.5f * canvas.getWidth());
		fogOriginCamY = (fogOrigin.y / WY * canvas.getHeight() - (gorf.getY() * scale.y - .5f * canvas.getHeight() / 2.0f)) / (.5f * canvas.getHeight());

		Array<Lantern> litLanterns = new Array<Lantern>();
		for (int i=0; i<lanterns.size(); i++) {
			if (lanterns.get(i).lit) {
				litLanterns.add(lanterns.get(i));
			}
		}

		litLanternsA = new float[litLanterns.size*2];
		for (int i=0; i<litLanterns.size; i++) {
			Vector2 lanternPos = new Vector2((int)(litLanterns.get(i).getX() * scale.x / canvas.getWidth() * WX), (int)(litLanterns.get(i).getY() * scale.y / canvas.getHeight() * WY));
			for (int j=-6; j<7; j++) {
				for (int k=-6; k<6; k++) {
					if (j==-6 || j==6 || k==-6 || k==6) {
						if (lanternPos.x+j > 0 && lanternPos.x+j < WX && lanternPos.y+k > 0 && lanternPos.y+k < WY) {
							fogBoard[(int)lanternPos.x+j][(int)lanternPos.y+k] = BOUNDARY;
						}
					} else {
						if (lanternPos.x+j > 0 && lanternPos.x+j < WX && lanternPos.y+k > 0 && lanternPos.y+k < WY) {
							fogBoard[(int)lanternPos.x+j][(int)lanternPos.y+k] = 0.0f;
						}
					}
				}
			}
			litLanternsA[i*2] = (litLanterns.get(i).getX() * scale.x - (gorf.getX() * scale.x - .5f * canvas.getWidth() / 2.0f)) / (.5f * canvas.getWidth());
			litLanternsA[i*2+1] = (litLanterns.get(i).getY() * scale.y - (gorf.getY() * scale.y - .5f * canvas.getHeight() / 2.0f)) / (.5f * canvas.getHeight());
		}

		if (fogDelay <= 0) {
			updateFog();
			fogDelay = FOG_DELAY;
//			fogReach++;
			thickness++;
//			System.out.println(fogReach);
//			System.out.println(fogBoard[(int)(Math.floor(fogOrigin.y/canvas.getHeight()*NY)+Math.floor(fogOrigin.y/canvas.getWidth()*NX)+fogReach)]);
		}
		else {
			fogDelay--;
		}
		fogReach+=(1f/FOG_DELAY);
//		fogReachX+=(1f/FOG_DELAY * spreadCountX/spreadCount);
//		fogReachY+=(1f/FOG_DELAY * spreadCountY/spreadCount);

		gorfPos = new Vector2((gorf.getX()) * scale.x / canvas.getWidth() * WX, gorf.getY() * scale.y / canvas.getHeight() * WY);

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
		boardLeftOffset = ((gorf.getX() * scale.x - .5f * canvas.getWidth() / 2.0f) % tileW) / .5f;
		boardBotOffset = ((gorf.getY() * scale.y - .5f * canvas.getHeight() / 2.0f) % tileH) / .5f;
	}

	private void updateFog() {
		spreadCount = 0;
		spreadCountX = 0;
		spreadCountY = 0;
		newFogBoard = fogBoard.clone();
		for (int i = 0; i < WX; i++) {
			for (int j = 0; j < WY; j++) {
				if (fogBoard[i][j] == BOUNDARY) {
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

			newFogBoard[x][y1] = Math.max(newFogBoard[x][y1], BOUNDARY * (1 - elementBoard[x][y1]));
			newFogBoard[x][y2] = Math.max(newFogBoard[x][y2], BOUNDARY * (1 - elementBoard[x][y2]));
			newFogBoard[x1][y] = Math.max(newFogBoard[x1][y], BOUNDARY * (1 - elementBoard[x1][y]));
			newFogBoard[x2][y] = Math.max(newFogBoard[x2][y], BOUNDARY * (1 - elementBoard[x2][y]));

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
						newFogBoard[ii][jj] = Math.max(newFogBoard[ii][jj], BOUNDARY * (1 - elementBoard[ii][jj]));
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
//			newFogBoard[y * WX + x1] = Math.max(newFogBoard[y * WX + x1], BOUNDARY * (1 - elementBoard[x1][y]));
//			newFogBoard[y * WX + x2] = Math.max(newFogBoard[y * WX + x2], BOUNDARY * (1 - elementBoard[x2][y]));
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
//			newFogBoard[y1 * WX + x] = Math.max(newFogBoard[y1 * WX + x], BOUNDARY * (1 - elementBoard[x][y1]));
//			newFogBoard[y2 * WX + x] = Math.max(newFogBoard[y2 * WX + x], BOUNDARY * (1 - elementBoard[x][y2]));
//		}
	}
}
