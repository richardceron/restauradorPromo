/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.riyoce.restauradorpromo.readers;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 *
 * @author admin
 */
public class CSVReader {

    public void readCSV(String path) {
        try {
            BufferedReader br = null;
            String line = "";
            
            //InputStreamReader fis = new InputStreamReader(new FileInputStream(path), "ISO-8859-2");
            InputStreamReader fis = new InputStreamReader(new FileInputStream(path), StandardCharsets.ISO_8859_1);
            br = new BufferedReader(fis);
            
            //br = new BufferedReader(new FileReader(path));
                        
            
            while ((line = br.readLine()) != null) {
                String[] producto = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                String clave_producto = producto[0];
                String nombre_producto = producto[1];
                
                String nombre_categoria = producto[2];
                String clave_categoria = generateClave(nombre_categoria);
                
                String nombre_material = producto[3];
                String clave_material = generateClave(nombre_material);
                
                String nombre_color = producto[4];
                String clave_color = generateClave(nombre_color);
                
                System.out.println(nombre_categoria);
            }
            
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Error al leer el csv", e);
        }
    }

    private String generateClave(String name){
        String clave = deAccent(name);
        clave = clave.replace(" ", "_");
        clave = clave.toLowerCase();
        return clave;
    }
    
    private String deAccent(String str) {
        String nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(nfdNormalizedString).replaceAll("");
    }
    
}
