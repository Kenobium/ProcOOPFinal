package tk.thesenate.JPhotoEdit;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainFrame {
    private JPanel mainPanel;
    private JButton okButton;
    private JButton reduceNoiseButton;
    private JButton blurButton;

    public MainFrame() {
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Process p = Runtime.getRuntime().exec("python EdgeFinding.py b.jpb");
                    BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

                    BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

                    // read the output from the command
                    System.out.println("Here is the standard output of the command:\n");
                    String s;
                    while ((s = stdInput.readLine()) != null) {
                        System.out.println(s);
                    }

                    // read any errors from the attempted command
                    System.out.println("Here is the standard error of the command (if any):\n");
                    while ((s = stdError.readLine()) != null) {
                        System.out.println(s);
                    }
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });
        reduceNoiseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Process p = Runtime.getRuntime().exec("python NoiseRemoval.py b.jpg");
                    BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

                    BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

                    // read the output from the command
                    System.out.println("Here is the standard output of the command:\n");
                    String s;
                    while ((s = stdInput.readLine()) != null) {
                        System.out.println(s);
                    }

                    // read any errors from the attempted command
                    System.out.println("Here is the standard error of the command (if any):\n");
                    while ((s = stdError.readLine()) != null) {
                        System.out.println(s);
                    }
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });
        blurButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Process p = Runtime.getRuntime().exec("python Blur.py b.jpg");
                    BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

                    BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

                    // read the output from the command
                    System.out.println("Here is the standard output of the command:\n");
                    String s;
                    while ((s = stdInput.readLine()) != null) {
                        System.out.println(s);
                    }

                    // read any errors from the attempted command
                    System.out.println("Here is the standard error of the command (if any):\n");
                    while ((s = stdError.readLine()) != null) {
                        System.out.println(s);
                    }
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });
    }


    public static void main(String[] args) {
        JFrame frame = new JFrame("MainFrame");
        frame.setContentPane(new MainFrame().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
