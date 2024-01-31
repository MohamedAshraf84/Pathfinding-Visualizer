package com.mohamedahassan.pathfindingvisualizer.pojo.mazegenerationalgorithms;

import com.mohamedahassan.pathfindingvisualizer.ui.MazeNode;
import com.mohamedahassan.pathfindingvisualizer.ui.NodeState;
import android.util.Pair;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import io.reactivex.rxjava3.core.Observable;

public class RandomizedPrimAlgorithm extends RandomMazeGenerator {

    public RandomizedPrimAlgorithm(int noOfRows, int noOfColumns) {
        super(noOfRows, noOfColumns);
    }

    @Override
    public Observable<MazeNode> generatePath(int x, int y) {
        return Observable.create( emitter -> {

            final List<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> frontier = new ArrayList<>();

            frontier.add(new Pair<>(new Pair<>(x, y), null));
            mMaze[x][y].setState(NodeState.EMPTY);
            emitter.onNext(mMaze[x][y]);

            while (!frontier.isEmpty()) {
                Collections.shuffle(frontier);

                Pair<Integer, Integer> cur = frontier.get(0).first;
                Pair<Integer, Integer> parent = frontier.remove(0).second;

                for (int[] dir : directions) {
                    int newX = cur.first + dir[0] * 2;
                    int newY = cur.second + dir[1] * 2;
                    if (isValid(newX, newY)) {
                        frontier.add(new Pair<>(new Pair<>(newX, newY),
                                new Pair<>(cur.first + dir[0], cur.second + dir[1])));
                    }
                }

                if (frontier.isEmpty() || parent == null)
                    continue;

                if ( mMaze[cur.first][cur.second].getState() == NodeState.WALL &&
                     mMaze[parent.first][parent.second].getState() == NodeState.WALL) {

                    int wallX = parent.first;
                    int wallY = parent.second;
                    mMaze[wallX][wallY].setVisited(true);
                    mMaze[wallX][wallY].setState(NodeState.EMPTY);

                    emitter.onNext(mMaze[wallX][wallY]);
                    Thread.sleep(mSpeed);

                    mMaze[cur.first][cur.second].setVisited(true);
                    mMaze[cur.first][cur.second].setState(NodeState.EMPTY);
                    emitter.onNext(mMaze[cur.first][cur.second]);
                    Thread.sleep(mSpeed);
                }
            }
            emitter.onComplete();
        });
    }
}
