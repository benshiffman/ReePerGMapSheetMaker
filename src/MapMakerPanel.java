import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class MapMakerPanel extends JPanel{
	
	BufferedImage[][] world;
	int tileDim = 64;
	int chunkDim = 8;
	int worldHeight;
	int worldWidth;
	
	File testMapFile = new File("res/testMap.png");
	File tileKeyFile = new File("res/tileKey.png");
	File colorKeyFile = new File("res/colorKey.png");
	File canvasFile = new File("res/canvas.png");
	File tileborderFile = new File("res/tileborder.png");
	
	BufferedImage tileborder;
	
	public MapMakerPanel(BufferedImage[][] in, int wH, int wW){
		world = in;

		worldHeight = wH;
		worldWidth = wW;

        try{
            tileborder = ImageIO.read(tileborderFile);
        }
        catch(IOException e){
            System.out.println("Error"+e);
        }
        setPreferredSize(new Dimension(worldWidth*tileDim, worldHeight*tileDim));
	}
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		
		//draws entire map
		for (int x = 0; x < world.length; x++){
		    for (int y = 0; y < world[0].length; y++){
                g.drawImage(tileborder, x*tileDim, y*tileDim, null);
                g.drawImage(world[x][y], x*tileDim, y*tileDim, null);
            }
        }
	}
}
