import net.proteanit.sql.DbUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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

    public int generaterandomidcustomer(){
        Random rd = new Random();
        return rd.nextInt(51);
    }

    //init method per inizializzare fake "chiamata da parte del customer" scegliendo un numero random da 1 a 50(?)
    //richiama metodo connect per la connessione al database e setta anche il logo dell'azienda
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
        /*
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    pst = con.prepareStatement("select id from user where username=? and password=?");
                    pst.setString(1,usernamefield.getText());
                    pst.setString(2, String.valueOf(passwordfield.getPassword()));
                    ResultSet rs = pst.executeQuery();
                    if(rs.next()){
                        JOptionPane.showMessageDialog(null,"Login Successful");
                        panellocard.removeAll();
                        panellocard.add(panellomain);
                        panellocard.repaint();
                        panellocard.revalidate();
                    }
                    else{
                        JOptionPane.showMessageDialog(null,"Password or User incorrect");
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }); */

        submitorder.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panellocard.removeAll();
                panellocard.add(panellouser);
                panellocard.repaint();
                panellocard.revalidate();
            }
        });
        table1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (e.getClickCount() == 1) {
                    int row = table1.getSelectedRow();
                    listorder.add(table1.getValueAt(row,1));
                    System.out.println("questo è l'ordine" +listorder);
                }

            }
        });

        table3.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (e.getClickCount() == 1) {
                    int row = table3.getSelectedRow();
                    //TODO:prova con element collection nome e prezzo, nome e prezzo ... (anche per table1)
                    listorder.add(table3.getValueAt(row,1));
                    System.out.println("questo è l'ordine" +listorder);
                }

            }
        });
    }

}
