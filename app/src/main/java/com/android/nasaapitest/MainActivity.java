package com.android.nasaapitest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

//Gabriel Lara 28/01/2022


public class MainActivity extends AppCompatActivity {

    String nasakey= "6e7JH416mAeuuvpGVDJRU4oil6wsfYEGPJenUs8a";
    String end_date="", start_date="", today="", haceocho="";  Date finalfechadias;
    ArrayList<HashMap<String, String>> formList;
    private RecyclerView listadefotos;
    LottieAnimationView figura;
    NasaAdapter adapter; EditText end, start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Inicializo los objetos a utilizar por aqui

        today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        try {
            Date diadehoy = format.parse(today);
            finalfechadias= traerultimosdias(diadehoy);
            haceocho = format.format(finalfechadias.getTime());

        } catch (ParseException e) {
            e.printStackTrace();
        }

        RecyclerView fotosdenasa = findViewById(R.id.fotosnasa);
        formList = new ArrayList<>();
        adapter = new NasaAdapter(formList);
        figura = findViewById(R.id.animationView);

        GridLayoutManager layoutausar = new GridLayoutManager(MainActivity.this,1,GridLayoutManager.VERTICAL, false);


        fotosdenasa.setLayoutManager(layoutausar);
        fotosdenasa.setAdapter(adapter);

        end= findViewById(R.id.end_date);
        start= findViewById(R.id.start_date);

        end.setVisibility(View.INVISIBLE);
        start.setVisibility(View.INVISIBLE);

        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog("end");

            }
        });

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog("start");

            }
        });

       adapter.setOnItemClickListener((position, v) -> {
                    Intent visordedetalle = new Intent(this, VisorDetalle.class);

                    String title= String.valueOf(formList.get(position).get("title"));
                    String url= String.valueOf(formList.get(position).get("icon"));
                    //String copy= String.valueOf(formList.get(position).get("copyright"));
                    String explanation= String.valueOf(formList.get(position).get("explanation"));


                    visordedetalle.putExtra("url",url);
                    visordedetalle.putExtra("explanation",explanation);
                   // visordedetalle.putExtra("copyrigth",copy);
                    visordedetalle.putExtra("title",title);

                    startActivity(visordedetalle);
       });

        getNASADatarange("alcrear");
    }

    private void showDatePickerDialog(String origen) {
        DateDialogSelector newFragment = DateDialogSelector.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                String mesfinal="";
                if (month+1<10)
                {
                    mesfinal= "0"+(month+1);
                }
                else { mesfinal=""+(month+1); }

                final String selectedDate = year + "-" + (mesfinal) + "-" + day;

               if(origen.equals("end"))
               {
                   end.setText(selectedDate);
                   end_date=selectedDate;
               }
               else
               {
                   start.setText(selectedDate);
                   start_date=selectedDate;
               }

               if(!start_date.equals("") && !end_date.equals("")){

                   SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                   Date fecha1 = null,fecha2 = null, hoy=null;
                   try {
                      fecha1 = format.parse(start_date);
                   } catch (ParseException e) {
                       e.printStackTrace();
                   }

                   try {
                       fecha2 = format.parse(end_date);
                   } catch (ParseException e) {
                       e.printStackTrace();
                   }

                   try {
                       hoy= format.parse(today);
                   } catch (ParseException e) {
                       e.printStackTrace();
                   }

                   if(fecha1.equals(fecha2)){
                       Toast.makeText(getApplicationContext(),"Las fechas no pueden ser iguales",Toast.LENGTH_LONG).show();
                   }else if(fecha1.after(hoy) || fecha2.after(hoy))
                   {
                       Toast.makeText(getApplicationContext(),"Aun no existe registro, usa fechas iguales o anteriores a hoy",Toast.LENGTH_LONG).show();

                   }
                   else if(fecha1.after(fecha2)){
                       Toast.makeText(getApplicationContext(),"Las fecha inicial no puede ser despues de la final",Toast.LENGTH_LONG).show();

                   }else if(fecha1.before(fecha2)){

                       Toast.makeText(getApplicationContext(),"La API de la NASA funciona mejor en un rango de un dia para otro ;)",Toast.LENGTH_LONG).show();

                       //ejecuto endpoint

                       formList.clear(); //Limpio la lista de la imagen del dia presente
                       getNASADatarange("selector");
                   }
               }
            }
        });

        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

public Date traerultimosdias(Date fecha){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fecha);
        calendar.add(Calendar.DAY_OF_YEAR,-8);
        return calendar.getTime();
}



    private void getNASADatarange(String origen){

        String inicio, finalizar;

        //Este metodo decide que rango de fechas esta usando el usuario
        if(origen.equals("alcrear"))
        {
            inicio= haceocho;
            finalizar = today;
        }
        else {
            inicio= start_date;
            finalizar = end_date;
        }

        JSONArray jsonArray = new JSONArray();
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, "https://api.nasa.gov/planetary/apod?api_key="+nasakey+"&start_date="+inicio+"&end_date="+finalizar, jsonArray,
                response -> {
                    try {
                        JSONArray codigo = response;
                        HashMap<String, String> m_li;

                        for(int i=0;i<codigo.length();i++) {

                            JSONObject jsonObject = codigo.getJSONObject(i);

                            String title = jsonObject.getString("title");
                            String date = jsonObject.getString("date");
                            String url = jsonObject.getString("url");
                            String explanation = jsonObject.getString("explanation");
                            //String copy= jsonObject.getString("copyright");

                            m_li = new HashMap<>();
                            m_li.put("title", title);
                            m_li.put("icon", url);
                            m_li.put("date", date);
                            m_li.put("explanation",explanation);
                            //m_li.put("copyright",copy);

                            formList.add(m_li);

                        }
                        figura.setVisibility(View.GONE);
                        end.setVisibility(View.VISIBLE);
                        start.setVisibility(View.VISIBLE);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(getApplicationContext(), "Desliza hacia abajo la tarjeta para ver mas imagenes", Toast.LENGTH_SHORT).show();



                        // adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {
            if (error instanceof TimeoutError || error instanceof NoConnectionError) {

                Log.e("Algo paso","timeout");

            } else if (error instanceof AuthFailureError) {

                Log.e("Algo paso","con el key seguro");

            } else if (error instanceof ServerError) {
                Log.e("Algo paso","el servidor no sirve, ya es constante");


            } else if (error instanceof NetworkError) {

                Log.e("Algo paso","no tienes internet asi no sirve");


            } else if (error instanceof ParseError) {

                Log.e("Algo paso","Imposible * Inserta Thanos?");

            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        VolleyQueue.getInstance(getApplicationContext()).addToRequestQueue(request);

    }


}