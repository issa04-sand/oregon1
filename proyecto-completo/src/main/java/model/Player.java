package model;

public class Player {

    private int x;
    private int y;
    private int vida;
    private double hearts;

    public Player(int x, int y){
        this(x, y, 3);
    }

    public Player(int x, int y, int vida){
        this.x = x;
        this.y = y;
        this.vida = vida;
        this.hearts = vida;
    }

    public Player(double hearts){
        this.x = 0;
        this.y = 0;
        this.hearts = hearts;
        this.vida = (int)Math.round(hearts);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getPosX(){
        return x;
    }

    public int getPosY(){
        return y;
    }

    public void setPosition(int nx, int ny){
        this.x = nx;
        this.y = ny;
    }

    public int getVida(){
        return vida;
    }

    public double gethearts(){
        return hearts;
    }

    public void damage(int d){
        vida = Math.max(0, vida - Math.max(0, d));
        hearts = Math.max(0, hearts - Math.max(0, d));
    }

    public void heal(double h){
        hearts += Math.max(0, h);
        vida = (int)Math.round(hearts);
    }
}
