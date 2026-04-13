import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

public class StudentInformation extends JFrame {

    private JTextField nameField, idField, ageField, courseField;
    private JTable studentTable;
    private DefaultTableModel tableModel;
    private ArrayList<String[]> studentList = new ArrayList<>();

    public StudentInformation() {
        setTitle("Student Information System");
        setSize(800, 550);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Student Details"));
        formPanel.setBackground(new Color(240, 248, 255));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel idLabel = new JLabel("Student ID:");
        idField = new JTextField(15);

        JLabel nameLabel = new JLabel("Name:");
        nameField = new JTextField(15);

        JLabel ageLabel = new JLabel("Age:");
        ageField = new JTextField(15);

        JLabel courseLabel = new JLabel("Course:");
        courseField = new JTextField(15);

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(idLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(idField, gbc);

        gbc.gridx = 2; gbc.gridy = 0;
        formPanel.add(nameLabel, gbc);
        gbc.gridx = 3;
        formPanel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(ageLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(ageField, gbc);

        gbc.gridx = 2; gbc.gridy = 1;
        formPanel.add(courseLabel, gbc);
        gbc.gridx = 3;
        formPanel.add(courseField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(new Color(240, 248, 255));

        JButton addButton = new JButton("Add Student");
        JButton updateButton = new JButton("Update Student");
        JButton deleteButton = new JButton("Delete Student");
        JButton clearButton = new JButton("Clear Fields");

        styleButton(addButton, new Color(34, 139, 34));
        styleButton(updateButton, new Color(30, 100, 200));
        styleButton(deleteButton, new Color(200, 50, 50));
        styleButton(clearButton, new Color(120, 120, 120));

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);

        JPanel topSection = new JPanel(new BorderLayout());
        topSection.add(formPanel, BorderLayout.CENTER);
        topSection.add(buttonPanel, BorderLayout.SOUTH);

        String[] columnNames = {"Student ID", "Name", "Age", "Course"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        studentTable = new JTable(tableModel);
        studentTable.setRowHeight(28);
        studentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        studentTable.getTableHeader().setBackground(new Color(70, 130, 180));
        studentTable.getTableHeader().setForeground(Color.WHITE);
        studentTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        studentTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        studentTable.setGridColor(new Color(200, 210, 220));

        JScrollPane scrollPane = new JScrollPane(studentTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Student Records"));

        add(topSection, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        addButton.addActionListener(e -> addStudent());
        updateButton.addActionListener(e -> updateStudent());
        deleteButton.addActionListener(e -> deleteStudent());
        clearButton.addActionListener(e -> clearFields());

        studentTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedRow();
            }
        });

        setVisible(true);
    }

    private void styleButton(JButton button, Color color) {
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("SansSerif", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private boolean fieldsAreValid() {
        if (idField.getText().trim().isEmpty() ||
            nameField.getText().trim().isEmpty() ||
            ageField.getText().trim().isEmpty() ||
            courseField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Missing Info", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        try {
            Integer.parseInt(ageField.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Age must be a valid number.", "Invalid Age", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private void addStudent() {
        if (!fieldsAreValid()) return;

        String id = idField.getText().trim();
        for (String[] student : studentList) {
            if (student[0].equals(id)) {
                JOptionPane.showMessageDialog(this, "Student ID already exists.", "Duplicate ID", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        String[] newStudent = {
            id,
            nameField.getText().trim(),
            ageField.getText().trim(),
            courseField.getText().trim()
        };

        studentList.add(newStudent);
        tableModel.addRow(newStudent);
        clearFields();
        JOptionPane.showMessageDialog(this, "Student added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void updateStudent() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a student to update.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!fieldsAreValid()) return;

        String[] updated = {
            idField.getText().trim(),
            nameField.getText().trim(),
            ageField.getText().trim(),
            courseField.getText().trim()
        };

        studentList.set(selectedRow, updated);
        tableModel.setValueAt(updated[0], selectedRow, 0);
        tableModel.setValueAt(updated[1], selectedRow, 1);
        tableModel.setValueAt(updated[2], selectedRow, 2);
        tableModel.setValueAt(updated[3], selectedRow, 3);
        clearFields();
        JOptionPane.showMessageDialog(this, "Student updated successfully!", "Updated", JOptionPane.INFORMATION_MESSAGE);
    }

    private void deleteStudent() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a student to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this student?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            studentList.remove(selectedRow);
            tableModel.removeRow(selectedRow);
            clearFields();
            JOptionPane.showMessageDialog(this, "Student deleted.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void loadSelectedRow() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow >= 0) {
            idField.setText(tableModel.getValueAt(selectedRow, 0).toString());
            nameField.setText(tableModel.getValueAt(selectedRow, 1).toString());
            ageField.setText(tableModel.getValueAt(selectedRow, 2).toString());
            courseField.setText(tableModel.getValueAt(selectedRow, 3).toString());
        }
    }

    private void clearFields() {
        idField.setText("");
        nameField.setText("");
        ageField.setText("");
        courseField.setText("");
        studentTable.clearSelection();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(StudentInformation::new);
    }
}