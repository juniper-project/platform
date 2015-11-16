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

import java.util.ArrayList;

import eu.juniper.platform.models.auxiliary.Program;

public class ProgramModel {
	
	ArrayList<Program> programs;
	
	public ProgramModel() {
		programs = new ArrayList<Program>();
	}
	
	public void addProgram(Program program) {
		this.programs.add(program);
	}
	
	public Program getProgramByName(String name) {
		for (int i = 0; i < programs.size(); i++)
			if (programs.get(i).getName().equals(name))
				return programs.get(i);
		
		return null;
	}
	
	public Program getProgramByJavaClassName(String javaClassName) {
		for (int i = 0; i < programs.size(); i++)
			if (programs.get(i).getJavaClass().equals(javaClassName))
				return programs.get(i);
		
		return null;
	}
		
}
