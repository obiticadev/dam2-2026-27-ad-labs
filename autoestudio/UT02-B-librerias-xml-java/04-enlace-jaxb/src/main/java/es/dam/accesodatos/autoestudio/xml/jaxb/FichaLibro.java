package es.dam.accesodatos.autoestudio.xml.jaxb;

/** Modelo de práctica: codigo como atributo; titulo y anio como elementos, en ese orden. */
// TODO: @XmlType(propOrder = {"titulo", "anio"}); codigo no pertenece a propOrder al ser atributo.
public class FichaLibro {
    private String codigo;
    private String titulo;
    private int anio;

    public FichaLibro() {}
    public FichaLibro(String codigo, String titulo, int anio) {
        this.codigo = codigo;
        this.titulo = titulo;
        this.anio = anio;
    }
    public String getCodigo() { return codigo; }
    // TODO: @XmlAttribute.
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getTitulo() { return titulo; }
    // TODO: @XmlElement.
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public int getAnio() { return anio; }
    // TODO: @XmlElement.
    public void setAnio(int anio) { this.anio = anio; }
}
