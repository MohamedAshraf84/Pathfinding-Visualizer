package com.mohamedahassan.pathfindingvisualizer.pojo.graphalgorithms;

import android.os.Build;
import android.util.Pair;
import com.mohamedahassan.pathfindingvisualizer.ui.MazeNode;
import com.mohamedahassan.pathfindingvisualizer.ui.NodeState;
import java.util.Objects;
import java.util.PriorityQueue;
import io.reactivex.rxjava3.core.Observable;

public class AStar extends WeightedPathFinder {

    public AStar(MazeNode[][] maze, Pair<Integer, Integer> start, Pair<Integer, Integer> end) {
        super(maze, start, end);
    }

    @Override
    public Observable<MazeNode> visualize(Pair<Integer, Integer> start, Pair<Integer, Integer> end) {
        return Observable.create(emitter -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mPQ = new PriorityQueue<>(new WeightedPathFinder.SortByTotalCost());
            }

            MazeNode startNode = mMaze[start.first][start.second];
            MazeNode endNode = mMaze[end.first][end.second];
            mMaze[start.first][start.second].setCost(0);
            mPQ.add(new Pair<>(manhattenDistance(startNode, endNode), start));
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

                        int newCost = gCost(mMaze[current.first][current.second], mMaze[newX][newY]);
                        if (newCost < mMaze[newX][newY].getCost()) {
                            mMaze[newX][newY].setCost(newCost);
                            int h = manhattenDistance(mMaze[newX][newY], mMaze[end.first][end.second]);
                            mPQ.add(new Pair<>(newCost + h, new Pair<>(newX, newY)));
                        }
                        mMaze[newX][newY].setState(NodeState.CURRENT);
                        emitter.onNext(mMaze[newX][newY]);
                        Thread.sleep(mAnimationSpeed);

                        mMaze[newX][newY].setVisited(true);
                        mMaze[newX][newY].setState(NodeState.VISITED_WEIGHT);
                        mMaze[newX][newY].setParent(new Pair<>(current.first, current.second));
                        emitter.onNext(mMaze[newX][newY]);
                        Thread.sleep(mAnimationSpeed);
                    }
                }
                if (isPathFound())
                    break;
            }
        });
    }

    private int gCost(MazeNode a, MazeNode b)
    {
        return a.getCost() + b.getWeight();
    }

}
