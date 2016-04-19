/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.riyoce.restauradorpromo.mains;

import java.util.Scanner;
import mx.riyoce.restauradorpromo.readers.CSVReader;
import mx.riyoce.restauradorpromo.readers.XLSXReader;

/**
 *
 * @author admin
 */
public class Main {

    public static void main(String[] args) {
        XLSXReader reader = new XLSXReader();
        Scanner keyboard = new Scanner(System.in);
        System.out.println("Introduce la dirección absoluta del archivo");
        String path = keyboard.nextLine();        
        
        System.out.println("Introduce el número de opción");
        int option = keyboard.nextInt();
        reader.readFile(path, option);
        
        ///root/promo_files/G4/G4.xlsx
        ///root/promo_files/G4/images/
        
        ///root/promo_files/Sunline/Sunline.xlsx
        ///root/promo_files/Sunline/images/        
        
        ///root/promo_files/Innovation/Innovation.xlsx
        ///root/promo_files/Innovation/images/
        
        ///root/promo_files/Promoopcion/promo.xls
        ///root/promo_files/Promoopcion/images/
        
        ///Users/admin/Downloads/promo/innovation/innovation.xlsx
        ///Users/admin/Downloads/promo/innovation/images/
    }
}
