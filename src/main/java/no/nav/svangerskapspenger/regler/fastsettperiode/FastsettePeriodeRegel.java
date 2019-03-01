package no.nav.svangerskapspenger.regler.fastsettperiode;

import java.util.Optional;

import no.nav.fpsak.nare.evaluation.Resultat;
import no.nav.fpsak.nare.evaluation.summary.EvaluationSummary;
import no.nav.svangerskapspenger.domene.resultat.PeriodeÅrsak;
import no.nav.svangerskapspenger.domene.resultat.UtfallType;
import no.nav.svangerskapspenger.regler.fastsettperiode.betingelser.SjekkBarnetsDødsdato;
import no.nav.svangerskapspenger.regler.fastsettperiode.betingelser.SjekkBrukersDødsdato;
import no.nav.svangerskapspenger.regler.fastsettperiode.betingelser.SjekkFødselsdato;
import no.nav.svangerskapspenger.regler.fastsettperiode.betingelser.SjekkFørsteLovligeUttaksdato;
import no.nav.svangerskapspenger.regler.fastsettperiode.betingelser.SjekkOpphørsdatoForMedlemskap;
import no.nav.svangerskapspenger.regler.fastsettperiode.betingelser.SjekkTermindato;
import no.nav.svangerskapspenger.regler.fastsettperiode.grunnlag.FastsettePeriodeGrunnlag;
import no.nav.svangerskapspenger.regler.fastsettperiode.utfall.Sluttpunkt;
import no.nav.svangerskapspenger.domene.resultat.PeriodeAvslåttÅrsak;
import no.nav.svangerskapspenger.domene.resultat.PeriodeInnvilgetÅrsak;
import no.nav.fpsak.nare.RuleService;
import no.nav.fpsak.nare.Ruleset;
import no.nav.fpsak.nare.doc.RuleDocumentation;
import no.nav.fpsak.nare.evaluation.Evaluation;
import no.nav.fpsak.nare.specification.Specification;

/**
 * Regeltjeneste som fastsetter uttaksperioder som er søkt om for svangerskapspenger.
 */
@RuleDocumentation(value = FastsettePeriodeRegel.ID, specificationReference = "TODO")
public class FastsettePeriodeRegel implements RuleService<FastsettePeriodeGrunnlag> {

    public static final String ID = "SVP_VK 14.4";

    private final Ruleset<FastsettePeriodeGrunnlag> rs = new Ruleset<>();

    public FastsettePeriodeRegel() {
        // For dokumentasjonsgenerering
    }

    @Override
    public Evaluation evaluer(FastsettePeriodeGrunnlag grunnlag) {
        return getSpecification().evaluate(grunnlag);
    }

    @Override
    public Specification<FastsettePeriodeGrunnlag> getSpecification() {

        var sjekkTermindato = rs.hvisRegel(SjekkTermindato.ID, "Er perioden på eller etter \"termin minus tre uker\"?")
                .hvis(new SjekkTermindato(), Sluttpunkt.avslag("UT8010", PeriodeAvslåttÅrsak.PERIODEN_MÅ_SLUTTE_SENEST_TRE_UKER_FØR_TERMIN))
                .ellers(Sluttpunkt.innvilgelse("UT8011", PeriodeInnvilgetÅrsak.UTTAK_ER_INNVILGET));

        var sjekkFødselsdato = rs.hvisRegel(SjekkFødselsdato.ID, "Er fødselsdato kjent og er fødsel minst tre uker før termin og er perioden på eller etter fødselsdato?")
                .hvis(new SjekkFødselsdato(), Sluttpunkt.avslag("UT8009", PeriodeAvslåttÅrsak.PERIODEN_ER_IKKE_FØR_FØDSEL))
                .ellers(sjekkTermindato);

        var sjekkFørsteLovligeUttaksdato = rs.hvisRegel(SjekkFørsteLovligeUttaksdato.ID, "Er perioder før \"gyldig dato\" fra søknadsfrist?")
                .hvis(new SjekkFørsteLovligeUttaksdato(), Sluttpunkt.avslag("UT8007", PeriodeAvslåttÅrsak.SØKT_FOR_SENT))
                .ellers(sjekkFødselsdato);

        var sjekkOpphørsdatoForMedlemskap = rs.hvisRegel(SjekkOpphørsdatoForMedlemskap.ID, "Er perioden på eller etter opphørsdato for medlemskap?")
                .hvis(new SjekkOpphørsdatoForMedlemskap(), Sluttpunkt.avslag("UT8006", PeriodeAvslåttÅrsak.BRUKER_ER_IKKE_MEDLEM))
                .ellers(sjekkFørsteLovligeUttaksdato);

        var sjekkBarnetsDødsdato = rs.hvisRegel(SjekkBarnetsDødsdato.ID, "Er perioden på eller etter barnets/barnes dødsdato?")
                .hvis(new SjekkBarnetsDødsdato(), Sluttpunkt.avslag("UT8005", PeriodeAvslåttÅrsak.BARN_ER_DØDT))
                .ellers(sjekkOpphørsdatoForMedlemskap);

        return rs.hvisRegel(SjekkBrukersDødsdato.ID, "Er perioden på eller etter brukers dødsdato?")
                .hvis(new SjekkBrukersDødsdato(), Sluttpunkt.avslag("UT8004", PeriodeAvslåttÅrsak.BRUKER_ER_DØD))
                .ellers(sjekkBarnetsDødsdato);
    }

    public static final class FastsettePeriodePropertyType {

        public static final String UTFALL = "UTFALL";
        public static final String ÅRSAK = "ÅRSAK";

        private FastsettePeriodePropertyType() {
            //For å hindre instanser
        }

    }

    public static class Regelresultat {

        private final EvaluationSummary evaluationSummary;

        public Regelresultat(Evaluation evaluation) {
            this.evaluationSummary = new EvaluationSummary(evaluation);
        }

        private <T> T getProperty(String tag, Class<T> clazz) {
            Object obj = getProperty(tag);
            if (obj != null && !clazz.isAssignableFrom(obj.getClass())) {
                throw new IllegalArgumentException("Kan ikke hente property " + tag + ". Forventet " + clazz.getSimpleName() + " men fant " + obj.getClass());
            }
            return (T) obj;
        }


        public UtfallType getUtfallType() {
            return getProperty(FastsettePeriodePropertyType.UTFALL, UtfallType.class);
        }

        public PeriodeÅrsak getPeriodeÅrsak() {
            return getProperty(FastsettePeriodePropertyType.ÅRSAK, PeriodeÅrsak.class);
        }


        public boolean oppfylt() {
            return !evaluationSummary.leafEvaluations(Resultat.JA).isEmpty();
        }

        private Object getProperty(String tag) {
            Optional<Evaluation> first = evaluationSummary.leafEvaluations().stream()
                .filter(e -> e.getEvaluationProperties() != null)
                .findFirst();

            return first.map(evaluation -> evaluation.getEvaluationProperties().get(tag)).orElse(null);
        }

    }
}
