package com.company;

import javax.swing.*;
import java.awt.*;
import java.util.Hashtable;

public class GUI {
    public static final int DISPLAY_IMAGE_SIZE = 300;
    private JPanel mainpnl;
    private JPanel imageLeftpnl;
    private JPanel imageRightpnl;
    private JLabel leftImage;
    private JLabel rightImage;
    private JButton lecutreExampleCompressionButton;
    private JButton lectureExampleDecompressionButton;
    private JPanel numberBlockspnl;
    private JSlider numberBlocksSlider;
    private JButton compressButton;
    private JButton selectImageButton;
    private JButton saveCompressionFileButton;
    private JTextField compressionDataFile;
    private JButton selectFileButton;
    private JButton decompressButton;
    private JButton selectOriginalImageButton;
    private JSpinner blockSizeSpn;

    GUI(){
        JFrame form = new JFrame("Vector Quantizer");
        form.setMaximumSize(new Dimension(700,800));
        form.setPreferredSize(new Dimension(700,800));
        form.setResizable(true);
        form.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        form.setContentPane(mainpnl);
        form.pack();
        form.setVisible(true);
        form.setLocationRelativeTo(null);


        initiateSliders();
    }
    private void initiateSliders(){
        SpinnerNumberModel spnModel = new SpinnerNumberModel(1, 1, 50, 1);
        blockSizeSpn.setModel(spnModel);


        int MAX_POWER = 7;
        Hashtable<Integer, JLabel> numberOfBlocksLabels = new Hashtable<>();
        numberBlocksSlider.setMinimum(0);
        numberBlocksSlider.setMaximum(MAX_POWER-1);
        numberBlocksSlider.setPaintLabels(true);
        numberBlocksSlider.setPaintTicks(true);
        numberBlocksSlider.setMajorTickSpacing(1);
        for (int i = 0 ; i<MAX_POWER ; i+=1){ // ADDS TO 64
            numberOfBlocksLabels.put(i,new JLabel((1<<i)+""));
        }
        numberBlocksSlider.setLabelTable(numberOfBlocksLabels);
    }
}
