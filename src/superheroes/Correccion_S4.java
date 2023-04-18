package superheroes;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.jupiter.api.Test;

class Correccion_S4 {
	
	private Connection conn;
	
	public void openConnection() {
		String addr = "localhost:3306";
		String user = "superheroes_user";
		String pass = "superheroes_pass";
		String db= "superheroes";
		String url = "jdbc:mysql://" + addr + "/" + db;
		
		try {
			if(conn == null || conn.isClosed()) {
				conn = DriverManager.getConnection(url, user, pass);
			}
		} catch (SQLException e) { }
	}

	void executeScript(String filename) {
		File f = null;
		FileReader fr = null;
		BufferedReader br = null;
		String line = null, text = "";
		Statement st = null;

		f = new File(filename);
		this.openConnection();
		try {
			st = conn.createStatement();
			fr = new FileReader (f);
			br = new BufferedReader(fr);
			
			while ((line = br.readLine()) != null) {
				if (line.length() > 0) {
					text += line;
					if (text.charAt(text.length()-1)==';') {
						st.executeUpdate(text);
						text = "";
					} else {
						text += " ";
					}
				}
			}

			if (conn != null) conn.close();
			if (st != null) st.close();
			if (br != null) br.close();
			if (fr != null) fr.close();
		} catch (SQLException | IOException e) {
			System.out.println(e.getMessage());
		}
	}

	@Test
	void test_desenmascara() {
		try {
			this.executeScript("scripts/superheroes_drop_all_tables.sql");
		} catch (Exception e) { }
		
		SuperheroesDatabase s = new SuperheroesDatabase();
		assertFalse(s.desenmascara("Clark", "Kent", "scripts/foto_extraida.jpg"));
		
		try {
			this.executeScript("scripts/superheroes_create_tables.sql");
		} catch (Exception e) { }
		
		assertFalse(s.desenmascara("Clark", "Kent", "scripts/foto_extraida.jpg"));
		
		try {
			this.executeScript("scripts/superheroes_insert_data_photo.sql");
		} catch (Exception e) { }
		
		assertFalse(s.desenmascara("Clark", "Kent", "scripts/foto_extraida.jpg"));
		assertTrue(s.desenmascara("Peter", "Parker", "scripts/foto_extraida.jpg"));
		
		File f_orig = new File("scripts/original.jpg"),
		     f_extraida = new File("scripts/foto_extraida.jpg");
		
		try {
			FileInputStream fis_orig = new FileInputStream(f_orig);
			FileInputStream fis_extraida = new FileInputStream(f_extraida);
			
			byte []b_orig = new byte[128];
			byte []b_extraida = new byte[128];
			
			fis_orig.read(b_orig, 0, 128);
			fis_extraida.read(b_extraida, 0, 128);
			
			assertArrayEquals(b_orig, b_extraida);
		} catch (IOException e) {
			fail();
		}
		
		
		
		s.closeConnection();
	}
	
}
