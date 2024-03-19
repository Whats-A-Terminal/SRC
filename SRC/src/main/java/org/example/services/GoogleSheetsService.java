package org.example.services;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.example.Farm;
import org.example.Crop;

public class GoogleSheetsService {
    private Sheets sheetsService;
    private String spreadsheetId;

    public GoogleSheetsService(Sheets sheetsService, String spreadsheetId) {
        this.sheetsService = sheetsService;
        this.spreadsheetId = spreadsheetId;
    }

    public List<Crop> getAllCrops(String range) {
        List<Crop> crops = new ArrayList<>();
        try {
            ValueRange response = sheetsService.spreadsheets().values()
                    .get(spreadsheetId, range)
                    .execute();
            List<List<Object>> values = response.getValues();
            if (values == null || values.isEmpty()) {
                System.out.println("No data found.");
            } else {
                for (List<Object> row : values) {
                    String farmName = (String) row.get(0);
                    String farmLocation = (String) row.get(1);
                    int cropID = Integer.parseInt((String) row.get(2));
                    String cropName = (String) row.get(3);
                    int quantityAvailable = Integer.parseInt((String) row.get(4));
                    String[] harvestDate = new String[]{(String) row.get(5)};
                    boolean inSeason = Boolean.parseBoolean((String) row.get(6));
                    Crop crop = new Crop(farmName, farmLocation, cropID, cropName, quantityAvailable, harvestDate, inSeason);
                    crops.add(crop);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return crops;
    }
}
