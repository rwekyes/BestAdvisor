package edu.advising.navmenu;

import edu.advising.users.Admin;

import java.util.List;

public class AdminProfilePanel implements ProfilePanel {
    private final Admin admin;

    public AdminProfilePanel(Admin admin) {
        this.admin = admin;
    }

    @Override
    public List<ProfileField> getFields() {
        return List.of(
                new ProfileField("Name",  admin.getFullName(), false),
                new ProfileField("Email", admin.getEmail(),    true),
                new ProfileField("Phone", admin.getPhone(),    true)
        );
    }

    @Override
    public void render() {
        System.out.println("--- Your Profile ---");
        for (ProfileField f : getFields()) {
            System.out.printf("  %-18s %s%s%n", f.getLabel() + ":", f.getValue() != null ? f.getValue() : "—",
                    f.isEditable() ? "  [editable]" : "");
        }
    }
}
