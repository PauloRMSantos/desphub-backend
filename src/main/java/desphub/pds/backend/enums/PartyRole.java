package desphub.pds.backend.enums;

/** Papel de uma variável na procuração. Toda procuração exige um OUTORGANTE e um OUTORGADO. */
public enum PartyRole {
    OUTORGANTE, // quem concede os poderes (ex.: o cliente/proprietário)
    OUTORGADO   // quem recebe os poderes (ex.: o despachante/escritório)
}
