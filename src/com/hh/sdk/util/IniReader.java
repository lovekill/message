package com.hh.sdk.util;

import java.io.BufferedReader;  
import java.io.File;  
import java.io.FileReader;  
import java.io.IOException;  
import java.util.ArrayList;  
import java.util.HashMap;  
import java.util.Iterator;  
import java.util.List;  
import java.util.Map;  
import java.util.Set; 

/* 
 * IniReader.java 
 * 用Java读取INI文件(带section的) 
 * 示例： 
 * tmp.IniReader reader = new tmp.IniReader("E:\\james\\win.ini"); 
 * out.println(reader.getValue("TestSect3", "kkk 6")); 
 */ 

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

public class IniReader {

	protected HashMap sections = new HashMap();
	private transient String currentSecion;
	private transient Properties current;

	public IniReader(String filename) throws IOException {
		File file = new File(filename);
		if(!file.exists()) {
			return;
		}
		BufferedReader reader = new BufferedReader(new FileReader(filename));
		read(reader);
		reader.close();
	}

	protected void read(BufferedReader reader) throws IOException {
		String line;
		while ((line = reader.readLine()) != null) {
			parseLine(line);
		}
	}

	protected void parseLine(String line) {
		line = line.trim();
		if (line.matches("\\[.*\\]")) {
			currentSecion = line.replaceFirst("\\[(.*)\\]", "$1");
			current = new Properties();
			sections.put(currentSecion, current);
		} else if (line.matches(".*=.*")) {
			if (current != null) {
				int i = line.indexOf('=');
				String name = line.substring(0, i);
				String value = line.substring(i + 1);
				current.setProperty(name, value);
			}
		}
	}

	public String getValue(String section, String name) {
		Properties p = (Properties) sections.get(section);

		if (p == null) {
			return null;
		}

		String value = p.getProperty(name);
		return value;
	}

}