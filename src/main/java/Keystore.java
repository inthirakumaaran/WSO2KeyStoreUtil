import sun.net.www.http.HttpClient;
import sun.security.x509.AlgorithmId;
import sun.security.x509.CertificateAlgorithmId;
import sun.security.x509.CertificateSerialNumber;
import sun.security.x509.CertificateValidity;
import sun.security.x509.CertificateVersion;
import sun.security.x509.CertificateX509Key;
import sun.security.x509.X500Name;
import sun.security.x509.X509CertImpl;
import sun.security.x509.X509CertInfo;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Date;

public class Keystore {

    private static String KEYSTORE_FILE = "/Users/kumaaran/IAM/phase3/wso2carbon.jks";

    private static X509Certificate generateCertificate(String dn, KeyPair keyPair, int validity, String sigAlgName) throws GeneralSecurityException, IOException {
        PrivateKey privateKey = keyPair.getPrivate();

        X509CertInfo info = new X509CertInfo();

        Date from = new Date();
        Date to = new Date(from.getTime() + validity * 1000L * 24L * 60L * 60L);

        CertificateValidity interval = new CertificateValidity(from, to);
        BigInteger serialNumber = new BigInteger(64, new SecureRandom());
        X500Name owner = new X500Name(dn);
        AlgorithmId sigAlgId = new AlgorithmId(AlgorithmId.RSA_oid);

        info.set(X509CertInfo.VALIDITY, interval);
        info.set(X509CertInfo.SERIAL_NUMBER, new CertificateSerialNumber(serialNumber));
        info.set(X509CertInfo.SUBJECT, owner);
        info.set(X509CertInfo.ISSUER, owner);
        info.set(X509CertInfo.KEY, new CertificateX509Key(keyPair.getPublic()));
        info.set(X509CertInfo.VERSION, new CertificateVersion(CertificateVersion.V3));
        info.set(X509CertInfo.ALGORITHM_ID, new CertificateAlgorithmId(sigAlgId));

        // Sign the cert to identify the algorithm that's used.
        X509CertImpl certificate = new X509CertImpl(info);
        certificate.sign(privateKey, sigAlgName);

        // Update the algorith, and resign.
        sigAlgId = (AlgorithmId) certificate.get(X509CertImpl.SIG_ALG);
        info.set(CertificateAlgorithmId.NAME + "." + CertificateAlgorithmId.ALGORITHM, sigAlgId);
        certificate = new X509CertImpl(info);
        certificate.sign(privateKey, sigAlgName);

        return certificate;
    }

    public static void main(String[] args) {
//        try {
//            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
//            keyPairGenerator.initialize(2048);
//            KeyPair keyPair = keyPairGenerator.generateKeyPair();
//
//            Certificate[] chain = {generateCertificate("cn=localhost", keyPair, 365, "SHA256withRSA")};
//
//            KeyStore keyStore = KeyStore.getInstance("JKS");
//            InputStream file = new FileInputStream(KEYSTORE_FILE);
//            keyStore.load(file, "wso2carbon".toCharArray());
//            keyStore.setKeyEntry("arya", keyPair.getPrivate(), "wso2carbon".toCharArray(), chain);
//            FileOutputStream fos = new FileOutputStream(KEYSTORE_FILE);
//            keyStore.store(fos, "wso2carbon".toCharArray());
//        } catch (IOException | GeneralSecurityException e) {
//            e.printStackTrace();
//        }
//        HttpClient client = new DefaultHttpClient();
//        HttpPost httpRevoke = new HttpPost(REVOKE_TOKEN_API_ENDPOINT);
        removeKeyFromKeyStore("arya");
    }

    private static void removeKeyFromKeyStore(String alias){
        try {

            KeyStore keyStore = KeyStore.getInstance("JKS");
            InputStream file = new FileInputStream(KEYSTORE_FILE);
            keyStore.load(file, "wso2carbon".toCharArray());
            keyStore.deleteEntry(alias);
            FileOutputStream fos = new FileOutputStream(KEYSTORE_FILE);
            keyStore.store(fos, "wso2carbon".toCharArray());
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
        }
    }

}
