package no.nav.svangerskapspenger.domene.søknad;


import no.nav.svangerskapspenger.domene.resultat.Uttaksperioder;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface Tilrettelegging {

    void opprettPerioder(Uttaksperioder uttaksperioder, Søknad søknad);

    LocalDate getArbeidsgiversDato();

    TilretteleggingKryss getTilretteleggingKryss();

    BigDecimal getTilretteleggingsprosent();

    void setArbeidsgiversDato(LocalDate arbeidsgiversDato);

}
