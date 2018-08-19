/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.maksym.pdfproject;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

/**
 *
 * @author Admin
 */
public class Functions {
    
    public static String start_path = "C:\\";
    
    public static ArrayList<String> textToStrings(String source, int begin, int end){
            ArrayList<String> res = new ArrayList<>();
            int size = source.length()/end;
            if(source.length() < end){
                res.add(source);
                return res;
            }
            //if(source.length() % end  != 0) size +=1; 
            for(int i = 0; i < size; i++){
                String s = source.substring(begin, end);
                res.add(s);
                begin += end;
                end += end;
                if(end > source.length()){
                  res.add(source.substring(begin, source.length()));
                  return res; 
                }
            }
            return res;
    }
    
    public static void displayText(PDPageContentStream stream, int yaxis, ArrayList<String> source) throws IOException{
         for(String str : source){
            stream.beginText();
            stream.newLineAtOffset(25, yaxis);
            stream.setFont(PDType1Font.COURIER, 15);
            stream.showText(str);
            stream.endText();
            yaxis -= 15;
         }
    }
    
    public static boolean isImage(String path){
        return ("jpg".equals(path.substring(path.length() - 3, path.length())) || "png".equals(path.substring(path.length() - 3, path.length())));
    }
    
    public static void saveDocument(PDDocument doc) throws IOException{
        JFileChooser saver = new JFileChooser(new File(start_path));
        saver.setDialogTitle("Save PDF");
        saver.addChoosableFileFilter(new FileNameExtensionFilter("PDF File (.pdf)","pdf"));
        saver.setAcceptAllFileFilterUsed(true);
        saver.setSelectedFile(new File("doc_" + Functions.generateUniqueFileName() +".pdf"));
        int res = saver.showSaveDialog(null);
        if(res == JFileChooser.APPROVE_OPTION){
            File file = saver.getSelectedFile();
            start_path = file.getAbsolutePath();
            if(".pdf".equals(file.getAbsolutePath().substring(file.getAbsolutePath().length() - 4, file.getAbsolutePath().length()))){
                if(!file.exists()){
                    doc.save(file.getAbsolutePath());
                    JOptionPane.showMessageDialog(saver, "PDF saved!");
                }
                else{
                    int response = JOptionPane.showConfirmDialog(null, //
                        "Do you want to replace the existing file?", //
                        "Confirm", JOptionPane.YES_NO_OPTION, //
                        JOptionPane.QUESTION_MESSAGE);
                    if(response == JOptionPane.YES_OPTION) {
                        doc.save(file.getAbsolutePath());
                        JOptionPane.showMessageDialog(saver, "PDF saved!");
                    } 
                    else {saveDocument(doc);}
                }
            }
            else{
                boolean fileExists = new File(file.getAbsolutePath() + ".pdf").exists();
                if(!fileExists){
                    doc.save(file.getAbsolutePath() + ".pdf");
                    JOptionPane.showMessageDialog(saver, "PDF saved!");
                } 
                else{
                    int response = JOptionPane.showConfirmDialog(null, //
                        "Do you want to replace the existing file?", //
                        "Confirm", JOptionPane.YES_NO_OPTION, //
                        JOptionPane.QUESTION_MESSAGE);
                    if(response == JOptionPane.YES_OPTION){
                        doc.save(file.getAbsolutePath());
                        JOptionPane.showMessageDialog(saver, "PDF saved!");
                    } 
                    else saveDocument(doc);
                }
            }
        }
    }
    
    public static String generateUniqueFileName() {
        String filename = "";
        long millis = System.currentTimeMillis();
        String datetime = new Date().toGMTString();
        datetime = datetime.replace(" ", "_");
        datetime = datetime.replace("_GMT", "");
        datetime = datetime.replace("J", "j");
        datetime = datetime.replace(":", "_");
        filename = datetime + "_" + millis;
        return filename;
    }
    
    public static void setImageToLabel(String path, JLabel label) throws IOException{
        final BufferedImage bi = ImageIO.read(new File(path));
        int divider = 3;
        
        int scaled_width = bi.getWidth() / divider;
        int scaled_height = bi.getHeight() / divider;
        
        while(scaled_width > label.getWidth()|| scaled_height > label.getHeight()){
            divider++;
            scaled_width = bi.getWidth() / divider;
            scaled_height = bi.getHeight() / divider;
        }
        
        label.setIcon(new ImageIcon(new ImageIcon(path).getImage().getScaledInstance(scaled_width, scaled_height, Image.SCALE_DEFAULT)));
        label.setText("");
    }
    
    public static void previewPDF(JFileChooser fileopen, JTextField jTextField1, JTextField jTextField3, JTextArea jTextArea1, JLabel jLabel4){
        String s1 = jTextField1.getText();
         String s2 = jTextField3.getText();
         String s3 = jTextArea1.getText();
         ArrayList<String> strings3 = Functions.textToStrings(s3, 0, 60);
         
         
         PDDocument doc = new PDDocument();
         PDPage page = new PDPage();
         
         doc.addPage(page);
         try {
         PDPageContentStream stream = new PDPageContentStream(doc, page);
         
         stream.beginText();
         stream.newLineAtOffset(25, 710);
         stream.setFont(PDType1Font.COURIER_BOLD, 30);
         stream.showText("My PDF File");
         stream.endText();
         
         if(fileopen.getSelectedFile() != null && Functions.isImage(fileopen.getSelectedFile().getAbsolutePath())){
            PDImageXObject img = PDImageXObject.createFromFile(fileopen.getSelectedFile().getAbsolutePath(), doc);
            if(img.getHeight() > img.getWidth()) stream.drawImage(img, 25, 570, 90, 120);
            else if(img.getHeight() < img.getWidth()) stream.drawImage(img, 25, 570, 120, 90);
            else stream.drawImage(img, 25, 570, 100, 100);
         }
         
         stream.beginText();
         stream.newLineAtOffset(25, 540);
         stream.setFont(PDType1Font.COURIER, 20);
         stream.showText(s1 + " " + s2);
         stream.endText();
         
         Functions.displayText(stream, 510, strings3);
         
         stream.close();
         } catch (IOException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        PDFRenderer pdfRenderer = new PDFRenderer(doc);
        try {
            //for (int i = 0; i < doc.getNumberOfPages(); i++)
            //{
            BufferedImage bim = pdfRenderer.renderImageWithDPI(0, 300, ImageType.RGB);
            jLabel4.setIcon(new ImageIcon(new ImageIcon(bim).getImage().getScaledInstance(jLabel4.getWidth(), jLabel4.getHeight(), Image.SCALE_DEFAULT)));
            //}
        } catch (IOException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
