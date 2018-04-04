import java.awt.*;
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
	
	int canvasX;
	int canvasY;
	
	File testMapFile = new File("res/testMap.png");
	File tileKeyFile = new File("res/tileKey.png");
	File colorKeyFile = new File("res/colorKey.png");
	File canvasFile = new File("res/canvas.png");
	File blankFile = new File("res/blank.png");
	
	BufferedImage blank;
	
	boolean firstRun = true;
	
	public MapMakerPanel(BufferedImage[][][][] in, int wH, int wW){
		world = in;
		worldHeight = wH;
		worldWidth = wW;
		canvasX = worldWidth*chunkDim*tileDim;
		canvasY = worldHeight*chunkDim*tileDim;
        try{
            blank = ImageIO.read(blankFile);
        }
        catch(IOException e){
            System.out.println("Error"+e);
        }
        setPreferredSize(new Dimension(worldWidth*chunkDim*tileDim, worldHeight*chunkDim*tileDim));
	}
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		
		//draws entire map
		for (int chunkX = 0; chunkX < world.length; chunkX++) {
			for (int chunkY = 0; chunkY < world[0].length; chunkY++) {
				for (int tileX = 0; tileX < world[0][0].length * tileDim; tileX += tileDim) {
					for (int tileY = 0; tileY < world[0][0][0].length * tileDim; tileY+=tileDim) {
                        g.drawImage(blank, tileX+(chunkX*tileDim*chunkDim), tileY+(chunkY*tileDim*chunkDim), null);
						g.drawImage(world[chunkX][chunkY][tileX / tileDim][tileY / tileDim], tileX+(chunkX*tileDim*chunkDim), tileY+(chunkY*tileDim*chunkDim), null);
					}
				}
			} 
		}
	}
}
