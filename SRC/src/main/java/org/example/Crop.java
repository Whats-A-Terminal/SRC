package org.example;

public class Crop extends Farm{
    private final int cropID;
    private final String cropName;
    private int quantityAvailable;
    private String harvestDate;
    private boolean inSeason;

    public Crop(int cropID, String cropName, int quantityAvailable,
                String harvestDate, boolean inSeason){
        super("unknown", "unknown");
        this.cropID = cropID;
        this.cropName = cropName;
        this.quantityAvailable = quantityAvailable;
        this.harvestDate = harvestDate;
        this.inSeason = inSeason;
    }

    public Crop(String farmName, String farmLocation, int cropID,
                String cropName, int quantityAvailable, String harvestDate,
                boolean inSeason){
        super(farmName, farmLocation);
        this.cropID = cropID;
        this.cropName = cropName;
        this.quantityAvailable = quantityAvailable;
        this.harvestDate = harvestDate;
        this.inSeason = inSeason;
    }

    // Getters
    public int getCropID() {
        return cropID;
    }

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
    // Note: No setter for cropID and cropName because they are final
    public void setQuantityAvailable(int quantityAvailable) {
        this.quantityAvailable = quantityAvailable;
    }

    public void setHarvestDate(String harvestDate) {
        this.harvestDate = harvestDate;
    }

    public void setInSeason(boolean inSeason) {
        this.inSeason = inSeason;
    }
}
