import com.alexmerz.graphviz.Parser;
import com.alexmerz.graphviz.objects.Edge;
import com.alexmerz.graphviz.objects.Graph;
import com.alexmerz.graphviz.objects.Node;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 * @version 1.0
 * @date 2021-05-22 23:26
 */
public class MyParser {
    /**
     * Used to mark the starting location.
     */
    public static final String START_LOCATION = "__START_LOCATION__";

    public static Map<String, Location> graphParse(String entityFilename) {
        Map<String, Location> map = new HashMap<>();
        try {
            Parser parser = new Parser();
            FileReader reader = new FileReader(entityFilename);
            parser.parse(reader);
            ArrayList<Graph> graphs = parser.getGraphs();
            ArrayList<Graph> subGraphs = graphs.get(0).getSubgraphs();
            boolean start = true;
            for (Graph g : subGraphs) {
                // locations
//                System.out.printf("id = %s\n", g.getId().getId());
                ArrayList<Graph> subGraphs1 = g.getSubgraphs();
                for (Graph g1 : subGraphs1) {
                    ArrayList<Node> nodesLoc = g1.getNodes(false);
                    Node nLoc = nodesLoc.get(0);
//                    System.out.printf("\tid = %s, name = %s\n", g1.getId().getId(), nLoc.getId().getId());
                    // id = cluster001, name = start
                    // Create a location first
                    // A Location corresponds to a g1, id is description, name is name.
                    String locationName = nLoc.getId().getId();
                    Location location = new Location(locationName, nLoc.getAttribute("description"));
                    ArrayList<Graph> subGraphs2 = g1.getSubgraphs();
                    for (Graph g2 : subGraphs2) {
//                        System.out.printf("\t\tid = %s\n", g2.getId().getId());
                        // id = artefacts
                        // A g2 corresponds to a category of things
                        String entityType = g2.getId().getId();
                        ArrayList<Node> nodesEnt = g2.getNodes(false);
                        for (Node nEnt : nodesEnt) {
//                            System.out.printf("\t\t\tid = %s, description = %s\n", nEnt.getId().getId(), nEnt.getAttribute("description"));
                            // id = potion, description = Magic potion
                            String name = nEnt.getId().getId(), description = nEnt.getAttribute("description");
                            AbstractEntity abstractEntity = null;
                            switch (entityType) {
                                case AbstractEntity.ARTEFACT_TYPE:
                                    abstractEntity = new Artefact(name, description);
                                    break;
                                case AbstractEntity.CHARACTER_TYPE:
                                    abstractEntity = new Character(name, description);
                                    break;
                                case AbstractEntity.FURNITURE_TYPE:
                                    abstractEntity = new Furniture(name, description);
                                    break;
                            }
                            location.addEntity(entityType, abstractEntity);
                        }
                    }
                    map.put(locationName, location);
                    if (start) {
                        map.put(START_LOCATION, location);
                        start = false;
                    }
                }

                ArrayList<Edge> edges = g.getEdges();
                for (Edge e : edges) {
//                    System.out.printf("Path from %s to %s\n", e.getSource().getNode().getId().getId(), e.getTarget().getNode().getId().getId());
                    String sourceName = e.getSource().getNode().getId().getId();
                    String targetName = e.getTarget().getNode().getId().getId();
                    Location source = map.get(sourceName);
                    Location target = map.get(targetName);
                    source.addPath(target);
                }
            }
        } catch (FileNotFoundException fnfe) {
            System.out.println(fnfe);
        } catch (com.alexmerz.graphviz.ParseException pe) {
            System.out.println(pe);
        }
        return map;
    }

    /**
     * Parse actions.
     *
     * @param actionFilename an action filename
     * @return actions.
     */
    public static List<Action> actionParse(String actionFilename) {
        List<Action> actions = new ArrayList<>();
        try {
            // parsing file actionFilename
            Object obj = new JSONParser().parse(new FileReader(actionFilename));
            // typecasting obj to JSONObject
            JSONObject jo = (JSONObject) obj;
            // actions json
            JSONArray actionsArray = (JSONArray) jo.get("actions");
            for (Object o : actionsArray) {
                // an action
                JSONObject actionJson = (JSONObject) o;
                List<String> triggers = new ArrayList<>();
                List<String> subjects = new ArrayList<>();
                List<String> consumed = new ArrayList<>();
                List<String> produced = new ArrayList<>();
                // triggers
                JSONArray triggersJson = (JSONArray) actionJson.get("triggers");
                for (Object o1 : triggersJson) {
                    triggers.add((String) o1);
                }

                // subjects
                JSONArray subjectsJson = (JSONArray) actionJson.get("subjects");
                for (Object o1 : subjectsJson) {
                    subjects.add((String) o1);
                }

                // consumed
                JSONArray consumedJson = (JSONArray) actionJson.get("consumed");
                for (Object o1 : consumedJson) {
                    consumed.add((String) o1);
                }

                // produced
                JSONArray producedJson = (JSONArray) actionJson.get("produced");
                for (Object o1 : producedJson) {
                    produced.add((String) o1);
                }

                // narration
                String narration = (String) actionJson.get("narration");
                Action action = new Action(triggers, subjects, consumed, produced, narration);
                actions.add(action);
            }
        } catch (FileNotFoundException fnfe) {
            System.out.println(fnfe);
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
        return actions;
    }

//    public static void main(String[] args) {
//        Map<String, Location> stringLocationMap = graphParse("basic-entities.dot");
//        List<Action> actions = actionParse("basic-actions.json");
//        return;
//    }
}
