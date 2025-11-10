package fr.arolla;

import java.time.LocalDateTime;

public record Evp(String type, LocalDateTime date, String statut) {
    public static final String PANIER_REPAS = "pannier repas";
    public static final String INCOMMODITE = "incommodité";
    public static final String SALISSURE = "salissure";
    public static final String TRANSPORT = "transport";
}
