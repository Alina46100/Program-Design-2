import java.io.Serializable;
import java.util.ArrayList;

public class Indexer implements Serializable {
    private static final long serialVersionUID = 1L;
    private ArrayList<String> content;
    private String name;

    public Indexer(ArrayList<String> content) {
        this.name = name;
        this.content = content;
    }

    // Getters and setters (如果需要)
    public ArrayList<String> getContent() {
        return content;
    }

    public void setContent(ArrayList<String> content) {
        this.content = content;
    }

}
