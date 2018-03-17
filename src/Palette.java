import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.Buffer;

public class Palette extends JPanel{

    File selectedFile = new File("res/selected.png");
    BufferedImage selected;

    int tileDim = 64;

    BufferedImage[][] paletteArray;
    int paletteWindowWidth;
    int paletteWindowHeight;
    int paletteWidth;
    int paletteHeight;
    int selectedX;
    int selectedY;

    public Palette(BufferedImage[][] pA, int pW, int pH, int pWW, int pWH, int sX, int sY){
        paletteArray = pA;
        paletteWidth = pW;
        paletteHeight = pH;
        paletteWindowWidth = pWW;
        paletteWindowHeight = pWH;
        selectedX = sX;
        selectedY = sY;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        /*for (int pixelX = tileDim/4; pixelX < paletteWindowWidth; pixelX+=(tileDim+tileDim/4)){
            for (int pixelY = tileDim/4; pixelY < paletteWindowHeight; pixelY+=(tileDim+tileDim/4)){
                for (int paletteX = 0; paletteX < paletteArray.length; paletteX++){
                    for (int paletteY = 0; paletteY < paletteArray[0].length; paletteY++){
                        g.drawImage(paletteArray[paletteX][paletteY], pixelX, pixelY, null);
                        System.out.println("Palette Draw: "+paletteX+" "+paletteY);
                        //System.out.println("printed");
                    }
                }
            }
        }*/

        try{
            selected = ImageIO.read(selectedFile);
            System.out.println("selected variable created");
        }
        catch(IOException e){
            System.out.println("Error" + e);
        }

        for (int x = 0; x < paletteArray.length; x++){
            for (int y = 0; y < paletteArray[0].length; y++){
                int xCoord = 16+(x*tileDim+(x*tileDim/4));
                int yCoord = 16+(y*tileDim+(y*tileDim/4));

                if(x == selectedX && y == selectedY){
                    System.out.println("Palette selectedX: "+selectedX);
                    System.out.println("Palette selectedY: "+selectedY);
                    System.out.println("");
                    g.drawImage(selected, xCoord-2, yCoord-2, null);
                }
                g.drawImage(paletteArray[x][y], xCoord, yCoord, null);
            }
        }
    }
}
