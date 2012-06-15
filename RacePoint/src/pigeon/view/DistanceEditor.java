/*
    Copyright (c) 2005, 2006, 2007, 2008, 2012 Paul Richards <paul.richards@gmail.com>

    Permission to use, copy, modify, and/or distribute this software for any
    purpose with or without fee is hereby granted, provided that the above
    copyright notice and this permission notice appear in all copies.

    THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
    WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
    MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
    ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
    WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
    ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
    OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
*/

package pigeon.view;

import java.awt.Component;
import java.util.Map;
import javax.swing.JOptionPane;
import pigeon.model.Distance;
import pigeon.model.Organization;
import pigeon.model.Member;
import pigeon.model.Racepoint;

/**
 * Panel to let the user enter the distances (in the form of a table) for a single racepoint or member.
 */
final class DistanceEditor<Subject, Target> extends javax.swing.JPanel {

    private static final long serialVersionUID = 8017955650652536256L;

    private Map<Target, Distance> distances;
    private DistancesTableModel<Target> distancesTableModel;

    public static Organization editMemberDistances(Component parent, Member member, Organization club) throws UserCancelledException {
        Map<Racepoint, Distance> distances = club.getDistancesForMember(member);
        if (distances.isEmpty()) return club;
        DistanceEditor<Member, Racepoint> panel = new DistanceEditor<Member, Racepoint>(member, "Racepoint", distances);
        while (true) {
            Object[] options = {"Ok", "Cancel"};
            int result = JOptionPane.showOptionDialog(parent, panel, "Distances", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
            if (result == 0) {
                for (Map.Entry<Racepoint, Distance> entry: distances.entrySet()) {
                    club = club.repSetDistance(member, entry.getKey(), entry.getValue());
                }
                break;
            } else {
                result = JOptionPane.showConfirmDialog(parent, "Return to main menu and discard these changes?", "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (result == JOptionPane.YES_OPTION) {
                    throw new UserCancelledException();
                }
            }
        }
        return club;
    }

    public static Organization editRacepointDistances(Component parent, Racepoint racepoint, Organization club) throws UserCancelledException {
        Map<Member, Distance> distances = club.getDistancesForRacepoint(racepoint);
        if (distances.isEmpty()) return club;
        DistanceEditor<Racepoint, Member> panel = new DistanceEditor<Racepoint, Member>(racepoint, "Member", distances);
        while (true) {
            Object[] options = {"Ok", "Cancel"};
            int result = JOptionPane.showOptionDialog(parent, panel, "Distances", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
            if (result == 0) {
                for (Map.Entry<Member, Distance> entry: distances.entrySet()) {
                    club = club.repSetDistance(entry.getKey(), racepoint, entry.getValue());
                }
                break;
            } else {
                result = JOptionPane.showConfirmDialog(parent, "Return to main menu and discard these changes?", "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (result == JOptionPane.YES_OPTION) {
                    throw new UserCancelledException();
                }
            }
        }
        return club;
    }

    public DistanceEditor(Subject subject, String targetTitle, Map<Target, Distance> distances) {
        this.distances = distances;
        this.distancesTableModel = new DistancesTableModel<Target>(targetTitle, distances, true);
        initComponents();
        distancesPanel.setBorder(new javax.swing.border.TitledBorder("Distances For " + subject));
    }

    private void updateDistancesMap() {

    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        distancesPanel = new javax.swing.JPanel();
        scrollPane = new javax.swing.JScrollPane();
        distancesTable = new javax.swing.JTable();

        setLayout(new java.awt.BorderLayout());

        distancesPanel.setLayout(new java.awt.BorderLayout());

        distancesPanel.setBorder(new javax.swing.border.TitledBorder("Distances For"));
        distancesTable.setModel(distancesTableModel);
        distancesTable.setRowSelectionAllowed(false);
        scrollPane.setViewportView(distancesTable);

        distancesPanel.add(scrollPane, java.awt.BorderLayout.CENTER);

        add(distancesPanel, java.awt.BorderLayout.CENTER);

    }
    // </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel distancesPanel;
    private javax.swing.JTable distancesTable;
    private javax.swing.JScrollPane scrollPane;
    // End of variables declaration//GEN-END:variables

}