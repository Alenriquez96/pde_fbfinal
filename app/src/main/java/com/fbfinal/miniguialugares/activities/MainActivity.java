package com.fbfinal.miniguialugares.activities;

import static android.view.View.INVISIBLE;

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
import androidx.annotation.Nullable;
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
        prefs = this.getSharedPreferences("com.fbfinal.miniguialugares", Context.MODE_PRIVATE);

        String lang = prefs.getString("locale", "es");
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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

        binding.notif.setOnClickListener(this::programarRecordatorio);
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

        // EEsto hace falta porque el Toolbar se gestiona de manera diferente a las activities y si no no funciona el cambio de idioma
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.app_name);
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        if (!prefs.contains("mostrandoFavoritos")) {
            prefs.edit().putBoolean("mostrandoFavoritos", false).apply();
        } else {
            boolean mostrandoFavoritos = prefs.getBoolean("mostrandoFavoritos", false);

            if (mostrandoFavoritos) {
                binding.labelMostrandoFavs.setVisibility(View.VISIBLE);
            } else {
                binding.labelMostrandoFavs.setVisibility(INVISIBLE);
            }
        }
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
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Solo añadir datos si la BD está vacía
            if (dbManager.getAll().isEmpty()) {
                List<Lugar> lugares = cargarLugaresDesdeJson();
                dbManager.addLugares(lugares);
            }

            List<Lugar> items;
            boolean mostrandoFavs = prefs.getBoolean("mostrandoFavoritos", false);
            if (mostrandoFavs)  {
                items = dbManager.getFavoritos();
            } else {
                items = dbManager.getAll();
            }

            List<Lugar> finalItems = items;
                handler.post(() -> {
                listaLugares.clear();
                listaLugares.addAll(finalItems);
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
        String lang = prefs.getString("locale", "es");
        // Elegimos el archivo según el idioma guardado
        String fileName = lang.equals("es") ? "places.json" : "places-en.json";

        String json = loadJSONFromAssets(fileName);
        if (json == null) return new ArrayList<>();

        Gson gson = new Gson();
        Type type = new TypeToken<List<Lugar>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public void onShowFavoritos(View v) {
        // Traemos del SharedPreferences el boolean y lo actualziamos
        boolean mostrandoFavoritos = !prefs.getBoolean("mostrandoFavoritos", false);
        binding.labelMostrandoFavs.setVisibility(mostrandoFavoritos ? View.VISIBLE : View.INVISIBLE);
        prefs.edit().putBoolean("mostrandoFavoritos", mostrandoFavoritos).apply();

        String msg = mostrandoFavoritos ? getString(R.string.msg_mostrando_favoritos) : getString(R.string.msg_mostrando_todos);
        Toast.makeText(this.getApplicationContext(), msg, Toast.LENGTH_LONG).show();

        cargarDatos();
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
        long triggerTime = System.currentTimeMillis() + 5000;

        alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
        );

        Toast.makeText(this, getString(R.string.recordatorio_programado), Toast.LENGTH_SHORT).show();
    }

    private void cambiarIdioma() {
        String idiomaActual = prefs.getString("locale", "es");
        // Si es "es", el nuevo es "en". Si no, es "es".
        String nuevoIdioma = idiomaActual.equals("es") ? "en" : "es";

        if (nuevoIdioma.equals("en")) {
            Toast.makeText(this, "Language set to english", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Idioma cambiado a español", Toast.LENGTH_SHORT).show();
        }

        // Guardamos y aplicamos
        prefs.edit().putString("locale", nuevoIdioma).apply();
        setLocale(nuevoIdioma);
    }

    private void setLocale(String idioma) {
        Locale locale = new Locale(idioma);
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.setLocale(locale);

        // Esto asegura que los recursos (strings.xml) se recarguen
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        // Finalizamos la actividad y la volvemos a abrir para limpiar el stack
        Intent refresh = new Intent(this, MainActivity.class);
        startActivity(refresh);
        finish();
    }
}
