import it.sauronsoftware.ftp4j.*;

import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.JTree.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import it.sauronsoftware.ftp4j.FTPDataTransferListener;
import java.lang.Thread;
import java.util.Arrays;

/**
 * Created by bbuckland on 3/18/15.
 */
public class file_transfer implements ActionListener {
    /**
     * Our private UI elements
     */
    private JPanel mainContainer;
    private JProgressBar progressBar;
    private JPanel activityStatus;
    private JLabel activity;
    private JButton uploadButton;
    private JButton downloadButton;
    private JButton deleteButton;
    private JButton ftpPermissions;
    private JTree ftpTree;
    private JButton newDirButton;
    private JButton refreshButton;

    /**
     * our private members
     */
    private int transferredBytes;
    static boolean stillRunning, needsDownloadAnimation;
    static String username, server, port, password;
    FTPClient client;
    private DefaultMutableTreeNode rootNode;
    DefaultTreeModel model;
    private File curDir = new File(".");
    final JFileChooser fc = new JFileChooser();


    /**
     * This is our listener for all server transactions
     */
    public class MyTransferListener implements FTPDataTransferListener {
        //on init
        public void started() {
            // Transfer started
            activity.setText("Transfer Started");
            progressBar.setValue(0);
            transferredBytes=0;
        }

        //on transfer
        public void transferred(int length) {
            // Yet other length bytes has been transferred since the last time this
            // method was called
            //transferredBytes+=length;
            //progressBar.setValue(transferredBytes);
            activity.setText("Length:  " + length);
        }

        //on complete
        public void completed() {
            // Transfer completed
            progressBar.setValue(100);
            activity.setText("Upload Complete");
            refreshFTPTree();
        }

        //on abort
        public void aborted() {
            // Transfer aborted
            activity.setText("Transfer Aborted");
        }

        //on fail
        public void failed() {
            // Transfer failed
            activity.setText("Transfer Failed");
        }


    }



    public file_transfer() {
        client = new FTPClient();
        while (!client.isAuthenticated()) {


            try {
                client.connect(server, Integer.parseInt(port));
                client.login(username, password);

                if (client.isAuthenticated()) {
                    activity.setText("Connected!");
                }
                //updateFilePanes(); //Updates the file listing with the files from the ftpRadioButton server now that it is connected.

                //ftpTree.setCellRenderer(new MyTreeCellRenderer());
                model = (DefaultTreeModel) ftpTree.getModel();
                rootNode = new DefaultMutableTreeNode("/", true);
                model.setRoot(rootNode);

                buildFTPTree(rootNode);
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (FTPIllegalReplyException e1) {
                e1.printStackTrace();
            } catch (FTPException e1) {
                e1.printStackTrace();
            }
        }

        downloadButton.addActionListener(this); //General listener call
        downloadButton.addActionListener(new ActionListener() {//Specific listener call
            @Override
            public void actionPerformed(ActionEvent e) {
                //Download file here.
                //downloadFile(fileNameText.getText(), e);
                String filepath = "";
                Object[] hello = ftpTree.getSelectionPath().getPath();

                for (int i = 1; i < hello.length; i++) {
                    filepath += "/" + hello[i];
                }

                fc.setDialogTitle("Specify a file to save");

                int userSelection = fc.showSaveDialog(fc);

                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    File fileToSave = fc.getSelectedFile();
                    //This is where a real application would open the file.
                    downloadFile(filepath,fileToSave,e);
                    System.out.println("Downloading file from server: " + filepath);
                    progressBar.setValue(100); //If this action completes, the progress bar's value is set.
                    activity.setText("Download Complete");
                } else {
                    System.out.println("Open command cancelled by user.");
                }
            }
        });


        uploadButton.addActionListener(this); //General listener call
        uploadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {//Specific listener call
                //Upload file here.
                //uploadFile(fileNameText.getText(), e);
                int returnVal = fc.showOpenDialog(fc);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    //This is where a real application would open the file.
                    uploadFile(file, e);
                    System.out.println("Opening: " + file.getName() + ".");
                } else {
                    System.out.println("Open command cancelled by user.");
                }
            }
        });

        deleteButton.addActionListener(this);
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if(deleteFile(getPath(), e)) {
                    activity.setText("Deleted");
                }
                refreshFTPTree();
            }
        });

        ftpPermissions.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String filepath = getPath();

                // If a file is selected, bring up permissions dialog
                if (filepath != "") file_permissions.main(client, filepath);
            }
        });

        ftpTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                progressBar.setValue(0);
                activity.setText("Ready");
            }
        });


        newDirButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String filepath = getPath();
                String dirName = JOptionPane.showInputDialog(null,
                        "Name of new directory:",
                        "Enter name of directory",
                        JOptionPane.PLAIN_MESSAGE);
                if (dirName != null) {
                    try {
                        client.createDirectory(filepath + "/" + dirName);
                        refreshFTPTree();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    } catch (FTPIllegalReplyException e1) {
                        e1.printStackTrace();
                    } catch (FTPException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });


        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Refreshing Tree");
                refreshFTPTree();
            }
        });
    }

    private String getPath() {
        // Gets file path from currently selected node

        String filepath = "";
        Object[] hello = ftpTree.getSelectionPath().getPath();
        for (int i = 1; i < hello.length; i++) {
            filepath += "/" + hello[i];
        }
        return filepath;

    }

    private void uploadFile(File file, ActionEvent e)
    {
        try {
            client.upload(file, new MyTransferListener());
        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (FTPIllegalReplyException e1) {
            e1.printStackTrace();
        } catch (FTPException e1) {
            e1.printStackTrace();
        } catch (FTPDataTransferException e1) {
            e1.printStackTrace();
        } catch (FTPAbortedException e1) {
            e1.printStackTrace();
        }
    }

    private void downloadFile(String ftpLocation, File localFile, ActionEvent e)
    {
        try {
            client.download(ftpLocation, localFile, new MyTransferListener());
        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (FTPIllegalReplyException e1) {
            e1.printStackTrace();
        } catch (FTPException e1) {
            e1.printStackTrace();
        } catch (FTPDataTransferException e1) {
            e1.printStackTrace();
        } catch (FTPAbortedException e1) {
            e1.printStackTrace();
        }
    }

    private boolean deleteFile(String fileName, ActionEvent e)
    {
        try {
            //activity.setText(Character.toString(fileName.indexOf(1)));
            if(fileName.indexOf('.') != fileName.length()-4 && fileName.indexOf('.') != fileName.length()-5) //If there is no extension.
            {
                //Directory Delete
               client.deleteDirectory(fileName);
            }
            else {
                //Client Delete
                client.deleteFile(fileName);
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (FTPIllegalReplyException e1) {
            e1.printStackTrace();
        } catch (FTPException e1) {
            activity.setText("Error: Directory not empty");
            e1.printStackTrace();
            return false;
        }
/*        } catch (FTPDataTransferException e1) {
            e1.printStackTrace();
        } catch (FTPAbortedException e1) {
            e1.printStackTrace();
        } catch (FTPListParseException e1) {
            e1.printStackTrace();
        }*/
    return true;
    }

    private void refreshFTPTree () {
        rootNode.removeAllChildren();
        buildFTPTree(rootNode);
        model.reload(rootNode);
    }

    private void buildFTPTree(DefaultMutableTreeNode node) {
        DefaultMutableTreeNode child;
        try {
            FTPFile[] list = client.list();

            for (FTPFile file : list) {
                child = new DefaultMutableTreeNode( file.getName());
                node.add(child);
                if (file.getType() == 1) { //is directory
                    client.changeDirectory(file.getName());
                    buildFTPTree(child);
                    client.changeDirectoryUp();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (FTPIllegalReplyException e) {
            e.printStackTrace();
        } catch (FTPException e) {
            e.printStackTrace();
        } catch (FTPDataTransferException e) {
            e.printStackTrace();
        } catch (FTPAbortedException e) {
            e.printStackTrace();
        } catch (FTPListParseException e) {
            e.printStackTrace();
        }

    }

   /* public static void updateProgressBar() {
        while(true != false) {
            progressBar.setValue(progressBar.getValue());
        }
    }*/

   @Override
    public void actionPerformed(ActionEvent e) {
       /*
        try {
            //client.upload(new java.io.File("test.txt"), new MyTransferListener());
            //updateFilePanes();

        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (FTPIllegalReplyException e1) {
            e1.printStackTrace();
        } catch (FTPException e1) {
            e1.printStackTrace();
        } catch (FTPDataTransferException e1) {
            e1.printStackTrace();
        } catch (FTPAbortedException e1) {
            e1.printStackTrace();
        }*/
    }

    /*public void checkDownloadAnimation(){
        if(needsDownloadAnimation == true) {
            needsDownloadAnimation = false;
            for (int i = 0; i < 100; i++)
            progressBar.setValue(i);
        }
    }
    Thread progressBarUpdater = new Thread(() -> {
        checkDownloadAnimation();
    });
*/

    /*private static class MyTreeCellRenderer extends DefaultTreeCellRenderer {

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

            // decide what icons you want by examining the node
            if (value instanceof DefaultMutableTreeNode) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
                if (node.getUserObject() instanceof FTPFile) {
                    // your root node, since you just put a String as a user obj
                    //setIcon(UIManager.getIcon("FileView.computerIcon"));
                    FTPFile theFile = (FTPFile) node.getUserObject();
                    if (theFile.getType() == theFile.TYPE_DIRECTORY) {
                        setIcon(UIManager.getIcon("FileChooser.directoryIcon"));
                    } else {
                        setIcon(UIManager.getIcon("FileChooser.directoryIcon"));
                    }
                } else {
                    setIcon(UIManager.getIcon("FileChooser.directoryIcon"));
                }
            }
            return this;
        }

    }*/

    public static void main(String[] args) {
        server = args[0];
        port = args[1];
        username = args[2];
        password = args[3];



        JFrame frame = new JFrame("file_transfer");
        frame.setContentPane(new file_transfer().mainContainer);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null); //Center it on the screen
        frame.setVisible(true);

        System.out.println("Press Any Key To Continue...");
        new java.util.Scanner(System.in).nextLine();
    }
}
