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
│   │   └── service/
│   ├── repository/         # Implementaciones de repositorios
│   └── mapper/             # Conversores de datos
│
├── presentation/           # Capa de UI
│   ├── navigation/         # Navegación
│   ├── theme/             # Estilos y temas
│   └── screens/           # Pantallas de la app
│       ├── home/
│       │   ├── HomeScreen.kt
│       │   ├── HomeViewModel.kt
│       │   └── components/    # Componentes específicos de la pantalla
│       ├── map/
│       │   ├── MapScreen.kt
│       │   ├── MapViewModel.kt
│       │   └── components/
│       └── settings/
│           ├── SettingsScreen.kt
│           ├── SettingsViewModel.kt
│           └── components/
│
└── util/                  # Utilidades y extensiones
