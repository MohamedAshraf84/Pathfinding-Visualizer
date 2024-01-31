package com.mohamedahassan.pathfindingvisualizer.pojo.graphalgorithms;

import android.util.Pair;
import com.mohamedahassan.pathfindingvisualizer.ui.MazeNode;
import com.mohamedahassan.pathfindingvisualizer.ui.NodeState;
import java.util.ArrayList;
import java.util.Collections;
import io.reactivex.rxjava3.core.Observable;

public abstract class PathFinderVisualizer {

    protected Pair<Integer, Integer> mStart, mEnd;
    public final ArrayList<Pair<Integer, Integer>> mPath;
    public boolean mPathFound;
    private int noOfRows;
    private int noOfColumns;

    protected MazeNode[][]mMaze;
    protected int []dr = new int[]{-1, 1, 0, 0};
    protected int []dc = new int[]{0, 0, -1, 1};
    protected int mAnimationSpeed = 3;

    public void setMaze(MazeNode[][] maze) {
        mMaze = maze;
        noOfRows = this.mMaze.length;
        noOfColumns = this.mMaze[0].length;
        mPathFound = false;
        clearPath();
    }

    public void setStart(Pair<Integer, Integer> start) {
        this.mStart = start;
    }
    public void setEnd(Pair<Integer, Integer> end) {
        this.mEnd = end;
    }
    public void setAnimationSpeed(int speed)
    {
        mAnimationSpeed = speed;
    }


    public PathFinderVisualizer(MazeNode[][] maze, Pair<Integer, Integer> start, Pair<Integer, Integer> end)
    {
        this.setMaze(maze);
        this.setStart(start);
        this.setEnd(end);
        this.mPathFound = false;
        this.mPath = new ArrayList<>();
    }

    public boolean isValid(int r, int c)
    {
        return  r >= 0 && r < noOfRows &&
                c >= 0 && c < noOfColumns &&
                !mMaze[r][c].getVisited() &&
                mMaze[r][c].getState() != NodeState.WALL;
    }

    protected void clearPath()
    {
        if (mPath != null)
            mPath.clear();
    }

    public Observable<MazeNode> visualize()
    {
        return visualize(mStart, mEnd).concatWith(drawPath());
                /*.concatMap(item -> Observable.just(item).delay(1, TimeUnit.SECONDS));*/
    }

    public abstract Observable<MazeNode> visualize(Pair<Integer, Integer> start, Pair<Integer, Integer> end);

    public boolean isPathFound()
    {
        return mPathFound;
    }

    private void reconstructPath()
    {
        if (isPathFound())
        {
            //Stack<Pair<Integer, Integer>> stack = new Stack<>();
            Pair<Integer, Integer> current = mMaze[mEnd.first][mEnd.second].getParent();

            while ( !current.equals(mStart)) {
                //stack.add(cur);
                mPath.add(current);
                current = mMaze[current.first][current.second].getParent();
            }

            Collections.reverse(mPath);
            /*while (!stack.isEmpty())
                mPath.add(stack.pop());*/

            mPath.add(mEnd);
        }
    }
    private Observable<MazeNode> drawPath()
    {
        return Observable.create( emitter -> {
            if (mPathFound) {

                reconstructPath();
                MazeNode temp;
                for (int i = 0; i < mPath.size() - 1; ++i) {
                    int x = mPath.get(i).first, y = mPath.get(i).second;
                    int xNext = mPath.get(i + 1).first, yNext = mPath.get(i + 1).second;

                    temp = mMaze[x][y].clone();

                    if (xNext > x) {
                        mMaze[x][y].setState(NodeState.PATH_D);
                    } else if (xNext < x) {
                        mMaze[x][y].setState(NodeState.PATH_U);
                    } else if (yNext > y) {
                        mMaze[x][y].setState(NodeState.PATH_R);
                    } else if (yNext < y) {
                        mMaze[x][y].setState(NodeState.PATH_L);
                    }
                    emitter.onNext(mMaze[x][y]);
                    Thread.sleep(mAnimationSpeed); //Not a good idea CONSIDER remove it and use author approach.

                    temp.setState((temp.getState() == NodeState.VISITED_WEIGHT) ? NodeState.WEIGHT_PATH : NodeState.PATH);
                    emitter.onNext(temp);
                }
                emitter.onComplete();
            }
        });
    }
}
