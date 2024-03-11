package org.example;

public class Farm {
    private String farmName;
    private String farmLocation;

    public Farm(){
        this.farmName = "";
        this.farmLocation = "";
    }

    public Farm(String farmName, String farmLocation){
        this.farmName = farmName;
        this.farmLocation = farmLocation;
    }

    // Getters
    public String getFarmName(){
        return this.farmName;
    }

    public String getFarmLocation(){
        return this.farmLocation;
    }

    // Setters
    public void setFarmName(String farmName){
        this.farmName = farmName;
    }

    public void setFarmLocation(String farmLocation){
        this.farmLocation = farmLocation;
    }

}
