import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class Schedule implements CUDROperations<String[]> {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private String getFileName(String userID) {
        return "data/schedules_" + userID + ".txt";
    }

    @Override
    public void create(String[] details) throws IOException {
        if (details.length < 4) {
            throw new IllegalArgumentException("잘못된 입력 형식입니다. 올바른 형식: 날짜,설명,위치,사용자ID");
        }
        String dateStr = details[0].trim();
        String description = details[1].trim();
        String location = details[2].trim();
        String userID = details[3].trim();

        String fileName = getFileName(userID);
        Map<String, List<String[]>> schedules = readEntries(fileName);

        // Check if an entry with the same description exists
        List<String[]> entriesForDate = schedules.get(dateStr);
        if (entriesForDate != null) {
            for (String[] entry : entriesForDate) {
                if (entry[0].equals(description)) {
                    throw new IllegalArgumentException("이 설명을 가진 스케줄이 이미 존재합니다.");
                }
            }
        }

        schedules.putIfAbsent(dateStr, new ArrayList<>());
        schedules.get(dateStr).add(new String[]{description, location});

        writeEntries(schedules, fileName);
    }

    @Override
    public void update(String[] details) throws IOException {
        if (details.length < 4) {
            throw new IllegalArgumentException("잘못된 입력 형식입니다. 올바른 형식: 날짜,설명,위치,사용자ID");
        }
        String dateStr = details[0].trim();
        String description = details[1].trim();
        String location = details[2].trim();
        String userID = details[3].trim();

        String fileName = getFileName(userID);
        Map<String, List<String[]>> schedules = readEntries(fileName);

        List<String[]> entriesForDate = schedules.get(dateStr);
        if (entriesForDate != null) {
            boolean updated = false;
            for (String[] entry : entriesForDate) {
                if (entry[0].equals(description)) {
                    entry[1] = location;
                    updated = true;
                    break;
                }
            }
            if (!updated) {
                throw new IllegalArgumentException("수정할 설명을 가진 스케줄을 찾을 수 없습니다.");
            }
        } else {
            throw new IllegalArgumentException("해당 날짜의 스케줄을 찾을 수 없습니다.");
        }

        writeEntries(schedules, fileName);
    }

    @Override
    public String[] read(Date date, String userID) throws IOException {
        String dateStr = DATE_FORMAT.format(date);
        String fileName = getFileName(userID);
        Map<String, List<String[]>> schedules = readEntries(fileName);
        List<String[]> detailsList = schedules.get(dateStr);

        if (detailsList != null && !detailsList.isEmpty()) {
            String[] descriptions = new String[detailsList.size()];
            for (int i = 0; i < detailsList.size(); i++) {
                String[] details = detailsList.get(i);
                descriptions[i] = (i + 1) + ". " + details[0];
            }
            return descriptions;
        }
        return new String[]{"해당 날짜의 스케줄을 찾을 수 없습니다."};
    }

    @Override
    public void delete(Date date, String userID, String description) throws IOException {
        String dateStr = DATE_FORMAT.format(date);
        String fileName = getFileName(userID);
        Map<String, List<String[]>> schedules = readEntries(fileName);

        List<String[]> entriesForDate = schedules.get(dateStr);
        if (entriesForDate != null) {
            boolean deleted = false;
            for (Iterator<String[]> iterator = entriesForDate.iterator(); iterator.hasNext(); ) {
                String[] entry = iterator.next();
                if (entry[0].equals(description)) {
                    iterator.remove();
                    deleted = true;
                    break;
                }
            }
            if (deleted) {
                if (entriesForDate.isEmpty()) {
                    schedules.remove(dateStr);
                }
                writeEntries(schedules, fileName);
            } else {
                throw new IllegalArgumentException("삭제할 설명을 가진 스케줄을 찾을 수 없습니다.");
            }
        } else {
            throw new IllegalArgumentException("해당 날짜의 스케줄을 찾을 수 없습니다.");
        }
    }

    private Map<String, List<String[]>> readEntries(String fileName) throws IOException {
        Map<String, List<String[]>> schedules = new HashMap<>();
        File file = new File(fileName);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            file.createNewFile();
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 3);
                if (parts.length == 3) {
                    schedules.putIfAbsent(parts[0], new ArrayList<>());
                    schedules.get(parts[0]).add(Arrays.copyOfRange(parts, 1, parts.length));
                }
            }
        }
        return schedules;
    }

    private void writeEntries(Map<String, List<String[]>> schedules, String fileName) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (Map.Entry<String, List<String[]>> entry : schedules.entrySet()) {
                for (String[] details : entry.getValue()) {
                    writer.write(entry.getKey() + "," + String.join(",", details));
                    writer.newLine();
                }
            }
        }
    }
}
