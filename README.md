# Proyecto de Google Maps en Android

Este proyecto utiliza la API de Google Maps para mostrar mapas en una aplicación Android. Para poder probar la aplicación en tu entorno local, necesitas configurar tu propia clave de API de Google Maps.

## Instrucciones para configurar la clave de API

### 1. Obtener una clave de API de Google Maps

Sigue estos pasos para obtener tu propia clave de API:

1. Ve al [Google Cloud Console](https://console.developers.google.com/).
2. Crea un proyecto o selecciona uno existente.
3. Habilita la API **Maps SDK for Android** en tu proyecto.
4. Crea una nueva clave de API:
    - Ve a **APIs & Services > Credentials**.
    - Haz clic en **Create credentials** y selecciona **API key**.
5. Copia la clave generada.

### 2. Configurar la clave de API en tu proyecto

1. Abre el archivo `AndroidManifest.xml` en la raíz del proyecto.
2. Busca el siguiente bloque de código:

   ```xml
   <meta-data
       android:name="com.google.android.maps.v2.API_KEY"
       android:value="YOUR_API_KEY" />

## Para la aplicación Android hecha con Kotlin y Jetpack Compose, usaremos Clean Architecture con el patrón MVVM (Model-View-ViewModel).

app/
├── di/                      # Dependency Injection
├── domain/                  # Reglas de negocio
│   ├── model/              # Modelos de dominio
│   ├── repository/         # Interfaces de repositorios
│   └── usecase/            # Casos de uso
│
├── data/                    # Capa de datos
│   ├── local/              # Fuentes de datos locales (Room, DataStore)
│   │   ├── dao/
│   │   ├── database/
│   │   └── entity/
│   ├── remote/             # Fuentes de datos remotos (API)
│   │   ├── api/
│   │   ├── dto/
│   │   ├── service/
│   │   └── mapper/
│   ├── repository/         # Implementaciones de repositorios
│   └── mapper/             # Conversores de datos
│
├── presentation/           # Capa de UI
│   ├── navigation/         # Navegación
│   ├── theme/             # Estilos y temas
│   └── screens/           # Pantallas de la app
│
└── util/                  # Utilidades y extensiones
