Proyecto Final Programación dirigida por
Eventos: MiniGuía de Lugares
1. Contexto
   Vas a desarrollar una aplicación Android sencilla en Android Studio usando Java. La app
   permitirá:
   • Mantener una lista de lugares (p. ej. biblioteca, cafetería, gimnasio, museo, parque…).
   • Ver el detalle de cada lugar.
   • Guardar favoritos y configurar preferencias del usuario.
   • Mostrar la ubicación en un mapa y, si el usuario lo permite, su ubicación actual.
   • Ejecutar una tarea en segundo plano (simulando “carga de datos”).
   • Aplicar buenas prácticas de accesibilidad, localización y rendimiento.
2. Requisitos de entrega
   Entrega en un repositorio (Git) o en un ZIP con:
   • Proyecto Android Studio completo (incluyendo .apk)
   • Documento breve README.md con: cómo ejecutar, qué hace, capturas, y decisiones
   importantes.
3. Historia de usuario principal
   Como usuario, quiero explorar una lista de lugares, verlos en detalle y en un mapa, y
   marcar favoritos para encontrarlos rápido más tarde.
4. Funcionalidades
   4.1 Pantalla principal: listado de lugares
   • Pantalla con un RecyclerView mostrando una lista de lugares.
   • Cada elemento debe mostrar al menos: nombre, tipo y un icono o imagen pequeña.
   • Al pulsar un elemento, se abre la pantalla de detalle.
   Datos mínimos por lugar (puedes ampliarlos):
   • id (entero)
   • name (texto)
   • type (texto)
   • description (texto)
   • lat y lng (double)
   • isFavorite (boolean)
   4.2 Navegación: Activities + Intents
   • Debe haber al menos 2 Activities:
   • MainActivity (lista)
   • DetailActivity (detalle)
   • La navegación se hará mediante Intent explícito, pasando el id del lugar.
   4.3 Detalle del lugar: UI clara y accesible
   La pantalla de detalle debe incluir:
   • Nombre y descripción.
   • Botón “Marcar/Desmarcar favorito”.
   • Botón “Ver en mapa” (abre una pantalla o fragmento con mapa).
   Accesibilidad mínima:
   • Todas las imágenes relevantes deben tener contentDescription.
   • Los botones deben tener texto claro.
   • Tamaño táctil razonable (sin botones minúsculos).
   4.4 Fragmentos: Mapa como Fragment
   • Implementa un Fragment dedicado al mapa (por ejemplo MapFragment).
   • Ese fragment debe mostrar:
   • Un marcador del lugar seleccionado.
   • (Si hay permiso) un indicador de ubicación actual.
   • El fragment se podrá usar en una MapActivity o dentro de la pantalla de detalle (como tú
   prefieras).
   Nota: si eliges MapActivity, el botón “Ver en mapa” abre esa actividad.
   4.5 Almacenamiento local
   Debes usar dos formas de persistencia:
   A) Room (obligatorio)
   • Guarda la lista de lugares en una base de datos local con Room.
   • La tabla debe permitir marcar favoritos.
   B) SharedPreferences (obligatorio)
   • Guarda al menos 2 preferencias del usuario, por ejemplo:
   • “Mostrar solo favoritos” (boolean)
   • “Último tipo de filtro seleccionado” (String)
   • “Acepta notificaciones” (boolean)
   4.6 Trabajo en segundo plano (simple)
   Implementa una tarea en segundo plano que simule “cargar datos” sin bloquear la UI:
   • Opción A: leer un JSON desde assets/places.json y convertirlo a lista.
   • Opción B: generar datos “mock” con un retardo (p. ej. 1 segundo) para simular una
   descarga.
   Requisito:
   • Mientras carga, muestra un indicador de progreso (ProgressBar o texto).
   • Al finalizar, rellena la base de datos Room (si está vacía) y refresca el RecyclerView.
   4.7 Notificación programada (sencilla)
   • Crea una notificación que se muestre 1 vez al día (o al pulsar un botón “Programar
   recordatorio”).
   • El contenido puede ser: “Revisa tus lugares favoritos hoy”.
   • Al tocar la notificación, debe abrir la app en la pantalla principal.
   4.8 Localización + formato regional
   • La app debe estar al menos en 2 idiomas: español y otro a tu elección.
   • Todo el texto visible debe venir de strings.xml.
   • Muestra una fecha u hora en alguna pantalla (por ejemplo “Última actualización: …”)
   usando formato del dispositivo.
   4.9 Rendimiento: mini-evidencia con Profiler
   No se pide optimización avanzada, pero sí:
   • Ejecuta la app con Android Profiler y anota en el README:
   • un problema o riesgo detectado (aunque sea “cuidado con imágenes grandes”)
   • una mejora aplicada (por ejemplo: usar imágenes más pequeñas, evitar recrear listas,
   etc.)
5. Restricciones técnicas
   • Lenguaje: Java.
   • UI: XML + Views.
   • Arquitectura: a tu elección.
   • Librerías:
   • Room
   • Google Maps (si no puedes usar API Key por limitación, crea una alternativa:
   mostrar lat/lng y un botón que abra Google Maps con Intent implícito).
6. Sugerencia de estructura (orientativa)
   • data/
   • Place.java (modelo)
   • PlaceEntity.java (Room)
   • PlaceDao.java
   • AppDatabase.java
   • PlaceRepository.java (opcional)
   • ui/
   • MainActivity.java
   • DetailActivity.java
   • MapActivity.java (si aplica)
   • MapFragment.java
   • PlaceAdapter.java
   • utils/
   • PrefsManager.java (SharedPreferences)
   • JsonLoader.java (carga desde assets)
7. Casos de uso mínimos (para validar)
1. La app abre y muestra listado (sin quedarse congelada durante la carga).
2. Se puede entrar al detalle de un lugar.
3. Se puede marcar/desmarcar favorito y queda guardado al cerrar y abrir.
4. El filtro “solo favoritos” funciona.
5. El mapa muestra el marcador del lugar seleccionado.
6. La app funciona en ambos idiomas.
7. Se dispara la notificación programada.
8. Rúbrica de evaluación (0–10 puntos)
   Criterio Puntos Qué se espera
   Funcionalidad base (lista + detalle +
   navegación) 2.0 RecyclerView correcto, intents, pantalla de
   detalle usable
   Persistencia (Room) 2.0 CRUD mínimo: insertar inicial, actualizar
   favorito, leer lista
   Preferencias (SharedPreferences) 1.0 Al menos 2 preferencias que afecten a la UI
   Segundo plano + UX de carga 1.5 No bloquear UI, mostrar progreso, refresco
   correcto
   Mapa + localización (o alternativa con
   Intent a Maps) 1.5 Marcador/lat-lng correcto, permisos
   gestionados con cuidado
   Localización + accesibilidad 1.0 2 idiomas y buenas prácticas básicas de
   accesibilidad
   Notificación programada 0.5 Se programa y abre la app al pulsarla
   Rendimiento (evidencia en README) 0.5 Captura/nota del profiler + pequeña mejora
   aplicada
   Total: 10 puntos
9. Extensiones opcionales (para subir nota o mejorar)
   • Búsqueda por nombre (SearchView) en la lista.
   • Ordenar por distancia a la ubicación actual.
   • Añadir una pantalla “Ajustes”.
   • Añadir un widget simple mostrando el número de favoritos.
10. Consejos
    • Empieza por la UI (lista → detalle) con datos en memoria.
    • Después añade Room y migra los datos.
    • Luego añade SharedPreferences para el filtro.
    • Por último mapa, permisos y notificación.