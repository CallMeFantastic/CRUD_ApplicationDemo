import java.util.List;

public class PartialOrder {
    private String id;
    private int qty;
    private String notes;
    private List <String> addons;

    public void setId(String id) {
        this.id = id;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setAddons(List<String> addons) {
        this.addons = addons;
    }

    public String getId() {
        return id;
    }

    public int getQty() {
        return qty;
    }

    public String getNotes() {
        return notes;
    }

    public List<String> getAddons() {
        return addons;
    }
}
