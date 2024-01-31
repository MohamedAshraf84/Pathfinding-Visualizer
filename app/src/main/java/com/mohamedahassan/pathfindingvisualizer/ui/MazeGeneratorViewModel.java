package com.mohamedahassan.pathfindingvisualizer.ui;

import android.util.Log;
import androidx.lifecycle.ViewModel;
import com.mohamedahassan.pathfindingvisualizer.pojo.mazegenerationalgorithms.RandomMazeGenerator;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MazeGeneratorViewModel extends ViewModel {
    private RandomMazeGenerator mazeGenerator;
    public void setMazeGenerationAlgorithm(RandomMazeGenerator mazeGenerator)
    {
        this.mazeGenerator = mazeGenerator;
    }
    public void generateMaze(Observer<MazeNode> observer)
    {
        mazeGenerator.generateMaze()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
}
