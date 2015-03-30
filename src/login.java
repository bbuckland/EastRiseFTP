import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import it.sauronsoftware.ftp4j.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
//import com.seaglasslookandfeel.*;

public class login extends JDialog {

    private JPanel loginPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField server;
    private JPasswordField password;
    private JTextField username;
    private JTextField port;
    FTPClient client;

    private static String[] logonParams = new String[4]; //Global variable, bleh!
    public login() {
        setContentPane(loginPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

// call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

// call onCancel() on ESCAPE
        loginPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
// add your code here
        client = new FTPClient();
        while (!client.isAuthenticated()) {
            try {
                logonParams[0] = server.getText();
                logonParams[1] = port.getText();
                logonParams[2] = username.getText();
                logonParams[3] = password.getText();
                //How it should be.

                logonParams[0] = "50.23.218.100";
                logonParams[1] = "21";
                logonParams[2] = "ftp01";
                logonParams[3] = "student";
                //Temporary override so you don't have to retype it everytime.

                client.connect(logonParams[0], Integer.parseInt(logonParams[1]));
                client.login(logonParams[2], logonParams[3]);

                if (client.isAuthenticated()) {
                    System.out.println("Successfully connected!");
                }

            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (FTPIllegalReplyException e1) {
                e1.printStackTrace();
            } catch (FTPException e1) {
                e1.printStackTrace();
            }
        }

        dispose();
    }

    private void onCancel() {
// add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {

        //JFrame frame = new JFrame("login");
        //frame.setContentPane(new login().loginPane);
        //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //frame.pack();
        //frame.setVisible(true);
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }

        login dialog = new login();
        dialog.setUndecorated(true); //Make borderless
        dialog.pack();
        dialog.setLocationRelativeTo(null); //Center it on the screen.
        dialog.setVisible(true);
        file_transfer.main(logonParams);
        System.exit(0);
    }
}
