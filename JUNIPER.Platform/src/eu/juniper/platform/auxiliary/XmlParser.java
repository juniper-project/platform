/*******************************************************************************
 * Copyright (c) 2015, The High Performance Computing Center Stuttgart (HLRS)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************/
package eu.juniper.platform.auxiliary;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XmlParser {
	
	String filepath = "/home/hpcochep/Desktop/deployment-plan-new1.xml";
	List<Map<String, Map<String, String>>> programModel;
	List<Map<String, Map<String, String>>> deploymentModel;
	List<Map<String, Map<String, String>>> GroupModel_mpigroups;
	List<List<Map<String, Map<String, String>>>> GroupModel_mpigroups_elements;
	List<Map<String, Map<String, String>>> communicationModel;
	String applicationModelName;
	
	public void parse_Model_XML()
			throws ParserConfigurationException, SAXException, IOException {
		
		String element_name;
		
		String xml = readFile(filepath); //, Charset.defaultCharset());
		
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(new ByteArrayInputStream(xml.getBytes("UTF-8")));
		
		String s, attr;
		NodeList nList = doc.getElementsByTagName("application");
		for (int temp = 0; temp < nList.getLength(); temp++) {
			Node nNode = nList.item(temp);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;
				
				NamedNodeMap nAttrMap = nNode.getAttributes();
				if (nAttrMap != null) {
					Node nAttrForName = nAttrMap.getNamedItem("name");
					if (nAttrForName != null) {
						applicationModelName = nAttrForName.getNodeValue();
					}
				}
				//System.out.println("-" + eElement.getNodeName());
				NodeList nList2 = nNode.getChildNodes();
				
				for (int temp2 = 0; temp2 < nList2.getLength(); temp2++) {
					Node nNode2 = nList2.item(temp2);
					if (nNode2.getNodeType() == Node.ELEMENT_NODE) {
						Element eElement2 = (Element) (nNode2);
						//System.out.println("--" + eElement2.getNodeName());
						NodeList nList3 = nNode2.getChildNodes();
						
						List<Map<String, Map<String, String>>> level3listElements = new ArrayList<Map<String, Map<String, String>>>();
						List<List<Map<String, Map<String, String>>>> level3listElements2 = new ArrayList<List<Map<String, Map<String, String>>>>();
						Map<String, Map<String, String>> level3mapElements = new HashMap<String, Map<String, String>>();
						if (eElement2.getNodeName().equals("ProgramModel")) {
							programModel = new ArrayList<Map<String, Map<String, String>>>();
							level3listElements = programModel;
						}
						else if (eElement2.getNodeName().equals("DeploymentModel")) {
							deploymentModel = new ArrayList<Map<String, Map<String, String>>>();
							level3listElements = deploymentModel;
						}
						else if (eElement2.getNodeName().equals("GroupModel")) {
							GroupModel_mpigroups = new ArrayList<Map<String, Map<String, String>>>();
							level3listElements = GroupModel_mpigroups;
							GroupModel_mpigroups_elements = new ArrayList<List<Map<String, Map<String, String>>>>();
							level3listElements2 = GroupModel_mpigroups_elements;
						}
						else if (eElement2.getNodeName().equals("CommunicationModel")) {
							communicationModel = new ArrayList<Map<String, Map<String, String>>>();
							level3listElements = communicationModel;
						}
						
						for (int temp3 = 0; temp3 < nList3.getLength(); temp3++) {
							Node nNode3 = nList3.item(temp3);
							if (nNode3 != null)
								if (nNode3.getNodeType() == Node.ELEMENT_NODE) {
									Element eElement3 = (Element) (nNode3);
									//System.out.println("---" + eElement3.getNodeName());
									NodeList nList4 = nNode3.getChildNodes();
									
									Map<String, String> level3mapProperties = new HashMap<String, String>();
									level3mapElements = new HashMap<String, Map<String, String>>();
									
									attr = "name";
									s = eElement3.getAttribute(attr);
									if (!s.isEmpty()) {
										level3mapProperties.put(attr, s);
									}
									attr = "type";
									s = eElement3.getAttribute(attr);
									if (!s.isEmpty()) {
										level3mapProperties.put(attr, s);
									}
									attr = "javaclass";
									s = eElement3.getAttribute(attr);
									if (!s.isEmpty()) {
										level3mapProperties.put(attr, s);
									}
									/*
									attr = "nr_instances";
									s = eElement3.getAttribute(attr);
									if (!s.isEmpty()) {
										level3mapProperties.put(attr, s);
									}
									*/
									attr = "mpiglobalrank";
									s = eElement3.getAttribute(attr);
									if (!s.isEmpty()) {
										level3mapProperties.put(attr, s);
									}
									attr = "mpilocalrank";
									s = eElement3.getAttribute(attr);
									if (!s.isEmpty()) {
										level3mapProperties.put(attr, s);
									}
									attr = "hostipaddr";
									s = eElement3.getAttribute(attr);
									if (!s.isEmpty()) {
										level3mapProperties.put(attr, s);
									}
									attr = "programName";
									s = eElement3.getAttribute(attr);
									if (!s.isEmpty()) {
										level3mapProperties.put(attr, s);
									}
									
									attr = "sendingGroup";
									s = eElement3.getAttribute(attr);
									if (!s.isEmpty()) {
										level3mapProperties.put(attr, s);
									}
									
									attr = "receiverMpiGroup";
									s = eElement3.getAttribute(attr);
									if (!s.isEmpty()) {
										level3mapProperties.put(attr, s);
									}
									
									attr = "splittable";
									s = eElement3.getAttribute(attr);
									if (!s.isEmpty()) {
										level3mapProperties.put(attr, s);
									}
									
									Map<String, Map<String, String>> level4mapElements = new HashMap<String, Map<String, String>>();
									List<Map<String, Map<String, String>>> level4listElements = new ArrayList<Map<String, Map<String, String>>>();
									for (int temp4 = 0; temp4 < nList4.getLength(); temp4++) {
										
										Node nNode4 = nList4.item(temp4);
										if (nNode4.getNodeType() == Node.ELEMENT_NODE) {
											Element eElement4 = (Element) (nNode4);
											//System.out.println("----" + eElement4.getNodeName());
											
											Map<String, String> level4mapProperties = new HashMap<String, String>();
											level4mapElements = new HashMap<String, Map<String, String>>();
											
											attr = "name";
											s = eElement4.getAttribute(attr);
											if (!s.isEmpty()) {
												level4mapProperties.put(attr, s);
											}
											attr = "type";
											s = eElement4.getAttribute(attr);
											if (!s.isEmpty()) {
												level4mapProperties.put(attr, s);
											}
											attr = "javaclass";
											s = eElement4.getAttribute(attr);
											if (!s.isEmpty()) {
												level4mapProperties.put(attr, s);
											}
											/*
											attr = "nr_instances";
											s = eElement4.getAttribute(attr);
											if (!s.isEmpty()) {
												level4mapProperties.put(attr, s);
											}
											*/
											attr = "mpiglobalrank";
											s = eElement4.getAttribute(attr);
											if (!s.isEmpty()) {
												level4mapProperties.put(attr, s);
											}
											attr = "mpilocalrank";
											s = eElement4.getAttribute(attr);
											if (!s.isEmpty()) {
												level4mapProperties.put(attr, s);
											}
											
											attr = "programName";
											s = eElement4.getAttribute(attr);
											if (!s.isEmpty()) {
												level4mapProperties.put(attr, s);
											}
											
											attr = "hostipaddr";
											s = eElement4.getAttribute(attr);
											if (!s.isEmpty()) {
												level4mapProperties.put(attr, s);
											}
											
											attr = "sendingGroup";
											s = eElement4.getAttribute(attr);
											if (!s.isEmpty()) {
												level4mapProperties.put(attr, s);
											}
											
											attr = "receiverMpiGroup";
											s = eElement4.getAttribute(attr);
											if (!s.isEmpty()) {
												level4mapProperties.put(attr, s);
											}
											
											attr = "splittable";
											s = eElement4.getAttribute(attr);
											if (!s.isEmpty()) {
												level4mapProperties.put(attr, s);
											}
											
											level4mapElements.put(eElement4.getNodeName(), level4mapProperties);
											if (!level4mapElements.isEmpty())
												level4listElements.add(level4mapElements);
										}
									}
									
									level3mapElements.put(eElement3.getNodeName(), level3mapProperties);
									if (!level3mapElements.isEmpty())
										level3listElements.add(level3mapElements);
									if (!level4listElements.isEmpty())
										level3listElements2.add(level4listElements);
								}
						}
						element_name = level3listElements.get(0).keySet().iterator().next();
						
						/*
						if (!level3listElements.isEmpty())
							System.out.println("--- " + element_name + "s: " + level3listElements.toString());
						if (!level3listElements2.isEmpty())
							if (!level3listElements2.get(0).isEmpty())
								System.out.println("--- " + element_name + "s_elements: " + level3listElements2.toString());
						*/
						
					}
				}
			}
		}
	}
	
	static String readFile(String fileName) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		try {
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();
			
			while (line != null) {
				sb.append(line);
				sb.append("\n");
				line = br.readLine();
			}
			return sb.toString();
		} finally {
			br.close();
		}
	}
	
	public String getFilepath() {
		return filepath;
	}
	
	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}
	
	public List<Map<String, Map<String, String>>> getProgramModel() throws ParserConfigurationException, SAXException, IOException {
		return programModel;
	}
	
	public List<Map<String, Map<String, String>>> getDeploymentModel() throws ParserConfigurationException, SAXException, IOException {
		return deploymentModel;
	}
	
	public List<Map<String, Map<String, String>>> getGroupModel_mpigroups() throws ParserConfigurationException, SAXException, IOException {
		//parse_Model_XML();
		return GroupModel_mpigroups;
	}
	
	public List<List<Map<String, Map<String, String>>>> getGroupModel_mpigroups_elements() throws ParserConfigurationException, SAXException, IOException {
		//parse_Model_XML();
		return GroupModel_mpigroups_elements;
	}
	
	public List<Map<String, Map<String, String>>> getCommunicationModel() throws ParserConfigurationException, SAXException, IOException {
		//parse_Model_XML();
		return communicationModel;
	}
	
	public String getApplicationModelName() {
		return applicationModelName;
	}
}

//-------------------Usage example------------------------------------------------------------
/*
package parsing;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

public class JUNIPER_test_parser {

    public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {

    XmlParser x = new XmlParser();
    	
    //first, you need to define the filepath of the XML source 
    x.setFilepath("D:/j.xml");
    	
    //then, you need to parse the XML source in order to make the variables defined
	x.parse_Model_XML();

	String res = x.getProgramModel().toString();
	//full output of ProgramModel
	System.out.println("full output of ProgramModel = " + res);
	
	//alternative full output of ProgramModel:
	List<Map<String, Map<String, String>>> myProgramModel = x.getProgramModel();
	System.out.println("alternative full output of ProgramModel = " + myProgramModel.toString());
	
	res = x.getProgramModel().get(0).get("program").get("name").toString();
	// "get(0).get("program")" means go get the first element called "program"
	// "get("name")" means to get the element's property called name
	//=> "Map"
	System.out.println("first program's name = " + res);
	
	res = x.getDeploymentModel().get(0).toString();
	//=> first cloudnode
	System.out.println("first cloudnode = " + res);
	
	res = x.getGroupModel_mpigroups().get(0).get("mpigroup").get("name").toString();
	//=> first mpigroup member name
	System.out.println("first mpigroup member's name = " + res);

	res = x.getGroupModel_mpigroups_elements().get(1).get(0).get("member").toString();
	//=> *first* member of *second* mpigroup
	System.out.println("first member of second mpigroup = " + res);

	res = x.getGroupModel_mpigroups_elements().get(1).get(0).get("member").get("mpiglobalrank").toString();
	// mpiglobalrank of *first* member of *second* mpigroup
	System.out.println("globalrank of first member of second mpigroup = " + res);
	
	res = x.getCommunicationModel().get(0).get("dataconnection").get("name").toString();
	// name of first dataconnection
	System.out.println("first dataconnection's name = " + res);
	
    }
}
*/
