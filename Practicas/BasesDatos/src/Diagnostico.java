import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Diagnostico {

	private final String DATAFILE = "data/disease_data.data";
	private Connection conn;
	private boolean creado=false;
	private boolean conectado=false;

	private void showMenu() {

		int option = -1;
		do {
			System.out.println("Bienvenido a sistema de diagnÛstico\n");
			System.out.println("Selecciona una opciÛn:\n");
			System.out.println("\t1. CreaciÛn de base de datos y carga de datos.");
			System.out.println("\t2. Realizar diagnÛstico.");
			System.out.println("\t3. Listar sÌntomas de una enfermedad.");
			System.out.println("\t4. Listar enfermedades y sus cÛdigos asociados.");
			System.out.println("\t5. Listar sÌntomas existentes en la BD y su tipo sem·ntico.");
			System.out.println("\t6. Mostrar estadÌsticas de la base de datos.");
			System.out.println("\t7. Salir.");
			try {
				option = readInt();
				switch (option) {
				case 1:
					crearBD();
					break;
				case 2:
					realizarDiagnostico();
					break;
				case 3:
					listarSintomasEnfermedad();
					break;
				case 4:
					listarEnfermedadesYCodigosAsociados();
					break;
				case 5:
					listarSintomasYTiposSemanticos();
					break;
				case 6:
					mostrarEstadisticasBD();
					break;
				case 7:
					exit();
					break;
				}
			} catch (Exception e) {
				System.err.println("OpciÛn introducida no v·lida!");
			}
		} while (option != 7);
		exit();
	}

	private void exit() {
		System.out.println("Saliendo.. °hasta otra!");
		System.exit(0);
	}

	private void conectar() {
		try {
			if(!conectado){
				String drv = "com.mysql.jdbc.Driver";
				Class.forName(drv);

				String serverAddress = "localhost:3306";
				String db = "diagnostico";
				String user = "bddx";
				String pass = "bddx_pwd";
				String url = "jdbc:mysql://" + serverAddress + "/";
				conn = DriverManager.getConnection(url, user, pass);
				conn.setAutoCommit(true);
				System.out.println("Conectado a la base de datos!");
			}
			conectado=true;
		}catch(Exception e){
			System.err.println("Error al conectar BD:" + e.getMessage());
			e.printStackTrace();
		} 

	}

	private void crearBD() {
		conectar();
		try {
			if(!creado){
				conn.setAutoCommit(false);

				String baseDeDatos = "CREATE DATABASE diagnostico";
				String semantic_type= "CREATE TABLE `semantic_type` (`"
						+ "semantic_type_id` int(11) NOT NULL AUTO_INCREMENT,"
						+ " `tui` varchar(45) DEFAULT NULL,"
						+ "PRIMARY KEY (`semantic_type_id`))ENGINE=InnoDB DEFAULT CHARSET=latin1;"; //Creacion de la tabla semantic_type

				String symptom = "CREATE TABLE `symptom` ("
						+ "`cui` varchar(25) NOT NULL,"
						+ "`name` varchar(255) DEFAULT NULL,"
						+ "PRIMARY KEY (`cui`)"
						+ ")ENGINE=InnoDB DEFAULT CHARSET=latin1; "; //creacion de la tabla symptom

				String symptom_semantic_type="CREATE TABLE `symptom_semantic_type` ("
						+ "`cui` varchar(25) NOT NULL,"
						+ "`semantic_type_id` int(11) NOT NULL,"
						+ " PRIMARY KEY (`cui`,`semantic_type_id`),"
						+ "  KEY `fk_semantic_type_id_idx` (`semantic_type_id`),"
						+ " CONSTRAINT `fk_cui` FOREIGN KEY (`cui`) REFERENCES `symptom` (`cui`) ON DELETE NO ACTION ON UPDATE NO ACTION,"
						+ " CONSTRAINT `fk_semantic_type_id` FOREIGN KEY (`semantic_type_id`) REFERENCES `semantic_type` (`semantic_type_id`) ON DELETE NO ACTION ON UPDATE NO ACTION"
						+ ")ENGINE=InnoDB DEFAULT CHARSET=latin1; ";// creacion de la tabla symptom_semantic_type

				String source= "CREATE TABLE `source` ("
						+ " `source_id` int(11) NOT NULL AUTO_INCREMENT,"
						+ " `name` varchar(255) DEFAULT NULL,"
						+ "PRIMARY KEY (`source_id`)"
						+ ")ENGINE=InnoDB DEFAULT CHARSET=latin1;"; //creacion de la tabla source


				String code = "CREATE TABLE `code` ("
						+ "`code_id` varchar(255) NOT NULL,"
						+ " `source_id` int(11) NOT NULL,"
						+ "PRIMARY KEY (`code_id`),"
						+ " KEY `source_id_idx` (`source_id`),"
						+ "CONSTRAINT `fk_source_id` FOREIGN KEY (`source_id`) REFERENCES `source` (`source_id`) ON DELETE NO ACTION ON UPDATE NO ACTION"
						+ ")ENGINE=InnoDB DEFAULT CHARSET=latin1;"; //creacion de la tabla code


				String disease= "CREATE TABLE `disease` ("
						+ "`disease_id` int(11) NOT NULL AUTO_INCREMENT,"
						+ "`name` varchar(255) DEFAULT NULL,"
						+ "PRIMARY KEY (`disease_id`)"
						+ ")ENGINE=InnoDB DEFAULT CHARSET=latin1;"; //creacion de la tabla disease 


				String disease_code = "CREATE TABLE `disease_code` ("
						+ " `disease_id` int(11) NOT NULL,"
						+ "`code` varchar(255) NOT NULL,"
						+ " `source_id` int(11) NOT NULL,"
						+ "PRIMARY KEY (`disease_id`,`code`,`source_id`),"
						+ " KEY `fk_code_idx` (`code`),"
						+ " KEY `fk_source_id_idx` (`source_id`),"
						+ "CONSTRAINT `fko_code` FOREIGN KEY (`code`) REFERENCES `code` (`code_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,"
						+ " CONSTRAINT `fko_disease_id` FOREIGN KEY (`disease_id`) REFERENCES `disease` (`disease_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,"
						+ " CONSTRAINT `fko_source_id` FOREIGN KEY (`source_id`) REFERENCES `code` (`source_id`) ON DELETE NO ACTION ON UPDATE NO ACTION"
						+ ")ENGINE=InnoDB DEFAULT CHARSET=latin1;"; //creacion de la tabla disease_code

				String disease_symptom="CREATE TABLE `disease_symptom` ("
						+ "`disease_id` int(11) NOT NULL,"
						+ "`symptom_id` varchar(25) NOT NULL,"
						+ "PRIMARY KEY (`disease_id`,`symptom_id`),"
						+ " KEY `symptom_id_idx` (`symptom_id`),"
						+ "CONSTRAINT `fk_disease_id` FOREIGN KEY (`disease_id`) REFERENCES `disease` (`disease_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,"
						+ " CONSTRAINT `fk_symptom_id` FOREIGN KEY (`symptom_id`) REFERENCES `symptom` (`cui`) ON DELETE NO ACTION ON UPDATE NO ACTION"
						+ ")ENGINE=InnoDB DEFAULT CHARSET=latin1;"; //creacion de la tabla disease_symptom 


				PreparedStatement pst= conn.prepareStatement(baseDeDatos);
				pst.execute();
				conn= DriverManager.getConnection("jdbc:mysql://" +  "localhost:3306" + "/"+ "diagnostico" + "?allowMultiQueries=true", "bddx","bddx_pwd");
				conn.setAutoCommit(false);
				pst= conn.prepareStatement(semantic_type);
				pst.execute();
				pst= conn.prepareStatement(symptom);
				pst.execute();
				pst= conn.prepareStatement(symptom_semantic_type);
				pst.execute();
				pst= conn.prepareStatement(source);
				pst.execute();
				pst= conn.prepareStatement(code);
				pst.execute();
				pst= conn.prepareStatement(disease);
				pst.execute();
				pst= conn.prepareStatement(disease_code);
				pst.execute();
				pst= conn.prepareStatement(disease_symptom);
				pst.execute();

				//creacion base de datos
				ArrayList<ArrayList<ArrayList<String>>> datos = separador();
				String disease_data = "INSERT INTO disease(name) VALUES (?);";
				PreparedStatement pst1 = conn.prepareStatement(disease_data);
				for(int i=0; i<datos.size();i++){
					pst1.setString(1,datos.get(i).get(0).get(0));
					pst1.executeUpdate();
				}
				String source_data = "INSERT INTO source(name) VALUES (?);";
				pst1 = conn.prepareStatement(source_data);
				for(int i=0; i<datos.size();i++){
					for(int j=0; j<datos.get(i).get(2).size();j++){
						pst1.setString(1,datos.get(i).get(2).get(j));
						pst1.executeUpdate();
					}
				}
				String sem_type_data = "INSERT INTO semantic_type(tui) VALUES (?);";
				pst1 = conn.prepareStatement(sem_type_data);
				for(int i=0; i<datos.size();i++){
					for(int j=0; j<datos.get(i).get(5).size();j++){
						pst1.setString(1,datos.get(i).get(5).get(j));
						pst1.executeUpdate();
					}
				}
				String sim_data = "INSERT INTO symptom(cui,name) VALUES (?,?);";
				pst1 = conn.prepareStatement(sim_data);
				HashMap<String,String> sintomas = new HashMap<String,String>();
				for (int i=0; i<datos.size(); i++){
					for (int j=0; j<datos.get(i).get(4).size(); j++){
						sintomas.put(datos.get(i).get(4).get(j), datos.get(i).get(3).get(j));
					}
				}
				
			
				for(String k: sintomas.keySet()) { 
					pst1.setString(1,k);
					pst1.setString(2,sintomas.get(k));
					pst1.executeUpdate();
				}
				
				/*String dis_cod_data = "INSERT INTO disease_code (disease_id,_code,source_id) VALUES (?,?);";
				pst1=conn.prepareStatement(dis_cod_data);*/
				String code_data = "INSERT INTO code(code_id,fk_source_id)  VALUES (?, SELECT * source_id FROM source);";
				//pst1 = conn.prepareStatement(code_data);
				Statement st = conn.createStatement();
				ResultSet rs = st.executeQuery(code_data);
				ArrayList<String> source_id = new ArrayList<String>();
				for(int i=0; i<datos.size();i++){
					for(int j=0; j<datos.get(i).get(1).size();j++){
						pst1.setString(1,datos.get(i).get(1).get(j));
						pst1.executeUpdate();
					}
				}
				

				pst.close();
				pst1.close();
				conn.commit();
				conn.setAutoCommit(true);
				creado=true;
			}
			else{
				System.out.println("Base ya creada");
			}
		} catch (SQLException e) {
			System.err.println("Error SQL:" + e.getMessage());
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	private void realizarDiagnostico() {

		conectar();
	}

	private void listarSintomasEnfermedad() {
		conectar();
		Statement st;
		int opcion;
		try {
			st = conn.createStatement();
			ResultSet rs = st.executeQuery("SELECT * FROM disease");
			System.out.println("Introduce el ID de la enfermedad cuyos sÌntomas quiera consultar");
			opcion=readInt();
			Statement st2 = conn.createStatement();
			ResultSet rs2 = st.executeQuery("SELECT symptom.name FROM disease_symptom, symptom WHERE disease_symptom.symptom_id = symptom.cui AND disease_id = " + opcion);
			st.close();
			st2.close();
			rs.close();
			rs2.close();
		} catch (SQLException e) {
			System.err.println("Error SQL:" + e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			System.err.println("Error en la lectura por teclado");
			e.printStackTrace();
		}

	}

	private void listarEnfermedadesYCodigosAsociados() {
		conectar();
		Statement st;
		try {
			st = conn.createStatement();
			ResultSet rs = st.executeQuery("SELECT * FROM disease_code");
			st.close();
			rs.close();

		} catch (SQLException e) {
			System.err.println("Error SQL:" + e.getMessage());
			e.printStackTrace();
		}
	}

	private void listarSintomasYTiposSemanticos() throws Exception {
		ArrayList<ArrayList<ArrayList<String>>> datos = separador();
		HashMap<String,Integer> map= new HashMap<String,Integer>();
		int n=0;
		for(int i=0; i<datos.size();i++){
			for( int j=0; j< datos.get(i).get(2).size(); j++){
				map.put(datos.get(i).get(2).get(j), j);
			}
		}
		for(String k: map.keySet()){
			System.out.println(map.get(k)+ "  " + k);
		}
	
	}

	private void mostrarEstadisticasBD() {
		conectar();
		/*	System.out.println("Hay un total de "+ + "enfermedades");
		System.out.println("Hay un total de "+ + "sÌntomas");
		System.out.println("Hay un total de "+ + "enfermedades");
		 */	
	}

	/**
	 * MÈtodo para leer n˙meros enteros de teclado.
	 * 
	 * @return Devuelve el n˙mero leÌdo.
	 * @throws Exception
	 *             Puede lanzar excepciÛn.
	 */
	private int readInt() throws Exception {
		try {
			System.out.print("> ");
			return Integer.parseInt(new BufferedReader(new InputStreamReader(System.in)).readLine());
		} catch (Exception e) {
			throw new Exception("Not number");
		}
	}

	/**
	 * MÈtodo para leer cadenas de teclado.
	 * 
	 * @return Devuelve la cadena leÌda.
	 * @throws Exception
	 *             Puede lanzar excepciÛn.
	 */
	private String readString() throws Exception {
		try {
			System.out.print("> ");
			return new BufferedReader(new InputStreamReader(System.in)).readLine();
		} catch (Exception e) {
			throw new Exception("Error reading line");
		}
	}

	/**
	 * MÈtodo para leer el fichero que contiene los datos.
	 * 
	 * @return Devuelve una lista de String con el contenido.
	 * @throws Exception
	 *             Puede lanzar excepciÛn.
	 */
	private LinkedList<String> readData() throws Exception {
		LinkedList<String> data = new LinkedList<String>();
		BufferedReader bL = new BufferedReader(new FileReader(DATAFILE));
		while (bL.ready()) {
			data.add(bL.readLine());
		}
		bL.close();
		return data;
	}
	/*private String[] separadorEnferm () throws Exception{
		LinkedList <String> datos = readData();
		Iterator<String> it=datos.iterator();	
		String[] enfermedades=  new String[datos.size()];
		while(it.hasNext()){
			for(int i=0;i<enfermedades.length;i++){
				enfermedades[i] = it.next();
			}
		}
		return enfermedades; 
	}*/
	private ArrayList<ArrayList<ArrayList<String>>> separador() throws Exception  {

		LinkedList<String> datos = readData();
		java.util.Iterator<String> it = datos.iterator();
		ArrayList<String> arr= new ArrayList<String>();
		while( it.hasNext()){
			arr.add(it.next());
		}

		ArrayList<ArrayList<ArrayList<String>>> total = new ArrayList<ArrayList<ArrayList<String>>>();



		for (int i=0; i<arr.size(); i++){  // separacion de cada enfermedad

			ArrayList<ArrayList<String>> enfermedad = new ArrayList<ArrayList<String>>();

			ArrayList<String> nombre =  new ArrayList<String>();
			ArrayList<String> nombre_codigo = new ArrayList<String>();
			ArrayList<String> source = new ArrayList<String>();
			ArrayList<String> nombre_sintom= new ArrayList<String>();
			ArrayList<String> cod_sintom = new ArrayList<String>();
			ArrayList<String> semantic_type= new ArrayList<String>();


			String[] div1 = arr.get(i).split("=");   // primera dividion por =

			//LADO IZQUIERDO

			String[] div2 = div1[0].split(":");  
			nombre.add(div2[0]);  // nombre de la enfermedad  
			String[] div3 = div2[1].split(";");  // division de cada uno de los codigos con sus dos valores 
			for ( int i2=0; i2 < div3.length-1; i2++){  // para<		<< cada codigo
				String[] div4 = div3[i2].split("@");  // division del nombre del codigo y su lenguaje 
				nombre_codigo.add(div4[0]);  //a√±adir linkedList que contiene los nombres de los codigos
				source.add(div4[1]);   // a√±adir linkedlist que contiene los sources de los codigos 
				//}


			}

			//LADO DERECHO

			String[] div5= div1[1].split(";"); // division de los distintos sintomas 
			for ( int i3=0; i3 < div5.length-1; i3++){ // 
				String[]div6 = div5[i3].split(":") ;// separacion de nombre sintoma, codigo sintoma y semantic type
				nombre_sintom.add(div6[0]);
				cod_sintom.add(div6[1]);
				semantic_type.add(div6[2]);
			}

			enfermedad.add(nombre);
			enfermedad.add(nombre_codigo);
			enfermedad.add(source);
			enfermedad.add(nombre_sintom);
			enfermedad.add(cod_sintom);
			enfermedad.add(semantic_type);

			total.add(enfermedad);
		}

		return total;

	}

	public static void main(String args[]) {
		new Diagnostico().showMenu();
	}
}
