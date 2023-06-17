/**
 * @author Administrator
 * @version 1.0
 * @date 2021-05-21 10:02
 */
public abstract class AbstractEntity {

    public static final String ARTEFACT_TYPE = "artefacts";
    public static final String CHARACTER_TYPE = "characters";
    public static final String FURNITURE_TYPE = "furniture";

    /**
     * The name of this entity.
     */
    private String name;

    /**
     * The description of this entity.
     */
    private String description;

    public AbstractEntity(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
