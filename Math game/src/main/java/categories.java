import java.util.ArrayList;
import java.util.List;

public class categories {

    private List<String> list = new ArrayList<>();

    public void add(String c) {
        if (!list.contains(c)) {
            list.add(c);
        }
    }

    public boolean contains(String c) {
        return list.contains(c);
    }

    public int size() {
        return list.size();
    }

    public String get(int index) {
        return list.get(index);
    }

    public List<String> getAll() {
        return list;
    }
}
