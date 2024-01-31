package com.mohamedahassan.pathfindingvisualizer.ui;

import android.util.Pair;

import androidx.annotation.NonNull;

import org.w3c.dom.Node;

public class MazeNode implements Cloneable {

    private Pair<Integer, Integer> coordinate;
    private Pair<Integer, Integer> parent;
    private NodeState state;
    private boolean isVisited;
    private int weight;
    private int cost;

    public MazeNode(Pair<Integer, Integer> coordinate)
    {
        this.coordinate = coordinate;
        this.cost = Integer.MAX_VALUE;
        this.state = NodeState.EMPTY;
    }
    public MazeNode(Pair<Integer, Integer> coordinate, NodeState state)
    {
        this(coordinate);
        this.state = state;
    }

    @NonNull
    @Override
    public String toString() {
        return String.format("[ Coord: %s | Parent: %s | State: %s | isVisited: %s ]",
                coordinate.toString(), parent.toString(), state.toString(), isVisited);
    }


    public void setCoordinate(Pair<Integer, Integer> coordinate) {
        this.coordinate = coordinate;
    }

    public void setParent(Pair<Integer, Integer> parent) {
        this.parent = parent;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public void setVisited(boolean visited) {
        isVisited = visited;
    }

    public void setState(NodeState state) {
        this.state = state;
    }

    public Pair<Integer, Integer> getCoordinate() {
        return coordinate;
    }

    public Pair<Integer, Integer> getParent() {
        return parent;
    }

    public NodeState getState() {
        return state;
    }

    public boolean getVisited() {
        return isVisited;
    }

    public int getWeight() {
        return weight;
    }

    public int getCost() {
        return cost;
    }

    @NonNull
    @Override
    public MazeNode clone() {
        try {
            MazeNode cloned = (MazeNode) super.clone();
            cloned.coordinate = new Pair<>(coordinate.first, coordinate.second);
            cloned.parent = new Pair<>(parent.first, parent.second);
            cloned.isVisited = isVisited;
            cloned.state = state;
            cloned.weight = weight;
            cloned.cost = cost;
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
