package com.fbfinal.miniguialugares.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.fbfinal.miniguialugares.R;
import com.fbfinal.miniguialugares.databinding.ActivityDetailBinding;
import com.fbfinal.miniguialugares.db.DbManager;
import com.fbfinal.miniguialugares.fragments.MapsFragment;
import com.fbfinal.miniguialugares.model.Lugar;

public class DetailActivity extends AppCompatActivity {

    private DbManager dbManager;
    private Lugar lugar;
    private ActivityDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(binding.detailActivity, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbManager = DbManager.getDbManager(this);

        int placeId = getIntent().getIntExtra("LUGAR_ID", -1);
        if (placeId != -1) {
            lugar = dbManager.getLugarById(placeId);
            if (lugar != null) {
                mostrarDetalles();
                configurarBotones();
                cargarMapa();
            }
        }
    }

    private void mostrarDetalles() {
        binding.tvNombreDetail.setText(lugar.getNombre());
        binding.tvDescripcionDetail.setText(lugar.getDescripcion());
        
        Glide.with(this)
                .load(lugar.getImg())
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(binding.imgDetail);
        
        actualizarEstadoFavorito();
    }

    private void configurarBotones() {
        binding.btnFavorito.setOnClickListener(v -> {
            lugar.setEsFavorita(!lugar.isEsFavorita());
            dbManager.updateLugar(lugar);
            actualizarEstadoFavorito();
            String msg = lugar.isEsFavorita() ? "Añadido a favoritos" : "Quitado de favoritos";
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        });

        binding.btnVerMapa.setOnClickListener(v -> {
            // Desliza suavemente hasta el mapa
            binding.detailActivity.smoothScrollTo(0, binding.frameLayoutMap.getTop());
        });
    }

    private void actualizarEstadoFavorito() {
        SharedPreferences prefs = this.getSharedPreferences("com.fbfinal.miniguialugares", Context.MODE_PRIVATE);
        if (lugar.isEsFavorita()) {
            prefs
                    .edit()
                    .putBoolean(String.valueOf(lugar.getId()), true)
                    .apply();

            binding.btnFavorito.setText("Quitar Favorito");
            binding.btnFavorito.setIconResource(android.R.drawable.btn_star_big_on);
        } else {
            prefs
                    .edit()
                    .putBoolean(String.valueOf(lugar.getId()), false)
                    .apply();
            binding.btnFavorito.setText("Marcar Favorito");
            binding.btnFavorito.setIconResource(android.R.drawable.btn_star_big_off);
        }
    }

    private void cargarMapa() {
        MapsFragment mapsFragment = new MapsFragment();
        Bundle args = new Bundle();
        args.putDouble("latitud", lugar.getLatitud());
        args.putDouble("longitud", lugar.getLongitud());
        args.putString("nombre", lugar.getNombre());
        mapsFragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayoutMap, mapsFragment);
        transaction.commit();
    }
}
