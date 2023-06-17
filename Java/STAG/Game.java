import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 * @version 1.0
 * @date 2021-05-22 22:36
 */
public class Game {
    private final Map<String, Location> locationsMap;
    //    private final Map<String, Player> players;
    private final List<Action> actions;

    private Player player;

    public Game(String entityFilename, String actionFilename) {
        locationsMap = MyParser.graphParse(entityFilename);
        actions = MyParser.actionParse(actionFilename);
        player = null;
    }

    /**
     * "get": picks up a specified artefact from current location and puts it into player's inventory
     *
     * @param player   a player
     * @param commands commands
     * @return result
     */
    public String processGet(Player player, List<String> commands) {
        Location currentLocation = player.getCurrentLocation();
        StringBuilder result = new StringBuilder("You picked up a ");
        boolean get = false;
        for (int i = 1; i < commands.size(); i++) {
            // remove it from this locations
            Artefact artefact = (Artefact) currentLocation.removeEntity(commands.get(i));
            if (artefact != null) {
                // put it into player's Inventory.
                player.addInventory(artefact);
                result.append(artefact.getName()).append(" ");
                get = true;
            }
        }
        if (get) {
            result.deleteCharAt(result.length() - 1);
            return result.toString();
        } else {
            return "No such artefact.";
        }

    }

    /**
     * "drop": puts down an artefact from player's inventory and places it into the current location
     *
     * @param player   player
     * @param commands commands
     * @return result
     */
    private String processDrop(Player player, List<String> commands) {
        String artefactName = commands.get(1);
        // remove it from player's Inventory.
        Artefact artefact = player.removeInventory(artefactName);
        if (artefact != null) {
            player.getCurrentLocation().addEntity(AbstractEntity.ARTEFACT_TYPE, artefact);
            return "You dropped a " + artefactName;
        } else {
            return "You don't have " + artefactName;
        }
    }

    /**
     * "look": describes the entities in the current location and lists the paths to other locations
     *
     * @param player player
     * @return result
     */
    private String processLook(Player player) {
        Location currentLocation = player.getCurrentLocation();
        return "You are in " + currentLocation.getDescription() + ". You can see:\n" +
                currentLocation.listAllEntities() +
                "You can access from here:\n" +
                currentLocation.listPath();
    }

    /**
     * goto": moves from one location to another (if there is a path between the two)
     *
     * @param player   player
     * @param commands another locations
     * @return result
     */
    private String processGoto(Player player, List<String> commands) {
        String locationName = commands.get(1);
        Location currentLocation = player.getCurrentLocation();
        Location destination = currentLocation.pathTo(locationName);
        if (destination == null) {
            return "Can't reach " + locationName;
        }
        player.setCurrentLocation(destination);
        return processLook(player);
    }

    /**
     * you should also add a new health command keyword that reports back the player's current health level
     * (so the player can keep track of it).
     *
     * @param player player
     * @return health level
     */
    private String processHealth(Player player) {
        int healthLevel = player.getHealthLevel();
        return healthLevel + "\n";
    }

    /**
     * accept ANY of the trigger keywords from the loaded-in game actions file.
     *
     * @param player   player
     * @param commands commands
     * @return result
     */
    private String actionTrigger(Player player, List<String> commands) {
        // You should first verify that the conditions hold to perform the action
        // verify trigger words and Subjects
        Action action = null;
        // a specific action
        for (Action tmpAction : actions) {
            boolean condition = false;
            // all words
            for (String command : commands) {
                if (tmpAction.verifyTrigger(command) && tmpAction.verifySubjects(player)) {
                    condition = true;
                    break;
                }
            }
            if (condition) {
                action = tmpAction;
                break;
            }
        }
        if (action == null) {
            return "Unrecognized command.\n";
        }
        // Perform the action
        action.perform(player, locationsMap);

        return action.getNarration() + "\n";

    }

    public String service(String message) {
        try {
            String[] nameCommand = message.split(":");
            // get player
            String playerName = nameCommand[0];
            if(player == null){
                player = new Player(playerName, playerName, locationsMap.get(MyParser.START_LOCATION));
            }
//            Player player = players.get(playerName);
//            if (player == null) {
//                player = new Player(playerName, playerName, locationsMap.get(MyParser.START_LOCATION));
//                players.put(playerName, player);
//            }
            // Reorganization order.
            List<String> commands = new ArrayList<>();
            for (int i = 1; i < nameCommand.length; i++) {
                String[] commandString = nameCommand[i].trim().split("\\s+");
                commands.addAll(Arrays.asList(commandString));
            }
            // Execute a command.
            String command = commands.get(0);
            if ("inventory".equals(command) || "inv".equals(command)) {
                return player.listArtefacts();
            } else if ("get".equals(command)) {
                return processGet(player, commands);
            } else if ("drop".equals(command)) {
                return processDrop(player, commands);
            } else if ("goto".equals(command)) {
                return processGoto(player, commands);
            } else if ("look".equals(command)) {
                return processLook(player);
            } else if ("health".equals(command)) {
                return processHealth(player);
            } else {
                // In addition to the standard "built-in" commands, your game engine should also
                // accept ANY of the trigger keywords from the loaded-in game actions file.
                return actionTrigger(player, commands);
            }
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

}