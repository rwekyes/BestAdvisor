package edu.advising.navmenu;

public class NavItem {
    private String label;
    private String sceneKey;
    private String iconPath;
    public NavItem(String label, String sceneKey, String iconPath){
        this.label = label;
        this.sceneKey = sceneKey;
        this.iconPath = iconPath;
    }

}
