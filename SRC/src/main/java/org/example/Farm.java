package org.example;

/**
 * Represents a farm with basic properties such as name and location.
 * This class serves as a foundation for more specific farm-related entities, allowing
 * for the creation and manipulation of farm objects within the system. It provides
 * essential information that can be inherited by other classes, such as Crop, which extends
 * Farm to include more detailed attributes specific to agricultural crops.
 */
public class Farm {
    private String farmName;       // The name of the farm
    private String farmLocation;   // The geographical location of the farm

    /**
     * Constructs a new Farm instance with default values.
     * This constructor initializes a farm object with empty strings for both
     * the farm name and location, indicating an unspecified or unknown farm entity.
     */
    public Farm() {
        this.farmName = "";
        this.farmLocation = "";
    }

    /**
     * Constructs a new Farm instance with the specified name and location.
     * This constructor allows for the creation of a farm object with defined properties,
     * providing a more descriptive representation of the farm.
     *
     * @param farmName The name of the farm.
     * @param farmLocation The geographical location of the farm.
     */
    public Farm(String farmName, String farmLocation) {
        this.farmName = farmName;
        this.farmLocation = farmLocation;
    }

    // Getters

    /**
     * Retrieves the name of the farm.
     *
     * @return A string representing the name of the farm.
     */
    public String getFarmName() {
        return this.farmName;
    }

    /**
     * Retrieves the location of the farm.
     *
     * @return A string representing the geographical location of the farm.
     */
    public String getFarmLocation() {
        return this.farmLocation;
    }

    // Setters

    /**
     * Sets the name of the farm.
     * This method allows for changing the farm's name after the object has been created,
     * enabling dynamic updates to the farm's identifying information.
     *
     * @param farmName The new name to be assigned to the farm.
     */
    public void setFarmName(String farmName) {
        this.farmName = farmName;
    }

    /**
     * Sets the location of the farm.
     * This method allows for changing the farm's location after the object has been created,
     * enabling updates to the farm's geographical information to reflect changes or corrections.
     *
     * @param farmLocation The new location to be assigned to the farm.
     */
    public void setFarmLocation(String farmLocation) {
        this.farmLocation = farmLocation;
    }


}
