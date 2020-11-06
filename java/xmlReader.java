import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class xmlReader {
    public static void main(String[] args) throws SAXException, ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        try {
            File inputFile = new File("./dump/xml/63260067.xml");
            Document doc = builder.parse(inputFile);
            doc.getDocumentElement().normalize();
            // System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
            String AcceptedAnswerId = "0";

            Node nNode = doc.getDocumentElement().getFirstChild();

            String question = "";
            String acceptedAnswer = "";
            String answers = "";
            while (nNode.getNextSibling() != null) {
                nNode = nNode.getNextSibling();


                if (nNode.getNodeName() == "Title") {
                    question += nNode.getTextContent();
                }

                if (nNode.getNodeName() == "Body") {
                    question += nNode.getTextContent();
                }
                
                if (nNode.getNodeName() == "AcceptedAnswerId") {
                    AcceptedAnswerId = nNode.getTextContent();
                }

                if (nNode.getNodeName() == "Answers") {
                    Node answer = nNode.getFirstChild();
                    while (answer != null) {
                        // Loop over all answers
                        if (answer.getNodeName() == "item") {
                            Element element = (Element) answer;
                            String id = element.getElementsByTagName("Id").item(0).getTextContent();
                            if (id == AcceptedAnswerId) {
                                acceptedAnswer = element.getElementsByTagName("Body").item(0).getTextContent();
                            }
                            else {
                                answers += "\n" + element.getElementsByTagName("Body").item(0).getTextContent();
                            }
                            // System.out.println(" ------------------- ");
                            // System.out.println(answer.getTextContent());
                        }
                        answer = answer.getNextSibling();
                    }
                    // System.out.println(index);
                }
            }

            System.out.println("Question:\n " + question + "\n");
            System.out.println("Accepted Answer:\n " + acceptedAnswer + "\n");
            System.out.println("Answers:\n " + answers + "\n");

            // NodeList nList = doc.getDocumentElement().getChildNodes();

            // for (int temp = 0; temp < nList.getLength(); temp++) {
            //     nNode = nList.item(temp);
            //     System.out.println("\nCurrent Element :" + nNode.getNodeName());


            //     if (nNode.getNodeName() == "AcceptedAnswerId") {
            //         AcceptedAnswerId = Integer.parseInt(nNode.getTextContent());
            //     }

            //     if (nNode.getNodeName() == "Answers") {
            //         // Element eElement = (Element) nNode;
            //         NodeList newList = nNode.getChildNodes();
            //         for (int i = 0; i < newList.getLength(); i++) {
            //             System.out.println("Answer " + i + ":");
            //             System.out.println(newList.item(i).getTextContent());
            //         }

            //         System.out.println(nNode.getTextContent());     
            //     }

            //     // System.out.println(nNode.getTextContent()); 
            // }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static Map<String, String> xmlToMap(String inputPath) throws SAXException, ParserConfigurationException,
            IOException {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        try {
            File inputFile = new File(inputPath);
            Document doc = builder.parse(inputFile);
            doc.getDocumentElement().normalize();
            // System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
            String AcceptedAnswerId = "0";

            Node nNode = doc.getDocumentElement().getFirstChild();

            String questionTitle = "";
            String questionBody = "";
            String acceptedAnswer = "";
            String answers = "";
            while (nNode.getNextSibling() != null) {
                nNode = nNode.getNextSibling();

                if (nNode.getNodeName() == "Title") {
                    questionTitle = nNode.getTextContent();
                }

                if (nNode.getNodeName() == "Body") {
                    questionBody = nNode.getTextContent();
                }
                
                if (nNode.getNodeName() == "AcceptedAnswerId") {
                    AcceptedAnswerId = nNode.getTextContent();
                }

                if (nNode.getNodeName() == "Answers") {
                    Node answer = nNode.getFirstChild();
                    while (answer != null) {
                        // Loop over all answers
                        if (answer.getNodeName() == "item") {
                            Element element = (Element) answer;
                            String id = element.getElementsByTagName("Id").item(0).getTextContent();
                            if (id == AcceptedAnswerId) {
                                acceptedAnswer = element.getElementsByTagName("Body").item(0).getTextContent();
                            }
                            else {
                                answers += "\n" + element.getElementsByTagName("Body").item(0).getTextContent();
                            }
                        }
                        answer = answer.getNextSibling();
                    }
                }
            }
            Map<String, String> temp = new HashMap<String, String>();
            temp.put("questionBody", questionBody);
            temp.put("questionTitle", questionTitle);
            temp.put("answers", answers);
            temp.put("acceptedAnswer", acceptedAnswer);
            return temp;

        } catch (SAXException e) {
            //TODO: handle exception
            throw e;
        }
    }
}
