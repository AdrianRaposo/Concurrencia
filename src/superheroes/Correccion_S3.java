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

class Correccion_S3 {
	
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
	void test_catalogo() {
		try {
			this.executeScript("scripts/superheroes_drop_all_tables.sql");
		} catch (Exception e) { }
		
		SuperheroesDatabase s = new SuperheroesDatabase();
		assertEquals(null, s.catalogo());
		
		try {
			this.executeScript("scripts/superheroes_create_tables.sql");
		} catch (Exception e) { }
		
		assertEquals("{}", s.catalogo());
		
		try {
			this.executeScript("scripts/superheroes_insert_data.sql");
		} catch (Exception e) { }
		
		assertEquals("{Avengers: Endgame, Batman: The Killing Joke, Capitan America: Civil War, Spider-Man: No Way Home, Superman II, Superman: la pelicula, Thor: Ragnarok}", s.catalogo());
		
		s.closeConnection();
	}
	
	@Test
	void test_duracionPelicula() {
		try {
			this.executeScript("scripts/superheroes_drop_all_tables.sql");
		} catch (Exception e) { }
		
		SuperheroesDatabase s = new SuperheroesDatabase();
		assertEquals(-2, s.duracionPelicula("Avengers: Endgame"));
		
		try {
			this.executeScript("scripts/superheroes_create_tables.sql");
		} catch (Exception e) { }
		
		assertEquals(-1, s.duracionPelicula("Avengers: Endgame"));
		
		try {
			this.executeScript("scripts/superheroes_insert_data.sql");
		} catch (Exception e) { }
		
		assertEquals(0, s.duracionPelicula("Avengers: Endgame"));
		assertEquals(102, s.duracionPelicula("Superman: la pelicula"));
		
		s.closeConnection();
	}
	
	@Test
	void test_getEscenas() {
		try {
			this.executeScript("scripts/superheroes_drop_all_tables.sql");
		} catch (Exception e) { }
		
		SuperheroesDatabase s = new SuperheroesDatabase();
		assertEquals(null, s.getEscenas("Thanos"));
		
		try {
			this.executeScript("scripts/superheroes_create_tables.sql");
		} catch (Exception e) { }
		
		assertEquals("{}", s.getEscenas("Thanos"));
		
		try {
			this.executeScript("scripts/superheroes_insert_data.sql");
		} catch (Exception e) { }
		
		assertEquals("{}", s.getEscenas("Thanos"));
		assertEquals("{Superman II E1, Superman II E2, Superman II E3, Superman Escena 1, Superman Escena 2, Superman Escena b2, Superman Escena 3, Superman Escena 4}", s.getEscenas("Lex Luthor"));
		
		s.closeConnection();
	}

}
