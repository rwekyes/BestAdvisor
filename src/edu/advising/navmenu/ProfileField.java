package edu.advising.navmenu;

public class ProfileField {
    private final String label;
    private String value;
    private final boolean editable;

    public ProfileField(String label, String value, boolean editable){
        this.label = label;
        this.value = value;
        this.editable = editable;
    }

    public String getLabel() {
        return label;
    }

    public String getValue() {
        return value;
    }

    public boolean isEditable() {
        return editable;
    }
}
