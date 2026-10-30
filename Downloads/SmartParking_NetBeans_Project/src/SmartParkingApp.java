import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.sql.*;

public class SmartParkingApp extends JFrame {
    private CardLayout cards = new CardLayout();
    private JPanel main = new JPanel(cards);
    private JLabel status = new JLabel("Ready");
    private JPanel slotsPanel;

    private Font title = new Font("Segoe UI", Font.BOLD, 28);
    private Font head = new Font("Segoe UI", Font.BOLD, 18);
    private Font normal = new Font("Segoe UI", Font.PLAIN, 14);

    public SmartParkingApp() {
        setTitle("Smart Parking Management System");
        setSize(1100, 700);
        setMinimumSize(new Dimension(950, 600));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        buildUI();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(245,247,250));

        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(220, 700));
        sidebar.setBackground(new Color(24, 32, 45));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        JLabel logo = new JLabel("<html><div style='text-align:center'>SMART<br>PARKING</div></html>");
        logo.setForeground(Color.WHITE);
        logo.setFont(new Font("Segoe UI", Font.BOLD, 23));
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        logo.setBorder(new EmptyBorder(28,10,30,10));
        sidebar.add(logo);

        addNav(sidebar, "Dashboard", "dashboard");
        addNav(sidebar, "Find Parking", "parking");
        addNav(sidebar, "My Reservations", "reservations");
        addNav(sidebar, "Admin Panel", "admin");

        sidebar.add(Box.createVerticalGlue());
        
        JLabel footer = new JLabel("<html><center>Find. Reserve. Park.<br><br>Developed by:<br>Naman Gupta<br>0251CSAITWN017</center></html>");
        footer.setForeground(new Color(180,190,205));
        footer.setAlignmentX(Component.CENTER_ALIGNMENT);
        footer.setBorder(new EmptyBorder(10,10,25,10));
        sidebar.add(footer);

        main.add(dashboardPage(), "dashboard");
        main.add(parkingPage(), "parking");
        main.add(reservationsPage(), "reservations");
        main.add(adminPage(), "admin");

        root.add(sidebar, BorderLayout.WEST);
        root.add(main, BorderLayout.CENTER);
        setContentPane(root);
    }

    private void addNav(JPanel side, String text, String card) {
        JButton b = new JButton(text);
        b.setMaximumSize(new Dimension(190, 48));
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(12,15,12,15));
        b.setForeground(Color.WHITE);
        b.setBackground(new Color(38,49,66));
        b.setFont(normal);
        b.addActionListener(e -> {
            if(card.equals("dashboard")) {
                main.add(dashboardPage(), "dashboard"); 
            }
            cards.show(main, card);
        });
        side.add(b);
        side.add(Box.createVerticalStrut(8));
    }

    private JPanel pageTitle(String heading, String sub) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(25,30,15,30));
        JLabel h = new JLabel(heading);
        h.setFont(title);
        h.setForeground(new Color(30,39,52));
        JLabel s = new JLabel(sub);
        s.setFont(normal);
        s.setForeground(new Color(105,115,130));
        JPanel texts = new JPanel();
        texts.setOpaque(false);
        texts.setLayout(new BoxLayout(texts, BoxLayout.Y_AXIS));
        texts.add(h); texts.add(Box.createVerticalStrut(5)); texts.add(s);
        p.add(texts, BorderLayout.WEST);
        return p;
    }

    private int[] getDBStats() {
        int total = 0, available = 0, occupied = 0, bookings = 0;
        try (Connection conn = DBConnection.getConnection()) {
            if (conn != null) {
                ResultSet rs = conn.createStatement().executeQuery("SELECT COUNT(*) FROM parking_slots");
                if (rs.next()) total = rs.getInt(1);
                
                rs = conn.createStatement().executeQuery("SELECT COUNT(*) FROM parking_slots WHERE status = 'AVAILABLE'");
                if (rs.next()) available = rs.getInt(1);
                
                occupied = total - available;
                
                rs = conn.createStatement().executeQuery("SELECT COUNT(*) FROM reservations");
                if (rs.next()) bookings = rs.getInt(1);
            }
        } catch (Exception e) {
            System.out.println("Dashboard error: " + e.getMessage());
        }
        return new int[]{total, available, occupied, bookings};
    }

    private JPanel dashboardPage() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(245,247,250));
        p.add(pageTitle("Parking Dashboard", "Real-time overview of your parking facility"), BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout(20,20));
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(5,30,30,30));

        int[] dbStats = getDBStats();

        JPanel stats = new JPanel(new GridLayout(1,4,15,15));
        stats.setOpaque(false);
        stats.add(stat("Total Slots", String.valueOf(dbStats[0]), "Parking capacity"));
        stats.add(stat("Available", String.valueOf(dbStats[1]), "Ready to reserve"));
        stats.add(stat("Occupied", String.valueOf(dbStats[2]), "Currently parked"));
        stats.add(stat("Bookings", String.valueOf(dbStats[3]), "Today's reservations"));
        content.add(stats, BorderLayout.NORTH);

        JPanel welcome = card();
        welcome.setLayout(new BorderLayout(20,10));
        JLabel wh = new JLabel("Welcome to Smart Parking");
        wh.setFont(head);
        JLabel wt = new JLabel("<html>Search available spaces, reserve a slot in advance,<br>and manage parking through one organized interface.</html>");
        wt.setFont(normal);
        JButton find = primary("Find Available Slot");
        find.addActionListener(e -> cards.show(main, "parking"));
        JPanel wleft = new JPanel();
        wleft.setOpaque(false);
        wleft.setLayout(new BoxLayout(wleft, BoxLayout.Y_AXIS));
        wleft.add(wh); wleft.add(Box.createVerticalStrut(12)); wleft.add(wt);
        welcome.add(wleft, BorderLayout.CENTER);
        welcome.add(find, BorderLayout.EAST);
        content.add(welcome, BorderLayout.CENTER);

        p.add(content, BorderLayout.CENTER);
        return p;
    }

    private JPanel stat(String name, String value, String desc) {
        JPanel c = card();
        c.setLayout(new BorderLayout());
        JLabel n = new JLabel(name);
        n.setFont(normal);
        n.setForeground(new Color(100,110,125));
        JLabel v = new JLabel(value);
        v.setFont(new Font("Segoe UI", Font.BOLD, 30));
        v.setForeground(new Color(30,100,190));
        JLabel d = new JLabel(desc);
        d.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        d.setForeground(new Color(130,140,150));
        JPanel x = new JPanel();
        x.setOpaque(false);
        x.setLayout(new BoxLayout(x,BoxLayout.Y_AXIS));
        x.add(n); x.add(Box.createVerticalStrut(5)); x.add(v); x.add(d);
        c.add(x,BorderLayout.CENTER);
        return c;
    }

    private JPanel parkingPage() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(245,247,250));
        p.add(pageTitle("Find Parking", "Check availability and reserve your preferred slot"), BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(20,15));
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(5,30,30,30));

        JPanel filter = card();
        filter.setLayout(new FlowLayout(FlowLayout.LEFT,12,12));
        JButton refresh = primary("Refresh Grid");
        refresh.addActionListener(e -> buildSlots()); 
        filter.add(refresh);
        body.add(filter,BorderLayout.NORTH);

        slotsPanel = new JPanel(new GridLayout(3,4,14,14));
        slotsPanel.setOpaque(false);
        buildSlots(); 
        body.add(slotsPanel,BorderLayout.CENTER);

        JPanel legend = new JPanel(new FlowLayout(FlowLayout.LEFT,20,5));
        legend.setOpaque(false);
        legend.add(legendItem("Available", new Color(46,160,90)));
        legend.add(legendItem("Occupied", new Color(190,70,70)));
        body.add(legend,BorderLayout.SOUTH);
        p.add(body,BorderLayout.CENTER);
        return p;
    }

    private void buildSlots() {
        slotsPanel.removeAll();
        try (Connection conn = DBConnection.getConnection()) {
            if (conn != null) {
                ResultSet rs = conn.createStatement().executeQuery("SELECT id, slot_number, status FROM parking_slots");
                while (rs.next()) {
                    int slotId = rs.getInt("id");
                    String slotName = rs.getString("slot_number");
                    boolean isOccupied = !rs.getString("status").equals("AVAILABLE");

                    JPanel s = card();
                    s.setLayout(new BorderLayout(5,8));
                    JLabel name = new JLabel(slotName, SwingConstants.CENTER);
                    name.setFont(new Font("Segoe UI", Font.BOLD, 22));
                    name.setForeground(isOccupied ? new Color(190,70,70) : new Color(46,160,90));
                    
                    JLabel state = new JLabel(isOccupied ? "OCCUPIED" : "AVAILABLE", SwingConstants.CENTER);
                    state.setFont(new Font("Segoe UI", Font.BOLD, 12));
                    state.setForeground(name.getForeground());
                    
                    // Button ab dono cases (Reserve / Unreserve) handle karega
                    JButton b = new JButton(isOccupied ? "Unreserve" : "Reserve");
                    b.addActionListener(e -> {
                        if (isOccupied) {
                            unreserveSlot(slotId, slotName);
                        } else {
                            reserveSlot(slotId, slotName);
                        }
                    });
                    
                    s.add(name, BorderLayout.CENTER);
                    JPanel south = new JPanel(new BorderLayout());
                    south.setOpaque(false); 
                    south.add(state, BorderLayout.NORTH); 
                    south.add(b, BorderLayout.SOUTH);
                    s.add(south, BorderLayout.SOUTH);
                    slotsPanel.add(s);
                }
            }
        } catch (Exception ex) {
            System.out.println("Error loading slots: " + ex.getMessage());
        }
        slotsPanel.revalidate(); 
        slotsPanel.repaint();
    }

    private void reserveSlot(int slotId, String slotName) {
        int r = JOptionPane.showConfirmDialog(this,"Reserve parking slot " + slotName + "?", "Confirm Reservation", JOptionPane.YES_NO_OPTION);
        if (r == JOptionPane.YES_OPTION) {
            try (Connection conn = DBConnection.getConnection()) {
                if (conn != null) {
                    PreparedStatement ps = conn.prepareStatement("UPDATE parking_slots SET status = 'OCCUPIED' WHERE id = ?");
                    ps.setInt(1, slotId);
                    ps.executeUpdate();
                    
                    buildSlots(); 
                    JOptionPane.showMessageDialog(this, "Reservation confirmed for " + slotName + "!", "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }

    // Naya method Slot ko Free (Unreserve) karne ke liye
    private void unreserveSlot(int slotId, String slotName) {
        int r = JOptionPane.showConfirmDialog(this,"Do you want to free up slot " + slotName + "?", "Confirm Unreserve", JOptionPane.YES_NO_OPTION);
        if (r == JOptionPane.YES_OPTION) {
            try (Connection conn = DBConnection.getConnection()) {
                if (conn != null) {
                    PreparedStatement ps = conn.prepareStatement("UPDATE parking_slots SET status = 'AVAILABLE' WHERE id = ?");
                    ps.setInt(1, slotId);
                    ps.executeUpdate();
                    
                    buildSlots(); 
                    JOptionPane.showMessageDialog(this, "Slot " + slotName + " is now free and available!", "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }

    private JPanel reservationsPage() {
        JPanel p=new JPanel(new BorderLayout());
        p.setBackground(new Color(245,247,250));
        p.add(pageTitle("My Reservations","Your recent parking bookings"),BorderLayout.NORTH);
        JPanel c=card();
        c.setBorder(new CompoundBorder(new EmptyBorder(5,30,30,30),new LineBorder(new Color(220,225,232),1,true)));
        c.setLayout(new BorderLayout());
        String[] cols={"Booking ID","Slot","Location","Date","Time","Status"};
        Object[][] data={{"BK-1024","A02","Main Campus","22 Sep 2026","10:00 AM","Confirmed"},
                         {"BK-1019","B03","Main Campus","21 Sep 2026","02:30 PM","Completed"},
                         {"BK-1007","A05","Block A","20 Sep 2026","11:00 AM","Completed"}};
        JTable table=new JTable(data,cols);
        table.setRowHeight(35); table.setFont(normal); table.getTableHeader().setFont(new Font("Segoe UI",Font.BOLD,13));
        c.add(new JScrollPane(table),BorderLayout.CENTER);
        p.add(c,BorderLayout.CENTER);
        return p;
    }

    private JPanel adminPage() {
        JPanel p=new JPanel(new BorderLayout());
        p.setBackground(new Color(245,247,250));
        p.add(pageTitle("Admin Panel","Manage users, slots and occupancy"),BorderLayout.NORTH);
        JPanel body=new JPanel(new GridLayout(2,2,18,18));
        body.setOpaque(false); body.setBorder(new EmptyBorder(5,30,30,30));
        body.add(adminCard("Slot Management","5 total slots","Database Connected"));
        body.add(adminCard("User Management","Live Users","Live tracking"));
        body.add(adminCard("Occupancy","Live Status","Monitoring enabled"));
        body.add(adminCard("Analytics","Daily trends","Utilization reporting"));
        p.add(body,BorderLayout.CENTER);
        return p;
    }

    private JPanel adminCard(String h,String a,String b){
        JPanel c=card(); c.setLayout(new BorderLayout(8,8));
        JLabel x=new JLabel(h); x.setFont(head);
        JLabel y=new JLabel(a); y.setFont(new Font("Segoe UI",Font.BOLD,18)); y.setForeground(new Color(30,100,190));
        JLabel z=new JLabel(b); z.setFont(normal); z.setForeground(new Color(100,110,125));
        c.add(x,BorderLayout.NORTH); c.add(y,BorderLayout.CENTER); c.add(z,BorderLayout.SOUTH);
        return c;
    }

    private JPanel legendItem(String text, Color color){
        JPanel p=new JPanel(new FlowLayout(FlowLayout.LEFT,5,0)); p.setOpaque(false);
        JLabel dot=new JLabel("●"); dot.setForeground(color); dot.setFont(new Font("Segoe UI",Font.BOLD,18));
        p.add(dot); p.add(new JLabel(text)); return p;
    }

    private JPanel card(){
        JPanel p=new JPanel();
        p.setBackground(Color.WHITE);
        p.setBorder(new CompoundBorder(new LineBorder(new Color(225,229,235),1,true),new EmptyBorder(15,15,15,15)));
        return p;
    }

    private JButton primary(String text){
        JButton b=new JButton(text);
        b.setFocusPainted(false);
        b.setBackground(new Color(35,110,200));
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI",Font.BOLD,13));
        b.setBorder(new EmptyBorder(10,18,10,18));
        return b;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SmartParkingApp().setVisible(true));
    }
}