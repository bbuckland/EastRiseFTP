import javax.swing.*;
import java.awt.event.*;


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

    /**
     * our private members
     */



    public file_transfer() {
        launchProgram.addActionListener(this); //General listener call
        launchProgram.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "Eggs are not supposed to be green.");
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
    }
}
