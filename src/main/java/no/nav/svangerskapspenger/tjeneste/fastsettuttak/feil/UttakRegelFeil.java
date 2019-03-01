package no.nav.svangerskapspenger.tjeneste.fastsettuttak.feil;

public class UttakRegelFeil extends RuntimeException {

    public UttakRegelFeil(String message, Throwable cause) {
        super(message, cause);
    }
}
