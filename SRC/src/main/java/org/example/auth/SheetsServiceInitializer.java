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

/**
 * Initializes and provides a Google Sheets service instance.
 * This class encapsulates the configuration and creation of a Google Sheets API service object,
 * handling authentication and providing a ready-to-use service instance for interacting with Google Sheets.
 * It utilizes OAuth 2.0 for authentication, ensuring secure access to Google Sheets based on the
 * credentials provided.
 * <p>
 * Usage example:
 * SheetsServiceInitializer initializer = new SheetsServiceInitializer();
 * Sheets sheetsService = initializer.getSheetsService();
 */
public class SheetsServiceInitializer {
    private Sheets sheetsService;


    /**
     * Constructor for SheetsServiceInitializer.
     * Initializes the Sheets service upon instantiation of the class.
     *
     * @throws Exception if there is an issue initializing the Google Sheets service,
     *                   including problems with file access, network issues, or API errors.
     */
    public SheetsServiceInitializer() throws Exception {
        this.sheetsService = getSheetsService();
    }


    /**
     * Creates and configures the Sheets service instance.
     * This method sets up the authentication credentials and the HTTP transport for the Sheets service.
     *
     * @return A Sheets service instance, ready for use.
     * @throws Exception if there are issues creating the Sheets service instance.
     */
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


    /**
     * Attempts to establish a connection to a specified Google Spreadsheet.
     * Useful for testing if the Sheets service has been initialized correctly and can access spreadsheets.
     *
     * @param spreadsheetId The unique identifier for the spreadsheet to test connection to.
     */
    public void testConnection(String spreadsheetId) {
        try {
            Spreadsheet spreadsheet = sheetsService.spreadsheets().get(spreadsheetId).execute();
            System.out.println("Successfully connected to the spreadsheet: " + spreadsheet.getProperties().getTitle());
        } catch (Exception e) {
            System.err.println("Failed to test connection to the spreadsheet: " + e.getMessage());
        }
    }


    /**
     * Provides the initialized Sheets service instance for use in other parts of the application.
     *
     * @return The Sheets service instance.
     */
    public Sheets getSheets() {
        return this.sheetsService;
    }
}
