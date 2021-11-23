package com.example.paasdcmfirebasehotel;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.paasdcmfirebasehotel.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ktx.Firebase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    public static UsuarioDatosClass usuarioDatosClass = new UsuarioDatosClass();

    NavController navController;

    //Firebase
    FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    StorageReference storageProfilePicsRef;

    //Auxiliares
    int aux_tipo;
    Menu nav_menu;
    CountDownTimer mCountDownTimer;
    int aux_informacion=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("RTDB_Users");
        storageProfilePicsRef = FirebaseStorage.getInstance().getReference().child("STORAGE_Users_Profile_Pics");

        //obtenerInformacion();

        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_reservacion, R.id.nav_listar_mis_reservaciones, R.id.nav_listar, R.id.nav_mi_hotel, R.id.nav_mis_habitaciones, R.id.nav_listar_mis_habitaciones,
                R.id.nav_listar_mis_reservaciones_usuario, R.id.nav_editar_mi_perfil, R.id.nav_editar_eliminar_reservaciones
                ,R.id.nav_editar_eliminar_habitaciones)
                .setOpenableLayout(drawer)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        nav_menu = navigationView.getMenu();
        navigationView.setItemIconTintList(null);

        if (aux_tipo==1){//Hoteles
            nav_menu.findItem(R.id.nav_listar).setVisible(true);
        }

        if (aux_tipo==0){//Usuario standar
            nav_menu.findItem(R.id.nav_listar).setVisible(false);
        }

        //Toast.makeText(MainActivity.this, String.valueOf(aux_tipo), Toast.LENGTH_LONG).show();


        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {

                int menuId = destination.getId();

                switch (menuId){

                    case R.id.nav_home:
                        //Toast.makeText(MainActivity.this, "Esta en HOME", Toast.LENGTH_SHORT).show();
                        //navController.navigate(R.id.nav_eliminar);
                        break;

                    case R.id.nav_reservacion:
                        //Toast.makeText(MainActivity.this, "Esta en RESERVACION", Toast.LENGTH_SHORT).show();
                        //navController.navigate(R.id.nav_eliminar);
                        break;

                    case R.id.nav_mi_hotel:
                        //Toast.makeText(MainActivity.this, "Esta en HOTEL", Toast.LENGTH_SHORT).show();
                        //navController.navigate(R.id.nav_eliminar);
                        break;

                    case R.id.nav_mis_habitaciones:
                        //Toast.makeText(MainActivity.this, "Esta en HABITACIONES", Toast.LENGTH_SHORT).show();
                        //navController.navigate(R.id.nav_eliminar);
                        break;

                    case R.id.nav_listar:
                        //Toast.makeText(MainActivity.this, "Esta en LISTAR", Toast.LENGTH_SHORT).show();
                        //navController.navigate(R.id.nav_eliminar);
                        break;


                }

            }
        });


    }

    @Override
    public void invalidateOptionsMenu() {
        super.invalidateOptionsMenu();

//        MenuItem item = menu1.findItem(R.id.nav_listar);
//        item.setVisible(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
//
//        //getMenuInflater().inflate(R.menu.activity_main_drawer, menu);
//
//        //menu.getItem(R.id.nav_listar).setVisible(false);
//
////        MenuItem item = menu.findItem(R.id.nav_listar);
////        if (item != null){
////            item.setVisible(true);
////        }
////
////        item.setVisible(true);
//
//        menu1 = menu;

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){

            case R.id.action_settings:

                Toast.makeText(MainActivity.this, "Hasta Pronto", Toast.LENGTH_SHORT).show();
                mAuth.signOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));

                break;

            case R.id.action_editar_mi_perfil:

                navController.navigate(R.id.nav_editar_mi_perfil);

                break;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);

        boolean a = NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();

        String str = String.valueOf(a);
        //Toast.makeText(MainActivity.this, str, Toast.LENGTH_SHORT).show();

        return a;

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null){
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        } else {
            if (aux_informacion==0){
                obtenerInformacion();
                aux_informacion=1;
            }
        }

    }

    private void obtenerInformacion() {

        ProgressDialog TempDialog;

        int i=0;

        TempDialog = new ProgressDialog(MainActivity.this);
        TempDialog.setMessage("Please wait...");
        TempDialog.setCancelable(false);
        TempDialog.setProgress(i);
        TempDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        TempDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.GRAY));

        TempDialog.show();

        databaseReference.child("Usuarios1").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {

                    String sNombre = snapshot.child("c_usu_ad_nombre").getValue().toString();
                    String sTipoUsuario = snapshot.child("c_usu_ar_tipo_usuario").getValue().toString();

                    usuarioDatosClass.setObjNombre(sNombre);

                    if (sTipoUsuario.equals("Hoteles")) {

                        aux_tipo = 1;

                        //Toast.makeText(MainActivity.this, aux_tipo + sNombre + sTipoUsuario, Toast.LENGTH_SHORT).show();

                        //nav_menu.findItem(R.id.nav_listar).setVisible(true);//finish();//startActivity( getIntent() );

                        nav_menu.findItem(R.id.nav_listar).setVisible(true);

                    } else if (sTipoUsuario.equals("Reservaciones")) {

                        aux_tipo = 0;

                        //Toast.makeText(MainActivity.this, aux_tipo + sNombre + sTipoUsuario, Toast.LENGTH_SHORT).show();

                        //nav_menu.findItem(R.id.nav_listar).setVisible(false);

                        nav_menu.findItem(R.id.nav_listar).setVisible(false);

                    }

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

//        databaseReference.child("Usuarios1").child( mAuth.getCurrentUser().getUid() ).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                if (snapshot.exists()){
//
//                    String sNombre = snapshot.child("c_usu_ad_nombre").getValue().toString();
//                    String sTipoUsuario = snapshot.child("c_usu_ar_tipo_usuario").getValue().toString();
//
//                    if ( sTipoUsuario.equals("Hoteles") ){
//
//                        aux_tipo=1;
//
//                        //Toast.makeText(MainActivity.this, aux_tipo + sNombre + sTipoUsuario, Toast.LENGTH_SHORT).show();
//
//                        //nav_menu.findItem(R.id.nav_listar).setVisible(true);
//
//                    } else if ( sTipoUsuario.equals("Reservaciones") ){
//
//                        aux_tipo=0;
//
//                        //Toast.makeText(MainActivity.this, aux_tipo + sNombre + sTipoUsuario, Toast.LENGTH_SHORT).show();
//
//                        //nav_menu.findItem(R.id.nav_listar).setVisible(false);
//
//                    }
//
//
//
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//
//        });

    }


    @Override
    protected void onResume() {
        super.onResume();

    }
}