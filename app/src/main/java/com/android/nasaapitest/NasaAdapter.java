package com.android.nasaapitest;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class NasaAdapter extends RecyclerView.Adapter<NasaAdapter.MyViewHolder> {

    private ArrayList<HashMap<String, String>> mDataSet;

    private ClickListener clickListener;
    private View.OnFocusChangeListener focusChangeListener;


    public NasaAdapter(ArrayList<HashMap<String, String>> mDataSet) {
        this.mDataSet = mDataSet;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemfoto, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        HashMap<String, String> data = mDataSet.get(position);
        holder.bindData(data);
    }

    @Override
    public int getItemCount() {
        return (mDataSet != null) ? mDataSet.size() : 0;
    }

    public String getItem(int position) {
        return (mDataSet != null) ? String.valueOf(mDataSet.get(position)) : null;
    }

void setOnFocusChangeListener(View.OnFocusChangeListener focusChangeListener){
        this.focusChangeListener = focusChangeListener;
}

    void setOnItemClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView titulo;
        private TextView fecha;

        private ImageView imagen;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.titleview);
            fecha = itemView.findViewById(R.id.dateview);
            imagen = itemView.findViewById(R.id.imagelist);

            if (clickListener != null) {
                itemView.setFocusable(true);
                itemView.setOnClickListener(this);


            }
        }

        public void bindData(final HashMap<String, String> data) {

            String titulostring;
            data.entrySet();
            titulostring= data.get("title");
            titulo.setText(titulostring);

            String fechastring;
            data.entrySet();
            fechastring= data.get("date");
            fecha.setText(fechastring);

            String imagens;
            imagens=data.get("icon");
            Log.e("imag"," "+imagens);


            //Verificamos que las imagenes no esten vacias
            if (imagens != null && !imagens.equals("")) {
                Picasso.get().load(imagens).into(imagen);
            }

        }

        @Override
        public void onClick(View v) {
            if (clickListener != null) {
                clickListener.onItemClick(getAdapterPosition(), v);
            }
        }



    }

    public interface ClickListener {
        void onItemClick(int position, View v);
    }

}
