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
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {

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
        int oPerm = 0;
        int gPerm = 0;
        int aPerm = 0;

        if (ownerRead.isSelected()) oPerm += 4;
        if (ownerWrite.isSelected()) oPerm += 2;
        if (ownerExe.isSelected()) oPerm += 1;
        if (groupRead.isSelected()) gPerm += 4;
        if (groupWrite.isSelected()) gPerm += 2;
        if (groupExe.isSelected()) gPerm += 1;
        if (allRead.isSelected()) aPerm += 4;
        if (allWrite.isSelected()) aPerm += 2;
        if (allExe.isSelected()) aPerm += 1;

        String perms = Integer.toString(oPerm) + Integer.toString(gPerm) + Integer.toString(aPerm);
        System.out.println(perms);

        FTPReply lsReply = null;
        try {
            lsReply = client.sendSiteCommand("chmod " + perms + " " + theFile);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FTPIllegalReplyException e) {
            e.printStackTrace();
        }
        System.out.println(lsReply.toString());
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
