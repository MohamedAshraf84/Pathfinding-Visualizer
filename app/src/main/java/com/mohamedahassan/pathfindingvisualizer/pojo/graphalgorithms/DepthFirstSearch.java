package com.mohamedahassan.pathfindingvisualizer.pojo.graphalgorithms;

import android.util.Pair;
import com.mohamedahassan.pathfindingvisualizer.ui.MazeNode;
import com.mohamedahassan.pathfindingvisualizer.ui.NodeState;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;

public class DepthFirstSearch extends UnweightedPathFinder {

    public DepthFirstSearch(MazeNode[][] maze, Pair<Integer, Integer> start, Pair<Integer, Integer> end) {
        super(maze , start, end);
    }

    public Observable<MazeNode> visualize(Pair<Integer, Integer> start, Pair<Integer, Integer> end) {

        return Observable.create( emitter -> visualizeRecursive(start, end, emitter));
    }

    private void visualizeRecursive(Pair<Integer, Integer> start, Pair<Integer, Integer> end,
                                    ObservableEmitter<MazeNode> emitter) throws InterruptedException {

        int x = start.first, y = start.second;

        if (mMaze[x][y].getVisited() || mPathFound) {
            return;
        }

        mMaze[x][y].setVisited(true);

        if (start.equals(end)) {
            mPathFound = true;
            emitter.onComplete();
            return;
        }

        if (!start.equals(mStart)) {
            mMaze[x][y].setState(NodeState.CURRENT);
            emitter.onNext(mMaze[x][y]);
            Thread.sleep(mAnimationSpeed);

            mMaze[x][y].setState(NodeState.VISITED);
            emitter.onNext(mMaze[x][y]);
            Thread.sleep(mAnimationSpeed);
        }


        int newX , newY;
        for (int i = 0; i < dr.length; ++i) {
             newX = x + dr[i];
             newY = y + dc[i];
            if (isValid(newX, newY)) {
                mMaze[newX][newY].setParent(start);
                visualizeRecursive(new Pair<>(newX, newY), end, emitter);
            }
        }
    }
}
