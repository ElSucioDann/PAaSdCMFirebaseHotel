package com.example.paasdcmfirebasehotel.ui.mishabitaciones;

import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.paasdcmfirebasehotel.LoginActivity;
import com.example.paasdcmfirebasehotel.MainActivity;
import com.example.paasdcmfirebasehotel.R;
import com.example.paasdcmfirebasehotel.RegisterActivity;
import com.example.paasdcmfirebasehotel.UsuarioDatosClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.UUID;

public class MisHabitacionesFragment extends Fragment {

    private MisHabitacionesViewModel mViewModel;

    EditText objTipoNombre;

    ImageView objFotoHabitacion;

    EditText objPrecio;

    EditText objCapacidad;

    EditText objPiso;

    EditText objHabitacion;

    EditText objTotalCamas;

    Spinner objTipoBanio;
    Spinner objTipoCamas;

    RadioGroup objGrupoBalcon;
    RadioButton objBalconSi;
    RadioButton objBalconNo;

    EditText objDescripcionAdicional;

    RadioGroup objGrupoDisponibilidad;
    RadioButton objDisponibilidadSi;
    RadioButton objDisponibilidadNo;

    RadioGroup objGrupoStatus;
    RadioButton objStatusActivo;
    RadioButton objStatusInactivo;//12

    Button objRegistrarHabitacion;

    //Cadenas Habitacion
    String sHAB_ID_Habitacion="";
    String sHAB_TipoNombre="";
    String sHAB_FotoHabitacion="";
    String sHAB_Precio="";
    String sHAB_Capacidad="";
    String sHAB_Piso="";
    String sHAB_Habitacion = "";
    String sHAB_TotalCamas = "";
    String sHAB_TipoBanio = "";
    String sHAB_TipoCamas = "";
    String sHAB_Balcon = "";
    String sHAB_DescripcionAdicional = "";
    String sHAB_Disponibilidad = "";
    String sHAB_ID_Hotel = "";
    String sHAB_Razon_Social_Hotel = "";
    String sHAB_FechaCreacion = "";
    String sHAB_Status = "";//17

    //Cadenas Hotel
    String sHOT_ID_Hotel="";
    String sHOT_Razon_Social="";

    //Firebase
    FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    DatabaseReference databaseReferenceHotel;
    DatabaseReference databaseReferenceHabitaciones;
    StorageReference storageProfilePicsRef;
    StorageReference storageProfilePicsRefHotel;
    StorageReference storageProfilePicsRefHabitaciones;

    //Imagenes
    String ruta="";
    Uri imageUri;
    String myUri = "";

    //Auxiliares
    CountDownTimer mCountDownTimer;
    int aux_info=0;


    public static MisHabitacionesFragment newInstance() {
        return new MisHabitacionesFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_mis_habitaciones, container, false);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("RTDB_Users");
        databaseReferenceHotel = FirebaseDatabase.getInstance().getReference().child("RTDB_Hotels");
        databaseReferenceHabitaciones = FirebaseDatabase.getInstance().getReference().child("RTDB_Rooms");
        storageProfilePicsRef = FirebaseStorage.getInstance().getReference().child("STORAGE_Users_Profile_Pics");
        storageProfilePicsRefHotel = FirebaseStorage.getInstance().getReference().child("STORAGE_Hotels_Profile_Pics");
        storageProfilePicsRefHabitaciones = FirebaseStorage.getInstance().getReference().child("STORAGE_Rooms_Profile_Pics");

        asignar(root);

        tomarFoto();

        registrarHabitacion();

        return root;
    }

    private void tomarFoto() {
        objFotoHabitacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                camara();
            }
        });
    }

    public void camara(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity( getContext().getPackageManager() ) != null){

            File fotoArchivo = null;
            try {
                fotoArchivo = guardarImagen();
            } catch (IOException ex) {
                Log.e("Error", ex.toString());
            }
            if (fotoArchivo != null){
                Uri uri = FileProvider.getUriForFile(getContext(), getContext().getApplicationContext().getPackageName()+".fileprovider", fotoArchivo);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                imageUri = uri;
                startActivityForResult(intent, 1);
            }


        }
    }

    private File guardarImagen() throws IOException{
        String nombreFoto = "foto_";
        File directorio = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File foto = File.createTempFile(nombreFoto, ".jpg", directorio);
        ruta = foto.getAbsolutePath();

        return foto;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == getActivity().RESULT_OK){
            Bitmap imgBitmap = BitmapFactory.decodeFile(ruta);
            objFotoHabitacion.setImageBitmap(imgBitmap);
            Toast.makeText(getContext(), ruta, Toast.LENGTH_SHORT).show();

//            Bundle bundle = new Bundle();
//            bundle.putString("bun_id", "1" );
//
//            MiHotelFragment fragobj = new MiHotelFragment();
//            fragobj.setArguments(bundle);

        }

    }

    private void registrarHabitacion() {

        objRegistrarHabitacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                obtenerStrings();

                Toast.makeText(getContext(), "HOTEL" + sHOT_ID_Hotel, Toast.LENGTH_LONG).show();

                if (estaVacio()){

                    if (imageUri != null){

                        String aux_ID_Habitacion = UUID.randomUUID().toString();

                        final StorageReference fileRef = storageProfilePicsRefHabitaciones.child( mAuth.getCurrentUser().getUid() ).child( sHOT_ID_Hotel ).child( aux_ID_Habitacion+".jpg" );

                        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Toast.makeText(getContext(), "Subiendo", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "Error al Subir Imagen", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                                if ( task.isSuccessful() ){

                                    UploadTask.TaskSnapshot downloadUri = null;
                                    downloadUri = task.getResult();
                                    Task<Uri> result = downloadUri.getStorage().getDownloadUrl();
                                    result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {

                                            String aux = uri.toString();
                                            myUri = aux;

                                            sHAB_ID_Habitacion = aux_ID_Habitacion;
                                            sHAB_ID_Hotel = sHOT_ID_Hotel;
                                            sHAB_Razon_Social_Hotel = sHOT_Razon_Social;

                                            HashMap<String, Object> userMapHabitacion = new HashMap<>();
                                            userMapHabitacion.put("c_hab_aa_id_habitacion", sHAB_ID_Habitacion);

                                            userMapHabitacion.put("c_hab_ab_tipo_nombre", sHAB_TipoNombre);
                                            userMapHabitacion.put("c_hab_ac_foto_habitacion", myUri);
                                            userMapHabitacion.put("c_hab_ad_precio", sHAB_Precio);
                                            userMapHabitacion.put("c_hab_ae_capacidad", sHAB_Capacidad);
                                            userMapHabitacion.put("c_hab_af_piso", sHAB_Piso);
                                            userMapHabitacion.put("c_hab_ag_habitacion", sHAB_Habitacion);
                                            userMapHabitacion.put("c_hab_ah_total_camas", sHAB_TotalCamas);
                                            userMapHabitacion.put("c_hab_ai_tipo_banio", sHAB_TipoBanio);
                                            userMapHabitacion.put("c_hab_aj_tipo_camas", sHAB_TipoCamas);
                                            userMapHabitacion.put("c_hab_ak_balcon", sHAB_Balcon);
                                            userMapHabitacion.put("c_hab_al_descripcion_adicional", sHAB_DescripcionAdicional);

                                            userMapHabitacion.put("c_hab_am_disponibilidad", sHAB_Disponibilidad);
                                            userMapHabitacion.put("c_hab_an_ref_id_hotel", sHAB_ID_Hotel);
                                            userMapHabitacion.put("c_hab_ao_ref_razon_social", sHAB_Razon_Social_Hotel);

                                            userMapHabitacion.put("c_hab_ap_fecha_creacion", getFechaMilisegundos() * -1 );
                                            userMapHabitacion.put("c_hab_aq_status", 1);

                                            databaseReferenceHabitaciones.child( "Habitaciones1" ).child( mAuth.getCurrentUser().getUid() ).child( sHOT_ID_Hotel ).child( aux_ID_Habitacion ).setValue( userMapHabitacion ).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    Toast.makeText(getContext(), "Habitación Guardada", Toast.LENGTH_SHORT).show();
                                                    limpiar();

                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(getContext(), "Error al Subir Datos Habitación", Toast.LENGTH_SHORT).show();
                                                }
                                            });


                                        }
                                    });

                                }

                            }
                        });


                    }

                } else {
                    Toast.makeText(getContext(), "Debe llenar los campos", Toast.LENGTH_SHORT).show();
                }
                //String a = MainActivity.usuarioDatosClass.getObjNombre();
                //Toast.makeText(getContext(), a, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void limpiar(){

        sHAB_ID_Habitacion="";

        sHAB_TipoNombre="";
        objTipoNombre.setText("");

        objFotoHabitacion.setImageResource(R.drawable.ic_tomar_foto_habitacion);
        sHAB_FotoHabitacion = "";
        ruta = "";
        imageUri = null;
        myUri = "";

        sHAB_Precio = "";
        objPrecio.setText("");

        sHAB_Capacidad = "";
        objCapacidad.setText("");

        sHAB_Piso = "";
        objPiso.setText("");

        sHAB_Habitacion = "";
        objHabitacion.setText("");

        sHAB_TotalCamas = "";
        objTotalCamas.setText("");

        sHAB_TipoBanio = "";
        objTipoBanio.setSelection(0);

        sHAB_TipoCamas = "";
        objTipoCamas.setSelection(0);

        sHAB_Balcon = "";
        objGrupoBalcon.clearCheck();

        sHAB_DescripcionAdicional = "";
        objDescripcionAdicional.setText("");

        sHAB_Disponibilidad = "";
        objGrupoDisponibilidad.clearCheck();

        sHAB_ID_Hotel = "";

        sHAB_FechaCreacion = "";

        sHAB_Status = "";
        objGrupoStatus.clearCheck();

    }

    public long getFechaMilisegundos(){

        Calendar calendar = Calendar.getInstance();
        long tiempoUnix = calendar.getTimeInMillis();
        return tiempoUnix;

    }

    private void buscarMiHotel(){

        ProgressDialog TempDialog;

        int i=0;

        TempDialog = new ProgressDialog(getContext());
        TempDialog.setMessage("Please wait...");
        TempDialog.setCancelable(false);
        TempDialog.setProgress(i);
        TempDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        TempDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.GRAY));

        TempDialog.show();

        databaseReferenceHotel.child( "Hoteles1" ).child( mAuth.getCurrentUser().getUid() ).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                ArrayList<String> referenciasIDsHoteles = new ArrayList<>();
                referenciasIDsHoteles.clear();
                for(DataSnapshot ds : snapshot.getChildren()){
                    referenciasIDsHoteles.add( ds.getRef().getKey() );
                    Log.d("--------VALOR", String.valueOf( ds.getRef().getKey() ) );
                }
                //Toast.makeText(getContext(), String.valueOf( referenciasIDsHoteles.size() ) , Toast.LENGTH_SHORT).show();
                Log.d("--------VALOR SIZE", String.valueOf( referenciasIDsHoteles.size()) );

                for (int j=0; j < referenciasIDsHoteles.size(); j++){

                    databaseReferenceHotel.child( "Hoteles1" ).child( mAuth.getCurrentUser().getUid() ).child( referenciasIDsHoteles.get(j) ).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            sHOT_ID_Hotel= snapshot.child( "c_hot_aa_id_hotel" ).getValue().toString();
                            sHOT_Razon_Social= snapshot.child( "c_hot_ab_razon_social" ).getValue().toString();

                            Toast.makeText(getContext(), "Mi ID Hotel es: " + sHOT_ID_Hotel + sHOT_Razon_Social, Toast.LENGTH_LONG).show();

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

    private boolean estaVacio(){

        if (
                   //sHAB_ID_Habitacion.equals("")
                //sHAB_ID_Habitacion.equals("")
                sHAB_TipoNombre.equals("")
                //|| sHAB_FotoHabitacion.equals("")
                || imageUri==null
                || sHAB_Precio.equals("")
                || sHAB_Capacidad.equals("")
                || sHAB_Piso.equals("")
                || sHAB_Habitacion.equals("")
                || sHAB_TotalCamas.equals("")
                || sHAB_TipoBanio.equals("")
                || objTipoBanio.getSelectedItemPosition()==0
                || sHAB_TipoCamas.equals("")
                || objTipoCamas.getSelectedItemPosition()==0
                || sHAB_Balcon.equals("")
                || sHAB_DescripcionAdicional.equals("")
                || sHAB_Disponibilidad.equals("")
                //|| sHAB_ID_Hotel.equals("")
                //|| sHAB_FechaCreacion.equals("")
                || sHAB_Status.equals("")//16
        ){
          return false;
        } else {
            return true;
        }

    }

    private void obtenerStrings(){

        //ID Habitacion

        sHAB_TipoNombre = objTipoNombre.getText().toString();

        //sHAB_FotoHabitacion = ruta;

        sHAB_Precio = objPrecio.getText().toString();

        sHAB_Capacidad = objCapacidad.getText().toString();

        sHAB_Piso = objPiso.getText().toString();

        sHAB_Habitacion = objHabitacion.getText().toString();

        sHAB_TotalCamas = objTotalCamas.getText().toString();

        sHAB_TipoBanio = objTipoBanio.getSelectedItem().toString();

        sHAB_TipoCamas = objTipoCamas.getSelectedItem().toString();

        String aux_Balcon = "";
        if ( objBalconSi.isChecked() ){
            aux_Balcon = "1";
        } else if ( objBalconNo.isChecked() ){
            aux_Balcon = "0";
        }
        sHAB_Balcon = aux_Balcon;

        sHAB_DescripcionAdicional = objDescripcionAdicional.getText().toString();

        String aux_Disponibilidad = "";
        if ( objDisponibilidadSi.isChecked() ){
            aux_Disponibilidad = "1";
        } else if ( objDisponibilidadNo.isChecked() ){
            aux_Disponibilidad = "0";
        }
        sHAB_Disponibilidad = aux_Disponibilidad;

        //ID Hotel
        //Fecha Creacion

        String aux_Status = "";
        if ( objStatusActivo.isChecked() ){
            aux_Status = "1";
        } else if ( objStatusInactivo.isChecked() ){
            aux_Status = "0";
        }
        sHAB_Status = aux_Status;

    }

    private void asignar(View root) {

         objTipoNombre = root.findViewById(R.id.idMisHabitacionesEditTextTipoNombre);

         objFotoHabitacion = root.findViewById(R.id.idMisHabitacionesImageViewFotoHabitacion);

         objPrecio = root.findViewById(R.id.idMisHabitacionesEditTextPrecio);

         objCapacidad = root.findViewById(R.id.idMisHabitacionesEditTextCapacidad);

         objPiso = root.findViewById(R.id.idMisHabitacionesEditTextPiso);

         objHabitacion = root.findViewById(R.id.idMisHabitacionesEditTextHabitacion);

         objTotalCamas = root.findViewById(R.id.idMisHabitacionesEditTextTotalCamas);

         objTipoBanio = root.findViewById(R.id.idMisHabitacionesSpinnerTipoBanio);
        ArrayAdapter<CharSequence> adapterBanio =
                ArrayAdapter.createFromResource(getContext(), R.array.tipoBanio, android.R.layout.simple_spinner_item);
        objTipoBanio.setAdapter(adapterBanio);

         objTipoCamas = root.findViewById(R.id.idMisHabitacionesSpinnerTipoCamas);
        ArrayAdapter<CharSequence> adapterCamas =
                ArrayAdapter.createFromResource(getContext(), R.array.tipoCama, android.R.layout.simple_spinner_item);
        objTipoCamas.setAdapter(adapterCamas);

         objGrupoBalcon = root.findViewById(R.id.idMisHabitacionesRadioGroupBalcon);
         objBalconSi = root.findViewById(R.id.idMisHabitacionesRadioButtonBalconSi);
         objBalconNo = root.findViewById(R.id.idMisHabitacionesRadioButtonBalconNo);

         objDescripcionAdicional = root.findViewById(R.id.idMisHabitacionesEditTextDescripcionAdicional);

         objGrupoDisponibilidad = root.findViewById(R.id.idMisHabitacionesRadioGroupDisponibilidad);
         objDisponibilidadSi = root.findViewById(R.id.idMisHabitacionesRadioButtonDisponibilidadSi);
         objDisponibilidadNo = root.findViewById(R.id.idMisHabitacionesRadioButtonDisponibilidadNo);

         objGrupoStatus = root.findViewById(R.id.idMisHabitacionesRadioGroupStatus);
         objStatusActivo = root.findViewById(R.id.idMisHabitacionesRadioButtonStatusActivo);
         objStatusInactivo = root.findViewById(R.id.idMisHabitacionesRadioButtonStatusInactivo);

         objRegistrarHabitacion = root.findViewById(R.id.idMisHabitacionesButtonRegistrarHabitacion);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MisHabitacionesViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null){
            startActivity(new Intent(getContext(), LoginActivity.class));
        } else {
            if (aux_info==0){
                buscarMiHotel();
                aux_info=1;
            }
        }

    }
}