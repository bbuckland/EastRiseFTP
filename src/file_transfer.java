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
    private JButton launchProgram;
    private JProgressBar progressBar;
    private JLabel activity;
    private JTree ftpTree;

    /**
     * our private members
     */



    public file_transfer() {
        launchProgram.addActionListener(this); //General listener call
        launchProgram.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
    }


   @Override
    public void actionPerformed(ActionEvent e) {
        //Global event handler
    }


    public static void main(String[] args) {

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
