package no.nav.svangerskapspenger.domene.søknad;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface Tilrettelegging {

    LocalDate getArbeidsgiversDato();

    TilretteleggingKryss getTilretteleggingKryss();

    BigDecimal getTilretteleggingsprosent();

    void setArbeidsgiversDato(LocalDate arbeidsgiversDato);

    BigDecimal getOverstyrtUtbetalingsgrad();

}
