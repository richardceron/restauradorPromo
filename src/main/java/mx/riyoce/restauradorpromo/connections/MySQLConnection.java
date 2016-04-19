/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.riyoce.restauradorpromo.connections;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author admin
 */
public class MySQLConnection {

    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private static final String DB_NAME = "promocionando";
    private static final String DB_URL = "jdbc:mysql://127.0.0.1/" + DB_NAME;

    private static final String USER = "promocionando";
    private static final String PASS = "Kabongo696*";

    Connection conn = null;

    public MySQLConnection() {
        connect();
    }

    private void connect() {
        try {
            Class.forName(JDBC_DRIVER);
            System.out.println("Connecting to database...");
            conn = (Connection) DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("Connecton success...");
        } catch (ClassNotFoundException | SQLException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Error al conectar a la DB", e);
        }
    }

    public boolean insertProducto(String clave, String nombre, String des, Date vigencia, boolean novedad, long cat_id, long mat_id, long color_id) {
        boolean flag = false;
        try {
            String sql = "INSERT INTO PRODUCTO (CLAVE, NOMBRE, DESCRIPCION, FECHAPUBLICACION, FECHAVIGENCIA, NOVEDAD, CATEGORIA_ID, MATERIAL_ID, COLOR_ID) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, clave);
            ps.setString(2, nombre);
            ps.setString(3, des);
            ps.setDate(4, new java.sql.Date(new Date().getTime()));
            ps.setDate(5, new java.sql.Date(vigencia.getTime()));
            ps.setBoolean(6, novedad);
            ps.setLong(7, cat_id);
            
            if (mat_id > 0) {
                ps.setLong(8, mat_id);
            } else{
                ps.setObject(8, null);
            }
            
            if (color_id > 0) {
                ps.setLong(9, color_id);
            } else{
                ps.setObject(9, null);
            }
            
            ps.executeUpdate();
            System.out.println("Se insertó con éxito");
            flag = true;
        } catch (MySQLIntegrityConstraintViolationException de) {
            System.out.println("Ya existe esa clave");
        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Error al insertar el producto", e);
        }
        return flag;
    }
    
    public boolean insertImageProducto(byte[] image, String filename, String mime, long pid){
        boolean flag = false;
        try {
            String sql = "INSERT INTO IMAGENPRODUCTO (ARCHIVO, FILENAME, MIME, PRODUCTO_ID) VALUES (?, ?, ?, ?)";
            
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setBytes(1, image);
            ps.setString(2, filename);
            ps.setString(3, mime);
            ps.setLong(4, pid);
            ps.executeUpdate();            
            System.out.println("Se insertó con éxito");
        } catch (MySQLIntegrityConstraintViolationException de) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Error al insertar el producto", de.getMessage());
        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Error al insertar el producto", e);
        }
        return flag;
    }

    public boolean insertObject(String clave, String nombre, int type) {
        boolean flag = false;
        try {
            String sql = "INSERT INTO TIPO (CLAVE, NOMBRE) VALUES (?, ?)";

            if (type == 0) {
                sql = sql.replace("TIPO", "CATEGORIA");
            }

            if (type == 1) {
                sql = sql.replace("TIPO", "MATERIAL");
            }

            if (type == 2) {
                sql = sql.replace("TIPO", "COLOR");
            }

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, clave);
            ps.setString(2, nombre);
            ps.executeUpdate();
            System.out.println("Se insertó con éxito");
            flag = true;
        } catch (MySQLIntegrityConstraintViolationException de) {
            System.out.println("Ya existe esa clave");
        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Error al insertar la categoria o material", e);
        }
        return flag;
    }

    public long getObjectId(String clave, int type) {
        long id = 0;
        try {
            Statement stmt = conn.createStatement();

            String sql = "SELECT ID FROM TIPO WHERE CLAVE = '" + clave + "' LIMIT 1";

            if (type == 0) {
                sql = sql.replace("TIPO", "CATEGORIA");
            }

            if (type == 1) {
                sql = sql.replace("TIPO", "MATERIAL");
            }

            if (type == 2) {
                sql = sql.replace("TIPO", "COLOR");
            }

            if (type == 3) {
                sql = sql.replace("TIPO", "PRODUCTO");
            }

            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                id = rs.getLong("ID");
            }
        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Error al iobtener el id", e);
        }
        return id;
    }

}
