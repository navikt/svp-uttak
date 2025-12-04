package no.nav.svangerskapspenger.domene.søknad;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import no.nav.svangerskapspenger.domene.felles.LukketPeriode;
import no.nav.svangerskapspenger.tjeneste.fastsettuttak.SvpOppholdÅrsak;
import no.nav.svangerskapspenger.utils.BevegeligeHelligdagerUtil;

public class Opphold extends LukketPeriode {
    private SvpOppholdÅrsak årsak;

    private Opphold(LocalDate fom, LocalDate tom, SvpOppholdÅrsak årsak) {
        super(fom, tom);
        this.årsak = årsak;
    }

    public static List<Opphold> opprett(LocalDate fom, LocalDate tom, SvpOppholdÅrsak årsak) {
        var opppholdPeriode = new Opphold(fom, tom, årsak);

        if (SvpOppholdÅrsak.FERIE.equals(årsak)) {
            return splittOppholdPåBevegeligeHelligdager(opppholdPeriode, årsak);
        }

        return List.of(opppholdPeriode);
    }

    private static List<Opphold> splittOppholdPåBevegeligeHelligdager(Opphold opppholdPeriode, SvpOppholdÅrsak årsak) {
        var oppholdPerioder = new ArrayList<Opphold>();
        var gjeldendePeriode = opppholdPeriode;
        var helligdager = BevegeligeHelligdagerUtil.finnBevegeligeHelligdagerUtenHelg(opppholdPeriode);

        for (LocalDate helligdag : helligdager) {
            gjeldendePeriode = behandleHelligdag(helligdag, gjeldendePeriode, oppholdPerioder, årsak);

            if (gjeldendePeriode == null) {
                return oppholdPerioder;
            }
        }

        oppholdPerioder.add(gjeldendePeriode);
        return oppholdPerioder;
    }

    private static Opphold behandleHelligdag(LocalDate helligdag, Opphold gjeldendePeriode,
                                             List<Opphold> oppholdPerioder, SvpOppholdÅrsak årsak) {
        if (!gjeldendePeriode.overlapper(helligdag)) {
            return gjeldendePeriode;
        }

        if (erKunEnDagIPerioden(gjeldendePeriode)) {
            return null;
        }

        if (erHelligdagFørsteDag(helligdag, gjeldendePeriode)) {
            return new Opphold(gjeldendePeriode.getFom().plusDays(1), gjeldendePeriode.getTom(), årsak);
        }

        if (erHelligdagSisteDag(helligdag, gjeldendePeriode)) {
            return new Opphold(gjeldendePeriode.getFom(), gjeldendePeriode.getTom().minusDays(1), årsak);
        }

        oppholdPerioder.add(new Opphold(gjeldendePeriode.getFom(), helligdag.minusDays(1), årsak));
        return new Opphold(helligdag.plusDays(1), gjeldendePeriode.getTom(), årsak);
    }

    private static boolean erKunEnDagIPerioden(Opphold periode) {
        return !periode.getFom().isBefore(periode.getTom());
    }

    private static boolean erHelligdagFørsteDag(LocalDate helligdag, Opphold periode) {
        return helligdag.equals(periode.getFom());
    }

    private static boolean erHelligdagSisteDag(LocalDate helligdag, Opphold periode) {
        return helligdag.equals(periode.getTom());
    }

    public SvpOppholdÅrsak getÅrsak() {
        return årsak;
    }
}
