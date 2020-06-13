
package cc.qp;
import java.util.ArrayList;

import es.upm.aedlib.map.HashTableMap;
import es.upm.aedlib.map.Map;
import es.upm.babel.cclib.Monitor;

public class QuePasaMonitor implements QuePasa, Practica {

	public Alumno[] getAutores() {
		return new Alumno[] {
				new Alumno("Loreto Garcia Tejada", "y16i010"),
				new Alumno("Maria Gutierrez Martin", "y16i017")
		};
	}

	// declaración de los mapas 
	private Map<String,Integer> creador;     		   // mapa que contiene como clave el nombre de grupo, y como valor el creador de este
	private Map<String,ArrayList<Integer>> miembros ;  // mapa que contiene como clave el nombre de grupo, y como valor un array list de los miembros de ese grupo 
	private Map<Integer, ArrayList<Mensaje>> mensajes; // mapa que contiene como clave el usuario, y como valor un array list de los mensajes de ese usuario
	private Map<Integer, Monitor.Cond> mapAux;         // mapa que contiene como clave un usuario, y como valor la condition de este usuarioo

	//declaracion listas, monitores y variables necesarios 
	private ArrayList<Integer> bloqueados ;    // array list complementario al mapAux, donde tenemos los usuarios que estan bloqueados
	private Monitor.Cond cond;				  
	private Monitor mutex;

	public QuePasaMonitor (){
		//unicializamos los mapas, monitores y listas creadas anteriormente
		mutex= new Monitor();
		creador=  new HashTableMap<String,Integer>();
		miembros= new HashTableMap<String,ArrayList<Integer>>();
		mensajes= new HashTableMap<Integer,ArrayList<Mensaje>>();

		mapAux= new HashTableMap<Integer, Monitor.Cond>();
		bloqueados = new ArrayList<Integer>();
		// cond no esta inicializado ya que lo hacemos cada vez que tengamos una nueva condicion 


	}


	//PRE: grupo ∉ dom(self.creador) 
	//CPRE: Cierto
	//POST self = (c ∪ {grupo  → uid}, m ∪ {grupo  → {uid}}, s )
	public void crearGrupo(int creadorUid, String grupo) throws PreconditionFailedException {
		mutex.enter();

		//PRE
		if ( creador.get(grupo) != null){  // comprobamos que no exista un grupo con ese nombre para el creador 
			mutex.leave();		
			throw new PreconditionFailedException();	// si ya existe salta la excepcion, antes liberando el mutex
		}

		//POST
		creador.put(grupo, creadorUid);   // añadimos al mapa de creador una clave grupo y de valor su creador 
		ArrayList<Integer> miem = new ArrayList<Integer>();     
		miem.add(creadorUid);
		miembros.put(grupo, miem);			//  añadimos al creador a los miembros de ese grupo

		if ( mensajes.get(creadorUid) == null){   // si la lista que contendrá los mensajes para el creador no esta creada la creamos, si ya esta no hacemos nada 
			mensajes.put(creadorUid, new ArrayList<Mensaje>());
		}

		desbloqueo();
		mutex.leave(); //Liberacion del mutex

	}


	//PRE: self.creador(grupo) = creadoruid ∧ uid ∉ self.miembros(grupo) 
	//CPRE: Cierto
	//POST: selfpre =(c,m,s) ∧ uid∈dom(s)⇒s′ =s ∧ uid∉dom(s)⇒s′ =s∪{uid →〈〉} ∧ self=(c,m⊕{grupo →m(grupo)∪{uid}},s′})
	public void anadirMiembro(int creadorUid, String grupo, int nuevoMiembroUid) throws PreconditionFailedException {
		mutex.enter();

		//PRE
		// comprobamos que el valor para la clave grupo en el mapa creador no sea null => el grupo esta creado 
		//comprobamos que el creador del grupo sea igual al creadorUid
		// comprobamos que el miembro que queremos añadir no sea ya miembro 
		if (creador.get(grupo)==null|| !creador.get(grupo).equals(creadorUid) || miembros.get(grupo).contains(nuevoMiembroUid) ){  
			mutex.leave();
			throw new PreconditionFailedException();  // si no se cumple cualquiera de las comprobaciones entonces salta excepcion
		}

		//POST
		miembros.get(grupo).add(nuevoMiembroUid); // añadimos el nuevo usuario al mapa de miembros, para la clave grupo ( al grupo donde queremos añadirlo)
		// añadimos el miembro al array list que contiene los miembros

		if ( mensajes.get(nuevoMiembroUid) == null){  // si la lista que contendrá los mensajes para el creador no esta creada la creamos, si ya esta no hacemos nada 
			mensajes.put(nuevoMiembroUid, new ArrayList<Mensaje>());
		}

		desbloqueo(); 
		//Liberacion del mutex
		mutex.leave();		
	}


	//PRE: uid ∈ self.miembros(grupo) ∧ uid  != self.creador(grupo) 
	//CPRE: Cierto
	//POST: selfpre =(c,m,s) ∧ borrados = {(u,g,x) ∈ s(uid) | g = grupo} ∧
	//self = (c, m ⊕ {grupo  → m(grupo) \ {uid}}, s ⊕ {uid  → s(uid) |  borrados})
	public void salirGrupo(int miembroUid, String grupo) throws PreconditionFailedException {
		mutex.enter();

		//PRE
		// comprobamos que el grupo exista
		// que el creador no sea la persona que queremos eliminar
		// y que esa persona que queremos eliminar sea miembro del grupo
		if ( creador.get(grupo)==null||creador.get(grupo).equals(miembroUid) || !miembros.get(grupo).contains(miembroUid) ){ //
			mutex.leave();
			throw new PreconditionFailedException(); // si no se cumple cualquiera de las comprobaciones entonces salta excepcion
		}

		//POST
		for ( int j=0; j< mensajes.get(miembroUid).size();j++){    // recorremos los mensajes del usuario que queremos eliminado
			if ( (mensajes.get(miembroUid).get(j).getGrupo()).equals(grupo)){   // borramos todos los mensajes del usuario que pertenecian al grupo donde ha sido eliminado
				mensajes.get(miembroUid).remove(j);
				j--;     // porque sino no comprobamos el que al eliminar el j pasa a ser el nuevo j
			}
		}	

		boolean esMiembro=false;
		for ( int i=0; i< miembros.get(grupo).size() && !esMiembro; i++){  // buscamos en el array list de miembros para el grupo hasta que encontremos el miembro que queremos eliminar
			if (miembros.get(grupo).get(i).equals(miembroUid)){  // cuando encontremos al miembro que queremos eliminar, lo eliminamos 
				esMiembro= true; 
				miembros.get(grupo).remove(i);  
			}
		}

		desbloqueo(); 
		mutex.leave(); //Liberacion del mutex
	}


	//PRE: uid ∈ self.miembros(grupo) 
	//CPRE: Cierto
	// POST: self pre = (c,m,s) ∧ self = (c,m,s′) ∧ ∀u∈UID •u∉m(grupo)⇒s′(u)=s(u)∧
	//u ∈ m(grupo) ⇒ s′(u) = s(u) + 〈(uid,grupo,contenido)〉)

	public void mandarMensaje(int remitenteUid, String grupo, Object contenidos) throws PreconditionFailedException {
		mutex.enter();

		//PRE
		// comprobamos que el grupo exista
		// y que el remitente sea miembro del grupo
		if (creador.get(grupo)==null||!miembros.get(grupo).contains(remitenteUid)){
			mutex.leave();
			throw new PreconditionFailedException();  // si no se cumple cualquiera de las comprobaciones entonces salta excepcion
		}

		//POST 
		ArrayList<Integer> miembrosAmandar = miembros.get(grupo);   // array list que contiene los miembros del grupo, por tanto a todas las personas que hay que enviar el mensaje 
		Mensaje mensaje = new Mensaje ( remitenteUid, grupo, contenidos);  // creamos el mensaje que queremos enviar, se crea para cada usuario un mensaje propio 
		for (int i =0; i< miembrosAmandar.size(); i++){	 // recorremos esa lista 
			if (mensajes.get(miembrosAmandar.get(i))== null){    // si el array que contiene los mensajes para el usuario i no esta creado, 
				ArrayList<Mensaje> mens= new ArrayList<Mensaje>();  // lo creamos 
				mens.add(mensaje);								// y añadimos el mensaje a ese array 
			}else{   // si este array ya esta creado 
				mensajes.get(miembrosAmandar.get(i)).add(mensaje);  // simplemente le añadimos el nuevo mensaje
			}
		}

		desbloqueo();  // llamamos al metodo desbloquear, ya que posibles metodos bloqueados ya se podran despertar al haber nuevos mensajes 

		mutex.leave(); //Liberacion del mutex

	}


	//CPRE: self.mensajes(uid)  != 〈〉 
	//POST: selfpre = (c,m,s) ∧ pendientes = s(uid) ∧ msg = pendientes(1) self = (c, m, s ⊕ {uid  → s(2..Longitud(pendientes))}
	public Mensaje leer(int uid) {
		mutex.enter();

		if ( mensajes.get(uid) == null){     // si la lista que contendrá los mensajes para el creador no esta creada la creamos, si ya esta no hacemos nada 
			mensajes.put(uid, new ArrayList<Mensaje>());    
		}

		//CPRE
		if ( mensajes.get(uid).size()==0){   // si la lista que contiene los menajes del usuario esta vacia ( no hay mensajes ) nos vamos a bloquear 
			if ( mapAux.get(uid)==null){    // si en nuestro mapa que contiene los usuarios con las conditions, para el usuario no hay condition ya creada
				this.cond= mutex.newCond(); // la creamos 
				mapAux.put(uid, cond);		// y añadimos al usuario y la condition al mapa 
			}
			bloqueados.add(uid);    // añadimos ese usuario a nuestra lista que contiene los que estan bloqueados 
			mapAux.get(uid).await();			// nos quedamos bloqueados
		}

		//POST
		Mensaje leido= mensajes.get(uid).get(0);    // guardamos  el primer mensaje de la lista de mensajes del usuario  
		mensajes.get(uid).remove(0);				// lo borramos 
		desbloqueo();			
		mutex.leave();			//Liberacion del mutex
		return leido;			// devolvemos ese mensaje guardado ( el primero de la lista de mensajes de usuario) 
	}


	private void desbloqueo(){
		boolean signaled= false;       // variable para que una vez ya hayamos despertado a un metodo no despertemos a mas 
		for (int i=0; i<bloqueados.size()  && !signaled; i++){   // recorremos la lista que contiene los usuarios que se han quedado bloqueados 
			int aux= bloqueados.get(i);
			if ( mapAux.get(aux).waiting()>0 && mensajes.get(aux).size()>0 ){      // si hay mensajes para el usuario i bloqueado 

				signaled= true;      //ponemos signaled a true ya que vamos a hacer un signal, y no podemos hacer mas de uno
				bloqueados.remove(i);			// y lo eliminamos de la lista de bloqueados
				mapAux.get(aux).signal();   // hacemos un signal a la condition de ese usuario que tiene mensjes

			} 
		}
	}




}