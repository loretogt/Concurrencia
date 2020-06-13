//Grupo: Loreto García Tejada (y16i010), María Gutierrez Martín (y16i017)


// QuePasaCSP.java
// Lars-Ake Fredlund y Julio Mariño
// Esqueleto para la realización de la práctica por paso de mensajes
// Completad las líneas marcadas con "TO DO"
// Solución basada en peticiones aplazadas
// Los huecos son orientativos: se basan en nuestra propia
// implementación (incluyendo comentarios).
package cc.qp;

import java.util.ArrayList;

import org.jcsp.lang.Alternative;
import org.jcsp.lang.AltingChannelInput;
import org.jcsp.lang.Any2OneChannel;
import org.jcsp.lang.CSProcess;
import org.jcsp.lang.Channel;
import org.jcsp.lang.Guard;
import org.jcsp.lang.One2OneChannel;

import es.upm.aedlib.map.HashTableMap;
import es.upm.aedlib.map.Map;


// TO DO: importad aquí lo que hayáis usado para implementar
//        el estado del recurso


public class QuePasaCSP implements QuePasa, CSProcess,Practica {

	public Alumno[] getAutores() {
		return new Alumno[] {
				new Alumno("Loreto Garcia Tejada", "y16i010"),
				new Alumno("Maria Gutierrez Martin", "y16i017")
		};
	}

	Map<String,Integer> creador;     		   // mapa que contiene como clave el nombre de grupo, y como valor el creador de este
	Map<String,ArrayList<Integer>> miembros ;  // mapa que contiene como clave el nombre de grupo, y como valor un array list de los miembros de ese grupo 
	Map<Integer, ArrayList<Mensaje>> mensajes; // mapa que contiene como clave el usuario, y como valor un array list de los mensajes de ese usuario

	// Creamos un canal por cada operación sin CPRE
	private Any2OneChannel chCrearGrupo =    Channel.any2one();
	private Any2OneChannel chAnadirMiembro = Channel.any2one();
	private Any2OneChannel chSalirGrupo =    Channel.any2one();
	private Any2OneChannel chMandarMensaje = Channel.any2one();

	// Creamos un canal para solicitar leer
	// Usaremos peticiones aplazadas en el servidor para tratar
	// la CPRE de leer
	private Any2OneChannel chPetLeer = Channel.any2one();

	public QuePasaCSP() { }

	// clases auxiliares para realizar peticiones al servidor
	// os regalamos la implementación de CrearGrupo
	public class PetCrearGrupo {
		public int creadorUid;
		public String grupo;
		// para tratamiento de la PRE
		public One2OneChannel chResp;

		public PetCrearGrupo(int creadorUid, String grupo) {
			this.creadorUid = creadorUid;
			this.grupo = grupo;
			this.chResp = Channel.one2one();
		}
	}

	public class PetAnadirMiembro {
		// TO DO: atributos de la clase
		public int creadorUid;
		public String grupo;
		public int nuevoMiembro;
		// para tratamiento de la PRE
		public One2OneChannel chResp;

		public PetAnadirMiembro(int creadorUid, String grupo, int nuevoMiembro) {
			// TO DO
			this.creadorUid= creadorUid;
			this.grupo= grupo;
			this.nuevoMiembro= nuevoMiembro;
			this.chResp= Channel.one2one();

		}
	}

	public class PetSalirGrupo {
		// TO DO : atributos de la clase
		public int miembroUid;
		public String grupo;

		// para tratamiento de la PRE
		public One2OneChannel chResp;

		public PetSalirGrupo(int miembroUid, String grupo) {
			// TO DO
			this.miembroUid= miembroUid;
			this.grupo= grupo ;
			this.chResp= Channel.one2one();

		}
	}

	public class PetMandarMensaje {
		// TO DO: atributos de la clase 
		public int remitenteUid;
		public String grupo;
		public Object contenido;

		// para tratamiento de la PRE
		public One2OneChannel chResp;

		public PetMandarMensaje(int remitenteUid, String grupo, Object contenido) {
			// TO DO 
			this.remitenteUid = remitenteUid;
			this.grupo=grupo;
			this.contenido= contenido;
			this.chResp= Channel.one2one();
		}
	}

	public class PetLeer {
		// TO DO: atributos de la clase
		public int uid;

		// para tratamiento de la PRE
		public One2OneChannel chResp;


		public PetLeer(int uid) {
			// TO DO
			this.uid= uid;
			this.chResp= Channel.one2one();
		}
	}

	// Implementamos aquí los métodos de la interfaz QuePasa
	// os regalamos la implementación de crearGrupo
	public void crearGrupo(int creadorUid, String grupo) 
			throws PreconditionFailedException
	{
		// creamos la petición
		PetCrearGrupo pet = new PetCrearGrupo(creadorUid,grupo);
		// la enviamos
		chCrearGrupo.out().write(pet);
		// recibimos mensaje de status
		Boolean exito = (Boolean) pet.chResp.in().read();
		// si el estado de la petición es negativo, lanzamos excepción
		if (!exito)
			throw new PreconditionFailedException();
	}

	public void anadirMiembro(int uid, String grupo, int nuevoMiembroUid) 
			throws PreconditionFailedException
	{
		// TO DO
		PetAnadirMiembro pet = new PetAnadirMiembro ( uid, grupo, nuevoMiembroUid);
		chAnadirMiembro.out().write(pet);
		// recibimos mensaje de status
		Boolean exito = (Boolean) pet.chResp.in().read();
		// si el estado de la petición es negativo, lanzamos excepción
		if (!exito)
			throw new PreconditionFailedException();
	}

	public void salirGrupo(int uid, String grupo) 
			throws PreconditionFailedException
	{
		// TO DO
		PetSalirGrupo pet = new PetSalirGrupo ( uid, grupo);
		chSalirGrupo.out().write(pet);
		// recibimos mensaje de status
		Boolean exito = (Boolean) pet.chResp.in().read();
		// si el estado de la petición es negativo, lanzamos excepción
		if (!exito)
			throw new PreconditionFailedException();
	}

	public void mandarMensaje(int remitenteUid, String grupo, Object contenidos)
			throws PreconditionFailedException
	{
		PetMandarMensaje pet = new PetMandarMensaje ( remitenteUid, grupo, contenidos );
		chMandarMensaje.out().write(pet);
		// recibimos mensaje de status
		Boolean exito = (Boolean) pet.chResp.in().read();
		// si el estado de la petición es negativo, lanzamos excepción
		if (!exito)
			throw new PreconditionFailedException();
	}

	public Mensaje leer(int uid) {
		PetLeer pet = new PetLeer (uid);
		chPetLeer.out().write(pet);
		return (Mensaje) pet.chResp.in().read();
	}


	// El servidor va en el método run()
	public void run() {

		// Mete aquí tu implementación del estado del recurso
		// (tráela de la práctica 1)
		creador=  new HashTableMap<String,Integer>();
		miembros= new HashTableMap<String,ArrayList<Integer>>();
		mensajes= new HashTableMap<Integer,ArrayList<Mensaje>>();

		// Colección para aplazar peticiones de leer
		// (adapta la que usaste en monitores, pero
		//  sustituye las Cond por One2OneChannel)

		Map<Integer, One2OneChannel> mapAux = new HashTableMap< Integer, One2OneChannel>();
		ArrayList<Integer> bloqueados = new ArrayList<Integer>() ; 


		// Códigos de peticiones para facilitar la asociación
		// de canales a operaciones 
		final int CREAR_GRUPO    = 0;
		final int ANADIR_MIEMBRO = 1;
		final int SALIR_GRUPO    = 2;
		final int MANDAR_MENSAJE = 3;
		final int LEER           = 4;

		// recepción alternativa
		final Guard[] guards = new AltingChannelInput[5];
		guards[CREAR_GRUPO]    = chCrearGrupo.in();
		guards[ANADIR_MIEMBRO] = chAnadirMiembro.in();
		guards[SALIR_GRUPO]    = chSalirGrupo.in();
		guards[MANDAR_MENSAJE] = chMandarMensaje.in();
		guards[LEER]           = chPetLeer.in();

		final Alternative services = new Alternative(guards);
		int chosenService;

		while (true) {
			// toda recepcion es incondicional
			chosenService = services.fairSelect();
			switch (chosenService) {
			// regalamos la implementación del servicio de crearGrupo
			case CREAR_GRUPO: {
				// recepción del mensaje
				PetCrearGrupo pet = (PetCrearGrupo) chCrearGrupo.in().read();
				// comprobación de la PRE
				if (creador.get(pet.grupo) != null){
					pet.chResp.out().write(false);
				}
				// ejecución normal
				else {
					// operación
					// TO DO: copia aquí tu implementación
					//        de crearGrupo de la práctica 1
					creador.put(pet.grupo, pet.creadorUid);   // añadimos al mapa de creador una clave grupo y de valor su creador 
					ArrayList<Integer> miem = new ArrayList<Integer>();     
					miem.add(pet.creadorUid);
					miembros.put(pet.grupo, miem);			//  añadimos al creador a los miembros de ese grupo
					if ( mensajes.get(pet.creadorUid) == null){   // si la lista que contendrá los mensajes para el creador no esta creada la creamos, si ya esta no hacemos nada 
						mensajes.put(pet.creadorUid, new ArrayList<Mensaje>());
					}
					pet.chResp.out().write(true);
				}
				break;
			}

			case ANADIR_MIEMBRO: {
				// recepcion del mensaje
				PetAnadirMiembro pet = (PetAnadirMiembro) chAnadirMiembro.in().read();
				// comprobacion de la PRE
				if (creador.get(pet.grupo)==null|| !creador.get(pet.grupo).equals(pet.creadorUid) 
						|| miembros.get(pet.grupo).contains(pet.nuevoMiembro) ){
					pet.chResp.out().write(false);
				}
				// ejecucion normal
				else{
					miembros.get(pet.grupo).add(pet.nuevoMiembro); // añadimos el nuevo usuario al mapa de miembros, para la clave grupo ( al grupo donde queremos añadirlo)
					// añadimos el miembro al array list que contiene los miembros

					if ( mensajes.get(pet.nuevoMiembro) == null){  // si la lista que contendrá los mensajes para el creador no esta creada la creamos, si ya esta no hacemos nada 
						mensajes.put(pet.nuevoMiembro, new ArrayList<Mensaje>());
					}
					pet.chResp.out().write(true);
				}
				break;
			}
			case SALIR_GRUPO: {
				// recepcion de la peticion
				PetSalirGrupo pet = (PetSalirGrupo) chSalirGrupo.in().read();
				// comprobacion de la PRE
				if ( creador.get(pet.grupo)==null||creador.get(pet.grupo).equals(pet.miembroUid) 
						|| !miembros.get(pet.grupo).contains(pet.miembroUid) ){
					pet.chResp.out().write(false);

				}
				// ejecucion normal
				else{
					boolean esMiembro=false;
					for ( int i=0; i< miembros.get(pet.grupo).size() && !esMiembro; i++){  // buscamos en el array list de miembros para el grupo hasta que encontremos el miembro que queremos eliminar
						if (miembros.get(pet.grupo).get(i).equals(pet.miembroUid)){  // cuando encontremos al miembro que queremos eliminar, lo eliminamos 
							esMiembro= true; 
							miembros.get(pet.grupo).remove(i);  
						}
					}
					for ( int j=0; j< mensajes.get(pet.miembroUid).size();j++){    // recorremos los mensajes del usuario que hemos eliminado
						if ( mensajes.get(pet.miembroUid).get(j).getGrupo().equals(pet.grupo)){   // borramos todos los mensajes del usuario que pertenecian al grupo donde ha sido eliminado
							mensajes.get(pet.miembroUid).remove(j);
							j--;
						}
					}	
					pet.chResp.out().write(true);
				}
				break;
			}
			case MANDAR_MENSAJE: {
				// recepcion de la peticion
				PetMandarMensaje pet = (PetMandarMensaje) chMandarMensaje.in().read();
				// comprobacion de la PRE
				if (creador.get(pet.grupo)==null||!miembros.get(pet.grupo).contains(pet.remitenteUid)){
					pet.chResp.out().write(false);
				}
				// ejecucion normal
				else{
					ArrayList<Integer> miembrosAmandar = miembros.get(pet.grupo);   // array list que contiene los miembros del grupo, por tanto a todas las personas que hay que enviar el mensaje 
					for (int i =0; i< miembrosAmandar.size(); i++){	 // recorremos esa lista 
						Mensaje mensaje = new Mensaje ( pet.remitenteUid, pet.grupo, pet.contenido);  // creamos el mensaje que queremos enviar, se crea para cada usuario un mensaje propio
						if (mensajes.get(miembrosAmandar.get(i))== null){    // si el array que contiene los mensajes para el usuario i no esta creado, 
							ArrayList<Mensaje>mens= new ArrayList<Mensaje>();  // lo creamos 
							mens.add(mensaje);								// y añadimos el mensaje a ese array 

						}else{   // si este array ya esta creado 
							mensajes.get(miembrosAmandar.get(i)).add(mensaje);  // simplemente le añadimos el nuevo mensaje
						}
					}

					pet.chResp.out().write(true);
				}


				break;
			}
			case LEER: {
				// recepcion de la peticion
				PetLeer pet = (PetLeer) chPetLeer.in().read();
				// no hay PRE que comprobar!
				// TO DO: aquí lo más sencillo es guardar la petición según se recibe
				// TO DO  (reutilizad la estructura que usasteis en monitores cambiando Cond por One2OneChannel)
				if ( mensajes.get(pet.uid) == null){     // si la lista que contendrá los mensajes para el creador no esta creada la creamos, si ya esta no hacemos nada 
					mensajes.put(pet.uid, new ArrayList<Mensaje>());    
				} 

				mapAux.put(pet.uid, pet.chResp);


				bloqueados.add(pet.uid);  //lo añadimos tambien en la lista donde tenemos los usuarios que estan bloqueados
				break;
			}
			} // END SWITCH

			// código de desbloqueos
			// solo hay peticiones aplazadas de leer
			// TO DO: recorred la estructura  con las peticiones aplazadas y responded a todas aquellas cuya CPRE se cumpla

			boolean cambiado = true;
			while ( cambiado){ // mientras que algo cambie
				cambiado = false;
				int nPet= bloqueados.size();
				boolean borrada= false;
				for ( int i=0; i< nPet && !borrada; i++){ // recorremos todas las peticiones aplazadas
					int m = mensajes.get(bloqueados.get(i)).size();
					if(m>0){ // se cumple la cpre
						mapAux.get(bloqueados.get(i)).out().write(mensajes.get(bloqueados.get(i)).get(0)); // escribimos en el canal
						mensajes.get(bloqueados.get(i)).remove(0);	// borramos el primer mensaje de la persona que lee
						bloqueados.remove(i);	// lo eliminamos de la lista de usuarios bloqueados 
						borrada= true;	
						cambiado= true;  // al leer algo a cambiado
					}
				}
			}

		} // END while(true) SERVIDOR
	} // END run()
} // END class QuePasaCSP

