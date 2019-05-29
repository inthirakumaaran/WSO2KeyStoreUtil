import com.google.gson.Gson;
import org.apache.axiom.om.util.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import javax.crypto.Cipher;

;

public class Decryptor {

    private static final String CIPHER_TRANSFORMATION = "RSA/ECB/OAEPwithSHA1andMGF1Padding";
    private String KEYSTORE_FILE = "/Users/kumaaran/IAM/phase3/newSupportCheck/wso2is-5.7.0" +
            "/repository/resources/security/wso2carbon.jks";
    private String KEYSTORE_TYPE = KeyStore.getDefaultType();
    private String KEYSTORE_PASSWORD = "wso2carbon";
    private String KEYSTORE_KEY_ALIAS = "wso2carbon";
    private String KEYSTORE_KEY_PASSWORD = "wso2carbon";
    private Gson gson = new Gson();

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

    public static void main(String[] args) throws Exception {

        Decryptor app = new Decryptor();
        String password =
                "eyJjIjoiTWJQYnpXQUlvSlVwV1RvNEs4eXYvRk5jaUZGblFZb3RYaDgwbWF0S2IzWmo1YUcwSGVXdkJKQzJ3VWpiemhiMm5xT2N5OGYrVmJQeWxZMW5FMnZ6MUppSDdsYWhET1ljTUR6MU1vQjdNSmV1MFpNbkpFVEE5OURuWUNWeURwWlpmVDZxL2NTbTBVSml2Nlg0cFZLaWdWN3B3dWlGU09CYUtxZllEc0R5VFBqTklndWdLZWpVZWpLYWdPeWVNUWZ3bnphRGEyVVFiWGdGMWJKT2NWbTlYbUgxam9VbStmUTVhNENZaGsyVW9UMVV5cnhHTW5mRXFrdEl5RmQ1NGRUUG1XelJCZ1pBdkFXenB2VExrVzdiSlBNYVJqMWxvZU00NUpPNWRWZ0tBQ3Noc2M2YnRENURpd0hVbDRFWkVZQ2lqeldBTVE4QW1UdENPOWZGMWJ6a053XHUwMDNkXHUwMDNkIiwidCI6IlJTQS9FQ0IvT0FFUHdpdGhTSEExYW5kTUdGMVBhZGRpbmciLCJ0cCI6IjUwMUZDMTQzMkQ4NzE1NURDNDMxMzgyQUVCODQzRUQ1NThBRDYxQjEiLCJ0cGQiOiJTSEEtMSJ9";

        String pass =
                "eyJjIjoiT0QrU213RHB5V2lsV045SU41TkhpL0tibkgrR0RiaGhnYmxxT25TeVNEV01ERWx1K0NYWTRPMks0UU93RTcvOTJWTXM0RU92eWRwaEN6c0JuS2xGWFhlMHcwNUdWcndtMFNoaDBPbWUraTJsWUtrQzdXZXRrSVVab09oUGhqWHk3YXN6Q0lxVEduK1YwRUdKVVRsMGFWWGV2OXpaSWNuL0FoTzRrR055djZ3cENTQmRBY0ZQVXBvVWE1RFFYd0tkdXhXYk5zbmoralhwTnpITWNySWYvQ2ZHek9qL1ZGL3VJaHlkbHB4cDh6enQxQVVodXlaVTdYM25MUGdZYytqVVhRbGxYOVNEVE5ZRmhPdjlsMWpTT0U1Qk9MTVora21xQ3J0Yzh2RGlTTFVMQjhtMUtsK1F4S3Z6UmxSck5wN0pMbUJZeW9BdC80eG9rZGNNY3FHcVN3XHUwMDNkXHUwMDNkIiwidCI6IlJTQS9FQ0IvT0FFUHdpdGhTSEExYW5kTUdGMVBhZGRpbmciLCJ0cCI6IjUwMUZDMTQzMkQ4NzE1NURDNDMxMzgyQUVCODQzRUQ1NThBRDYxQjEiLCJ0cGQiOiJTSEEtMSJ9";

        String test =
                "eyJjIjoiVG1WR3h0RVhCQ0tVWmg4TFkyWU5iaFl1MENERDdZZk93RzVvdFIvUGYrdVlja2Nhc2pPVlAvYm9peTRmeFAwb0MvWVRTMkVGejA2L1VMVm5yV2xiNGVxYTVLQnZEVXkyV3R4YTlZRytGMkpmc2NtNFB5VXN4MmlXV01TRHpsa0tDbWp4SjlCbExlUWtJL09jU0dEQzY1TVlvMFdjSWxpYnNZR2lWR053cVZZYzI5SnQ0dng1M1FPLzdBSDUybGtGK3hqaFpCd3JEVE94ZDEyVE1pYmlTWlJWT3VKMTg0RnVaSkxIbSs1UWxldVBjbHFzY2diYk9kdlk2QVRGZXlYUjlOT3FyZW9BVlZaQks0RHpMM2M3Rmt0Snk0b3NUQVkxZUlidjBWbXBqY1NZclk2ZTRPV1lrUG1XZFRGVWRxczFCcmxDc2Z2bG9yTTI1RWVKcGR1OVB3XHUwMDNkXHUwMDNkIiwidCI6IlJTQS9FQ0IvT0FFUHdpdGhTSEExYW5kTUdGMVBhZGRpbmciLCJ0cCI6IjUwMUZDMTQzMkQ4NzE1NURDNDMxMzgyQUVCODQzRUQ1NThBRDYxQjEiLCJ0cGQiOiJTSEEtMSJ9";

        app.decrypt(test);
    }

    public String decrypt(String data) throws Exception {

        Security.addProvider(new BouncyCastleProvider());
        // point your keystore here
        InputStream file = new FileInputStream(KEYSTORE_FILE);
        KeyStore keystore = KeyStore.getInstance(KEYSTORE_TYPE);
        keystore.load(file, KEYSTORE_PASSWORD.toCharArray());

        PrivateKey privateKey = (PrivateKey) keystore.getKey(KEYSTORE_KEY_ALIAS, KEYSTORE_KEY_PASSWORD.toCharArray());

        Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION, "BC");

        byte[] cipherTextBytes = Base64.decode(data.trim());

        String cipherStr = new String(cipherTextBytes, Charset.defaultCharset());

        CipherHolder cipherHolder = gson.fromJson(cipherStr, CipherHolder.class);

//            System.out.println(cipherTextBytes);

        cipherTextBytes = cipherHolder.getCipherBase64Decoded();

        // decrypt the text
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        String decrypted = new String(cipher.doFinal(cipherTextBytes), Charset.defaultCharset());
        System.out.println("Decrypted test is "+decrypted);
        return decrypted;

    }

    public static class CipherHolder {

        // Base64 encoded ciphertext.
        private String c;

        // Transformation used for encryption, default is "RSA".
        private String t = "RSA";

        // Thumbprint of the certificate.
        private String tp;

        // Digest used to generate certificate thumbprint.
        private String tpd;

        public String getTransformation() {

            return t;
        }

        public void setTransformation(String transformation) {

            this.t = transformation;
        }

        public String getCipherText() {

            return c;
        }

        public void setCipherText(String cipher) {

            this.c = cipher;
        }

        public byte[] getCipherBase64Decoded() {

            return Base64.decode(c);
        }

        public String getThumbPrint() {

            return tp;
        }

        public void setThumbPrint(String tp) {

            this.tp = tp;
        }

        public String getThumbprintDigest() {

            return tpd;
        }

        public void setThumbprintDigest(String digest) {

            this.tpd = digest;
        }

        /**
         * Function to base64 encode ciphertext and set ciphertext
         * @param cipher
         */
        public void setCipherBase64Encoded(byte[] cipher) {

            this.c = Base64.encode(cipher);
        }

        /**
         * Function to set thumbprint
         * @param tp thumb print
         * @param digest digest (hash algorithm) used for to create thumb print
         */
        public void setThumbPrint(String tp, String digest) {

            this.tp = tp;
            this.tpd = digest;
        }

        @Override
        public String toString() {

            Gson gson = new Gson();
            return gson.toJson(this);
        }

    }
}
