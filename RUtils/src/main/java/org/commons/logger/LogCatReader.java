package org.commons.logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class LogCatReader {

	public static String foo() {
		try {
			Process process = Runtime.getRuntime().exec("logcat -d");
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

			StringBuilder log = new StringBuilder();
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				log.append(line);
			}
			return log.toString();
		} catch (IOException e) {
			Ln.w("could not read logcat");
		}
		
		return "";
	}
}
