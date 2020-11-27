package battleshipGame;

public class Battleship {
    private int hitpoints;

    public int size;
    public boolean vertical;

    public Battleship (int size, boolean vertical) {
        this.size = size;
        this.vertical = vertical;
        hitpoints = size;
    }

    public void hit() {
        hitpoints--;
    }

    public boolean isAlive() {
        return hitpoints > 0;
    }

    public void rotate(){
        vertical = !vertical;
    }
}
