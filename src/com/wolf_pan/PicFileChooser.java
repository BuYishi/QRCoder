package com.wolf_pan;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

public class PicFileChooser extends JFileChooser {

    public PicFileChooser() {

        FileNameExtensionFilter filter1 = new FileNameExtensionFilter("ICO (*.ico)", "ico");
        FileNameExtensionFilter filter2 = new FileNameExtensionFilter("PNG (*.png)", "png");
        FileNameExtensionFilter filter3 = new FileNameExtensionFilter("TIFF (*.tif;*.tiff)", "tif", "tiff");
        FileNameExtensionFilter filter4 = new FileNameExtensionFilter("GIF (*.gif)", "gif");
        FileNameExtensionFilter filter5 = new FileNameExtensionFilter("JPEG (*.jpg;*.jpeg;*.jpe;*.jfif)", "jpg", "jpeg", "jpe", "jfif");
        FileNameExtensionFilter filter6 = new FileNameExtensionFilter("Bitmap Files (*.bmp)", "bmp", "dib");
        FileNameExtensionFilter filter7 = new FileNameExtensionFilter("All Picture Files", "ico", "png", "tif", "tiff", "gif", "jpg", "jpeg", "jpe", "jfif", "bmp", "dib");
        super.setFileFilter(filter1);
        super.setFileFilter(filter2);
        super.setFileFilter(filter3);
        super.setFileFilter(filter4);
        super.setFileFilter(filter5);
        super.setFileFilter(filter6);
        super.setFileFilter(filter7);
        super.setAcceptAllFileFilterUsed(false);
    }
}
