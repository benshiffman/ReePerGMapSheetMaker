import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;

public class MapMaker implements MouseListener, ActionListener {

	int worldWidth = 2;
	int worldHeight = 2;
	int chunkDim = 8;
	int tileDim = 64;
	
	int canvasX = worldWidth*chunkDim*tileDim;
	int canvasY = worldHeight*chunkDim*tileDim;
	
	boolean firstRun = true;
	
	JFrame frame = new JFrame("Map Maker");
	JButton save = new JButton();
	Container topButtons = new Container();
	
	BufferedImage[][] tempImageGrid = new BufferedImage[chunkDim][chunkDim];
	
	BufferedImage[][][][] worldImageArray = new BufferedImage[worldWidth][worldHeight][chunkDim][chunkDim];
	Color[][][][] worldColorArray = new Color[worldWidth][worldHeight][chunkDim][chunkDim];
	
	MapMakerPanel panel = new MapMakerPanel(worldImageArray, worldWidth, worldHeight, firstRun);
	
	File testMapFile = new File("res/testMap.png");
	File tileKeyFile = new File("res/tileKey.png");
	File colorKeyFile = new File("res/colorKey.png");
	File canvasFile = new File("res/canvas.png");
	File testImageFile = new File("res/testImage.png");
	
	BufferedImage colorKey;
	BufferedImage tileKey;
	BufferedImage testImage;
	
	Color[][] colorKeyArray;
	
	int mapSheetDim = 8;
	BufferedImage mapSheet = new BufferedImage(mapSheetDim, mapSheetDim, BufferedImage.TYPE_INT_ARGB);
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new MapMaker();
	}
	
	public MapMaker(){
		init();
		//creates a BufferedImage array of chunks
		for (int chunkCoordX = 0; chunkCoordX < worldImageArray.length; chunkCoordX++) {
			for (int chunkCoordY = 0; chunkCoordY < worldImageArray[0].length; chunkCoordY++) {
				for (int tileCoordX = 0; tileCoordX < worldImageArray[0][0].length; tileCoordX++) {
					for (int tileCoordY = 0; tileCoordY < worldImageArray[0][0][0].length; tileCoordY++) {
						worldImageArray[chunkCoordX][chunkCoordY][tileCoordX][tileCoordY] = new BufferedImage(tileDim, tileDim, BufferedImage.TYPE_INT_ARGB);
					}
				}
			}
		}
		
		for (int colorKeyX = 0; colorKeyX < colorKeyArray.length; colorKeyX++) {
			for (int colorKeyY = 0; colorKeyY < colorKeyArray.length; colorKeyY++) {
				int rgba = colorKey.getRGB(colorKeyX, colorKeyY);
				int red = (rgba >> 16) & 0xFF;
				int green = (rgba >> 8) & 0xFF;
				int blue = rgba & 0xFF;
				int alpha = (rgba >> 24) & 0xFF;
				
				colorKeyArray[colorKeyX][colorKeyY] = new Color(red, green, blue, alpha);
			}
		}
	}
	
	public void init(){
		firstRun = false;
		
		try {
			testImage = ImageIO.read(testImageFile);
			System.out.println("Reading complete: testImage");
        }
		catch (IOException e) {
        	System.out.println("Error"+e);
        }
		
		try {
			colorKey = ImageIO.read(colorKeyFile);
			System.out.println("Reading complete: colorKey");
        }
		catch (IOException e) {
        	System.out.println("Error"+e);
        }
		
		colorKeyArray = new Color[colorKey.getWidth()][colorKey.getHeight()];
		
		//frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setSize(canvasX, canvasY);
		frame.setLayout(new BorderLayout());
		topButtons.setLayout(new GridLayout(1,1));
		
		save.setText("Save");
		save.setName("Save");
		topButtons.add(save);
		frame.add(topButtons, BorderLayout.NORTH);
		panel.setSize(tileDim*chunkDim, tileDim*chunkDim);
		frame.add(panel, BorderLayout.CENTER);
		//canvas.createBufferStrategy(3);
		//BufferStrategy bufferStrategy = canvas.getBufferStrategy();
		panel.addMouseListener(this);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent event) {
		// TODO Auto-generated method stub

		System.out.println("X: "+event.getX());
		System.out.println("Y: "+event.getY());
		
		int eChunkCoordX = (int)(((double)event.getX())/((double)canvasX/(double)worldWidth));
		System.out.println("chunkX: "+eChunkCoordX);
		
		int eChunkCoordY = (int)(((double)event.getY())/((double)canvasY/(double)worldHeight));
		System.out.println("chunkY: "+eChunkCoordY);
		
		int eTileCoordX = (int)(((double)event.getX()-(double)(eChunkCoordX*tileDim*chunkDim))/(double)tileDim);
		System.out.println("tileX: "+eTileCoordX);
		int eTileCoordY = (int)(((double)event.getY()-(double)(eChunkCoordY*tileDim*chunkDim))/(double)tileDim);
		System.out.println("tileY: "+eTileCoordY);
		
		if(event.getSource().equals(panel)){
			worldImageArray[eChunkCoordX][eChunkCoordY][eTileCoordX][eTileCoordY] = testImage;
			frame.repaint();
		}
	}

}
