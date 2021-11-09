package com.polimi.applicationdemo;

import com.polimi.utils.PartialOrderPizza;
import com.polimi.utils.PartialOrderProduct;
import net.proteanit.sql.DbUtils;

import javax.swing.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Random;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

public class ApplicationDemo {
    private static JFrame frame;
    private JPanel panellomain;
    private JPanel panellouser;
    private JPanel panelloproduct;
    private JPanel panellocard;
    private JLabel logolabel;
    private JPanel Main;
    private JTable table1;
    private JTable table2;
    private JTable table3;
    private JList list1;
    private JButton submitorder;
    private JTextField ordernotes;
    private JRadioButton takeAwayRadioButton;
    private JTextField desiredArrivalOrder;
    private JButton DELETELASTINSERTIONButton;
    private JButton DELETEPRODUCTButton;
    private JTable table4;
    private JTable table5;
    private JButton BACKButton;
    private Connection con;
    private PreparedStatement pst;
    private ArrayList listorder;
    private ArrayList <PartialOrderPizza> runtimestructpizza;
    private ArrayList <PartialOrderProduct> runtimestructproduct;
    private ArrayList <JComboBox> combobox;

    //TODO: valuta se migliorare il modo in cui sono definite alcune variabili, se farle private all'interno del metodo stesso oppure globali della class
    //TODO: controllo su inserimento come notes, desired arrival time ecc.ecc.
    //TODO: pannelocard gestione user con modifica dati ecc.ecc.
    //TODO: fix queries and table4 and 5 not showing

    public static void main(String[] args) {
        frame = new JFrame("CRUD_PIZZA");
        frame.setContentPane(new ApplicationDemo().Main);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public boolean connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost/pizzamgt", "root", "");
            System.out.println("Successfully connected via jdbc Driver to the server    \n");
            return true;
        } catch (SQLException| ClassNotFoundException throwables) {
            throwables.printStackTrace();
            return false;
        }
    }

    public void loadpizzatb(){
        try{
            pst = con.prepareStatement("select * from pizza");
            ResultSet rs = pst.executeQuery();
            table1.setModel(DbUtils.resultSetToTableModel(rs));
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    public void loadcustomertb(String idcustomer){
        try{
            pst = con.prepareStatement("select * from customer where id=?");
            pst.setString(1,idcustomer);
            ResultSet rs = pst.executeQuery();
            table2.setModel(DbUtils.resultSetToTableModel(rs));
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    public void loadproductstb(){
        try{
            pst = con.prepareStatement("select * from product");
            ResultSet rs = pst.executeQuery();
            table3.setModel(DbUtils.resultSetToTableModel(rs));
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    public void updateorderlist (ArrayList <String> ls){
        DefaultListModel demoList = new DefaultListModel();
        demoList.addElement(ls);
        //TODO: try list of lists in future versions per cercare di avere spaziature oppure anche coppie product,price oppure usa table
        list1.setModel(demoList);
    }

    public int generaterandomidcustomer(){
        Random rd = new Random();
        return rd.nextInt(51);
    }

    public void generatestat(int idcustomer){
        try {
            pst = con.prepareStatement("select count(*) from customerorder where fromcustomer=?1");
            pst.setInt(1,idcustomer);
            ResultSet rs = pst.executeQuery();
            table4.setModel(DbUtils.resultSetToTableModel(rs));
        }catch (SQLException e){
            e.printStackTrace();
        }

    }

    public void generatestat2(int idcustomer){
        try {
            pst = con.prepareStatement("select * from customerorder where fromcustomer=?1 order by price");
            pst.setInt(1,idcustomer);
            ResultSet rs = pst.executeQuery();
            table4.setModel(DbUtils.resultSetToTableModel(rs));
        }catch (SQLException e){
            e.printStackTrace();
        }

    }

    public void init(int customer){
        //set enterprise icon
        ImageIcon iconlogo = new ImageIcon("logo.png");
        logolabel.setIcon(iconlogo);
        connect();
        loadpizzatb();
        loadcustomertb(String.valueOf(customer));
        loadproductstb();
    }

    public void addOrder(String nt, String arr, Boolean tw) {
        int numofprod = 0;
        int numofpizza = 0;
        float price = 0.0F;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        try{
            con.setAutoCommit(false);
            System.out.println("Partial insertion into customerorder for the given order together with containsprod & pizza\n");
            pst = con.prepareStatement("INSERT into customerorder (fromcustomer,ts,desiredtime,takeaway,numofprod,numofpizza,price,notes) values (?,?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
            pst.setInt(1, (Integer) table2.getValueAt(0,0));
            pst.setString(2, dtf.format(now));
            pst.setString(3, arr);
            pst.setBoolean(4,tw);
            pst.setInt(5,0);
            pst.setInt(6,0);
            pst.setDouble(7,0.0F);
            pst.setString(8,nt);
            pst.executeUpdate();
            ResultSet rs = pst.getGeneratedKeys();
            rs.next();
            int orderid = rs.getInt(1);

            //calculate numofprod the corresponding price and insert into containsprod
            for (PartialOrderProduct x : runtimestructproduct) {
                pst = con.prepareStatement("SELECT id,price from product where name=?",ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                pst.setString(1, x.getName());
                rs = pst.executeQuery();
                while (rs.next()) {
                    numofprod = numofprod + x.getQty();
                    price = price + rs.getFloat("PRICE") * x.getQty();
                    }
                pst = con.prepareStatement("INSERT INTO containsprod (orderid,productid,qty,note) values (?,?,?,?)");
                pst.setInt(1,orderid);
                pst.setInt(2,Integer.valueOf(x.getId()));
                pst.setInt(3,x.getQty());
                pst.setString(4,x.getNotes());
                pst.executeUpdate();
            }
            //calculate numofpizza the corresponding price and insert into containspizza
            for (PartialOrderPizza x : runtimestructpizza){
                pst = con.prepareStatement("SELECT pizzaid,price from pizza where name=?",ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
                pst.setString(1, x.getName());
                rs = pst.executeQuery();
                while (rs.next()) {
                    numofpizza = numofpizza + x.getQty();
                    price = price + rs.getFloat("PRICE") * x.getQty();
                }
                pst = con.prepareStatement("INSERT INTO containspizza (orderid,pizzaid,addon1,addon2,addon3,qty,note) values (?,?,?,?,?,?,?)");
                pst.setInt(1,orderid);
                pst.setInt(2,Integer.valueOf(x.getId()));
                pst.setString(3,x.getAddons().get(0));
                pst.setString(4,x.getAddons().get(1));
                pst.setString(5,x.getAddons().get(2));
                pst.setInt(6,x.getQty());
                pst.setString(7,x.getNotes());
                pst.executeUpdate();
            }
            System.out.println("Completed insertion into customerorder with the final tuple\n");
            pst = con.prepareStatement("UPDATE customerorder SET numofprod = ?, numofpizza = ?, price = ? WHERE id = ?");
            pst.setInt(1,numofprod);
            pst.setInt(2,numofpizza);
            pst.setFloat(3,price);
            pst.setInt(4,orderid);
            pst.executeUpdate();
            con.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                System.out.println("Something went wrong during query execution, rolling back insertions");
                con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void createDynamicComboBox(){
        combobox = new ArrayList<JComboBox>();

        JComboBox zField = new JComboBox();
        JComboBox pField = new JComboBox();
        JComboBox tField = new JComboBox();
        combobox.add(zField);
        combobox.add(pField);
        combobox.add(tField);
        try {
            pst = con.prepareStatement("SELECT * from ingredient",ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet rs = pst.executeQuery();
            for(int i=0;i<3;i++){
                combobox.get(i).addItem("");
                while(rs.next()){
                    combobox.get(i).addItem(rs.getString("name"));
                }
                rs.beforeFirst();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }


    public ApplicationDemo(){
        int customer = generaterandomidcustomer();
        runtimestructpizza = new ArrayList<PartialOrderPizza>();
        runtimestructproduct = new ArrayList<PartialOrderProduct>();
        listorder = new ArrayList<String>();
        init(customer);

        table1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                //super.mouseClicked(e);
                if (e.getClickCount() == 1) {
                    int row = table1.getSelectedRow();
                    PartialOrderPizza neworder = new PartialOrderPizza();

                    JTextField xField = new JTextField(20);
                    JComboBox yField = new JComboBox();
                    yField.addItem(1);
                    yField.addItem(2);
                    yField.addItem(3);
                    yField.addItem(4);
                    yField.addItem(5);

                    createDynamicComboBox();
                    JPanel myPanel = new JPanel();
                    myPanel.add(new JLabel("Notes:"));
                    myPanel.add(xField);
                    myPanel.add(Box.createHorizontalStrut(15)); // a spacer
                    myPanel.add(new JLabel("Quantity:"));
                    myPanel.add(yField);
                    myPanel.add(Box.createVerticalStrut(15));
                    myPanel.add(new JLabel("Addons1"));
                    myPanel.add(combobox.get(0));
                    myPanel.add(new JLabel("Addons2"));
                    myPanel.add(combobox.get(1));
                    myPanel.add(new JLabel("Addons3"));
                    myPanel.add(combobox.get(2));
                    int input = JOptionPane.showConfirmDialog(null, myPanel,"Please Enter Notes, Addons and Quantities Values", JOptionPane.OK_OPTION);
                    if(input == 0){
                        ArrayList <String> ls = new ArrayList<>();
                        neworder.setNotes(xField.getText());
                        neworder.setQty((Integer) yField.getItemAt(yField.getSelectedIndex()));
                        neworder.setId(table1.getValueAt(row,0).toString());
                        neworder.setName(table1.getValueAt(row,1).toString());
                        ls.add((String) combobox.get(0).getItemAt(combobox.get(0).getSelectedIndex()));
                        ls.add((String) combobox.get(1).getItemAt(combobox.get(1).getSelectedIndex()));
                        ls.add((String) combobox.get(2).getItemAt(combobox.get(2).getSelectedIndex()));
                        neworder.addAddons(ls.get(0));
                        neworder.addAddons(ls.get(1));
                        neworder.addAddons(ls.get(2));
                        String sldprodid = table1.getValueAt(row,1).toString();
                        runtimestructpizza.add(neworder);
                        listorder.add(sldprodid);
                        updateorderlist(listorder);
                        combobox.removeAll(combobox);
                    }
                }
            }
        });

        table3.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                //super.mouseClicked(e);
                if (e.getClickCount() == 1) {
                    PartialOrderProduct neworder = new PartialOrderProduct();
                    int row = table3.getSelectedRow();

                    JTextField xField = new JTextField(40);
                    JComboBox yField = new JComboBox();
                    yField.addItem(1);
                    yField.addItem(2);
                    yField.addItem(3);
                    yField.addItem(4);
                    yField.addItem(5);
                    JPanel myPanel = new JPanel();
                    myPanel.add(new JLabel("Notes:"));
                    myPanel.add(xField);
                    myPanel.add(Box.createHorizontalStrut(15)); // a spacer
                    myPanel.add(new JLabel("Quantity:"));
                    myPanel.add(yField);
                    int input = JOptionPane.showConfirmDialog(null, myPanel,"Please Enter Notes and Quantities Values", JOptionPane.OK_OPTION);
                    if(input == 0){
                        neworder.setNotes(xField.getText());
                        neworder.setQty((Integer) yField.getItemAt(yField.getSelectedIndex()));
                        neworder.setId(table3.getValueAt(row,0).toString());
                        neworder.setName(table3.getValueAt(row,1).toString());
                        String sldprodid = table3.getValueAt(row,1).toString();
                        runtimestructproduct.add(neworder);

                        listorder.add(sldprodid);
                        updateorderlist(listorder);
                    }
                }
            }
        });

        ordernotes.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                super.focusGained(e);
                ordernotes.setText(null);
            }

            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                if(ordernotes.getText().isBlank()){
                    ordernotes.setText("Notes");
                }
            }
        });

        desiredArrivalOrder.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                super.focusGained(e);
                desiredArrivalOrder.setText(null);
            }

            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                if(desiredArrivalOrder.getText().isBlank()){
                    desiredArrivalOrder.setText("Desired arrival time");
                }
            }
        });

        submitorder.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String notes;
                String desarr;
                Boolean takeaway;
                takeaway = takeAwayRadioButton.isSelected();
                notes = ordernotes.getText();
                if (notes.equals("Notes")){
                    notes = null;
                }
                desarr = desiredArrivalOrder.getText();
                if(desarr.equals("Desired arrival time")){
                    desarr = null;
                }
                if (runtimestructpizza.isEmpty() == false || runtimestructproduct.isEmpty() == false){
                    addOrder(notes,desarr,takeaway);
                    runtimestructpizza.removeAll(runtimestructpizza);
                    runtimestructproduct.removeAll(runtimestructproduct);
                    frame.setContentPane(new ApplicationDemo().Main);
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    frame.pack();
                    frame.setVisible(true);
                }
                else{
                    JOptionPane.showMessageDialog(null,"Order is empty");
                }
            }
        });

        DELETELASTINSERTIONButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(runtimestructpizza.isEmpty() == true){
                    JOptionPane.showMessageDialog(null,"Order don't contain pizza,nothing to remove ");
                }
                else{
                    listorder.remove(runtimestructpizza.get(runtimestructpizza.size()-1).getName());
                    updateorderlist(listorder);
                    runtimestructpizza.remove(runtimestructpizza.get(runtimestructpizza.size() -1));
                }

            }
        });

        DELETEPRODUCTButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if(runtimestructproduct.isEmpty() == true){
                    JOptionPane.showMessageDialog(null,"Order doesn't contain product,nothing to remove ");
                }
                else{
                    listorder.remove(runtimestructproduct.get(runtimestructproduct.size()-1).getName());
                    updateorderlist(listorder);
                    runtimestructproduct.remove(runtimestructproduct.get(runtimestructproduct.size() -1));
                }

            }
        });

        table2.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1){
                    panellocard.removeAll();
                    panellocard.add(panellouser);
                    panellocard.revalidate();
                    panellocard.repaint();

                    generatestat(customer);
                    generatestat2(customer);

                }
            }
        });

        BACKButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panellocard.removeAll();
                panellocard.add(panellomain);
                panellocard.revalidate();
                panellocard.repaint();
            }
        });
    }

}
