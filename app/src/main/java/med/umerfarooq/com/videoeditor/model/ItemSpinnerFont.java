package med.umerfarooq.com.videoeditor.model;

public class ItemSpinnerFont {
    String name;
    String path;

    public ItemSpinnerFont(String name, String path) {
        this.name = name;
        this.path = path;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
