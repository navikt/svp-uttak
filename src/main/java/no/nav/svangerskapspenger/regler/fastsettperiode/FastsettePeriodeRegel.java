package no.nav.svangerskapspenger.regler.fastsettperiode;

import no.nav.svangerskapspenger.regler.fastsettperiode.betingelser.SjekkBarnetsDødsdato;
import no.nav.svangerskapspenger.regler.fastsettperiode.betingelser.SjekkBrukersDødsdato;
import no.nav.svangerskapspenger.regler.fastsettperiode.betingelser.SjekkFerie;
import no.nav.svangerskapspenger.regler.fastsettperiode.betingelser.SjekkFødselsdato;
import no.nav.svangerskapspenger.regler.fastsettperiode.betingelser.SjekkFørsteLovligeUttaksdato;
import no.nav.svangerskapspenger.regler.fastsettperiode.betingelser.SjekkHullUttak;
import no.nav.svangerskapspenger.regler.fastsettperiode.betingelser.SjekkOpphørsdatoForMedlemskap;
import no.nav.svangerskapspenger.regler.fastsettperiode.betingelser.SjekkTermindato;
import no.nav.svangerskapspenger.regler.fastsettperiode.grunnlag.FastsettePeriodeGrunnlag;
import no.nav.svangerskapspenger.domene.resultat.PeriodeIkkeOppfyltÅrsak;
import no.nav.svangerskapspenger.domene.resultat.PeriodeOppfyltÅrsak;
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

        var sjekkHull = rs.hvisRegel(SjekkHullUttak.ID, "Er perioden etter et hull uten ytelse?")
            .hvis(new SjekkHullUttak(), Sluttpunkt.ikkeOppfylt("UT8014", PeriodeIkkeOppfyltÅrsak.PERIODEN_ER_ETTER_ET_OPPHOLD_I_UTTAK))
            .ellers(Sluttpunkt.oppfylt("UT8011", PeriodeOppfyltÅrsak.UTTAK_ER_INNVILGET));

        var sjekkFerie = rs.hvisRegel(SjekkFerie.ID, "Er perioden i en ferieperiode?")
            .hvis(new SjekkFerie(), Sluttpunkt.ikkeOppfylt("UT8012", PeriodeIkkeOppfyltÅrsak.PERIODEN_ER_SAMTIDIG_SOM_EN_FERIE))
            .ellers(sjekkHull);

        var sjekkTermindato = rs.hvisRegel(SjekkTermindato.ID, "Er perioden på eller etter \"termin minus tre uker\"?")
                .hvis(new SjekkTermindato(), Sluttpunkt.ikkeOppfylt("UT8010", PeriodeIkkeOppfyltÅrsak.PERIODEN_MÅ_SLUTTE_SENEST_TRE_UKER_FØR_TERMIN))
                .ellers(sjekkFerie);

        var sjekkFødselsdato = rs.hvisRegel(SjekkFødselsdato.ID, "Er fødselsdato kjent og er fødsel minst tre uker før termin og er perioden på eller etter fødselsdato?")
                .hvis(new SjekkFødselsdato(), Sluttpunkt.ikkeOppfylt("UT8009", PeriodeIkkeOppfyltÅrsak.PERIODEN_ER_IKKE_FØR_FØDSEL))
                .ellers(sjekkTermindato);

        var sjekkFørsteLovligeUttaksdato = rs.hvisRegel(SjekkFørsteLovligeUttaksdato.ID, "Er perioder før \"gyldig dato\" fra søknadsfrist?")
                .hvis(new SjekkFørsteLovligeUttaksdato(), Sluttpunkt.ikkeOppfylt("UT8007", PeriodeIkkeOppfyltÅrsak.SØKT_FOR_SENT))
                .ellers(sjekkFødselsdato);

        var sjekkOpphørsdatoForMedlemskap = rs.hvisRegel(SjekkOpphørsdatoForMedlemskap.ID, "Er perioden på eller etter opphørsdato for medlemskap?")
                .hvis(new SjekkOpphørsdatoForMedlemskap(), Sluttpunkt.ikkeOppfylt("UT8006", PeriodeIkkeOppfyltÅrsak.BRUKER_ER_IKKE_MEDLEM))
                .ellers(sjekkFørsteLovligeUttaksdato);

        var sjekkBarnetsDødsdato = rs.hvisRegel(SjekkBarnetsDødsdato.ID, "Er perioden på eller etter barnets/barnes dødsdato?")
                .hvis(new SjekkBarnetsDødsdato(), Sluttpunkt.ikkeOppfylt("UT8005", PeriodeIkkeOppfyltÅrsak.BARN_ER_DØDT))
                .ellers(sjekkOpphørsdatoForMedlemskap);

        return rs.hvisRegel(SjekkBrukersDødsdato.ID, "Er perioden på eller etter brukers dødsdato?")
                .hvis(new SjekkBrukersDødsdato(), Sluttpunkt.ikkeOppfylt("UT8004", PeriodeIkkeOppfyltÅrsak.BRUKER_ER_DØD))
                .ellers(sjekkBarnetsDødsdato);
    }

}
