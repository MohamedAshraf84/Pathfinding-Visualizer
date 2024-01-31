package com.mohamedahassan.pathfindingvisualizer.pojo.mazegenerationalgorithms;

import android.util.Pair;
import com.mohamedahassan.pathfindingvisualizer.ui.MazeNode;
import com.mohamedahassan.pathfindingvisualizer.ui.NodeState;
import java.security.SecureRandom;
import io.reactivex.rxjava3.core.Observable;

public abstract class RandomMazeGenerator {

    protected final MazeNode[][] mMaze;
    protected final int noOfRows;
    protected final int noOfColumns;
    protected final SecureRandom random;
    protected int mSpeed = 1;

    protected int[][] directions = {{0, -1}, {0, 1}, {-1, 0}, {1, 0}};

    public RandomMazeGenerator(int noOfRows, int noOfColumns) {
        this.random = new SecureRandom();
        this.noOfRows = noOfRows;
        this.noOfColumns = noOfColumns;
        this.mMaze = new MazeNode[noOfRows][noOfColumns];
        initMaze();
    }


    public Observable<MazeNode> generateMaze() {
        resetMaze();
        return generatePath(random.nextInt(noOfRows), random.nextInt(noOfColumns));
    }

    protected void resetMaze() {
        for (int i = 0; i < noOfRows; ++i)
        {
            for (int j = 0; j < noOfColumns; ++j) {
                mMaze[i][j].setVisited(false);
                mMaze[i][j].setParent(null);
                mMaze[i][j].setWeight(0);
                mMaze[i][j].setCost(Integer.MAX_VALUE);
                mMaze[i][j].setState(NodeState.WALL);
            }
        }
    }

    private void initMaze()
    {
        for (int i = 0; i < noOfRows; ++i)
            for (int j = 0; j < noOfColumns; ++j) {
                mMaze[i][j] = new MazeNode(new Pair<>(i, j), NodeState.WALL);
            }
    }

    abstract Observable<MazeNode> generatePath(int x, int y);


    protected boolean isValid(int x, int y) {

        return  x >= 0 && x < noOfRows &&
                y >= 0 && y < noOfColumns &&
                mMaze[x][y].getState() == NodeState.WALL &&
                !mMaze[x][y].getVisited();
    }

   /* public List<Pair<Integer, Integer>> getNeighbours(int x, int y)
    {
        int[][] directions = {{0, -1}, {0, 1}, {-1, 0}, {1, 0}};

        List<Pair<Integer, Integer>> frontier = new ArrayList<>();
        //List<Pair<Integer, Integer>> curCellFrontier = new ArrayList<>();
        for (int []dir : directions)
        {
            int newX = x + dir[0] * 2;
            int newY = y + dir[1] * 2;
            if (isValid(newX, newY))
                frontier.add(*//*new Pair<>(new Pair<>(newX, newY),
                        new Pair<>(x + dir[0], y + dir[1]))*//*
                        new Pair<>(newX, newY)
                );

        }

        return frontier;
    }
*/

}
