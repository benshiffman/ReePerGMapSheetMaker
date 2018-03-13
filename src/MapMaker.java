import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;

import static java.awt.GridBagConstraints.BOTH;

public class MapMaker extends JFrame implements MouseListener, ActionListener {

	int worldWidth = 1;
	int worldHeight = 1;
	int chunkDim = 8;
	int tileDim = 64;
	
	int canvasX = worldWidth*chunkDim*tileDim;
	int canvasY = worldHeight*chunkDim*tileDim;
	
	boolean firstRun = true;
	
	JFrame frame = new JFrame("Map Maker");
	JButton save = new JButton("Save");

    Container contentPane = getContentPane();
	GridBagLayout gridbag = new GridBagLayout();
    GridBagConstraints c = new GridBagConstraints();
	
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

	int paletteWidth = 4;
	int paletteHeight = 5;
	int paletteWindowWidth = paletteWidth*tileDim+(paletteWidth+1)*(tileDim/4);
	int paletteWindowHeight = paletteHeight*tileDim+(paletteHeight+1)*(tileDim/4);
	BufferedImage[][] paletteArray = new BufferedImage[paletteWidth][paletteHeight];
    Palette palette = new Palette(paletteArray);
	
	int mapSheetDim = 8;
	BufferedImage mapSheet = new BufferedImage(mapSheetDim, mapSheetDim, BufferedImage.TYPE_INT_ARGB);
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new MapMaker();
	}
	
	public MapMaker(){
		init();

		//creates palette
        for (int x = 0; x < tileKey.getWidth()/tileDim; x++){
            for (int y = 0; y < tileKey.getHeight()/tileDim; y++){
                for (int i = 0; i < paletteArray.length; i++){
                    for (int j = 0; j < paletteArray[0].length; j++){
                        paletteArray[i][j] = tileKey.getSubimage(x*tileDim, y*tileDim, tileDim, tileDim);
                    }
                }
            }
        }

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

        try {
            tileKey = ImageIO.read(tileKeyFile);
            System.out.println("Reading complete: tileKey");
        }
        catch (IOException e) {
            System.out.println("Error"+e);
        }
		
		colorKeyArray = new Color[colorKey.getWidth()][colorKey.getHeight()];
		
		//frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setSize(canvasX+paletteWindowWidth+save.getHeight(), canvasY);
		contentPane.setLayout(gridbag);

		c.gridheight = 1;

		c.gridwidth = 2;
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.NORTHWEST;
		gridbag.setConstraints(save, c);
		contentPane.add(save);

		palette.setSize(paletteWindowWidth, paletteWindowHeight);
		palette.setBorder(BorderFactory.createLineBorder(Color.black));
		c.fill = BOTH;
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 1;
		c.ipadx = paletteWindowWidth-12;
		c.ipady = canvasY-12;
		gridbag.setConstraints(palette, c);
        contentPane.add(palette);

		panel.setSize(canvasX, canvasY);
		panel.setBorder(BorderFactory.createLineBorder(Color.black));
		c.gridwidth = 1;
		c.gridx = 1;
		c.gridy = 1;
		c.ipadx = canvasX-12;
		c.ipady = canvasY-12;
		gridbag.setConstraints(panel, c);
		contentPane.add(panel);

		palette.addMouseListener(this);
		panel.addMouseListener(this);
		frame.add(contentPane);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setVisible(true);
		frame.pack();
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

        if(event.getSource().equals(panel)) {
            System.out.println("X: " + event.getX());
            System.out.println("Y: " + event.getY());

            int eChunkCoordX = (int) (((double) event.getX()) / ((double) canvasX / (double) worldWidth));
            System.out.println("chunkX: " + eChunkCoordX);

            int eChunkCoordY = (int) (((double) event.getY()) / ((double) canvasY / (double) worldHeight));
            System.out.println("chunkY: " + eChunkCoordY);

            int eTileCoordX = (int) (((double) event.getX() - (double) (eChunkCoordX * tileDim * chunkDim)) / (double) tileDim);
            System.out.println("tileX: " + eTileCoordX);
            int eTileCoordY = (int) (((double) event.getY() - (double) (eChunkCoordY * tileDim * chunkDim)) / (double) tileDim);
            System.out.println("tileY: " + eTileCoordY);

            worldImageArray[eChunkCoordX][eChunkCoordY][eTileCoordX][eTileCoordY] = testImage;
            frame.repaint();
            System.out.println("");
        }
	}

}
