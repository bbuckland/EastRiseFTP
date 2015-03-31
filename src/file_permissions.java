import it.sauronsoftware.ftp4j.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.IOException;

public class file_permissions extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JLabel ownerTitle;
    private JLabel groupTitle;
    private JLabel allTitle;
    private JCheckBox ownerRead;
    private JCheckBox ownerWrite;
    private JCheckBox ownerExe;
    private JCheckBox groupRead;
    private JCheckBox groupWrite;
    private JCheckBox groupExe;
    private JCheckBox allRead;
    private JCheckBox allWrite;
    private JCheckBox allExe;

    static FTPClient client;
    static String theFile;

    public file_permissions() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        setChecks();

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onSubmit();
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
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onSubmit() {
        setPerms();

        dispose();
    }

    private void onCancel() {
        dispose();
    }

    private void setChecks() {
        // Sets check marks on dialog to current permissions

    }

    private void setPerms() {
        // Sets permissions based on check marks
        int oPerm = 0;  // Owner permissions
        int gPerm = 0;  // Group permissions
        int aPerm = 0;  // All (other) permissions

        // Set owner permissions digit
        if (ownerRead.isSelected()) oPerm += 4;     // Read
        if (ownerWrite.isSelected()) oPerm += 2;    // Write
        if (ownerExe.isSelected()) oPerm += 1;      // Execute

        // Set group permissions digit
        if (groupRead.isSelected()) gPerm += 4;     // Read
        if (groupWrite.isSelected()) gPerm += 2;    // Write
        if (groupExe.isSelected()) gPerm += 1;      // Execute

        // Set all permissions digit
        if (allRead.isSelected()) aPerm += 4;       // Read
        if (allWrite.isSelected()) aPerm += 2;      // Write
        if (allExe.isSelected()) aPerm += 1;        // Execute

        // Concatenate digits into chmod code
        String perms = Integer.toString(oPerm) + Integer.toString(gPerm) + Integer.toString(aPerm);
        //System.out.println(perms); // just for testing

        FTPReply chmodReply = null;
        try {
            // Set file permissions
            chmodReply = client.sendSiteCommand("chmod " + perms + " " + theFile);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FTPIllegalReplyException e) {
            e.printStackTrace();
        }
        System.out.println(chmodReply.toString());
    }

    public static void main(FTPClient inClient, String inFile) {
        client = inClient;
        theFile = inFile;

        file_permissions dialog = new file_permissions();
        dialog.pack();
        dialog.setLocationRelativeTo(null); //Center it on the screen.
        dialog.setVisible(true);
    }
}
