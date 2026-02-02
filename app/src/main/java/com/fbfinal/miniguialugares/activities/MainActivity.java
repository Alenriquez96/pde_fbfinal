package com.fbfinal.miniguialugares.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fbfinal.miniguialugares.R;
import com.fbfinal.miniguialugares.adapters.CustomAdapter;
import com.fbfinal.miniguialugares.databinding.ActivityMainBinding;
import com.fbfinal.miniguialugares.db.DbManager;
import com.fbfinal.miniguialugares.model.Lugar;
import com.fbfinal.miniguialugares.notifications.ReminderReceiver;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private final List<Lugar> listaLugares = new ArrayList<>();
    private ActivityMainBinding binding;
    private DbManager dbManager;
    private CustomAdapter adapter;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = this.getSharedPreferences("com.fbfinal.miniguialugares", Context.MODE_PRIVATE);
        prefs.edit().putBoolean("mostrandoFavoritos", false).apply();
        prefs.edit().putString("locale", "es").apply();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(binding.mainActivity, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // HAcer que se vea el toolbar
        setSupportActionBar(binding.toolbar);

        dbManager = DbManager.getDbManager(this);

        // Configuramos RecyclerView
        binding.listaLugares.setLayoutManager(new LinearLayoutManager(this));

        adapter = new CustomAdapter(this, listaLugares, new CustomAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Lugar lugar) {
                // Acción al pulsar en un elemento de la lista
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra("LUGAR_ID", lugar.getId());
                startActivity(intent);
            }
        });
        
        binding.listaLugares.setAdapter(adapter);

        binding.searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Filtramos la lista de lugares según el texto de búsqueda
                String query = s.toString().toLowerCase();
                List<Lugar> filteredList = new ArrayList<>();
                for (Lugar lugar : listaLugares) {
                    if (lugar.getNombre().toLowerCase().contains(query)) {
                        filteredList.add(lugar);
                    }
                }
                adapter.setLugares(filteredList);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Cargamos datos cada vez que la actividad vuelve al primer plano
        // Esto asegura que si cambiamos un favorito en el detalle, se refleje aquí
        cargarDatos();
    }

    private void cargarDatos() {
        binding.progressBar.setVisibility(View.VISIBLE);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Solo añadir datos si la BD está vacía
            if (dbManager.getAll().isEmpty()) {
                List<Lugar> lugares = cargarLugaresDesdeJson();
                dbManager.addLugares(lugares);
            }

            List<Lugar> items = dbManager.getAll();

            handler.post(() -> {
                listaLugares.clear();
                listaLugares.addAll(items);
                adapter.notifyDataSetChanged();
                binding.progressBar.setVisibility(View.GONE);
            });
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_language_en) {
            if (prefs.getString("locale", "es").equals("es")) {
                prefs.edit().putString("locale", "en").apply();
            } else {
                prefs.edit().putString("locale", "es").apply();
            }
            cambiarIdioma();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private String loadJSONFromAssets(String fileName) {
        try {
            InputStream is = getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            return new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private List<Lugar> cargarLugaresDesdeJson() {
        String json = loadJSONFromAssets("places.json");
        if (json == null) return new ArrayList<>();

        Gson gson = new Gson();
        Type type = new TypeToken<List<Lugar>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public void onShowFavoritos(View v) {
        // Traemos del SharedPreferences el boolean y lo actualziamos
        boolean mostrandoFavoritos = !prefs.getBoolean("mostrandoFavoritos", false);
        prefs.edit().putBoolean("mostrandoFavoritos", mostrandoFavoritos).apply();

        String msg = mostrandoFavoritos ? "Mostrando Favoritos" : "Mostrando Todos";
        Toast.makeText(this.getApplicationContext(), msg, Toast.LENGTH_LONG).show();

        if (mostrandoFavoritos) {
            List<Lugar> filteredList = new ArrayList<>();
            for (Lugar lugar : listaLugares) {
                if (lugar.isEsFavorita()) {
                    filteredList.add(lugar);
                }
            }
            adapter.setLugares(filteredList);
        } else {
            adapter.setLugares(listaLugares);
        }
    }

    /**
     * Metodo para lanzar alarma
     */
    public void programarRecordatorio(View view) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(this, ReminderReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Se dispara en 24h (una vez al día)
        long intervalo = AlarmManager.INTERVAL_FIFTEEN_MINUTES;
        long triggerTime = System.currentTimeMillis() + intervalo;

        alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                intervalo,
                pendingIntent
        );

        Toast.makeText(this, "Recordatorio programado", Toast.LENGTH_SHORT).show();
    }

    private void cambiarIdioma() {
        String idiomaActual = prefs.getString("locale", "es");
        String nuevoIdioma = idiomaActual.equals("es") ? "en" : "es";
        String msg = nuevoIdioma.equals("es") ? "Español" : "Inglés";
        Toast.makeText(this.getApplicationContext(), "Cambiando idioma a: " + msg, Toast.LENGTH_LONG).show();

        prefs.edit().putString("locale", nuevoIdioma).apply();
        setLocale(nuevoIdioma);
    }

    private void setLocale(String idioma) {
        Locale locale = new Locale(idioma);
        Locale.setDefault(locale);

        Configuration config = new Configuration(getResources().getConfiguration());
        config.setLocale(locale);

        Context context = createConfigurationContext(config);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        recreate();
    }
}
