package org.example.auth;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

public class SheetsServiceInitializer {
    private Sheets sheetsService;

    public SheetsServiceInitializer() throws Exception {
        this.sheetsService = getSheetsService();
    }

    private Sheets getSheetsService() throws Exception {
        try {
            InputStream serviceAccountStream = SheetsServiceInitializer.class.getClassLoader().getResourceAsStream("TEST-java-api-key.json");
            if (serviceAccountStream == null) {
                throw new IOException("Service account key file not found.");
            }

            GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccountStream)
                    .createScoped(Collections.singleton("https://www.googleapis.com/auth/spreadsheets"));
            NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

            return new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, new HttpCredentialsAdapter(credentials))
                    .setApplicationName("Google Sheets Example")
                    .build();
        } catch (IOException e) {
            System.err.println("Failed to initialize the Sheets service: " + e.getMessage());
            throw e;
        }
    }

    public void testConnection(String spreadsheetId) {
        try {
            Spreadsheet spreadsheet = sheetsService.spreadsheets().get(spreadsheetId).execute();
            System.out.println("Successfully connected to the spreadsheet: " + spreadsheet.getProperties().getTitle());
        } catch (Exception e) {
            System.err.println("Failed to test connection to the spreadsheet: " + e.getMessage());
        }
    }

    public Sheets getSheets() {
        return this.sheetsService;
    }
}
