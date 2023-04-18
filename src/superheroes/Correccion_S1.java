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

class Correccion_S1 {
	
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
	void test_openConnection() {
		SuperheroesDatabase s = new SuperheroesDatabase();
		assertTrue(s.openConnection());
		assertFalse(s.openConnection());
	}

	@Test
	void test_closeConnection() {
		SuperheroesDatabase s = new SuperheroesDatabase();
		assertTrue(s.closeConnection());
		s.openConnection();
		assertTrue(s.closeConnection());
		assertTrue(s.closeConnection());
	}

	@Test
	void test_createTableEscena() {
		try {
			this.executeScript("scripts/superheroes_drop_tables.sql");
		} catch (Exception e) { }
		
		SuperheroesDatabase s = new SuperheroesDatabase();
		assertTrue(s.createTableEscena());
		assertFalse(s.createTableEscena());
		s.closeConnection();
		
		Statement st;
		ResultSet rs;
		this.openConnection();
		try {
			st = conn.createStatement();
			rs = st.executeQuery("SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='escena';");
			boolean columnas[] = {false, false, false, false};
			int n_columnas = 0;
			while (rs.next()) {
				String columna = rs.getString("COLUMN_NAME");
				String data_type = rs.getString("DATA_TYPE");
				if (columna.equals("id_pelicula") && data_type.equals("int")) columnas[0]=true;
				if (columna.equals("n_orden") && data_type.equals("int")) columnas[1]=true;
				if (columna.equals("titulo") && data_type.equals("varchar")) columnas[2]=true;
				if (columna.equals("duracion") && data_type.equals("int")) columnas[3]=true;
				n_columnas++;
			}
			assertArrayEquals(new boolean[] {true, true, true, true}, columnas);
			assertEquals(columnas.length, n_columnas);
			
			rs = st.executeQuery("SELECT * FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE WHERE TABLE_NAME='escena';");
			boolean pk[] = {false, false};
			int n_pk = 0;
			boolean fk[] = {false};
			int n_fk = 0;
			while (rs.next()) {
				String columna = rs.getString("COLUMN_NAME");
				String cons_type = rs.getString("CONSTRAINT_NAME");
				if (cons_type.equals("PRIMARY")) {
					if (columna.equals("id_pelicula")) pk[0]=true;
					if (columna.equals("n_orden")) pk[1]=true;
					n_pk++;
				} else {
					if (columna.equals("id_pelicula") && rs.getString("REFERENCED_TABLE_NAME").equals("pelicula")) {
						fk[0]=true;
					}
					n_fk++;
				}
			}
			assertArrayEquals(new boolean[] {true, true}, pk);
			assertEquals(pk.length, n_pk);
			assertArrayEquals(new boolean[] {true}, fk);
			assertEquals(fk.length, n_fk);
			
			if (conn!=null) conn.close();
		} catch (SQLException e) { }
	}

	@Test
	void test_createTableRival() {
		try {
			this.executeScript("scripts/superheroes_drop_tables.sql");
		} catch (Exception e) { }
		
		SuperheroesDatabase s = new SuperheroesDatabase();
		assertTrue(s.createTableRival());
		assertFalse(s.createTableRival());
		s.closeConnection();
		
		Statement st;
		ResultSet rs;
		this.openConnection();
		try {
			st = conn.createStatement();
			rs = st.executeQuery("SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='rival';");
			boolean columnas[] = {false, false, false};
			int n_columnas = 0;
			while (rs.next()) {
				String columna = rs.getString("COLUMN_NAME");
				String data_type = rs.getString("DATA_TYPE");
				if (columna.equals("id_sup") && data_type.equals("int")) columnas[0]=true;
				if (columna.equals("id_villano") && data_type.equals("int")) columnas[1]=true;
				if (columna.equals("fecha_primer_encuentro") && data_type.equals("date")) columnas[2]=true;
				n_columnas++;
			}
			assertArrayEquals(new boolean[] {true, true, true}, columnas);
			assertEquals(columnas.length, n_columnas);
			
			rs = st.executeQuery("SELECT * FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE WHERE TABLE_NAME='rival';");
			boolean pk[] = {false, false};
			int n_pk = 0;
			boolean fk[] = {false, false};
			int n_fk = 0;
			while (rs.next()) {
				String columna = rs.getString("COLUMN_NAME");
				String cons_type = rs.getString("CONSTRAINT_NAME");
				if (cons_type.equals("PRIMARY")) {
					if (columna.equals("id_sup")) pk[0]=true;
					if (columna.equals("id_villano")) pk[1]=true;
					n_pk++;
				} else {
					if (columna.equals("id_sup") && rs.getString("REFERENCED_TABLE_NAME").equals("superheroe")) {
						fk[0]=true;
					}
					if (columna.equals("id_villano") && rs.getString("REFERENCED_TABLE_NAME").equals("villano")) {
						fk[1]=true;
					}
					n_fk++;
				}
			}
			assertArrayEquals(new boolean[] {true, true}, pk);
			assertEquals(pk.length, n_pk);
			assertArrayEquals(new boolean[] {true, true}, fk);
			assertEquals(fk.length, n_fk);
			
			if (conn!=null) conn.close();
		} catch (SQLException e) { }
	}

}
