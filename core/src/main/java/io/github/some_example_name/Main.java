package io.github.some_example_name;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.naming.Context;

import io.github.some_example_name.utils.GestureHandler;

public class Main extends ApplicationAdapter {
    public PerspectiveCamera camera;
    private ModelBatch modelBatch;
    private Environment environment;
    public List<List<List<ModelInstance>>> smallCubes;
    private PointLight pointLight;
    private List<List<List<String>>> displayedNumbers;
    public int xsize = 3;
    public int ysize = 3;
    public int zsize = 3;
    int bombsQuantity = 3;
    public GameState gameState = GameState.GameWait;

    private BitmapFont font; // Шрифт для отображения текста
    private SpriteBatch spriteBatch; // SpriteBatch для отрисовки текста

    private Rectangle easyButtonBounds;
    private Rectangle mediumButtonBounds;
    private Rectangle hardButtonBounds;
    private Rectangle resetHistoryButton;
    private ShapeRenderer shapeRenderer;

    public Box box = new Box();

    public float cameraRotationAngleY = 0f;
    public float cameraRotationAngleX = 0f;
    public float cameraDistance = 10f;

    float screenWidth;
    public float screenHeight;

    private Texture backgroundTexture;
    private float timer; // Для хранения времени
    private boolean isTimerRunning; // Индикатор работы таймера
    private List<String> sessionHistory;
    private String difficulty;

    @Override
    public void create() {
        camera = new PerspectiveCamera(67f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(5f, 5f, 5f);
        camera.lookAt(0f, 0f, 0f);
        camera.up.set(0f, 1f, 0f);
        camera.near = 1f;
        camera.far = 100f;
        camera.update();

        backgroundTexture = new Texture(Gdx.files.internal("backgroung.png"));
        timer = 0f;
        isTimerRunning = false;
        sessionHistory = new ArrayList<>();
        readSessionHistoryFromFile();

        font = new BitmapFont();
        font.getData().setScale(4);
        spriteBatch = new SpriteBatch();

        modelBatch = new ModelBatch();
        environment = new Environment();
        environment.add(new DirectionalLight().set(1f, 1f, 1f, -1f, -1f, -1f));
        pointLight = new PointLight();
        pointLight.set(1f, 1f, 1f, camera.position.x, camera.position.y, camera.position.z, 100f); // Цвет и позиция
        environment.add(pointLight);
        spriteBatch = new SpriteBatch();
        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();
        createCube();

        GestureDetector gestureDetector = new GestureDetector(new GestureHandler(this));
        Gdx.input.setInputProcessor(gestureDetector);
        shapeRenderer = new ShapeRenderer();
        float buttonWidth = 250;
        float buttonHeight = 50;
        float buttonYPosition = screenHeight / 2 - 50; // П

        easyButtonBounds = new Rectangle((screenWidth / 2 - 50) - (buttonWidth * 1.5f), buttonYPosition, buttonWidth, buttonHeight);
        mediumButtonBounds = new Rectangle((screenWidth / 2- 50) - (buttonWidth / 2), buttonYPosition, buttonWidth, buttonHeight);
        hardButtonBounds = new Rectangle((screenWidth / 2 - 50) + (buttonWidth * 0.5f), buttonYPosition, buttonWidth, buttonHeight);
        resetHistoryButton = new Rectangle((screenWidth / 2 - 50) + (buttonWidth * 3.5f), buttonYPosition - 400, buttonWidth, buttonHeight);
    }

    private Model createSmallCubeModel() {
        ModelBuilder modelBuilder = new ModelBuilder();
        return modelBuilder.createBox(1f, 1f, 1f,
            new Material(ColorAttribute.createDiffuse(150f, 150f, 150f, 150f)),
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
    }

    private void createCube() {
        box = new Box();
        smallCubes = new ArrayList<>(xsize);
        for (float x = (float) -xsize / 2; x < (float) xsize / 2; x++) {
            List<List<ModelInstance>> layer = new ArrayList<>(ysize);
            for (float y = (float) -ysize / 2; y < (float) ysize / 2; y++) {
                List<ModelInstance> row = new ArrayList<>(zsize);
                for (float z = (float) -zsize / 2; z < (float) zsize / 2; z++) {
                    Cube cube = new Cube(smallCubes.size(), layer.size(), row.size(), false, new Vector3(x * 1.1f, y * 1.1f, z * 1.1f), new Vector3(x * 1.1f + 1.1f, y * 1.1f + 1.1f, z * 1.1f + 1.1f), 0);
                    ModelInstance cubeInstance = new ModelInstance(createSmallCubeModel());
                    cubeInstance.transform.setTranslation(x * 1.1f + 0.5f, y * 1.1f + 0.5f, z * 1.1f + 0.5f);
                    row.add(cubeInstance);
                    box.addCube(cube);
                }
                layer.add(row);
            }
            smallCubes.add(layer);
        }
        setBombs();
        setBombsQuantity();
        initDisplayedNumbers();

        font.setColor(Color.GRAY);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        pointLight.setPosition(camera.position);
        camera.position.set(cameraDistance * (float) Math.cos(cameraRotationAngleY) * (float) Math.cos(cameraRotationAngleX),
            cameraDistance * (float) Math.sin(cameraRotationAngleX),
            cameraDistance * (float) Math.sin(cameraRotationAngleY) * (float) Math.cos(cameraRotationAngleX));
        camera.lookAt(0f, 0f, 0f);
        camera.up.set(0f, 1f, 0f);
        camera.update();

        modelBatch.begin(camera);
        for (int x = 0; x < smallCubes.size(); x++) {
            for (int y = 0; y < smallCubes.get(x).size(); y++) {
                for (int z = 0; z < smallCubes.get(x).get(y).size(); z++) {
                    ModelInstance cube = smallCubes.get(x).get(y).get(z);
                    if (cube != null) {
                        if (!cube.materials.isEmpty() && box.findCubeByCoordinates(x, y, z).getState() == State.Flag) {
                            Material material = cube.materials.get(0);
                            material.set(ColorAttribute.createDiffuse(Color.RED));
                        }
                        if (!cube.materials.isEmpty() && box.findCubeByCoordinates(x, y, z).getState() == State.Active) {
                            Material material = cube.materials.get(0);
                            material.set(ColorAttribute.createDiffuse(150f, 150f, 150f, 150f));
                        }
                        modelBatch.render(cube, environment);
                    }
                }
            }
        }
        modelBatch.end();
        renderDisplayedNumbers();

        spriteBatch.begin();
        font.setColor(Color.WHITE);
        font.draw(spriteBatch, String.format("Time: %.1f s", timer), screenWidth - 300, screenHeight - 50); // Таймер в правом верхнем углу
        spriteBatch.end();


        switch (gameState) {
            case GameWait:
                renderStartScreen();
                break;
            case GameStarted:
                renderGame();
                break;
            case GameOver:
                renderGameOverScreen();
                break;
            case GameWin:
                renderWinScreen();
                break;
        }


    }

    @SuppressWarnings("DefaultLocale")
    private void renderWinScreen() {
        spriteBatch.begin();
        spriteBatch.draw(backgroundTexture, 0, 0, screenWidth, screenHeight); // Отрисовка фона на весь экран
        font.setColor(Color.GREEN);
        font.draw(spriteBatch, String.format("You Win! Time: %.1f s\nTap to restart.", timer), screenWidth / 2 - 350, screenHeight / 2);
        spriteBatch.end();
        if(isTimerRunning){
            sessionHistory.add(String.format("(%s) Victory: %.1f s", difficulty, timer));
            writeSessionHistoryToFile();
        }
        isTimerRunning = false;

        handleGameOverTouch();
    }

    @SuppressWarnings("DefaultLocale")
    private void renderGameOverScreen() {
        spriteBatch.begin();
        spriteBatch.draw(backgroundTexture, 0, 0, screenWidth, screenHeight); // Отрисовка фона на весь экран
        font.setColor(Color.RED);
        font.draw(spriteBatch, String.format("Game Over! Time: %.1f s\nTap to restart.", timer), screenWidth / 2 - 350, screenHeight / 2);
        spriteBatch.end();
        if(isTimerRunning){
            sessionHistory.add(String.format("(%s) Game Over: %.1f s", difficulty, timer));
            writeSessionHistoryToFile();
        }
        isTimerRunning = false;
        handleGameOverTouch();
    }

    private void renderGame() {
        if (isTimerRunning) {
            timer += Gdx.graphics.getDeltaTime(); // Увеличиваем таймер на время, прошедшее с последнего кадра
        }
        renderDisplayedNumbers();
    }

    private void renderStartScreen() {
        spriteBatch.begin();
        spriteBatch.draw(backgroundTexture, 0, 0, screenWidth, screenHeight); // Отрисовка фона на весь экран
        font.setColor(Color.WHITE);
        font.draw(spriteBatch, "3D Minesweeper", screenWidth / 2 - 250, screenHeight - 100);
        spriteBatch.end();
        drawDifficultyButtons(); // Рисуем кнопки выбора уровня сложности

        if (Gdx.input.justTouched()) {
            Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            handleDifficultySelection(touchPos);
        }
    }

    private void writeSessionHistoryToFile() {
        try {
            FileHandle file = Gdx.files.local("session_history.txt"); // Путь к файлу в локальной папке
            StringBuilder historyData = new StringBuilder();
            for (String session : sessionHistory) {
                historyData.append(session).append("\n");
            }
            file.writeString(historyData.toString(), false); // Записываем данные в файл (false - перезапись)
        } catch (Exception e) {
            Gdx.app.log("File", "Error writing to file: " + e.getMessage());
        }
    }

    private void readSessionHistoryFromFile() {
        try {
            FileHandle file = Gdx.files.local("session_history.txt");
            if (file.exists()) {
                String content = file.readString(); // Читаем весь файл
                String[] sessions = content.split("\n");
                sessionHistory.clear();
                sessionHistory.addAll(Arrays.asList(sessions));
            }
        } catch (Exception e) {
            Gdx.app.log("File", "Error reading from file: " + e.getMessage());
        }
    }

    private void handleGameOverTouch() {
        if (Gdx.input.justTouched()) {
            gameState = GameState.GameWait;
        }
    }

    private void drawDifficultyButtons() {
        timer = 0f;
        isTimerRunning = true;
        drawRoundedButton(easyButtonBounds, Color.GREEN, "Easy");
        drawRoundedButton(mediumButtonBounds, Color.YELLOW, "Medium");
        drawRoundedButton(hardButtonBounds, Color.RED, "Hard");
        drawRoundedButton(resetHistoryButton, Color.RED, "Reset");

        // Отображение истории
        spriteBatch.begin();
        font.setColor(Color.WHITE);
        font.getData().setScale(2); // Уменьшим размер текста для списка
        float historyY = screenHeight - 100; // Начальная позиция списка
        for (String session : sessionHistory) {
            font.draw(spriteBatch, session, screenWidth - 400, historyY);
            historyY -= 30; // Смещение вниз для следующей строки
        }
        font.getData().setScale(4); // Вернём масштаб текста обратно
        spriteBatch.end();
    }

    private void drawRoundedButton(Rectangle bounds, Color color, String text) {

        float cornerRadius = 15f;

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(color);

        shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height, color, color, color, color);
        shapeRenderer.rect(bounds.x, bounds.y + cornerRadius, bounds.width, bounds.height - 2 * cornerRadius);
        shapeRenderer.arc(bounds.x + cornerRadius, bounds.y + cornerRadius, cornerRadius, 180, 90); // Левый нижний угол
        shapeRenderer.arc(bounds.x + bounds.width - cornerRadius, bounds.y + cornerRadius, cornerRadius, 270, 90); // Правый нижний угол
        shapeRenderer.arc(bounds.x + bounds.width - cornerRadius, bounds.y + bounds.height - cornerRadius, cornerRadius, 0, 90); // Правый верхний угол
        shapeRenderer.arc(bounds.x + cornerRadius, bounds.y + bounds.height - cornerRadius, cornerRadius, 90, 90); // Левый верхний угол
        shapeRenderer.end();

        spriteBatch.begin();
        float textX = bounds.x + (bounds.width / 2);
        float textY = bounds.y + (bounds.height / 2);
        font.draw(spriteBatch, text, textX, textY);
        spriteBatch.end();
    }

    public void handleDifficultySelection(Vector3 touchPos) {
        font.setColor(Color.GRAY);
        if (touchPos.x > easyButtonBounds.x - easyButtonBounds.width &&
            touchPos.x < easyButtonBounds.x + easyButtonBounds.width &&
            touchPos.y > easyButtonBounds.y - easyButtonBounds.height &&
            touchPos.y < easyButtonBounds.y + easyButtonBounds.height) {
            bombsQuantity = 5; difficulty = "easy";
            xsize = 3;
            ysize = 3;
            zsize = 3;
            createCube();
            gameState = GameState.GameStarted;
        } else if (touchPos.x > mediumButtonBounds.x - mediumButtonBounds.width &&
            touchPos.x < mediumButtonBounds.x + mediumButtonBounds.width &&
            touchPos.y > mediumButtonBounds.y - mediumButtonBounds.height &&
            touchPos.y < mediumButtonBounds.y + mediumButtonBounds.height) {
            bombsQuantity = 10; difficulty = "medium";
            xsize = 4;
            ysize = 4;
            zsize = 4;
            createCube();
            gameState = GameState.GameStarted;
        } else if (touchPos.x > hardButtonBounds.x - hardButtonBounds.width &&
            touchPos.x < hardButtonBounds.x + hardButtonBounds.width &&
            touchPos.y > hardButtonBounds.y - hardButtonBounds.height &&
            touchPos.y < hardButtonBounds.y + hardButtonBounds.height) {
            bombsQuantity = 15; difficulty = "hard";
            xsize = 5;
            ysize = 5;
            zsize = 5;
            createCube();
            gameState = GameState.GameStarted;
        } else if(touchPos.x > resetHistoryButton.x - resetHistoryButton.width &&
            touchPos.x < resetHistoryButton.x + resetHistoryButton.width &&
            touchPos.y > resetHistoryButton.y - resetHistoryButton.height &&
            touchPos.y < resetHistoryButton.y + resetHistoryButton.height){
            FileHandle sessionHistoryFile = Gdx.files.local("session_history.txt");
            sessionHistoryFile.writeString("", false);
            sessionHistory.clear();
        }
    }

    private void renderDisplayedNumbers() {
        spriteBatch.begin();
        for (int x = 0; x < displayedNumbers.size(); x++) {
            for (int y = 0; y < displayedNumbers.get(x).size(); y++) {
                for (int z = 0; z < displayedNumbers.get(x).get(y).size(); z++) {
                    String number = displayedNumbers.get(x).get(y).get(z);
                    if (number != null) {
                        Cube cube = box.findCubeByCoordinates(x, y, z);
                        Vector3 screenPosition = camera.project(cube.getCenter());
                        font.draw(spriteBatch, cube.getBombQuantity() + "", screenPosition.x, screenPosition.y);
                    }
                }
            }
        }
        spriteBatch.end();
    }

    private void initDisplayedNumbers() {
        displayedNumbers = new ArrayList<>(xsize);
        for (int i = 0; i < xsize; i++) {
            List<List<String>> layer = new ArrayList<>(ysize);
            for (int j = 0; j < ysize; j++) {
                List<String> row = new ArrayList<>(zsize);
                for (int k = 0; k < zsize; k++) {
                    row.add(null);
                }
                layer.add(row);
            }
            displayedNumbers.add(layer);
        }
    }

    public void removeCube(int x, int y, int z) {
        if (x >= 0 && x < xsize && y >= 0 && y < ysize && z >= 0 && z < zsize) {
            Cube cube = box.findCubeByCoordinates(x, y, z);
            if (cube.isBomb()) {
                gameState = GameState.GameOver;
            }
            else  if (box.checkCubesState()){
                gameState = GameState.GameWin;
            }
            else {
                cube.setState(State.Invisible);
                smallCubes.get(x).get(y).set(z, null);
                displayedNumbers.get(x).get(y).set(z, cube.getBombQuantity() + "");
            }
        }
    }

    public void changeFlag(int x, int y, int z) {
        if (x >= 0 && x < xsize && y >= 0 && y < ysize && z >= 0 && z < zsize) {
            Cube cube = box.findCubeByCoordinates(x, y, z);
            if (cube.getState() == State.Active)
                cube.setState(State.Flag);
            else if (cube.getState() == State.Flag)
                cube.setState(State.Active);
        }
    }

    @Override
    public void dispose() {
        modelBatch.dispose();
        backgroundTexture.dispose();
        for (List<List<ModelInstance>> layer : smallCubes) {
            for (List<ModelInstance> row : layer) {
                for (ModelInstance cube : row) {
                    cube.model.dispose();
                }
            }
        }
    }

    public void setBombs() {
        Random random = new Random();
        while (box.getBombsQuantity() < bombsQuantity) {
            int randomIndex = random.nextInt(box.getCubes().size());
            Cube cube = box.getCubes().get(randomIndex);
            if (!cube.isBomb()) {
                cube.setBomb(true);
            }
        }
    }

    public void setBombsQuantity() {
        for (Cube cube : box.getCubes()) {
            if (cube.isBomb()) {
                addBombQuantityToNeighbours(cube.getX(), cube.getY(), cube.getZ());
            }
        }
    }

    private void addBombQuantityToNeighbours(int x, int y, int z) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    if (dx == 0 && dy == 0 && dz == 0) {
                        continue;
                    }

                    int newX = x + dx;
                    int newY = y + dy;
                    int newZ = z + dz;

                    if (newX >= 0 && newX < xsize && newY >= 0 && newY < ysize && newZ >= 0 && newZ < zsize) {
                        box.findCubeByCoordinates(newX, newY, newZ).addBombQuantity();
                    }
                }
            }
        }
    }
}



