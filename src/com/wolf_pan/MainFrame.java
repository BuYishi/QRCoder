package com.wolf_pan;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.EncodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.qrcode.QRCodeWriter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.event.ChangeEvent;
import javax.swing.filechooser.FileNameExtensionFilter;

public class MainFrame extends JFrame {

    private JTabbedPane tabbedPane;
    private HintTextArea textToEncodeHintTextArea;
    private JTextArea decodedResultTextArea;
    private JLabel encodedImageLabel, qrcodeToDecodeLabel, tipLabel;
    private JButton encodeButton, chooseButton, decodeButton, contactAuthorButton;
    private JPopupMenu popupMenu;
    private JMenuItem copyMenuItem, saveMenuItem;
    private BufferedImage qrcodeBufferedImage;
    private String qrcodePicFilename;
    private static final Logger LOGGER = Logger.getLogger(MainFrame.class.getName());

    private MainFrame() {
        super("QRCoder");
        initializeComponents();
        initializeEventListeners();
    }

    private void initializeComponents() {
        setSize(400, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        tabbedPane = new JTabbedPane();
        JPanel encodePanel = new JPanel(new BorderLayout()), decodePanel = new JPanel(new BorderLayout()), authorPanel = new JPanel(new BorderLayout());
        textToEncodeHintTextArea = new HintTextArea("Type text you want to encode here", 4);
        encodePanel.add(new JScrollPane(textToEncodeHintTextArea), BorderLayout.NORTH);
        encodedImageLabel = new JLabel();
        encodedImageLabel.setHorizontalAlignment(JLabel.CENTER);
        encodePanel.add(encodedImageLabel);
        JPanel southEncodePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        tipLabel = new JLabel("<html><font color=\"blue\" size=\"8\">Tip</font></html>");
        southEncodePanel.add(tipLabel);
        encodeButton = new JButton("Encode");
        southEncodePanel.add(encodeButton);
        encodePanel.add(southEncodePanel, BorderLayout.SOUTH);
        tabbedPane.add("Encode", encodePanel);
        JPanel northPanel = new JPanel(new BorderLayout());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        chooseButton = new JButton("Choose...");
        buttonPanel.add(chooseButton);
        decodeButton = new JButton("Decode");
        buttonPanel.add(decodeButton);
        northPanel.add(buttonPanel, BorderLayout.NORTH);
        decodedResultTextArea = new JTextArea(4, 0);
        decodedResultTextArea.setLineWrap(true);
        decodedResultTextArea.setEditable(false);
        JPanel decodedResultPanel = new JPanel(new BorderLayout());
        JLabel decodedResultLabel = new JLabel("<html><font size=\"5\">Decoded result:</font></html>");
        decodedResultPanel.add(decodedResultLabel, BorderLayout.NORTH);
        decodedResultPanel.add(new JScrollPane(decodedResultTextArea));
        northPanel.add(decodedResultPanel);
        decodePanel.add(northPanel, BorderLayout.NORTH);
        qrcodeToDecodeLabel = new JLabel(new ImageIcon(MainFrame.class.getResource("/images/author.jpg")));
        decodePanel.add(qrcodeToDecodeLabel);
        tabbedPane.add("Decode", decodePanel);
        JLabel authorLabel = new JLabel("<html><br /><font size=\"20\">作者：布伊什<br />QQ: 980639902</font></html>", JLabel.CENTER);
        authorPanel.add(authorLabel, BorderLayout.NORTH);
        JLabel authorQRCodeLabel = new JLabel(new ImageIcon(MainFrame.class.getResource("/images/author.jpg")));
        authorPanel.add(authorQRCodeLabel);
        contactAuthorButton = new JButton("联系作者");
        JPanel contactAuthorButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        contactAuthorButtonPanel.add(contactAuthorButton);
        authorPanel.add(contactAuthorButtonPanel, BorderLayout.SOUTH);
        tabbedPane.add("Author", authorPanel);
        add(tabbedPane);
        popupMenu = new JPopupMenu();
        copyMenuItem = new JMenuItem("Copy");
        popupMenu.add(copyMenuItem);
        saveMenuItem = new JMenuItem("Save");
        popupMenu.add(saveMenuItem);
    }

    private void initializeEventListeners() {
        tabbedPane.addChangeListener((ChangeEvent ev) -> {
            if (tabbedPane.getSelectedIndex() == 0) {
                encodeButton.requestFocus();
            }
        });
        encodeButton.addActionListener((ActionEvent ev) -> {
            onEncodeButtonClicked();
        });
        chooseButton.addActionListener((ActionEvent ev) -> {
            onChooseButtonClicked();
        });
        decodeButton.addActionListener((ActionEvent ev) -> {
            onDecodeButtonClicked();
        });
        contactAuthorButton.addActionListener((ActionEvent ev) -> {
            onContactAuthorButtonClicked();
        });
        encodedImageLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent ev) {
                onEncodedImageLabelClicked(ev);
            }
        });
        tipLabel.addMouseListener(new MouseAdapter() {
            private TipDialog tipDialog;

            @Override
            public void mouseEntered(MouseEvent ev) {
                tipDialog = new TipDialog(MainFrame.this, "右键单击生成的二维码弹出工具菜单", 220, 30);
                tipDialog.setVisible(true);
            }

            @Override
            public void mouseExited(MouseEvent ev) {
                tipDialog.dispose();
            }
        });
        copyMenuItem.addActionListener((ActionEvent ev) -> {
            onCopyMenuItemClicked();
        });
        saveMenuItem.addActionListener((ActionEvent ev) -> {
            onSaveMenuItemClicked();
        });
    }

    private void onEncodeButtonClicked() {
        try {
            String contents = textToEncodeHintTextArea.getText();
            int width = encodedImageLabel.getWidth() - 20;
            int height = encodedImageLabel.getHeight() - 20;
            HashMap<EncodeHintType, String> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            BitMatrix bitMatrix = new QRCodeWriter().encode(contents, BarcodeFormat.QR_CODE, width, height, hints);
            qrcodeBufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            for (int y = 0; y < height; ++y) {
                for (int x = 0; x < width; ++x) {
                    Color color = bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE;
                    qrcodeBufferedImage.setRGB(x, y, color.getRGB());
                }
            }
            encodedImageLabel.setIcon(new ImageIcon(qrcodeBufferedImage));
        } catch (WriterException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onChooseButtonClicked() {
        PicFileChooser picFileChooser = new PicFileChooser();
        if (picFileChooser.showOpenDialog(this) == PicFileChooser.APPROVE_OPTION) {
            qrcodePicFilename = picFileChooser.getSelectedFile().getAbsolutePath();
            qrcodeToDecodeLabel.setIcon(new ImageIcon(qrcodePicFilename));
        }
    }

    private void onDecodeButtonClicked() {
        try {
            BufferedImage qrcodeToDecodeBufferedImage;
            if (qrcodePicFilename == null) {
                qrcodeToDecodeBufferedImage = ImageIO.read(MainFrame.class.getResource("/images/author.jpg"));
            } else {
                qrcodeToDecodeBufferedImage = ImageIO.read(new File(qrcodePicFilename));
            }
            BufferedImageLuminanceSource source = new BufferedImageLuminanceSource(qrcodeToDecodeBufferedImage);
            String decodedText = new QRCodeReader().decode(new BinaryBitmap(new HybridBinarizer(source))).getText();
            decodedResultTextArea.setText(decodedText);
        } catch (ChecksumException | FormatException | NotFoundException | IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            if (ex instanceof NotFoundException) {
                JOptionPane.showMessageDialog(this, "QR code not found", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Cannot decode the QR code", "Unknown Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onContactAuthorButtonClicked() {
        try {
            Desktop.getDesktop().browse(URI.create("tencent://message/?uin=980639902"));
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    private void onEncodedImageLabelClicked(MouseEvent ev) {
        if (ev.getButton() == MouseEvent.BUTTON3) {
            popupMenu.show(encodedImageLabel, ev.getX(), ev.getY());
        }
    }

    private void onCopyMenuItemClicked() {
        if (qrcodeBufferedImage != null) {
            ImageSelection contents = new ImageSelection(qrcodeBufferedImage);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(contents, null);
            TipDialog tipDialog = new TipDialog(this, "Copied to clipboard", 150, 30);
            tipDialog.show(1500);
        }
    }

    private void onSaveMenuItemClicked() {
        if (qrcodeBufferedImage != null) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new FileNameExtensionFilter("PNG (*.png)", "png"));
            fileChooser.setFileFilter(new FileNameExtensionFilter("Bitmap Files (*.bmp)", "bmp"));
            fileChooser.setAcceptAllFileFilterUsed(false);
            if (fileChooser.showSaveDialog(this) == PicFileChooser.APPROVE_OPTION) {
                try {
                    String formatName;
                    if (fileChooser.getFileFilter().getDescription().equals("PNG (*.png)")) {
                        formatName = "png";
                    } else {
                        formatName = "bmp";
                    }
                    File destinationFile = new File(fileChooser.getSelectedFile() + "." + formatName);
                    if (ImageIO.write(qrcodeBufferedImage, formatName, destinationFile)) {
                        TipDialog tipDialog = new TipDialog(this, "Saved to disk", 150, 30);
                        tipDialog.show(1500);
                    }
                } catch (IOException ex) {
                    LOGGER.log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public static void main(String[] args) {
        MainFrame mainFrame = new MainFrame();
        mainFrame.setVisible(true);
    }
}
