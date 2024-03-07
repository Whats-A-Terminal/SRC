package org.example;

public class Crop extends Farm{
    private final int cropID;
    private final String cropName;
    private int quantityAvailable;
    private Object[] harvestDate;
    private boolean inSeason;

    public Crop(int cropID, String cropName, int quantityAvailable,
                Object[] harvestDate, boolean inSeason){
        super();
        this.cropID = cropID;
        this.cropName = cropName;
        this.quantityAvailable = quantityAvailable;
        this.harvestDate = harvestDate;
        this.inSeason = inSeason;
    }

    public Crop(String farmName, String farmLocation, int cropID,
                String cropName, int quantityAvailable, Object[] harvestDate,
                boolean inSeason){
        super(farmName, farmLocation);
        this.cropID = cropID;
        this.cropName = cropName;
        this.quantityAvailable = quantityAvailable;
        this.harvestDate = harvestDate;
        this.inSeason = inSeason;
    }
}
