import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

class StagServer {
    /**
     * Player name and game.
     * Every player has his own game.
     */
    private final Map<String, Game> games;
    private final String entityFilename;
    private final String actionFilename;

    public static void main(String args[]) {
        if (args.length != 2) System.out.println("Usage: java StagServer <entity-file> <action-file>");
        else new StagServer(args[0], args[1], 8888);
    }

    public StagServer(String entityFilename, String actionFilename, int portNumber) {
        games = new HashMap<>();
        this.entityFilename = entityFilename;
        this.actionFilename = actionFilename;
        try {
            ServerSocket ss = new ServerSocket(portNumber);
            System.out.println("Server Listening");
            while (true) {
                acceptNextConnection(ss);
            }
        } catch (IOException ioe) {
            System.err.println(ioe);
        }
    }

    private void acceptNextConnection(ServerSocket ss) {
        try {
            // Next line will block until a connection is received
            Socket socket = ss.accept();
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            processNextCommand(in, out);
            out.close();
            in.close();
            socket.close();
        } catch (IOException ioe) {
            System.err.println(ioe);
        }
    }

    private void processNextCommand(BufferedReader in, BufferedWriter out) throws IOException {
        String line = in.readLine();
        // One game per player.
        String name = line.split(":")[0];
        Game game = games.get(name);
        if (game == null) {
            game = new Game(entityFilename, actionFilename);
            games.put(name, game);
        }
        String result = game.service(line);
        out.write(result);
    }
}
