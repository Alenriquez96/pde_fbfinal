package com.fbfinal.miniguialugares.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fbfinal.miniguialugares.R;
import com.fbfinal.miniguialugares.model.Lugar;

import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {
    private List<Lugar> lugares;
    private final Context ctx;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Lugar lugar);
    }

    public CustomAdapter(Context context, List<Lugar> objects, OnItemClickListener listener) {
        this.ctx = context;
        this.lugares = objects;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(ctx).inflate(R.layout.item_lugar, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Lugar lugar = lugares.get(position);
        holder.bind(lugar, listener);
    }

    @Override
    public int getItemCount() {
        return lugares.size();
    }

    public void setLugares(List<Lugar> nuevosLugares) {
        this.lugares = nuevosLugares;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView img;
        private final TextView nombre;
        private final TextView tipo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.imgLugar);
            nombre = itemView.findViewById(R.id.nombreLugar);
            tipo = itemView.findViewById(R.id.textTipo);
        }

        public void bind(final Lugar lugar, final OnItemClickListener listener) {
            nombre.setText(lugar.getNombre());
            tipo.setText(lugar.getTipo());

            // Usamos Glide para cargar la imagen
            Glide.with(itemView.getContext())
                    .load(lugar.getImg())
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .into(img);

            itemView.setOnClickListener(v -> listener.onItemClick(lugar));
        }
    }
}
