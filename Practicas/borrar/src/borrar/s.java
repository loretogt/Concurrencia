// QuePasaCSP.java
// Lars-Ake Fredlund y Julio MariÃ±o
// Esqueleto para la realizaciÃ³n de la prÃ¡ctica por paso de mensajes
// Completad las lÃ­neas marcadas con "TO DO"
// SoluciÃ³n basada en peticiones aplazadas
// Los huecos son orientativos: se basan en nuestra propia
// implementaciÃ³n (incluyendo comentarios).
package cc.qp;

import org.jcsp.lang.Alternative;
import org.jcsp.lang.AltingChannelInput;
import org.jcsp.lang.Any2OneChannel;
import org.jcsp.lang.CSProcess;
import org.jcsp.lang.Channel;
import org.jcsp.lang.Guard;
import org.jcsp.lang.One2OneChannel;

import es.upm.babel.cclib.Monitor.Cond;

// TO DO: importad aquÃ­ lo que hayÃ¡is usado para implementar
//        el estado del recurso
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
// TO DO


public class QuePasaCSP implements QuePasa, CSProcess {

    // Creamos un canal por cada operaciÃ³n sin CPRE
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
    // os regalamos la implementaciÃ³n de CrearGrupo
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
    	public String grupo;
    	public int creadorUid;
    	public int nuevoMiembroUid;
    	public One2OneChannel chResp;
	public PetAnadirMiembro(int creadorUid, String grupo, int nuevoMiembroUid) {
	    this.grupo = grupo;
	    this.creadorUid = creadorUid;
	    this.nuevoMiembroUid = nuevoMiembroUid;
	    this.chResp = Channel.one2one();
	}
    }

    public class PetSalirGrupo {
    	public String grupo;
    	public int miembroUid;
    	public One2OneChannel chResp;
	public PetSalirGrupo(int miembroUid, String grupo) {
		 this.grupo = grupo;
		 this.miembroUid = miembroUid;
		this.chResp = Channel.one2one();
	}
    }

    public class PetMandarMensaje {
    	public String grupo;
    	public int remitenteUid;
    	public Object contenidos;
    	public One2OneChannel chResp;
	public PetMandarMensaje(int remitenteUid, String grupo, Object contenidos) {
		this.grupo = grupo;
		this.contenidos = contenidos;
		this.remitenteUid = remitenteUid;
		this.chResp = Channel.one2one();
	}
    }

    public class PetLeer {
    	public int uid;
    	public One2OneChannel chResp;
	
	public PetLeer(int uid) {
	    this.uid = uid;
	    this.chResp = Channel.one2one();
	}
    }
    
    // Implementamos aquÃ­ los mÃ©todos de la interfaz QuePasa
    // os regalamos la implementaciÃ³n de crearGrupo
    public void crearGrupo(int creadorUid, String grupo) 
	throws PreconditionFailedException
    {
	// creamos la peticiÃ³n
	PetCrearGrupo pet = new PetCrearGrupo(creadorUid,grupo);
	// la enviamos
	chCrearGrupo.out().write(pet);
	// recibimos mensaje de status
	Boolean exito = (Boolean) pet.chResp.in().read();
	// si el estado de la peticiÃ³n es negativo, lanzamos excepciÃ³n
	if (!exito)
	    throw new PreconditionFailedException();
    }

    public void anadirMiembro(int uid, String grupo, int nuevoMiembroUid) 
	throws PreconditionFailedException
    {
    	// creamos la peticiÃ³n
    	PetAnadirMiembro pet = new PetAnadirMiembro(uid,grupo,nuevoMiembroUid);
    	// la enviamos
    	chAnadirMiembro.out().write(pet);
    	// recibimos mensaje de status
    	Boolean exito = (Boolean) pet.chResp.in().read();
    	// si el estado de la peticiÃ³n es negativo, lanzamos excepciÃ³n
    	if (!exito)
    	    throw new PreconditionFailedException();
    }
  
    public void salirGrupo(int uid, String grupo) 
	throws PreconditionFailedException
    {
    	// creamos la peticiÃ³n
    	PetSalirGrupo pet = new PetSalirGrupo(uid,grupo);
    	// la enviamos
    	chSalirGrupo.out().write(pet);
    	// recibimos mensaje de status
    	Boolean exito = (Boolean) pet.chResp.in().read();
    	// si el estado de la peticiÃ³n es negativo, lanzamos excepciÃ³n
    	if (!exito)
    	    throw new PreconditionFailedException();
    }

    public void mandarMensaje(int remitenteUid, String grupo, Object contenidos)
	throws PreconditionFailedException
    {
    	// creamos la peticiÃ³n
    	PetMandarMensaje pet = new PetMandarMensaje(remitenteUid,grupo,contenidos);
    	// la enviamos
    	chMandarMensaje.out().write(pet);
    	// recibimos mensaje de status
    	Boolean exito = (Boolean) pet.chResp.in().read();
    	// si el estado de la peticiÃ³n es negativo, lanzamos excepciÃ³n
    	if (!exito)
    	    throw new PreconditionFailedException();
    }
  
    public Mensaje leer(int uid)
    {
    	// creamos la peticiÃ³n
    	PetLeer pet = new PetLeer(uid);
    	// la enviamos
    	chMandarMensaje.out().write(pet);
    	// recibimos mensaje de status
    	Mensaje msg = (Mensaje) pet.chResp.in().read();
    	// si el estado de la peticiÃ³n es negativo, lanzamos excepciÃ³n
    	return msg;
    }
    private LinkedList<Mensaje> eliminarMensajes(PetSalirGrupo pet, Map<Integer,LinkedList<Mensaje>> mensajes){
		LinkedList<Mensaje> msg = mensajes.get(pet.miembroUid); //Copiamos los mensajes del usuario
		for(int i=0;i<msg.size();i++){
			Mensaje ms = msg.get(i); //Sacamos cada mensaje
			if(ms.getGrupo().equals(pet.grupo)){ //Si el nombre del grupo coincide con el grupo del que sale el usuario
				msg.remove(i); //Eliminamos ese mensaje
				i--; //Decrementamos en 1 porque hemos eliminado y la lista se desplaza uno hacia la izq
			}
		}
		return msg;
	}
    private LinkedList<Mensaje> aniadir(PetAnadirMiembro pet, Map<Integer, LinkedList<Mensaje>> mensajes){
    		if(!mensajes.containsKey(pet.nuevoMiembroUid)){ //Si no esta en mensajes
    			LinkedList<Mensaje> msg = new LinkedList<Mensaje>(); //Creamos la lista que va a contener los mensajes
    			mensajes.put(pet.nuevoMiembroUid,msg); //Los metemos
    			return msg;
    		}
    		else {
    			return mensajes.get(pet.nuevoMiembroUid);
    		}
    	}
    	
    	

    // El servidor va en el mÃ©todo run()
    public void run() {
    	//Declaramos los mapas atributo necesarios
    	Map<String,Integer> creador = new HashMap<String,Integer>(); //Contiene el creador de cada grupo K->nombre del grupo V->Uid del creador
    	Map<String,LinkedList<Integer>> miembros = new HashMap<String,LinkedList<Integer>>(); //Contiene los miembros de un grupo K->nombre del grupo V->lista con los Uid
    	Map<Integer,LinkedList<Mensaje>> mensajes = new HashMap<Integer,LinkedList<Mensaje>>();//Contiene los mensajes para cada usuario K->Uid del usuario V->mensaje
    	//Inicializamos los parametros para evitar errores
	// Mete aquÃ­ tu implementaciÃ³n del estado del recurso
	// (trÃ¡ela de la prÃ¡ctica 1)
    	//Declaramos un mapa para los channels
    	Map<Integer,One2OneChannel> espera = new HashMap<Integer,One2OneChannel>(); //Contiene una condicion para cada usuario K->Uid usuario V->CHANNEL
	// ColecciÃ³n para aplazar peticiones de leer
    	
	// (adapta la que usaste en monitores, pero
	//  sustituye las Cond por One2OneChannel)
	// TO DO
	// TO DO

	// CÃ³digos de peticiones para facilitar la asociaciÃ³n
	// de canales a operaciones 
	final int CREAR_GRUPO    = 0;
	final int ANADIR_MIEMBRO = 1;
	final int SALIR_GRUPO    = 2;
	final int MANDAR_MENSAJE = 3;
	final int LEER           = 4;

	// recepciÃ³n alternativa
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
		// regalamos la implementaciÃ³n del servicio de crearGrupo
	    case CREAR_GRUPO: {
		// recepciÃ³n del mensaje
		PetCrearGrupo pet = (PetCrearGrupo) chCrearGrupo.in().read();
		// comprobaciÃ³n de la PRE
		if (creador.containsKey(pet.grupo))
		    // status KO
		    pet.chResp.out().write(false);
		// ejecuciÃ³n normal
		else {
		    creador.put(pet.grupo, pet.creadorUid); //Metemos en el mapa al grupo y su creador
			LinkedList<Integer> miembro = new LinkedList<Integer>(); //Contiene los miembros de un grupo
			LinkedList<Mensaje> mensaje = new LinkedList<Mensaje>(); //Contiene los mensajes de un usuario
			miembro.add(pet.creadorUid);//aniadimos el creador a la lista de miembros
			miembros.put(pet.grupo, miembro);//lo aniadimos al map de miembros
			mensajes.put(pet.creadorUid, mensaje);//le aniadimos la lista de mensajes vacia del creador
		    // status OK
		    pet.chResp.out().write(true);
		}
		break;
	    }
	    case ANADIR_MIEMBRO: {
		// recepcion del mensaje
	    PetAnadirMiembro pet = (PetAnadirMiembro) chCrearGrupo.in().read();
		// comprobacion de la PRE
	    if(!creador.containsKey(pet.grupo) || pet.creadorUid!=creador.get(pet.grupo)
	    		|| !miembros.get(pet.grupo).contains(pet.nuevoMiembroUid)) {
	    pet.chResp.out().write(false);
	    }else {
	    	if(!mensajes.containsKey(pet.nuevoMiembroUid)){ //Si no esta en mensajes
    			LinkedList<Mensaje> msg = new LinkedList<Mensaje>(); //Creamos la lista que va a contener los mensajes
    			mensajes.put(pet.nuevoMiembroUid,msg); //Los metemos
    		}
			LinkedList<Integer> usuarios = miembros.get(pet.grupo); //Copiamos la lista ya existente
			usuarios.add(pet.nuevoMiembroUid); //Metemos al usuario nuevo en la lista
			miembros.put(pet.grupo, usuarios); //Actualizamos el valor
		    // status OK
		    pet.chResp.out().write(true);
		}
		break;
	    }
	    case SALIR_GRUPO: {
	    	PetSalirGrupo pet = (PetSalirGrupo) chSalirGrupo.in().read();
			if(!creador.containsKey(pet.grupo)){
				pet.chResp.out().write(false);
			}
			if(!miembros.get(pet.grupo).contains(pet.miembroUid) || pet.miembroUid==creador.get(pet.grupo)){
				pet.chResp.out().write(false);
			}
			LinkedList<Integer> usuarios = miembros.get(pet.grupo);
			boolean encontrado = false;
			int pos = -1;
			for(int i = 0;i<usuarios.size() && !encontrado;i++){
				encontrado = usuarios.get(i) == pet.miembroUid;
				pos = i;
			}
			usuarios.remove(pos); //Eliminamos al usuario de la lista de miembros
			miembros.put(pet.grupo, usuarios); //Actualizamos la lista de miembros
			
			//Ahora eliminamos los mensajes del grupo del que hemos salido
			LinkedList<Mensaje> msg = eliminarMensajes(pet,mensajes); //Mensajes con los mensajes del grupo del que salimos eliminados
			mensajes.put(pet.miembroUid, msg); //Actualizamos los mensajes sin los eliminados
			pet.chResp.out().write(true);
		break;
	    }
	    case MANDAR_MENSAJE: {
	    	PetMandarMensaje pet = (PetMandarMensaje) chMandarMensaje.in().read();
			//Si el grupo no existe
			if(!creador.containsKey(pet.grupo)){
				pet.chResp.out().write(false);
			}
			//Si el miembro que quiere enviar el mensaje no esta en el grupo
			if(!miembros.get(pet.grupo).contains(pet.remitenteUid)){
				pet.chResp.out().write(false);
			}
			//Si no esta en la lista de mensajes
			if(!mensajes.containsKey(pet.remitenteUid)){ //Si no esta en mensajes
    			LinkedList<Mensaje> msg = new LinkedList<Mensaje>(); //Creamos la lista que va a contener los mensajes
    			mensajes.put(pet.remitenteUid,msg); //Los metemos
    		}
			
			Mensaje msg = new Mensaje(pet.remitenteUid,pet.grupo,pet.contenidos); //Creamos el mensaje que vamos a enviar
			LinkedList<Integer> usuarios = miembros.get(pet.grupo); //Copiamos los miembros del grupo a los que enviaremos el mensaje
			for(int i=0;i<usuarios.size();i++){
				LinkedList<Mensaje> ms = mensajes.get(usuarios.get(i)); //Copiamos los mensajes del usuario
				ms.add(msg); //Metemos el nuevo mensaje
				mensajes.put(usuarios.get(i),ms); //Actualizamos la lista de mensajes pendientes
			}
			
			pet.chResp.out().write(true);
		break;
	    }
	    case LEER: {
	    	PetLeer pet = (PetLeer) chPetLeer.in().read();
	    	if(!mensajes.containsKey(pet.uid)){ //Si no esta en mensajes
    			LinkedList<Mensaje> msg = new LinkedList<Mensaje>(); //Creamos la lista que va a contener los mensajes
    			mensajes.put(pet.uid,msg); //Los metemos
    		}
			if(!mensajes.containsKey(pet.uid) || mensajes.get(pet.uid).isEmpty()){
				//condicion de bloqueo
			espera.put(pet.uid,espera.get(pet.uid));
			pet.chResp.in().read();
			}
			Mensaje msg = null;
			LinkedList<Mensaje> ms = mensajes.get(pet.uid);
			msg = ms.removeFirst();
			mensajes.put(pet.uid, ms);
			
			pet.chResp.out().write(true);
			
	    	break;
	    }
	    } // END SWITCH

	    Set<Integer> keys = espera.keySet();//cogemos las keys de los mensajes esperando
		boolean signaled = false;//variable para cerciorar que solo se senializa una variable por cada desbloqueo
		for(Integer key: keys){
			if(mensajes.containsKey(key) && !mensajes.get(key).isEmpty()){
				espera.get(key).out().write(true);
				signaled = true;//dejamos constancia de la operacion anterior
			}
			if(signaled) break;// si senializamos debemos salir del bucle
		}
	} // END while(true) SERVIDOR
    } // END run()
} //