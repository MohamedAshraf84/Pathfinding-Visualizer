package com.mohamedahassan.pathfindingvisualizer.pojo.graphalgorithms;

import android.util.Pair;
import com.mohamedahassan.pathfindingvisualizer.ui.MazeNode;
import com.mohamedahassan.pathfindingvisualizer.ui.NodeState;
import java.util.Objects;
import java.util.PriorityQueue;
import io.reactivex.rxjava3.core.Observable;

public class GreedyBestFirstSearch extends WeightedPathFinder {

    public GreedyBestFirstSearch(MazeNode[][] maze, Pair<Integer, Integer> start, Pair<Integer, Integer> end) {
        super(maze , start, end);
    }
    @Override
    public Observable<MazeNode> visualize(Pair<Integer, Integer> start, Pair<Integer, Integer> end) {
        return Observable.create( emitter -> {

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N)
                mPQ = new PriorityQueue<>(new WeightedPathFinder.SortByTotalCost());

            assert mPQ != null;
            mPQ.add(new Pair<>(0, start));
            mMaze[start.first][start.second].setVisited(true);

            while (!mPQ.isEmpty()) {
                Pair<Integer, Integer> current = Objects.requireNonNull(mPQ.poll()).second;
                assert current != null;

                int newX, newY;
                Pair<Integer, Integer> neighbour;
                for (int i = 0; i < dr.length; ++i) {
                    newX = current.first + dr[i];
                    newY = current.second + dc[i];
                    neighbour = new Pair<>(newX, newY);

                    if (isValid(newX, newY)) {

                        if (neighbour.equals(end)) {
                            mMaze[newX][newY].setParent(new Pair<>(current.first, current.second));
                            mPathFound = true;
                            emitter.onComplete();
                            break;
                        }

                        int heuristicValue = manhattenDistance(mMaze[neighbour.first][neighbour.second],
                                                                mMaze[end.first][end.second]);
                        mPQ.add(new Pair<>(heuristicValue, neighbour));

                        mMaze[newX][newY].setState(NodeState.CURRENT);
                        emitter.onNext(mMaze[newX][newY]);
                        Thread.sleep(mAnimationSpeed);

                        mMaze[newX][newY].setVisited(true);
                        mMaze[newX][newY].setParent(current);
                        mMaze[newX][newY].setState((mMaze[newX][newY].getWeight() == 0) ? NodeState.VISITED : NodeState.VISITED_WEIGHT);
                        emitter.onNext(mMaze[newX][newY]);
                        Thread.sleep(mAnimationSpeed);
                    }
                }

                if (isPathFound())
                    break;
            }
        });
    }
}
