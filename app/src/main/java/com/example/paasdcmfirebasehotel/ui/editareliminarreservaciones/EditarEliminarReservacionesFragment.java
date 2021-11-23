package com.example.paasdcmfirebasehotel.ui.editareliminarreservaciones;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.paasdcmfirebasehotel.R;

public class EditarEliminarReservacionesFragment extends Fragment {

    private EditarEliminarReservacionesViewModel mViewModel;

    public static EditarEliminarReservacionesFragment newInstance() {
        return new EditarEliminarReservacionesFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_editar_eliminar_reservaciones, container, false);


        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(EditarEliminarReservacionesViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onStart() {
        super.onStart();

    }


}