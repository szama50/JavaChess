import javax.swing.*;
import java.util.Arrays;
import java.util.Objects;
import java.util.Vector;

class Check {
    static public boolean myTeamInCheck(Vector<Figure> figures, String enemyTeam) {
        Vector<Coordinate> allCheckAreas = new Vector<>();
        Figure myKing = new King("smg",-1,-1,"",new JPanel());
        for (Figure figure : figures) {
            if (Objects.equals(figure.team, enemyTeam)) allCheckAreas.addAll(figure.getCheckAreas(figures));
            else if (figure.king) myKing = figure;
        }
        for (Coordinate checkArea : allCheckAreas) {
            if (checkArea.x == myKing.positionX && checkArea.y == myKing.positionY) {
                return true;
            }
        }
        return false;
    }
}

abstract class Figure {
    protected final JLabel graphics;
    protected final JPanel parent;

    public int positionX;
    public int positionY;

    protected Vector<Coordinate> allowedAreas;
    protected Vector<Coordinate> targetAreas;
    protected Vector<Coordinate> checkAreas;

    public boolean firstStep;

    public String team;
    protected String enemyTeam;

    boolean king;
    boolean pawn;

    public Figure(String path, int posX, int posY, String figureTeam, JPanel figureParent) {
        firstStep = true;

        positionX = posX;
        positionY = posY;

        parent = figureParent;

        team = figureTeam;
        if (Objects.equals(team, "white")) enemyTeam = "black";
        else enemyTeam = "white";

        graphics = new JLabel();
        graphics.setIcon(new ImageIcon(path));
        graphics.setBounds((positionX-1)*Game.windowSize/8,(positionY-1)*Game.windowSize/8,
                Game.windowSize/8,Game.windowSize/8);
        parent.add(graphics);
    }
    public abstract void updateVectorsWithoutCheck(Vector<Figure> figures);
    private Vector<Coordinate> searchDangerousAreas(Vector<Figure> figures,Vector<Coordinate> examineVector) {
        Vector<Coordinate> dangerousAreas = new Vector<>();
        int previousX = this.positionX;
        int previousY = this.positionY;
        for (Coordinate area : examineVector) {
            this.positionX = area.x;
            this.positionY = area.y;
            Figure figureToBeRemoved = null;
            for (Figure f : figures) {
                if (Objects.equals(f.team, this.enemyTeam) && f.positionX == this.positionX && f.positionY == this.positionY)
                    figureToBeRemoved = f;
            }
            if (figureToBeRemoved != null) figures.remove(figureToBeRemoved);
            if (Check.myTeamInCheck(figures,enemyTeam)) dangerousAreas.add(area);
            if (figureToBeRemoved != null) figures.add(figureToBeRemoved);
        }
        this.positionX = previousX;
        this.positionY = previousY;
        return dangerousAreas;
    }
    public void updateVectorsWithCheck(Vector<Figure> figures) {
        this.updateVectorsWithoutCheck(figures);
        Vector<Coordinate> deleteDangerousAllowedAreas = searchDangerousAreas(figures,allowedAreas);
        for (Coordinate danger : deleteDangerousAllowedAreas) allowedAreas.remove(danger);
        Vector<Coordinate> deleteDangerousTargetAreas = searchDangerousAreas(figures,targetAreas);
        for (Coordinate danger : deleteDangerousTargetAreas) targetAreas.remove(danger);
    }
    public Vector<Coordinate> getCheckAreas(Vector<Figure> figures) {
        this.updateVectorsWithoutCheck(figures);
        return checkAreas;
    }
    public Vector<Coordinate> getAllowedAreas(Vector<Figure> figures) {
        this.updateVectorsWithCheck(figures);
        return allowedAreas;
    }
    public Vector<Coordinate> getTargetAreas(Vector<Figure> figures) {
        this.updateVectorsWithCheck(figures);
        return targetAreas;
    }
    protected boolean areaOccupied(Coordinate area, Vector<Figure> figures, String team) {
        for (Figure f : figures) {
            if (Objects.equals(f.team, team) && f.positionX == area.x && f.positionY == area.y) {
                return true;
            }
        }
        return false;
    }
    public void figureOffset(int newX, int newY) {
        positionX = newX;
        positionY = newY;
        graphics.setBounds((positionX-1)*Game.windowSize/8,(positionY-1)*Game.windowSize/8,
                Game.windowSize/8,Game.windowSize/8);
        parent.add(graphics);
    }
    public void figureRemove() {
        parent.remove(graphics);
    }
}

class Pawn extends Figure {
    public Pawn(String path,int posX, int posY,String figureTeam,JPanel figureParent) {
        super(path,posX,posY,figureTeam,figureParent);
        king = false;
        pawn = true;
    }
    @Override
    public void updateVectorsWithoutCheck(Vector<Figure> figures) {
        allowedAreas = new Vector<>();
        targetAreas = new Vector<>();
        checkAreas = new Vector<>();

        int direction;
        if (Objects.equals(team, "white")) direction = -1;
        else direction = 1;

        Vector<Coordinate> stepAreas = new Vector<>(); //Possible allowed areas
        Vector<Coordinate> attackAreas = new Vector<>(); //Possible target areas

        stepAreas.add(new Coordinate(positionX,positionY+direction));
        if (firstStep) stepAreas.add(new Coordinate(positionX,positionY+direction*2));
        attackAreas.add(new Coordinate(positionX-1,positionY+direction));
        attackAreas.add(new Coordinate(positionX+1, positionY+direction));
        checkAreas = attackAreas;

        for (Coordinate stepArea : stepAreas) {
            if (!areaOccupied(stepArea,figures,team) && !areaOccupied(stepArea,figures,enemyTeam)) allowedAreas.add(stepArea);
            else break;
        }
        for (Coordinate attackArea : attackAreas) if (areaOccupied(attackArea,figures,enemyTeam)) targetAreas.add(attackArea);
    }
}

class Knight extends Figure {
    public Knight(String path,int posX, int posY,String figureTeam,JPanel figureParent) {
        super(path,posX,posY,figureTeam,figureParent);
        king = false;
        pawn = false;
    }
    @Override
    public void updateVectorsWithoutCheck(Vector<Figure> figures) {
        targetAreas = new Vector<>();
        allowedAreas = new Vector<>();
        checkAreas = new Vector<>();

        Coordinate[] array = {
            new Coordinate(positionX-1,positionY-2), new Coordinate(positionX+1,positionY-2),
            new Coordinate(positionX+2,positionY-1), new Coordinate(positionX-2,positionY-1),
            new Coordinate(positionX-2,positionY+1), new Coordinate(positionX+2,positionY+1),
            new Coordinate(positionX-1,positionY+2), new Coordinate(positionX+1, positionY+2)
        };
        Vector<Coordinate> possibleAreas = new Vector<>(Arrays.asList(array));

        for (Coordinate possibleArea : possibleAreas) {
            if (possibleArea.x < 1 || possibleArea.x > 8 || possibleArea.y < 1 || possibleArea.y > 8) continue;
            checkAreas.add(possibleArea);
            if (areaOccupied(possibleArea,figures,enemyTeam)) targetAreas.add(possibleArea);
            else if (!areaOccupied(possibleArea,figures,team)) allowedAreas.add(possibleArea);
        }
    }
}

class Bishop extends Figure {
    public Bishop(String path,int posX, int posY,String figureTeam,JPanel figureParent) {
        super(path,posX,posY,figureTeam,figureParent);
        king = false;
        pawn = false;
    }
    private boolean insideOfLoop(Vector<Figure> figures,Coordinate current) {
        checkAreas.add(current);
        if (areaOccupied(current,figures,enemyTeam)) {
            targetAreas.add(current);
            return true;
        }
        if (areaOccupied(current,figures,team)) return true;
        allowedAreas.add(current);
        return false;
    }
    @Override
    public void updateVectorsWithoutCheck(Vector<Figure> figures) {
        allowedAreas = new Vector<>();
        targetAreas = new Vector<>();
        checkAreas = new Vector<>();

        int terminate = Math.min(positionX - 1, positionY - 1);
        for (int i = 1; i <= terminate; i++) if (insideOfLoop(figures,new Coordinate(positionX-i,positionY-i))) break;

        int terminate2 = Math.min(8-positionX,8-positionY);
        for (int i = 1; i <= terminate2; i++) if (insideOfLoop(figures,new Coordinate(positionX+i,positionY+i))) break;

        int terminate3 = Math.min(positionX-1,8-positionY);
        for (int i = 1; i <= terminate3; i++) if (insideOfLoop(figures,new Coordinate(positionX-i,positionY+i))) break;

        int terminate4 = Math.min(8-positionX,positionY-1);
        for (int i = 1; i <= terminate4; i++) if (insideOfLoop(figures,new Coordinate(positionX+i,positionY-i))) break;
    }
}

class Rook extends Figure {
    public Rook(String path,int posX, int posY,String figureTeam,JPanel figureParent) {
        super(path,posX,posY,figureTeam,figureParent);
        king = false;
        pawn = false;
    }
    private boolean insideOfLoop(Vector<Figure> figures,Coordinate current) {
        checkAreas.add(current);
        if (areaOccupied(current,figures,enemyTeam)) {
            targetAreas.add(current);
            return true;
        }
        if (areaOccupied(current,figures,team)) return true;
        allowedAreas.add(current);
        checkAreas.add(current);
        return false;
    }
    @Override
    public void updateVectorsWithoutCheck(Vector<Figure> figures) {
        targetAreas = new Vector<>();
        allowedAreas = new Vector<>();
        checkAreas = new Vector<>();

        for (int currentY = positionY-1; currentY > 0; currentY--) if (insideOfLoop(figures,new Coordinate(positionX,currentY))) break;
        for (int currentY = positionY+1; currentY < 9; currentY++) if (insideOfLoop(figures,new Coordinate(positionX,currentY))) break;
        for (int currentX = positionX-1; currentX > 0; currentX--) if (insideOfLoop(figures,new Coordinate(currentX,positionY))) break;
        for (int currentX = positionX+1; currentX < 9; currentX++) if (insideOfLoop(figures,new Coordinate(currentX,positionY))) break;
    }
}

class Queen extends Figure {
    public Queen(String path,int posX, int posY,String figureTeam,JPanel figureParent) {
        super(path,posX,posY,figureTeam,figureParent);
        king = false;
        pawn = false;
    }
    @Override
    public void updateVectorsWithoutCheck(Vector<Figure> figures) {
        Rook representingRook = new Rook("smg",positionX,positionY,team,this.parent);
        Bishop representingBishop = new Bishop("smg",positionX,positionY,team,this.parent);

        representingRook.updateVectorsWithoutCheck(figures);
        representingBishop.updateVectorsWithoutCheck(figures);

        allowedAreas = representingRook.allowedAreas;
        allowedAreas.addAll(representingBishop.allowedAreas);

        targetAreas = representingRook.targetAreas;
        targetAreas.addAll(representingBishop.targetAreas);

        checkAreas = representingRook.checkAreas;
        checkAreas.addAll(representingBishop.checkAreas);
    }
}

class King extends Figure {
    public King(String path,int posX, int posY,String figureTeam,JPanel figureParent) {
        super(path,posX,posY,figureTeam,figureParent);
        king = true;
        pawn = false;
    }
    @Override
    public void updateVectorsWithoutCheck(Vector<Figure> figures) {
        allowedAreas = new Vector<>();
        targetAreas = new Vector<>();
        checkAreas = new Vector<>();
        Coordinate[] array = {
                new Coordinate(positionX-1,positionY-1), new Coordinate(positionX,positionY-1),
                new Coordinate(positionX+1,positionY-1), new Coordinate(positionX-1,positionY),
                new Coordinate(positionX+1,positionY), new Coordinate(positionX-1,positionY+1),
                new Coordinate(positionX,positionY+1), new Coordinate(positionX+1, positionY+1)
        };
        Vector<Coordinate> possibleAreas = new Vector<>(Arrays.asList(array));
        for (Coordinate possibleArea : possibleAreas) {
            if (possibleArea.x < 1 || possibleArea.x > 8 || possibleArea.y < 1 || possibleArea.y > 8) continue;
            checkAreas.add(possibleArea);
            if (areaOccupied(possibleArea,figures,enemyTeam)) targetAreas.add(possibleArea);
            else if (!areaOccupied(possibleArea,figures,team)) allowedAreas.add(possibleArea);
        }
    }
}