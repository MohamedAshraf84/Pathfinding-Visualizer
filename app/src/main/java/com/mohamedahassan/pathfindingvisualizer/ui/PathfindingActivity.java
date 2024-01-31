package com.mohamedahassan.pathfindingvisualizer.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import com.mohamedahassan.pathfindingvisualizer.R;
import com.mohamedahassan.pathfindingvisualizer.databinding.ActivityPathfindingBinding;
import com.mohamedahassan.pathfindingvisualizer.pojo.mazegenerationalgorithms.BasicRandom;
import com.mohamedahassan.pathfindingvisualizer.pojo.mazegenerationalgorithms.RandomizedPrimAlgorithm;
import com.mohamedahassan.pathfindingvisualizer.pojo.mazegenerationalgorithms.RecursiveBackTracker;
import com.mohamedahassan.pathfindingvisualizer.pojo.graphalgorithms.AStar;
import com.mohamedahassan.pathfindingvisualizer.pojo.graphalgorithms.BreadthFirstSearch;
import com.mohamedahassan.pathfindingvisualizer.pojo.graphalgorithms.DepthFirstSearch;
import com.mohamedahassan.pathfindingvisualizer.pojo.graphalgorithms.Dijkstra;
import com.mohamedahassan.pathfindingvisualizer.pojo.graphalgorithms.GreedyBestFirstSearch;
import com.mohamedahassan.pathfindingvisualizer.pojo.graphalgorithms.PathFinderVisualizer;

public class PathfindingActivity extends AppCompatActivity
        /*implements BottomDialogFragment.FragmentCommunicator*/ {
    private ActivityPathfindingBinding binding;
    private MazeGeneratorViewModel mazeGeneratorViewModel;
    private PathfinderViewModel pathFinderViewModel;
    private PathFinderVisualizer mPathFinderVisualizer;
    private MazeNode[][] maze;
    private SharedViewModel vm;
    private BottomDialogFragment configFragment;
    private String chosenAlgo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Hide App bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_pathfinding);
        binding.setLifecycleOwner(this);


        configFragment = new BottomDialogFragment();

        // ViewModel Providers
        mazeGeneratorViewModel = new ViewModelProvider(this).get(MazeGeneratorViewModel.class);
        pathFinderViewModel = new ViewModelProvider(this).get(PathfinderViewModel.class);

        vm = new ViewModelProvider(this).get(SharedViewModel.class);

        vm.getNodeSize().observe(this, nodeSize -> binding.pathfindingCanvas.resizeGrid(nodeSize));
        vm.getAnimationSpeed().observe(this, speed -> pathFinderViewModel.changeAnimationSpeed(speed));

        // Pathfinding & Maze Generation Algorithms Spinners
        binding.bottom.pathfindingAlgorithmsSpinner
                .setOnItemSelectedListener(new PathfindingAlgorithmsSpinner());
        binding.bottom.randomMazeSpinner
                .setOnItemSelectedListener(new MazeAlgorithmsSpinner());

        // Adapter for populating algorithms spinner
        ArrayAdapter<String> algorithmSpinnerAdapter =
                new ArrayAdapter<>
                        (this,
                                android.R.layout.simple_spinner_item,
                                getResources().getStringArray(R.array.algorithms_choices)
                        );

        algorithmSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.bottom.pathfindingAlgorithmsSpinner.setAdapter(algorithmSpinnerAdapter);

        // Adapter fot populating Maze Generation spinner
        ArrayAdapter<String> randomMazeGeneratorSpinnerAdapter =
                new ArrayAdapter<>
                        (this,
                                 android.R.layout.simple_spinner_item,
                                 getResources().getStringArray(R.array.mazes_choices)
                        );
        randomMazeGeneratorSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.bottom.randomMazeSpinner.setAdapter(randomMazeGeneratorSpinnerAdapter);

        binding.bottom.visualizeBtn.setOnClickListener( v ->
        {
            if(mPathFinderVisualizer != null)
            {
                maze = binding.pathfindingCanvas.mMaze.clone();
                mPathFinderVisualizer.setMaze(maze);
                mPathFinderVisualizer.setStart(binding.pathfindingCanvas.getStart());
                mPathFinderVisualizer.setEnd(binding.pathfindingCanvas.getEnd());
                pathFinderViewModel.setPathFinderAlgorithm(mPathFinderVisualizer);
                pathFinderViewModel.visualize(binding.pathfindingCanvas.getPathFinderObserver());
            }
            else
            {
                Toast.makeText(this, "Choose an Algorithm first.", Toast.LENGTH_SHORT).show();
            }

        });

        binding.bottom.clearAllBtn.setOnClickListener(v -> binding.pathfindingCanvas.clear());
        binding.bottom.clearPathBtn.setOnClickListener(v -> binding.pathfindingCanvas.clearPath());
        binding.bottom.configBtn.setOnClickListener( view -> configFragment.show(getSupportFragmentManager(), "configDialog"));

        handleNodeToDrawClick();
    }



    private void handleNodeToDrawClick()
    {

        View.OnClickListener nodeClickListener = selectNode();
        binding.header.startNode.setOnClickListener(nodeClickListener);
        binding.header.endNode.setOnClickListener(nodeClickListener);
        binding.header.wallNode.setOnClickListener(nodeClickListener);
        binding.header.weightNode.setOnClickListener(nodeClickListener);
    }

    private View.OnClickListener selectNode() {
        return view -> {
            resetNodeBackgrounds();

            if (view == binding.header.startNode) {
                binding.pathfindingCanvas.setSelectedNode(PathfindingCanvas.START_NODE);
                binding.header.startNode.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.selected_node_border_rect, null));
            } else if (view == binding.header.endNode) {
                binding.pathfindingCanvas.setSelectedNode(PathfindingCanvas.END_NODE);
                binding.header.endNode.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.selected_node_border_rect, null));

            } else if (view == binding.header.wallNode) {
                binding.pathfindingCanvas.setSelectedNode(PathfindingCanvas.WALL_NODE);
                binding.header.wallNode.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.selected_node_border_rect, null));

            } else if (view == binding.header.weightNode) {
                binding.pathfindingCanvas.setSelectedNode(PathfindingCanvas.WEIGHT_NODE);
                binding.header.weightNode.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.selected_node_border_rect, null));
            }
        };
    }


    private void resetNodeBackgrounds() {
        binding.header.startNode.setBackground(null);
        binding.header.endNode.setBackground(null);
        binding.header.wallNode.setBackground(null);
        binding.header.weightNode.setBackground(null);
    }

    class MazeAlgorithmsSpinner implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (position != 0)
            {
                parent.setSelection(0);
                int noOfRows = PathfindingCanvas.NUM_OF_ROWS, noOfColumns = PathfindingCanvas.NUM_OF_COLUMNS;

                if (position == 1) {
                    mazeGeneratorViewModel.setMazeGenerationAlgorithm(
                            new BasicRandom(noOfRows, noOfColumns));
                }
                else if (position == 2) {

                    mazeGeneratorViewModel.setMazeGenerationAlgorithm(
                            new RecursiveBackTracker(noOfRows, noOfColumns));

                }
                else if (position == 3) {
                    mazeGeneratorViewModel.setMazeGenerationAlgorithm(
                            new RandomizedPrimAlgorithm(noOfRows, noOfColumns));
                }

                mazeGeneratorViewModel.generateMaze(binding.pathfindingCanvas.getMazeGenerationObserver());
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }


   class PathfindingAlgorithmsSpinner implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            if (parent.getItemAtPosition(position) != parent.getItemAtPosition(0)) {
                maze = binding.pathfindingCanvas.mMaze.clone();
                Pair<Integer, Integer> start = binding.pathfindingCanvas.getStart();
                Pair<Integer, Integer> end = binding.pathfindingCanvas.getEnd();

                if (position == 1) {
                    mPathFinderVisualizer =  new DepthFirstSearch(maze, start, end);
                    chosenAlgo = getString(R.string.dfs);

                }
                else if (position == 2) {
                    mPathFinderVisualizer = new BreadthFirstSearch(maze, start, end);
                    chosenAlgo = getString(R.string.bfs);

                }
                else if (position == 3)
                {
                    mPathFinderVisualizer = new GreedyBestFirstSearch(maze, start, end);
                    chosenAlgo = getString(R.string.greedy_bfs);
                }
                else if (position == 4)
                {
                    mPathFinderVisualizer = new Dijkstra(maze, start, end);
                    chosenAlgo = getString(R.string.dijkstra);
                }
                else if (position == 5)
                {
                    mPathFinderVisualizer = new AStar(maze, start, end);
                    chosenAlgo = getString(R.string.a_star);
                }
                binding.bottom.visualizeBtn.setText(String.format(
                        getString(R.string.visualize_btn_text) + " " + chosenAlgo));
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }
}
