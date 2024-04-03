package org.example;

public class Crop extends Farm{
    private final int cropID; // Final because cropID cannot be changed (used for searching in database).
    private String cropName; // The name of the crop.
    private int quantityAvailable; // How much of the crop is on-hand.
    private int cropChanges; // Whether the crop has changed states (compared to the database).
    private String harvestDate; // When the crop was harvested.
    private boolean inSeason; // Whether the crop is currently in season.
    private String sheetName; // What Google Sheet is this crop under (such as Sheet1, Sheet2, etc.).
    private String sheetID; // The ID of the Google Sheet that this crop is under (such as 0, 1, 500, etc.).


    /**
     * Generic Constructor.
     * No reason for it to be here, but might need it in the future?
     * */
    public Crop() {
        super(); // Call to the superclass default constructor, if it exists
        this.cropID = -1; // Example default value, assuming ID is required. Adjust as necessary.
        this.sheetName = "";
        this.sheetID = "";
        this.cropName = "";
        this.quantityAvailable = 0;
        this.harvestDate = "";
        this.inSeason = false;
        this.cropChanges = 0; // Set to 0 as Main.java will change this later.
    }


    /**
     * Constructor for the Crop Object.
     * Sets the Crop object with all the appropriate values (from or to the database).
     *
     * @param farmName What farm is the Crop under.
     * @param farmLocation Where said farm is located.
     * @param cropID A unique identifier for this specific Crop.
     * @param cropName The name of the Crop.
     * @param quantityAvailable How many of the crop is currently on hand.
     * @param harvestDate When the Crop was harvested (presented in a format valid for Google Sheets)
     * @param inSeason A boolean that indicates whether the Crop is currently in season.
     * @param sheetName What Google Sheet the Crop is under.
     * @param sheetID The numeric identifier for the Google Sheet (Ex: 0 = Sheet1, 1 = Sheet2, etc.).
     *                Used as a backup to sheetName in the event that sheetName changes.
     * */
    public Crop(String farmName, String farmLocation, int cropID, String cropName, int quantityAvailable, String harvestDate, boolean inSeason, String sheetName, String sheetID) {
        super(farmName, farmLocation);
        this.cropID = cropID;
        this.cropName = cropName;
        this.quantityAvailable = quantityAvailable;
        this.harvestDate = harvestDate;
        this.inSeason = inSeason;
        this.sheetName = sheetName;
        this.sheetID = sheetID;
        this.cropChanges = 0; // Set to 0 as Main.java will change this later.
    }

    // Getters

    /**
     * Returns to the user what the ID of the Crop is.
     * This is typically used for database access.
     *
     * @return cropID the Crops identifier within the database.
     * */
    public int getCropID() {
        return cropID;
    }

    /**
     * Returns to the user what sheet the Crop is under.
     * This is quicker than sheetID, but more prone to failure
     * as sheetName can change, while sheetID can't.
     *
     * @return sheetName the sheet that Crop is under.
     * */
    public String getSheetName(){return sheetName;}

    /**
     * Returns the SheetID for the given Crop.
     * Should be used as a backup in the event sheetName is unavailable.
     *
     * @return sheetID the ID of sheetName*/
    public String getSheetID(){return sheetID;}

    /**
     * Returns the name of the Crop object.
     *
     * @return cropName the name of the Crop object.
     * */
    public String getCropName() {
        return cropName;
    }

    /**
     * Returns how many of the Crop is currently available.
     *
     * @return quantityAvailable How many of said Crop is available currently.
     * */
    public int getQuantityAvailable() {
        return quantityAvailable;
    }

    /**
     * Returns the current position of the Crop in relation to the database.
     * 0 indicates that no change has happened compared to the database.
     * 1 indicates that the crop has just been created, but not in the database.
     * 2 indicates that the crop has been modified from its database form (but not pushed).
     * 3 indicates that the crop has been deleted, but not changed in the database yet.
     *
     * @return cropChanges The current relation to the database.
     * */
    public int getCropChanges(){ return cropChanges;}

    /**
     * Returns the harvest date for the Crop.
     * Will be in the format that best suits sheets.v4.*
     *
     * @return harvestDate When the Crop was harvested.
     * */
    public String getHarvestDate() {return harvestDate;}

    /**
     * Returns whether the current Crop is in season.
     *
     * @return inSeason Whether the Crop is currently being harvested.
     * */
    public boolean isInSeason() {return inSeason;}

    // Setters
    // Note: No setter for cropID because they are final

    /**
     * Changes the name of the Crop object.
     *
     * @param cropName The new name of the Crop object.
     * */
    public void setCropName(String cropName){this.cropName = cropName;}

    /**
     * Changes the current quantity of the Crop available on hand.
     *
     * @param quantityAvailable The new quantity of the Crop on hand.
     * */
    public void setQuantityAvailable(int quantityAvailable) {
        this.quantityAvailable = quantityAvailable;
    }

    /**
     * Method to set whether the crop has been modified for not.
     * 0 indicates that no change has happened compared to the database.
     * 1 indicates that the crop has just been created, but not in the database.
     * 2 indicates that the crop has been modified from its database form (but not pushed).
     * 3 indicates that the crop has been deleted, but not changed in the database yet.
     *
     * @param cropChanges Sets the condition of the Crop object in relation to the database.
     * */
    public void setCropChanges(int cropChanges){this.cropChanges = cropChanges;}

    /**
     * Changes the harvest date of the Crop.
     *
     * @param harvestDate Changes the harvest date of the Crop.
     * */
    public void setHarvestDate(String harvestDate) {this.harvestDate = harvestDate;}

    /**
     * Changes whether the Crop is currently harvestable.
     *
     * @param inSeason Sets the value of the Crop to harvestable or not.
     * */
    public void setInSeason(boolean inSeason) {
        this.inSeason = inSeason;
    }

    /**
     * Sets the Sheet Identifier for the Crop.
     *
     * @param sheetID The current sheet (ID) that the Crop is under.
     * */
    public void setSheetID(String sheetID){this.sheetID = sheetID;}

    /**
     * Sets what Sheet the Crop is under.
     *
     * @param sheetName The Google Sheet that the Crop is under.
     * */
    public void setSheetName(String sheetName){this.sheetName = sheetName;}
}
