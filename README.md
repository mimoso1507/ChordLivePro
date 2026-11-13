# Chord Live Pro

App personal de piano con pads de acordes para Android: teclado con tira de desplazamiento, pads de acordes con filas extra, grabadora con cuenta regresiva y cuantizado, metrónomo, reverb, sustain, modos de una hilera, dos hileras y dos jugadores, SoundFonts (.sf2) propios y exportación a MIDI, WAV y MP3.

## Instalar en el teléfono

1. En el teléfono, abre **Releases** de este repositorio (iniciando sesión en GitHub) y descarga `ChordLivePro.apk` de la versión más reciente.
2. Ábrela. Si Android lo pide, permite "Instalar apps desconocidas" para el navegador o el gestor de archivos.
3. Las versiones nuevas se instalan encima de la anterior sin perder tus datos.

## Carpeta de la app

Todo se guarda en `Documentos/ChordLivePro` del almacenamiento del teléfono:

- `sf2/` SoundFonts que agregas desde Instrumento → Más.
- `instrumentos/` cada instrumento propio (`.json` con el sonido elegido y `.png` con su ícono).
- `grabaciones/` exportaciones MIDI, WAV y MP3.

## Cómo está hecho

- `www/index.html`: toda la app (HTML, CSS y JavaScript con Web Audio). También funciona abierta en un navegador.
- `android/`: proyecto Android generado con Capacitor 7.
- `.github/workflows/build-apk.yml`: GitHub compila la APK en cada cambio a `main` y la publica en Releases.
