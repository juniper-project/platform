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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import mpi.MPIException;
import eu.juniper.platform.Core;

public class Mapper extends JuniperProgram {
	
	public void run(Core juniperPlatform) throws MPIException {
		
		//initialisation
		super.run(juniperPlatform);
		System.out.println("[" + super.juniperPlatform.getHostIp() + "] Running Mapper [" + (super.myLocalRank + 1) + "/" + super.myGroupSize + "] of the group " + super.myGroupName);
		
		//try{TimeUnit.SECONDS.sleep(1);}
		//catch(InterruptedException e){}
		
		//receiving boundaries from the LoadBalancer
		ArrayList<Object> recvContainer = super.transferData("connection1");
		
		Boundaries myBoundaries = (Boundaries) recvContainer.get(0);
		
		//reading input text file and initial map creation
		List<Map> map = readTextFile("./input.txt");
		System.out.println("Maps in Mapper = " + map.size());
		
		//trimming the map to the part that the mapper is responsible for
		List<Map> mapTrimmed = new ArrayList<Map>();
		for (int i = myBoundaries.start; i <= myBoundaries.end; i++)
			mapTrimmed.add(map.get(i));
		
		//sending the data to the reducers
		sendData(mapTrimmed, "connection2");
	}
	
	public void sendData(List<Map> sendBuffer, String connectionName) {
		//super.transferData(outboundConnections.get(0), sendBuffer);
		ArrayList<Object> sendContainer = new ArrayList<Object>();
		sendContainer.add(sendBuffer);
		
		super.transferData(connectionName, sendContainer);
		
		System.out.println("[" + super.juniperPlatform.getHostIp() + "] Mapper rank " + juniperPlatform.getMpiRank() + " sent: " + sendBuffer.size() + " maps");
	}
	
	public List<Map> readTextFile(String fileName) {
		
		Integer defaultKey = 1;
		List<Map> map = new ArrayList<Map>();
		
		FileInputStream fstream;
		try {
			fstream = new FileInputStream(fileName);
			
			BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
			
			String nextWord;
			while ((nextWord = br.readLine()) != null) {
				map.add(new Map(nextWord, defaultKey));
			}
			
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return map;
	}
}

class Map implements Serializable {
	String word;
	Integer number;
	
	public Map(String word, Integer number) {
		this.word = word;
		this.number = number;
	}
	
	public String getWord() {
		return word;
	}
	
	public Integer getNumber() {
		return number;
	}
	
	public void setNumber(Integer number) {
		this.number = number;
	}
}
