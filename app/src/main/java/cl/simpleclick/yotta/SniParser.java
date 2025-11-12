package cl.simpleclick.yotta;

import java.nio.ByteBuffer;

public class SniParser {

    public static String extractSni(byte[] data, int length) {
        try {
            ByteBuffer buffer = ByteBuffer.wrap(data, 0, length);

            // TLS record header: 5 bytes
            if (buffer.remaining() < 5) return null;
            byte contentType = buffer.get(); // 0x16 = Handshake
            if (contentType != 0x16) return null;

            buffer.getShort(); // Version
            int recordLength = getUnsignedShort(buffer);
            if (recordLength > buffer.remaining()) return null;

            // Handshake header: 4 bytes
            byte handshakeType = buffer.get(); // 0x01 = ClientHello
            if (handshakeType != 0x01) return null;

            int handshakeLength = getUnsignedMedium(buffer);
            if (handshakeLength > buffer.remaining()) return null;

            buffer.position(buffer.position() + 2); // client_version
            buffer.position(buffer.position() + 32); // random

            // Session ID
            int sessionIdLength = getUnsignedByte(buffer);
            buffer.position(buffer.position() + sessionIdLength);

            // Cipher Suites
            int cipherLength = getUnsignedShort(buffer);
            buffer.position(buffer.position() + cipherLength);

            // Compression Methods
            int compressionLength = getUnsignedByte(buffer);
            buffer.position(buffer.position() + compressionLength);

            // Extensions
            int extensionsLength = getUnsignedShort(buffer);
            int extensionsEnd = buffer.position() + extensionsLength;

            while (buffer.position() + 4 <= extensionsEnd) {
                int extType = getUnsignedShort(buffer);
                int extLen = getUnsignedShort(buffer);

                if (extType == 0x00) { // server_name
                    int sniListLen = getUnsignedShort(buffer);
                    int sniListEnd = buffer.position() + sniListLen;

                    while (buffer.position() + 3 <= sniListEnd) {
                        int nameType = getUnsignedByte(buffer);
                        int nameLen = getUnsignedShort(buffer);
                        if (nameType == 0x00 && buffer.remaining() >= nameLen) {
                            byte[] nameBytes = new byte[nameLen];
                            buffer.get(nameBytes);
                            return new String(nameBytes);
                        } else {
                            buffer.position(buffer.position() + nameLen);
                        }
                    }
                } else {
                    buffer.position(buffer.position() + extLen);
                }
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    private static int getUnsignedByte(ByteBuffer buffer) {
        return buffer.get() & 0xFF;
    }

    private static int getUnsignedShort(ByteBuffer buffer) {
        return buffer.getShort() & 0xFFFF;
    }

    private static int getUnsignedMedium(ByteBuffer buffer) {
        int b1 = getUnsignedByte(buffer);
        int b2 = getUnsignedByte(buffer);
        int b3 = getUnsignedByte(buffer);
        return (b1 << 16) | (b2 << 8) | b3;
    }
}