import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class ApplicationDemo {
    private JPanel panellogin;
    private JPanel panellomain;
    private JPanel panellouser;
    private JPanel panelloproduct;
    private JPanel panellocard;
    private JLabel logolabel;
    private JPanel Main;
    private JTextField usernamefield;
    private JPasswordField passwordfield;
    private JButton loginButton;
    private JButton button1;


    public static void main(String[] args) {
        JFrame frame = new JFrame("CRUD_O");
        frame.setContentPane(new ApplicationDemo().Main);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
    Connection con;
    PreparedStatement pst;

    public boolean connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost/enterprisedb", "root", "");
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

    public ApplicationDemo(){
        //set enterprise icon
        ImageIcon iconlogo = new ImageIcon("logo.png");
        logolabel.setIcon(iconlogo);
        connect();

        //action listener
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //TODO: manage hash function in db
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
                        //TODO: manage and divide the two options ( user or password or both incorrect)
                        JOptionPane.showMessageDialog(null,"Password or User incorrect");
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panellocard.removeAll();
                panellocard.add(panellouser);
                panellocard.repaint();
                panellocard.revalidate();
            }
        });
    }

}
