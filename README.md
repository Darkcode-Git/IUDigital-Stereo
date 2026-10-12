# IU Digital Radio (IUDigital-Stereo)

Aplicación Android nativa en **Kotlin + Jetpack Compose** orientada a la experiencia de radio universitaria por streaming.

## Propósito

IU Digital Radio presenta una interfaz de reproducción con selección de emisoras, estado de reproducción/mute, captura de foto de perfil desde cámara y retroalimentación háptica en interacciones clave.

---

## Funcionalidades comprobadas en el código (estado actual)

- Interfaz declarativa con Compose y Material 3.
- Listado local de emisoras (en memoria).
- Selección de emisora y cambio de estado visual de reproducción.
- Botones de play/pausa y mute (estado UI).
- Captura de imagen con cámara nativa (`TakePicturePreview`).
- Solicitud de permiso de cámara en tiempo de ejecución.
- Vibración/háptica al interactuar con controles principales.
- Permiso de red (`INTERNET`) declarado para streaming.

## Funcionalidades **no** comprobadas en el código actual

- Reproducción real de audio con motor multimedia (solo estado UI, no se encontró integración de ExoPlayer/MediaPlayer).
- Selección desde galería.
- Analizador FFT en tiempo real.

---

## Arquitectura aplicada (refactor conservador)

Se migró de un único archivo monolítico (`MainActivity.kt`) a una estructura tipo **MVVM por capas**, manteniendo la funcionalidad visible:

- **Modelo/Dominio**: `Station`.
- **Datos**: `StationRepository` + implementación `InMemoryStationRepository`.
- **Estado/UI State**: `RadioUiState`.
- **Presentación**: `RadioViewModel` (eventos y estado).
- **UI Compose**: `RadioRoute` + `RadioScreen` y subcomponentes.
- **Utilidades**: permisos de cámara y háptica en helpers dedicados.

### Árbol real de archivos (módulo app)

```text
app/src/main/
├── AndroidManifest.xml
└── java/co/edu/iudigital/radio/
    ├── MainActivity.kt
    ├── data/
    │   └── StationRepository.kt
    ├── model/
    │   └── Station.kt
    ├── ui/
    │   ├── RadioScreen.kt
    │   └── RadioViewModel.kt
    ├── ui/state/
    │   └── RadioUiState.kt
    └── util/
        ├── HapticUtils.kt
        └── PermissionUtils.kt
```

---

## Flujo funcional

### 1) Reproducción (estado UI)
1. La pantalla muestra emisora activa desde `RadioUiState.currentStation`.
2. `RadioViewModel.onPlayPauseClick()` alterna `isPlaying`.
3. `RadioViewModel.onMuteClick()` alterna `isMuted`.
4. `RadioViewModel.onStationSelected(index)` cambia emisora y activa `isPlaying = true`.

### 2) Permisos y cámara
1. `RadioRoute` valida permiso con `hasCameraPermission`.
2. Si falta permiso, solicita `Manifest.permission.CAMERA`.
3. Si es concedido, abre `TakePicturePreview`.
4. La imagen capturada se envía a `onPhotoCaptured` del ViewModel.

### 3) Háptica
Las acciones de play/pausa, mute y selección de emisora ejecutan `triggerHapticFeedback(context)`, con compatibilidad para APIs recientes y legacy.

### 4) Galería y FFT
No se encontró implementación activa de galería ni FFT en el código del repositorio.

---

## Tecnologías y dependencias observables en código

- Kotlin
- AndroidX Activity + Compose
- Jetpack Compose (Foundation/UI/Runtime)
- Material 3
- Activity Result API (`TakePicturePreview`, `RequestPermission`)
- APIs Android de vibración (`VibratorManager` / `Vibrator`)

> Nota: en esta instantánea del repositorio no se observaron archivos Gradle versionados; el inventario anterior se deriva de imports y código fuente.

---

## Requisitos y ejecución

1. Android Studio reciente con soporte Compose.
2. SDK Android con permisos de cámara/vibración.
3. Conectividad de red para URLs de streaming.

Pasos generales:
1. Abrir el proyecto en Android Studio.
2. Sincronizar dependencias del proyecto (si el entorno local contiene los Gradle scripts correspondientes).
3. Ejecutar en dispositivo/emulador Android.

---

## Permisos Android y red

Declarados en `AndroidManifest.xml`:

- `android.permission.CAMERA`
- `android.permission.VIBRATE`
- `android.permission.INTERNET`

Consideraciones:
- La cámara se usa para foto de perfil temporal en UI.
- `INTERNET` es necesario para endpoints de streaming HTTP.
- Las URL de emisoras están hardcodeadas en repositorio local en memoria.

---

## Análisis técnico

### Fortalezas
- UI declarativa clara con Compose.
- Refactor a capas mejora mantenibilidad y pruebas futuras.
- Separación de responsabilidades (actividad mínima, UI desacoplada, estado centralizado).

### Riesgos actuales
- No hay motor de reproducción real; posible brecha entre UI y comportamiento esperado de radio.
- URLs en memoria sin validación/estrategia de errores de red.
- Sin pruebas automatizadas versionadas en esta instantánea.

### Oportunidades de mejora (futuro)
- Integrar reproductor real (p. ej. Media3) con ciclo de vida.
- Añadir selector de galería si es requisito funcional.
- Incorporar FFT real conectado al flujo de audio.
- Agregar tests unitarios/UI y pipeline de CI para build/lint/test.

---

## Evidencias técnicas (rutas y símbolos)

| Evidencia | Archivo | Referencia aproximada |
|---|---|---|
| Activity principal y entrada Compose | `app/src/main/java/co/edu/iudigital/radio/MainActivity.kt` | `class MainActivity`, `setContent`, `RadioRoute()` |
| Modelo de emisora | `app/src/main/java/co/edu/iudigital/radio/model/Station.kt` | `data class Station` |
| Repositorio de emisoras en memoria | `app/src/main/java/co/edu/iudigital/radio/data/StationRepository.kt` | `StationRepository`, `InMemoryStationRepository.getStations()` |
| Estado de UI y emisora actual | `app/src/main/java/co/edu/iudigital/radio/ui/state/RadioUiState.kt` | `data class RadioUiState`, `currentStation` |
| Eventos de UI (play/mute/selección/foto) | `app/src/main/java/co/edu/iudigital/radio/ui/RadioViewModel.kt` | `onPlayPauseClick`, `onMuteClick`, `onStationSelected`, `onPhotoCaptured` |
| Flujo permisos/cámara + integración de UI | `app/src/main/java/co/edu/iudigital/radio/ui/RadioScreen.kt` | `RadioRoute`, launchers `RequestPermission` y `TakePicturePreview` |
| Componentes Compose desacoplados | `app/src/main/java/co/edu/iudigital/radio/ui/RadioScreen.kt` | `RadioScreen`, `ProfileCard`, `NowPlayingCard`, `StationList` |
| Utilidad de permisos | `app/src/main/java/co/edu/iudigital/radio/util/PermissionUtils.kt` | `hasCameraPermission` |
| Utilidad háptica | `app/src/main/java/co/edu/iudigital/radio/util/HapticUtils.kt` | `triggerHapticFeedback` |
| Permisos declarados | `app/src/main/AndroidManifest.xml` | `uses-permission` CAMERA/VIBRATE/INTERNET |

---

## Validaciones realizadas en esta intervención

- Revisión estructural completa del módulo `app`.
- Revisión de CI (workflow runs y logs) en GitHub Actions.
- Verificación de ausencia de infraestructura de pruebas/gradle versionada en esta copia del repositorio.

---

## Limitaciones y trabajo futuro

- Esta copia del repositorio no incluye scripts Gradle ni tests versionados; por ello no fue posible ejecutar build/lint/test localmente en esta sesión.
- Se mantuvo el alcance en refactor arquitectónico conservador, sin agregar funcionalidades no verificadas.

---

## Licencia

Este repositorio incluye archivo `LICENSE`. Revisar su contenido para términos completos de uso y distribución.
