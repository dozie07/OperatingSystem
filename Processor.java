public class Processor {

	public static void execute(String jobString, String jobId, int[] registers, int CPUNumber, String[] programCache) {
		String operationType = jobString.substring(0, 2);
		switch (operationType) {
			case "00":
				arithmeticFunction(jobString, jobId, registers, CPUNumber, programCache);
				break;
			case "01":
				conditionalBranchAndImmediateFunction(jobString, jobId, registers, CPUNumber, programCache);
				break;
			case "10":
				unconditionalJumpFunction(jobString, jobId, registers, CPUNumber, programCache);
				break;
			case "11":
				ioFunction(jobString, jobId, registers, CPUNumber, programCache);
				break;
		}
	}

	public static void arithmeticFunction(String jobString, String jobId, int[] registers, int CPUNumber, String[] programCache) {
		{
			//Breaks the code into separate chunks according to type
			String opCode = jobString.substring(2, 8);
			String binSReg1 = jobString.substring(8, 12);
			String binSReg2 = jobString.substring(12, 16);
			String binDReg = jobString.substring(16, 20);


			//Converts those chunks to decimal for inserting into array and math purpose
			int SReg1 = Integer.parseInt(binSReg1, 2);
			int SReg2 = Integer.parseInt(binSReg2, 2);
			int DReg = Integer.parseInt(binDReg, 2);
			int programCounter;

			switch (opCode) {
				case "000100":
					System.out.println("Moving register: " + DReg + " which contains " + registers[DReg] + " into " + SReg1 + " which contains " + registers[SReg1]);
					System.out.println();
					registers[SReg1] = registers[DReg];
					OS.programLines[CPUNumber] = OS.programLines[CPUNumber] + 1;
					programCounter = OS.pcb.getProgramCounter(jobId) + 1;
					OS.pcb.setProgramCounter(jobId, Integer.toString(programCounter));
					break;

				case "000101":
					System.out.println("Adding: " + registers[SReg1] + " located in: " + SReg1 + " to: " + registers[SReg2] + " located in: " + SReg2 + " adding these to register: " + DReg);
					System.out.println();
					registers[DReg] = registers[SReg1] + registers[SReg2];
					OS.programLines[CPUNumber] = OS.programLines[CPUNumber] + 1;
					programCounter = OS.pcb.getProgramCounter(jobId) + 1; //Increments the Program Counter
					OS.pcb.setProgramCounter(jobId, Integer.toString(programCounter));
					break;

				case "000110":
					System.out.println("Subtracting: " + registers[SReg1] + " located in: " + SReg1 + " from: " + registers[SReg2] + " located in: " + SReg2 + " adding these to register: " + DReg);
					System.out.println();
					registers[DReg] = registers[SReg1] - registers[SReg2];
					OS.programLines[CPUNumber] = OS.programLines[CPUNumber] + 1;
					programCounter = OS.pcb.getProgramCounter(jobId) + 1;
					OS.pcb.setProgramCounter(jobId, Integer.toString(programCounter));
					break;

				case "000111":
					System.out.println("Multiplying: " + registers[SReg1] + " located in: " + SReg1 + " to: " + registers[SReg2] + " located in: " + SReg2 + " adding these to register: " + DReg);
					System.out.println();
					registers[DReg] = registers[SReg1] * registers[SReg2];
					OS.programLines[CPUNumber] = OS.programLines[CPUNumber] + 1;
					programCounter = OS.pcb.getProgramCounter(jobId) + 1;
					OS.pcb.setProgramCounter(jobId, Integer.toString(programCounter));
					break;

				case "001000":
					System.out.println("Divide: " + registers[SReg1] + " located in: " + SReg1 + " to: " + registers[SReg2] + " located in: " + SReg2 + " adding these to register: " + DReg);
					System.out.println();
					registers[DReg] = registers[SReg1] / registers[SReg2];
					OS.programLines[CPUNumber] = OS.programLines[CPUNumber] + 1;
					programCounter = OS.pcb.getProgramCounter(jobId) + 1;
					OS.pcb.setProgramCounter(jobId, Integer.toString(programCounter));
					break;

				case "010000":
					if (registers[SReg1] < registers[SReg2]) {
						registers[DReg] = 1;
						OS.programLines[CPUNumber] = OS.programLines[CPUNumber] + 1;
						OS.PCBiterator(jobId);
					} else {
						registers[DReg] = 0;
						OS.programLines[CPUNumber] = OS.programLines[CPUNumber] + 1;
						OS.PCBiterator(jobId);
					}
					break;
			}
		}
	}

	public static void conditionalBranchAndImmediateFunction(String jobString, String jobId, int[] registers, int CPUNumber, String[] programCache) {
		String opCode = jobString.substring(2, 8);
		String binBReg = jobString.substring(8, 12);
		String binDReg = jobString.substring(12, 16);
		String binAddress = jobString.substring(16, 32);

		int BReg = Integer.parseInt(binBReg, 2);
		int DReg = Integer.parseInt(binDReg, 2);
		int Address = Integer.parseInt(binAddress, 2);
		int thePC;
		//int pointedAddress;

		switch (opCode) {
			case "000010":
				String RAMinsert = Integer.toHexString(registers[BReg]);
				while (RAMinsert.length() != 8) {
					RAMinsert = "0" + RAMinsert;
				}

				RAMinsert = "0x" + RAMinsert;
				System.out.println("Storing value from " + BReg + " into RAM slot " + registers[DReg] + " the value in Decimal is " + registers[BReg] + " but will be stored as hex value " + RAMinsert);
				System.out.println();
				int pointedAddress = registers[DReg] / 4;
				programCache[pointedAddress] = RAMinsert;


				OS.programLines[CPUNumber] = OS.programLines[CPUNumber] + 1;
				thePC = OS.pcb.getProgramCounter(jobId) + 1;
				OS.pcb.setProgramCounter(jobId, Integer.toString(thePC));
				break;
			case "000011":
				String contentOfAddress = programCache[(registers[BReg] / 4)].substring(2, 10);
				int contentOfAddressDec = Integer.parseInt(contentOfAddress, 16);
				System.out.println("Loading content of register " + BReg + "'s address which is address " + registers[BReg] + " which contains " + contentOfAddressDec + " into register " + DReg + ".");
				System.out.println();
				registers[DReg] = contentOfAddressDec;
				OS.programLines[CPUNumber] = OS.programLines[CPUNumber] + 1;
				thePC = OS.pcb.getProgramCounter(jobId) + 1;
				OS.pcb.setProgramCounter(jobId, Integer.toString(thePC));

				break;
			case "001011":
				System.out.println("Moving the data: " + Address + " Into register " + DReg);
				System.out.println();
				registers[DReg] = Address;
				thePC = OS.pcb.getProgramCounter(jobId) + 1;
				OS.programLines[CPUNumber] = OS.programLines[CPUNumber] + 1;
				OS.pcb.setProgramCounter(jobId, Integer.toString(thePC));
				break;
			case "001100":
				System.out.println("Adding the data: " + Address + " In register: " + DReg + " which holds: " + registers[DReg] + ".");
				System.out.println();
				registers[DReg] = Address + registers[DReg];
				OS.programLines[CPUNumber] = OS.programLines[CPUNumber] + 1;
				thePC = OS.pcb.getProgramCounter(jobId) + 1;
				OS.pcb.setProgramCounter(jobId, Integer.toString(thePC));
				break;
			case "001111":
				Address = Address / 4;
				String RAMString = programCache[Address].substring(2, 10); //Pulls hex value from RAM
				Address = Address * 4;
				int RAMint = Integer.parseInt(RAMString, 16); //Converts value to decimal
				System.out.println("Loading the RAM spot " + Address + " Which contains " + RAMint + " into register " + DReg + ".");
				System.out.println();
				registers[DReg] = Address;
				OS.programLines[CPUNumber] = OS.programLines[CPUNumber] + 1;
				thePC = OS.pcb.getProgramCounter(jobId) + 1;
				OS.pcb.setProgramCounter(jobId, Integer.toString(thePC));
				break;
			case "010101":
				if (registers[BReg] == registers[DReg]) {
					System.out.println("Register: " + BReg + " does equal " + DReg + " therefore skipping to instruction " + Address + ".");
					System.out.println();
					Address = Address / 4;
					OS.programLines[CPUNumber] = Address;

					//Moves to where the address told it to move
					thePC = Address + OS.pcb.getStartIndex(jobId);
					OS.pcb.setProgramCounter(jobId, Integer.toString(thePC));
				} else {
					System.out.println("Register: " + BReg + " does equal " + DReg + " therefore moving to next instruction.");
					System.out.println();
					OS.programLines[CPUNumber] = OS.programLines[CPUNumber] + 1;
					OS.PCBiterator(jobId);
				}
				break;
			case "010110":
				if (registers[BReg] != registers[DReg]) {
					System.out.println("Register: " + BReg + " does not equal " + DReg + " therefore skipping to instruction " + Address + ".");
					System.out.println();
					Address = Address / 4;
					OS.programLines[CPUNumber] = Address;

					//Moves to where the address told it to move
					thePC = Address + OS.pcb.getStartIndex(jobId);
					OS.pcb.setProgramCounter(jobId, Integer.toString(thePC));
				} else {
					System.out.println("Register: " + BReg + " DOES EQUAL " + DReg + " therefore moving to next instruction.");
					System.out.println();
					OS.programLines[CPUNumber] = OS.programLines[CPUNumber] + 1;
					OS.PCBiterator(jobId);
				}
				break;
		}
	}

	public static void unconditionalJumpFunction(String jobString, String jobId, int[] registers, int CPUNumber, String[] programCache) {
		String opCode = jobString.substring(2, 8);

		switch (opCode) {
			case "010010":
				System.out.println("Program has reached a Halt Command");
				System.out.println();

				int EndingLine = OS.pcb.getEndIndex(jobId) + 1;
				String EndingLinestring = Integer.toString(EndingLine);
				OS.programLines[CPUNumber] = EndingLine;
				OS.pcb.setProgramCounter(jobId, EndingLinestring);
		}
	}

	public static void ioFunction(String jobString, String jobId, int[] registers, int CPUNumber, String[] programCache) {
		OS.IOcount[(Integer.parseInt(jobId, 16) - 1)] = (Integer.parseInt(jobId, 16) - 1) + 1;
		String opCode = jobString.substring(2, 8);
		String binReg1 = jobString.substring(8, 12);
		String binReg2 = jobString.substring(12, 16);
		String binAddress = jobString.substring(16, 32);
		int Reg1 = Integer.parseInt(binReg1, 2);
		int Reg2 = Integer.parseInt(binReg2, 2);
		int Address = Integer.parseInt(binAddress, 2);
		Address = Address / 4;

		if ("000000".equals(opCode)) //Reads Instruction
		{
			if (Reg2 == 0) {
				//Hex value from RAM
				String RAMString = programCache[Address].substring(2, 10);

				//Converts to decimal
				int RAMint = Integer.parseInt(RAMString, 16);

				System.out.println("Reading data from RAM Slot " + Address + " to register " + Reg1);
				System.out.println();
				registers[Reg1] = RAMint;
				OS.programLines[CPUNumber] = OS.programLines[CPUNumber] + 1;

				OS.PCBiterator(jobId);
			} else {
				System.out.println("Reading data from register " + Reg2 + " to register " + Reg1);
				System.out.println();
				registers[Reg1] = registers[Reg2];
				OS.programLines[CPUNumber] = OS.programLines[CPUNumber] + 1;
				OS.PCBiterator(jobId);
			}
		} else if ("000001".equals(opCode)) //Write Instruction
		{
			if (Reg2 == 0) {
				String Reg1String = (registers[Reg1] + "");
				programCache[Address] = Reg1String;
				System.out.println(registers[Reg1]);
				System.out.println();
				OS.programLines[CPUNumber] = OS.programLines[CPUNumber] + 1;
				OS.PCBiterator(jobId);
			} else {
				registers[Reg2] = registers[Reg1];
				System.out.println(registers[Reg2]);
				System.out.println();
				OS.programLines[CPUNumber] = OS.programLines[CPUNumber] + 1;
				OS.PCBiterator(jobId);
			}
		}
	}

}
