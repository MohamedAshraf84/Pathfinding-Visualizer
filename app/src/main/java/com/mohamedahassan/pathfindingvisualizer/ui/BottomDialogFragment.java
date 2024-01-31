package com.mohamedahassan.pathfindingvisualizer.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.mohamedahassan.pathfindingvisualizer.R;
import com.mohamedahassan.pathfindingvisualizer.databinding.FragmentSettingsDialogBinding;

public class BottomDialogFragment extends BottomSheetDialogFragment {
    private FragmentSettingsDialogBinding binding;
    private SharedViewModel vm;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings_dialog, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //communicator = (PathfindingActivity) getActivity();

        vm = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        vm.getNodeSize().observe(this, nodeSize -> binding.gridResizer.setProgress(nodeSize));

        vm.getAnimationSpeed().observe(this, delay -> {
            binding.speedSeekbar.setProgress(delay);
            binding.textSpeed.setText(String.format(delay + " ms"));
        });

        binding.gridResizer.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Integer p = vm.getNodeSize().getValue();
                int prog = p != null ? p : 0;
                if (progress != prog)
                    vm.getNodeSize().setValue(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        binding.speedSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                //communicator.onAnimationSeekbarChanged(i);
                vm.getAnimationSpeed().setValue(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    /*public interface FragmentCommunicator {

        void onGridResizerChanged(int nodeSize);
        void onAnimationSeekbarChanged(int delay);
    }*/

}
