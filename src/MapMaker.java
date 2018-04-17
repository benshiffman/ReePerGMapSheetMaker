import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

import javax.imageio.ImageIO;
import javax.swing.*;

import javax.swing.JFileChooser;

import static java.awt.GridBagConstraints.BOTH;
import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;

public class MapMaker extends JFrame implements MouseListener, ActionListener {

    String windowTitle = "Map Maker";

    //level maker dimensions
	int worldWidth = 16;
	int worldHeight = 16;
	int windowDim = 8;
	int tileDim = 64;

    //dimensions of the visible level canvas
    int canvasX = windowDim *tileDim;
    int canvasY = windowDim *tileDim;

    //image files
    File tileKeyFile = new File("res/tileKey.png");
    File colorKeyFile = new File("res/colorKey.png");
    File testImageFile = new File("res/testImage.png");


    //BufferedImage variables
    BufferedImage colorKey;
    BufferedImage tileKey;
    BufferedImage testImage;

    BufferedImage toSave = new BufferedImage(worldWidth, worldHeight, BufferedImage.TYPE_INT_ARGB);
    File saveFile = new File("save.png");

    //these arrays hold the BufferedImage tiles and color values for the whole level
    BufferedImage[][] worldImageArray = new BufferedImage[worldWidth][worldHeight];
    Color[][] worldColorArray = new Color[worldWidth][worldHeight];

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
	JButton saveAs = new JButton("Save As");
	JButton open = new JButton("Open");

	//layout variables
    Container contentPane = getContentPane();
	GridBagLayout gridbag = new GridBagLayout();
    GridBagConstraints c = new GridBagConstraints();
    Container panels = new Container();

	//colorKey array in color values
	Color[][] colorKeyArray; //stores Color data type values of each pixel of the colorKey

	//scroll panes
    JScrollPane paletteScrollPane = new JScrollPane(palette);
    JScrollPane panelScrollPane = new JScrollPane(panel);
	
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

        paletteScrollPane.repaint();

		//creates BufferedImages in worldImageArray
        for (int x = 0; x < worldImageArray.length; x++){
            for (int y = 0; y < worldImageArray[0].length; y++){
                worldImageArray[x][y] = new BufferedImage(tileDim, tileDim, BufferedImage.TYPE_INT_ARGB);
            }
        }

        //creates placeholder color values in worldColorArray
        for (int x = 0; x < worldColorArray.length; x++){
            for (int y = 0; y < worldColorArray[0].length; y++){
                worldColorArray[x][y] = new Color(0x00FFFFFF, true);
            }
        }

        //transforms colorKey
        colorKeyArray = new Color[colorKey.getWidth()][colorKey.getHeight()];
		for (int x = 0; x < colorKeyArray.length; x++) {
			for (int y = 0; y < colorKeyArray[0].length; y++) {
				int rgba = colorKey.getRGB(x, y);
				int red = (rgba >> 16) & 0xFF;
				int green = (rgba >> 8) & 0xFF;
				int blue = rgba & 0xFF;
				int alpha = (rgba >> 24) & 0xFF;
				
				colorKeyArray[x][y] = new Color(red, green, blue, alpha);
				//System.out.println("("+x+", "+y+"): "+red+", "+green+", "+blue+", "+alpha);
			}
		}
	}
	
	public void init(){
		try {
			colorKey = ImageIO.read(colorKeyFile);
			//System.out.println("Reading complete: colorKey");
        }
		catch (IOException e) {
        	System.out.println("Error"+e);
        }

        try {
            tileKey = ImageIO.read(tileKeyFile);
            //System.out.println("Reading complete: tileKey");
        }
        catch (IOException e) {
            System.out.println("Error"+e);
        }

		paletteScrollPane.setPreferredSize(new Dimension(paletteWindowWidth+19, canvasY+19));
        paletteScrollPane.setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_NEVER);

        panelScrollPane.setPreferredSize(new Dimension(canvasX+19, canvasY+19));
        panelScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        panelScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        panels.setLayout(new BorderLayout());
        panels.setPreferredSize(new Dimension(paletteScrollPane.getWidth()+panelScrollPane.getWidth()+5, panelScrollPane.getHeight()+19));
        panels.add(paletteScrollPane, BorderLayout.LINE_START);
        panels.add(panelScrollPane, BorderLayout.LINE_END);

		contentPane.setLayout(gridbag);

		c.gridheight = 1;
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(save, c);
		contentPane.add(save);

        c.gridheight = 1;
        c.gridwidth = 1;
        c.gridx = 1;
        c.gridy = 0;
        c.anchor = GridBagConstraints.WEST;
        gridbag.setConstraints(saveAs, c);
        contentPane.add(saveAs);

        c.gridheight = 1;
        c.gridwidth = 1;
        c.gridx = 2;
        c.gridy = 0;
        c.anchor = GridBagConstraints.WEST;
        gridbag.setConstraints(open, c);
        contentPane.add(open);

		c.fill = BOTH;
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 3;
		c.anchor = GridBagConstraints.SOUTHWEST;
		c.ipadx = paletteWindowWidth+canvasX+32;
		c.ipady = canvasY;
		gridbag.setConstraints(panels, c);
		contentPane.add(panels);

		palette.addMouseListener(this);
		panel.addMouseListener(this);
		save.addMouseListener(this);
		saveAs.addMouseListener(this);
		open.addMouseListener(this);

		frame.add(contentPane);

		frame.setName("Map Maker"+ windowTitle);
        frame.setLocation(200, 100);
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
            //System.out.println("X: " + event.getX());
            //System.out.println("Y: " + event.getY());

            int eventXCoord = (int)(((double)event.getX()) / ((double)tileDim));
            int eventYCoord = (int)(((double)event.getY()) / ((double)tileDim));

            //System.out.println("Coordinates: "+eventXCoord+" "+eventYCoord);
            worldImageArray[eventXCoord][eventYCoord] = writeTile;
            toSave.setRGB(eventXCoord, eventYCoord, colorKeyArray[selectedX][selectedY].getRGB());

            panel.repaint();
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

            if(paletteCoordX!=-1 && paletteCoordY!=-1
                    && ((new Color(colorKey.getRGB(paletteCoordX, paletteCoordY), true).getAlpha()!=0)
                    || (paletteCoordX==0 && paletteCoordY==0))){

                //System.out.println(colorKey.getRGB(paletteCoordX, paletteCoordY));
                selectedX = paletteCoordX;
                selectedY = paletteCoordY;
                //System.out.println("MapMaker selectedX: "+selectedX);
                //System.out.println("MapMaker selectedY: "+selectedY);
                writeTile = paletteArray[paletteCoordX][paletteCoordY];

                palette.repaint();

            }
        }

        if(event.getSource().equals(save) && windowTitle !=null){
            String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date());
            saveFile=new File("saves", "mapsheet"+timeStamp+".png");

            try {
                ImageIO.write(toSave, "png", saveFile);
            }
            catch (IOException e){
                System.out.println("Error"+e);
            }
        }

        if(event.getSource().equals(saveAs)){
            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new File(System.getProperty("user.dir")+"/saves"));
            int result = chooser.showSaveDialog(new MapMakerFileChooser());
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = chooser.getSelectedFile();
                windowTitle = "Map Maker -- "+selectedFile.getAbsolutePath()+".png";
                frame.setName(windowTitle);
                try {
                    ImageIO.write(toSave, "png", new File("saves", selectedFile.getName()+".png"));
                }
                catch (IOException e){
                    System.out.println("Error"+e);
                }
            }
        }
	}

}
