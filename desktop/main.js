// Chord Live Pro para Windows: siempre horizontal y a pantalla completa.
// ESC sale de pantalla completa; F11 vuelve a entrar.
const { app, BrowserWindow, session } = require('electron');
const path = require('path');
const fs = require('fs');

app.commandLine.appendSwitch('autoplay-policy', 'no-user-gesture-required');

function carpeta(sub) {
  const dir = path.join(app.getPath('documents'), 'ChordLivePro', sub);
  fs.mkdirSync(dir, { recursive: true });
  return dir;
}

function crearVentana() {
  const win = new BrowserWindow({
    width: 1280,
    height: 720,
    minWidth: 960,
    minHeight: 540,
    fullscreen: true,
    backgroundColor: '#101116',
    autoHideMenuBar: true,
    title: 'Chord Live Pro',
    icon: path.join(__dirname, 'icon.png'),
    webPreferences: { contextIsolation: true, backgroundThrottling: false }
  });
  win.setMenu(null);
  // Fuera de pantalla completa la ventana se mantiene horizontal (16:9)
  win.setAspectRatio(16 / 9);

  win.webContents.on('before-input-event', (event, input) => {
    if (input.type !== 'keyDown') return;
    if (input.key === 'Escape' && win.isFullScreen()) win.setFullScreen(false);
    if (input.key === 'F11') { win.setFullScreen(!win.isFullScreen()); event.preventDefault(); }
  });

  // Las exportaciones (MIDI, WAV, MP3) se guardan solas en Documentos/ChordLivePro/grabaciones
  session.defaultSession.on('will-download', (_e, item) => {
    item.setSavePath(path.join(carpeta('grabaciones'), item.getFilename()));
  });

  win.loadFile(path.join(__dirname, 'app', 'index.html'));
}

app.whenReady().then(crearVentana);
app.on('window-all-closed', () => app.quit());
