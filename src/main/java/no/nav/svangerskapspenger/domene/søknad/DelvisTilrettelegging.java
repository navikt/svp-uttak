package no.nav.svangerskapspenger.domene.søknad;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

public class DelvisTilrettelegging implements Tilrettelegging {

    private LocalDate tilretteleggingArbeidsgiverDato;
    private final BigDecimal tilretteleggingsprosent;


    public DelvisTilrettelegging(LocalDate tilretteleggingArbeidsgiverDato, BigDecimal tilretteleggingsprosent) {
        this.tilretteleggingArbeidsgiverDato = tilretteleggingArbeidsgiverDato;
        this.tilretteleggingsprosent = tilretteleggingsprosent;
    }

    @Override
    public LocalDate getArbeidsgiversDato() {
        return tilretteleggingArbeidsgiverDato;
    }

    @Override
    public TilretteleggingKryss getTilretteleggingKryss() {
        return TilretteleggingKryss.B;
    }

    @Override
    public BigDecimal getTilretteleggingsprosent() {
        return tilretteleggingsprosent;
    }

    @Override
    public void setArbeidsgiversDato(LocalDate arbeidsgiversDato) {
        this.tilretteleggingArbeidsgiverDato = arbeidsgiversDato;
    }
}
