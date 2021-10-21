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
    private Connection con;
    private PreparedStatement pst;
    private ArrayList listorder;
    private ArrayList <PartialOrderPizza> runtimestructpizza;
    private ArrayList <PartialOrderProduct> runtimestructproduct;


    //TODO: cambia i setString con relativi cast con i giusti set nei preparedstatement

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
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void loadpizzatb(){
        try{
            pst = con.prepareStatement("select * from pizza");
            ResultSet rs = pst.executeQuery();
            table1.setModel(DbUtils.resultSetToTableModel(rs));
            //abbiamo importato mediante dipendenza una libreria esterna rs2xml.jar
            //che presenta il metodo DbUtils per creare e riempire dinamicamente una tabella Jtable.
        }catch(SQLException e2){
            e2.printStackTrace();
        }
    }

    public void loadcustomertb(String idcustomer){
        try{
            pst = con.prepareStatement("select * from customer where id=?");
            pst.setString(1,idcustomer);
            ResultSet rs = pst.executeQuery();
            table2.setModel(DbUtils.resultSetToTableModel(rs));
            //abbiamo importato mediante dipendenza una libreria esterna rs2xml.jar
            //che presenta il metodo DbUtils per creare e riempire dinamicamente una tabella Jtable.
        }catch(SQLException e2){
            e2.printStackTrace();
        }
    }

    public void loadproductstb(){
        try{
            pst = con.prepareStatement("select * from product");
            ResultSet rs = pst.executeQuery();
            table3.setModel(DbUtils.resultSetToTableModel(rs));
            //abbiamo importato mediante dipendenza una libreria esterna rs2xml.jar
            //che presenta il metodo DbUtils per creare e riempire dinamicamente una tabella Jtable.
        }catch(SQLException e2){
            e2.printStackTrace();
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

    public void init(){
        //set enterprise icon
        ImageIcon iconlogo = new ImageIcon("logo.png");
        logolabel.setIcon(iconlogo);
        connect();
        loadpizzatb();
        loadcustomertb(String.valueOf(generaterandomidcustomer()));
        loadproductstb();
    }

    //TODO: Snellisci metodo, oltre a suddividere la roba potresti passare da 4 foreach a 2 facendo prima inserimento order ma senza alcuni valori ed aggiungerli dopo con un update
    //TODO: gestisci in modo consono anche i try catch magari ne serve solo uno ad inizio metodo e basta a meno che tu non decida di generare diverse except
    public void addOrder(String nt, String arr, Boolean tw) {
        int numofprod = 0;
        int numofpizza = 0;
        float price = 0.0F;

        for (PartialOrderProduct x : runtimestructproduct) {
            try {
                pst = con.prepareStatement("SELECT id,price from product where name=?",ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                pst.setString(1, x.getName());
                ResultSet rs = pst.executeQuery();
                while (rs.next()) {
                    numofprod = numofprod + x.getQty();
                    price = price + rs.getFloat("PRICE") * x.getQty();
                    }
            } catch (SQLException e){
                e.printStackTrace();
            }
        }
        for (PartialOrderPizza x : runtimestructpizza){
            try {
                pst = con.prepareStatement("SELECT pizzaid,price from pizza where name=?",ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
                pst.setString(1, x.getName());
                ResultSet rs = pst.executeQuery();
                while (rs.next()) {
                    numofpizza = numofpizza + x.getQty();
                    System.out.println("il numero di pizze è:"+numofpizza + "\n");
                    price = price + rs.getFloat("PRICE") * x.getQty();
                    System.out.println("Il price attuale è: "+price +"\n");
                }
            } catch (SQLException e){
                e.printStackTrace();
            }
        }
        try {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            pst = con.prepareStatement("INSERT into customerorder (fromcustomer,ts,desiredtime,takeaway,numofprod,numofpizza,price,notes) values (?,?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
            pst.setInt(1, (Integer) table2.getValueAt(0,0));
            pst.setString(2, dtf.format(now));
            pst.setString(3, arr);
            pst.setBoolean(4,tw);
            pst.setInt(5,numofprod);
            pst.setInt(6,numofpizza);
            pst.setDouble(7,price);
            pst.setString(8,nt);
            pst.executeUpdate();
            ResultSet rs = pst.getGeneratedKeys();
            rs.next();
            int orderid = rs.getInt(1);
            for (PartialOrderProduct x:runtimestructproduct) {
                pst = con.prepareStatement("INSERT INTO containsprod (orderid,productid,qty,note) values (?,?,?,?)");
                pst.setInt(1,orderid);
                pst.setInt(2,Integer.valueOf(x.getId()));
                pst.setInt(3,x.getQty());
                pst.setString(4,x.getNotes());
                pst.executeUpdate();
            }
            for (PartialOrderPizza x:runtimestructpizza) {
                pst = con.prepareStatement("INSERT INTO containspizza (orderid,pizzaid,addon1,addon2,addon3,qty,note) values (?,?,?,?,?,?,?)");
                pst.setInt(1,orderid);
                pst.setInt(2,Integer.valueOf(x.getId()));
                pst.setString(3,x.getAddons().get(0));
                //TODO: fix multiple addon that can be null, il problema è che facendo get sull'Arraylist anzichè tornare null ti da un errore out of index
                pst.setString(4,"");
                pst.setString(5,"");
                pst.setInt(6,x.getQty());
                pst.setString(7,x.getNotes());
                pst.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public ApplicationDemo(){
        runtimestructpizza = new ArrayList<PartialOrderPizza>();
        runtimestructproduct = new ArrayList<PartialOrderProduct>();

        listorder = new ArrayList<String>();


        table1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (e.getClickCount() == 1) {
                    int row = table1.getSelectedRow();
                    PartialOrderPizza neworder = new PartialOrderPizza();

                    JTextField xField = new JTextField(40);
                    JComboBox yField = new JComboBox();
                    yField.addItem(1);
                    yField.addItem(2);
                    yField.addItem(3);
                    yField.addItem(4);
                    yField.addItem(5);
                    //TODO: sistema questo combobox devi mettere i nomi degli ingredienti dopo averli "queryati" oppure puoi hardcodarli semplicemente ed anche se aggiungi un ingrediente modifichi la struttura aggiungendo l'elemento
                    JComboBox zField = new JComboBox();
                    zField.addItem("Patatine");
                    zField.addItem("Funghi");
                    zField.addItem("Prosciutto");
                    zField.addItem("Crema spalmabile");
                    zField.addItem("Porcini");
                    JPanel myPanel = new JPanel();
                    myPanel.add(new JLabel("Notes:"));
                    myPanel.add(xField);
                    myPanel.add(Box.createHorizontalStrut(15)); // a spacer
                    myPanel.add(new JLabel("Quantity:"));
                    myPanel.add(yField);
                    myPanel.add(new JLabel("Addons"));
                    myPanel.add(zField);
                    //TODO: modifica sopra e sotto per far si che crei un pannellocard dal design e glielo setti qui anzichè crearlo tramite codice - EDIT CI HAI PROVATO NON SEMBRA FUNZIONARE LO DEVI CREARE TIPO RUNTIME IL PANEL
                    //TODO: modifica il fatto che non sia corretto usare Jcombobox per selezione addons dato che così ne puoi selezionare solo uno
                    JOptionPane.showConfirmDialog(null, myPanel,"Please Enter Notes, Addons and Quantities Values", JOptionPane.OK_OPTION);


                    ArrayList <String> ls = new ArrayList<>();
                    neworder.setNotes(xField.getText());
                    neworder.setQty((Integer) yField.getItemAt(yField.getSelectedIndex()));
                    neworder.setId(table1.getValueAt(row,0).toString());
                    neworder.setName(table1.getValueAt(row,1).toString());
                    ls.add((String) zField.getItemAt(zField.getSelectedIndex()));
                    neworder.setAddons(ls);
                    String sldprodid = table1.getValueAt(row,1).toString();
                    System.out.println("Id: "+neworder.getId() + "\n Name: "+neworder.getName() + "\n Qty" + neworder.getQty() + "\n Notes:" + neworder.getNotes() + "\n Addon:" + neworder.getAddons().get(0));
                    runtimestructpizza.add(neworder);

                    /*for(PartialOrderPizza x:runtimestructpizza){
                        System.out.println("\n" +x.getId() + "\n" + x.getAddons() + "\n" +x.getQty() + "" +x.getNotes());
                    }*/

                    listorder.add(sldprodid);
                    //System.out.println("questo è l'ordine" +listorder);
                    updateorderlist(listorder);
                }
            }
        });

        table3.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
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
                    //TODO: modifica sopra e sotto per far si che crei un pannellocard dal design e glielo setti qui anzichè crearlo tramite codice - EDIT CI HAI PROVATO NON SEMBRA FUNZIONARE LO DEVI CREARE TIPO RUNTIME IL PANEL
                    JOptionPane.showConfirmDialog(null, myPanel,"Please Enter Notes and Quantities Values", JOptionPane.OK_OPTION);

                    neworder.setNotes(xField.getText());
                    neworder.setQty((Integer) yField.getItemAt(yField.getSelectedIndex()));
                    neworder.setId(table3.getValueAt(row,0).toString());
                    neworder.setName(table3.getValueAt(row,1).toString());
                    String sldprodid = table3.getValueAt(row,1).toString();
                    runtimestructproduct.add(neworder);

                    /*for(PartialOrderProduct x:runtimestructproduct){
                        System.out.println("\n" +x.getId() + "\n" +x.getQty() + "\n" +x.getNotes());
                    }*/
                    listorder.add(sldprodid);
                    //System.out.println("questo è l'ordine\n" +listorder);
                    updateorderlist(listorder);
                }
            }
        });

        //TODO: fix that text disappears if you start clicking both textfields ordernotes and desiredarrivaltextfield in future versions
        ordernotes.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                super.focusGained(e);
                ordernotes.setText("");
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
                desiredArrivalOrder.setText("");
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
                if(ordernotes.getText() != "Notes")
                    notes = ordernotes.getText();
                else
                    notes = null;
                if(desiredArrivalOrder.getText() != "Desired arrival time")
                    desarr = desiredArrivalOrder.getText();
                else
                    desarr = null;
                addOrder(notes,desarr,takeaway);
                runtimestructpizza.removeAll(runtimestructpizza);
                runtimestructproduct.removeAll(runtimestructproduct);
                frame.setContentPane(new ApplicationDemo().Main);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.pack();
                frame.setVisible(true);
            }
        });
    }

}
