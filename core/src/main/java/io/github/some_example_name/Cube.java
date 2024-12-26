package io.github.some_example_name;

import com.badlogic.gdx.math.Vector3;

import java.util.UUID;

public class Cube {
    private UUID id;
    private int x;
    private int y;
    private int z;
    private Vector3 min;
    private Vector3 max;
    private boolean bomb;
    private int bombQuantity;
    private State state;


    public Cube(int x, int y, int z, boolean bomb, Vector3 min, Vector3 max,int bombQuantity) {
        this.id = UUID.randomUUID();
        this.x = x;
        this.y = y;
        this.z = z;
        this.bomb = bomb;
        this.max = max;
        this.min = min;
        this.bombQuantity = bombQuantity;
        this.state =  State.Active;
    }


    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public boolean isBomb() {
        return bomb;
    }

    public void setBomb(boolean bomb) {
        this.bomb = bomb;
    }

    public Vector3 getMax (){
        return max;
    }
    public Vector3 getMin (){
        return min;
    }

    public Vector3 getCenter (){
        return new  Vector3((min.x + max.x) / 2, (min.y + max.y) / 2, (min.z + max.z) / 2);

    }
    public State getState(){return state;}

    public void setState(State state){this.state = state;}

    public void addBombQuantity(){
        this.bombQuantity+=1;
    }

    public int getBombQuantity(){
        return this.bombQuantity;
    }
}

