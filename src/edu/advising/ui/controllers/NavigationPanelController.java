package edu.advising.ui.controllers;

import edu.advising.navmenu.*;
import edu.advising.navmenu.NavItem;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

public class NavigationPanelController {

    @FXML
    private ListView<NavItem> navListView;

    @FXML
    public void initialize() {


        String role = "ADMIN";

        NavMenuFactory factory;

        switch (role) {

            case "STUDENT":
                factory = new StudentNavMenuFactory();
                break;

            case "FACULTY":
                factory = new FacultyNavMenuFactory();
                break;

            default:
                factory = new AdminNavMenuFactory();
        }

        navListView.setItems(
                FXCollections.observableArrayList(
                        factory.createMenu()
                )
        );

        navListView.setOnMouseClicked(event -> {

            NavItem selected =
                    navListView.getSelectionModel().getSelectedItem();

            if (selected != null) {

                System.out.println(
                        "Navigate to: " + selected.getSceneKey()
                );


            }
        });
    }
}