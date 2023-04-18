package superheroes;



import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;



public class SuperheroesDatabase {
	
	//Atributos
	
	private Connection conn = null;
	private PreparedStatement pst = null;
	private PreparedStatement pst2 = null;
	private Statement st = null;
	private ResultSet rs = null;
	private ResultSet rs2 = null;
	private BufferedReader csvReader = null;
	private FileOutputStream output = null;
	private InputStream input = null;

	//Constructor que inicializa el driver
	public SuperheroesDatabase() {
		init();
	}
	
	/**
	 * 
	 * @return <true> si abre correctamente la conexión y <false> si ninguna conexión es abierta
	 */
	
	public boolean openConnection() {
		String serverAddress = "localhost:3306";
		String db = "superheroes";
		String user = "superheroes_user";
		String pass = "superheroes_pass";
		String url = "jdbc:mysql://" + serverAddress + "/" + db;
		if (isConnected()) {     
			System.out.println("Ya estabas conectado a la base de datos!");
			return false;
		}else {
			try {
				conn = DriverManager.getConnection(url, user, pass);
			} catch (SQLException esql) {
				System.err.println("Mensaje: " + esql.getMessage());
				System.err.println("Código: " + esql.getErrorCode());
				System.err.println("Estado SQL: " + esql.getSQLState());
			}
			if(conn!=null) {
				System.out.println("Conectado a la base de datos!");
				return true;
			}else {
				System.err.println("Error al conectarse a la base de datos.");
				return false;}
		}


	}
	/**
	 * 
	 * @return <true> si se cierra la conexión y <false> si no lo hace
	 */
	public boolean closeConnection() {
		if(conn==null) {
			return true;
		}
		try {
			conn.close();
		} catch (SQLException esql) {
			System.err.println("Mensaje: " + esql.getMessage());
			System.err.println("Código: " + esql.getErrorCode());
			System.err.println("Estado SQL: " + esql.getSQLState());
			return false;
		}
		return true;
	}
	/** 
	 * @return <true> si se crea la tabla "Escena" y <false> si no lo hace
	 */
	public boolean createTableEscena(){
		boolean res = false;
		if(openConnection()|| isConnected()) {
			try {
				String query="CREATE TABLE escena (id_pelicula INTEGER, n_orden INTEGER, titulo VARCHAR(100),duracion INTEGER, PRIMARY KEY (n_orden, id_pelicula),FOREIGN KEY (id_pelicula) REFERENCES pelicula (id_pelicula) ON DELETE RESTRICT ON UPDATE CASCADE);";
				DatabaseMetaData dbMet= conn.getMetaData();
				rs = dbMet.getTables(null, "superheroes", "escena", null);
				if (!rs.next()) {
					st = conn.createStatement();
					st.executeUpdate(query);
					System.out.println("La tabla ha sido creada.");
					res= true;
				}
				else
					System.err.println("Ya exisitía una tabla creada.");
			}
			catch (SQLException esql) {
				System.err.println("Mensaje: " + esql.getMessage());
				System.err.println("Código: " + esql.getErrorCode());
				System.err.println("Estado SQL: " + esql.getSQLState());
				System.err.println("Error al crear la tabla.");
			}finally {
				limpieza();
			}
		}
		return res;
	}
	/**
	 * @return <true> si se crea la tabla "Rival" y <false> si no lo hace
	 */
	public boolean createTableRival(){
		boolean res= false;
		if(openConnection()||isConnected()) {
			try {
				String query="CREATE TABLE rival (id_sup INTEGER, id_villano INTEGER, fecha_primer_encuentro DATE, PRIMARY KEY (id_sup, id_villano),FOREIGN KEY (id_sup) REFERENCES superheroe (id_sup) ON DELETE RESTRICT ON UPDATE CASCADE, FOREIGN KEY (id_villano) REFERENCES villano (id_villano)ON DELETE RESTRICT ON UPDATE CASCADE);";
				DatabaseMetaData dbMet= conn.getMetaData();
				rs = dbMet.getTables(null, "superheroes", "rival", null);
				if (!rs.next()) {
					st = conn.createStatement();
					st.executeUpdate(query);
					System.out.println("La tabla ha sido creada.");
					res= true;
				}
				else
					System.err.println("Ya exisitía una tabla creada.");

			} catch (SQLException esql) {
				System.err.println("Mensaje: " + esql.getMessage());
				System.err.println("Código: " + esql.getErrorCode());
				System.err.println("Estado SQL: " + esql.getSQLState());
				System.err.println("Error al crear la tabla.");
			}finally {
				limpieza();
			}

		}
		return res;
	}
	/**
	 * 
	 * @param fileName (fichero con los datos que se van a introducir)
	 * @return <int> de elementos introducidos en la base de datos
	 */
	public int loadEscenas(String fileName) {
		int res = 0;
		if(openConnection()||isConnected()) {		
			try {
				csvReader = new BufferedReader(new FileReader(fileName));
				String row= csvReader.readLine();
				String query ="INSERT INTO escena values( ?,?,?,?);";
				while ( row  != null) {
					String[] data = row.split(";");
					String[] datos = new String[4];
					for(int i =0; i<data.length; i++) {
						datos[i]=data[i];
					}
					try {
						pst = conn.prepareStatement(query);
						for(int i =0; i<datos.length; i++) {
							pst.setString(i+1, datos[i]);
						}
						pst.executeUpdate();
						res++;
					}catch (SQLException esql) {
						System.err.println("Mensaje: " + esql.getMessage());
						System.err.println("Código: " + esql.getErrorCode());
						System.err.println("Estado SQL: " + esql.getSQLState());
					}

					row= csvReader.readLine();

				}
			} catch (FileNotFoundException e) {
				System.err.println("Problema al encontrar el archivo.");
			}catch(IOException a){
				System.err.println("Error.");

			} finally {
				limpieza();
			}

		}
		return res;


	}
	/**
	 * @param fileName (fichero con los datos que se van a introducir)
	 * @return <int> de lementos ue se van a introducir en la base de datos
	 * Se hara un rollback si no se ha podido insertar algun elemento en la tabla "Protagoniza"
	 */
	public int loadProtagoniza(String fileName) {
		int res = 0;
		boolean stop = false;
		if(openConnection()||isConnected()) {		
			try {
				csvReader = new BufferedReader(new FileReader(fileName));
				String row= csvReader.readLine();
				String query ="INSERT INTO protagoniza value(?,?,?);";
				String query2 ="INSERT INTO rival value(?,?,?);";
				while (row  != null && !stop) {
					try {
						conn.setAutoCommit(false);
					} catch (SQLException esql) {
						System.err.println("Mensaje: " + esql.getMessage());
						System.err.println("Código: " + esql.getErrorCode());
						System.err.println("Estado SQL: " + esql.getSQLState());
					}
					String[] data = row.split(";");
					String[] datos = new String[3];
					for(int i =0; i<data.length; i++) {
						datos[i]=data[i];
					}
					try {
						pst = conn.prepareStatement(query);
						for(int i =0; i<datos.length; i++) {
							pst.setString(i+1, datos[i]);
						}
						pst.executeUpdate();
						res++;
					}catch (SQLException esql) {

						System.err.println("Mensaje: " + esql.getMessage());
						System.err.println("Código: " + esql.getErrorCode());
						System.err.println("Estado SQL: " + esql.getSQLState());
						try {
							conn.rollback();
						} catch (SQLException e) {
							System.err.println("Error al hacer el Rollback");
						}
						res=0;
						stop= true;
					}
					if(res!=0) {
						try {
							pst2 = conn.prepareStatement(query2);
							pst2.setString(1, datos[0]);
							pst2.setString(2, datos[1]);
							pst2.setDate(3,new Date(new java.util.Date().getTime()));
							pst2.executeUpdate();
							res++;
						}catch (SQLException esql) {
							System.err.println("Mensaje: " + esql.getMessage());
							System.err.println("Código: " + esql.getErrorCode());
							System.err.println("Estado SQL: " + esql.getSQLState());

						}
					}

					row= csvReader.readLine();
				}
			} catch (FileNotFoundException e) {
				System.err.println("Problema al encontrar el archivo.");

			}catch(IOException a){
				System.err.println("Error.");

			} finally {
				limpieza();
			}
		}
		return res;
	}
	/**
	 * @return <String> de todas las peliculas de la base de datos
	 */
	public String catalogo() {
		if(openConnection()||isConnected()) {
			String res = "{";
			String query="SELECT pelicula.titulo FROM pelicula ORDER BY titulo ASC;" ;
			try {
				st= conn.createStatement();
				rs =st.executeQuery(query);
				if(rs.next()) {
					res= res+rs.getString("titulo");
				}
				while(rs.next()) {
					res= res+", "+rs.getString("titulo");
				}
			} catch (SQLException esql) {
				System.err.println("Mensaje: " + esql.getMessage());
				System.err.println("Código: " + esql.getErrorCode());
				System.err.println("Estado SQL: " + esql.getSQLState());
				return null;

			}finally {
				limpieza();
			}

			return res + "}";
		}
		return null;
	}
	/**
	 * 
	 * @param nombrePelicula (pelicula de la que queremos saber su duracion)
	 * @return <int> duración de la pelicula
	 * <-1> en caso de que no exista la pelicula
	 * <-2> si ocurre un error
	 */
	public int duracionPelicula(String nombrePelicula) {
		int duracion= -2;
		if(openConnection()||conn!=null) {
			String query ="SELECT SUM(escena.duracion) as totalDuracion, pelicula.titulo FROM superheroes.pelicula, superheroes.escena WHERE pelicula.id_pelicula = escena.id_pelicula AND pelicula.titulo LIKE ?" ;

			String query2="SELECT pelicula.titulo FROM pelicula where pelicula.titulo = ?;" ;

			try {
				pst = conn.prepareStatement(query);
				pst2 = conn.prepareStatement(query2);
				pst.setString(1, nombrePelicula);
				pst2.setString(1, nombrePelicula);
				rs = pst.executeQuery();
				rs2 = pst2.executeQuery();
				if(rs2.next() && rs.next()) {
					duracion= rs.getInt("totalDuracion");
				}
				else 
					duracion = -1;
			} catch (SQLException esql) {
				System.err.println("Mensaje: " + esql.getMessage());
				System.err.println("Código: " + esql.getErrorCode());
				System.err.println("Estado SQL: " + esql.getSQLState());
			}finally {
				limpieza();
			}
			return duracion;
		}
		return duracion;
	}
	/**
	 * @param nombreVillano (villano del que se quiere saber las escenas
	 * @return <String> de las escenas donde sale el villano
	 */
	public String getEscenas(String nombreVillano) {
		if(openConnection()||isConnected()) {
			String res = "{";
			String query= "SELECT escena.titulo FROM villano, escena, protagoniza, pelicula WHERE protagoniza.id_pelicula = escena.id_pelicula AND villano.id_villano = protagoniza.id_villano AND pelicula.id_pelicula = protagoniza.id_pelicula AND villano.nombre LIKE ? GROUP BY escena.id_pelicula , escena.n_orden ORDER BY pelicula.titulo , escena.n_orden ASC;";
			try {
				pst = conn.prepareStatement(query);
				pst.setString(1, nombreVillano);
				rs = pst.executeQuery();
				rs = pst.executeQuery();
				if(rs.next()) {
					res= res+rs.getString("titulo");
				}
				while(rs.next()) {
					res= res+", "+rs.getString("titulo");
				}

			} catch (SQLException esql) {
				System.err.println("Mensaje: " + esql.getMessage());
				System.err.println("Código: " + esql.getErrorCode());
				System.err.println("Estado SQL: " + esql.getSQLState());
				return null;
			}finally {
				limpieza();
			}
			return res+ "}";
		}

		return null;
	}
	/**
	 * @param nombre (de la persona)
	 * @param apellido (de la persona)
	 * @param filename (archivo donde se guardara la imagen)
	 * @return <true> si se obtiene la imagen correctamente y <false> en caso contrario>
	 */
	public boolean desenmascara(String nombre, String apellido, String filename) {
		Boolean res = false;
		if(openConnection()||isConnected()) {
			String query = "select superheroe.avatar from persona_real, superheroe where persona_real.id_persona= superheroe.id_persona and persona_real.nombre like ? and persona_real.apellido like ?;";

			try {
				pst = conn.prepareStatement(query);
				pst.setString(1, nombre);
				pst.setString(2, apellido);
				rs = pst.executeQuery();
				if(rs.next()) {
					input = rs.getBinaryStream("avatar");
					if(input!=null) {
						File file = new File(filename);
						output = new FileOutputStream(file);
						System.out.println("Leyendo archivo desde la base de datos...");
						byte[] buffer = new byte[input.available()];
						while (input.read(buffer) > 0) {
							output.write(buffer);
							res= true;
						}
					}
				}
			} catch (SQLException esql) {
				System.err.println("Mensaje: " + esql.getMessage());
				System.err.println("Código: " + esql.getErrorCode());
				System.err.println("Estado SQL: " + esql.getSQLState());
			}catch(IOException ex){
				System.err.println("ERROR");
			}finally {
				limpieza();
			}
		}
		return res;
	}


	//Metodos auxiliares
	
	//Inicializa el driver
	private void init() {   
		try {
			String drv = "com.mysql.jdbc.Driver";
			Class.forName(drv);
		} catch (ClassNotFoundException e) {
			System.err.println("Error al cargar el driver.");
		}
	}
	// Comprueba que esta establecida la conexion
	/**
	 * @return <true> si esta conectado y <false> si no lo esta
	 */
	private boolean isConnected() { 

		try {
			if(conn!=null && !conn.isClosed())
				return true;
		} catch (SQLException esql) {
			System.err.println("Mensaje: " + esql.getMessage());
			System.err.println("Código: " + esql.getErrorCode());
			System.err.println("Estado SQL: " + esql.getSQLState());
		}
		return false;
	}
	// Se encarga de cerrar todos los recursos usados excepto la conexion
	private void limpieza() {
		try {
			if(st != null) st.close(); 
			if(rs != null) rs.close(); 
			if(rs2!=null) rs2.close();
			if(pst != null) pst.close(); 
			if( pst2 != null) pst2.close(); 
			if (conn != null && conn.getAutoCommit() == false) conn.setAutoCommit(true); 
			if(output!=null) output.close(); 
			if(input!=null) input.close();
			if(csvReader!=null) csvReader.close();

		}catch (SQLException esql) {
			System.err.println("Mensaje: " + esql.getMessage());
			System.err.println("Código: " + esql.getErrorCode());
			System.err.println("Estado SQL: " + esql.getSQLState());
		}catch (IOException ex) {
			System.err.println("Error al cerrar el FileOutputStream or BufferedReader  ");
		}
	}

}