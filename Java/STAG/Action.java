import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 * @version 1.0
 * @date 2021-05-21 11:30
 */
public class Action {

    /**
     * A set of possible "trigger" words (ANY of which can be used to initiate the action).
     */
    private List<String> triggers;

    /**
     * A set of "subjects" entities that are acted upon (ALL of which need to be present to perform the action).
     */
    private List<String> subjects;

    /**
     * A set of "consumed" entities that are all removed ("eaten up") by the action.
     */
    private List<String> consumed;

    /**
     * A set of "produced" entities that are all created ("generated") by the action.
     */
    private List<String> produced;

    /**
     * narration
     */
    private String narration;

    public Action(List<String> triggers, List<String> subjects, List<String> consumed, List<String> produced, String narration) {
        this.triggers = new ArrayList<>(triggers);
        this.subjects = new ArrayList<>(subjects);
        this.consumed = new ArrayList<>(consumed);
        this.produced = new ArrayList<>(produced);
        this.narration = narration;
    }

    /**
     * Check if command is in the trigger.
     *
     * @param command command
     * @return If it is, return true, else return false
     */
    public boolean verifyTrigger(String command) {
        return triggers.contains(command);
    }

    /**
     * Check that the player and the current location have all subjects
     *
     * @return ture or false
     */
    public boolean verifySubjects(Player player) {
        Location currentLocation = player.getCurrentLocation();
        for (String name : subjects) {
            // Not in the location.
            // Not in the player's inventory
            if (!currentLocation.containsEntity(name) && !player.hasEntity(name)) {
                return false;
            }
        }
        return true;
    }


    /**
     * Consume items from the player and the current location.
     * When a player's health runs out (i.e. reaches zero) they should lose all of the items
     * in their inventory (which are dropped in the location where they ran out of health)
     * and then they should return to the start location
     *
     * @param player player
     */
    public void perform(Player player, Map<String, Location> locationMap) {
        Location currentLocation = player.getCurrentLocation();
        // consume
        int consumeHeath = 0;
        for (String s : consumed) {
            if ("health".equals(s)) {
                consumeHeath += 1;
                continue;
            }
            // consume the item in the location first.
            if (currentLocation.removeEntity(s) == null) {
                // if not exist, then player.
                player.removeInventory(s);
            }
        }

        Location unplacedLocation = locationMap.get("unplaced");
        // produced
        int produceHeath = 0;
        for (String s : produced) {
            if ("health".equals(s)) {
                produceHeath += 1;
                continue;
            }
            // produce a path to the location
            Location location = locationMap.get(s);
            if(location != null){
                currentLocation.addPath(location);
                continue;
            }

            // entity
            AbstractEntity abstractEntity = unplacedLocation.removeEntity(s);
            if (abstractEntity instanceof Artefact) {
                player.addInventory((Artefact) abstractEntity);
            } else if (abstractEntity instanceof Character) {
                currentLocation.addEntity(AbstractEntity.CHARACTER_TYPE, abstractEntity);
            } else {
                currentLocation.addEntity(AbstractEntity.FURNITURE_TYPE, abstractEntity);
            }
        }

        // Consume health and produce health.
        while (consumeHeath > 0) {
            int currentHealth = player.decreaseHealth();
            // Drop items and reset status if the player dies.
            // return
            if (currentHealth == 0) {
                List<Artefact> remnant = player.reset();
                for (Artefact artefact : remnant) {
                    currentLocation.addEntity(AbstractEntity.ARTEFACT_TYPE, artefact);
                }
                return;
            }
            consumeHeath -= 1;
        }
        while (produceHeath > 0) {
            produceHeath -= 1;
            player.increaseHealth();
        }

    }


    public String getNarration() {
        return narration;
    }

}
