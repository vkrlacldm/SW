import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class UserManager {
    private static final String USER_FILE = "data/users.txt";
    private Map<String, String> users = new HashMap<>();
    private Map<String, String> names = new HashMap<>(); // Stores user names

    public UserManager() {
        loadUsers();
    }

    public boolean login(String userID, String password) throws IOException {
        return users.containsKey(userID) && users.get(userID).equals(password);
    }

    public boolean join(String userID, String password, String name) throws IOException {
        if (users.containsKey(userID)) {
            return false; // User already exists
        }
        users.put(userID, password);
        names.put(userID, name);
        saveUsers();
        return true;
    }

    private void loadUsers() {
        File file = new File(USER_FILE);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 3);
                if (parts.length == 3) {
                    users.put(parts[0], parts[1]);
                    names.put(parts[0], parts[2]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveUsers() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USER_FILE))) {
            for (Map.Entry<String, String> entry : users.entrySet()) {
                String userID = entry.getKey();
                String password = entry.getValue();
                String name = names.get(userID);
                writer.write(userID + "," + password + "," + name);
                writer.newLine();
            }
        }
    }

    public String getName(String userID) {
        return names.get(userID);
    }
}
