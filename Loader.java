import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.StringTokenizer;

public class Loader {
	public static void loadFile() throws FileNotFoundException {

		String[] process = new String[15];

		File file = new File("src/Program-File.txt");
		Scanner scan = new Scanner(file);
		String line = new String();
		while (scan.hasNext()) {
			line += scan.nextLine();
			line += "\n";
		}

		StringTokenizer st = new StringTokenizer(line, "/ \n");
		int startIndex = 0;
		int endIndex = startIndex;
		int counter = 0;
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			switch (token) {
				case "JOB":
					process[0] = st.nextToken();
					OS.newQueue.enqueue(process[0]);
					process[1] = "new";
					process[2] = st.nextToken();
					process[3] = st.nextToken();
					break;

				case "Data":
					process[4] = st.nextToken();
					process[5] = st.nextToken();
					process[6] = st.nextToken();
					break;

				case "END":
					process[7] = Integer.toHexString(startIndex);
					process[8] = Integer.toHexString(endIndex);
					startIndex = endIndex + 1;
					OS.pcb.addProcess(process);
					process = new String[15];
					break;

				default:
					if (OS.disk[counter] == null && counter == 0) {
						OS.disk[counter] = token;
						counter++;
					} else if (OS.disk[counter] == null && counter > 0) {
						OS.disk[counter] = token;
						counter++;
						endIndex++;
					} else {
						System.out.println("Disk space is full.");

					}
					break;
			}
		}
	}
}
