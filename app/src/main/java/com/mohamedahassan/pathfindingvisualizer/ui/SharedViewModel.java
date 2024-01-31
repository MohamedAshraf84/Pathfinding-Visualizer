package com.mohamedahassan.pathfindingvisualizer.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<Integer> nodeSize = new MutableLiveData<>();
    private final MutableLiveData<Integer> animationSpeed = new MutableLiveData<>();
    public MutableLiveData<Integer> getNodeSize() {
        return nodeSize;
    }
    public MutableLiveData<Integer> getAnimationSpeed() {
        return animationSpeed;
    }

    public void setNodeSize(int nodeSize)
    {
        this.nodeSize.setValue(nodeSize);
    }
    public void setAnimationSpeed(int speed)
    {
        this.animationSpeed.setValue(speed);
    }
}
