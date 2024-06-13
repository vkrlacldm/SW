import java.io.IOException;
import java.util.Date;

public interface CUDROperations<T> {
    void create(T entry) throws IOException;
    void update(T entry) throws IOException;
    T read(Date date, String userID) throws IOException;
    void delete(Date date, String userID, String description) throws IOException;
}
