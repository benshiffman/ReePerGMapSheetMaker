import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

import javax.swing.JFileChooser;

import static java.awt.GridBagConstraints.BOTH;
import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;

public class MapMaker extends JFrame implements MouseListener {

    final String defaultWindowTitle = "Map Maker";
    final String windowTitlePrefix = "Map Maker";
    final String windowTitleBreaker = " -- ";
    String windowTitleSuffix = "";

    String currentFileName = "";

    //level maker dimensions
    final int minWorldDim = 8;
    final int maxWorldDim = 64;

    final int windowDim = 8;
    final int tileDim = 64;

	int worldWidth = minWorldDim;
	int worldHeight = minWorldDim;

    //dimensions of the visible level canvas
    int canvasX = windowDim*tileDim;
    int canvasY = windowDim*tileDim;

    //image files
    File tileKeyFile = new File("res/tileKey.png");
    File colorKeyFile = new File("res/colorKey.png");

    //BufferedImage variables
    BufferedImage colorKey;
    BufferedImage tileKey;

    BufferedImage toSave;// = new BufferedImage(worldWidth, worldHeight, BufferedImage.TYPE_INT_ARGB);


    //these arrays hold the BufferedImage tiles and color values for the whole level
    BufferedImage[][] worldImageArray;// = new BufferedImage[worldWidth][worldHeight];
    Color[][] worldColorArray;// = new Color[worldWidth][worldHeight];

    MapMakerPanel panel;// = new MapMakerPanel(worldImageArray, worldWidth, worldHeight);

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

	JButton newFile = new JButton("New");
	JButton save = new JButton("Save");
	JButton saveAs = new JButton("Save As");
	JButton open = new JButton("Open");
	JButton changeDims = new JButton("Change Dimensions");

    String[] xDimArray = new String[maxWorldDim - minWorldDim +1];
	JComboBox xDimField;
    String[] yDimArray = new String[maxWorldDim - minWorldDim +1];
	JComboBox yDimField;

	//layout variables
    Container contentPane = getContentPane();
	GridBagLayout gridBag = new GridBagLayout();
    GridBagConstraints c = new GridBagConstraints();
    Container panels = new Container();

	//colorKey array in color values
	Color[][] colorKeyArray; //stores Color data type values of each pixel of the colorKey

	//scroll panes
    JScrollPane paletteScrollPane = new JScrollPane(palette);
    JScrollPane panelScrollPane;// = new JScrollPane(panel);
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new MapMaker();
	}
	
	public MapMaker(){

        toSave = new BufferedImage(worldWidth, worldHeight, BufferedImage.TYPE_INT_ARGB);

        worldImageArray = new BufferedImage[worldWidth][worldHeight];
        worldColorArray = new Color[worldWidth][worldHeight];

        panel = new MapMakerPanel(worldImageArray, worldWidth, worldHeight);

        panelScrollPane = new JScrollPane(panel);

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

        //creates and draws the palette array
        for (int i = 0; i < paletteArray.length; i++){
            for (int j = 0; j < paletteArray[0].length; j++){
                paletteArray[i][j] = tileKey.getSubimage(i*tileDim, j*tileDim, tileDim, tileDim);
            }
        }

        //creates BufferedImages in worldImageArray
        for (int x = 0; x < worldImageArray.length; x++){
            for (int y = 0; y < worldImageArray[0].length; y++){
                worldImageArray[x][y] = new BufferedImage(tileDim, tileDim, BufferedImage.TYPE_INT_ARGB);
            }
        }
        panel.repaint();

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

        paletteScrollPane.setPreferredSize(new Dimension(paletteWindowWidth+19, canvasY+19));
        paletteScrollPane.setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_NEVER);

        panelScrollPane.setPreferredSize(new Dimension(canvasX+19, canvasY+19));
        panelScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        panelScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        panels.setLayout(new BorderLayout());
        panels.setPreferredSize(new Dimension(paletteScrollPane.getWidth()+panelScrollPane.getWidth()+5, panelScrollPane.getHeight()+19));
        panels.add(paletteScrollPane, BorderLayout.LINE_START);
        panels.add(panelScrollPane, BorderLayout.LINE_END);

        //defines values for x and y dim combo boxes
        for(int i = minWorldDim; i <= maxWorldDim; i++){
            xDimArray[i-minWorldDim] = i+"";
            yDimArray[i-minWorldDim] = i+"";
        }
        xDimField = new JComboBox(xDimArray);
        yDimField = new JComboBox(yDimArray);

        //gridbaglayout management
        contentPane.setLayout(gridBag);

        c.gridheight = 1;
        c.gridwidth = 1;
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.WEST;
        gridBag.setConstraints(save, c);
        contentPane.add(save);

        c.gridheight = 1;
        c.gridwidth = 1;
        c.gridx = 1;
        c.gridy = 0;
        c.anchor = GridBagConstraints.WEST;
        gridBag.setConstraints(saveAs, c);
        contentPane.add(saveAs);

        c.gridheight = 1;
        c.gridwidth = 1;
        c.gridx = 2;
        c.gridy = 0;
        c.anchor = GridBagConstraints.WEST;
        gridBag.setConstraints(open, c);
        contentPane.add(open);

        c.gridheight = 1;
        c.gridwidth = 1;
        c.gridx = 3;
        c.gridy = 0;
        c.anchor = GridBagConstraints.WEST;
        gridBag.setConstraints(newFile, c);
        contentPane.add(newFile);

        c.gridheight = 1;
        c.gridwidth = 1;
        c.gridx = 4;
        c.gridy = 0;
        c.anchor = GridBagConstraints.WEST;
        gridBag.setConstraints(changeDims, c);
        contentPane.add(changeDims);

        c.gridheight = 1;
        c.gridwidth = 1;
        c.gridx = 5;
        c.gridy = 0;
        c.anchor = GridBagConstraints.WEST;
        gridBag.setConstraints(xDimField, c);
        contentPane.add(xDimField);

        c.gridheight = 1;
        c.gridwidth = 1;
        c.gridx = 6;
        c.gridy = 0;
        c.anchor = GridBagConstraints.WEST;
        gridBag.setConstraints(yDimField, c);
        contentPane.add(yDimField);

        c.fill = BOTH;
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 7;
        c.anchor = GridBagConstraints.SOUTHWEST;
        c.ipadx = paletteWindowWidth+canvasX+32;
        c.ipady = canvasY;
        gridBag.setConstraints(panels, c);
        contentPane.add(panels);

        palette.addMouseListener(this);
        panel.addMouseListener(this);
        save.addMouseListener(this);
        saveAs.addMouseListener(this);
        open.addMouseListener(this);
        newFile.addMouseListener(this);
        changeDims.addMouseListener(this);

        xDimField.setSelectedIndex(worldWidth-minWorldDim);
        yDimField.setSelectedIndex(worldHeight-minWorldDim);

        frame.add(contentPane);

        //frame stuff
        frame.setTitle(defaultWindowTitle);
        frame.setLocation(200, 100);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setVisible(true);
        frame.pack();
	}

    public MapMaker(int width, int height,
                    BufferedImage[][] worldImages, Color[][] worldColors, BufferedImage saveImage,
                    String newFrameTitleSuffix){

        toSave = saveImage;//new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        currentFileName = newFrameTitleSuffix;

        worldImageArray = worldImages;
        worldColorArray = worldColors;

        panel = new MapMakerPanel(worldImageArray, width, height);

        panelScrollPane = new JScrollPane(panel);

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

        //creates and draws the palette array
        for (int i = 0; i < paletteArray.length; i++){
            for (int j = 0; j < paletteArray[0].length; j++){
                paletteArray[i][j] = tileKey.getSubimage(i*tileDim, j*tileDim, tileDim, tileDim);
            }
        }

        //creates BufferedImages in worldImageArray -- only runs in initial constructor
        /*for (int x = 0; x < worldImageArray.length; x++){
            for (int y = 0; y < worldImageArray[0].length; y++){
                worldImageArray[x][y] = new BufferedImage(tileDim, tileDim, BufferedImage.TYPE_INT_ARGB);
            }
        }*/

        //creates placeholder color values in worldColorArray -- only runs in initial constructor
        /*for (int x = 0; x < worldColorArray.length; x++){
            for (int y = 0; y < worldColorArray[0].length; y++){
                worldColorArray[x][y] = new Color(0x00FFFFFF, true);
            }
        }*/

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

        paletteScrollPane.setPreferredSize(new Dimension(paletteWindowWidth+19, canvasY+19));
        paletteScrollPane.setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_NEVER);

        panelScrollPane.setPreferredSize(new Dimension(canvasX+19, canvasY+19));
        panelScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        panelScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        panels.setLayout(new BorderLayout());
        panels.setPreferredSize(new Dimension(paletteScrollPane.getWidth()+panelScrollPane.getWidth()+5, panelScrollPane.getHeight()+19));
        panels.add(paletteScrollPane, BorderLayout.LINE_START);
        panels.add(panelScrollPane, BorderLayout.LINE_END);

        //defines values for x and y dim combo boxes
        for(int i = minWorldDim; i <= maxWorldDim; i++){
            xDimArray[i-minWorldDim] = i+"";
            yDimArray[i-minWorldDim] = i+"";
        }
        xDimField = new JComboBox(xDimArray);
        yDimField = new JComboBox(yDimArray);

        //gridbaglayout management
        contentPane.setLayout(gridBag);

        c.gridheight = 1;
        c.gridwidth = 1;
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.WEST;
        gridBag.setConstraints(save, c);
        contentPane.add(save);

        c.gridheight = 1;
        c.gridwidth = 1;
        c.gridx = 1;
        c.gridy = 0;
        c.anchor = GridBagConstraints.WEST;
        gridBag.setConstraints(saveAs, c);
        contentPane.add(saveAs);

        c.gridheight = 1;
        c.gridwidth = 1;
        c.gridx = 2;
        c.gridy = 0;
        c.anchor = GridBagConstraints.WEST;
        gridBag.setConstraints(open, c);
        contentPane.add(open);

        c.gridheight = 1;
        c.gridwidth = 1;
        c.gridx = 3;
        c.gridy = 0;
        c.anchor = GridBagConstraints.WEST;
        gridBag.setConstraints(newFile, c);
        contentPane.add(newFile);

        c.gridheight = 1;
        c.gridwidth = 1;
        c.gridx = 4;
        c.gridy = 0;
        c.anchor = GridBagConstraints.WEST;
        gridBag.setConstraints(changeDims, c);
        contentPane.add(changeDims);

        c.gridheight = 1;
        c.gridwidth = 1;
        c.gridx = 5;
        c.gridy = 0;
        c.anchor = GridBagConstraints.WEST;
        gridBag.setConstraints(xDimField, c);
        contentPane.add(xDimField);

        c.gridheight = 1;
        c.gridwidth = 1;
        c.gridx = 6;
        c.gridy = 0;
        c.anchor = GridBagConstraints.WEST;
        gridBag.setConstraints(yDimField, c);
        contentPane.add(yDimField);

        c.fill = BOTH;
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 7;
        c.anchor = GridBagConstraints.SOUTHWEST;
        c.ipadx = paletteWindowWidth+canvasX+32;
        c.ipady = canvasY;
        gridBag.setConstraints(panels, c);
        contentPane.add(panels);

        palette.addMouseListener(this);
        panel.addMouseListener(this);
        save.addMouseListener(this);
        saveAs.addMouseListener(this);
        open.addMouseListener(this);
        newFile.addMouseListener(this);
        changeDims.addMouseListener(this);

        xDimField.setSelectedIndex(worldWidth-minWorldDim);
        yDimField.setSelectedIndex(worldHeight-minWorldDim);

        frame.add(contentPane);

        //frame stuff
        if(newFrameTitleSuffix == ""){
            frame.setTitle(defaultWindowTitle);
        }
        else{
            frame.setTitle(windowTitlePrefix+windowTitleBreaker+newFrameTitleSuffix);
        }

        frame.setLocation(200, 100);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setVisible(true);
        frame.pack();
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

        if(event.getSource().equals(panel) && selectedX!=-1 && selectedY!=-1) {
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

        if(event.getSource().equals(save) && !frame.getTitle().equals(defaultWindowTitle)){
            System.out.println("currentFileName: "+currentFileName);
            try {
                ImageIO.write(toSave, "png", new File("saves", currentFileName));
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
                currentFileName = selectedFile.getName();
                windowTitleSuffix = selectedFile.getAbsolutePath()+".png";
                frame.setTitle(windowTitlePrefix+windowTitleBreaker+windowTitleSuffix);
                try {
                    ImageIO.write(toSave, "png", new File("saves", selectedFile.getName()+".png"));
                }
                catch (IOException e){
                    System.out.println("Error"+e);
                }
            }
        }

        if(event.getSource().equals(open)){
            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new File(System.getProperty("user.dir")+"/saves"));
            int result = chooser.showOpenDialog(new MapMakerFileChooser());
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = chooser.getSelectedFile();
                currentFileName = selectedFile.getName();

                BufferedImage openedColorImage = null;
                try{
                    openedColorImage = ImageIO.read(selectedFile);
                }
                catch (IOException e){
                    System.out.println("Error"+e);
                }

                int newWidth = openedColorImage.getWidth(); System.out.println(newWidth);
                int newHeight = openedColorImage.getHeight(); System.out.println(newHeight);

                BufferedImage[][] newImageArray = new BufferedImage[newWidth][newHeight];
                Color[][] newColorArray = new Color[newWidth][newHeight];
                BufferedImage newSaveImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
                for (int x = 0; x < newWidth; x++){
                    for (int y = 0; y < newHeight; y++){

                        for (int x2 = 0; x2 < colorKeyArray.length; x2++){
                            for (int y2 = 0; y2 < colorKeyArray[0].length; y2++){

                                if(openedColorImage.getRGB(x,y) == colorKey.getRGB(x2, y2)){
                                    newColorArray[x][y] = new Color(colorKey.getRGB(x2, y2), true);
                                    newSaveImage.setRGB(x, y, colorKey.getRGB(x2, y2));
                                    newImageArray[x][y] = tileKey.getSubimage(x2*tileDim, y2*tileDim, tileDim, tileDim);
                                }
                            }
                        }
                    }
                }

                selectedX = -1;
                selectedY = -1;

                windowTitleSuffix = selectedFile.getName();

                frame.dispose();
                new MapMaker(newWidth, newHeight, newImageArray, newColorArray, newSaveImage, windowTitleSuffix);
            }
        }

        if(event.getSource().equals(newFile)){
            toSave = new BufferedImage(worldWidth, worldHeight, BufferedImage.TYPE_INT_ARGB);
            for (int x = 0; x < worldImageArray.length; x++){
                for (int y = 0; y < worldImageArray[0].length; y++){
                    worldImageArray[x][y] = tileKey.getSubimage(0,0, tileDim, tileDim);
                }
            }
            for (int x = 0; x < worldColorArray.length; x++){
                for (int y = 0; y < worldColorArray[0].length; y++){
                    worldColorArray[x][y] = new Color(colorKey.getRGB(0,0), true);
                }
            }
            frame.setTitle(defaultWindowTitle);
            panel.repaint();
        }

        if(event.getSource().equals(changeDims)){
            int newWidth = xDimField.getSelectedIndex()+minWorldDim;
            int newHeight = yDimField.getSelectedIndex()+minWorldDim;

            selectedX = -1;
            selectedY = -1;

            frame.dispose();
            BufferedImage[][] newImageArray = new BufferedImage[newWidth][newHeight];
            for (int x = 0; x < newWidth; x++){
                for (int y = 0; y < newHeight; y++){
                    newImageArray[x][y] = new BufferedImage(tileDim, tileDim, BufferedImage.TYPE_INT_ARGB);
                }
            }

            Color[][] newColorArray = new Color[newWidth][newHeight];
            for (int x = 0; x < newWidth; x++){
                for (int y = 0; y < newHeight; y++){
                    newColorArray[x][y] = new Color(colorKey.getRGB(0,0), true);
                }
            }

            BufferedImage newSaveImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);

            new MapMaker(newWidth, newHeight, newImageArray, newColorArray, newSaveImage, "");

            xDimField.setSelectedIndex(newWidth-minWorldDim); System.out.println(xDimField.getSelectedIndex());
            yDimField.setSelectedIndex(newHeight-minWorldDim); System.out.println(yDimField.getSelectedIndex());
        }
	}

}
