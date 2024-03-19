package org.example.auth;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

public class SheetsServiceInitializer {
    public Sheets getSheetsService() throws Exception {
        try {
            // Load the service account key file as a classpath resource
            InputStream serviceAccountStream = SheetsServiceInitializer.class.getClassLoader().getResourceAsStream("TEST-java-api-key.json");
            if (serviceAccountStream == null) {
                throw new IOException("Service account key file not found.");
            }

            // Create Google credentials from the service account key
            GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccountStream)
                    .createScoped(Collections.singleton("https://www.googleapis.com/auth/spreadsheets"));

            // Initialize HTTP transport and JSON factory
            NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

            // Build and return the Sheets service
            return new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, new HttpCredentialsAdapter(credentials))
                    .setApplicationName("Google Sheets Example")
                    .build();
        } catch (IOException e) {
            System.err.println("Failed to initialize the Sheets service: " + e.getMessage());
            throw e;
        }
    }
}
