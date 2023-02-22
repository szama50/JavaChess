import javax.swing.*;
import java.awt.*;
import java.util.Vector;

class Board extends JComponent {
    private static final Color myWhite = new Color(251,211,175);;
    private static final Color myBlack = new Color(209,139,71);;
    private static final Color myDarkGreen = new Color(17,171,0);
    private static final Color myLightGreen = new Color(185,235,30);
    private static final Color myRed = Color.RED;
    private Coordinate darkGreenCoordinate;
    private Vector<Coordinate> lightGreenCoordinates;
    private Vector<Coordinate> redCoordinates;
    public Board() {
        this.setSize(new Dimension(Game.windowSize,Game.windowSize));
        darkGreenCoordinate = null;
        lightGreenCoordinates = new Vector<>();
        redCoordinates = new Vector<>();
    }
    @Override
    public void paint(Graphics g) {
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                if ((x+y) % 2 == 0) g.setColor(myWhite);
                else g.setColor(myBlack);
                g.fillRect(x*Game.windowSize/8,y*Game.windowSize/8, Game.windowSize,Game.windowSize);
            }
        }
        if (darkGreenCoordinate != null) {
            g.setColor(myDarkGreen);
            g.fillRect((darkGreenCoordinate.x-1)*Game.windowSize/8,
                    (darkGreenCoordinate.y-1)*Game.windowSize/8, Game.windowSize/8,Game.windowSize/8);
        }
        g.setColor(myLightGreen);
        for (Coordinate c : lightGreenCoordinates) g.fillRect((c.x-1)*Game.windowSize/8,(c.y-1)*Game.windowSize/8,
                Game.windowSize/8,Game.windowSize/8);
        g.setColor(myRed);
        for (Coordinate c : redCoordinates) g.fillRect((c.x-1)*Game.windowSize/8,(c.y-1)*Game.windowSize/8,
                Game.windowSize/8,Game.windowSize/8);
    }
    void updateParameters(Coordinate focus,Vector<Coordinate> allies, Vector<Coordinate> enemies) {
        darkGreenCoordinate = focus;
        lightGreenCoordinates = allies;
        redCoordinates = enemies;
    }
}