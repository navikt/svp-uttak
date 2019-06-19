package no.nav.svangerskapspenger.domene.søknad;

import java.math.BigDecimal;
import java.time.LocalDate;

public class IngenTilrettelegging implements Tilrettelegging {

    private LocalDate tilretteleggingOpphørerDato;

    public IngenTilrettelegging(LocalDate tilretteleggingOpphørerDato) {
        this.tilretteleggingOpphørerDato = tilretteleggingOpphørerDato;
    }

    @Override
    public LocalDate getArbeidsgiversDato() {
        return tilretteleggingOpphørerDato;
    }

    @Override
    public TilretteleggingKryss getTilretteleggingKryss() {
        return TilretteleggingKryss.C;
    }

    @Override
    public BigDecimal getTilretteleggingsprosent() {
        return BigDecimal.ZERO;
    }

    @Override
    public void setArbeidsgiversDato(LocalDate arbeidsgiversDato) {
        this.tilretteleggingOpphørerDato = arbeidsgiversDato;
    }


}
