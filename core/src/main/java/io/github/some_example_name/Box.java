package io.github.some_example_name;

import java.util.ArrayList;
import java.util.List;

public class Box {
    private List<Cube> cubes;

    public Box() {
        this.cubes = new ArrayList<>();
    }

    public List<Cube> getCubes() {
        return cubes;
    }


    public void addCube(Cube cube) {
        this.cubes.add(cube);
    }

    public Cube findCubeByCoordinates(int x, int y, int z) {
        for (Cube cube : cubes) {
            if (cube.getX() == x && cube.getY() == y && cube.getZ() == z) {
                return cube;
            }
        }
        return null;
    }

    public int getBombsQuantity() {
        int q = 0;
        for (Cube cube : cubes) {
            q += cube.isBomb() ? 1 : 0;
        }
        return q;
    }

    public int getInactiveQuantity() {
        int q = 0;
        for (Cube cube : cubes) {
            q += cube.getState() != State.Invisible ? 1 : 0;
        }
        return q;
    }

    public boolean checkCubesState() {
        return getBombsQuantity() == getInactiveQuantity() - 1;
    }
}
