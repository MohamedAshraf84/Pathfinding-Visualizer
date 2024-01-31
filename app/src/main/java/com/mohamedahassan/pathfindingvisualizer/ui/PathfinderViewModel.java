package com.mohamedahassan.pathfindingvisualizer.ui;

import androidx.lifecycle.ViewModel;
import com.mohamedahassan.pathfindingvisualizer.pojo.graphalgorithms.*;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class PathfinderViewModel extends ViewModel {
    private PathFinderVisualizer pathFinderVisualizer;
    private int delay = 3;

    public void setPathFinderAlgorithm(PathFinderVisualizer pathFinderVisualizer)
    {
        this.pathFinderVisualizer = pathFinderVisualizer;
    }

    public void changeAnimationSpeed(int delay)
    {
        this.delay = delay;
        if (pathFinderVisualizer != null)
            pathFinderVisualizer.setAnimationSpeed(delay);
    }
    public void visualize(Observer<MazeNode> observer)
    {
        pathFinderVisualizer.setAnimationSpeed(delay);
        pathFinderVisualizer.visualize().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
}
