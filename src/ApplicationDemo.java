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

    //TODO:completa metodo inserimento richiamato da submit, per il momento hai creato una classe per contenere l'ordine che poi verrà iterato ed inserito nel db
    public void addOrder(String nt, String arr, Boolean tw){

        for(PartialOrder x:runtimestruct){

        }


        try {
            pst = con.prepareStatement("INSERT INTO ");
        }catch (SQLException e){
            e.printStackTrace();
        }


    }

    public ApplicationDemo(){
        init();
        runtimestruct = new ArrayList<PartialOrder>();
        listorder = new ArrayList<String>();


        table1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (e.getClickCount() == 1) {
                    int row = table1.getSelectedRow();
                    PartialOrder neworder = new PartialOrder();

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
                    ls.add((String) zField.getItemAt(zField.getSelectedIndex()));
                    neworder.setAddons(ls);
                    String sldprodid = table1.getValueAt(row,1).toString();
                    runtimestruct.add(neworder);

                    for(PartialOrder x:runtimestruct){
                        System.out.println("\n" +x.getId() + "\n" + x.getAddons() + "\n" +x.getQty() + "" +x.getNotes());
                    }

                    listorder.add(sldprodid);
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
                    String sldprodid = table3.getValueAt(row,1).toString();
                    runtimestruct.add(neworder);
                    for(PartialOrder x:runtimestruct){
                        System.out.println("\n" +x.getId() + "\n" + x.getAddons() + "\n" +x.getQty() + "\n" +x.getNotes());
                    }
                    listorder.add(sldprodid);
                    System.out.println("questo è l'ordine\n" +listorder);
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
                System.out.println("\n" + notes +"\n" +desarr +"\n"+takeaway);
                addOrder(notes,desarr,takeaway);
                runtimestruct.removeAll(runtimestruct);
                panellocard.removeAll();
                panellocard.add(panellomain);
                panellocard.repaint();
                panellocard.revalidate();
                //TODO: fix passaggio non refresha la pagina main
            }
        });
    }

}
