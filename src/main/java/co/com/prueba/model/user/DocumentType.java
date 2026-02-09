package co.com.prueba.model.user;


import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum DocumentType {
    P("P", "PASAPORTE"),
    C("C","CEDULA DE CIUDADANIA");

    private String documentType;
    private String description;

}
