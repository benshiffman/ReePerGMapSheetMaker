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

import static java.awt.GridBagConstraints.BOTH;
import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;

public class MapMaker extends JFrame implements MouseListener, ActionListener {

    //level maker dimensions
	int worldWidth = 1;
	int worldHeight = 1;
	int chunkDim = 8;
	int tileDim = 64;

    //dimensions of the level canvas
    int canvasX = worldWidth*chunkDim*tileDim;
    int canvasY = worldHeight*chunkDim*tileDim;

    //these arrays hold the BufferedImage tiles and color values for the whole level
    BufferedImage[][][][] worldImageArray = new BufferedImage[worldWidth][worldHeight][chunkDim][chunkDim];
    Color[][][][] worldColorArray = new Color[worldWidth][worldHeight][chunkDim][chunkDim];

    MapMakerPanel panel = new MapMakerPanel(worldImageArray, worldWidth, worldHeight);

	//tile palette dimensions and object
    BufferedImage writeTile = null;
    public static int selectedX = -1;
    public static int selectedY = -1;
    int paletteTileWidth = 4;
    int paletteTileHeight = 8;
    int paletteWindowWidth = paletteTileWidth*tileDim+(paletteTileWidth+1)*(tileDim/4); //space between tiles on palette is 16px
    int paletteWindowHeight = paletteTileHeight*tileDim+(paletteTileHeight+1)*(tileDim/4); //"
    BufferedImage[][] paletteArray = new BufferedImage[paletteTileWidth][paletteTileHeight];
    Palette palette = new Palette(paletteArray, paletteTileWidth, paletteTileHeight, paletteWindowWidth, paletteWindowHeight);

	//default swing components
	JFrame frame = new JFrame("Map Maker");
	JButton save = new JButton("Save");

	//layout variables
    Container contentPane = getContentPane();
	GridBagLayout gridbag = new GridBagLayout();
    GridBagConstraints c = new GridBagConstraints();
	
	BufferedImage[][] tempImageGrid = new BufferedImage[chunkDim][chunkDim];

	//image files
	File testMapFile = new File("res/testMap.png");
	File tileKeyFile = new File("res/tileKey.png");
	File colorKeyFile = new File("res/colorKey.png");
	File canvasFile = new File("res/canvas.png");
	File testImageFile = new File("res/testImage.png");


	//BufferedImage variables
	BufferedImage colorKey;
	BufferedImage tileKey;
	BufferedImage testImage;
	
	Color[][] colorKeyArray; //stores Color data type values of each pixel of the colorKey

    //
	int mapSheetDim = 8;
	BufferedImage mapSheet = new BufferedImage(mapSheetDim*worldWidth, mapSheetDim*worldHeight, BufferedImage.TYPE_INT_ARGB);

	//scroll bar
    JScrollPane scrollPane = new JScrollPane(palette);
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new MapMaker();
	}
	
	public MapMaker(){
		init();

		//creates palette
        /*for (int i = 0; i < paletteArray.length; i++){
            for (int j = 0; j < paletteArray[0].length; j++){
                for (int x = 0; x < tileKeyWidth*tileDim; x+=tileDim){
                    for (int y = 0; y < tileKeyHeight*tileDim; y+=tileDim){
                        paletteArray[i][j] = tileKey.getSubimage(x, y, tileDim, tileDim);
                    }
                }
            }
        }*/

        for (int i = 0; i < paletteArray.length; i++){
            for (int j = 0; j < paletteArray[0].length; j++){
                paletteArray[i][j] = tileKey.getSubimage(i*tileDim, j*tileDim, tileDim, tileDim);
            }
        }

        scrollPane.repaint();

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
		//firstRun = false;
		
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

		scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(16, 0));
		scrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 16));
		scrollPane.setPreferredSize(new Dimension(paletteWindowWidth+16, paletteWindowHeight+16));
		System.out.println(scrollPane.getWidth()+" "+scrollPane.getHeight());
        scrollPane.setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_NEVER);
		contentPane.setLayout(gridbag);

		c.gridheight = 1;
		c.gridwidth = 2;
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.NORTHWEST;
		gridbag.setConstraints(save, c);
		contentPane.add(save);

		scrollPane.setSize(paletteWindowWidth, paletteWindowHeight);
		scrollPane.setBorder(BorderFactory.createLineBorder(Color.black));
		c.fill = BOTH;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.gridx = 0;
		c.gridy = 1;
		c.ipadx = paletteWindowWidth-12;
		c.ipady = canvasY-12;
		gridbag.setConstraints(scrollPane, c);
        contentPane.add(scrollPane);

        /*scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(scrollBarWidth, canvasY));
        c.gridx = 1;
        c.gridy = 1;
        c.ipadx = 0;
        c.ipady = 0;
        gridbag.setConstraints(scrollPane, c);
        contentPane.add(scrollPane);*/

		panel.setSize(canvasX, canvasY);
		panel.setBorder(BorderFactory.createLineBorder(Color.black));
		c.gridwidth = 1;
		c.gridheight = 1;
		c.gridx = 1;
		c.gridy = 1;
		c.ipadx = canvasX-12;
		c.ipady = canvasY-12;
		gridbag.setConstraints(panel, c);
		contentPane.add(panel);

		palette.addMouseListener(this);
		panel.addMouseListener(this);
		frame.add(contentPane);
        frame.setSize(canvasX+scrollPane.getWidth()+10, canvasY+51);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
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

        if(event.getSource().equals(panel)) {
            //System.out.println("X: " + event.getX());
            //System.out.println("Y: " + event.getY());

            int eChunkCoordX = (int) (((double) event.getX()) / ((double) canvasX / (double) worldWidth));
            //System.out.println("chunkX: " + eChunkCoordX);

            int eChunkCoordY = (int) (((double) event.getY()) / ((double) canvasY / (double) worldHeight));
            //System.out.println("chunkY: " + eChunkCoordY);

            int eTileCoordX = (int) (((double) event.getX() - (double) (eChunkCoordX * tileDim * chunkDim)) / (double) tileDim);
            //System.out.println("tileX: " + eTileCoordX);
            int eTileCoordY = (int) (((double) event.getY() - (double) (eChunkCoordY * tileDim * chunkDim)) / (double) tileDim);
            //System.out.println("tileY: " + eTileCoordY);

            worldImageArray[eChunkCoordX][eChunkCoordY][eTileCoordX][eTileCoordY] = writeTile;
            panel.repaint();
            //System.out.println("");
        }

        if(event.getSource().equals(palette)){
            int x = event.getX();
            int y = event.getY();
            //System.out.println(x);
            //System.out.println(y);
            int paletteCoordX = -1;
            int paletteCoordY = -1;

            /* multiply these by tileDim
            0: 1/4 to 5/4
            1: 6/4 to 10/4
            2: 11/4 to 15/4
            3: 16/4 to 20/4
            */
            for (int i = 1; i<=((paletteArray.length-1)*5)+1; i+=5){
                int low = (i*tileDim)/4;
                int high = ((i+4)*tileDim)/4;
                if(x>=low && x<high){
                    paletteCoordX = (i-1)/5;
                    break;
                }
            }

            /* multiply these by tileDim
            0: 1/4 to 5/4
            1: 6/4 to 10/4
            2: 11/4 to 15/4
            3: 16/4 to 20/4
            4: 21/4 to 25/4
            5: 26/4 to 30/4
            */
            for (int i = 1; i<=((paletteArray[0].length-1)*5)+1; i+=5){
                int low = (i*tileDim)/4;
                int high = ((i+4)*tileDim)/4;
                if(y>=low && y<high){
                    paletteCoordY = (i-1)/5;
                    break;
                }
            }

            if(paletteCoordX!=-1 && paletteCoordY!=-1){
                selectedX = paletteCoordX;
                selectedY = paletteCoordY;
                System.out.println("MapMaker selectedX: "+selectedX);
                System.out.println("MapMaker selectedY: "+selectedY);
                writeTile = paletteArray[paletteCoordX][paletteCoordY];
                palette.repaint();
                System.out.println("test");

            }
        }
	}

}
