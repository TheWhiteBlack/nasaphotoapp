package com.android.nasaapitest;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;


public class VisorDetalle extends AppCompatActivity {

    ImageView foto; TextView titulo, explicacion;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visor_detalle);

        String fondo = getIntent().getExtras().getString("url");
        Log.e("Fondo"," "+fondo);
        String explanation = getIntent().getExtras().getString("explanation");
        String titletwo = getIntent().getExtras().getString("title");

        foto = findViewById(R.id.foto);
        titulo = findViewById(R.id.titletwo);
        explicacion = findViewById(R.id.explana);
        fab= findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "gabriel.alejandro.lara@gmail.com", Toast.LENGTH_SHORT).show();
            }
        });

        Picasso.get().load(fondo)
               // .resize(3800, 2160)
                //.centerCrop()
                .into((ImageView) foto);
        titulo.setText(titletwo);
        explicacion.setText(explanation);

    }
}