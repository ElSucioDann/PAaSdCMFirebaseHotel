package com.example.paasdcmfirebasehotel.ui.editareliminarmishabitaciones;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.paasdcmfirebasehotel.R;

public class EditarEliminarMisHabitacionesFragment extends Fragment {

    private EditarEliminarMisHabitacionesViewModel mViewModel;

    public static EditarEliminarMisHabitacionesFragment newInstance() {
        return new EditarEliminarMisHabitacionesFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_editar_eliminar_mis_habitaciones, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(EditarEliminarMisHabitacionesViewModel.class);
        // TODO: Use the ViewModel
    }

}