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
package eu.juniper.program;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Random;

import mpi.MPI;
import mpi.MPIException;
import mpi.Status;
import eu.juniper.platform.Core;
import eu.juniper.platform.models.auxiliary.DataConnection;

public class JuniperProgram {
	private static final int DEFAULT_MPI_TAG = 999;
	protected int communicationID = -1;
	protected String connectionName = "-";
	protected Core juniperPlatform;
	//protected ArrayList<String> inboundConnections = new ArrayList();
	//protected ArrayList<String> outboundConnections = new ArrayList();
	
	protected String myGroupName;
	protected int myGroupSize;
	protected int myLocalRank;
	protected int myGlobalRank;
	
	public void init() {
		//initialization of in- and outbound connections
		/*
		ArrayList<String> myGroups = juniperPlatform.getApplicationModel().getGroupModel().getGroupsOfMpiRank(juniperPlatform.getMpiRank());
		
		for (int i = 0; i < juniperPlatform.getApplicationModel().getCommunicationModel().getDataConnections().size(); i++)
			for (int j = 0; j < myGroups.size(); j++) {
				if (juniperPlatform.getApplicationModel().getCommunicationModel().getDataConnections().get(i).getSenderGroup().equals(myGroups.get(j)))
					outboundConnections.add(juniperPlatform.getApplicationModel().getCommunicationModel().getDataConnections().get(i).getName());
				else if (juniperPlatform.getApplicationModel().getCommunicationModel().getDataConnections().get(i).getReceiverGroup().equals(myGroups.get(j)))
					inboundConnections.add(juniperPlatform.getApplicationModel().getCommunicationModel().getDataConnections().get(i).getName());
			}
		*/
		
		/*
		for (int i = 0; i < inboundConnections.size(); i++)
			System.out.println("Rank " + juniperPlatform.getMpiRank() + " inbound: " + inboundConnections.get(i));
		for (int i = 0; i < outboundConnections.size(); i++)
			System.out.println("Rank " + juniperPlatform.getMpiRank() + " outbound: " + outboundConnections.get(i));
		*/
		
		myGlobalRank = juniperPlatform.getMpiRank();
		myGroupName = juniperPlatform.getApplicationModel().getGroupModel().getGroupNameOfMpiRank(myGlobalRank);
		myGroupSize = juniperPlatform.getApplicationModel().getGroupModel().getGroupSize(myGroupName);
		myLocalRank = new Integer(juniperPlatform.getApplicationModel().getGroupModel().getLocalRankOfMpiRank(myGlobalRank));
		//System.out.println("Global Rank: " + myGlobalRank + ", Local Rank: " + myLocalRank + ", Group: " + myGroupName + ", Size:  " + myGroupSize);
		
	}
	
	public void run(Core juniperPlatform) throws MPIException {
		
		this.juniperPlatform = juniperPlatform;
		
		init();
		
		//System.out.println("Running the JUNIPER program: " + this.getClass().getName() + " with MPI rank " + juniperPlatform.getMpiRank() + " on " + juniperPlatform.getHostIp());
		
	}
	
	/*
	 * Sends a single MPI message via p2p communication
	 */
	public void sendMpiMessage(JuniperContainer container, int receiverMpiRank) {
		try
		{
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream os = new ObjectOutputStream(bos);
			os.writeObject((Object) container); //dataContainer
			
			ByteBuffer bufMpi = ByteBuffer.wrap(bos.toByteArray());
			
			//System.out.println("Sent buf = " + Arrays.toString(buf));
			
			MPI.COMM_WORLD.send(bufMpi, bufMpi.capacity(), MPI.BYTE, receiverMpiRank, DEFAULT_MPI_TAG);
			
		} catch (Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		}
		
		//System.out.println("Global rank " + myGlobalRank + " sending to Global Rank " + receiverMpiRank);
	}
	
	/*
	 * Receives a single MPI message via p2p communication
	 */
	public JuniperContainer recvMpiMessage(int senderMpiRank) {
		
		JuniperContainer container = null;
		
		try
		{
			
			int size;
			
			Status status = MPI.COMM_WORLD.probe(senderMpiRank, DEFAULT_MPI_TAG);
			size = status.getCount(MPI.BYTE);
			
			ByteBuffer bufMpi = ByteBuffer.allocate(size);
			
			MPI.COMM_WORLD.recv(bufMpi, bufMpi.capacity(), MPI.BYTE, senderMpiRank, DEFAULT_MPI_TAG);
			
			if (size <= 1)
				return null;
			
			//System.out.println("Received buf1 = " + Arrays.toString(buf));
			ByteArrayInputStream bis = new ByteArrayInputStream(bufMpi.array());
			ObjectInputStream is = new ObjectInputStream(bis);
			
			container = (JuniperContainer) is.readObject();
			
			//System.out.println("receiver, commID = " + container.getCommID());
		} catch (Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		}
		
		return container;
	}
	
	/*
	 * Implementation of the data transfer on the sender side
	 */
	public void transferData(String connectionName, ArrayList<Object> dataContainer) {
		
		DataConnection dataConnection = juniperPlatform.getApplicationModel().getCommunicationModel().getDataConnectionByName(connectionName);
		String recvGroupName = dataConnection.getReceiverGroup();
		
		if (dataConnection.getType().equals("symmetric"))
		{
			if (myGroupSize != juniperPlatform.getApplicationModel().getGroupModel().getGroupSize(recvGroupName))
				System.err.println("Symmetric communicator cannot be applied to the groups of different size");
			
			//Getting the global rank of the receiver (which has the same local rank as of the sender in its group)
			int receiverMpiRank = Integer.parseInt(juniperPlatform.getApplicationModel().getGroupModel().getGlobalRankOfMpiLocalRank(myLocalRank, recvGroupName));
			
			JuniperContainer container = new JuniperContainer(dataContainer);
			sendMpiMessage(container, receiverMpiRank);
		}
		else if (dataConnection.getType().equals("all_to_one"))
		{
			if (juniperPlatform.getApplicationModel().getGroupModel().getGroupSize(recvGroupName) > 1)
				System.err.println("All_to_one communicator cannot be applied to the group with more than 1 receiver");
			
			//Getting the global rank of the (single) receiver
			int receiverMpiRank = Integer.parseInt(juniperPlatform.getApplicationModel().getGroupModel().getGlobalRankOfMpiLocalRank(0, recvGroupName));
			JuniperContainer container = new JuniperContainer(dataContainer);
			
			//System.out.println("Global rank " + myGlobalRank + " sending to Global Rank " + receiverMpiRank);
			sendMpiMessage(container, receiverMpiRank);
		}
		else if (dataConnection.getType().equals("one_to_all"))
		{
			int receiversNr = juniperPlatform.getApplicationModel().getGroupModel().getGroupSize(recvGroupName);
			
			if (receiversNr != dataContainer.size())
				System.err.println("One_to_all communicator cannot be applied to the group with the nr of receivers differencing from the size of the data container");
			
			for (int i = 0; i < receiversNr; i++) {
				//Getting the global rank of the sender
				int receiverMpiRank = Integer.parseInt(juniperPlatform.getApplicationModel().getGroupModel().getGlobalRankOfMpiLocalRank(i, recvGroupName));
				
				ArrayList<Object> sendContainer = new ArrayList<Object>();
				sendContainer.add(dataContainer.get(i));
				JuniperContainer container = new JuniperContainer(sendContainer);
				sendMpiMessage(container, receiverMpiRank);
				
				//System.out.println("Global rank " + myGlobalRank + " sending to Global Rank " + receiverMpiRank);
				
			}
		}
	}
	
	/*
	 * Implementation of the data transfer on the receiver side
	 */
	public ArrayList<Object> transferData(String connectionName) {
		
		ArrayList<Object> dataContainer = new ArrayList<Object>();
		JuniperContainer container = null;
		
		DataConnection dataConnection = juniperPlatform.getApplicationModel().getCommunicationModel().getDataConnectionByName(connectionName);
		String sendGroupName = dataConnection.getSenderGroup();
		
		if (dataConnection.getType().equals("symmetric"))
		{
			if (myGroupSize != juniperPlatform.getApplicationModel().getGroupModel().getGroupSize(sendGroupName))
				System.err.println("Symmetric communicator cannot be applied to the groups of different size");
			
			//Getting the global rank of the sender (which has the same local rank as of the receiver in its group)
			int senderMpiRank = Integer.parseInt(juniperPlatform.getApplicationModel().getGroupModel().getGlobalRankOfMpiLocalRank(myLocalRank, sendGroupName));
			container = recvMpiMessage(senderMpiRank);
			
			dataContainer = (ArrayList<Object>) container.getObj();
			//System.out.println("Global rank " + myGlobalRank + " receiving from Global Rank " + senderMpiRank);
		}
		else if (dataConnection.getType().equals("all_to_one"))
		{
			int sendersNr = juniperPlatform.getApplicationModel().getGroupModel().getGroupSize(sendGroupName);
			for (int i = 0; i < sendersNr; i++) {
				//Getting the global rank of the sender
				int senderMpiRank = Integer.parseInt(juniperPlatform.getApplicationModel().getGroupModel().getGlobalRankOfMpiLocalRank(i, sendGroupName));
				container = recvMpiMessage(senderMpiRank);
				
				ArrayList<Object> partDataContainer = (ArrayList<Object>) container.getObj();
				for (int j = 0; j < partDataContainer.size(); j++)
					dataContainer.add(partDataContainer.get(j));
				
				//System.out.println("Global rank " + myGlobalRank + " receiving from Global Rank " + senderMpiRank);
			}
		}
		else if (dataConnection.getType().equals("one_to_all"))
		{
			//Getting the global rank of the sender
			int senderMpiRank = Integer.parseInt(juniperPlatform.getApplicationModel().getGroupModel().getGlobalRankOfMpiLocalRank(0, sendGroupName));
			container = recvMpiMessage(senderMpiRank);
			
			dataContainer = (ArrayList<Object>) container.getObj();
			//System.out.println("Global rank " + myGlobalRank + " receiving from Global Rank " + senderMpiRank);
		}
		
		return dataContainer;
	}
	
	public boolean hasData(String connectionName) {
		
		ArrayList<Object> dataContainer = new ArrayList<Object>();
		JuniperContainer container = null;
		
		DataConnection dataConnection = juniperPlatform.getApplicationModel().getCommunicationModel().getDataConnectionByName(connectionName);
		String sendGroupName = dataConnection.getSenderGroup();
		
		int senderMpiRank = -1;
		try {
			if (dataConnection.getType().equals("symmetric"))
			{
				if (myGroupSize != juniperPlatform.getApplicationModel().getGroupModel().getGroupSize(sendGroupName))
					System.err.println("Symmetric communicator cannot be applied to the groups of different size");
				
				//Getting the global rank of the sender (which has the same local rank as of the receiver in its group)
				senderMpiRank = Integer.parseInt(juniperPlatform.getApplicationModel().getGroupModel().getGlobalRankOfMpiLocalRank(myLocalRank, sendGroupName));
				return MPI.COMM_WORLD.iProbe(senderMpiRank, DEFAULT_MPI_TAG) != null;
			}
			else
			{
				int sendersNr = juniperPlatform.getApplicationModel().getGroupModel().getGroupSize(sendGroupName);
				for (int i = 0; i < sendersNr; i++) {
					//Getting the global rank of the sender
					senderMpiRank = Integer.parseInt(juniperPlatform.getApplicationModel().getGroupModel().getGlobalRankOfMpiLocalRank(i, sendGroupName));
					if (MPI.COMM_WORLD.iProbe(senderMpiRank, DEFAULT_MPI_TAG) != null) {
						return true;
					}
				}
			}
		} catch (MPIException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		return false;
	}
	
	public void setCommunicationID() {
		Random rand = new Random();
		int commID = rand.nextInt(Integer.MAX_VALUE); // (new Object().hashCode());
		this.communicationID = commID;
		System.out.println("Instance ID generated: " + this.communicationID);
	}
	
	public void setCommunicationID(int commID) {
		this.communicationID = commID;
		System.out.println("Instance ID is set: " + this.communicationID);
	}
	
	public void setConnectionName(String cName) {
		this.connectionName = cName;
		System.out.println("Connection name is set: " + this.connectionName);
	}
	
	public int getCommunicationID() {
		return this.communicationID;
	}
	
	public String getConnectionName() {
		return this.connectionName;
	}
	/*
	public void getRank(Core juniperPlatform) throws MPIException {
		System.out.println("Hello from Program with rank " + juniperPlatform.getMpiRank());
		System.out.println("Hello from Program with rank " + MPI.COMM_WORLD.getRank());
	}
	*/
	
}
