package com;

import com.javarush.engine.cell.*;

import java.util.ArrayList;
import java.util.List;

public class MinesweeperGame extends Game  {

    private static final int SIDE = 9;
    private final GameObject[][] gameField = new GameObject[SIDE][SIDE];
    private int countMinesOnField = 0;
    private static final String MINE = "\uD83D\uDCA3";
    private static final String FLAG = "\uD83D\uDEA9";
    private int countFlags;
    private boolean isGameStopped;
    private int countClosedTiles = SIDE * SIDE;
    private int score = 0;
    private boolean isFirstTurn = true;

    @Override
    public void initialize() {
        setScreenSize(SIDE, SIDE);
        createGame();
    }

    private void createGame() {
        for (int x = 0; x < SIDE; x++) {
            for (int y = 0; y < SIDE; y++) {
                boolean isMine = getRandomNumber(5) < 1;
                if (isMine) {
                    countMinesOnField++;
                }
                gameField[y][x] = new GameObject(x, y, isMine);
                setCellColor(x, y, Color.ORANGE);
                setCellValue(x, y, "");
            }
        }
        countMineNeighbors();
        countFlags = countMinesOnField;
    }

    private List<GameObject> getNeighbors(GameObject gameObject) {
        List<GameObject> result = new ArrayList<>();
        for (int y = gameObject.y - 1; y <= gameObject.y + 1; y++) {
            for (int x = gameObject.x - 1; x <= gameObject.x + 1; x++) {
                if (y < 0 || y >= SIDE) {
                    continue;
                }
                if (x < 0 || x >= SIDE) {
                    continue;
                }
                if (gameField[y][x] == gameObject) {
                    continue;
                }
                result.add(gameField[y][x]);
            }
        }
        return result;
    }

    private void countMineNeighbors() {
        for (int x = 0; x < SIDE; x++) {
            for (int y = 0; y < SIDE; y++) {
                if (!gameField[x][y].isMine) {
                    GameObject gameObject = gameField[x][y];
                    List<GameObject> listNeighbors = getNeighbors(gameObject);
                    for (GameObject listNeighbor : listNeighbors) {
                        if (listNeighbor.isMine) {
                            gameField[x][y].countMineNeighbors++;
                        }
                    }
                }
            }
        }
    }

    private void openTile(int x, int y) {
        GameObject gameObject = gameField[y][x];
        if (gameObject.isOpen)
            return;
        if (gameObject.isFlag)
            return;
        if (isGameStopped)
            return;
        gameObject.isOpen = true;
        countClosedTiles--;
        if (gameObject.isMine && isFirstTurn) {
            restart();
            openTile(x, y);
        }
        else if (gameObject.isMine) {
            setCellColor(x, y, Color.RED);
            setCellValue(x, y, MINE);
            setCellValueEx(x, y, Color.RED, MINE);
            gameOver();
        }
        else if (countClosedTiles == countMinesOnField) {
            score = score + 5;
            win();
        }
        else if (gameObject.countMineNeighbors == 0) {
            score = score + 5;
            setCellColor(x, y, Color.GREEN);
            setCellValue(x, y, "");
            for (GameObject neighbor :
                    getNeighbors(gameObject)) {
                openTile(neighbor.x, neighbor.y);
            }
        }
        else {
            score = score + 5;
            setCellNumber(x, y, gameObject.countMineNeighbors);
            setCellColor(x, y, Color.GREEN);
        }
        setScore(score);
        isFirstTurn = false;
    }

    @Override
    public void onMouseLeftClick(int x, int y) {
        if (isGameStopped)
            restart();
        else {
            openTile(x, y);
        }
    }

    private void markTile(int x, int y) {
        if (!isGameStopped) {
            GameObject gameObject = gameField[y][x];
            if (gameObject.isOpen) {
            } else if (countFlags == 0 && !gameObject.isFlag) {
            } else if (!gameObject.isFlag) {
                gameObject.isFlag = true;
                countFlags--;
                setCellValue(x, y, FLAG);
                setCellColor(x, y, Color.YELLOW);
            } else {
                gameObject.isFlag = false;
                countFlags++;
                setCellValue(x, y, "");
                setCellColor(x, y, Color.ORANGE);
            }
        }
    }

    @Override
    public void onMouseRightClick(int x, int y) {
        markTile(x, y);
    }

    private void gameOver() {
        isGameStopped = true;
        showMessageDialog(Color.RED, " Game Over ", Color.BLACK, 50);
    }

    private void win() {
        isGameStopped = true;
        showMessageDialog(Color.GREEN, " Victory!!! ", Color.BLACK, 50);
    }

    private void restart() {
        isGameStopped = false;
        countClosedTiles = SIDE * SIDE;
        score = 0;
        countMinesOnField = 0;
        isFirstTurn = true;
        setScore(score);
        createGame();
    }


}