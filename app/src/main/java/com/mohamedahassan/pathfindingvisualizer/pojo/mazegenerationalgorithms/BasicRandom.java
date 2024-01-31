package com.mohamedahassan.pathfindingvisualizer.pojo.mazegenerationalgorithms;

import com.mohamedahassan.pathfindingvisualizer.ui.MazeNode;
import com.mohamedahassan.pathfindingvisualizer.ui.NodeState;
import io.reactivex.rxjava3.core.Observable;

public class BasicRandom extends RandomMazeGenerator {

    private final double WALLS_PERCENTAGE = 1 - 0.25;
    public BasicRandom(int noOfRows, int noOfColumns) {
        super(noOfRows, noOfColumns);
    }
    @Override
    public Observable<MazeNode> generatePath(int x, int y) {
        return Observable.create( emitter -> {
            int NO_OF_WALLS = (int) (WALLS_PERCENTAGE * noOfRows * noOfColumns);
            int wallsCnt = 0;
            while(wallsCnt < NO_OF_WALLS)
            {
                int i = random.nextInt(noOfRows);
                int j = random.nextInt(noOfColumns);
                if (mMaze[i][j].getState() == NodeState.WALL)
                {
                    mMaze[i][j].setState(NodeState.EMPTY);
                    emitter.onNext(mMaze[i][j]);
                    wallsCnt++;
                }
            }
            emitter.onComplete();
        });
    }
}

