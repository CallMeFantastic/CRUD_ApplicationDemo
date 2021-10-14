import net.proteanit.sql.DbUtils;

import javax.swing.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
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



    public static void main(String[] args) {
        JFrame frame = new JFrame("CRUD_PIZZA");
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

    public void loadpizza(){
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

    public void loadcustomer(String idcustomer){
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

    public void loadproducts(){
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

    public void updateorder(List ls){
        DefaultListModel demoList = new DefaultListModel();
        demoList.addElement(ls);
        //TODO: try list of lists in future versions
        list1.setModel(demoList);
    }

    public int generaterandomidcustomer(){
        Random rd = new Random();
        return rd.nextInt(51);
    }

    /*
    //TODO:completa metodo, hai una lista di nomi di prodotti
    public void addOrder(List ls){
        try{
            pst = con.prepareStatement("insert into order(fromcustomer,ts,desiredtime,takeaway,numofprod,numofpizza,price,notes) values()");
            pst.setString(1,idcustomer);
            pst.executeUpdate();
            System.out.println("Order inserted");
        }catch(SQLException e){
            e.printStackTrace();
        }

    }*/

    /*init method per inizializzare fake "chiamata da parte del customer" scegliendo un numero random da 1 a 50(?)
    richiama metodo connect per la connessione al database e setta anche il logo dell'azienda*/

    public void init(){
        //set enterprise icon
        ImageIcon iconlogo = new ImageIcon("logo.png");
        logolabel.setIcon(iconlogo);
        connect();
        loadpizza();
        loadcustomer(String.valueOf(generaterandomidcustomer()));
        loadproducts();

    }

    public ApplicationDemo(){
        init();
        //action listener
        listorder = new ArrayList<>();

        table1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (e.getClickCount() == 1) {
                    int row = table1.getSelectedRow();
                    //TODO:prova con element collection nome e prezzo, nome e prezzo ... (anche per table1) in future versions
                    listorder.add(table1.getValueAt(row,1));
                    System.out.println("questo è l'ordine" +listorder);
                    updateorder(listorder);
                }

            }
        });

        table3.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (e.getClickCount() == 1) {
                    int row = table3.getSelectedRow();
                    //TODO:prova con element collection nome e prezzo, nome e prezzo ... (anche per table1) in future versions
                    listorder.add(table3.getValueAt(row,1));
                    System.out.println("questo è l'ordine" +listorder);
                    updateorder(listorder);
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
            }
        });

    }

}
