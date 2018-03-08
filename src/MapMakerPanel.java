import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class MapMakerPanel extends JPanel{
	
	BufferedImage[][][][] world;
	int tileDim = 64;
	int chunkDim = 8;
	int worldHeight;
	int worldWidth;
	
	int canvasX = worldWidth*chunkDim*tileDim;
	int canvasY = worldHeight*chunkDim*tileDim;
	
	File testMapFile = new File("res/testMap.png");
	File tileKeyFile = new File("res/tileKey.png");
	File colorKeyFile = new File("res/colorKey.png");
	File canvasFile = new File("res/canvas.png");
	File blankFile = new File("res/blank.png");
	
	BufferedImage blank;
	
	boolean firstRun;
	
	public MapMakerPanel(BufferedImage[][][][] in, int wH, int wW, boolean fR){
		world = in;
		worldHeight = wH;
		worldWidth = wW;
		firstRun = fR;
	}
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		
		//not working
		/*try{
			blank = ImageIO.read(blankFile);
		}
		catch(IOException e){
			System.out.println("Error"+e);
		}
		
		if (firstRun == true) {
			for (int x = 0; x < canvasX / tileDim; x += tileDim) {
				for (int y = 0; y < canvasY / tileDim; y += tileDim) {
					g.drawImage(blank, x, y, null);
				}
			} 
		}*/
		
		g.drawLine(0, 0, canvasX, canvasY);
		for (int x = 1; x < canvasX / tileDim; x += tileDim) {
			for (int y = 0; y < canvasY / tileDim; y += tileDim) {
				
			}
		} 
		
		//draws entire map
		for (int chunkX = 0; chunkX < world.length; chunkX++) {
			for (int chunkY = 0; chunkY < world[0].length; chunkY++) {
				for (int tileX = 0; tileX < world[0][0].length * tileDim; tileX += tileDim) {
					for (int tileY = 0; tileY < world[0][0][0].length * tileDim; tileY+=tileDim) {
						g.drawImage(world[chunkX][chunkY][tileX / tileDim][tileY / tileDim], tileX+(chunkX*tileDim*chunkDim), tileY+(chunkY*tileDim*chunkDim), null);
					}
				}
			} 
		}
	}
}
