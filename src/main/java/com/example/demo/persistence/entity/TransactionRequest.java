package com.example.demo.persistence.entity;

import lombok.Data;

@Data
public class TransactionRequest {
    private CardInfo tarjetaCreditoDebido;
    private double monto;
    private Configuracion configuracion;
    private String urlRedirect;
    private String nombre;
    private String apellido;
    private String email;
    private String ciudad;
    private String direccion;
    private String idPais;
    private String idRegion;
    private String codigoPostal;
    private String telefono;
    private DatosAdicionales datosAdicionales;

    @Data
    public static class CardInfo {
        private String numeroTarjeta;
        private String cvv;
        private int mesVencimiento;
        private int anioVencimiento;
    }

    @Data
    public static class Configuracion {
        private String emailsNotificacion;
        private String urlWebhook;
        private String telefonosNotificacion;
        private boolean notificarTransaccionCliente;
    }

    @Data
    public static class DatosAdicionales {
        private String additionalProp1;
        private String additionalProp2;
        private String additionalProp3;
    }
}
