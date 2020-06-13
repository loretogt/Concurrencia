// Interfaz del recurso compartido ¿QuePasa?

package cc.qp;

public interface QuePasa {

  public void crearGrupo(int creadorUid, String grupo)
    throws PreconditionFailedException;

  public void anadirMiembro(int creadorUid, String grupo, int nuevoMiembroUid)
    throws PreconditionFailedException;
  
  public void salirGrupo(int miembroUid, String grupo)
    throws PreconditionFailedException;

  public void mandarMensaje(int remitenteUid, String grupo, Object contenidos)
    throws PreconditionFailedException;
  
  public Mensaje leer(int uid);
}
