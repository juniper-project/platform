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
package eu.juniper.platform;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import mpi.MPIException;
import eu.juniper.platform.auxiliary.XmlParser;
import eu.juniper.platform.models.ApplicationModel;
import eu.juniper.platform.models.CommunicationModel;
import eu.juniper.platform.models.DeploymentModel;
import eu.juniper.platform.models.GroupModel;
import eu.juniper.platform.models.ProgramModel;
import eu.juniper.platform.models.auxiliary.CloudNode;
import eu.juniper.platform.models.auxiliary.DataConnection;
import eu.juniper.platform.models.auxiliary.MpiGroup;
import eu.juniper.platform.models.auxiliary.MpiGroupMember;
import eu.juniper.platform.models.auxiliary.Program;

public class Rte {
	
	Core juniperPlatform;
	
	public static void main(String args[]) {
		
		Rte rte = new Rte();
		
		// platform's launch
		try {
			rte.juniperPlatform = new Core();
			rte.juniperPlatform.init(args);
			//System.out.println("Platform launched on " + juniperPlatform.getMpiRanksTotal() + " nodes");
		} catch (MPIException e) {
			e.printStackTrace();
		}
		
		// platform's initialization
		rte.initialize((args.length > 0) ? args[0] : "application_model.xml");
		
		// deployment of Juniper Programs by means of dynamic class loaders
		rte.deploy();
		
		// platform's finalization
		rte.shutdown();
	}
	
	// executing the "run" method of the JuniperProgram class
	void run(String programJavaClass, String methodToRun) throws ClassNotFoundException, InstantiationException, IllegalAccessException, SecurityException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException {
		
		Class programToInstantiate = Rte.class.getClassLoader().loadClass(programJavaClass);
		//System.out.println("rank + " + juniperPlatform.getMpiRank() + " invoking " + programJavaClass);
		
		Object programInstance = programToInstantiate.newInstance();
		Method instanceMethod = programToInstantiate.getMethod(methodToRun, new Class[] {Core.class});
		instanceMethod.invoke(programInstance, new Object[] {juniperPlatform});
		
		/*
		 * Examples of method invocation with and without arguments
		 *
		myMethod = programToInstantiate.getMethod(methodToRun); //no arguments
		myMethod.invoke(programInstance);  
		
		myMethod = programToInstantiate.getMethod("setInstanceId", new Class[] {int.class});
		myMethod.invoke(programInstance, new Object[] {1});
		
		myMethod = programToInstantiate.getMethod("getRank", new Class[] {Core.class});
		myMethod.invoke(programInstance, new Object[] {juniperPlatform});
		*/
		
	}
	
	// deployment of Juniper programs by means of dynamic class loaders
	void deploy() {
		CloudNode mynode = juniperPlatform.juniperApplicationModel.getDeploymentModel().getCloudNodeByMpiglobalrank(juniperPlatform.mpiRank);
		if (mynode != null) {
			
			String programName = juniperPlatform.juniperApplicationModel.getGroupModel().getProgramNameForGlobalRank(new Integer(juniperPlatform.mpiRank).toString());
			String javaClass = juniperPlatform.juniperApplicationModel.getProgramModel().getProgramByName(programName).getJavaClass();
			
			//System.out.println("rank + " + juniperPlatform.getMpiRank() + " running " + javaClass);
			
			try {
				run(javaClass, "run");
			} catch (ClassNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
			System.out.println("Rank " + juniperPlatform.mpiRank + " has nothing to deploy");
		
	}
	
	//platform's initialization according to the Application Model 
	void initialize(String xmlFile) {
		
		//System.out.println("Hello from " + juniperPlatform.getMpiRank() + " out of " + juniperPlatform.getMpiRanksTotal());
		
		if (juniperPlatform.mpiRank == 0)
			System.out.println("Parsing Application Model XML description");
		
		//read xml and initilise the models
		try {
			
			XmlParser parser = new XmlParser();
			
			parser.setFilepath(xmlFile);
			parser.parse_Model_XML();
			
			//ProgramModel
			ProgramModel juniperProgramModel = new ProgramModel();
			List<Map<String, Map<String, String>>> myProgramModel = parser.getProgramModel();
			for (int i = 0; i < myProgramModel.size(); i++) {
				Program program = new Program(myProgramModel.get(i).get("program").get("name"), myProgramModel.get(i).get("program").get("javaclass"));
				juniperProgramModel.addProgram(program);
				//System.out.println("Adding a program: " + myProgramModel.get(i).get("program").get("name") + " : " + myProgramModel.get(i).get("program").get("javaclass"));
			}
			
			//DeploymentModel
			DeploymentModel juniperDeploymentModel = new DeploymentModel();
			List<Map<String, Map<String, String>>> myDeploymentModel = parser.getDeploymentModel();
			for (int i = 0; i < myDeploymentModel.size(); i++) {
				CloudNode node = new CloudNode(myDeploymentModel.get(i).get("cloudnode").get("mpiglobalrank"), myDeploymentModel.get(i).get("cloudnode").get("hostipaddr"));
				juniperDeploymentModel.addCloudNode(node);
				//System.out.println("Adding a cloudnode: " + myDeploymentModel.get(i).get("cloudnode").get("mpiglobalrank") + myDeploymentModel.get(i).get("cloudnode").get("programname") + myDeploymentModel.get(i).get("cloudnode").get("hostipaddr"));
			}
			
			//CommunicationModel
			CommunicationModel juniperCommunicationModel = new CommunicationModel();
			List<Map<String, Map<String, String>>> myCommunicationModel = parser.getCommunicationModel();
			for (int i = 0; i < myCommunicationModel.size(); i++) {
				DataConnection connection = new DataConnection(myCommunicationModel.get(i).get("dataconnection").get("name"), myCommunicationModel.get(i).get("dataconnection").get("sendingGroup"), myCommunicationModel.get(i).get("dataconnection").get("receiverMpiGroup"), myCommunicationModel.get(i).get("dataconnection").get("type"));
				juniperCommunicationModel.addDataConnection(connection);
				//System.out.println("Adding a connection: " + myCommunicationModel.get(i).get("dataconnection").get("name") + myCommunicationModel.get(i).get("dataconnection").get("senderMpiGroup") + myCommunicationModel.get(i).get("dataconnection").get("receiverMpiGroup") + myCommunicationModel.get(i).get("dataconnection").get("splittable") + myCommunicationModel.get(i).get("dataconnection").get("javaclass"));
			}
			
			//MpiTopologyModel
			GroupModel juniperMpiTopologyModel = new GroupModel();
			List<List<Map<String, Map<String, String>>>> mpigroups_elements = parser.getGroupModel_mpigroups_elements();
			for (int i = 0; i < mpigroups_elements.size(); i++) {
				
				MpiGroup group = new MpiGroup(parser.getGroupModel_mpigroups().get(i).get("mpigroup").get("name"));
				//System.out.println("Adding a group " + parser.getGroupModel_mpigroups().get(i).get("mpigroup").get("name"));
				for (int j = 0; j < mpigroups_elements.get(i).size(); j++) {
					MpiGroupMember member = new MpiGroupMember(mpigroups_elements.get(i).get(j).get("member").get("mpiglobalrank"), mpigroups_elements.get(i).get(j).get("member").get("mpilocalrank"), mpigroups_elements.get(i).get(j).get("member").get("programName"));
					group.addMpiGroupMember(member);
					//System.out.println("Adding a member: " + mpigroups_elements.get(i).get(j).get("member").get("mpiglobalrank") + " : " + mpigroups_elements.get(i).get(j).get("member").get("programName"));
				}
				juniperMpiTopologyModel.addMpiGroup(group);
			}
			
			juniperPlatform.juniperApplicationModel = new ApplicationModel(parser.getApplicationModelName(), juniperProgramModel, juniperDeploymentModel, juniperMpiTopologyModel, juniperCommunicationModel);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// platform's finalization
	void shutdown() {
		try {
			juniperPlatform.shutdown();
			if (juniperPlatform.mpiRank == 0)
				System.out.println("Platform shutdown");
		} catch (MPIException e) {
			e.printStackTrace();
		}
	}
}
