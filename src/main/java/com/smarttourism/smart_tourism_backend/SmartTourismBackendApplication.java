package com.smarttourism.smart_tourism_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;

@SpringBootApplication
public class SmartTourismBackendApplication {

	public static void main(String[] args) {
		loadDotEnv();
		SpringApplication.run(SmartTourismBackendApplication.class, args);
	}

	private static void loadDotEnv() {
		File[] envFiles = new File[] {
				new File(".env"),
				new File("src/.env"),
				new File("src/main/resources/.env")
		};

		for (File file : envFiles) {
			if (file.exists() && file.isFile()) {
				try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
					String line;
					while ((line = reader.readLine()) != null) {
						line = line.trim();
						if (line.isEmpty() || line.startsWith("#") || !line.contains("=")) {
							continue;
						}
						int eqIdx = line.indexOf('=');
						String key = line.substring(0, eqIdx).trim();
						String value = line.substring(eqIdx + 1).trim();
						if ((value.startsWith("\"") && value.endsWith("\"")) ||
								(value.startsWith("'") && value.endsWith("'"))) {
							value = value.substring(1, value.length() - 1);
						}
						if (System.getProperty(key) == null && System.getenv(key) == null) {
							System.setProperty(key, value);
						}
					}
				} catch (Exception ignored) {
				}
			}
		}
	}

}
