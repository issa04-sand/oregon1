package model;

public class MovementController {
    private final MapLoader map;
    private final Player player;

    public MovementController(MapLoader map, Player player){
        this.map = map;
        this.player = player;
    }

    public void moveUp(){
        int nx = player.getPosX();
        int ny = player.getPosY() - 1;
        if (!map.isBlocked(nx, ny)){
            player.setPosition(nx, ny);
        }
    }

    public void moveDown(){
        int nx = player.getPosX();
        int ny = player.getPosY() + 1;
        if (!map.isBlocked(nx, ny)){
            player.setPosition(nx, ny);
        }
    }

    public void moveLeft(){
        int nx = player.getPosX() - 1;
        int ny = player.getPosY();
        if (!map.isBlocked(nx, ny)){
            player.setPosition(nx, ny);
        }
    }

    public void moveRight(){
        int nx = player.getPosX() + 1;
        int ny = player.getPosY();
        if (!map.isBlocked(nx, ny)){
            player.setPosition(nx, ny);
        }
    }
}
