package com.mohamedahassan.pathfindingvisualizer.pojo.graphalgorithms;

import android.os.Build;
import android.util.Pair;
import com.mohamedahassan.pathfindingvisualizer.ui.MazeNode;
import com.mohamedahassan.pathfindingvisualizer.ui.NodeState;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.concurrent.TimeUnit;
import io.reactivex.rxjava3.core.Observable;

public class Dijkstra extends WeightedPathFinder {

    public Dijkstra(MazeNode[][] maze, Pair<Integer, Integer> start, Pair<Integer, Integer> end) {
        super(maze , start, end);
    }

    @Override
    public Observable<MazeNode> visualize(Pair<Integer, Integer> start, Pair<Integer, Integer> end) {

        return Observable.create(emitter -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                mPQ = new PriorityQueue<>(new WeightedPathFinder.SortByTotalCost());

            mMaze[start.first][start.second].setCost(0);
            mMaze[start.first][start.second].setVisited(true);

            mPQ.add(new Pair<>(0, start));

            while (!mPQ.isEmpty()) {
                Pair<Integer, Integer> current = Objects.requireNonNull(mPQ.poll()).second;
                assert current != null;

                Pair<Integer, Integer> neighbour;
                int newX, newY;

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

                        int costToCurrent = mMaze[current.first][current.second].getCost();
                        int edgeCost = mMaze[newX][newY].getWeight() == 0 ? 1 : mMaze[newX][newY].getWeight();
                        int newCost = costToCurrent + edgeCost;

                        if (newCost < mMaze[newX][newY].getCost()) {
                            mMaze[newX][newY].setCost(newCost);
                            mPQ.add(new Pair<>(newCost, new Pair<>(newX, newY)));
                            mMaze[newX][newY].setParent(new Pair<>(current.first, current.second));
                        }
                        mMaze[newX][newY].setState(NodeState.CURRENT);
                        emitter.onNext(mMaze[newX][newY]);
                        Thread.sleep(TimeUnit.SECONDS.toSeconds(mAnimationSpeed));

                        mMaze[newX][newY].setVisited(true);
                        mMaze[newX][newY].setState((mMaze[newX][newY].getWeight() == 0) ? NodeState.VISITED : NodeState.VISITED_WEIGHT);

                        emitter.onNext(mMaze[newX][newY]);
                        Thread.sleep(TimeUnit.SECONDS.toSeconds(mAnimationSpeed));
                    }
                }

                if (isPathFound())
                    break;
            }
        });
    }

}
