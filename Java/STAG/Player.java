import java.util.ArrayList;
import java.util.List;

/**
 * @author Administrator
 * @version 1.0
 * @date 2021-05-21 10:10
 */
public class Player extends AbstractEntity {

    /**
     * All of the artefacts currently being carried by the player.
     */
    private List<Artefact> inventories;

    /**
     * Where is the player now.
     */
    private Location currentLocation;

    /**
     * Player's initial location.
     */
    private final Location startLocation;

    /**
     * Each player should start with a health level of 3.
     * Consumption of "Poisons & Potions" or interaction with beneficial or
     * dangerous characters will increase or decrease a player's health.
     */
    private int healthLevel;

    public Player(String name, String description, Location startLocation) {
        super(name, description);
        this.startLocation = startLocation;
        currentLocation = startLocation;
        inventories = new ArrayList<>();
        healthLevel = 3;
    }

    /**
     * Puts a specified artefact into player's inventory
     *
     * @param artefact A specified artefact
     */
    public void addInventory(Artefact artefact) {
        inventories.add(artefact);
    }

    /**
     * Remove a specified artefact from the inventory.
     *
     * @param artefactName the name of artefact
     * @return artefact or null if it doesn't exist
     */
    public Artefact removeInventory(String artefactName) {
        for (int i = 0; i < inventories.size(); i++) {
            Artefact artefact = inventories.get(i);
            if (artefact.getName().equals(artefactName)) {
                inventories.remove(artefact);
                return artefact;
            }
        }
        return null;
    }

    /**
     * lists all of the artefacts currently being carried by the player.
     * For "inventory" (or "inv" for short)
     *
     * @return all of the artefacts currently being carried by the player.
     */
    public String listArtefacts() {
        StringBuilder sb = new StringBuilder();
        for (Artefact inventory : inventories) {
            sb.append(inventory.getName()).append(" ");
        }
        return sb.toString();
    }

    /**
     * Whether the player has a specific entity.
     *
     * @param name name
     * @return true or false
     */
    public boolean hasEntity(String name) {
        for (Artefact inventory : inventories) {
            if (inventory.getName().contains(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Consumption of "Poisons" or interaction with  dangerous characters will decrease a player's health.
     * When a player's health runs out (i.e. reaches zero) they should lose all of the items
     * in their inventory (which are dropped in the location where they ran out of health) and
     * then they should return to the start location.
     *
     * @return Health after decrease.
     */
    public int decreaseHealth() {
        healthLevel--;
//        if (healthLevel == 0) {
//            inventories.clear();
//            currentLocation = startLocation;
//        }
        return healthLevel;
    }

    /**
     * Consumption of "Potions" or interaction with beneficial characters will increase a player's health.
     *
     * @return Health after increase.
     */
    public int increaseHealth() {
        healthLevel++;
        return healthLevel;
    }

    /**
     * Reset the player status and drop everything.
     * @return
     */
    public List<Artefact> reset() {
        currentLocation = startLocation;
        List<Artefact> artefacts = new ArrayList<>(inventories);
        inventories.clear();
        healthLevel = 3;
        return artefacts;
    }

    public int getHealthLevel() {
        return healthLevel;
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
    }

}
