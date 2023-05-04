import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class TcConverter {

    public static void main(String[] args){
        String filePath = args[0];
        File xmlFile = new File(filePath);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();
            convertTcToJson(doc);
        } catch (ParserConfigurationException e1) {
            e1.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void convertTcToJson(Document doc) throws IOException {

        JSONArray testcases = new JSONArray(); // array of the testCases

        NodeList tcs = doc.getElementsByTagName("testCase");
        //now XML is loaded as Document in memory, lets convert it to Object List and then to json file

        if (tcs != null && tcs.getLength() > 0) {
            for (int i = 0; i < tcs.getLength(); i++) { // iterate over each TM4J test case
                Node testCase = tcs.item(i);
                JSONObject obj = new JSONObject(); // test case object
                JSONObject fields = new JSONObject();
                JSONArray steps = new JSONArray();
                JSONObject update = new JSONObject();

                // now we got the first TM4J test case
                if (testCase.getNodeType() == Node.ELEMENT_NODE) {
                    Element e = (Element) testCase;
                    NodeList nodeList = e.getElementsByTagName("name");

                    // build fields XRAY Json Object
                    if (nodeList != null && nodeList.getLength() > 0) {

                        JSONObject key = new JSONObject();
                        JSONObject assignee = new JSONObject();
                        JSONArray labels = new JSONArray();
                        JSONObject components = new JSONObject();
                        JSONObject priority = new JSONObject();
                        JSONObject typeOfTest = new JSONObject();
                        JSONObject testStatus = new JSONObject();

                        // Set the summary of the test
                        fields.put("summary", nodeList.item(0).getChildNodes().item(0).getNodeValue());
                        key.put("key", "QA");

                        // add assignee to fields
                        NodeList ch = e.getElementsByTagName("owner");
                        if (ch != null && ch.getLength() > 0) {
                            assignee.put("id", ch.item(0).getChildNodes().item(0).getNodeValue());
                        } else {
                            assignee.put("id", "5bfab55d2c86c94ac7c7297b");
                        }

                        // add labels to fields
                        ch = e.getElementsByTagName("label");
                        if (ch != null && ch.getLength() > 0) {
                            for (int k = 0; k < ch.getLength(); k++) {
                                labels.add(ch.item(k).getChildNodes().item(0).getNodeValue());
                            }
                        }

                        // add description to fields
                        ch = e.getElementsByTagName("objective");
                        if (ch != null && ch.getLength() > 0) {
                            fields.put("description", ch.item(0).getChildNodes().item(0).getNodeValue());
                        }

                        //add priority to fields
                        ch = e.getElementsByTagName("priority");
                        if (ch != null && ch.getLength() > 0) {
                            switch (ch.item(0).getChildNodes().item(0).getNodeValue()) {
                                case "High":
                                    priority.put("id", "2");
                                    break;
                                case "Low":
                                    priority.put("id", "4");
                                    break;
                                case "Normal":
                                default:
                                    priority.put("id", "3");
                                    break;
                            }

                        }
                        // add Type of Test to fields
                        ch = e.getElementsByTagName("value");
                        if (ch != null && ch.getLength() > 0) {
                            typeOfTest.put("value", ch.item(0).getChildNodes().item(0).getNodeValue());
                        }

                        // add Test Status to fields
                        ch = e.getElementsByTagName("status");
                        if (ch != null && ch.getLength() > 0) {
                            String status = ch.item(0).getChildNodes().item(0).getNodeValue();
                            switch (status) {
                                case "Draft":
                                    status = "Not Ready";
                                    break;
                                case "Approved":
                                    status = "Approved";
                                    break;
                                case "Need to Fix":
                                    status = "Need to Fix";
                                    break;
                                case "Automated":
                                    status = "Automation convert";
                                    break;
                                case "Delete":
                                case "Deprecated":
                                    status = "Please Delete";
                                    break;
                            }
                            testStatus.put("value", status);
                        }
                        fields.put("project", key);
                        fields.put("assignee", assignee);
                        fields.put("labels", labels);
                        fields.put("priority", priority);
                        fields.put("customfield_10360", typeOfTest);
                        fields.put("customfield_10374", testStatus);
                    }

                    // add linked issues to issue
                    JSONArray linkedIssues = new JSONArray();
                    nodeList = e.getElementsByTagName("issue");
                    if (nodeList != null && nodeList.getLength() > 0) {
                        for (int k = 0; k < nodeList.getLength(); k++) {
                            Node s = nodeList.item(k);
                            Element l = (Element) s;
                            NodeList nl = l.getElementsByTagName("key");
                            String issueKey = nl.item(0).getChildNodes().item(0).getNodeValue();
                            JSONObject add = new JSONObject();
                            JSONObject type = new JSONObject();
                            type.put("name", "Tests");
                            JSONObject outwardIssue = new JSONObject();
                            outwardIssue.put("key", issueKey);
                            add.put("type", type);
                            add.put("outwardIssue", outwardIssue);
                            JSONObject issue = new JSONObject();
                            issue.put("add", add);
                            linkedIssues.add(issue);
                        }
                        update.put("issuelinks", linkedIssues);
                    }


                    // build steps XRAY Json Object
                    nodeList = e.getElementsByTagName("step");
                    // parse the steps of the test case
                    if (nodeList != null && nodeList.getLength() > 0) {
                        for (int j = 0; j < nodeList.getLength(); j++) {
                            //XrayTestCase.Step step = new XrayTestCase.Step();
                            JSONObject step = new JSONObject();
                            Node s = nodeList.item(j);
                            if (s.getNodeType() == Node.ELEMENT_NODE) {
                                Element l = (Element) s;
                                NodeList nl = l.getElementsByTagName("description");
                                if (nl != null && nl.getLength() > 0) {
                                    //step.setAction(nl.item(0).getChildNodes().item(0).getNodeValue());
                                    step.put("action", nl.item(0).getChildNodes().item(0).getNodeValue());
                                } else
                                    step.put("action", "");

                                nl = l.getElementsByTagName("expectedResult");
                                if (nl != null && nl.getLength() > 0) {
                                    //step.setResult(nl.item(0).getChildNodes().item(0).getNodeValue());
                                    step.put("result", nl.item(0).getChildNodes().item(0).getNodeValue());
                                } else
                                    step.put("result", "");

                                nl = l.getElementsByTagName("testData");
                                if (nl != null && nl.getLength() > 0) {
                                    //step.setData(nl.item(0).getChildNodes().item(0).getNodeValue());
                                    step.put("data", nl.item(0).getChildNodes().item(0).getNodeValue());
                                } else
                                    step.put("data", "");
                            }
                            steps.add(step);
                        }
                    }

                    nodeList = e.getElementsByTagName("folder");
                    if (nodeList != null && nodeList.getLength() > 0) {
                        String folder = nodeList.item(0).getChildNodes().item(0).getNodeValue();
                        obj.put("xray_test_repository_folder", folder);
                    }

                    // build test case XRAY JSON Object
                    obj.put("elementNumber", String.format("%d", i));
                    obj.put("testtype", "Manual");
                    obj.put("fields", fields);
                    obj.put("update", update);
                    obj.put("steps", steps);
                    testcases.add(obj);
                }
                //Now we need to create the Json Object and add it to the JSONArray
                //testcases.add(obj);
            }// end for
        }

        FileWriter file = new FileWriter("XRAY.json");
        testcases.writeJSONString(testcases,file);
        file.flush();
        file.close();
    }
}
