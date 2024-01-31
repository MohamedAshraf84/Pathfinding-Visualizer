package com.mohamedahassan.pathfindingvisualizer.pojo.graphalgorithms;

import android.util.Pair;
import com.mohamedahassan.pathfindingvisualizer.ui.MazeNode;
import com.mohamedahassan.pathfindingvisualizer.ui.NodeState;
import java.util.LinkedList;
import java.util.Queue;
import io.reactivex.rxjava3.core.Observable;
public class BreadthFirstSearch extends UnweightedPathFinder {

    public BreadthFirstSearch(MazeNode[][] maze, Pair<Integer, Integer> start, Pair<Integer, Integer> end) {
        super(maze , start, end);
    }

    @Override
    public Observable<MazeNode> visualize(Pair<Integer, Integer> start, Pair<Integer, Integer> end) {
        return Observable.create( emitter -> {
            final Queue<Pair<Integer, Integer>> bfsQueue = new LinkedList<>();
            bfsQueue.add(start);
            mMaze[start.first][start.second].setVisited(true);

            while (!bfsQueue.isEmpty()) {
                Pair<Integer, Integer> current = bfsQueue.poll();
                assert current != null;

                Pair<Integer, Integer> neighbour;
                for (int i = 0; i < dr.length; ++i) {
                    int rr = current.first + dr[i];
                    int cc = current.second + dc[i];
                    neighbour = new Pair<>(rr, cc);

                    if (isValid(rr, cc)) {

                        if (neighbour.equals(end)) {
                            mMaze[rr][cc].setParent(new Pair<>(current.first, current.second));
                            mPathFound = true;
                            emitter.onComplete();
                            break;
                        }

                        bfsQueue.add(new Pair<>(rr, cc));
                        mMaze[rr][cc].setState(NodeState.CURRENT);
                        emitter.onNext(mMaze[rr][cc]);
                        Thread.sleep(mAnimationSpeed);

                        mMaze[rr][cc].setVisited(true);
                        mMaze[rr][cc].setState(NodeState.VISITED);
                        mMaze[rr][cc].setParent(current);
                        emitter.onNext(mMaze[rr][cc]);
                        Thread.sleep(mAnimationSpeed);
                    }
                }

                if (mPathFound) {
                    break;
                }
            }
        });
    }
}