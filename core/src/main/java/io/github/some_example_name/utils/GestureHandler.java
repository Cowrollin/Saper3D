package io.github.some_example_name.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;

import io.github.some_example_name.Cube;
import io.github.some_example_name.GameState;
import io.github.some_example_name.Main;
import io.github.some_example_name.State;

public class GestureHandler extends GestureDetector.GestureAdapter {
    private Main main;

    public GestureHandler(Main main) {
        this.main = main;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        main.cameraRotationAngleY += deltaX / 360f;
        main.cameraRotationAngleX += deltaY / 360f;

        return true;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        float zoomAmount = initialDistance - distance;
        main.cameraDistance += zoomAmount * 0.0001f;
        main.cameraDistance = Math.min(Math.max(Math.max(main.xsize, main.ysize) * 2, main.cameraDistance), 20f);
        return true;
    }

    @Override
    public boolean tap(float touchX, float touchY, int count, int button) {
        Ray ray = main.camera.getPickRay(touchX, touchY);
        if (main.gameState == GameState.GameWait){
            Vector3 touchPos = new Vector3(Gdx.input.getX(), main.screenHeight - Gdx.input.getY(), 0);
            main.handleDifficultySelection(touchPos);
        }
        else if (main.gameState == GameState.GameStarted)
            for (int x = 0; x < main.smallCubes.size(); x++) {
                for (int y = 0; y < main.smallCubes.get(x).size(); y++) {
                    for (int z = 0; z < main.smallCubes.get(x).get(y).size(); z++) {
                        int adjustedX = (main.camera.position.x > 0) ? (main.smallCubes.size() - 1 - x) : x;
                        int adjustedY = (main.camera.position.y > 0) ? (main.smallCubes.get(x).size() - 1 - y) : y;
                        int adjustedZ = (main.camera.position.z > 0) ? (main.smallCubes.get(x).get(y).size() - 1 - z) : z;
                        Cube cube = main.box.findCubeByCoordinates(adjustedX, adjustedY, adjustedZ);
                        if (cube != null && cube.getState() != State.Invisible) {
                            BoundingBox boundingBox = new BoundingBox();
                            Vector3 min = cube.getMin();
                            Vector3 max = cube.getMax();
                            boundingBox.set(min, max);
                            if (Intersector.intersectRayBounds(ray, boundingBox, null)) {
                                if (cube.getState() == State.Flag) {
                                    return true;
                                }
                                if (cube.getState() == State.Active) {
                                    main.removeCube(adjustedX, adjustedY, adjustedZ);
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        return true;
    }

    @Override
    public boolean longPress(float touchX, float touchY) {
        Ray ray = main.camera.getPickRay(touchX, touchY);
        if (main.gameState == GameState.GameStarted)
            for (int x = 0; x < main.smallCubes.size(); x++) {
                for (int y = 0; y < main.smallCubes.get(x).size(); y++) {
                    for (int z = 0; z < main.smallCubes.get(x).get(y).size(); z++) {
                        int adjustedX = (main.camera.position.x > 0) ? (main.smallCubes.size() - 1 - x) : x;
                        int adjustedY = (main.camera.position.y > 0) ? (main.smallCubes.get(x).size() - 1 - y) : y;
                        int adjustedZ = (main.camera.position.z > 0) ? (main.smallCubes.get(x).get(y).size() - 1 - z) : z;
                        Cube cube = main.box.findCubeByCoordinates(adjustedX, adjustedY, adjustedZ);
                        if (cube != null && cube.getState() != State.Invisible) {
                            BoundingBox boundingBox = new BoundingBox();
                            Vector3 min = cube.getMin();
                            Vector3 max = cube.getMax();
                            boundingBox.set(min, max);
                            if (Intersector.intersectRayBounds(ray, boundingBox, null)) {
                                if (cube.getState() != State.Invisible) {
                                    main.changeFlag(adjustedX, adjustedY, adjustedZ);
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        return true;
    }
}
