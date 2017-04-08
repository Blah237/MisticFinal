package edu.cornell.gdiac.mistic;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import edu.cornell.gdiac.GameCanvas;
import edu.cornell.gdiac.WorldController;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;


public class FogController {
	PolygonSpriteBatch batch;
	Texture bg;
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

	private final int FOG_DELAY = 20;
	int spreadType;
	float thickness;
	float spreadCount;
	float spreadCountX;
	float spreadCountY;
	private final int NX = 24;
	private final int NY = 24;

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

	private final float BOUNDARY = 0.8f;
	private final float FOG = 1.0f;
	private final float WALL = 0.9f;
	private final float LANTERN = 0.5f;

	float zoom;
	Vector2 screenDim;
	float canvasScale;
	Vector2 res;
	Vector2 dim;
	Vector2 scale;

	Array<Vector2> newFog;

	Texture perlinTex;

//	OrthographicCamera cam;
	FPSLogger logger = new FPSLogger();


	public FogController(BoardModel tileBoard, GameCanvas canvas, Rectangle screensize, float canvasScale, Vector2 scale) {
		bg = new Texture("mistic/backgroundresize.png");
		screenDim = new Vector2(screensize.getWidth(), screensize.getHeight());
		res = new Vector2(canvas.getWidth(), canvas.getHeight());
		this.scale = scale;

		this.canvasScale = canvasScale;
//		this.tileBoard = tileBoard;
		zoom = canvas.getZoom();

		WX = tileBoard.getWidth();
		WY = tileBoard.getHeight();

		tileW = screenDim.x / (float)WX;
		tileH = screenDim.y / (float)WY;

		boardTilesPerCamViewX = (int)Math.ceil(zoom * WX / canvasScale) + 1;
		boardTilesPerCamViewY = (int)Math.ceil(zoom * WY / canvasScale) + 1;

		cellW = boardTilesPerCamViewX*tileW / (float)NX;
		cellH = boardTilesPerCamViewY*tileH / (float)NY;

		fogBoard = new float[WX][WY];
		fogBoardCam = new float[NX*NY];
		newFogBoard = new float[WX][WY];

		elementBoard = new float[WX][WY];

		for (int i=0; i<WX; i++) {
			for (int j=0; j<WY; j++) {
				if (tileBoard.isWall(i,j)) {
					elementBoard[i][j] = WALL;
				}
//				else if (tileBoard.isLantern(i,j)) {
//					elementBoard[i][j] = LANTERN;
//				}
				else if (tileBoard.isFogSpawn(i,j)) {
					fogOrigin = new Vector2(i,j);
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

		dim = new Vector2(NX*cellW, NY*cellH);

		shader.begin();
		shader.setUniformf("dim", dim.x/zoom, dim.y/zoom);		// should be NX*cellW? aka graphics width...?
		shader.setUniformf("res", canvas.getWidth(), canvas.getHeight());
		shader.end();

		generatePerlin();
	}

	public void screenResize(int width, int height) {
//		cam.setToOrtho(false, width, height);
//		batch.setProjectionMatrix(cam.combined);
		//bind the shader, then set the uniform, then unbind the shader
		shader.begin();
		shader.setUniformf("res", width, height);
		shader.end();
	}

	public void draw(GameCanvas canvas, int numFireflies) {
		System.out.println(canvas.getHeight());
		batch = canvas.getSpriteBatch();
		batch.setShader(shader);



		shader.begin();

		perlinTex.bind(1);
		shader.setUniformi("u_texture_perlin", 1);

		bg.bind(0);
		shader.setUniformi("u_texture", 0);

		shader.setUniform1fv("fogBoard", fogBoardCam, 0, NX*NY);
		shader.setUniformf("fogReach", fogReach);
        shader.setUniform2fv("lanterns", litLanternsA, 0, litLanternsA.length);
		shader.setUniformi("numLanterns", litLanternsA.length/2);
        shader.setUniformi("numFireflies", numFireflies);
		shader.setUniformf("fogOrigin", fogOriginCamX, fogOriginCamY);
		shader.setUniformf("leftOffset", boardLeftOffset);
		shader.setUniformf("botOffset", boardBotOffset);
//		shader.setUniformi
		shader.end();

		batch.begin();
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.enableBlending();
		batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

		batch.draw(bg, 0, 0, screenDim.x, screenDim.y);

		batch.end();

		logger.log();
	}

	public void update(GorfModel gorf, ArrayList<Lantern> lanterns, BoardModel tileBoard) {
		fogOriginCamX = (fogOrigin.x / WX * screenDim.x - (gorf.getX() * scale.x - zoom * res.x / 2.0f)) / (zoom * res.x);
		fogOriginCamY = (fogOrigin.y / WY * screenDim.y - (gorf.getY() * scale.y - zoom * res.y / 2.0f)) / (zoom * res.y);

		gorfPos = new Vector2(gorf.getX() * scale.x + scale.x/2f, gorf.getY() * scale.y + scale.y/2f);		// in pixels

		updateLanterns(lanterns, tileBoard);

		newFog = new Array<Vector2>();
		if (fogDelay <= 0) {
			updateFog(tileBoard);
			fogDelay = FOG_DELAY;
//			fogReach++;
			thickness++;
//			System.out.println(fogReach);
//			System.out.println(fogBoard[(int)(Math.floor(fogOrigin.y/screenDim.y*NY)+Math.floor(fogOrigin.y/screenDim.x*NX)+fogReach)]);
		}
		else {
			fogDelay--;
		}
		fogReach+=(1f/FOG_DELAY);
//		fogReachX+=(1f/FOG_DELAY * spreadCountX/spreadCount);
//		fogReachY+=(1f/FOG_DELAY * spreadCountY/spreadCount);


//		System.out.println(gorfPos.x - boardTilesPerCamViewX * tileW / 2f);
		int startTileX = (int)Math.floor((gorfPos.x - res.x * zoom / 2f) / screenDim.x * WX);
		int startTileY = (int)Math.floor((gorfPos.y - res.y * zoom / 2f) / screenDim.y * WY);

		if (startTileX % 2 == 1) {
			startTileX--;
		}
		if (startTileY % 2 == 1) {
			startTileY--;
		}
//		System.out.println(startTileX);

//		fogBoard = newFogBoard;
		fogBoardCam = new float[NX*NY];
		int camTileX = 0;
		for (int i=0; i<boardTilesPerCamViewX; i++) {
			if (i>0 && i%2 == 0) {
//			if (i>0) {
				camTileX++;
			}
			int camTileY = 0;
			for (int j=0; j<boardTilesPerCamViewY; j++) {
				if (j>0 && j%2 == 0) {
//				if (j>0) {
					camTileY++;
				}
//				int camTileX = (int)((float)i/boardTilesPerCamViewX * NX);
//				int camTileY = (int)((float)j/boardTilesPerCamViewY * NY);
				int tileX = (startTileX+i + WX) % WX;
				int tileY = (startTileY+j + WY) % WY;
//				if (startTileX+i > 0 && startTileY+j > 0 && startTileX+i < WX && startTileY+j < WY) {
				if (fogBoardCam[camTileY * NX + camTileX] != 0) {
					fogBoardCam[camTileY * NX + camTileX] = Math.min(fogBoardCam[camTileY * NX + camTileX], fogBoard[tileX][tileY]);
				} else {
					fogBoardCam[camTileY * NX + camTileX] = fogBoard[tileX][tileY];
				}
//				}
//				System.out.println(fogBoard[60][60]);
			}
		}

		boardLeftOffset = ((((gorfPos.x - zoom * res.x / 2.0f) + screenDim.x) % screenDim.x) % cellW) / dim.x;
		boardBotOffset = ((((gorfPos.y - zoom * res.y / 2.0f) + screenDim.y) % screenDim.y) % cellH) / dim.y;
//		System.out.println(boardLeftOffset);
	}

	private void updateFog(BoardModel tileBoard) {
		spreadCount = 0;
		spreadCountX = 0;
		spreadCountY = 0;

		newFogBoard = new float[WX][WY];

		for (int q=0; q<WX; q++) {
			newFogBoard[q] = fogBoard[q].clone();
		}

		for (int i = 0; i < WX; i++) {
			for (int j = 0; j < WY; j++) {
				if (fogBoard[i][j] == BOUNDARY) {
//					System.out.println(newFogBoard[i][j]);
//					System.out.println(fogBoard[i][j]);
					spreadFog(i,j,tileBoard);
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

	private void spreadFog(int x, int y, BoardModel tileBoard) {

		spreadType = MathUtils.random(0,20);

		if (spreadType == 0) {
			newFogBoard[x][y] = 1 - elementBoard[x][y];
			if (newFogBoard[x][y] == 1) {
				tileBoard.setFog(x, y, true);
				newFog.add(new Vector2(x,y));
			}

			int x1 = (x - 1 + WX) % WX;
			int x2 = (x + 1     ) % WX;
			int y1 = (y - 1 + WY) % WY;
			int y2 = (y + 1     ) % WY;

			newFogBoard[x][y1] = Math.max(newFogBoard[x][y1], BOUNDARY * (1 - elementBoard[x][y1]));
			newFogBoard[x][y2] = Math.max(newFogBoard[x][y2], BOUNDARY * (1 - elementBoard[x][y2]));
			newFogBoard[x1][y] = Math.max(newFogBoard[x1][y], BOUNDARY * (1 - elementBoard[x1][y]));
			newFogBoard[x2][y] = Math.max(newFogBoard[x2][y], BOUNDARY * (1 - elementBoard[x2][y]));
		}
		else if (spreadType == 1){
			int ii, jj;
			for (int i=x-1; i<=x+1; i++) {

				ii = (i + WX) % WX;

				for (int j=y-1; j<=y+1; j++) {

					jj = (j + WY) % WY;

					if (i == x && j == y) {
						newFogBoard[i][j] = 1 - elementBoard[i][j];
						if (newFogBoard[i][j] == 1) {
							tileBoard.setFog(i,j,true);
							newFog.add(new Vector2(i,j));
						}
					} else {
						newFogBoard[ii][jj] = Math.max(newFogBoard[ii][jj], BOUNDARY * (1 - elementBoard[ii][jj]));
					}
				}
			}
		}

		// Spreading unidirectionally sometimes causes holes to form in the fog, because (actually have no idea why this happens...)
//		else if (spreadType == 2){
//			newFogBoard[x][y] = 1 - elementBoard[x][y];
//
//			int x1 = (x - 1 + WX) % WX;
//			int x2 = (x + 1     ) % WX;
//
//			newFogBoard[x1][y] = Math.max(newFogBoard[x1][y], BOUNDARY * (1 - elementBoard[x1][y]));
//			newFogBoard[x2][y] = Math.max(newFogBoard[x2][y], BOUNDARY * (1 - elementBoard[x2][y]));
//		}
//		else if (spreadType == 3){
//			newFogBoard[x][y] = 1 - elementBoard[x][y];
//
//			int y1 = (y - 1 + WY) % WY;
//			int y2 = (y + 1     ) % WY;
//
//			newFogBoard[x][y1] = Math.max(newFogBoard[x][y1], BOUNDARY * (1 - elementBoard[x][y1]));
//			newFogBoard[x][y2] = Math.max(newFogBoard[x][y2], BOUNDARY * (1 - elementBoard[x][y2]));
//		}
	}

	private void updateLanterns(ArrayList<Lantern> lanterns, BoardModel tileBoard) {
		Array<Lantern> litLanterns = new Array<Lantern>();
		for (int i=0; i<lanterns.size(); i++) {
			if (lanterns.get(i).lit) {
				litLanterns.add(lanterns.get(i));
			}
		}

		litLanternsA = new float[litLanterns.size*2];
		for (int i=0; i<litLanterns.size; i++) {
			Vector2 lanternPos = new Vector2((int)(litLanterns.get(i).getX() * scale.x / screenDim.x * WX), (int)(litLanterns.get(i).getY() * scale.y / screenDim.y * WY));

			int lx;
			int ly;

			for (int g=-2; g<=2; g++) {
				lx = (int)((lanternPos.x + g + WX) % WX);
				ly = (int)((lanternPos.y - 8 + WX) % WX);
				fogBoard[lx][ly] = Math.min(fogBoard[lx][ly], BOUNDARY);
				tileBoard.setFog(lx, ly, false);
			}

			for (int j=-7; j<=7; j++) {
				lx = (int) ((lanternPos.x + j + WX) % WX);
				int bound;
				if (j == -7 || j == 7) {
					bound = 2;
				} else if (j == -6 || j == 6) {
					bound = 4;
				} else if (j == -5 || j == 5) {
					bound = 5;
				} else if (j == -4 || j == 4) {
					bound = 6;
				} else if (j == -3 || j == 3) {
					bound = 6;
				} else {
					bound = 7;
				}

				ly = (int) ((lanternPos.y - bound - 1 + WY) % WY);

				fogBoard[lx][ly] = Math.min(fogBoard[lx][ly], BOUNDARY);
				tileBoard.setFog(lx, ly, false);

				for (int k = -bound; k <= bound; k++) {
					ly = (int) ((lanternPos.y + k + WY) % WY);
					fogBoard[lx][ly] = 0f;
					tileBoard.setFog(lx, ly, false);
				}

				ly = (int) ((lanternPos.y + bound + 1) % WY);

				fogBoard[lx][ly] = Math.min(fogBoard[lx][ly], BOUNDARY);
				tileBoard.setFog(lx, ly, false);
			}

			for (int h=-2; h<=2; h++) {
				lx = (int)((lanternPos.x + h + WX) % WX);
				ly = (int)((lanternPos.y + 8) % WX);
				fogBoard[lx][ly] = Math.min(fogBoard[lx][ly], BOUNDARY);
				tileBoard.setFog(lx, ly, false);
			}

			litLanternsA[i*2] = (litLanterns.get(i).getX() * scale.x + scale.x/2f - (gorfPos.x - zoom * res.x / 2.0f)) / (zoom * res.x);
			litLanternsA[i*2+1] = (litLanterns.get(i).getY() * scale.y + scale.y/2f - (gorfPos.y - zoom * res.y / 2.0f)) / (zoom * res.y);
		}
	}

	// Adapted from code provided at http://flafla2.github.io/2014/08/09/perlinnoise.html
	public void generatePerlin() {
		final int WIDTH = 1080, HEIGHT = 576;

		double[] data = new double[WIDTH * HEIGHT];
		int count = 0;

		Perlin perlin = new Perlin();
		for (int y = 0; y < HEIGHT; y++) {
			for (int x = 0; x < WIDTH; x++) {
				data[count++] = perlin.noise(30.0 * (double)x / WIDTH, 15.0 * (double)y / HEIGHT);
			}
		}

		double minValue = data[0], maxValue = data[0];
		for (int i = 0; i < data.length; i++) {
			minValue = Math.min(data[i], minValue);
			maxValue = Math.max(data[i], maxValue);
		}

		int[] pixelData = new int[WIDTH * HEIGHT];
		for (int i = 0; i < data.length; i++) {
			pixelData[i] = (int) (255 * (data[i] - minValue) / (maxValue - minValue));
		}

        BufferedImage img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_BYTE_GRAY);
        img.getRaster().setPixels(0, 0, WIDTH, HEIGHT, pixelData);

        File output = new File("image.png");
        try {
            ImageIO.write(img, "png", output);
        } catch (IOException e) {
            e.printStackTrace();
        }

//		ByteBuffer byteBuffer = ByteBuffer.allocate(4*pixelData.length);
//		for (int j=0; j<pixelData.length; j++) {
//			byteBuffer.putInt(pixelData[j]);
//		}
//		byte[] pixelBytes = byteBuffer.array();
//		System.out.println(pixelData[1]);
//		System.out.println((byte)pixelData[1]);
//		System.out.println(byteBuffer.get(7));
//		Pixmap perlinPix = new Pixmap(pixelBytes, 0, WIDTH*HEIGHT);
		Pixmap perlinPix = new Pixmap(new FileHandle("image.png"));
		perlinTex = new Texture(perlinPix);
		PixmapIO.writePNG(new FileHandle("image2.png"), perlinPix);
	}

	public Array<Vector2> getNewFog() {
		return newFog;
	}
}
