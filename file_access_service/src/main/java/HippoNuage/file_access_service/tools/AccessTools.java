package HippoNuage.file_access_service.tools;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

public class AccessTools {

    /**
     * Vérifie si le flux fourni est au format gzip (en lisant les deux premiers octets).
     * Le flux sera remis à sa position initiale après la vérification.
     *
     * @param in InputStream à vérifier (sera encapsulé dans un BufferedInputStream si nécessaire)
     * @return true si le flux est au format gzip, false sinon
     * @throws IOException en cas d'erreur de lecture
     */
    public static boolean isGzipped(InputStream in) throws IOException {
        if (!in.markSupported()) {
            in = new BufferedInputStream(in);
        }
        in.mark(2);
        int magic = in.read() & 0xff | ((in.read() << 8) & 0xff00);
        in.reset();
        return magic == GZIPInputStream.GZIP_MAGIC;
    }

    /**
     * Décompresse un flux gzip et retourne le contenu décompressé sous forme de tableau d'octets.
     *
     * @param gzippedInput InputStream gzip à décompresser
     * @return byte[] contenu décompressé
     * @throws IOException en cas d'erreur de lecture ou de décompression
     */
    public static byte[] decompressGzip(InputStream gzippedInput) throws IOException {
        try (GZIPInputStream gzipIn = new GZIPInputStream(gzippedInput);
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[8192];
            int len;
            while ((len = gzipIn.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
            return out.toByteArray();
        }
    }
}