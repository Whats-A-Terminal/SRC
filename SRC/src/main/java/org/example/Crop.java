package org.example;

public class Crop extends Farm{
    private final int cropID;
    private String sheetName;
    private String sheetID;
    private String cropName;
    private int quantityAvailable;
    private String harvestDate;
    private boolean inSeason;

    public Crop() {
        super(); // Call to the superclass default constructor, if it exists
        this.cropID = -1; // Example default value, assuming ID is required. Adjust as necessary.
        this.sheetName = "";
        this.sheetID = "";
        this.cropName = "";
        this.quantityAvailable = 0;
        this.harvestDate = "";
        this.inSeason = false;
    }

    public Crop(int cropID, String cropName, int quantityAvailable, String harvestDate, boolean inSeason, String sheetName, String sheetID) {
        super("unknown", "unknown");
        this.cropID = cropID;
        this.cropName = cropName;
        this.quantityAvailable = quantityAvailable;
        this.harvestDate = harvestDate;
        this.inSeason = inSeason;
        this.sheetName = sheetName;
        this.sheetID = sheetID;
    }

    public Crop(String farmName, String farmLocation, int cropID, String cropName, int quantityAvailable, String harvestDate, boolean inSeason, String sheetName, String sheetID) {
        super(farmName, farmLocation);
        this.cropID = cropID;
        this.cropName = cropName;
        this.quantityAvailable = quantityAvailable;
        this.harvestDate = harvestDate;
        this.inSeason = inSeason;
        this.sheetName = sheetName;
        this.sheetID = sheetID;
    }

    // Getters
    public int getCropID() {
        return cropID;
    }

    public String getSheetName(){return sheetName;}

    public String getSheetID(){return sheetID;}

    public String getCropName() {
        return cropName;
    }

    public int getQuantityAvailable() {
        return quantityAvailable;
    }

    public String getHarvestDate() {
        return harvestDate;
    }

    public boolean isInSeason() {
        return inSeason;
    }

    // Setters
    // Note: No setter for cropID because they are final

    public void setCropName(String cropName){this.cropName = cropName;}

    public void setQuantityAvailable(int quantityAvailable) {
        this.quantityAvailable = quantityAvailable;
    }

    public void setHarvestDate(String harvestDate) {
        this.harvestDate = harvestDate;
    }

    public void setInSeason(boolean inSeason) {
        this.inSeason = inSeason;
    }

    public void setSheetID(String sheetID){this.sheetID = sheetID;}

    public void setSheetName(String sheetName){this.sheetName = sheetName;}
}
