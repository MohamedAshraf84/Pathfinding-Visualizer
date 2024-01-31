package com.mohamedahassan.pathfindingvisualizer.pojo.graphalgorithms;

import android.util.Pair;
import com.mohamedahassan.pathfindingvisualizer.ui.MazeNode;
import java.util.Comparator;
import java.util.PriorityQueue;

abstract class WeightedPathFinder extends PathFinderVisualizer {
    PriorityQueue<Pair<Integer, Pair<Integer, Integer>>> mPQ;
    public WeightedPathFinder(MazeNode[][] maze, Pair<Integer, Integer> start, Pair<Integer, Integer> end) {
        super(maze, start, end);
    }

    protected int manhattenDistance(MazeNode a, MazeNode b)
    {
        Pair<Integer, Integer> aCoord, bCoord;
        aCoord = a.getCoordinate();
        bCoord = b.getCoordinate();
        return Math.abs(aCoord.first - bCoord.first) +
                Math.abs(aCoord.second - bCoord.second);
    }

    protected static class SortByTotalCost implements Comparator<Pair<Integer, Pair<Integer, Integer>>> {
        @Override
        public int compare(Pair<Integer, Pair<Integer, Integer>> a, Pair<Integer, Pair<Integer, Integer>> b) {
            return a.first - b.first;
        }
    }
}
