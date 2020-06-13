// Un mensaje leido

package cc.qp;

public class Mensaje {
  private int remitente;
  private String grupo;
  private Object contenidos;
  
  public Mensaje(int remitente, String grupo, Object contenidos) {
    this.remitente = remitente;
    this.grupo = grupo;
    this.contenidos = contenidos;
  }

  public int getRemitente() {
    return remitente;
  }

  public String getGrupo() {
    return grupo;
  }

  public Object getContenidos() {
    return contenidos;
  }

  @Override
  public int hashCode() {
    return getRemitente()+getGrupo().hashCode()+getContenidos().hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    boolean isEqual = (obj instanceof Mensaje);
    if (isEqual) {
      Mensaje otherMensaje = (Mensaje)obj;
      isEqual = getRemitente() == otherMensaje.getRemitente();
      if (isEqual) {
        if (getGrupo() == null)
          isEqual = (getGrupo() == otherMensaje.getGrupo());
        else
          isEqual = getGrupo().equals(otherMensaje.getGrupo());
      }
      if (isEqual) {
        if (getContenidos() == null)
          isEqual = (getContenidos() == otherMensaje.getContenidos());
        else
          isEqual = getContenidos().equals(otherMensaje.getContenidos());
      }
    }
    return isEqual;
  }

  @Override
  public String toString() {
    return "mensaje("+getRemitente()+","+getGrupo()+","+getContenidos()+")";
  }
}
