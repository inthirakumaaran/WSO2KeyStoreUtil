import com.google.gson.Gson;
import org.apache.axiom.om.util.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import javax.crypto.Cipher;

public class Encryptor {

    private static final String CIPHER_TRANSFORMATION = "RSA/ECB/OAEPwithSHA1andMGF1Padding";
    private String KEYSTORE_FILE = "/Users/kumaaran/IAM/phase3/newSupportCheck/wso2is-5.7.0" +
            "/repository/resources/security/wso2carbon.jks";
    private String KEYSTORE_TYPE = KeyStore.getDefaultType();
    private String KEYSTORE_PASSWORD = "wso2carbon";
    private String KEYSTORE_KEY_ALIAS = "snow";
    private String KEYSTORE_KEY_PASSWORD = "wso2carbon";

    public void setKeystoreFile(String keystoreFile) {

        KEYSTORE_FILE = keystoreFile;
    }

    public void setKeystoreType(String keystoreType) {

        KEYSTORE_TYPE = keystoreType;
    }

    public void setKeystorePassword(String keystorePassword) {

        KEYSTORE_PASSWORD = keystorePassword;
    }

    public void setKeystoreKeyAlias(String keystoreKeyAlias) {

        KEYSTORE_KEY_ALIAS = keystoreKeyAlias;
    }

    public void setKeystoreKeyPassword(String keystoreKeyPassword) {

        KEYSTORE_KEY_PASSWORD = keystoreKeyPassword;
    }

    private static final char[] HEX_CHARACTERS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B',
            'C', 'D', 'E', 'F'};
    private Gson gson = new Gson();



    public static void main(String[] args) throws Exception {

        Encryptor app = new Encryptor();
        app.encrypt("kumar");
    }

    public String encrypt(String data) throws Exception {

        Security.addProvider(new BouncyCastleProvider());
        // point your keystore here
        InputStream file = new FileInputStream(KEYSTORE_FILE);
        KeyStore keystore = KeyStore.getInstance(KEYSTORE_TYPE);
        keystore.load(file, KEYSTORE_PASSWORD.toCharArray());

        Certificate[] certs = keystore.getCertificateChain(KEYSTORE_KEY_ALIAS);
        Certificate certificate = certs[0];

        Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION, "BC");
        cipher.init(Cipher.ENCRYPT_MODE, certs[0].getPublicKey());
        byte[] encryptedKey = cipher.doFinal((data.getBytes()));
        encryptedKey = createSelfContainedCiphertext(encryptedKey, CIPHER_TRANSFORMATION, certificate);
        String result = Base64.encode(encryptedKey);
        System.out.println("encrypted data: "+result);
        return result;

    }

    public byte[] createSelfContainedCiphertext(byte[] originalCipher, String transformation, Certificate certificate)
            throws CertificateEncodingException, NoSuchAlgorithmException {

        Decryptor.CipherHolder cipherHolder = new Decryptor.CipherHolder();
        cipherHolder.setCipherText(Base64.encode(originalCipher));
        cipherHolder.setTransformation(transformation);
        cipherHolder.setThumbPrint(calculateThumbprint(certificate, "SHA-1"), "SHA-1");
        String cipherWithMetadataStr = gson.toJson(cipherHolder);
        return cipherWithMetadataStr.getBytes(Charset.defaultCharset());
    }

    private String calculateThumbprint(Certificate certificate, String digest)
            throws NoSuchAlgorithmException, CertificateEncodingException {

        MessageDigest messageDigest = MessageDigest.getInstance(digest);
        messageDigest.update(certificate.getEncoded());
        byte[] digestByteArray = messageDigest.digest();

        // convert digest in form of byte array to hex format
        StringBuffer strBuffer = new StringBuffer();

        for (int i = 0; i < digestByteArray.length; i++) {
            int leftNibble = (digestByteArray[i] & 0xF0) >> 4;
            int rightNibble = (digestByteArray[i] & 0x0F);
            strBuffer.append(HEX_CHARACTERS[leftNibble]).append(HEX_CHARACTERS[rightNibble]);
        }

        return strBuffer.toString();
    }
}
