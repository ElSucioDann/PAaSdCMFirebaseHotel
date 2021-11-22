package com.example.paasdcmfirebasehotel.ui.reservacion;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.paasdcmfirebasehotel.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ReservacionFragment extends Fragment {

    private ReservacionViewModel mViewModel;

    LinearLayout objLinearLayout;

    ScrollView objScrollViewPrincipal;

    RadioGroup objGrupoFiltrarHoteles;
    RadioButton objEstado;
    RadioButton objMejorCalificacion;

    ListView objListaHoteles;

    RadioGroup objGrupoFiltrarHabitaciones;
    RadioButton objPrecioAlto;
    RadioButton objPrecioBajo;
    RadioButton objCapacidad;

    ListView objListaHabitaciones;

    EditText objFechaInicio;
    EditText objFechaFinal;

    TextView objTotalDias;
    TextView objPrecioTotal;

    Button objReservar;

    //Firebase
    FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    DatabaseReference databaseReferenceHotel;
    DatabaseReference databaseReferenceHabitaciones;
    DatabaseReference databaseReferenceReservacion;
    StorageReference storageProfilePicsRef;
    StorageReference storageProfilePicsRefHotel;
    StorageReference storageProfilePicsRefHabitaciones;

    //Listas
    ArrayAdapter<String> adapterHotel;
    List<String> listaHotel1 = new ArrayList<>();
    List<String> listaHotel2 = new ArrayList<>();
    List<String> listaFotosHotel = new ArrayList<>();
    List<String> listaIDHotel = new ArrayList<>();

    ArrayAdapter<String> adapterHabitaciones;
    List<String> listaHabitaciones1 = new ArrayList<>();
    List<String> listaHabitaciones2 = new ArrayList<>();
    List<String> listaFotosHabitaciones = new ArrayList<>();
    List<String> listaIDHabitaciones = new ArrayList<>();

    //Cadenas Hotel
    String sHOT_ID=""
            , sHOT_RazonSocial=""
            , sHOT_FotoHotel=""
            , sHOT_Telefono=""
            , sHOT_Pais=""
            , sHOT_Estado=""
            , sHOT_Domicilio=""
            , sHOT_CodigoPostal=""
            , sHOT_CoordenadasGPS=""
            , sHOT_Calificacion=""
            , sHOT_DescripcionAdicional=""
            , sHOT_TotalHabitaciones=""
            , sHOT_FechaCreacion=""
            , sHOT_Status="";//14

    //Cadenas Habitacion
    String sHAB_ID_Habitacion=""
            , sHAB_TipoNombre=""
            , sHAB_FotoHabitacion=""
            , sHAB_Precio=""
            , sHAB_Capacidad=""
            , sHAB_Piso=""
            , sHAB_Habitacion = ""
            , sHAB_TotalCamas = ""
            , sHAB_TipoBanio = ""
            , sHAB_TipoCamas = ""
            , sHAB_Balcon = ""
            , sHAB_DescripcionAdicional = ""
            , sHAB_Disponibilidad = ""
            , sHAB_ID_Hotel = ""
            , sHAB_Razon_Social_Hotel = ""
            , sHAB_FechaCreacion = ""
            , sHAB_Status = "";//16

    //Auxiliares
    int aux_clic=0;
    String aux_USU_ID="";
    List<String> aux_lista_USU_IDs = new ArrayList<>();
    String aux_filtrado_hoteles="c_hot_af_estado";
    String aux_filtrado_habitaciones="c_hab_ad_precio";
    int aux_cargadosHab=0;
    String aux_USU_ID_Filtrar="";
    String aux_HOT_ID_Filtrar="";
    List<String> aux_lista_alreves = new ArrayList<>();
    List<String> aux_lista_alreves2 = new ArrayList<>();
    List<String> aux_lista_fotos_alreves = new ArrayList<>();
    List<String> aux_lista_ids_alreves = new ArrayList<>();
    int aux_press_alto_bajo=1;
    List<String> aux_lista_HabitacionesHoteles = new ArrayList<>();
    CountDownTimer mCountDownTimer;

    String HOT_opcion1_USU="";
    String HOT_opcion1="";
    String HAB_opcion2="";

    List<String> aux_lista_precios_habitaciones = new ArrayList<>();
    List<String> aux_lista_precios_habitaciones_alreves = new ArrayList<>();
    String HAB_Precio="";
    String precioTotal="";
    String totalDias="";

    View root1= null;

    //Fecha DatePickerDialog
    DatePickerDialog objPicker;


    public static ReservacionFragment newInstance() {
        return new ReservacionFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        root1 = inflater.inflate(R.layout.fragment_reservacion, container, false);

        View root;

        root = root1;

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("RTDB_Users");
        databaseReferenceHotel = FirebaseDatabase.getInstance().getReference().child("RTDB_Hotels");
        databaseReferenceHabitaciones = FirebaseDatabase.getInstance().getReference().child("RTDB_Rooms");
        databaseReferenceReservacion = FirebaseDatabase.getInstance().getReference().child("RTDB_Reservation");
        storageProfilePicsRef = FirebaseStorage.getInstance().getReference().child("STORAGE_Users_Profile_Pics");
        storageProfilePicsRefHotel = FirebaseStorage.getInstance().getReference().child("STORAGE_Hotels_Profile_Pics");
        storageProfilePicsRefHabitaciones = FirebaseStorage.getInstance().getReference().child("STORAGE_Rooms_Profile_Pics");

        asignar(root);
        Log.d("--------ASIGNAR", "ASIGNAR" );

        fechaInicio();
        fechaFinal();

        //obtenerIDsHabitacionesHoteles();

        listarHoteles();
        Log.d("--------LISTAR HOTELES", "LISTAR HOTELES" );


        filtradoHoteles();
        Log.d("--------BOTONES FILTRAR", "BOTONES FILTRAR HOTEL" );

        filtradoHabitaciones();
        Log.d("--------BOTONES FILTRAR", "BOTONES FILTRAR HABITACIONES" );

        reservar();


        root1=null;

        return root;
    }

    private void reservar(){

        objReservar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String FI = objFechaInicio.getText().toString();
                String FF = objFechaFinal.getText().toString();
                String TD = objTotalDias.getText().toString();
                String PT = objPrecioTotal.getText().toString();

                int dateDifference = (int) getDateDiff(new SimpleDateFormat("dd/MM/yyyy"), FI, FF);
                System.out.println("dateDifference: " + dateDifference);

                if ( !(aux_USU_ID_Filtrar.equals("")) && !(HOT_opcion1.equals("")) && !(HAB_opcion2.equals("")) && dateDifference >= 0 && !(FI.equals("")) && !(FF.equals("")) && !(HAB_Precio.equals("")) && !(precioTotal.equals("") ) ){

                    String aux_ID_Reservacion = UUID.randomUUID().toString();

                    Toast.makeText(getContext(),
                                    "ID RES: " + aux_ID_Reservacion
                                            + "\nID USU: " + aux_USU_ID_Filtrar
                                            + "\nID HOT USU: " + HOT_opcion1
                                            + "\nID HAB: " + HAB_opcion2
                                            + "\nID ACT USU: " + mAuth.getCurrentUser().getUid()
                                            + "\nFI: " + FI
                                            + "\nFF: " + FF
                                            + "\nTD: " + TD
                                            + "\nPT: " + PT
                                            + "\nFC: " + String.valueOf(getFechaMilisegundos() * -1)
                                            + "\nS: " + 1
                                            + "\n"
                            , Toast.LENGTH_SHORT).show();

                    HashMap<String, Object> userMapReservacion = new HashMap<>();

                    userMapReservacion.put("c_res_aa_id_reservacion", aux_ID_Reservacion);

                    userMapReservacion.put("c_res_ab_ref_id_usuario_hotel", aux_USU_ID_Filtrar );
                    userMapReservacion.put("c_res_ac_ref_id_hotel", HOT_opcion1 );
                    userMapReservacion.put("c_res_ad_ref_id_habitacion", HAB_opcion2 );
                    userMapReservacion.put("c_res_ae_ref_id_usuario_actual", mAuth.getCurrentUser().getUid() );

                    userMapReservacion.put("c_res_af_fecha_inicio", FI);
                    userMapReservacion.put("c_res_ag_fecha_final", FF);

                    userMapReservacion.put("c_res_ah_total_dias", TD);
                    userMapReservacion.put("c_res_ai_precio_total", PT);

                    userMapReservacion.put("c_res_aj_fecha_creacion", String.valueOf(getFechaMilisegundos() * -1) );
                    userMapReservacion.put("c_res_ak_status", "1");//11

                    databaseReferenceReservacion.child( "Reservaciones1" ).child( aux_ID_Reservacion ).setValue( userMapReservacion ).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){

                                Toast.makeText(getContext(), "Reservación Registrada", Toast.LENGTH_SHORT).show();

                                HashMap<String, Object> userMapHabitacion = new HashMap<>();
                                userMapHabitacion.put("c_hab_am_disponibilidad", "0");

                                databaseReferenceHabitaciones.child( "Habitaciones1" ).child( aux_USU_ID_Filtrar ).child( HOT_opcion1 ).child( HAB_opcion2 ).updateChildren( userMapHabitacion ).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {

                                    }
                                }).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()){
                                            Toast.makeText(getContext(), "Reservacion Registrada y Disponiblidad Actualizada", Toast.LENGTH_SHORT).show();
                                            //listarHoteles();
                                            vaciarListaHotel();
                                            vaciarListaHabitaciones();
                                            listarHoteles();
                                        }

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getContext(), "Falló al Actualizar Disponibilidad de Habitación", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), "Falló al Registrar Reservacion", Toast.LENGTH_SHORT).show();
                        }
                    });


                } else {
                    Toast.makeText(getContext(), "Debe llenar Campos", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void filtradoHabitaciones(){

        objPrecioAlto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                objPrecioTotal.setText("");
                objTotalDias.setText("");

                objFechaInicio.setText("");
                objFechaFinal.setText("");

                HAB_opcion2 = "";
                HAB_Precio = "";

                listaHabitaciones1.clear();
                listaHabitaciones2.clear();
                listaFotosHabitaciones.clear();
                listaIDHabitaciones.clear();

                aux_lista_precios_habitaciones.clear();

                aux_filtrado_habitaciones="c_hab_ad_precio";

                if ( aux_cargadosHab==1 ){

                    aux_press_alto_bajo=1;

                    cargarHabitaciones( aux_USU_ID_Filtrar, aux_HOT_ID_Filtrar );
                } else {
                    Toast.makeText(getContext(), "Debe Seleccionar un Hotel", Toast.LENGTH_SHORT).show();
                }

            }
        });

        objPrecioBajo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                objPrecioTotal.setText("");
                objTotalDias.setText("");

                objFechaInicio.setText("");
                objFechaFinal.setText("");

                HAB_opcion2 = "";
                HAB_Precio = "";

                listaHabitaciones1.clear();
                listaHabitaciones2.clear();
                listaFotosHabitaciones.clear();
                listaIDHabitaciones.clear();

                aux_lista_precios_habitaciones.clear();

                aux_filtrado_habitaciones="c_hab_ad_precio";

                if (aux_cargadosHab==1){

                    aux_press_alto_bajo=0;

                    cargarHabitaciones( aux_USU_ID_Filtrar, aux_HOT_ID_Filtrar );
                } else {
                    Toast.makeText(getContext(), "Debe Seleccionar un Hotel", Toast.LENGTH_SHORT).show();
                }

            }
        });

        objCapacidad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                objPrecioTotal.setText("");
                objTotalDias.setText("");

                objFechaInicio.setText("");
                objFechaFinal.setText("");

                HAB_opcion2 = "";
                HAB_Precio = "";

                listaHabitaciones1.clear();
                listaHabitaciones2.clear();
                listaFotosHabitaciones.clear();
                listaIDHabitaciones.clear();

                aux_lista_precios_habitaciones.clear();

                aux_filtrado_habitaciones="c_hab_ae_capacidad";

                if (aux_cargadosHab==1){

                    aux_press_alto_bajo=0;

                    cargarHabitaciones( aux_USU_ID_Filtrar, aux_HOT_ID_Filtrar );
                } else {
                    Toast.makeText(getContext(), "Debe Seleccionar un Hotel", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void filtradoHoteles(){

        objEstado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                objPrecioTotal.setText("");
                objTotalDias.setText("");

                objFechaInicio.setText("");
                objFechaFinal.setText("");

                HOT_opcion1 = "";
                HAB_opcion2 = "";
                HAB_Precio = "";

                aux_cargadosHab=0;
                aux_filtrado_hoteles = "c_hot_af_estado";
                listarHoteles();
                vaciarListaHabitaciones();

                objPrecioAlto.setSelected(false);
                objPrecioAlto.setChecked(false);

                objPrecioBajo.setSelected(false);
                objPrecioBajo.setChecked(false);

                objCapacidad.setSelected(false);
                objCapacidad.setChecked(false);

            }
        });

        objMejorCalificacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                objPrecioTotal.setText("");
                objTotalDias.setText("");

                objFechaInicio.setText("");
                objFechaFinal.setText("");

                HOT_opcion1 = "";
                HAB_opcion2 = "";
                HAB_Precio = "";

                aux_cargadosHab=0;
                aux_filtrado_hoteles = "c_hot_aj_calificacion";
                listarHoteles();
                vaciarListaHabitaciones();

                objPrecioAlto.setSelected(false);
                objPrecioAlto.setChecked(false);

                objPrecioBajo.setSelected(false);
                objPrecioBajo.setChecked(false);

                objCapacidad.setSelected(false);
                objCapacidad.setChecked(false);


            }
        });

    }

    private void vaciarListaHotel(){

        ArrayAdapter<String> adapterVaciarHotel;
        List<String> listaVaciar = new ArrayList<>();
        adapterVaciarHotel = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, listaVaciar);
        adapterVaciarHotel.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        objListaHoteles.setAdapter(adapterVaciarHotel);

    }

    private void vaciarListaHabitaciones(){

        ArrayAdapter<String> adapterVaciarHabitaciones;
        List<String> listaVaciar = new ArrayList<>();
        adapterVaciarHabitaciones = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, listaVaciar);
        adapterVaciarHabitaciones.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        objListaHabitaciones.setAdapter(adapterVaciarHabitaciones);

    }

    private void listarHoteles() {

        if (getActivity()==null){
            return ;
        }

        if (getContext()==null){
            return ;
        }

        objFechaInicio.setText("");
        objFechaFinal.setText("");
        objTotalDias.setText("");
        objPrecioTotal.setText("");

        vaciarListaHotel();

        Toast.makeText(getContext(), "HOTELES", Toast.LENGTH_SHORT).show();

        listaHotel1.clear();
        listaHotel2.clear();
        listaFotosHotel.clear();
        listaIDHotel.clear();

        listaHabitaciones1.clear();
        listaHabitaciones2.clear();
        listaFotosHabitaciones.clear();
        listaIDHabitaciones.clear();

        aux_lista_USU_IDs.clear();

        ProgressDialog TempDialog;

        int i=0;

        TempDialog = new ProgressDialog(getContext());
        TempDialog.setMessage("Please wait...");
        TempDialog.setCancelable(false);
        TempDialog.setProgress(i);
        TempDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        TempDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.GRAY));

        TempDialog.show();

        databaseReferenceHotel.child( "Hoteles1" ).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                ArrayList<String> referencias = new ArrayList<>();
                referencias.clear();
                for(DataSnapshot ds : snapshot.getChildren()){
                    referencias.add( ds.getRef().getKey() );
                }

                if (getActivity() == null){
                    return;
                }

                Toast.makeText(getContext(), String.valueOf(referencias.size() ) , Toast.LENGTH_SHORT).show();

                Log.d("--------VALOR", String.valueOf(referencias.size()) );


                for (int i=0; i < referencias.size(); i++){

                    int aux_i = i;

                    Log.d("--------VALOR", referencias.get(i) );

                    databaseReferenceHotel.child( "Hoteles1" ).child( referencias.get( i ).toString() ).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            ArrayList<String> referenciasIDHoteles = new ArrayList<>();
                            referenciasIDHoteles.clear();
                            for(DataSnapshot ds : snapshot.getChildren()){
                                referenciasIDHoteles.add( ds.getRef().getKey() );
                            }
                            Toast.makeText(getContext(), String.valueOf(referenciasIDHoteles.size()) , Toast.LENGTH_SHORT).show();

                            Log.d("--------VALOR_2", String.valueOf( referenciasIDHoteles.size()) );

                            for (int j=0; j < referenciasIDHoteles.size(); j++){

                                Log.d("--------VALOR_2", referenciasIDHoteles.get(j) );

                                databaseReferenceHotel.child( "Hoteles1" ).child( referencias.get( aux_i ).toString() ).child( referenciasIDHoteles.get(j) ).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        databaseReferenceHotel.child( "Hoteles1" ).child( referencias.get( aux_i ).toString() ).orderByChild( aux_filtrado_hoteles ).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                aux_USU_ID = referencias.get( aux_i ).toString();

                                                DataSnapshot objSnapshot2;
                                                objSnapshot2 = dataSnapshot;

                                                for (DataSnapshot objSnapshot : dataSnapshot.getChildren()){

                                                    for (int p=0; p<aux_lista_HabitacionesHoteles.size(); p++){

                                                        if ( aux_lista_HabitacionesHoteles.get(p).equals( objSnapshot.child( "c_hot_aa_id_hotel" ).getValue().toString() ) ){

                                                            sHOT_ID= objSnapshot.child( "c_hot_aa_id_hotel" ).getValue().toString();
                                                            sHOT_RazonSocial= objSnapshot.child( "c_hot_ab_razon_social" ).getValue().toString();
                                                            sHOT_FotoHotel= objSnapshot.child( "c_hot_ac_foto_hotel" ).getValue().toString();
                                                            sHOT_Telefono= objSnapshot.child( "c_hot_ad_telefono" ).getValue().toString();
                                                            sHOT_Pais= objSnapshot.child( "c_hot_ae_pais" ).getValue().toString();
                                                            sHOT_Estado= objSnapshot.child( "c_hot_af_estado" ).getValue().toString();
                                                            sHOT_Domicilio= objSnapshot.child( "c_hot_ag_domicilio" ).getValue().toString();
                                                            sHOT_CodigoPostal= objSnapshot.child( "c_hot_ah_codigo_postal" ).getValue().toString();
                                                            sHOT_CoordenadasGPS= objSnapshot.child( "c_hot_ai_coordenadas" ).getValue().toString();
                                                            sHOT_Calificacion= objSnapshot.child( "c_hot_aj_calificacion" ).getValue().toString();
                                                            sHOT_DescripcionAdicional= objSnapshot.child( "c_hot_ak_descripcion" ).getValue().toString();
                                                            sHOT_TotalHabitaciones= objSnapshot.child( "c_hot_al_total_habitaciones" ).getValue().toString();
                                                            sHOT_FechaCreacion= objSnapshot.child( "c_hot_am_fecha_creacion" ).getValue().toString();
                                                            sHOT_Status= objSnapshot.child( "c_hot_an_status" ).getValue().toString();

                                                            Log.d("--------VALOR_3_ID", sHOT_ID );
                                                            Log.d("--------VALOR_3_Nombre", sHOT_RazonSocial );

                                                            if ( estaVacio() ){

                                                                if ( sHOT_Status.equals("1") ){

                                                                    listaHotel1.add(
                                                                            //"\nID: " + sHOT_ID
                                                                            "\nRazón Social: " + sHOT_RazonSocial
                                                                                    //+"\nFoto Hotel Ruta: " + sHOT_FotoHotel
                                                                                    +"\nTeléfono: " + sHOT_Telefono
                                                                                    +"\nPaís: " + sHOT_Pais
                                                                                    +"\nEstado: " + sHOT_Estado
                                                                                    +"\nDomicilio: " + sHOT_Domicilio
                                                                                    +"\nCódigo Postal: " + sHOT_CodigoPostal
                                                                                    //+"\nCoordenadas GPS: " + sHOT_Telefono
                                                                                    +"\nCalificación: " + sHOT_Calificacion
                                                                                    +"\nDescripción Adicional: " + sHOT_DescripcionAdicional
                                                                            //+"\nTotal de Habitaciones: " + sHOT_Telefono
                                                                            //+"\nFecha de Creación: " + sHOT_Telefono
                                                                            //+"\nStatus: " + sHOT_Telefono
                                                                    );

                                                                    listaHotel2.add(
                                                                            "\nID: " + sHOT_ID
                                                                                    +"\nRazón Social: " + sHOT_RazonSocial
                                                                                    +"\nFoto Hotel Ruta: " + sHOT_FotoHotel
                                                                                    +"\nTeléfono: " + sHOT_Telefono
                                                                                    +"\nPaís: " + sHOT_Pais
                                                                                    +"\nEstado: " + sHOT_Estado
                                                                                    +"\nDomicilio: " + sHOT_Domicilio
                                                                                    +"\nCódigo Postal: " + sHOT_CodigoPostal
                                                                                    +"\nCoordenadas GPS: " + sHOT_CoordenadasGPS
                                                                                    +"\nCalificación: " + sHOT_Calificacion
                                                                                    +"\nDescripción Adicional: " + sHOT_DescripcionAdicional
                                                                                    +"\nTotal de Habitaciones: " + sHOT_TotalHabitaciones
                                                                                    +"\nFecha de Creación: " + sHOT_FechaCreacion
                                                                                    +"\nStatus: " + sHOT_Status
                                                                    );

                                                                    listaFotosHotel.add( sHOT_FotoHotel );
                                                                    listaIDHotel.add( sHOT_ID );

                                                                    aux_lista_USU_IDs.add( aux_USU_ID );

                                                                }
                                                            }
                                                            break;


                                                        }

                                                    }


                                                }

                                                if (getActivity()==null){
                                                    return ;
                                                }

                                                adapterHotel = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, listaHotel1);
                                                adapterHotel.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                                                objListaHoteles.setAdapter(adapterHotel);

                                                objListaHoteles.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_expandable_list_item_2){
                                                    @Override
                                                    public int getCount() {
                                                        return (int)listaHotel1.size();
                                                    }

                                                    @NonNull
                                                    @Override
                                                    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {


                                                        if(convertView == null) {
                                                            convertView = ((LayoutInflater)getActivity().getSystemService(LAYOUT_INFLATER_SERVICE)).inflate(R.layout.auxiliar_lista, parent, false);

                                                            convertView.setOnTouchListener(new View.OnTouchListener() {
                                                                @Override
                                                                public boolean onTouch(View view, MotionEvent motionEvent) {

                                                                    Toast.makeText(getContext(), "Tocando View", Toast.LENGTH_SHORT).show();
                                                                    return true;
                                                                }
                                                            });

                                                        }

                                                        ((TextView)convertView.findViewById(R.id.idAuxiliarListaTextViewInformacion)).setText( listaHotel1.get(position) );

                                                        ImageView ivImageView;
                                                        ivImageView = new ImageView(getContext());
                                                        ivImageView = convertView.findViewById(R.id.idAuxiliarListaImageViewImagen);

                                                        if (!listaFotosHotel.get(position).isEmpty()){
                                                            Picasso.get().load(listaFotosHotel.get(position)).into(ivImageView);
                                                        } else {
                                                            ivImageView.setImageResource(R.drawable.ic_menu_camera);
                                                        }

                                                        Button objInformacion = convertView.findViewById(R.id.idAuxiliarListaButtonInformacion);
                                                        objInformacion.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View view) {

                                                                objPrecioTotal.setText("");
                                                                objTotalDias.setText("");

                                                                objFechaInicio.setText("");
                                                                objFechaFinal.setText("");

                                                                HOT_opcion1 = listaIDHotel.get(position);
                                                                HAB_opcion2 = "";
                                                                HAB_Precio = "";

                                                                //aux_filtrado_hoteles="c_hot_af_estado";
                                                                aux_filtrado_habitaciones="c_hab_ad_precio";

                                                                listaHabitaciones1.clear();
                                                                listaHabitaciones2.clear();
                                                                listaFotosHabitaciones.clear();
                                                                listaIDHabitaciones.clear();

                                                                aux_lista_precios_habitaciones.clear();

                                                                //aux_lista_USU_IDs.clear();

                                                                vaciarListaHabitaciones();

                                                                aux_USU_ID_Filtrar = aux_lista_USU_IDs.get(position);

                                                                aux_HOT_ID_Filtrar = listaIDHotel.get(position);

                                                                cargarHabitaciones( aux_USU_ID_Filtrar, aux_HOT_ID_Filtrar );

                                                                objPrecioAlto.setSelected(true);
                                                                objPrecioAlto.setChecked(true);

                                                                //aux_cargadosHab=1;

                                                                Toast.makeText(getContext(), "USU: " + aux_lista_USU_IDs.get(position) + "\nHOT" + listaIDHotel.get(position), Toast.LENGTH_SHORT).show();

                                                                View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.auxiliar_lista_informacion, null);
                                                                ((TextView)dialogView.findViewById(R.id.idAuxiliarListaInformacionTextViewInformacion)).setText(listaHotel2.get(position));

                                                                ImageView ivImageView = dialogView.findViewById(R.id.idAuxiliarListaInformacionImageViewImagen);
                                                                if (!listaFotosHotel.get(position).isEmpty()){
                                                                    Picasso.get().load(listaFotosHotel.get(position)).into(ivImageView);
                                                                }

                                                                AlertDialog.Builder dialogo = new AlertDialog.Builder(getContext());
                                                                dialogo.setTitle("Información");
                                                                dialogo.setView(dialogView);
                                                                dialogo.setPositiveButton("Aceptar", null);
                                                                dialogo.show();

                                                            }
                                                        });

                                                        return convertView;
                                                    }
                                                });
                                                mCountDownTimer = new CountDownTimer(2000, 1000) {
                                                    public void onTick(long millisUntilFinished) {
                                                        TempDialog.setMessage("Please wait..");
                                                    }

                                                    public void onFinish() {
                                                        TempDialog.dismiss();//Your action like intents are placed here

                                                    }
                                                }.start();
                                            }
                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                mCountDownTimer = new CountDownTimer(2000, 1000) {
                                                    public void onTick(long millisUntilFinished) {
                                                        TempDialog.setMessage("Please wait..");
                                                    }

                                                    public void onFinish() {
                                                        TempDialog.dismiss();//Your action like intents are placed here

                                                    }
                                                }.start();
                                            }
                                        });
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
//                        aux_referencia = referencias.get(0);
//
//                        databaseReferenceHotel.child( "Hoteles1" ).child( mAuth.getCurrentUser().getUid() ).child( referencias.get(0) ).addListenerForSingleValueEvent(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                Toast.makeText(getContext(), "Aqui", Toast.LENGTH_SHORT).show();
//
//                                if (snapshot.exists()) {
//                                    //String sNombre = snapshot.child("c_usu_ad_nombre").getValue().toString();
//
//                                    sIDHotel= snapshot.child( "c_hot_aa_id_hotel" ).getValue().toString();
//                                    sRazonSocial= snapshot.child( "c_hot_ab_razon_social" ).getValue().toString();
//                                    sFotoHotel= snapshot.child( "c_hot_ac_foto_hotel" ).getValue().toString();
//                                    ruta = sFotoHotel;
//                                    sTelefono= snapshot.child( "c_hot_ad_telefono" ).getValue().toString();
//                                    sPais= snapshot.child( "c_hot_ae_pais" ).getValue().toString();
//                                    sEstado= snapshot.child( "c_hot_af_estado" ).getValue().toString();
//                                    sDomicilio= snapshot.child( "c_hot_ag_domicilio" ).getValue().toString();
//                                    sCodigoPostal= snapshot.child( "c_hot_ah_codigo_postal" ).getValue().toString();
//                                    sCoordenadasGPS= snapshot.child( "c_hot_ai_coordenadas" ).getValue().toString();
//                                    sCalificacion= snapshot.child( "c_hot_aj_calificacion" ).getValue().toString();
//                                    sDescripcionAdicional= snapshot.child( "c_hot_ak_descripcion" ).getValue().toString();
//                                    sTotalHabitaciones= snapshot.child( "c_hot_al_total_habitaciones" ).getValue().toString();
//                                    sFechaCreacion= snapshot.child( "c_hot_am_fecha_creacion" ).getValue().toString();
//                                    sStatus= snapshot.child( "c_hot_an_status" ).getValue().toString();
//
//                                    //Llenar EditTexts
//                                    objID.setText(sIDHotel);
//                                    objID.setClickable(false);
//
//                                    objRazonSocial.setText(sRazonSocial);
//
//                                    if ( !(sFotoHotel.equals("") || sFotoHotel.isEmpty()) ){
//                                        Picasso.get().load( sFotoHotel ).into( objFotoHotel );
//                                    }
//
//                                    objTelefono.setText(sTelefono);
//                                    objPais.setText(sPais);
//
//                                    objEstado.setSelection( obtenerPosition(objEstado, sEstado) );
//
//                                    objDomicilio.setText(sDomicilio);
//                                    objCodigoPostal.setText(sCodigoPostal);
//                                    objCoordenadasGPS.setText(sCoordenadasGPS);
//                                    objCalificacion.setText(sCalificacion);
//                                    objDescripcionAdicional.setText(sDescripcionAdicional);
//                                    objTotalHabitaciones.setText(sTotalHabitaciones);
//
//                                    if (sStatus.equals("1")){
//                                        objActivo.setChecked(true);
//                                        objInactivo.setChecked(false);
//                                    } else if ( sStatus.equals("0") ){
//                                        objInactivo.setChecked(true);
//                                        objActivo.setChecked(false);
//                                    }
//
//
//
//                                }
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError error) {
//
//                            }
//                        });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void asignar(View root) {

        objScrollViewPrincipal = root.findViewById(R.id.idReservacionScrollViewPrincipal);

         objGrupoFiltrarHoteles = root.findViewById(R.id.idReservacionRadioGroupFiltrarHoteles);
         objEstado = root.findViewById(R.id.idReservacionRadioButtonEstado);
         objEstado.setSelected(true);
         objEstado.setChecked(true);
         objMejorCalificacion = root.findViewById(R.id.idReservacionRadioButtonMejorCalificados);

         objListaHoteles = root.findViewById(R.id.idReservacionListViewHoteles);

         objGrupoFiltrarHabitaciones = root.findViewById(R.id.idReservacionRadioGroupFiltrarHabitaciones);
         objPrecioAlto = root.findViewById(R.id.idReservacionRadioButtonPrecioAltoBajo);
//         objPrecioAlto.setSelected(true);
//         objPrecioAlto.setChecked(true);
         objPrecioBajo = root.findViewById(R.id.idReservacionRadioButtonPrecioBajoAlto);
         objCapacidad = root.findViewById(R.id.idReservacionRadioButtonCapacidadAltoBajo);

         objListaHabitaciones = root.findViewById(R.id.idReservacionListViewHabitaciones);

         objFechaInicio = root.findViewById(R.id.idReservacionEditTextFechaInicio);
         objFechaFinal = root.findViewById(R.id.idReservacionEditTextFechaFinal);

         objTotalDias = root.findViewById(R.id.idReservacionTextViewTotalDias);
         objPrecioTotal = root.findViewById(R.id.idReservacionTextViewPrecioTotal);

         objReservar = root.findViewById(R.id.idReservacionButtonReservar);

         objLinearLayout = root.findViewById(R.id.idReservacionLinearLayoutPrincipal);

        objScrollViewPrincipal.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
//                objListaHoteles.getParent().requestDisallowInterceptTouchEvent(false);

//                View vS = v;

                //Toast.makeText(getContext(), "Tocando SCROLL", Toast.LENGTH_SHORT).show();

//                if ( aux_clic==1){
//                    return true;
//                } else {
//                    return false;
//                }

                //true para
                //false avanza

                return false;
            }
        });

        objLinearLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                view.getParent().requestDisallowInterceptTouchEvent(true);

                objScrollViewPrincipal.setOnTouchListener(new View.OnTouchListener() {
                    public boolean onTouch(View v, MotionEvent event) {
                        //true para
                        //false avanza
                        return false;
                    }
                });

                objListaHoteles.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        view.getParent().requestDisallowInterceptTouchEvent(true);

                        Toast.makeText(getContext(), "Tocando Hotel", Toast.LENGTH_SHORT).show();

                        return false;
                    }
                });


                Toast.makeText(getContext(), "Tocando LINAER", Toast.LENGTH_SHORT).show();

//                if (motionEvent.getAction()== MotionEvent.ACTION_BUTTON_PRESS){
//                    aux_clic=1;
//                } else {
//                    aux_clic=0;
//                }



                return false;
            }
        });


//        objListaHoteles.setOnScrollListener(new AbsListView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(AbsListView absListView, int i) {
//                Toast.makeText(getContext(), "Tocando LISTVIEW 1", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
//
//                Toast.makeText(getContext(), "Tocando LISTVIEW 2", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        objListaHoteles.setOnTouchListener(new View.OnTouchListener() {
//
//            public boolean onTouch(View v, MotionEvent event) {
////                v.getParent().requestDisallowInterceptTouchEvent(true);
//
//                Toast.makeText(getContext(), "Tocando LISTVIEW", Toast.LENGTH_SHORT).show();
//
//                return false;
//            }
//        });

//        objLinearLayout.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//
//                Toast.makeText(getContext(), "Tocando Linear", Toast.LENGTH_SHORT).show();
//
//                root.getParent().requestDisallowInterceptTouchEvent(true);
//
//
//                return true;
//            }
//        });

//        objScrollViewPrincipal.setOnTouchListener(new ListView.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                int action = event.getAction();
//                switch (action) {
//                    case MotionEvent.ACTION_DOWN:
//                        // Disallow ScrollView to intercept touch events.
//
//                        Toast.makeText(getContext(), "Tocando", Toast.LENGTH_SHORT).show();
//
//                        v.getParent().requestDisallowInterceptTouchEvent(true);
//                        break;
//
//                    case MotionEvent.ACTION_UP:
//                        // Allow ScrollView to intercept touch events.
//
//                        Toast.makeText(getContext(), "Tocando", Toast.LENGTH_SHORT).show();
//
//                        v.getParent().requestDisallowInterceptTouchEvent(false);
//                        break;
//                }
//
//                // Handle ListView touch events.
//                v.onTouchEvent(event);
//                return true;
//            }
//        });
//
//        objListaHoteles.setOnTouchListener(new ListView.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                int action = event.getAction();
//                switch (action) {
//                    case MotionEvent.ACTION_DOWN:
//                        // Disallow ScrollView to intercept touch events.
//
//                        Toast.makeText(getContext(), "Tocando", Toast.LENGTH_SHORT).show();
//
//                        v.getParent().requestDisallowInterceptTouchEvent(true);
//                        break;
//
//                    case MotionEvent.ACTION_UP:
//                        // Allow ScrollView to intercept touch events.
//
//                        Toast.makeText(getContext(), "Tocando", Toast.LENGTH_SHORT).show();
//
//                        v.getParent().requestDisallowInterceptTouchEvent(false);
//                        break;
//                }
//
//                // Handle ListView touch events.
//                v.onTouchEvent(event);
//                return true;
//            }
//        });

//        LinearLayoutManager layoutManager= new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL, false);
//        RecyclerView mRecyclerView;
//        mRecyclerView = (RecyclerView) root.findViewById(R.id.idRecicler);
//        mRecyclerView.setLayoutManager(layoutManager);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ReservacionViewModel.class);

        root1=null;

        //listarHoteles();

        // TODO: Use the ViewModel
    }

    private boolean estaVacio(){

        if (
        sHOT_ID.equals("")
        || sHOT_RazonSocial.equals("")
        || sHOT_FotoHotel.equals("")
        || sHOT_Telefono.equals("")
        || sHOT_Pais.equals("")
        || sHOT_Estado.equals("")
        || sHOT_Domicilio.equals("")
        || sHOT_CodigoPostal.equals("")
        || sHOT_CoordenadasGPS.equals("")
        || sHOT_Calificacion.equals("")
        || sHOT_DescripcionAdicional.equals("")
        || sHOT_TotalHabitaciones.equals("")
        || sHOT_FechaCreacion.equals("")
        || sHOT_Status.equals("")
        ){
            return false;
        } else {
            return true;
        }


    }

    private void cargarHabitaciones( String usu, String hot ){

        ProgressDialog TempDialog;

        int i=0;

        TempDialog = new ProgressDialog(getContext());
        TempDialog.setMessage("Please wait...");
        TempDialog.setCancelable(false);
        TempDialog.setProgress(i);
        TempDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        TempDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.GRAY));

        TempDialog.show();

        databaseReferenceHabitaciones.child( "Habitaciones1" ).child( usu ).child( hot ).orderByChild( aux_filtrado_habitaciones ).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if ( dataSnapshot.exists() ){

                    for (DataSnapshot objSnapshot : dataSnapshot.getChildren()) {

                        String aux_HAB_disponibilidad="";
                        String aux_HAB_status="";

                        aux_HAB_disponibilidad = objSnapshot.child("c_hab_am_disponibilidad").getValue().toString();
                        aux_HAB_status = objSnapshot.child("c_hab_aq_status").getValue().toString();

                        if ( aux_HAB_disponibilidad.equals("1") && aux_HAB_status.equals("1") ) {

                            sHAB_ID_Habitacion = objSnapshot.child("c_hab_aa_id_habitacion").getValue().toString();
                            sHAB_TipoNombre = objSnapshot.child("c_hab_ab_tipo_nombre").getValue().toString();
                            sHAB_FotoHabitacion = objSnapshot.child("c_hab_ac_foto_habitacion").getValue().toString();
                            sHAB_Precio = objSnapshot.child("c_hab_ad_precio").getValue().toString();
                            sHAB_Capacidad = objSnapshot.child("c_hab_ae_capacidad").getValue().toString();
                            sHAB_Piso = objSnapshot.child("c_hab_af_piso").getValue().toString();
                            sHAB_Habitacion = objSnapshot.child("c_hab_ag_habitacion").getValue().toString();
                            sHAB_TotalCamas = objSnapshot.child("c_hab_ah_total_camas").getValue().toString();
                            sHAB_TipoBanio = objSnapshot.child("c_hab_ai_tipo_banio").getValue().toString();
                            sHAB_TipoCamas = objSnapshot.child("c_hab_aj_tipo_camas").getValue().toString();
                            sHAB_Balcon = objSnapshot.child("c_hab_ak_balcon").getValue().toString();
                            sHAB_DescripcionAdicional = objSnapshot.child("c_hab_al_descripcion_adicional").getValue().toString();
                            sHAB_Disponibilidad = objSnapshot.child("c_hab_am_disponibilidad").getValue().toString();
                            sHAB_ID_Hotel = objSnapshot.child( "c_hab_an_ref_id_hotel" ).getValue().toString();
                            sHAB_Razon_Social_Hotel = objSnapshot.child("c_hab_ao_ref_razon_social").getValue().toString();
                            sHAB_FechaCreacion = objSnapshot.child("c_hab_ap_fecha_creacion").getValue().toString();
                            sHAB_Status = objSnapshot.child("c_hab_aq_status").getValue().toString();

                            listaHabitaciones1.add(
                                    //"\nID: " + sHAB_ID_Habitacion
                                    "\nTipo/Nombre: " + sHAB_TipoNombre
                                            //+ "\nFoto Habitación: " + sHAB_FotoHabitacion
                                            + "\nPrecio: " + sHAB_Precio
                                            + "\nCapacidad: " + sHAB_Capacidad
                                            + "\nPiso: " + sHAB_Piso
                                            + "\nHabitación: " + sHAB_Habitacion
                                            + "\nTotal de Camas: " + sHAB_TotalCamas
                                            + "\nTipo de Baño: " + sHAB_TipoBanio
                                            + "\nTipo de Camas: " + sHAB_TipoCamas
                                            + "\nBalcón: " + sHAB_Balcon
                                            + "\nDescripción Adicional: " + sHAB_DescripcionAdicional
                                            + "\nDisponibilidad: " + sHAB_Disponibilidad
                                            + "\nID REF Hotel: " + sHAB_ID_Hotel
                                            + "\nRazón Social: " + sHAB_Razon_Social_Hotel
                                    //+ "\nFecha de Creación: " + sHAB_FechaCreacion
                                    //+ "\nStatus: " + sHAB_Status
                            );

                            listaHabitaciones2.add(
                                    "\nID: " + sHAB_ID_Habitacion
                                            + "\nTipo/Nombre: " + sHAB_TipoNombre
                                            + "\nFoto Habitación: " + sHAB_FotoHabitacion
                                            + "\nPrecio: " + sHAB_Precio
                                            + "\nCapacidad: " + sHAB_Capacidad
                                            + "\nPiso: " + sHAB_Piso
                                            + "\nHabitación: " + sHAB_Habitacion
                                            + "\nTotal de Camas: " + sHAB_TotalCamas
                                            + "\nTipo de Baño: " + sHAB_TipoBanio
                                            + "\nTipo de Camas: " + sHAB_TipoCamas
                                            + "\nBalcón: " + sHAB_Balcon
                                            + "\nDescripción Adicional: " + sHAB_DescripcionAdicional
                                            + "\nDisponibilidad: " + sHAB_Disponibilidad
                                            + "\nID REF Hotel: " + sHAB_ID_Hotel
                                            + "\nRazón Social: " + sHAB_Razon_Social_Hotel
                                            + "\nFecha de Creación: " + sHAB_FechaCreacion
                                            + "\nStatus: " + sHAB_Status
                            );

                            listaFotosHabitaciones.add( sHAB_FotoHabitacion );
                            listaIDHabitaciones.add( sHAB_ID_Habitacion );

                            aux_lista_precios_habitaciones.add(sHAB_Precio);

                        }

                    }

                    aux_cargadosHab=1;

                    aux_lista_alreves.clear();
                    aux_lista_alreves2.clear();
                    aux_lista_fotos_alreves.clear();
                    aux_lista_ids_alreves.clear();

                    aux_lista_precios_habitaciones_alreves.clear();

                    if ( aux_press_alto_bajo==1 ){

                        for (int i=0; i < (listaHabitaciones1.size()-0) ; i++){
                            aux_lista_alreves.add( listaHabitaciones1.get( (listaHabitaciones1.size()-1) - i) );
                            aux_lista_fotos_alreves.add( listaFotosHabitaciones.get( (listaFotosHabitaciones.size()-1) - i) );
                            aux_lista_ids_alreves.add( listaIDHabitaciones.get( (listaIDHabitaciones.size()-1) - i) );
                            aux_lista_precios_habitaciones_alreves.add( aux_lista_precios_habitaciones.get( (aux_lista_precios_habitaciones.size()-1) - i) );
                            Log.d("--------VALOR_REV", listaHabitaciones1.get( (listaHabitaciones1.size()-1) - i) );
                        }

                        for (int i=0; i < (listaHabitaciones2.size()-0) ; i++){
                            aux_lista_alreves2.add( listaHabitaciones2.get( (listaHabitaciones2.size()-1) - i) );
                            Log.d("--------VALOR_REV", listaHabitaciones2.get( (listaHabitaciones2.size()-1) - i) );
                        }

                        //listaHabitaciones1 = aux_lista_alreves;

                        if (getActivity()==null){
                            return;
                        }

                        adapterHabitaciones = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, aux_lista_alreves);
                        adapterHabitaciones.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                        objListaHabitaciones.setAdapter(adapterHabitaciones);

                        //I

                        objListaHabitaciones.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_expandable_list_item_2){
                            @Override
                            public int getCount() {
                                return (int)aux_lista_alreves.size();
                            }

                            @NonNull
                            @Override
                            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {


                                if(convertView == null) {
                                    convertView = ((LayoutInflater)getActivity().getSystemService(LAYOUT_INFLATER_SERVICE)).inflate(R.layout.auxiliar_lista, parent, false);

                                    convertView.setOnTouchListener(new View.OnTouchListener() {
                                        @Override
                                        public boolean onTouch(View view, MotionEvent motionEvent) {

                                            Toast.makeText(getContext(), "Tocando View", Toast.LENGTH_SHORT).show();
                                            return true;
                                        }
                                    });

                                }

                                ((TextView)convertView.findViewById(R.id.idAuxiliarListaTextViewInformacion)).setText( aux_lista_alreves.get(position) );

                                ImageView ivImageView;
                                ivImageView = new ImageView(getContext());
                                ivImageView = convertView.findViewById(R.id.idAuxiliarListaImageViewImagen);

                                if (!aux_lista_fotos_alreves.get(position).isEmpty()){
                                    Picasso.get().load(aux_lista_fotos_alreves.get(position)).into(ivImageView);
                                } else {
                                    ivImageView.setImageResource(R.drawable.ic_menu_camera);
                                }

                                Button objInformacion = convertView.findViewById(R.id.idAuxiliarListaButtonInformacion);
                                objInformacion.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        //aux_HAHB_ID_Habitacion = aux_lista_ids_alreves.get(position);

                                        objPrecioTotal.setText("");
                                        objTotalDias.setText("");

                                        objFechaInicio.setText("");
                                        objFechaFinal.setText("");

                                        HAB_Precio = aux_lista_precios_habitaciones_alreves.get(position);

                                        HAB_opcion2 = aux_lista_ids_alreves.get(position);
//                                        listaHabitaciones1.clear();
//                                        listaHabitaciones2.clear();
//                                        listaFotosHabitaciones.clear();
//                                        listaIDHabitaciones.clear();

                                        //aux_lista_USU_IDs.clear();

//                                        vaciarListaHabitaciones();

//                                        aux_USU_ID_Filtrar = aux_lista_USU_IDs.get(position);
//
//                                        aux_HOT_ID_Filtrar = listaIDHotel.get(position);
//
//                                        cargarHabitaciones( aux_USU_ID_Filtrar, aux_HOT_ID_Filtrar );
//
//                                        objPrecioAlto.setSelected(true);
//                                        objPrecioAlto.setChecked(true);

                                        //aux_cargadosHab=1;

//                                        Toast.makeText(getContext(), "USU: " + aux_lista_USU_IDs.get(position) + "\nHOT" + listaIDHotel.get(position), Toast.LENGTH_SHORT).show();
                                        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.auxiliar_lista_informacion, null);
                                        ((TextView)dialogView.findViewById(R.id.idAuxiliarListaInformacionTextViewInformacion)).setText(aux_lista_alreves2.get(position));

                                        ImageView ivImageView = dialogView.findViewById(R.id.idAuxiliarListaInformacionImageViewImagen);
                                        if (!aux_lista_fotos_alreves.get(position).isEmpty()){
                                            Picasso.get().load(aux_lista_fotos_alreves.get(position)).into(ivImageView);
                                        }

                                        AlertDialog.Builder dialogo = new AlertDialog.Builder(getContext());
                                        dialogo.setTitle("Información");
                                        dialogo.setView(dialogView);
                                        dialogo.setPositiveButton("Aceptar", null);
                                        dialogo.show();

                                    }
                                });

                                return convertView;
                            }
                        });

                        //F


                    }

                    if ( aux_press_alto_bajo==0 ) {

                        adapterHabitaciones = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, listaHabitaciones1);
                        adapterHabitaciones.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                        objListaHabitaciones.setAdapter(adapterHabitaciones);

                        //I2

                        objListaHabitaciones.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_expandable_list_item_2){
                            @Override
                            public int getCount() {
                                return (int)listaHabitaciones1.size();
                            }

                            @NonNull
                            @Override
                            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {


                                if(convertView == null) {
                                    convertView = ((LayoutInflater)getActivity().getSystemService(LAYOUT_INFLATER_SERVICE)).inflate(R.layout.auxiliar_lista, parent, false);

                                    convertView.setOnTouchListener(new View.OnTouchListener() {
                                        @Override
                                        public boolean onTouch(View view, MotionEvent motionEvent) {

                                            Toast.makeText(getContext(), "Tocando View", Toast.LENGTH_SHORT).show();
                                            return true;
                                        }
                                    });

                                }

                                ((TextView)convertView.findViewById(R.id.idAuxiliarListaTextViewInformacion)).setText( listaHabitaciones1.get(position) );

                                ImageView ivImageView;
                                ivImageView = new ImageView(getContext());
                                ivImageView = convertView.findViewById(R.id.idAuxiliarListaImageViewImagen);

                                if (!listaFotosHabitaciones.get(position).isEmpty()){
                                    Picasso.get().load(listaFotosHabitaciones.get(position)).into(ivImageView);
                                } else {
                                    ivImageView.setImageResource(R.drawable.ic_menu_camera);
                                }

                                Button objInformacion = convertView.findViewById(R.id.idAuxiliarListaButtonInformacion);
                                objInformacion.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        //aux_HAHB_ID_Habitacion = listaIDHabitaciones.get(position);

                                        HAB_Precio = aux_lista_precios_habitaciones.get(position);

                                        HAB_opcion2 = listaIDHabitaciones.get(position);
//                                        listaHabitaciones1.clear();
//                                        listaHabitaciones2.clear();
//                                        listaFotosHabitaciones.clear();
//                                        listaIDHabitaciones.clear();

                                        //aux_lista_USU_IDs.clear();

//                                        vaciarListaHabitaciones();

//                                        aux_USU_ID_Filtrar = aux_lista_USU_IDs.get(position);
//
//                                        aux_HOT_ID_Filtrar = listaIDHotel.get(position);
//
//                                        cargarHabitaciones( aux_USU_ID_Filtrar, aux_HOT_ID_Filtrar );
//
//                                        objPrecioAlto.setSelected(true);
//                                        objPrecioAlto.setChecked(true);

                                        //aux_cargadosHab=1;

//                                        Toast.makeText(getContext(), "USU: " + aux_lista_USU_IDs.get(position) + "\nHOT" + listaIDHotel.get(position), Toast.LENGTH_SHORT).show();
                                        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.auxiliar_lista_informacion, null);
                                        ((TextView)dialogView.findViewById(R.id.idAuxiliarListaInformacionTextViewInformacion)).setText(listaHabitaciones2.get(position));

                                        ImageView ivImageView = dialogView.findViewById(R.id.idAuxiliarListaInformacionImageViewImagen);
                                        if (!listaFotosHabitaciones.get(position).isEmpty()){
                                            Picasso.get().load(listaFotosHabitaciones.get(position)).into(ivImageView);
                                        }

                                        AlertDialog.Builder dialogo = new AlertDialog.Builder(getContext());
                                        dialogo.setTitle("Información");
                                        dialogo.setView(dialogView);
                                        dialogo.setPositiveButton("Aceptar", null);
                                        dialogo.show();

                                    }
                                });

                                return convertView;
                            }
                        });

                        //F2



                    }

                } else {

                    aux_cargadosHab=0;

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }

        });

        mCountDownTimer = new CountDownTimer(2000, 1000) {
            public void onTick(long millisUntilFinished) {
                TempDialog.setMessage("Please wait..");
            }

            public void onFinish() {
                TempDialog.dismiss();//Your action like intents are placed here

            }
        }.start();
    }

    private void obtenerIDsHabitacionesHoteles(){
        aux_lista_HabitacionesHoteles.clear();
//        listaHabitacionesHoteles2.clear();
//        listaFotosHabitacionesHoteles.clear();
//        listaIDHabitacionesHoteles.clear();

        ProgressDialog TempDialog;

        int i=0;

        TempDialog = new ProgressDialog(getContext());
        TempDialog.setMessage("Please wait...");
        TempDialog.setCancelable(false);
        TempDialog.setProgress(i);
        TempDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        TempDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.GRAY));

        TempDialog.show();

        databaseReferenceHabitaciones.child( "Habitaciones1" ).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                ArrayList<String> referenciasIDsUsuarios = new ArrayList<>();
                referenciasIDsUsuarios.clear();
                for(DataSnapshot ds : snapshot.getChildren()){
                    referenciasIDsUsuarios.add( ds.getRef().getKey() );

                    Log.d("VALOR_HAB_USUARIOS_ID", ds.getRef().getKey() );

                }

                for (int i=0; i < referenciasIDsUsuarios.size(); i++){

                    int aux_pos_usu_id = i;

                    databaseReferenceHabitaciones.child( "Habitaciones1" ).child( referenciasIDsUsuarios.get( aux_pos_usu_id ).toString() ).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            ArrayList<String> referenciasIDsHoteles = new ArrayList<>();
                            referenciasIDsHoteles.clear();
                            for(DataSnapshot ds : snapshot.getChildren()){
                                referenciasIDsHoteles.add( ds.getRef().getKey() );

                                Log.d("VALOR_HAB_HOTELES_ID", ds.getRef().getKey() );

                            }

                            for (int j=0; j < referenciasIDsHoteles.size(); j++){

                                int aux_pos_hot_id = j;

                                databaseReferenceHabitaciones.child( "Habitaciones1" ).child( referenciasIDsUsuarios.get( aux_pos_usu_id ).toString() ).child( referenciasIDsHoteles.get( aux_pos_hot_id ).toString() ).orderByChild( "c_hab_aa_id_habitacion" ).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        if (dataSnapshot.hasChildren()){

                                            aux_lista_HabitacionesHoteles.add( referenciasIDsHoteles.get( aux_pos_hot_id ).toString() );
                                            Log.d("HOTEL_AGREGADOO: ", referenciasIDsHoteles.get( aux_pos_hot_id ).toString() );

                                        }

                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                    }
                                });
                            }

                            mCountDownTimer = new CountDownTimer(2000, 1000) {
                                public void onTick(long millisUntilFinished) {
                                    TempDialog.setMessage("Please wait..");
                                }

                                public void onFinish() {
                                    TempDialog.dismiss();//Your action like intents are placed here

                                }
                            }.start();

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
    @Override
    public void onStart() {
        super.onStart();
        root1=null;
        obtenerIDsHabitacionesHoteles();
        //listarHoteles();
    }

    @Override
    public void onPause() {
        super.onPause();
        //listarHoteles();

        root1=null;

        Toast.makeText(getContext(), "PAUSE", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        root1=null;
        Toast.makeText(getContext(), "RESUME", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        root1=null;
    }

    public void fechaFinal(){
        objFechaFinal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);

                if ( !HOT_opcion1.equals("") && !HAB_opcion2.equals("")  ){

                    //Toast.makeText(getContext(), "\nF HOY:" + day + "/" + (month+1) + "/" + year, Toast.LENGTH_SHORT).show();
                    String FH = + day + "/" + (month+1) + "/" + year;

                    objPicker = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                            objFechaFinal.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);

                            String FI="", FF="";
                            FI = objFechaInicio.getText().toString();
                            FF = objFechaFinal.getText().toString();

                            //I
                            String auxFI="";
                            auxFI = objFechaInicio.getText().toString();

                            int dateDifferenceHoy = (int) getDateDiff(new SimpleDateFormat("dd/MM/yyyy"), FH, FF);

                            if ( dateDifferenceHoy>=0 ){

                                if ( !auxFI.equals("") ){

                                    int dateDifference = (int) getDateDiff(new SimpleDateFormat("dd/MM/yyyy"), FI, FF);

                                    if (dateDifference==0){
                                        precioTotal = HAB_Precio;
                                        totalDias = "1";
                                    } else if (dateDifference>0){
                                        if (dateDifference==1){
                                            long aux_total_precio = (Long.parseLong(HAB_Precio)*2);
                                            precioTotal = String.valueOf(aux_total_precio);
                                            totalDias = "2";
                                        } else {
                                            long aux_total_precio = (Long.parseLong(HAB_Precio)*(dateDifference+1));
                                            precioTotal = String.valueOf(aux_total_precio);
                                            totalDias = String.valueOf( (dateDifference+1) );
                                        }
                                    } else {
                                        precioTotal = "";
                                        totalDias = "";

                                        objPrecioTotal.setText( precioTotal );
                                        objTotalDias.setText( totalDias );

                                        objFechaInicio.setText("");
                                        //objFechaFinal.setText("");

                                        Toast.makeText(getContext(), "Ingrese Fecha Válida", Toast.LENGTH_SHORT).show();
                                    }

                                    objPrecioTotal.setText( precioTotal );
                                    objTotalDias.setText( totalDias );
                                }//Debe llenar F Inicio

                            } else {
                                precioTotal = "";
                                totalDias = "";

                                objPrecioTotal.setText( precioTotal );
                                objTotalDias.setText( totalDias );

                                objFechaFinal.setText("");
                                Toast.makeText(getContext(), "Fecha Final\nDebe ser una Fecha Posterior a la Actual", Toast.LENGTH_SHORT).show();
                            }

                            //F
                        }
                    }, year, month, day);
                    objPicker.show();
                } else {
                    Toast.makeText(getContext(), "Debe Seleccionar Hotel y Habitación", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void fechaInicio(){

        objFechaInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);

                if ( !HOT_opcion1.equals("") && !HAB_opcion2.equals("")  ){

                    //Toast.makeText(getContext(), "\nF HOY:" + day + "/" + (month+1) + "/" + year, Toast.LENGTH_SHORT).show();
                    String FH = + day + "/" + (month+1) + "/" + year;

                    objPicker = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                            objFechaInicio.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);

                            String FI="", FF="";
                            FI = objFechaInicio.getText().toString();
                            FF = objFechaFinal.getText().toString();

                            String auxFF="";
                            auxFF = objFechaFinal.getText().toString();

                            int dateDifferenceHoy = (int) getDateDiff(new SimpleDateFormat("dd/MM/yyyy"), FH, FI);

                            if ( dateDifferenceHoy>=0 ){

                                if ( !auxFF.equals("") ){

                                    int dateDifference = (int) getDateDiff(new SimpleDateFormat("dd/MM/yyyy"), FI, FF);

                                    if (dateDifference==0){
                                        precioTotal = HAB_Precio;
                                        totalDias = "1";
                                    } else if (dateDifference>0){
                                        if (dateDifference==1){
                                            long aux_total_precio = (Long.parseLong(HAB_Precio)*2);
                                            precioTotal = String.valueOf(aux_total_precio);
                                            totalDias = "2";
                                        } else {
                                            long aux_total_precio = (Long.parseLong(HAB_Precio)*(dateDifference+1));
                                            precioTotal = String.valueOf(aux_total_precio);
                                            totalDias = String.valueOf( (dateDifference+1) );
                                        }
                                    } else {
                                        precioTotal = "";
                                        totalDias = "";

                                        objPrecioTotal.setText( precioTotal );
                                        objTotalDias.setText( totalDias );

                                        //objFechaInicio.setText("");
                                        objFechaFinal.setText("");

                                        Toast.makeText(getContext(), "Ingrese Fecha Válida", Toast.LENGTH_SHORT).show();
                                    }
                                    objPrecioTotal.setText( precioTotal );
                                    objTotalDias.setText( totalDias );
                                }//Debe llenar Fecha Final
                            } else {

                                precioTotal = "";
                                totalDias = "";

                                objPrecioTotal.setText( precioTotal );
                                objTotalDias.setText( totalDias );

                                objFechaInicio.setText("");
                                Toast.makeText(getContext(), "Fecha Inicial\nDebe ser una Fecha Posterior a la Actual", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, year, month, day);
                    objPicker.show();

                } else {
                    Toast.makeText(getContext(), "Debe Seleccionar Hotel y Habitación", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public static long getDateDiff(SimpleDateFormat format, String oldDate, String newDate) {
        try {
            return TimeUnit.DAYS.convert(format.parse(newDate).getTime() - format.parse(oldDate).getTime(), TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public long getFechaMilisegundos(){

        Calendar calendar = Calendar.getInstance();
        long tiempoUnix = calendar.getTimeInMillis();
        return tiempoUnix;

    }

}