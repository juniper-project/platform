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

import java.util.ArrayList;
import java.util.List;

import mpi.MPIException;
import eu.juniper.platform.Core;

public class Reducer extends JuniperProgram {
	
	public void run(Core juniperPlatform) throws MPIException {
		
		super.run(juniperPlatform);
		System.out.println("[" + super.juniperPlatform.getHostIp() + "] Running Reducer [" + (super.myLocalRank + 1) + "/" + super.myGroupSize + "] of the group " + super.myGroupName);
		
		if (super.myGroupName.equals("LocalReduces")) {
			List<Map> maps = recvData("connection2");
			System.out.println("[" + super.juniperPlatform.getHostIp() + "] Reducer rank " + juniperPlatform.getMpiRank() + " received: " + maps.size() + " maps");
			
			//performing reduction
			maps = reduce(maps);
			
			//List<Map> sendBuffer = recvBuffer;
			sendData(maps, "connection3");
			
		}
		else if (super.myGroupName.equals("GlobalReduces")) {
			List<Map> maps = recvData("connection3");
			System.out.println("[" + super.juniperPlatform.getHostIp() + "] Reducer rank " + juniperPlatform.getMpiRank() + " received: " + maps.size() + " maps");
			
			//performing reduction
			maps = reduce(maps);
			
			//output of results
			System.out.println("Resulting maps:");
			for (int i = 0; i < maps.size(); i++)
				System.out.println("Map " + i + ": [" + maps.get(i).getWord() + " , " + maps.get(i).getNumber() + "]");
			
		}
	}
	
	public List<Map> reduce(List<Map> maps) {
		for (int i = 0; i < maps.size() - 1; i++) {
			for (int j = i + 1; j < maps.size(); j++)
				if (maps.get(i).getWord().equals(maps.get(j).getWord())) {
					maps.get(i).setNumber(maps.get(i).getNumber() + maps.get(j).getNumber());
					maps.remove(j);
					j--;
				}
		}
		
		return maps;
	}
	
	public List<Map> recvData(String connectionName) {
		ArrayList<Object> recvContainer = super.transferData(connectionName);
		
		List<Map> map = new ArrayList<Map>();
		
		for (int i = 0; i < recvContainer.size(); i++) {
			List<Map> recvBuffer = (List<Map>) recvContainer.get(i);
			for (int j = 0; j < recvBuffer.size(); j++)
				map.add(recvBuffer.get(j));
		}
		
		return map;
	}
	
	public void sendData(List<Map> sendBuffer, String connectionName) {
		ArrayList<Object> sendContainer = new ArrayList<Object>();
		sendContainer.add(sendBuffer);
		
		super.transferData(connectionName, sendContainer);
		
		System.out.println("[" + super.juniperPlatform.getHostIp() + "] Reducer rank " + juniperPlatform.getMpiRank() + " sent: " + sendBuffer.size() + " maps");
	}
}
