package med.umerfarooq.com.videoeditor.model;

public class ItemSpinnerColor {
    private int idColor;
    private String name;

    public ItemSpinnerColor(String name, int idColor) {
        this.name = name;
        this.idColor = idColor;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIdColor() {
        return this.idColor;
    }

    public void setIdColor(int idColor) {
        this.idColor = idColor;
    }
}
