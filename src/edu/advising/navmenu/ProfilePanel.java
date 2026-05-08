package edu.advising.navmenu;

import java.sql.SQLException;
import java.util.List;

public interface ProfilePanel {
    List<ProfileField> getFields() throws SQLException;
    void render();
}
