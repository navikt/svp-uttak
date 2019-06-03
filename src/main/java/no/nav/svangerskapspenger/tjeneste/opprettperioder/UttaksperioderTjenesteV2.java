package no.nav.svangerskapspenger.tjeneste.opprettperioder;

import no.nav.svangerskapspenger.domene.felles.Arbeidsforhold;
import no.nav.svangerskapspenger.domene.resultat.ArbeidsforholdIkkeOppfyltÅrsak;
import no.nav.svangerskapspenger.domene.resultat.Uttaksperiode;
import no.nav.svangerskapspenger.domene.resultat.Uttaksperioder;
import no.nav.svangerskapspenger.domene.søknad.Søknad;
import no.nav.svangerskapspenger.domene.søknad.Tilrettelegging;
import no.nav.svangerskapspenger.domene.søknad.TilretteleggingKryss;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class UttaksperioderTjenesteV2 implements UttaksperioderTjeneste {

    private static final BigDecimal FULL_UTBETALINGSGRAD = BigDecimal.valueOf(100L);

    @Override
    public Set<ManuellBehandling> opprett(List<Søknad> søknader, Uttaksperioder uttaksperioder) {
        var manuellbehandlingSet = EnumSet.noneOf(ManuellBehandling.class);


        søknader.forEach(søknad -> {

            if (søknad.getTilretteliggingBehovDato().isAfter(søknad.sisteDagFørTermin())) {
                uttaksperioder.avslåForArbeidsforhold(søknad.getArbeidsforhold(), ArbeidsforholdIkkeOppfyltÅrsak.LEGES_DATO_IKKE_FØR_TRE_UKER_FØR_TERMINDATO);
            } else {
                var tilrettelegginger = fjernUnødvendigeTilrettelegginger(søknad);


                opprettPerioder(søknad, tilrettelegginger, uttaksperioder);

            }
        });

        return manuellbehandlingSet;
    }

    private void opprettPerioder(Søknad søknad, List<Tilrettelegging> tilrettelegginger, Uttaksperioder uttaksperioder) {
        if (tilrettelegginger.size() == 1) {
            var førsteTilrettelegging = tilrettelegginger.get(0);
            if (førsteTilrettelegging.getArbeidsgiversDato().equals(søknad.getTilretteliggingBehovDato()) &&
                førsteTilrettelegging.getTilretteleggingKryss().equals(TilretteleggingKryss.A)) {
                uttaksperioder.avslåForArbeidsforhold(søknad.getArbeidsforhold(), ArbeidsforholdIkkeOppfyltÅrsak.ARBEIDSGIVER_KAN_TILRETTELEGGE);
                return;
            }

            if (førsteTilrettelegging.getArbeidsgiversDato().isAfter(søknad.sisteDagFørTermin()) &&
                førsteTilrettelegging.getTilretteleggingKryss().equals(TilretteleggingKryss.C)) {
                uttaksperioder.avslåForArbeidsforhold(søknad.getArbeidsforhold(), ArbeidsforholdIkkeOppfyltÅrsak.ARBEIDSGIVER_KAN_TILRETTELEGGE_FREM_TIL_3_UKER_FØR_TERMIN);
                return;
            }
        }

        if (tilrettelegginger.isEmpty()) {
            uttaksperioder.leggTilPerioder(søknad.getArbeidsforhold(), new Uttaksperiode(søknad.getTilretteliggingBehovDato(), søknad.sisteDagFørTermin(), FULL_UTBETALINGSGRAD));
            return;
        }

        if (tilrettelegginger.size() == 1) {
            var førsteTilrettelegging = tilrettelegginger.get(0);
            if (førsteTilrettelegging.getArbeidsgiversDato().equals(søknad.getTilretteliggingBehovDato()) &&
                førsteTilrettelegging.getTilretteleggingKryss().equals(TilretteleggingKryss.C)) {
                uttaksperioder.leggTilPerioder(søknad.getArbeidsforhold(), new Uttaksperiode(søknad.getTilretteliggingBehovDato(), søknad.sisteDagFørTermin(), FULL_UTBETALINGSGRAD));
                return;
            }
        }

        var sorterteTilrettelegginger = tilrettelegginger.stream().sorted(Comparator.comparing(Tilrettelegging::getArbeidsgiversDato)).collect(Collectors.toList());
        opprettPerioderSisteSteg(søknad, uttaksperioder, sorterteTilrettelegginger);
    }

    private void opprettPerioderSisteSteg(Søknad søknad, Uttaksperioder uttaksperioder, List<Tilrettelegging> sorterteTilrettelegginger) {
        LocalDate nesteFom = søknad.getTilretteliggingBehovDato();
        if (!sorterteTilrettelegginger.isEmpty() && søknad.getTilretteliggingBehovDato().isBefore(sorterteTilrettelegginger.get(0).getArbeidsgiversDato())) {
            var utbetalingsgrad = FULL_UTBETALINGSGRAD;
            if (sorterteTilrettelegginger.get(0).getTilretteleggingKryss().equals(TilretteleggingKryss.C)) {
                utbetalingsgrad = BigDecimal.ZERO;
            }
            opprettPeriode(uttaksperioder, søknad.getArbeidsforhold(), nesteFom, sorterteTilrettelegginger.get(0).getArbeidsgiversDato().minusDays(1), utbetalingsgrad);
            nesteFom = sorterteTilrettelegginger.get(0).getArbeidsgiversDato();
        }
        for (int i = 0; i < sorterteTilrettelegginger.size(); i++) {
            var fom = nesteFom;
            LocalDate tom;
            if (i < sorterteTilrettelegginger.size()-1) {
                tom = sorterteTilrettelegginger.get(i+1).getArbeidsgiversDato().minusDays(1);
            } else {
                tom = søknad.sisteDagFørTermin();
            }
            var utbetalingsgrad = FULL_UTBETALINGSGRAD;
            if (i >= 0) {
                var tilrettelegging = sorterteTilrettelegginger.get(i);
                var kryss = tilrettelegging.getTilretteleggingKryss();

                if (kryss.equals(TilretteleggingKryss.A)) {
                    utbetalingsgrad = BigDecimal.ZERO;
                } else if (kryss.equals(TilretteleggingKryss.B)) {
                    //TODO ta hensyn til stillingsprosent fra  aareg
                    utbetalingsgrad = FULL_UTBETALINGSGRAD.subtract(tilrettelegging.getTilretteleggingsprosent().divide(BigDecimal.valueOf(100L)).multiply(BigDecimal.valueOf(100L)));
                }
            }
            opprettPeriode(uttaksperioder, søknad.getArbeidsforhold(), fom, tom, utbetalingsgrad);
            nesteFom = tom.plusDays(1);
        }
    }

    private void opprettPeriode(Uttaksperioder uttaksperioder, Arbeidsforhold arbeidsforhold, LocalDate fom, LocalDate tom, BigDecimal utbetalingsgrad) {
        if (tom.isAfter(fom) || fom.equals(tom)) {
            uttaksperioder.leggTilPerioder(arbeidsforhold, new Uttaksperiode(fom, tom, utbetalingsgrad));
        }
    }

    private List<Tilrettelegging> fjernUnødvendigeTilrettelegginger(Søknad søknad) {
        var tilretteleggingerEtterBehovDato = søknad.getTilrettelegginger()
            .stream()
            .filter(tilrettelegging ->
                tilrettelegging.getArbeidsgiversDato().isAfter(søknad.getTilretteliggingBehovDato()))
            .collect(Collectors.toList());

        var tilretteleggingerFørBehovDato = søknad.getTilrettelegginger()
            .stream()
            .filter(tilrettelegging ->
                tilrettelegging.getArbeidsgiversDato().isBefore(søknad.getTilretteliggingBehovDato()))
            .collect(Collectors.toList());

        var tilretteleggingerPåBehovDato = søknad.getTilrettelegginger()
            .stream()
            .filter(tilrettelegging ->
                tilrettelegging.getArbeidsgiversDato().equals(søknad.getTilretteliggingBehovDato()))
            .collect(Collectors.toList());

        var resultat = tilretteleggingerEtterBehovDato;


        if (!tilretteleggingerFørBehovDato.isEmpty()) {

            if (!tilretteleggingerPåBehovDato.isEmpty()) {
                resultat = join(tilretteleggingerPåBehovDato, resultat);
            } else {
                var sisteTilrettelegging = sisteArbeidsgiverDato(tilretteleggingerFørBehovDato);
                sisteTilrettelegging.setArbeidsgiversDato(søknad.getTilretteliggingBehovDato());
                resultat = join(List.of(sisteTilrettelegging), resultat);
            }

        } else {
            resultat = søknad.getTilrettelegginger();
        }

        return resultat
            .stream()
            .filter(tilrettelegging -> !tilrettelegging.getArbeidsgiversDato().isAfter(søknad.sisteDagFørTermin()) || tilrettelegging.getTilretteleggingKryss().equals(TilretteleggingKryss.C))
            .collect(Collectors.toList());
    }

    private List<Tilrettelegging> join(List<Tilrettelegging> t1, List<Tilrettelegging> t2) {
        var samletListe = new ArrayList<Tilrettelegging>();
        samletListe.addAll(t1);
        samletListe.addAll(t2);
        return samletListe;
    }

    private Tilrettelegging sisteArbeidsgiverDato(List<Tilrettelegging> tilrettelegginger) {
        var sortert = tilrettelegginger.stream().sorted(Comparator.comparing(Tilrettelegging::getArbeidsgiversDato)).collect(Collectors.toList());
        return sortert.get(sortert.size()-1);
    }

}
