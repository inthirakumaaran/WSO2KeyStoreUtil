//import java.io.FileInputStream;
//import java.security.KeyStore;
//
//public class notes {
//    String keystorePath = CarbonUtils.getServerConfiguration().getFirstProperty(SECURITY_KEY_STORE_LOCATION);
//    FileInputStream file = new FileInputStream(keystorePath);
//    KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
//    String password = CarbonUtils.getServerConfiguration().getFirstProperty(SECURITY_KEY_STORE_PW);
//                        keystore.load(file, password.toCharArray());
//    String alias = CarbonUtils.getServerConfiguration().getFirstProperty(SECURITY_KEY_STORE_KEY_ALIAS);
//    String keyPassword =
//            CarbonUtils.getServerConfiguration().getFirstProperty(SECURITY_KEY_STORE_PW);
//    privateKey = keystore.getKey(alias,keyPassword.toCharArray());
//
//}
