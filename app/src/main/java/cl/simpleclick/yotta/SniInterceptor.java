package cl.simpleclick.yotta;

import android.annotation.SuppressLint;
import android.net.VpnService;
import android.os.ParcelFileDescriptor;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

@SuppressLint("VpnServicePolicy")
public class SniInterceptor extends VpnService {
    @Override
    public void onCreate() {
        super.onCreate();
        // Configura la VPN en modo lectura
        Builder builder = new Builder();
        builder.addAddress("10.0.0.2", 32);
        builder.addRoute("0.0.0.0", 0);
        ParcelFileDescriptor vpnInterface = builder.establish();

        new Thread(() -> {
            assert vpnInterface != null;
            interceptTraffic(vpnInterface);
        }).start();
    }

    private void interceptTraffic(ParcelFileDescriptor vpnInterface) {
        FileInputStream in = new FileInputStream(vpnInterface.getFileDescriptor());
        ByteBuffer buffer = ByteBuffer.allocate(32767);

        while (true) {
            try {
                int length = in.read(buffer.array());
                if (length > 0) {
                    String sni = SniParser.extractSni(buffer.array(), length);
                    if (sni != null) {
                        SniSdk.getInstance().onSniCaptured(sni);
                    }
                }
            } catch (IOException e) {
                break;
            }
        }
    }
}