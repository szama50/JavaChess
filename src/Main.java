import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Objects;
import java.util.Vector;

class Game extends JPanel {
    public static final int windowSize = 640;

    //For painting
    private Board board;

    //Menu items
    private static final int buttonWidth = 200;
    private static final int buttonHeight = 100;
    private Button play;

    private static final int margin = 50;
    private static final int textHeight = 100;
    private Text title;

    //Game over items
    private Button menu;
    private Text gameOverText;

    //Figures
    private Vector<Figure> figures;
    private Figure inFocus;

    private boolean inGame;
    private String turn;

    boolean replay;

    public Game() {
        JFrame window = new JFrame();
        window.setTitle("Java Chess");
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        this.setPreferredSize(new Dimension(windowSize,windowSize));
        this.setLayout(null);
        this.setBackground(Color.BLACK);

        window.add(this);
        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);

        inGame = false;
        turn = "";
        loadMenuScreen();

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent me) {
                if (inGame) mouseUsed(me);
            }
        });

        replay = false;
    }

    // --- Load menu screen ---

    private void loadMenuScreen() {
        if (replay) {
            menu.removeFromParent(); menu = null;
            gameOverText.removeFromParent(); gameOverText = null;
        }
        ButtonFunction buttonFunction = this::loadGameScreen;
        play = new Button("Play",(windowSize-buttonWidth)/2,(windowSize-buttonHeight)/2,
                buttonWidth,buttonHeight,this, buttonFunction);
        title = new Text("Java Chess",0,margin,windowSize,textHeight,this,100);
        this.repaint();
    }

    // --- Load game screen functions ---

    private void loadGameScreen() {
        inGame = true;
        turn = "white";

        title.removeFromParent();
        play.removeFromParent();

        loadFigures();

        board = new Board();
        this.add(board);
        this.repaint();
    }
    private void loadFigures() {
        figures = new Vector<>();
        inFocus = null;
        //Pawns
        for (int x = 1; x < 9; x++) {
            figures.add(new Pawn("figures/blackPawn.png",x,2,"black",this));
            figures.add(new Pawn("figures/whitePawn.png",x,7,"white",this));
        }
        //Knights
        figures.add(new Knight("figures/blackKnight.png",2,1,"black",this));
        figures.add(new Knight("figures/blackKnight.png",7,1,"black",this));
        figures.add(new Knight("figures/whiteKnight.png",2,8,"white",this));
        figures.add(new Knight("figures/whiteKnight.png",7,8,"white",this));
        //Bishops
        figures.add(new Bishop("figures/blackBishop.png",3,1,"black",this));
        figures.add(new Bishop("figures/blackBishop.png",6,1,"black",this));
        figures.add(new Bishop("figures/whiteBishop.png",3,8,"white",this));
        figures.add(new Bishop("figures/whiteBishop.png",6,8,"white",this));
        //Rooks
        figures.add(new Rook("figures/blackRook.png",1,1,"black",this));
        figures.add(new Rook("figures/blackRook.png",8,1,"black",this));
        figures.add(new Rook("figures/whiteRook.png",1,8,"white",this));
        figures.add(new Rook("figures/whiteRook.png",8,8,"white",this));
        //Queens
        figures.add(new Queen("figures/blackQueen.png",4,1,"black",this));
        figures.add(new Queen("figures/whiteQueen.png",4,8,"white",this));
        //Kings
        figures.add(new King("figures/blackKing.png",5,1,"black",this));
        figures.add(new King("figures/whiteKing.png",5,8,"white",this));
    }

    // ---- Mouse functions ----

    private void mouseUsed(MouseEvent me) {
        if (inGame) {
            int mousePosX = me.getX() / (windowSize / 8) + 1;
            int mousePosY = me.getY() / (windowSize / 8) + 1;
            if (!(step(mousePosX, mousePosY) || figureRemoved(mousePosX, mousePosY))) {
                inFocus = searchForFocus(mousePosX, mousePosY);
                coloring(inFocus);
            }
            pawnToQueen();
            this.repaint();
            gameOver();
        }
    }
    private boolean inVector(int mousePosX, int mousePosY, Vector<Coordinate> coordinates) {
        for (Coordinate c : coordinates) {
            if (c.x == mousePosX && c.y == mousePosY) {
                return true;
            }
        }
        return false;
    }
    private boolean step(int mousePosX, int mousePosY) {
        if (inFocus == null) return false;
        if (inVector(mousePosX,mousePosY,inFocus.getAllowedAreas(figures))) {
            this.remove(board);
            inFocus.figureOffset(mousePosX,mousePosY);
            board.updateParameters(null,new Vector<>(),new Vector<>());
            this.add(board);
            if (Objects.equals(turn, "white")) turn = "black";
            else turn = "white";
            inFocus.firstStep = false;
            inFocus = null;
            return true;
        }
        return false;
    }
    private boolean figureRemoved(int mousePosX, int mousePosY) {
        if (inFocus == null) return false;
        if (inVector(mousePosX,mousePosY,inFocus.getTargetAreas(figures))) {
            this.remove(board);
            if (Objects.equals(turn, "white")) {
                turn = "black";
                for (int i = 0; i < figures.size(); i++) {
                    if (Objects.equals(figures.get(i).team, "black") && figures.get(i).positionX == mousePosX
                            && figures.get(i).positionY == mousePosY) {
                        figures.get(i).figureRemove();
                        figures.remove(i);
                        break;
                    }
                }
            }
            else {
                turn = "white";
                for (int i = 0; i < figures.size(); i++) {
                    if (Objects.equals(figures.get(i).team, "white") && figures.get(i).positionX == mousePosX
                            && figures.get(i).positionY == mousePosY) {
                        figures.get(i).figureRemove();
                        figures.remove(i);
                        break;
                    }
                }
            }
            inFocus.figureOffset(mousePosX,mousePosY);
            inFocus.firstStep = false;
            inFocus = null;
            board.updateParameters(null,new Vector<>(),new Vector<>());
            this.add(board);
            return true;
        }
        inFocus = null;
        return false;
    }
    private Figure searchForFocus(int mousePosX, int mousePosY) {
        for (Figure f : figures) {
            if (Objects.equals(f.team, turn) && mousePosX == f.positionX && mousePosY == f.positionY) {
                return f;
            }
        }
        return null;
    }
    private void coloring(Figure inFocus) {
        if (inFocus == null) board.updateParameters(null,new Vector<>(),new Vector<>());
        else board.updateParameters(new Coordinate(inFocus.positionX,inFocus.positionY),inFocus.getAllowedAreas(figures),
                inFocus.getTargetAreas(figures));
    }
    private void pawnToQueen() {
        for (int i = 0; i < figures.size(); i++) {
            Figure figure = figures.get(i);
            if (figure.pawn) {
                if (Objects.equals(figure.team, "white") && figure.positionY == 1 ||
                        Objects.equals(figures.get(i).team, "black") && figures.get(i).positionY == 8) {
                    String path = "figures/"+figure.team + "Queen.png";
                    figure.figureRemove();
                    this.remove(board);
                    figures.set(i,new Queen(path,figure.positionX,figure.positionY,figure.team,this));
                    this.add(board);
                    this.repaint();
                    return;
                }
            }
        }
    }

    // --- Game over functions ---
    private void gameOver() {
        if (Objects.equals(turn, "white") && checkForNoOptions("white")) {
            if (Check.myTeamInCheck(figures,"black")) loadGameOverScreen("Winner : black");
            else loadGameOverScreen("It's a tie!");
        }
        else if (Objects.equals(turn, "black") && checkForNoOptions("black")) {
            if (Check.myTeamInCheck(figures,"white")) loadGameOverScreen("Winner : white");
            else loadGameOverScreen("It's a tie!");
        }
    }
    private boolean checkForNoOptions(String team) {
        Vector<Figure> figuresTMP = figures;
        for (int i = 0; i < figures.size(); i++) {
            if (!Objects.equals(figures.get(i).team, team)) continue;
            if (!figures.get(i).getAllowedAreas(figuresTMP).isEmpty() || !figures.get(i).getTargetAreas(figuresTMP).isEmpty()) return false;
        }
        return true;
    }
    private void loadGameOverScreen(String text) {
        inGame = false;
        replay = true;
        for (Figure f : figures) f.figureRemove();
        figures = new Vector<>();
        this.remove(board);
        ButtonFunction buttonFunction = this::loadMenuScreen;
        menu = new Button("Menu",(windowSize-buttonWidth)/2,(windowSize-buttonHeight)/2,
                buttonWidth,buttonHeight,this, buttonFunction);
        gameOverText = new Text(text,0,margin,windowSize,textHeight,this,100);
        this.repaint();
    }
}

public class Main {
    public static void main(String[] args) {
        new Game();
    }
}