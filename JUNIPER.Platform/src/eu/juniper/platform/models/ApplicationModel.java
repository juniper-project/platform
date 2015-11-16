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
package eu.juniper.platform.models;

public class ApplicationModel {
	
	String name;
	
	ProgramModel juniperProgramModel;
	DeploymentModel juniperDeploymentModel;
	GroupModel juniperGroupModel;
	CommunicationModel juniperCommunicationModel;
	
	public ApplicationModel(String name, ProgramModel juniperProgramModel, DeploymentModel juniperDeploymentModel, GroupModel juniperMpiTopologyModel, CommunicationModel juniperCommunicationModel)
	{
		this.name = name;
		this.juniperProgramModel = juniperProgramModel;
		this.juniperDeploymentModel = juniperDeploymentModel;
		this.juniperGroupModel = juniperMpiTopologyModel;
		this.juniperCommunicationModel = juniperCommunicationModel;
	}
	
	public DeploymentModel getDeploymentModel()
	{
		return juniperDeploymentModel;
	}
	
	public ProgramModel getProgramModel()
	{
		return juniperProgramModel;
	}
	
	public CommunicationModel getCommunicationModel()
	{
		return juniperCommunicationModel;
	}
	
	public GroupModel getGroupModel()
	{
		return juniperGroupModel;
	}
	
	public String getName() {
		return this.name;
	}
}
