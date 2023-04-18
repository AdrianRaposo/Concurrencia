package superheroes;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.jupiter.api.Test;

class Correccion_S2 {
	
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
	void test_loadEscenas() {
		try {
			this.executeScript("scripts/superheroes_drop_and_create_tables.sql");
		} catch (Exception e) { }
		
		SuperheroesDatabase s = new SuperheroesDatabase();
		assertEquals(68, s.loadEscenas("scripts/escenas_pk_duplicated.csv"));
		assertEquals(0, s.loadEscenas("scripts/escenas_pk_duplicated.csv"));
		assertEquals(0, s.loadEscenas("scripts/escenas.csv"));
		s.closeConnection();
		
		Statement st;
		ResultSet rs;
		this.openConnection();
		try {
			st = conn.createStatement();
			rs = st.executeQuery("SELECT COUNT(*) FROM escena;");
			if (rs.next()) {
				assertEquals(68, rs.getInt(1));
			} else {
				fail();
			}
			if (conn!=null) conn.close();
		} catch (SQLException e) { }
	}

	@Test
	void test_loadProtagoniza() {
		try {
			this.executeScript("scripts/superheroes_drop_and_create_tables.sql");
		} catch (Exception e) { e.printStackTrace();}
		
		SuperheroesDatabase s = new SuperheroesDatabase();
		assertEquals(0, s.loadProtagoniza("scripts/protagonistas_pk_duplicated.csv"));
		assertEquals(22, s.loadProtagoniza("scripts/protagonistas.csv"));
		assertEquals(0, s.loadProtagoniza("scripts/protagonistas.csv"));
		
		Statement st;
		ResultSet rs;
		this.openConnection();
		try {
			st = conn.createStatement();
			rs = st.executeQuery("SELECT COUNT(*) FROM protagoniza;");
			if (rs.next()) {
				assertEquals(11, rs.getInt(1));
			} else {
				fail();
			}
			
			rs = st.executeQuery("SELECT COUNT(*) FROM rival;");
			if (rs.next()) {
				assertEquals(11, rs.getInt(1));
			} else {
				fail();
			}
		} catch (SQLException e) { }
		
		
		try {
			this.executeScript("scripts/superheroes_drop_and_create_tables.sql");
		} catch (Exception e) { }
		
		assertEquals(23, s.loadProtagoniza("scripts/protagonistas_same_sup_vill.csv"));
		s.closeConnection();
		
		try {
			st = conn.createStatement();
			rs = st.executeQuery("SELECT COUNT(*) FROM protagoniza;");
			if (rs.next()) {
				assertEquals(12, rs.getInt(1));
			} else {
				fail();
			}
			
			rs = st.executeQuery("SELECT COUNT(*) FROM rival;");
			if (rs.next()) {
				assertEquals(11, rs.getInt(1));
			} else {
				fail();
			}
			if (conn!=null) conn.close();
		} catch (SQLException e) { }
	}

}
