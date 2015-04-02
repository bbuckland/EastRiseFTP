import it.sauronsoftware.ftp4j.*;

import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.JTree.*;
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
    private JPanel mainContainer;
    private JProgressBar progressBar;
    private JPanel activityStatus;
    private JLabel activity;
    private JButton uploadButton;
    private JButton downloadButton;
    private JButton deleteButton;
    private JTree ftpTree;
    private int transferredBytes;
    static boolean stillRunning;
    static String username, server, port, password;
    FTPClient client;
    private DefaultMutableTreeNode rootNode;
    DefaultTreeModel model;
    private File curDir = new File(".");
    final JFileChooser fc = new JFileChooser();


    public class MyTransferListener implements FTPDataTransferListener {

        public void started() {
            // Transfer started
            activity.setText("Transfer Started");
            progressBar.setValue(0);
            transferredBytes=0;
        }

        public void transferred(int length) {
            // Yet other length bytes has been transferred since the last time this
            // method was called
            transferredBytes+=length;
            progressBar.setValue(transferredBytes);
            activity.setText("Length:  " + length);
        }

        public void completed() {
            // Transfer completed
            progressBar.setValue(100);
            activity.setText("Transfer Complete");
        }

        public void aborted() {
            // Transfer aborted
            activity.setText("Transfer Aborted");
        }

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
                String localfile = "";
                Object[] hello = ftpTree.getSelectionPath().getPath();
                for (int i = 1; i < hello.length; i++) {
                    filepath += "/" + hello[i];
                    localfile =  (String)hello[i];
                }
                downloadFile(filepath,localfile,e);
                System.out.println("Downloading file from server: " + filepath);
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

                //deleteFile(fileNameText.getText(), e);
            }
        });

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

    private void downloadFile(String ftpLocation, String localName, ActionEvent e)
    {
        try {
            File downloadFile = new File(new java.io.File(".").getCanonicalPath() + '\\' + localName);
            downloadFile.createNewFile();
            client.download(ftpLocation, new java.io.File(localName));
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

    private void deleteFile(String fileName, ActionEvent e)
    {
        try {
            client.deleteFile(fileName);
        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (FTPIllegalReplyException e1) {
            e1.printStackTrace();
        } catch (FTPException e1) {
            e1.printStackTrace();
        }
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

    public static void main(String[] args) {
        server = args[0];
        port = args[1];
        username = args[2];
        password = args[3];

        Thread progressBarUpdater = new Thread(() -> {
            //updateProgressBar();
        });

        JFrame frame = new JFrame("file_transfer");
        frame.setContentPane(new file_transfer().mainContainer);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null); //Center it on the screen
        frame.setVisible(true);
        while(stillRunning!=true) {

            //System.out.println("Press Any Key To Continue...");
        }
        System.out.println("Press Any Key To Continue...");
        new java.util.Scanner(System.in).nextLine();
    }
}
