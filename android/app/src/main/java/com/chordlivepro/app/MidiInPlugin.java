package com.chordlivepro.app;

import android.content.Context;
import android.content.pm.PackageManager;
import android.media.midi.MidiDevice;
import android.media.midi.MidiDeviceInfo;
import android.media.midi.MidiManager;
import android.media.midi.MidiOutputPort;
import android.media.midi.MidiReceiver;
import android.os.Handler;
import android.os.Looper;

import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

import java.util.HashMap;
import java.util.Map;

/** Entrada MIDI nativa (controladores USB y los que el sistema ya tenga emparejados). */
@CapacitorPlugin(name = "MidiIn")
public class MidiInPlugin extends Plugin {
    private MidiManager manager;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Map<Integer, MidiDevice> devices = new HashMap<>();
    private final Map<Integer, String> names = new HashMap<>();

    private final MidiManager.DeviceCallback callback = new MidiManager.DeviceCallback() {
        @Override
        public void onDeviceAdded(MidiDeviceInfo info) {
            open(info);
        }

        @Override
        public void onDeviceRemoved(MidiDeviceInfo info) {
            int id = info.getId();
            MidiDevice d = devices.remove(id);
            names.remove(id);
            if (d != null) {
                try {
                    d.close();
                } catch (Exception ignored) {
                }
            }
            notifyDevices();
        }
    };

    @PluginMethod
    public void start(PluginCall call) {
        JSObject res = new JSObject();
        Context c = getContext();
        if (!c.getPackageManager().hasSystemFeature(PackageManager.FEATURE_MIDI)) {
            res.put("supported", false);
            call.resolve(res);
            return;
        }
        if (manager == null) {
            manager = (MidiManager) c.getSystemService(Context.MIDI_SERVICE);
            manager.registerDeviceCallback(callback, handler);
            for (MidiDeviceInfo info : manager.getDevices()) {
                open(info);
            }
        }
        res.put("supported", true);
        call.resolve(res);
    }

    private void open(final MidiDeviceInfo info) {
        final int id = info.getId();
        if (info.getOutputPortCount() == 0 || devices.containsKey(id)) return;
        manager.openDevice(info, device -> {
            if (device == null) return;
            MidiOutputPort port = device.openOutputPort(0);
            if (port == null) {
                try {
                    device.close();
                } catch (Exception ignored) {
                }
                return;
            }
            devices.put(id, device);
            String name = info.getProperties().getString(MidiDeviceInfo.PROPERTY_NAME);
            names.put(id, name != null ? name : "Dispositivo MIDI");
            port.connect(new MidiReceiver() {
                @Override
                public void onSend(byte[] data, int offset, int count, long timestamp) {
                    JSArray arr = new JSArray();
                    for (int i = 0; i < count; i++) arr.put(data[offset + i] & 0xFF);
                    JSObject o = new JSObject();
                    o.put("data", arr);
                    notifyListeners("midi", o);
                }
            });
            notifyDevices();
        }, handler);
    }

    private void notifyDevices() {
        JSArray arr = new JSArray();
        for (String n : names.values()) arr.put(n);
        JSObject o = new JSObject();
        o.put("names", arr);
        notifyListeners("devices", o);
    }

    @Override
    protected void handleOnDestroy() {
        if (manager != null) manager.unregisterDeviceCallback(callback);
        for (MidiDevice d : devices.values()) {
            try {
                d.close();
            } catch (Exception ignored) {
            }
        }
        devices.clear();
    }
}
