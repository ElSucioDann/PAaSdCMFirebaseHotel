package com.example.paasdcmfirebasehotel.ui.editareliminarmishabitaciones;

import androidx.lifecycle.ViewModelProvider;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.paasdcmfirebasehotel.Objetos.Habitacion;
import com.example.paasdcmfirebasehotel.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class EditarEliminarMisHabitacionesFragment extends Fragment {

    private EditarEliminarMisHabitacionesViewModel mViewModel;


    private Button btnGuardar,btnBuscar,btnEliminar;
    private EditText etId;
    ArrayList<Habitacion> habitaciones = new ArrayList<Habitacion>();


    EditText etNombre,etHabitacion,etPrecio,etCapacidad,etTotalCamas,
            etTipoBanio,etCamas,etBalcon,etDescripcion;



    FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    DatabaseReference databaseReferenceHotel;
    StorageReference storageProfilePicsRef;

    //Strings
    String
    c_hab_aa_ud_habitacion,
    c_hab_ab_tipo_nombre,
    c_hab_ac_foto_habitacion,
    c_hab_ad_precio,
    c_hab_ae_capacidad,
    c_hab_af_piso,
    c_hab_ag_habitacion,
    c_hab_ah_total_camas,
    c_hab_ai_tipo_banio,
    c_hab_aj_tipo_camas,
    c_hab_ak_balcon,
    c_hab_al_descripcion_adicional,
    c_hab_am_disponibilidad,
    c_hab_an_ref_id_hotel,
    c_hab_ao_ref_razon_social,
    c_hab_ap_fecha_creacion,
    c_hab_aq_status;



    public static EditarEliminarMisHabitacionesFragment newInstance() {
        return new EditarEliminarMisHabitacionesFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root =  inflater.inflate(R.layout.fragment_editar_eliminar_mis_habitaciones, container, false);
        asignarComponentes(root);
        //referencias();
        //Toast.makeText(getContext(), "Referencia0"+referencias.get(0), Toast.LENGTH_SHORT).show();

        cargaHabitaciones1();

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(EditarEliminarMisHabitacionesViewModel.class);
        // TODO: Use the ViewModel
    }

    public void asignarComponentes(View root){
        btnGuardar = root.findViewById(R.id.EDELbtnGuardar);
        btnEliminar = root.findViewById(R.id.EDELbtnEliminar);
        btnBuscar = root.findViewById(R.id.EDELbtnBuscar);
        etId = root.findViewById(R.id.EDELetIdBus);

        etNombre = root.findViewById(R.id.EDELetnombre);
        etHabitacion = root.findViewById(R.id.EDELethabitacion);
        etPrecio = root.findViewById(R.id.EDELetprecio);
        etCapacidad = root.findViewById(R.id.EDELetcapacidad);
        etTotalCamas = root.findViewById(R.id.EDELettotalCamas);
        etTipoBanio  = root.findViewById(R.id.EDELetbanio);
        etCamas = root.findViewById(R.id.EDELettipoCamas);
        etBalcon = root.findViewById(R.id.EDELetbalcon);
        etDescripcion = root.findViewById(R.id.EDELetdescripcion);




        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("RTDB_Users");
        databaseReferenceHotel = FirebaseDatabase.getInstance().getReference().child("RTDB_Rooms");
        storageProfilePicsRef = FirebaseStorage.getInstance().getReference().child("STORAGE_Users_Profile_Pics");
    }




/*
    public void cargaHabitaciones(){
        databaseReferenceHotel.child( "Habitaciones1" ).child( mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Toast.makeText(getContext(), "existen resultadps 1", Toast.LENGTH_SHORT).show();
                    Toast.makeText(getContext(), "key"+snapshot.getKey(), Toast.LENGTH_SHORT).show();
//                    Toast.makeText(getContext(), snapshot.get, Toast.LENGTH_SHORT).show();

                    databaseReferenceHotel.child( "Habitaciones1" ).child( mAuth.getCurrentUser().getUid()).child(snapshot.getKey()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                                habitaciones.clear();
                                Toast.makeText(getContext(), "existen resultadps 2", Toast.LENGTH_SHORT).show();
                                Toast.makeText(getContext(), snapshot.getKey(), Toast.LENGTH_SHORT).show();
                                for (DataSnapshot objectSnapshot: snapshot.getChildren()) {
                                    Habitacion hab = new Habitacion();
                                    hab = objectSnapshot.getValue(Habitacion.class);
                                    Toast.makeText(getContext(), hab.getC_hab_ab_tipo_nombre(), Toast.LENGTH_SHORT).show();
                                    habitaciones.add(hab);
                                }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
*/
    public void cargaHabitaciones1(){
        ArrayList<String> referencias = new ArrayList<String>();
        databaseReferenceHotel.child( "Habitaciones1" ).child( mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener(){
            String ref;
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //referencias.clear();
                for(DataSnapshot ds : snapshot.getChildren()){
                    referencias.add(ds.getRef().getKey());
                    //ds.getValue();
                    //ds.getRef();
                    //Toast.makeText(getContext(),"Referencia-metodo"+ ds.getRef().getKey(), Toast.LENGTH_SHORT).show();


                    Log.d("ERROR", ds.getRef().getKey());


                }
                ref = referencias.get(0);


                databaseReferenceHotel.child( "Habitaciones1" ).child( mAuth.getCurrentUser().getUid()).child(ref).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {

                            for(DataSnapshot ds : snapshot.getChildren()){
                                referencias.add(ds.getRef().getKey());
                                //ds.getValue();
                                //ds.getRef();
                                Toast.makeText(getContext(),"Referencia-metodo"+ ds.getRef().getKey(), Toast.LENGTH_SHORT).show();

                                Log.d("ERROR", ds.getRef().getKey());


                            }
                        }
                        databaseReferenceHotel.child( "Habitaciones1" ).child( mAuth.getCurrentUser().getUid()).child(ref).child(referencias.get(0)).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                //Toast.makeText(getContext(), "existen resultadps 1", Toast.LENGTH_SHORT).show();
                                Toast.makeText(getContext(), snapshot.getKey(), Toast.LENGTH_SHORT).show();
                                //Toast.makeText(getContext(), snapshot.getChildren().toString(), Toast.LENGTH_SHORT).show();
                                for (DataSnapshot objectSnapshot: snapshot.getChildren()) {
                                    Toast.makeText(getContext(), "Codigo"+snapshot.child("c_hab_ab_tipo_nombre").getValue().toString(), Toast.LENGTH_SHORT).show();
                                    /*
                                    Habitacion hab = new Habitacion();

                                    hab.setC_hab_aa_ud_habitacion(snapshot.child("c_hab_aa_ud_habitacion").getValue().toString());
                                    hab.setC_hab_ab_tipo_nombre(snapshot.child("c_hab_ab_tipo_nombre").getValue().toString());
                                    hab.setC_hab_ac_foto_habitacion(snapshot.child("c_hab_ac_foto_habitacion").getValue().toString());
                                    hab.setC_hab_ad_precio(snapshot.child("c_hab_ad_precio").getValue().toString());
                                    hab.setC_hab_ae_capacidad(snapshot.child("c_hab_ae_capacidad").getValue().toString());
                                    hab.setC_hab_af_piso(snapshot.child("c_hab_af_peso").getValue().toString());
                                    hab.setC_hab_ag_habitacion(snapshot.child("c_hab_ag_habitacion").getValue().toString());
                                    hab.setC_hab_ah_total_camas(snapshot.child("c_hab_ah_total_camas").getValue().toString());
                                    hab.setC_hab_ai_tipo_banio(snapshot.child("c_hab_ai_tipo_banio").getValue().toString());
                                    hab.setC_hab_aj_tipo_camas(snapshot.child("c_hab_aj_tipo_camas").getValue().toString());
                                    hab.setC_hab_ak_balcon(snapshot.child("c_hab_ak_balcon").getValue().toString());
                                    hab.setC_hab_al_descripcion_adicional(snapshot.child("c_hab_al_al_descripcion_adicional").getValue().toString());
                                    hab.setC_hab_am_disponibilidad(snapshot.child("c_hab_am_disponibilidad").getValue().toString());
                                    hab.setC_hab_an_ref_id_hotel(snapshot.child("c_hab_an_ref_id_hotel").getValue().toString());
                                    hab.setC_hab_ao_ref_razon_social(snapshot.child("c_hab_ao_ref_razon_social").getValue().toString());
                                    hab.setC_hab_ap_fecha_creacion(snapshot.child("c_hab_ap_fecha_creacion").getValue().toString());
                                    hab.setC_hab_aq_status(snapshot.child("c_hab_aq_status").getValue().toString());


                                    Toast.makeText(getContext(), hab.getC_hab_ab_tipo_nombre(), Toast.LENGTH_SHORT).show();
                                    //habitaciones.add(hab);
                                    */

                                    /*
                                    c_hab_aa_ud_habitacion = snapshot.child("c_hab_aa_ud_habitacion").getValue().toString();
                                    c_hab_ab_tipo_nombre = snapshot.child("c_hab_ab_tipo_nombre").getValue().toString();
                                    c_hab_ac_foto_habitacion = snapshot.child("c_hab_ac_foto_habitacion").getValue().toString();
                                    c_hab_ad_precio = snapshot.child("c_hab_ad_precio").getValue().toString();
                                    c_hab_ae_capacidad = snapshot.child("c_hab_ae_capacidad").getValue().toString();
                                    c_hab_af_piso = snapshot.child("c_hab_af_piso").getValue().toString();
                                    c_hab_ag_habitacion = snapshot.child("c_hab_ag_habitacion").getValue().toString();
                                    c_hab_ah_total_camas = snapshot.child("c_hab_ah_total_camas").getValue().toString();
                                    c_hab_ai_tipo_banio = snapshot.child("c_hab_ai_tipo_banio").getValue().toString();
                                    c_hab_aj_tipo_camas = snapshot.child("c_hab_aj_tipo_camas").getValue().toString();
                                    c_hab_ak_balcon = snapshot.child("c_hab_ak_balcon").getValue().toString();
                                    c_hab_al_descripcion_adicional = snapshot.child("c_hab_al_descripcion_adicional").getValue().toString();
                                    c_hab_am_disponibilidad = snapshot.child("c_hab_am_disponibilidad").getValue().toString();
                                    c_hab_an_ref_id_hotel = snapshot.child("c_hab_an_ref_id_hotel").getValue().toString();
                                    c_hab_ao_ref_razon_social = snapshot.child("c_hab_ao_ref_razon_social").getValue().toString();
                                    c_hab_ap_fecha_creacion = snapshot.child("c_hab_ap_fecha_creacion").getValue().toString();
                                    c_hab_aq_status = snapshot.child("c_hab_aq_status").getValue().toString();

                                 */
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }



}


/*

                            c_hab_aa_ud_habitacion = snapshot.child("c_hab_aa_ud_habitacion").getValue().toString();
                            c_hab_ab_tipo_nombre = snapshot.child("c_hab_ab_tipo_nombre").getValue().toString();
                            c_hab_ac_foto_habitacion = snapshot.child("c_hab_ac_foto_habitacion").getValue().toString();
                            c_hab_ad_precio = snapshot.child("c_hab_ad_precio").getValue().toString();
                            c_hab_ae_capacidad = snapshot.child("c_hab_ae_capacidad").getValue().toString();
                            c_hab_af_piso = snapshot.child("c_hab_af_piso").getValue().toString();
                            c_hab_ag_habitacion = snapshot.child("c_hab_ag_habitacion").getValue().toString();
                            c_hab_ah_total_camas = snapshot.child("c_hab_ah_total_camas").getValue().toString();
                            c_hab_ai_tipo_banio = snapshot.child("c_hab_ai_tipo_banio").getValue().toString();
                            c_hab_aj_tipo_camas = snapshot.child("c_hab_aj_tipo_camas").getValue().toString();
                            c_hab_ak_balcon = snapshot.child("c_hab_ak_balcon").getValue().toString();
                            c_hab_al_descripcion_adicional = snapshot.child("c_hab_al_descripcion_adicional").getValue().toString();
                            c_hab_am_disponibilidad = snapshot.child("c_hab_am_disponibilidad").getValue().toString();
                            c_hab_an_ref_id_hotel = snapshot.child("c_hab_an_ref_id_hotel").getValue().toString();
                            c_hab_ao_ref_razon_social = snapshot.child("c_hab_ao_ref_razon_social").getValue().toString();
                            c_hab_ap_fecha_creacion = snapshot.child("c_hab_ap_fecha_creacion").getValue().toString();
                            c_hab_aq_status = snapshot.child("c_hab_aq_status").getValue().toString();
                            */





