package com.example.paasdcmfirebasehotel.ui.listar;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.paasdcmfirebasehotel.R;
import com.example.paasdcmfirebasehotel.databinding.FragmentListarBinding;
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
import java.util.HashMap;
import java.util.List;

public class ListarFragment extends Fragment {

    private ListarViewModel listarViewModel;
    private FragmentListarBinding binding;

    ListView objLista;

    RadioButton objUsuarios;
    RadioButton objHoteles;
    RadioButton objHabitaciones;
    RadioButton objReservaciones;

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
    ArrayAdapter<String> adapter;
    List<String> listaUsuarios1 = new ArrayList<>();
    List<String> listaUsuarios2 = new ArrayList<>();
    List<String> listaFotos = new ArrayList<>();
    List<String> listaID = new ArrayList<>();

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

    ArrayAdapter<String> adapterReservaciones;
    List<String> listaReservaciones1 = new ArrayList<>();
    List<String> listaReservaciones2 = new ArrayList<>();
    List<String> listaIDReservaciones = new ArrayList<>();
    List<String> listaUSUNombres = new ArrayList<>();
    List<String> listaHOTNombres = new ArrayList<>();
    List<String> listaHABNombres = new ArrayList<>();


    //Cadenas
    String sUSU_ID=""
            , sUSU_Correo=""
            , sUSU_Contrasenia=""
            , sUSU_Nombre=""
            , sUSU_Apellidos=""
            , sUSU_Ruta=""
            , sUSU_Telefono=""
            , sUSU_Pais=""
            , sUSU_Estado=""
            , sUSU_CodigoPostal=""
            , sUSU_Domicilio=""
            , sUSU_Sexo=""
            , sUSU_FechaNacimiento=""
            , sUSU_Reputacion=""
            , sUSU_FechaCreacion=""
            , sUSU_Status=""
            , sUSU_PermisoHotel=""
            , sUSU_TipoUsuario="";

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

    //Cadenas Reservaciones
    String sRES_ID_Reservacion=""
            , sRES_REF_ID_Usuario_Hotel=""
            , sRES_REF_ID_Hotel=""
            , sRES_REF_ID_Habitacion=""
            , sRES_REF_ID_Usuario_Actual=""
            , sRES_Fecha_Inicio=""
            , sRES_Fecha_Final=""
            , sRES_Total_Dias=""
            , sRES_Precio_Total=""
            , sRES_Fecha_Creacion=""
            , sRES_Status="";//11

    long aux_conteo_snapshots=0;
    long aux_conteo_snapshotsHotel=0;
    long aux_conteo_snapshotsHabitaciones=0;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        listarViewModel = new ViewModelProvider(this).get(ListarViewModel.class);

        binding = FragmentListarBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("RTDB_Users");
        databaseReferenceHotel = FirebaseDatabase.getInstance().getReference().child("RTDB_Hotels");
        databaseReferenceHabitaciones = FirebaseDatabase.getInstance().getReference().child("RTDB_Rooms");
        databaseReferenceReservacion = FirebaseDatabase.getInstance().getReference().child("RTDB_Reservation");
        storageProfilePicsRef = FirebaseStorage.getInstance().getReference().child("STORAGE_Users_Profile_Pics");
        storageProfilePicsRefHotel = FirebaseStorage.getInstance().getReference().child("STORAGE_Hotels_Profile_Pics");
        storageProfilePicsRefHabitaciones = FirebaseStorage.getInstance().getReference().child("STORAGE_Rooms_Profile_Pics");

        asignar(root);

        listar();



        return root;
    }

    private void listar() {

        if (getActivity()==null){
            return ;
        }

        objUsuarios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                vaciarLista();

                Toast.makeText(getContext(), "USUARIOS", Toast.LENGTH_SHORT).show();

                databaseReference.child( "Usuarios1" ).orderByChild( "c_usu_ao_fecha_creacion" ).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        DataSnapshot objSnapshot2;
                        objSnapshot2 = dataSnapshot;
                        aux_conteo_snapshots = objSnapshot2.getChildrenCount();

                        listaUsuarios1.clear();
                        listaUsuarios2.clear();
                        listaFotos.clear();
                        listaID.clear();

                        for (DataSnapshot objSnapshot : dataSnapshot.getChildren()){

                            sUSU_ID = objSnapshot.child("c_usu_aa_id_usuario").getValue().toString();

                            sUSU_Correo = objSnapshot.child("c_usu_ab_correo").getValue().toString();
                            sUSU_Contrasenia = objSnapshot.child("c_usu_ac_contrasenia").getValue().toString();

                            sUSU_Nombre = objSnapshot.child("c_usu_ad_nombre").getValue().toString();
                            sUSU_Apellidos = objSnapshot.child("c_usu_ae_apellidos").getValue().toString();
                            sUSU_Ruta = objSnapshot.child("c_usu_af_ruta_img").getValue().toString();
                            sUSU_Telefono = objSnapshot.child("c_usu_ag_telefono").getValue().toString();
                            sUSU_Pais = objSnapshot.child("c_usu_ah_pais").getValue().toString();
                            sUSU_Estado = objSnapshot.child("c_usu_ai_estado").getValue().toString();
                            sUSU_CodigoPostal = objSnapshot.child("c_usu_aj_codigo_postal").getValue().toString();
                            sUSU_Domicilio = objSnapshot.child("c_usu_ak_domicilio").getValue().toString();
                            sUSU_Sexo = objSnapshot.child("c_usu_al_sexo").getValue().toString();
                            sUSU_FechaNacimiento = objSnapshot.child("c_usu_am_fecha_nacimiento").getValue().toString();
                            sUSU_Reputacion = objSnapshot.child("c_usu_an_reputacion").getValue().toString();

                            sUSU_FechaCreacion = objSnapshot.child("c_usu_ao_fecha_creacion").getValue().toString();
                            sUSU_Status = objSnapshot.child("c_usu_ap_status").getValue().toString();
                            sUSU_PermisoHotel = objSnapshot.child("c_usu_aq_permiso_hotel").getValue().toString();
                            sUSU_TipoUsuario = objSnapshot.child("c_usu_ar_tipo_usuario").getValue().toString();

                            listaUsuarios1.add(
                                    //"\nID: "+sUSU_ID
                                    //+"\nCorreo: "+sUSU_Correo
                                    //+"\nContraseña: "+sUSU_Contrasenia
                                    "\nNombre(s): "+sUSU_Nombre
                                            +"\nApellidos: "+sUSU_Apellidos
                                            //+"\nRuta Imágen: "+sUSU_Ruta
                                            +"\nTeléfono: "+sUSU_Telefono
                                            +"\nPaís: "+sUSU_Pais
                                            +"\nEstado: "+sUSU_Estado
                                            +"\nCódigo Postal: "+sUSU_CodigoPostal
                                            +"\nDomicilio: "+sUSU_Domicilio
                                            +"\nSexo: "+sUSU_Sexo
                                            +"\nFecha de Nacimiento: "+sUSU_FechaNacimiento
                                            //+"\nReputación: "+sUSU_Reputacion
                                            //+"\nFecha de Creación: "+sUSU_FechaCreacion
                                            //+"\nStatus: "+sUSU_Status
                                            //+"\nPermiso Hotel: "+sUSU_PermisoHotel
                                            //+"\nTipo de Usuario: "+sUSU_TipoUsuario
                                            +"\n" );
                            listaUsuarios2.add(
                                    "\nID: "+sUSU_ID
                                            +"\nCorreo: "+sUSU_Correo
                                            +"\nContraseña: "+sUSU_Contrasenia
                                            +"\nNombre(s): "+sUSU_Nombre
                                            +"\nApellidos: "+sUSU_Apellidos
                                            +"\nRuta Imágen: "+sUSU_Ruta
                                            +"\nTeléfono: "+sUSU_Telefono
                                            +"\nPaís: "+sUSU_Pais
                                            +"\nEstado: "+sUSU_Estado
                                            +"\nCódigo Postal: "+sUSU_CodigoPostal
                                            +"\nDomicilio: "+sUSU_Domicilio
                                            +"\nSexo: "+sUSU_Sexo
                                            +"\nFecha de Nacimiento: "+sUSU_FechaNacimiento
                                            +"\nReputación: "+sUSU_Reputacion
                                            +"\nFecha de Creación: "+sUSU_FechaCreacion
                                            +"\nStatus: "+sUSU_Status
                                            +"\nPermiso Hotel: "+sUSU_PermisoHotel
                                            +"\nTipo de Usuario: "+sUSU_TipoUsuario
                                            +"\n" );
                            listaFotos.add( sUSU_Ruta );
                            listaID.add( sUSU_ID );
                        }

                        if (getActivity()==null){
                            return ;
                        }

                        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, listaUsuarios1);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        objLista.setAdapter(adapter);

                        objLista.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_expandable_list_item_2){
                            @Override
                            public int getCount() {
                                return (int)aux_conteo_snapshots;
                            }

                            @NonNull
                            @Override
                            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

                                if(convertView == null) {
                                    convertView = ((LayoutInflater)getActivity().getSystemService(LAYOUT_INFLATER_SERVICE)).inflate(R.layout.auxiliar_lista, parent, false);
                                }

                                ((TextView)convertView.findViewById(R.id.idAuxiliarListaTextViewInformacion)).setText( listaUsuarios1.get(position) );

                                ImageView ivImageView = convertView.findViewById(R.id.idAuxiliarListaImageViewImagen);

                                if (!listaFotos.get(position).isEmpty()){
                                    Picasso.get().load(listaFotos.get(position)).into(ivImageView);
                                }

                                Button objInformacion = convertView.findViewById(R.id.idAuxiliarListaButtonInformacion);
                                objInformacion.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        Toast.makeText(getContext(), "Aqui", Toast.LENGTH_SHORT).show();

                                        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.auxiliar_lista_informacion, null);
                                        ((TextView)dialogView.findViewById(R.id.idAuxiliarListaInformacionTextViewInformacion)).setText(listaUsuarios2.get(position));

                                        ImageView ivImageView = dialogView.findViewById(R.id.idAuxiliarListaInformacionImageViewImagen);
                                        if (!listaFotos.get(position).isEmpty()){
                                            Picasso.get().load(listaFotos.get(position)).into(ivImageView);
                                        } else {
                                            ivImageView.setImageResource(R.drawable.ic_menu_camera);
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

                        objLista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {



                            }
                        });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });

        objHoteles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                vaciarLista();

                Toast.makeText(getContext(), "HOTELES", Toast.LENGTH_SHORT).show();

                listaHotel1.clear();
                listaHotel2.clear();
                listaFotosHotel.clear();
                listaIDHotel.clear();

                databaseReferenceHotel.child( "Hoteles1" ).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        //Toast.makeText(getContext(), String.valueOf(snapshot.getChildrenCount()) + snapshot.getChildren() + snapshot.getKey(), Toast.LENGTH_SHORT).show();

                        ArrayList<String> referencias = new ArrayList<>();
                        referencias.clear();
                        for(DataSnapshot ds : snapshot.getChildren()){

                            //ds.getValue();
                            //ds.getRef();
                            //Toast.makeText(getContext(), ds.getRef().getKey(), Toast.LENGTH_SHORT).show();

                            referencias.add( ds.getRef().getKey() );
                        }
                        Toast.makeText(getContext(), String.valueOf(referencias.size() ) , Toast.LENGTH_SHORT).show();

                        Log.d("--------VALOR", String.valueOf(referencias.size()) );


                        for (int i=0; i < referencias.size(); i++){

                            int aux_i = i;

                            Log.d("--------VALOR", referencias.get(i) );

                            databaseReferenceHotel.child( "Hoteles1" ).child( referencias.get( i ).toString() ).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                    ArrayList<String> referenciasIDHoteles = new ArrayList<>();
                                    referenciasIDHoteles.clear();
                                    for(DataSnapshot ds : snapshot.getChildren()){

                                        //ds.getValue();
                                        //ds.getRef();
                                        //Toast.makeText(getContext(), ds.getRef().getKey(), Toast.LENGTH_SHORT).show();

                                        referenciasIDHoteles.add( ds.getRef().getKey() );
                                    }
                                    Toast.makeText(getContext(), String.valueOf(referenciasIDHoteles.size()) , Toast.LENGTH_SHORT).show();

                                    Log.d("--------VALOR_2", String.valueOf( referenciasIDHoteles.size()) );

                                    for (int j=0; j < referenciasIDHoteles.size(); j++){

                                        Log.d("--------VALOR_2", referenciasIDHoteles.get(j) );

                                        databaseReferenceHotel.child( "Hoteles1" ).child( referencias.get( aux_i ).toString() ).child( referenciasIDHoteles.get(j) ).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                //String sHOT_ID = dataSnapshot.child("c_hot_aa_id_hotel").getValue().toString();

                                                //String sHOT_RazonSocial="";
                                                //if (dataSnapshot.child("c_hot_ab_razon_social").exists()){
                                                //sHOT_RazonSocial = dataSnapshot.child("c_hot_ab_razon_social").getValue().toString();
                                                //}

                                                databaseReferenceHotel.child( "Hoteles1" ).child( referencias.get( aux_i ).toString() ).orderByChild( "c_hot_aa_id_hotel" ).addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                        DataSnapshot objSnapshot2;
                                                        objSnapshot2 = dataSnapshot;
                                                        aux_conteo_snapshotsHotel = objSnapshot2.getChildrenCount();

                                                        for (DataSnapshot objSnapshot : dataSnapshot.getChildren()){

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

                                                        }

                                                        if (getActivity()==null){
                                                            return ;
                                                        }

                                                        adapterHotel = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, listaHotel1);
                                                        adapterHotel.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                                                        objLista.setAdapter(adapterHotel);

                                                        objLista.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_expandable_list_item_2){
                                                            @Override
                                                            public int getCount() {
                                                                return (int)listaHotel1.size();
                                                            }

                                                            @NonNull
                                                            @Override
                                                            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

                                                                if(convertView == null) {
                                                                    convertView = ((LayoutInflater)getActivity().getSystemService(LAYOUT_INFLATER_SERVICE)).inflate(R.layout.auxiliar_lista, parent, false);
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

                                                                        Toast.makeText(getContext(), "Aqui", Toast.LENGTH_SHORT).show();

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
        });

        objHabitaciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                vaciarLista();

                Toast.makeText(getContext(), "HABITACIONES", Toast.LENGTH_SHORT).show();

                listaHabitaciones1.clear();
                listaHabitaciones2.clear();
                listaFotosHabitaciones.clear();
                listaIDHabitaciones.clear();

                databaseReferenceHabitaciones.child( "Habitaciones1" ).addListenerForSingleValueEvent(new ValueEventListener() {
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

                            databaseReferenceHabitaciones.child( "Habitaciones1" ).child( referenciasIDsUsuarios.get( aux_pos_usu_id ).toString() ).addListenerForSingleValueEvent(new ValueEventListener() {
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


                                                for (DataSnapshot objSnapshot : dataSnapshot.getChildren()){

                                                    sHAB_ID_Habitacion= objSnapshot.child( "c_hab_aa_id_habitacion" ).getValue().toString();
                                                    sHAB_TipoNombre= objSnapshot.child( "c_hab_ab_tipo_nombre" ).getValue().toString();
                                                    sHAB_FotoHabitacion= objSnapshot.child( "c_hab_ac_foto_habitacion" ).getValue().toString();
                                                    sHAB_Precio= objSnapshot.child( "c_hab_ad_precio" ).getValue().toString();
                                                    sHAB_Capacidad= objSnapshot.child( "c_hab_ae_capacidad" ).getValue().toString();
                                                    sHAB_Piso= objSnapshot.child( "c_hab_af_piso" ).getValue().toString();
                                                    sHAB_Habitacion = objSnapshot.child( "c_hab_ag_habitacion" ).getValue().toString();
                                                    sHAB_TotalCamas = objSnapshot.child( "c_hab_ah_total_camas" ).getValue().toString();
                                                    sHAB_TipoBanio = objSnapshot.child( "c_hab_ai_tipo_banio" ).getValue().toString();
                                                    sHAB_TipoCamas = objSnapshot.child( "c_hab_aj_tipo_camas" ).getValue().toString();
                                                    sHAB_Balcon = objSnapshot.child( "c_hab_ak_balcon" ).getValue().toString();
                                                    sHAB_DescripcionAdicional = objSnapshot.child( "c_hab_al_descripcion_adicional" ).getValue().toString();
                                                    sHAB_Disponibilidad = objSnapshot.child( "c_hab_am_disponibilidad" ).getValue().toString();
                                                    sHAB_ID_Hotel = objSnapshot.child( "c_hab_an_ref_id_hotel" ).getValue().toString();
                                                    sHAB_Razon_Social_Hotel = objSnapshot.child( "c_hab_ao_ref_razon_social" ).getValue().toString();
                                                    sHAB_FechaCreacion = objSnapshot.child( "c_hab_ap_fecha_creacion" ).getValue().toString();
                                                    sHAB_Status = objSnapshot.child( "c_hab_aq_status" ).getValue().toString();//17

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

                                                }

                                                if (getActivity()==null){
                                                    return ;
                                                }

                                                adapterHabitaciones = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, listaHabitaciones1);
                                                adapterHabitaciones.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                                                objLista.setAdapter(adapterHabitaciones);

                                                objLista.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_expandable_list_item_2){
                                                    @Override
                                                    public int getCount() {
                                                        return (int)listaHabitaciones1.size();
                                                    }

                                                    @NonNull
                                                    @Override
                                                    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

                                                        if(convertView == null) {
                                                            convertView = ((LayoutInflater)getActivity().getSystemService(LAYOUT_INFLATER_SERVICE)).inflate(R.layout.auxiliar_lista, parent, false);
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

                                                                Toast.makeText(getContext(), "Aqui", Toast.LENGTH_SHORT).show();

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


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }
        });

        objReservaciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                vaciarLista();
                Toast.makeText(getContext(), "RESERVACIONES", Toast.LENGTH_SHORT).show();

                listaReservaciones1.clear();
                listaReservaciones2.clear();
                listaIDReservaciones.clear();

                databaseReferenceReservacion.child( "Reservaciones1" ).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        ArrayList<String> referenciasIDsReservaciones = new ArrayList<>();
                        referenciasIDsReservaciones.clear();
                        for(DataSnapshot ds : snapshot.getChildren()){
                            referenciasIDsReservaciones.add( ds.getRef().getKey() );
                        }

                        for (int i=0; i < referenciasIDsReservaciones.size(); i++){
                            int aux_pos_res_id = i;

                            databaseReferenceReservacion.child( "Reservaciones1" ).child( referenciasIDsReservaciones.get( aux_pos_res_id ).toString() ).orderByChild( "c_res_aj_fecha_creacion" ).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    //11Toast.makeText(getContext(), "TOTAL CHILDREN: \n" + String.valueOf(dataSnapshot.getChildrenCount()), Toast.LENGTH_SHORT).show();

                                    sRES_ID_Reservacion = dataSnapshot.child( "c_res_aa_id_reservacion" ).getValue().toString();
                                    sRES_REF_ID_Usuario_Hotel = dataSnapshot.child( "c_res_ab_ref_id_usuario_hotel" ).getValue().toString();
                                    sRES_REF_ID_Hotel = dataSnapshot.child( "c_res_ac_ref_id_hotel" ).getValue().toString();
                                    sRES_REF_ID_Habitacion = dataSnapshot.child( "c_res_ad_ref_id_habitacion" ).getValue().toString();
                                    sRES_REF_ID_Usuario_Actual = dataSnapshot.child( "c_res_ae_ref_id_usuario_actual" ).getValue().toString();
                                    sRES_Fecha_Inicio = dataSnapshot.child( "c_res_af_fecha_inicio" ).getValue().toString();
                                    sRES_Fecha_Final = dataSnapshot.child( "c_res_ag_fecha_final" ).getValue().toString();
                                    sRES_Total_Dias = dataSnapshot.child( "c_res_ah_total_dias" ).getValue().toString();
                                    sRES_Precio_Total = dataSnapshot.child( "c_res_ai_precio_total" ).getValue().toString();
                                    sRES_Fecha_Creacion = dataSnapshot.child( "c_res_aj_fecha_creacion" ).getValue().toString();
                                    sRES_Status = dataSnapshot.child( "c_res_ak_status" ).getValue().toString();//11

                                    String aux_USU_Nombre="";
                                    for ( int i = 0; i<listaID.size(); i++){
                                        if (listaID.get(i).equals(sRES_REF_ID_Usuario_Hotel)){
                                            aux_USU_Nombre = listaUSUNombres.get(i).toString();
                                        }
                                    }

                                    String aux_HOT_Nombre="";
                                    for ( int i = 0; i<listaIDHotel.size(); i++){
                                        if (listaIDHotel.get(i).equals(sRES_REF_ID_Hotel)){
                                            aux_HOT_Nombre = listaHOTNombres.get(i).toString();
                                        }
                                    }

                                    String aux_HAB_Nombre="";
                                    for ( int i = 0; i<listaIDHabitaciones.size(); i++){
                                        if (listaIDHabitaciones.get(i).equals(sRES_REF_ID_Habitacion)){
                                            aux_HAB_Nombre = listaHABNombres.get(i).toString();
                                        }
                                    }

                                    String aux_USU_NombreQReservo="";
                                    for ( int i = 0; i<listaID.size(); i++){
                                        if (listaID.get(i).equals(sRES_REF_ID_Usuario_Actual)){
                                            aux_USU_NombreQReservo = listaUSUNombres.get(i).toString();
                                        }
                                    }

                                    listaReservaciones1.add(
                                            //"\nID: " + sRES_ID_Reservacion
                                                    //+ "\nREF ID Usuario Hotel: " + sRES_REF_ID_Usuario_Hotel
                                                    "\nNombre a Quien Pertenece el Hotel: " + aux_USU_Nombre
                                                    //+ "\nREF ID Hotel: " + sRES_REF_ID_Hotel
                                                    + "\nNombre del Hotel: " + aux_HOT_Nombre
                                                    //+ "\nREF ID Habitacion: " + sRES_REF_ID_Habitacion
                                                    + "\nNombre de Habitación: " + aux_HAB_Nombre
                                                    //+ "\nREF ID Usuario Actual: " + sRES_REF_ID_Usuario_Actual
                                                    + "\nNombre Quien Reservó: " + aux_USU_NombreQReservo
                                                    + "\nFecha Inicio: " + sRES_Fecha_Inicio
                                                    + "\nFecha Final: " + sRES_Fecha_Final
                                                    + "\nTotal de Días: " + sRES_Total_Dias
                                                    + "\nPrecio Total: " + sRES_Precio_Total
                                                    //+ "\nFecha Creación: " + sRES_Fecha_Creacion
                                                    //+ "\nStatus: " + sRES_Status
                                    );

                                    listaReservaciones2.add(
                                            "\nID: " + sRES_ID_Reservacion
                                                    + "\nREF ID Usuario Hotel: " + sRES_REF_ID_Usuario_Hotel
                                                    + "\nNombre a Quien Pertenece el Hotel: " + aux_USU_Nombre
                                                    + "\nREF ID Hotel: " + sRES_REF_ID_Hotel
                                                    + "\nNombre del Hotel: " + aux_HOT_Nombre
                                                    + "\nREF ID Habitacion: " + sRES_REF_ID_Habitacion
                                                    + "\nNombre de Habitación: " + aux_HAB_Nombre
                                                    + "\nREF ID Usuario Actual: " + sRES_REF_ID_Usuario_Actual
                                                    + "\nNombre Quien Reservó: " + aux_USU_NombreQReservo
                                                    + "\nFecha Inicio: " + sRES_Fecha_Inicio
                                                    + "\nFecha Final: " + sRES_Fecha_Final
                                                    + "\nTotal de Días: " + sRES_Total_Dias
                                                    + "\nPrecio Total: " + sRES_Precio_Total
                                                    + "\nFecha Creación: " + sRES_Fecha_Creacion
                                                    + "\nStatus: " + sRES_Status
                                    );

                                    listaIDReservaciones.add( sRES_ID_Reservacion );

                                    if (getActivity()==null){
                                        return ;
                                    }

                                    objLista.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_expandable_list_item_2){
                                        @Override
                                        public int getCount() {
                                            return (int)listaReservaciones1.size();
                                        }

                                        @NonNull
                                        @Override
                                        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

                                            if(convertView == null) {
                                                convertView = ((LayoutInflater)getActivity().getSystemService(LAYOUT_INFLATER_SERVICE)).inflate(R.layout.auxiliar_lista, parent, false);
                                            }

                                            ((TextView)convertView.findViewById(R.id.idAuxiliarListaTextViewInformacion)).setText( listaReservaciones1.get(position) );

                                            ImageView ivImageView = convertView.findViewById(R.id.idAuxiliarListaImageViewImagen);
                                            ivImageView.setVisibility(View.INVISIBLE);

                                            Button objInformacion = convertView.findViewById(R.id.idAuxiliarListaButtonInformacion);
                                            objInformacion.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {

                                                    Toast.makeText(getContext(), "Aqui", Toast.LENGTH_SHORT).show();

                                                    View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.auxiliar_lista_informacion, null);
                                                    ((TextView)dialogView.findViewById(R.id.idAuxiliarListaInformacionTextViewInformacion)).setText(listaReservaciones2.get(position));

                                                    ImageView ivImageView = dialogView.findViewById(R.id.idAuxiliarListaInformacionImageViewImagen);
                                                    ivImageView.setVisibility(View.INVISIBLE);

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
        });
    }

    private void vaciarLista(){

        ArrayAdapter<String> adapterVaciar;
        List<String> listaVaciar = new ArrayList<>();
        adapterVaciar = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, listaVaciar);
        adapterVaciar.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        objLista.setAdapter(adapterVaciar);

    }

    private void asignar(View root) {

         objLista = root.findViewById(R.id.idListarListViewListar);

         objUsuarios = root.findViewById(R.id.idListarRadioButtonUsuarios);
         objHoteles = root.findViewById(R.id.idListarRadioButtonHoteles);
         objHabitaciones = root.findViewById(R.id.idListarRadioButtonHabitaciones);
         objReservaciones = root.findViewById(R.id.idListarRadioButtonReservaciones);


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    private void cargarDatosHoteles(){

        listaHotel1.clear();
        listaHotel2.clear();
        listaFotosHotel.clear();
        listaIDHotel.clear();

        listaHOTNombres.clear();

        databaseReferenceHotel.child( "Hoteles1" ).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> referencias = new ArrayList<>();
                referencias.clear();
                for(DataSnapshot ds : snapshot.getChildren()){
                    referencias.add( ds.getRef().getKey() );
                }

                for (int i=0; i < referencias.size(); i++){
                    int aux_i = i;

                    databaseReferenceHotel.child( "Hoteles1" ).child( referencias.get( i ).toString() ).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            ArrayList<String> referenciasIDHoteles = new ArrayList<>();
                            referenciasIDHoteles.clear();
                            for(DataSnapshot ds : snapshot.getChildren()){
                                referenciasIDHoteles.add( ds.getRef().getKey() );
                            }

                            for (int j=0; j < referenciasIDHoteles.size(); j++){

                                databaseReferenceHotel.child( "Hoteles1" ).child( referencias.get( aux_i ).toString() ).child( referenciasIDHoteles.get(j) ).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        databaseReferenceHotel.child( "Hoteles1" ).child( referencias.get( aux_i ).toString() ).orderByChild( "c_hot_aa_id_hotel" ).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                DataSnapshot objSnapshot2;
                                                objSnapshot2 = dataSnapshot;
                                                aux_conteo_snapshotsHotel = objSnapshot2.getChildrenCount();

                                                for (DataSnapshot objSnapshot : dataSnapshot.getChildren()){

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

                                                    listaHOTNombres.add( sHOT_RazonSocial );

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

    private void cargarDatosHabitaciones(){

        listaHabitaciones1.clear();
        listaHabitaciones2.clear();
        listaFotosHabitaciones.clear();
        listaIDHabitaciones.clear();

        listaHABNombres.clear();

        databaseReferenceHabitaciones.child( "Habitaciones1" ).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                ArrayList<String> referenciasIDsUsuarios = new ArrayList<>();
                referenciasIDsUsuarios.clear();
                for(DataSnapshot ds : snapshot.getChildren()){
                    referenciasIDsUsuarios.add( ds.getRef().getKey() );
                }

                for (int i=0; i < referenciasIDsUsuarios.size(); i++){
                    int aux_pos_usu_id = i;

                    databaseReferenceHabitaciones.child( "Habitaciones1" ).child( referenciasIDsUsuarios.get( aux_pos_usu_id ).toString() ).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            ArrayList<String> referenciasIDsHoteles = new ArrayList<>();
                            referenciasIDsHoteles.clear();
                            for(DataSnapshot ds : snapshot.getChildren()){
                                referenciasIDsHoteles.add( ds.getRef().getKey() );
                            }

                            for (int j=0; j < referenciasIDsHoteles.size(); j++){
                                int aux_pos_hot_id = j;

                                databaseReferenceHabitaciones.child( "Habitaciones1" ).child( referenciasIDsUsuarios.get( aux_pos_usu_id ).toString() ).child( referenciasIDsHoteles.get( aux_pos_hot_id ).toString() ).orderByChild( "c_hab_aa_id_habitacion" ).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for (DataSnapshot objSnapshot : dataSnapshot.getChildren()){

                                            sHAB_ID_Habitacion= objSnapshot.child( "c_hab_aa_id_habitacion" ).getValue().toString();
                                            sHAB_TipoNombre= objSnapshot.child( "c_hab_ab_tipo_nombre" ).getValue().toString();
                                            sHAB_FotoHabitacion= objSnapshot.child( "c_hab_ac_foto_habitacion" ).getValue().toString();
                                            sHAB_Precio= objSnapshot.child( "c_hab_ad_precio" ).getValue().toString();
                                            sHAB_Capacidad= objSnapshot.child( "c_hab_ae_capacidad" ).getValue().toString();
                                            sHAB_Piso= objSnapshot.child( "c_hab_af_piso" ).getValue().toString();
                                            sHAB_Habitacion = objSnapshot.child( "c_hab_ag_habitacion" ).getValue().toString();
                                            sHAB_TotalCamas = objSnapshot.child( "c_hab_ah_total_camas" ).getValue().toString();
                                            sHAB_TipoBanio = objSnapshot.child( "c_hab_ai_tipo_banio" ).getValue().toString();
                                            sHAB_TipoCamas = objSnapshot.child( "c_hab_aj_tipo_camas" ).getValue().toString();
                                            sHAB_Balcon = objSnapshot.child( "c_hab_ak_balcon" ).getValue().toString();
                                            sHAB_DescripcionAdicional = objSnapshot.child( "c_hab_al_descripcion_adicional" ).getValue().toString();
                                            sHAB_Disponibilidad = objSnapshot.child( "c_hab_am_disponibilidad" ).getValue().toString();
                                            sHAB_ID_Hotel = objSnapshot.child( "c_hab_an_ref_id_hotel" ).getValue().toString();
                                            sHAB_Razon_Social_Hotel = objSnapshot.child( "c_hab_ao_ref_razon_social" ).getValue().toString();
                                            sHAB_FechaCreacion = objSnapshot.child( "c_hab_ap_fecha_creacion" ).getValue().toString();
                                            sHAB_Status = objSnapshot.child( "c_hab_aq_status" ).getValue().toString();//17

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

                                            listaHABNombres.add( sHAB_TipoNombre );
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
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void cargarDatosUsuarios(){

        databaseReference.child( "Usuarios1" ).orderByChild( "c_usu_ao_fecha_creacion" ).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                DataSnapshot objSnapshot2;
                objSnapshot2 = dataSnapshot;
                aux_conteo_snapshots = objSnapshot2.getChildrenCount();

                listaUsuarios1.clear();
                listaUsuarios2.clear();
                listaFotos.clear();
                listaID.clear();

                listaUSUNombres.clear();

                for (DataSnapshot objSnapshot : dataSnapshot.getChildren()){

                    sUSU_ID = objSnapshot.child("c_usu_aa_id_usuario").getValue().toString();

                    sUSU_Correo = objSnapshot.child("c_usu_ab_correo").getValue().toString();
                    sUSU_Contrasenia = objSnapshot.child("c_usu_ac_contrasenia").getValue().toString();

                    sUSU_Nombre = objSnapshot.child("c_usu_ad_nombre").getValue().toString();
                    sUSU_Apellidos = objSnapshot.child("c_usu_ae_apellidos").getValue().toString();
                    sUSU_Ruta = objSnapshot.child("c_usu_af_ruta_img").getValue().toString();
                    sUSU_Telefono = objSnapshot.child("c_usu_ag_telefono").getValue().toString();
                    sUSU_Pais = objSnapshot.child("c_usu_ah_pais").getValue().toString();
                    sUSU_Estado = objSnapshot.child("c_usu_ai_estado").getValue().toString();
                    sUSU_CodigoPostal = objSnapshot.child("c_usu_aj_codigo_postal").getValue().toString();
                    sUSU_Domicilio = objSnapshot.child("c_usu_ak_domicilio").getValue().toString();
                    sUSU_Sexo = objSnapshot.child("c_usu_al_sexo").getValue().toString();
                    sUSU_FechaNacimiento = objSnapshot.child("c_usu_am_fecha_nacimiento").getValue().toString();
                    sUSU_Reputacion = objSnapshot.child("c_usu_an_reputacion").getValue().toString();

                    sUSU_FechaCreacion = objSnapshot.child("c_usu_ao_fecha_creacion").getValue().toString();
                    sUSU_Status = objSnapshot.child("c_usu_ap_status").getValue().toString();
                    sUSU_PermisoHotel = objSnapshot.child("c_usu_aq_permiso_hotel").getValue().toString();
                    sUSU_TipoUsuario = objSnapshot.child("c_usu_ar_tipo_usuario").getValue().toString();

                    listaUsuarios1.add(
                            //"\nID: "+sUSU_ID
                            //+"\nCorreo: "+sUSU_Correo
                            //+"\nContraseña: "+sUSU_Contrasenia
                            "\nNombre(s): "+sUSU_Nombre
                                    +"\nApellidos: "+sUSU_Apellidos
                                    //+"\nRuta Imágen: "+sUSU_Ruta
                                    +"\nTeléfono: "+sUSU_Telefono
                                    +"\nPaís: "+sUSU_Pais
                                    +"\nEstado: "+sUSU_Estado
                                    +"\nCódigo Postal: "+sUSU_CodigoPostal
                                    +"\nDomicilio: "+sUSU_Domicilio
                                    +"\nSexo: "+sUSU_Sexo
                                    +"\nFecha de Nacimiento: "+sUSU_FechaNacimiento
                                    //+"\nReputación: "+sUSU_Reputacion
                                    //+"\nFecha de Creación: "+sUSU_FechaCreacion
                                    //+"\nStatus: "+sUSU_Status
                                    //+"\nPermiso Hotel: "+sUSU_PermisoHotel
                                    //+"\nTipo de Usuario: "+sUSU_TipoUsuario
                                    +"\n" );
                    listaUsuarios2.add(
                            "\nID: "+sUSU_ID
                                    +"\nCorreo: "+sUSU_Correo
                                    +"\nContraseña: "+sUSU_Contrasenia
                                    +"\nNombre(s): "+sUSU_Nombre
                                    +"\nApellidos: "+sUSU_Apellidos
                                    +"\nRuta Imágen: "+sUSU_Ruta
                                    +"\nTeléfono: "+sUSU_Telefono
                                    +"\nPaís: "+sUSU_Pais
                                    +"\nEstado: "+sUSU_Estado
                                    +"\nCódigo Postal: "+sUSU_CodigoPostal
                                    +"\nDomicilio: "+sUSU_Domicilio
                                    +"\nSexo: "+sUSU_Sexo
                                    +"\nFecha de Nacimiento: "+sUSU_FechaNacimiento
                                    +"\nReputación: "+sUSU_Reputacion
                                    +"\nFecha de Creación: "+sUSU_FechaCreacion
                                    +"\nStatus: "+sUSU_Status
                                    +"\nPermiso Hotel: "+sUSU_PermisoHotel
                                    +"\nTipo de Usuario: "+sUSU_TipoUsuario
                                    +"\n" );
                    listaFotos.add( sUSU_Ruta );
                    listaID.add( sUSU_ID );
                    listaUSUNombres.add(sUSU_Nombre + " " + sUSU_Apellidos);
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

        cargarDatosUsuarios();
        cargarDatosHoteles();
        cargarDatosHabitaciones();

    }
}