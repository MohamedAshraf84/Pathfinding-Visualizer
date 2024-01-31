package com.mohamedahassan.pathfindingvisualizer.pojo.mazegenerationalgorithms;

import com.mohamedahassan.pathfindingvisualizer.ui.MazeNode;
import com.mohamedahassan.pathfindingvisualizer.ui.NodeState;
import java.util.Arrays;
import java.util.Collections;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;

public class RecursiveBackTracker extends RandomMazeGenerator {
    public RecursiveBackTracker(int noOfRows, int noOfColumns) {
        super(noOfRows, noOfColumns);
    }

    protected Observable<MazeNode> generatePath(int x, int y) {
        return Observable.create(emitter -> {
            recursiveGenerate(x, y, emitter);
            emitter.onComplete();
        });
    }

    private void recursiveGenerate(int x, int y, ObservableEmitter<MazeNode> emitter) throws InterruptedException
    {
        mMaze[x][y].setVisited(true);
        mMaze[x][y].setState(NodeState.EMPTY);
        emitter.onNext(mMaze[x][y]);
        Thread.sleep(1);

        Collections.shuffle(Arrays.asList(directions));

        for (int[] direction : directions) {
            int newX = x + direction[0] * 2;
            int newY = y + direction[1] * 2;

            if (isValid(newX, newY)) {
                int wallX = x + direction[0];
                int wallY = y + direction[1];

                mMaze[wallX][wallY].setState(NodeState.EMPTY);
                mMaze[wallX][wallY].setVisited(true);
                emitter.onNext(mMaze[wallX][wallY]);
                Thread.sleep(mSpeed);
                recursiveGenerate(newX, newY, emitter);
            }
        }
    }
}

