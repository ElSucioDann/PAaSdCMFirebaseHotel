package com.example.paasdcmfirebasehotel.ui.editarmiperfil;

import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;

import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.paasdcmfirebasehotel.R;
import com.example.paasdcmfirebasehotel.RegisterActivity;
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
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class EditarMiPerfilFragment extends Fragment {

    private EditarMiPerfilViewModel mViewModel;

    EditText objNombre;
    EditText objApellidos;

    ImageView objFotoPerfil;

    EditText objTelefono;
    EditText objPais;
    Spinner objEstado;
    EditText objCodigoPostal;
    EditText objDomicilio;

    RadioGroup objGrupoGenero;
    RadioButton objMasculino;
    RadioButton objFemenino;

    EditText objFechaNacimiento;

    Button objGuardarCambios;

    //Firebase
    FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    DatabaseReference databaseReferenceHotel;
    StorageReference storageProfilePicsRef;

    //Imagenes
    String ruta="";
    Uri imageUri;
    String myUri = "";

    //Cadenas
    String sNombre=""
            , sApellidos=""
            , sRuta=""
            , sTelefono=""
            , sPais=""
            , sEstado=""
            , sCodigoPostal=""
            , sDomicilio=""
            , sSexo=""
            , sFechaNacimiento="";
    int aux_permiso_hotel=0;

    //Fecha DatePickerDialog
    DatePickerDialog objPicker;

    //Auxiliares
    CountDownTimer mCountDownTimer;
    int aux_info=0;

    public static EditarMiPerfilFragment newInstance() {
        return new EditarMiPerfilFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_editar_mi_perfil, container, false);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("RTDB_Users");
        databaseReferenceHotel = FirebaseDatabase.getInstance().getReference().child("RTDB_Hotels");
        storageProfilePicsRef = FirebaseStorage.getInstance().getReference().child("STORAGE_Users_Profile_Pics");

        asignar( root );

        fecha();

        tomarFoto();

        guardarCambios();

        return root;
    }

    private boolean estaVacio(){

        int aux_EstadoPosicion = objEstado.getSelectedItemPosition();

        if (
                        sNombre.equals("")
                        || sApellidos.equals("")
                        //|| sRuta.equals("")
                        || sTelefono.equals("")
                        || sPais.equals("")
                        || sEstado.equals("")
                        || aux_EstadoPosicion==0
                        || sCodigoPostal.equals("")
                        || sDomicilio.equals("")
                        || sSexo.equals("")
                        || sFechaNacimiento.equals("")
        ) {
            return false;
        } else {
            return true;
        }
    }

    private void obtenerStrings() {

        sNombre = objNombre.getText().toString();
        sApellidos = objApellidos.getText().toString();

        sRuta = ruta; //objFotoPerfil;

        sTelefono = objTelefono.getText().toString();
        sPais = objPais.getText().toString();
        sEstado = objEstado.getSelectedItem().toString();
        sCodigoPostal = objCodigoPostal.getText().toString();
        sDomicilio = objDomicilio.getText().toString();

        String aux_Sexo = "";
        if ( objMasculino.isChecked() ){
            aux_Sexo = "Masculino";
        } else if ( objFemenino.isChecked() ){
            aux_Sexo = "Femenino";
        } else {
            aux_Sexo = "";
        }
        sSexo = aux_Sexo;

        sFechaNacimiento = objFechaNacimiento.getText().toString();

    }

    private void guardarCambios(){

        objGuardarCambios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                obtenerStrings();

                if (estaVacio()){

                    if (imageUri != null) { //Guardar Con Foto

                        final StorageReference fileRef = storageProfilePicsRef.child( mAuth.getCurrentUser().getUid()+".jpg");

                        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "Falló al Subir Imagen", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                                if (task.isSuccessful()){

                                    UploadTask.TaskSnapshot downloadUri = null;
                                    downloadUri = task.getResult();
                                    Task<Uri> result = downloadUri.getStorage().getDownloadUrl();
                                    result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {

                                            String aux = uri.toString();

                                            myUri = aux;

                                            HashMap<String, Object> userMapUsuario = new HashMap<>();

                                            userMapUsuario.put("c_usu_ad_nombre", sNombre);
                                            userMapUsuario.put("c_usu_ae_apellidos", sApellidos);
                                            userMapUsuario.put("c_usu_af_ruta_img", myUri);
                                            userMapUsuario.put("c_usu_ag_telefono", sTelefono);
                                            userMapUsuario.put("c_usu_ah_pais", sPais);
                                            userMapUsuario.put("c_usu_ai_estado", sEstado);
                                            userMapUsuario.put("c_usu_aj_codigo_postal", sCodigoPostal);
                                            userMapUsuario.put("c_usu_ak_domicilio", sDomicilio);
                                            userMapUsuario.put("c_usu_al_sexo", sSexo);
                                            userMapUsuario.put("c_usu_am_fecha_nacimiento", sFechaNacimiento);

                                            databaseReference.child( "Usuarios1" ).child( mAuth.getCurrentUser().getUid() ).updateChildren( userMapUsuario ).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {

                                                }
                                            }).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    if (task.isSuccessful()){

                                                        Toast.makeText(getContext(), "Guardado DI", Toast.LENGTH_SHORT).show();
                                                        cargarMisDatos();

                                                    }

                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(getContext(), "Falló al Actualizar los Datos", Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                        }
                                    });

                                }

                            }
                        });

                    } else { //Guardar SIN Foto

                        HashMap<String, Object> userMapUsuario = new HashMap<>();

                        userMapUsuario.put("c_usu_ad_nombre", sNombre);
                        userMapUsuario.put("c_usu_ae_apellidos", sApellidos);
                        //userMapUsuario.put("c_usu_af_ruta_img", myUri);
                        userMapUsuario.put("c_usu_ag_telefono", sTelefono);
                        userMapUsuario.put("c_usu_ah_pais", sPais);
                        userMapUsuario.put("c_usu_ai_estado", sEstado);
                        userMapUsuario.put("c_usu_aj_codigo_postal", sCodigoPostal);
                        userMapUsuario.put("c_usu_ak_domicilio", sDomicilio);
                        userMapUsuario.put("c_usu_al_sexo", sSexo);
                        userMapUsuario.put("c_usu_am_fecha_nacimiento", sFechaNacimiento);

                        databaseReference.child( "Usuarios1" ).child( mAuth.getCurrentUser().getUid() ).updateChildren( userMapUsuario ).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                            }
                        }).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()){

                                    Toast.makeText(getContext(), "Guardado D", Toast.LENGTH_SHORT).show();
                                    cargarMisDatos();

                                }

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "Falló al Actualizar los Datos", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }

                } else {
                    Toast.makeText(getContext(), "Debe llenar los campos", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void cargarMisDatos(){

        ProgressDialog TempDialog;

        int i=0;

        TempDialog = new ProgressDialog(getContext());
        TempDialog.setMessage("Please wait...");
        TempDialog.setCancelable(false);
        TempDialog.setProgress(i);
        TempDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        TempDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.GRAY));

        TempDialog.show();

        databaseReference.child( "Usuarios1" ).child( mAuth.getCurrentUser().getUid() ).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //Toast.makeText(getContext(), String.valueOf(snapshot.getChildrenCount()) + snapshot.getChildren() + snapshot.getKey(), Toast.LENGTH_SHORT).show();

                if (snapshot.exists()) {

                    sNombre = snapshot.child("c_usu_ad_nombre").getValue().toString();
                    sApellidos = snapshot.child("c_usu_ae_apellidos").getValue().toString();
                    sRuta = snapshot.child("c_usu_af_ruta_img").getValue().toString();
                    sTelefono = snapshot.child("c_usu_ag_telefono").getValue().toString();
                    sPais = snapshot.child("c_usu_ah_pais").getValue().toString();
                    sEstado = snapshot.child("c_usu_ai_estado").getValue().toString();
                    sCodigoPostal = snapshot.child("c_usu_aj_codigo_postal").getValue().toString();
                    sDomicilio = snapshot.child("c_usu_ak_domicilio").getValue().toString();
                    sSexo = snapshot.child("c_usu_al_sexo").getValue().toString();
                    sFechaNacimiento = snapshot.child("c_usu_am_fecha_nacimiento").getValue().toString();

                    objNombre.setText(sNombre);
                    objApellidos.setText(sApellidos);

                    if (!(sRuta.equals("") || sRuta.isEmpty())) {
                        Picasso.get().load(sRuta).into(objFotoPerfil);
                    } else {
                        objFotoPerfil.setImageResource(R.drawable.ic_tomar_foto);
                    }

                    objTelefono.setText(sTelefono);
                    objPais.setText(sPais);

                    objEstado.setSelection(obtenerPosition(objEstado, sEstado));

                    objCodigoPostal.setText(sCodigoPostal);
                    objDomicilio.setText(sDomicilio);

                    if (sSexo.equals("Masculino")){
                        objMasculino.setChecked(true);
                        objFemenino.setChecked(false);
                    } else if ( sSexo.equals("Femenino") ){
                        objFemenino.setChecked(true);
                        objMasculino.setChecked(false);
                    }

                    objFechaNacimiento.setText(sFechaNacimiento);



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

    public static int obtenerPosition(Spinner sp, String item){
        int position = 0;
        for (int i = 0; i < sp.getCount(); i++){
            if (sp.getItemAtPosition(i).toString().equalsIgnoreCase(item)){
                position = i;
            }
        }
        return  position;
    }

    public void tomarFoto(){
        objFotoPerfil.setOnClickListener(new View.OnClickListener() {
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
            objFotoPerfil.setImageBitmap(imgBitmap);
            Toast.makeText(getContext(), ruta, Toast.LENGTH_SHORT).show();
        }

    }

    public void fecha(){
        objFechaNacimiento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);

                objPicker = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                        objFechaNacimiento.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                    }
                }, year, month, day);
                objPicker.show();
            }
        });
    }

    private void asignar(View root){

        objNombre = root.findViewById(R.id.idEditarMiPerfilEditTextNombre);
        objApellidos = root.findViewById(R.id.idEditarMiPerfilEditTextApellidos);

        objFotoPerfil = root.findViewById(R.id.idEditarMiPerfilImageViewFotoPerfil);

        objTelefono = root.findViewById(R.id.idEditarMiPerfilEditTextTelefono);
        objPais = root.findViewById(R.id.idEditarMiPerfilEditTextPais);

        objEstado = root.findViewById(R.id.idEditarMiPerfilSpinnerEstado);
        ArrayAdapter<CharSequence> adapter =
                ArrayAdapter.createFromResource(getContext(), R.array.estado, android.R.layout.simple_spinner_item);
        objEstado.setAdapter(adapter);

        objCodigoPostal = root.findViewById(R.id.idEditarMiPerfilEditTextCodigoPostal);
        objDomicilio = root.findViewById(R.id.idEditarMiPerfilEditTextDomicilio);

        objGrupoGenero = root.findViewById(R.id.idEditarMiPerfilRadioGroupGenero);
        objMasculino = root.findViewById(R.id.idEditarMiPerfilRadioButtonMasculino);
        objFemenino = root.findViewById(R.id.idEditarMiPerfilRadioButtonFemenino);

        objFechaNacimiento = root.findViewById(R.id.idEditarMiPerfilEdiTextFechaNacimiento);

        objGuardarCambios = root.findViewById(R.id.idEditarMiPerfilButtonRegistrar);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(EditarMiPerfilViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onStart() {
        super.onStart();


        if (aux_info==0){
            cargarMisDatos();
            aux_info=1;
        }


    }
}