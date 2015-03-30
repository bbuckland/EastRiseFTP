import it.sauronsoftware.ftp4j.*;

import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import it.sauronsoftware.ftp4j.FTPDataTransferListener;

/**
 * Created by bbuckland on 3/18/15.
 */
public class file_transfer implements ActionListener {
    private JPanel mainContainer;
    private JProgressBar progressBar1;
    private JPanel activityStatus;
    //private JButton goUpButton;
    private JLabel activity;
    private JTextPane ftpFilePane;
    private JTextField fileNameText;
    private JButton uploadButton;
    private JButton goToButton;
    private JButton goUpButton;
    private JButton downloadButton;
    private JButton deleteButton;
    private JTextPane computerFilePane;
    private JRadioButton computerRadioButton;
    private JRadioButton ftpRadioButton;
    static boolean stillRunning;
    static String username, server, port, password;
    FTPClient client;
    private File curDir = new File(".");

    public class MyTransferListener implements FTPDataTransferListener {

        public void started() {
            // Transfer started
            activity.setText("Transfer Started");
        }

        public void transferred(int length) {
            // Yet other length bytes has been transferred since the last time this
            // method was called
            activity.setText("Length:  " + length);
        }

        public void completed() {
            // Transfer completed
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
                updateFilePanes(); //Updates the file listing with the files from the ftpRadioButton server now that it is connected.
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (FTPIllegalReplyException e1) {
                e1.printStackTrace();
            } catch (FTPException e1) {
                e1.printStackTrace();
            }
        }


       /* goUpButton.addActionListener(this); //Adds listener for general actions.
        goUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {//Adds specific listener for this instance happening.
                //JOptionPane.showMessageDialog(null, "You clicked the button!");
            }
        });*/


        downloadButton.addActionListener(this); //General listener call
        downloadButton.addActionListener(new ActionListener() {//Specific listener call
            @Override
            public void actionPerformed(ActionEvent e) {
                //Download file here.
                downloadFile(fileNameText.getText(), e);
            }
        });


        uploadButton.addActionListener(this); //General listener call
        uploadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {//Specific listener call
                //Upload file here.
                uploadFile(fileNameText.getText(), e);
            }
        });

        deleteButton.addActionListener(this);
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                deleteFile(fileNameText.getText(), e);
            }
        });

        goToButton.addActionListener(this);
        goToButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            if (ftpRadioButton.isSelected()) serverNavigateTo(fileNameText.getText());
            else clientNavigateTo(fileNameText.getText());

            }
        });

        goUpButton.addActionListener(this);
        goUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (ftpRadioButton.isSelected()) serverGoUp();
                else clientGoUp();

            }
        });
    }



    private static void doThings() {
        //progressBar1.setValue(progressBar1.getValue()+1);

    } //Does nothing.

    private void clientNavigateTo(String fileName)
    {
        //Throws NullPointerException. Will work on it more
        //String fullPath = curDir.getPath() + fileName;
        //curDir = new File(fullPath);
    }

    private void serverNavigateTo(String fileName)
    {
        try {
            client.changeDirectory(fileName);
        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (FTPIllegalReplyException e1) {
            e1.printStackTrace();
        } catch (FTPException e1) {
            e1.printStackTrace();
        }
    }

    private void clientGoUp ()
    {
        //Throws NullPointerException. Will work on it more
        //String parentDir = curDir.getParent();
        //curDir = new File(parentDir);
    }

    private void serverGoUp () {
        try {
            client.changeDirectoryUp();
        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (FTPIllegalReplyException e1) {
            e1.printStackTrace();
        } catch (FTPException e1) {
            e1.printStackTrace();
        }
    }

    private void uploadFile(String fileName, ActionEvent e)
    {
        try {
            client.upload(new java.io.File(fileName), new MyTransferListener());
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

    private void downloadFile(String fileName, ActionEvent e)
    {
        try {
            File downloadFile = new File(new java.io.File(".").getCanonicalPath() + '\\' + fileName);
            downloadFile.createNewFile();
            client.download(fileName, new java.io.File(fileName));
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

    private static String getAllFilse(File curDir) {

        File[] filesList = curDir.listFiles();
        String[] directoryList = curDir.list(new FilenameFilter() {
            @Override
            public boolean accept(File current, String name) {
                return new File(current, name).isDirectory();
            }
        });
        String dirFiles = "";
        for(String f : directoryList){
            dirFiles += "►";
            dirFiles += f;
            dirFiles += '\n';
        } //Adds files to the list.

        for(File f : filesList){
            dirFiles += f.getName();
               dirFiles += '\n';
        } //Adds files to the list.
                return dirFiles;

    }
    private void updateFilePanes() {
        try {
            FTPFile[] list = client.list();
            //activity.setText("" + list[0].getType());
            String fileList = "";
            for (FTPFile file : list) {
                fileList += file.getName() + "\n";
            }
            ftpFilePane.setText(fileList);

            computerFilePane.setText(getAllFilse(curDir));

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
            } catch (FTPListParseException e1) {
                e1.printStackTrace();
            }
    }

   @Override
    public void actionPerformed(ActionEvent e) {
        try {
            client.upload(new java.io.File("test.txt"), new MyTransferListener());
            updateFilePanes();

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
        while(stillRunning!=true) {
            doThings();
            //System.out.println("Press Any Key To Continue...");
        }
        System.out.println("Press Any Key To Continue...");
        new java.util.Scanner(System.in).nextLine();
    }
}
