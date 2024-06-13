import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class Diary implements CUDROperations<String> {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private String getFileName(String userID) {
        return "data/diary_" + userID + ".txt";
    }

    @Override
    public void create(String entry) throws IOException {
        String[] parts = entry.split(",", 3);
        if (parts.length < 3) {
            throw new IllegalArgumentException("잘못된 입력 형식입니다. 올바른 형식: 날짜,내용,사용자ID");
        }
        String dateStr = parts[0].trim();
        String content = parts[1].trim();
        String userID = parts[2].trim();

        String fileName = getFileName(userID);
        Map<String, String> entries = readEntries(fileName);
        
        if (entries.containsKey(dateStr)) {
            throw new IllegalArgumentException("이 날짜에 이미 다이어리가 있습니다. 업데이트해 주세요.");
        }
        entries.put(dateStr, content);
        writeEntries(entries, fileName);
    }

    @Override
    public void update(String entry) throws IOException {
        String[] parts = entry.split(",", 3);
        if (parts.length < 3) {
            throw new IllegalArgumentException("잘못된 입력 형식입니다. 올바른 형식: 날짜,내용,사용자ID");
        }
        String dateStr = parts[0].trim();
        String content = parts[1].trim();
        String userID = parts[2].trim();

        String fileName = getFileName(userID);
        Map<String, String> entries = readEntries(fileName);

        entries.put(dateStr, content);
        writeEntries(entries, fileName);
    }

    @Override
    public String read(Date date, String userID) throws IOException {
        String dateStr = DATE_FORMAT.format(date);
        String fileName = getFileName(userID);
        Map<String, String> entries = readEntries(fileName);

        return entries.getOrDefault(dateStr, "해당 날짜의 다이어리 항목을 찾을 수 없습니다.");
    }

    @Override
    public void delete(Date date, String userID, String description) throws IOException {
        String dateStr = DATE_FORMAT.format(date);
        String fileName = getFileName(userID);
        Map<String, String> entries = readEntries(fileName);

        if (entries.remove(dateStr) != null) {
            writeEntries(entries, fileName);
        }
    }

    private Map<String, String> readEntries(String fileName) throws IOException {
        Map<String, String> entries = new HashMap<>();
        File file = new File(fileName);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            file.createNewFile();
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 2);
                if (parts.length == 2) {
                    entries.put(parts[0], parts[1]);
                }
            }
        }
        return entries;
    }

    private void writeEntries(Map<String, String> entries, String fileName) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (Map.Entry<String, String> entry : entries.entrySet()) {
                writer.write(entry.getKey() + "," + entry.getValue());
                writer.newLine();
            }
        }
    }
}
