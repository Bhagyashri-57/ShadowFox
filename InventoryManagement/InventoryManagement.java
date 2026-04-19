import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JWindow;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

public class InventoryManagement extends JFrame {

    private static InventoryManagement instance;

    private ArrayList<Product> products = new ArrayList<>();
    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField nameField, quantityField, priceField, barcodeSearchField;
    private JLabel totalValueLabel, totalItemsLabel, lowStockLabel;

    private Color PAGE_BG    = new Color(245, 247, 251);
    private Color CARD_BG    = new Color(255, 255, 255);
    private Color SIDEBAR_BG = new Color(250, 251, 255);
    private Color ACCENT     = new Color(67, 97, 238);
    private Color ACCENT2    = new Color(34, 197, 94);
    private Color DANGER     = new Color(239, 68, 68);
    private Color WARNING    = new Color(234, 179, 8);
    private Color TEXT_HEAD  = new Color(30, 41, 59);
    private Color TEXT_BODY  = new Color(71, 85, 105);
    private Color TEXT_MUTED = new Color(148, 163, 184);
    private Color BORDER     = new Color(226, 232, 240);
    private Color ROW_ALT    = new Color(248, 250, 252);
    private Color ROW_SEL    = new Color(239, 246, 255);
    private Color ROW_LOW    = new Color(255, 247, 237);

    private Font FONT_LABEL = new Font("Segoe UI", Font.BOLD, 12);
    private Font FONT_BODY  = new Font("Segoe UI", Font.PLAIN, 13);
    private Font FONT_MONO  = new Font("Consolas", Font.PLAIN, 13);

    public static InventoryManagement getInstance() {
        if (instance == null) instance = new InventoryManagement();
        return instance;
    }

    private InventoryManagement() {
        setTitle("Bhagya-ShadowFox-Inventory-Management");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1220, 760);
        setLocationRelativeTo(null);
        getContentPane().setBackground(PAGE_BG);
        setLayout(new BorderLayout(0, 0));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildSidebar(), BorderLayout.WEST);
        add(buildTablePanel(), BorderLayout.CENTER);

        seedData();
        refreshStats();
        setVisible(true);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout()) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(0, 0, new Color(67, 97, 238), getWidth(), 0, new Color(99, 102, 241)));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        header.setOpaque(false);
        header.setPreferredSize(new Dimension(0, 68));
        header.setBorder(new EmptyBorder(0, 24, 0, 24));

        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        logoPanel.setOpaque(false);

        JLabel icon = new JLabel("◈");
        icon.setFont(new Font("Segoe UI", Font.BOLD, 26));
        icon.setForeground(new Color(255, 255, 255, 200));

        JPanel titleBlock = new JPanel();
        titleBlock.setOpaque(false);
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));
        JLabel line1 = new JLabel("Bhagya · ShadowFox");
        line1.setFont(new Font("Segoe UI", Font.BOLD, 15));
        line1.setForeground(Color.WHITE);
        JLabel line2 = new JLabel("Inventory Management System");
        line2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        line2.setForeground(new Color(255, 255, 255, 180));
        titleBlock.add(line1);
        titleBlock.add(line2);

        logoPanel.add(icon);
        logoPanel.add(titleBlock);

        JPanel statsBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        statsBar.setOpaque(false);

        totalItemsLabel = makeStatChip("0 Items",      new Color(255,255,255,35), Color.WHITE);
        totalValueLabel = makeStatChip("0.00 Value",   new Color(255,255,255,35), Color.WHITE);
        lowStockLabel   = makeStatChip("0 Low Stock",  new Color(255,255,255,35), Color.WHITE);

        statsBar.add(totalItemsLabel);
        statsBar.add(totalValueLabel);
        statsBar.add(lowStockLabel);

        header.add(logoPanel, BorderLayout.WEST);
        header.add(statsBar, BorderLayout.EAST);
        return header;
    }

    private JLabel makeStatChip(String text, Color bg, Color fg) {
        JLabel lbl = new JLabel(text) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        lbl.setFont(FONT_LABEL);
        lbl.setForeground(fg);
        lbl.setBorder(new EmptyBorder(6, 14, 6, 14));
        lbl.setOpaque(false);
        return lbl;
    }

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(300, 0));
        sidebar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 0, 1, BORDER),
            new EmptyBorder(24, 20, 24, 20)
        ));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        sidebar.add(sectionLabel("BARCODE SEARCH"));
        sidebar.add(Box.createVerticalStrut(8));
        barcodeSearchField = styledField("Type Barcode ID to find...");
        barcodeSearchField.setFont(FONT_MONO);
        barcodeSearchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e)  { barcodeSearch(); }
            public void removeUpdate(DocumentEvent e)  { barcodeSearch(); }
            public void changedUpdate(DocumentEvent e) {}
        });
        sidebar.add(barcodeSearchField);

        sidebar.add(Box.createVerticalStrut(24));
        sidebar.add(divider());
        sidebar.add(Box.createVerticalStrut(20));
        sidebar.add(sectionLabel("ADD / UPDATE ITEM"));
        sidebar.add(Box.createVerticalStrut(12));

        sidebar.add(fieldLabel("Product Name"));
        sidebar.add(Box.createVerticalStrut(5));
        nameField = styledField("e.g. Wireless Mouse");
        sidebar.add(nameField);

        sidebar.add(Box.createVerticalStrut(14));
        sidebar.add(fieldLabel("Quantity"));
        sidebar.add(Box.createVerticalStrut(5));
        quantityField = styledField("e.g. 50");
        sidebar.add(quantityField);

        sidebar.add(Box.createVerticalStrut(14));
        sidebar.add(fieldLabel("Unit Price (Rs.)"));
        sidebar.add(Box.createVerticalStrut(5));
        priceField = styledField("e.g. 999.00");
        sidebar.add(priceField);

        sidebar.add(Box.createVerticalStrut(24));

        JButton addBtn    = actionButton("+ Add Item",         ACCENT,                  Color.WHITE);
        JButton updateBtn = actionButton("Edit  Update Selected", new Color(241,245,249), TEXT_BODY);
        JButton deleteBtn = actionButton("X  Delete Selected", new Color(255,241,242),   DANGER);

        addBtn.addActionListener(e    -> addItem());
        updateBtn.addActionListener(e -> updateItem());
        deleteBtn.addActionListener(e -> deleteItem());

        sidebar.add(addBtn);
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(updateBtn);
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(deleteBtn);

        sidebar.add(Box.createVerticalGlue());

        JLabel footer = new JLabel("ShadowFox Internship Project");
        footer.setFont(new Font("Segoe UI", Font.ITALIC, 10));
        footer.setForeground(TEXT_MUTED);
        footer.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(footer);
        return sidebar;
    }

    private JSeparator divider() {
        JSeparator sep = new JSeparator();
        sep.setForeground(BORDER);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);
        return sep;
    }

    private JLabel sectionLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lbl.setForeground(TEXT_MUTED);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JLabel fieldLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_LABEL);
        lbl.setForeground(TEXT_BODY);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JTextField styledField(String placeholder) {
        JTextField field = new JTextField() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        field.setFont(FONT_BODY);
        field.setForeground(TEXT_HEAD);
        field.setCaretColor(ACCENT);
        field.setOpaque(false);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1, true),
            new EmptyBorder(9, 12, 9, 12)
        ));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ACCENT, 2, true),
                    new EmptyBorder(8, 11, 8, 11)
                ));
            }
            public void focusLost(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER, 1, true),
                    new EmptyBorder(9, 12, 9, 12)
                ));
            }
        });
        return field;
    }

    private JButton actionButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text) {
            private boolean hover = false;
            {
                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                    public void mouseExited(MouseEvent e)  { hover = false; repaint(); }
                });
            }
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hover ? bg.darker() : bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(fg);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setBorder(new EmptyBorder(10, 16, 10, 16));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JPanel buildTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(PAGE_BG);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel heading = new JLabel("Product Inventory");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 18));
        heading.setForeground(TEXT_HEAD);
        panel.add(heading, BorderLayout.NORTH);

        String[] cols = {"ID", "Product Name", "Qty", "Price (Rs.)", "Total Value (Rs.)", "Status"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel) {
            public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                int qty = 0;
                try { qty = Integer.parseInt(tableModel.getValueAt(row, 2).toString()); } catch (Exception ignored) {}
                if (isRowSelected(row)) {
                    c.setBackground(ROW_SEL); c.setForeground(ACCENT);
                } else if (qty < 5) {
                    c.setBackground(ROW_LOW); c.setForeground(TEXT_HEAD);
                } else if (row % 2 == 0) {
                    c.setBackground(CARD_BG); c.setForeground(TEXT_HEAD);
                } else {
                    c.setBackground(ROW_ALT); c.setForeground(TEXT_HEAD);
                }
                return c;
            }
        };

        table.setFont(FONT_BODY);
        table.setRowHeight(44);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setBackground(CARD_BG);
        table.setForeground(TEXT_HEAD);
        table.setSelectionBackground(ROW_SEL);
        table.setSelectionForeground(ACCENT);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(new Color(241, 245, 249));
        header.setForeground(TEXT_MUTED);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, BORDER));
        header.setPreferredSize(new Dimension(0, 46));
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(JLabel.LEFT);

        table.getColumnModel().getColumn(0).setPreferredWidth(60);
        table.getColumnModel().getColumn(0).setMaxWidth(70);
        table.getColumnModel().getColumn(1).setPreferredWidth(230);
        table.getColumnModel().getColumn(2).setPreferredWidth(70);
        table.getColumnModel().getColumn(3).setPreferredWidth(110);
        table.getColumnModel().getColumn(4).setPreferredWidth(140);
        table.getColumnModel().getColumn(5).setPreferredWidth(110);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(5).setCellRenderer(new StatusRenderer());

        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                nameField.setText(tableModel.getValueAt(row, 1).toString());
                quantityField.setText(tableModel.getValueAt(row, 2).toString());
                priceField.setText(tableModel.getValueAt(row, 3).toString());
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBackground(CARD_BG);
        scroll.getViewport().setBackground(CARD_BG);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    class StatusRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean focus, int row, int col) {
            JLabel lbl = new JLabel(val != null ? val.toString() : "") {
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    String v = getText();
                    Color bg = v.contains("OK")  ? new Color(34, 197, 94, 30) :
                            v.contains("Low") ? new Color(234, 179, 8, 30) :
                            new Color(239, 68, 68, 30);
                    g2.setColor(bg);
                    int w = getFontMetrics(getFont()).stringWidth(getText()) + 24;
                    int x = (getWidth() - w) / 2;
                    g2.fillRoundRect(x, 10, w, getHeight() - 20, 20, 20);
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            int qty = 0;
            try { qty = Integer.parseInt(tableModel.getValueAt(row, 2).toString()); } catch (Exception ignored) {}
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
            lbl.setForeground(qty == 0 ? DANGER : qty < 5 ? WARNING : ACCENT2);
            lbl.setHorizontalAlignment(JLabel.CENTER);
            lbl.setOpaque(true);
            if (sel) lbl.setBackground(ROW_SEL);
            else if (qty < 5) lbl.setBackground(ROW_LOW);
            else lbl.setBackground(row % 2 == 0 ? CARD_BG : ROW_ALT);
            return lbl;
        }
    }

    private void addItem() {
        String name = nameField.getText().trim();
        String qtyStr = quantityField.getText().trim();
        String priceStr = priceField.getText().trim();
        if (name.isEmpty() || qtyStr.isEmpty() || priceStr.isEmpty()) {
            showToast("Please fill all fields.", DANGER); return;
        }
        int qty; double price;
        try {
            qty = Integer.parseInt(qtyStr);
            price = Double.parseDouble(priceStr);
            if (qty < 0 || price < 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            showToast("Qty and Price must be valid positive numbers.", DANGER); return;
        }
        int id = products.size() + 1001;
        Product p = new Product(id, name, qty, price);
        products.add(p);
        addRow(p);
        clearFields();
        refreshStats();
        showToast("Item added successfully!", ACCENT2);
    }

    private void updateItem() {
        int row = table.getSelectedRow();
        if (row < 0) { showToast("Select a row to update.", WARNING); return; }
        String name = nameField.getText().trim();
        String qtyStr = quantityField.getText().trim();
        String priceStr = priceField.getText().trim();
        if (name.isEmpty() || qtyStr.isEmpty() || priceStr.isEmpty()) {
            showToast("Please fill all fields.", DANGER); return;
        }
        int qty; double price;
        try {
            qty = Integer.parseInt(qtyStr);
            price = Double.parseDouble(priceStr);
            if (qty < 0 || price < 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            showToast("Qty and Price must be valid positive numbers.", DANGER); return;
        }
        int id = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
        for (Product p : products) {
            if (p.id == id) { p.name = name; p.quantity = qty; p.price = price; break; }
        }
        tableModel.setValueAt(name, row, 1);
        tableModel.setValueAt(qty, row, 2);
        tableModel.setValueAt(String.format("%.2f", price), row, 3);
        tableModel.setValueAt(String.format("%.2f", qty * price), row, 4);
        tableModel.setValueAt(statusText(qty), row, 5);
        clearFields();
        refreshStats();
        showToast("Item updated!", ACCENT);
    }

    private void deleteItem() {
        int row = table.getSelectedRow();
        if (row < 0) { showToast("Select a row to delete.", WARNING); return; }
        int id = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
        products.removeIf(p -> p.id == id);
        tableModel.removeRow(row);
        clearFields();
        refreshStats();
        showToast("Item deleted.", DANGER);
    }

    private void barcodeSearch() {
        String text = barcodeSearchField.getText().trim();
        if (text.isEmpty()) { reloadTable(); return; }
        try {
            int searchId = Integer.parseInt(text);
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                int rowId = Integer.parseInt(tableModel.getValueAt(i, 0).toString());
                if (rowId == searchId) {
                    table.setRowSelectionInterval(i, i);
                    table.scrollRectToVisible(table.getCellRect(i, 0, true));
                    return;
                }
            }
        } catch (NumberFormatException ignored) {}
    }

    private void reloadTable() {
        tableModel.setRowCount(0);
        for (Product p : products) addRow(p);
    }

    private void addRow(Product p) {
        tableModel.addRow(new Object[]{
            p.id, p.name, p.quantity,
            String.format("%.2f", p.price),
            String.format("%.2f", p.quantity * p.price),
            statusText(p.quantity)
        });
    }

    private String statusText(int qty) {
        return qty == 0 ? "Out of Stock" : qty < 5 ? "Low Stock" : "OK";
    }

    private void refreshStats() {
        int total = products.size();
        double value = products.stream().mapToDouble(p -> p.price * p.quantity).sum();
        long low = products.stream().filter(p -> p.quantity < 5).count();
        totalItemsLabel.setText("  " + total + " Items  ");
        totalValueLabel.setText("  Rs." + String.format("%,.2f", value) + " Value  ");
        lowStockLabel.setText("  " + low + " Low Stock  ");
    }

    private void clearFields() {
        nameField.setText("");
        quantityField.setText("");
        priceField.setText("");
        table.clearSelection();
    }

    private void showToast(String msg, Color color) {
        JWindow toast = new JWindow(this);
        JLabel lbl = new JLabel("  " + msg + "  ") {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(Color.WHITE);
        lbl.setBorder(new EmptyBorder(10, 18, 10, 18));
        toast.add(lbl);
        toast.pack();
        int x = getX() + getWidth() / 2 - toast.getWidth() / 2;
        int y = getY() + getHeight() - 80;
        toast.setLocation(x, y);
        toast.setVisible(true);
        new Timer(2200, e -> toast.dispose()).start();
    }

    private void seedData() {
        products.add(new Product(1001, "Coke Zero Sugar",    12,  70.00));
        products.add(new Product(1002, "Britania Cup Cake",        3,  60.00));
        products.add(new Product(1003, "Diary Milk Diet Coffee",          0, 190.00));
        products.add(new Product(1004, "Bingo Lays",      45,  31.00));
        products.add(new Product(1005, "Mountain Dew Energy",   2,  125.00));
        products.add(new Product(1006, "Nestle Veg Maggi",18,  25.00));
        products.add(new Product(1007, "Amul Curd",            8,   20.00));
        reloadTable();
    }

    class Product {
        int id, quantity;
        String name;
        double price;
        Product(int id, String name, int quantity, double price) {
            this.id = id; this.name = name; this.quantity = quantity; this.price = price;
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            UIManager.put("ScrollBar.thumb", new Color(203, 213, 225));
            UIManager.put("ScrollBar.track", new Color(241, 245, 249));
        } catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> InventoryManagement.getInstance());
    }
}