import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Administrator
 * @version 1.0
 * @date 2021-05-21 10:07
 */
public class Location extends AbstractEntity {

    /**
     * Paths to other Locations (note: it is possible for paths to be one-way !)
     */
    private List<Location> paths;

    /**
     * Characters that are currently at a Location.
     */
    private List<Character> charactersList;

    /**
     * Artefacts that are currently present in a Location.
     */
    private List<Artefact> artefactsList;

    /**
     * Furniture that belongs in a Location
     */
    private List<Furniture> furnitureList;

    public Location(String name, String description) {
        super(name, description);
        paths = new ArrayList<>();
        charactersList = new ArrayList<>();
        artefactsList = new ArrayList<>();
        furnitureList = new ArrayList<>();
    }

    /**
     * Describes the entities in the current location.
     *
     * @return A string.
     */
    public String listAllEntities() {
        StringBuilder sb = new StringBuilder();
        for (Character character : charactersList) {
            sb.append(character.getDescription()).append("\n");
        }
        for (Artefact artefact : artefactsList) {
            sb.append(artefact.getDescription()).append("\n");
        }
        for (Furniture furniture : furnitureList) {
            sb.append(furniture.getDescription()).append("\n");
        }
        return sb.toString();
    }

    /**
     * All the locations we can go.
     * @return string of locations.
     */
    public String listPath(){
        StringBuilder sb = new StringBuilder();
        for (Location path : paths) {
            sb.append(path.getName()).append("\n");
        }
        return sb.toString();
    }

    public void addPath(Location target){
        paths.add(target);
    }

    /**
     * Go to a location.
     * @param locationName name of the destination.
     * @return location or null
     */
    public Location pathTo(String locationName){
        for (Location location : paths) {
            if(location.getName().equals(locationName)){
                return location;
            }
        }
        return null;
    }

    /**
     * Check whether this place has a certain entity.
     * @param entityName name
     * @return true or false
     */
    public boolean containsEntity(String entityName){
        for (Artefact artefact : artefactsList) {
            if(artefact.getName().equals(entityName)){
                return true;
            }
        }
        for (Furniture furniture : furnitureList) {
            if(furniture.getName().equals(entityName)){
                return true;
            }
        }
        for (Character character : charactersList) {
            if(character.getName().equals(entityName)){
                return true;
            }
        }
        return false;
    }


    /**
     * Remove an Entity from this location.
     * @param name name of the Entity.
     * @return The Entity.
     */
    public AbstractEntity removeEntity(String name){
        for (Artefact artefact : artefactsList) {
            if(artefact.getName().equals(name)){
                artefactsList.remove(artefact);
                return artefact;
            }
        }
        for (Furniture furniture : furnitureList) {
            if(furniture.getName().equals(name)){
                furnitureList.remove(furniture);
                return furniture;
            }
        }
        for (Character character : charactersList) {
            if(character.getName().equals(name)){
                charactersList.remove(character);
                return character;
            }
        }
        return null;
    }

    public void addEntity(String entityType, AbstractEntity abstractEntity) {
        switch (entityType) {
            case AbstractEntity.ARTEFACT_TYPE:
                artefactsList.add((Artefact) abstractEntity);
                break;
            case AbstractEntity.CHARACTER_TYPE:
                charactersList.add((Character) abstractEntity);
                break;
            case AbstractEntity.FURNITURE_TYPE:
                furnitureList.add((Furniture) abstractEntity);
                break;
        }
    }

}
