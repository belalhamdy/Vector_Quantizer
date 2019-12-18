package com.company;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Hashtable;

public class GUI {

    private JPanel mainpnl;
    private JPanel imageLeftpnl;
    private JPanel imageRightpnl;
    private JLabel leftImage;
    private JLabel rightImage;
    private JPanel numberBlockspnl;
    private JSlider numberBlocksSlider;
    private JButton selectImageButton;
    private JButton saveCompressionFileButton;
    private JButton decompressButton;
    private JSpinner blockSizeSpn;

    private BufferedImage image = new BufferedImage(300, 300, BufferedImage.TYPE_INT_RGB);
    private BufferedImage compressed = new BufferedImage(300, 300, BufferedImage.TYPE_INT_RGB);

    GUI() {
        JFrame form = new JFrame("Vector Quantizer");
        form.setMaximumSize(new Dimension(700, 800));
        form.setPreferredSize(new Dimension(700, 800));
        form.setResizable(true);
        form.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        form.setContentPane(mainpnl);
        form.pack();
        form.setVisible(true);
        form.setLocationRelativeTo(null);

        leftImage.setHorizontalAlignment(SwingConstants.CENTER);
        rightImage.setHorizontalAlignment(SwingConstants.CENTER);
        leftImage.setVerticalAlignment(SwingConstants.CENTER);
        rightImage.setVerticalAlignment(SwingConstants.CENTER);

        initiateSliders();
        selectImageButton.addActionListener(e -> event_selectImageButton());
        numberBlocksSlider.addChangeListener(e -> refreshCompressionResults());
        blockSizeSpn.addChangeListener(e -> refreshCompressionResults());

        refreshCompressionResults();
        saveCompressionFileButton.addActionListener(e -> event_saveCompressionFileButton());
        decompressButton.addActionListener(e -> event_loadCompressionFileButton());
    }

    private void initiateSliders() {
        SpinnerNumberModel spnModel = new SpinnerNumberModel(1, 1, 50, 1);
        blockSizeSpn.setModel(spnModel);

        int MAX_POWER = 7;
        Hashtable<Integer, JLabel> numberOfBlocksLabels = new Hashtable<>();
        numberBlocksSlider.setMinimum(0);
        numberBlocksSlider.setMaximum(MAX_POWER - 1);

        numberBlocksSlider.setPaintLabels(true);
        numberBlocksSlider.setPaintTicks(true);
        numberBlocksSlider.setMajorTickSpacing(1);
        numberBlocksSlider.setValue(3);
        for (int i = 0; i < MAX_POWER; i += 1) {
            numberOfBlocksLabels.put(i, new JLabel((1 << i) + ""));
        }
        numberBlocksSlider.setLabelTable(numberOfBlocksLabels);
    }


    private void event_selectImageButton() {
        JFileChooser jf = new JFileChooser("D:\\");
        if (jf.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            try {
                image = ImageIO.read(jf.getSelectedFile());
                refreshCompressionResults();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void event_saveCompressionFileButton() {
        VectorQuantizer vq = new VectorQuantizer(
                Utilities.readImageToArray(image),
                (int) blockSizeSpn.getValue(),
                1 << (numberBlocksSlider.getValue()));
        JFileChooser jf = new JFileChooser("D:\\");
        if (jf.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            try {
                Utilities.saveCompressionData(vq.compress(), jf.getSelectedFile());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void event_loadCompressionFileButton() {
        JFileChooser jf = new JFileChooser("D:\\");
        if (jf.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            try {
                CompressionData cd = Utilities.readCompressionData(jf.getSelectedFile());
                VectorQuantizer vq = new VectorQuantizer(cd);
                image = Utilities.saveArrayToImage(vq.decompress());
                refreshCompressionResults();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void refreshCompressionResults() {
        //leftImage.setIcon(new ImageIcon(image));
        Dimension leftSize = getScaledDimension(image.getWidth(),image.getHeight());
        leftImage.setIcon(new ImageIcon(image.getScaledInstance(leftSize.width,leftSize.height, Image.SCALE_DEFAULT)));
        VectorQuantizer vq = new VectorQuantizer(
                Utilities.readImageToArray(image),
                (int) blockSizeSpn.getValue(),
                1 << (numberBlocksSlider.getValue()));

        VectorQuantizer qv = new VectorQuantizer(vq.compress());
        BufferedImage right = Utilities.saveArrayToImage(qv.decompress());
        Dimension rightSize = getScaledDimension(right.getWidth(), right.getHeight());
        //rightImage.setIcon(rightImage);
        rightImage.setIcon(new ImageIcon(right.getScaledInstance(rightSize.width, rightSize.height, Image.SCALE_DEFAULT)));
    }

    Dimension getScaledDimension(int width, int height) {

        double widthRatio =  300.0 / width;
        double heightRatio =  300.0 / height;
        double ratio = Math.min(widthRatio, heightRatio);

        return new Dimension((int) (width * ratio),
                (int) (height * ratio));
    }
}
