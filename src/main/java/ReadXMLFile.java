import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class ReadXMLFile {

    private static final String CARBONPATH = "/Users/kumaaran/IAM/phase3/newSupportCheck/wso2is-5.7" +
            ".0/repository/conf/carbon.xml";
    private static final String CARBON_HOME = "/Users/kumaaran/IAM/phase3/newSupportCheck/wso2is-5.7.0";
    public static Decryptor decryptor = new Decryptor();
    public static Encryptor encryptor = new Encryptor();

    public static void main(String argv[]) {

        String path = "/Users/kumaaran/IAM/phase3/newSupportCheck/wso2is-5.7" +
                ".0/repository/deployment/server/userstores";

        List<String> filePaths = findXMLfiles(path);
        decryptor.setKeystoreKeyAlias("snow");
        encryptor.setKeystoreKeyAlias("snow");
        readConfigs();
//
//        for (String file : filePaths){
//            System.out.println("----------------------------");
//
//            readFile(file,true,true,decryptor,encryptor);
//            System.out.println("----------------------------");
//        }
//        readFile(path+"/KumarJDBC3.xml",true,true,decryptor,encryptor);
        readConfigs();

    }

    public static List<String> findXMLfiles(String path) {

        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();
        List<String> filePaths = new ArrayList<String>();

        for (int i = 0; i < listOfFiles.length; i++) {
            String filename = listOfFiles[i].getName();
            String filePath = listOfFiles[i].getPath();
            if (filename.endsWith(".xml") || filename.endsWith(".XML")) {
                System.out.println(filename);
                System.out.println("path is " + filePath);
                filePaths.add(filePath);
            }
        }
        return filePaths;
    }

    public static void readFile(String file, boolean decrypt, boolean encrypt, Decryptor decryptor,
                                Encryptor encryptor) {

        try {
            File fXmlFile = new File(file);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();

//            System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

            NodeList nList = doc.getElementsByTagName("Property");

            System.out.println("----------------------------");

            for (int temp = 0; temp < nList.getLength(); temp++) {

                Node nNode = nList.item(temp);

//                System.out.println("\nCurrent Element :" + nNode.getNodeName());

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element eElement = (Element) nNode;

                    if (eElement.hasAttribute("name") && eElement.hasAttribute("encrypted")) {
                        String ans = eElement.getFirstChild().getNodeValue();
                        System.out.println(ans);

                        if (decrypt) {
                            String newans = decryptor.decrypt(ans);
                            System.out.println(newans);
                            if (encrypt) {
                                String second = encryptor.encrypt(newans);
                                eElement.getFirstChild().setNodeValue(second);
                                System.out.println(second);
                            }

                        }

                    }

                }
            }
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(doc);
            StreamResult streamResult = new StreamResult(new File(file));
            transformer.transform(domSource, streamResult);
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public static void readConfigs() {

        try {
            File fXmlFile = new File(CARBONPATH);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();

            Node nNode = doc.getElementsByTagName("Security").item(0);

            Element eElement = (Element) nNode;

            Node nodeKeystore = eElement.getElementsByTagName("KeyStore").item(0);

            Element e1 = (Element) nodeKeystore;

            decryptor.setKeystoreFile(getValue(e1,"Location"));
            decryptor.setKeystoreKeyAlias(getValue(e1,"KeyAlias"));
            decryptor.setKeystorePassword(getValue(e1,"Password"));
            decryptor.setKeystoreKeyPassword(getValue(e1,"KeyPassword"));
            decryptor.setKeystoreType(getValue(e1,"Type"));

            Node InternalKeystore = eElement.getElementsByTagName("InternalKeyStore").item(0);

            Element e2 = (Element) InternalKeystore;

            encryptor.setKeystoreFile(getValue(e2,"Location"));
            encryptor.setKeystoreKeyAlias(getValue(e2,"KeyAlias"));
            encryptor.setKeystorePassword(getValue(e2,"Password"));
            encryptor.setKeystoreKeyPassword(getValue(e2,"KeyPassword"));
            encryptor.setKeystoreType(getValue(e2,"Type"));

        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    public static void print(String key, String value) {

        System.out.println(key + " is " + value);
    }

    public static String getValue(Element eElement, String key) {

        String result = eElement.getElementsByTagName(key).item(0).getTextContent();
        String finall= result.replace("${carbon.home}", CARBON_HOME);
        print(key,finall);
        return finall;
    }

}
