package com.mohamedahassan.pathfindingvisualizer.pojo.graphalgorithms;

import android.util.Pair;
import com.mohamedahassan.pathfindingvisualizer.ui.MazeNode;

abstract class UnweightedPathFinder extends PathFinderVisualizer{
    public UnweightedPathFinder(MazeNode[][] maze, Pair<Integer, Integer> start, Pair<Integer, Integer> end) {
        super(maze, start, end);
    }
}
