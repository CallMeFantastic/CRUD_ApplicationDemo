import net.proteanit.sql.DbUtils;

import javax.swing.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class ApplicationDemo {

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
    private ArrayList <PartialOrder> runtimestruct;
    private String prod_notes;
    private Integer prod_qty;

    //TODO: cambia i setString con relativi cast con i giusti set nei preparedstatement

    public static void main(String[] args) {
        JFrame frame = new JFrame("CRUD_PIZZA");
        frame.setContentPane(new ApplicationDemo().Main);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public void printPartialOrderList(){
        for(PartialOrder x:runtimestruct){
            System.out.println("\n"+x.getId() +"\n"+x.getNotes() + "\n" + x.getQty() +"" +x.getAddons());
        }
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

    public void updateorderlist (List ls){
        DefaultListModel demoList = new DefaultListModel();
        demoList.addElement(ls);
        //TODO: try list of lists in future versions per cercare di avere spaziature oppure anche coppie product,price oppure usa table
        list1.setModel(demoList);
    }

    public int generaterandomidcustomer(){
        Random rd = new Random();
        return rd.nextInt(51);
    }

    //TODO:completa metodo inserimento richiamato da submit, per il momento hai creato una classe per contenere l'ordine che poi verrà iterato ed inserito nel db
    public void addOrder(List ls, String nt, String arr, Boolean tw){
        /*
        for(Object prod: listorder){
            try {
                pst = con.prepareStatement("select * from product where name= ?");
                pst.setString(1,prod.toString());
                ResultSet rs = pst.executeQuery();
                if(rs.next()){
                    pst = con.prepareStatement("select * from pizza where name= ?");
                    pst.setString(1,prod.toString());
                    ResultSet rs2 = pst.executeQuery();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
            pst = con.prepareStatement("INSERT INTO CONTAINSTPROD (orderid,productid,qty,note)" +
                    "VALUES (?,?,?,?)");

            }catch (SQLException e){
            e.printStackTrace();

            }
        }
        try{
            pst = con.prepareStatement("insert into order(fromcustomer,ts,desiredtime,takeaway,numofprod,numofpizza,price,notes)" +
                    " values(?,?,?,?,?,?,?,?)");
            String idcustomer = (String) table2.getValueAt(1,0);
            pst.setString(1,idcustomer);
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            pst.setString(2,dtf.format(now));
            pst.setString(3,arr);
            pst.setBoolean(4,tw);
            PreparedStatement numprodstm = con.prepareStatement("SELECT count(*) FROM" +
                    "");
            pst.executeUpdate();
            System.out.println("Order inserted");
        }catch(SQLException e){
            e.printStackTrace();
        }*/
    }

    /*init method per inizializzare fake "chiamata da parte del customer" scegliendo un numero random da 1 a 50(?)
    richiama metodo connect per la connessione al database e setta anche il logo dell'azienda*/

    public void init(){
        //set enterprise icon
        ImageIcon iconlogo = new ImageIcon("logo.png");
        logolabel.setIcon(iconlogo);
        connect();
        loadpizzatb();
        loadcustomertb(String.valueOf(generaterandomidcustomer()));
        loadproductstb();
    }

    public ApplicationDemo(){
        init();
        runtimestruct = new ArrayList<PartialOrder>();
        listorder = new ArrayList<String>();


        //action listener



        table1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (e.getClickCount() == 1) {
                    int row = table1.getSelectedRow();
                    PartialOrder neworder = new PartialOrder();
                    //TODO:prova con element collection nome e prezzo, nome e prezzo ... (anche per table1) in future versions
                    listorder.add(table1.getValueAt(row,1).toString());
                    System.out.println("questo è l'ordine" +listorder);
                    updateorderlist(listorder);
                }
            }
        });

        table3.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (e.getClickCount() == 1) {
                    PartialOrder neworder = new PartialOrder();
                    int row = table3.getSelectedRow();
                    JTextField xField = new JTextField(40);
                    JComboBox yField = new JComboBox();
                    yField.addItem(1);
                    yField.addItem(2);
                    yField.addItem(3);
                    yField.addItem(4);
                    yField.addItem(5);
                    JComboBox zField = new JComboBox();
                    //TODO: sistema questo combobox devi mettere i nomi degli ingredienti dopo averli "queryati"
                    JPanel myPanel = new JPanel();
                    myPanel.add(new JLabel("Notes:"));
                    myPanel.add(xField);
                    myPanel.add(Box.createHorizontalStrut(15)); // a spacer
                    myPanel.add(new JLabel("Quantity:"));
                    myPanel.add(yField);
                    //TODO: modifica sopra e sotto per far si che crei un pannellocard dal design e glielo setti qui anzichè crearlo tramite codice
                    JOptionPane.showConfirmDialog(null, myPanel,"Please Enter Notes and Quantities Values", JOptionPane.OK_OPTION);
                    //System.out.println("Notes value: " + xField.getText());
                    //System.out.println("Quantities value: " + yField.getItemAt(yField.getSelectedIndex()));
                    neworder.setNotes(xField.getText());
                    neworder.setQty((Integer) yField.getItemAt(yField.getSelectedIndex()));
                    neworder.setId(table3.getValueAt(row,0).toString());
                    String sldprodid = table3.getValueAt(row,0).toString();
                    runtimestruct.add(neworder);
                    //TODO:prova con element collection nome e prezzo, nome e prezzo ... (anche per table1) in future versions
                    listorder.add(sldprodid);
                    System.out.println("questo è l'ordine" +listorder);
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
                String notes = ordernotes.getText();
                String desarr = desiredArrivalOrder.getText();
                Boolean takeaway = takeAwayRadioButton.isSelected();
                System.out.println("" + notes +"\n" +desarr +"\n"+takeaway);
                //addOrder(listorder,notes,desarr,takeaway);
                panellocard.removeAll();
                panellocard.add(panellomain);
                panellocard.repaint();
                panellocard.revalidate();
                //TODO: fix passaggio non refresha la pagina main
            }
        });

    }

}
