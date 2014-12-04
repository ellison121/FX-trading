package com.test.ellison;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ReadCSV {

  public String process(String csvFile) throws IOException {
			String line;
            BufferedReader in;
			in = new BufferedReader(new FileReader(csvFile));
			StringBuilder sb = new StringBuilder();
			while ((line = in.readLine()) != null){
				//System.out.println(line);
				sb.append(line);
				sb.append("\r\n");
 			}
			in.close();
			return sb.toString();			
  	}
}