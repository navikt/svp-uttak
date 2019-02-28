package no.nav.svangerskapspenger.regler.uttak.fastsetteperiode;

import no.nav.svangerskapspenger.regler.uttak.fastsetteperiode.betingelser.SjekkBarnetsDødsdato;
import no.nav.svangerskapspenger.regler.uttak.fastsetteperiode.betingelser.SjekkBrukersDødsdato;
import no.nav.svangerskapspenger.regler.uttak.fastsetteperiode.betingelser.SjekkFødselsdato;
import no.nav.svangerskapspenger.regler.uttak.fastsetteperiode.betingelser.SjekkFørsteLovligeUttaksdato;
import no.nav.svangerskapspenger.regler.uttak.fastsetteperiode.betingelser.SjekkOpphørsdatoForMedlemskap;
import no.nav.svangerskapspenger.regler.uttak.fastsetteperiode.betingelser.SjekkTermindato;
import no.nav.svangerskapspenger.regler.uttak.fastsetteperiode.grunnlag.FastsettePeriodeGrunnlag;
import no.nav.svangerskapspenger.domene.resultat.PeriodeAvslåttÅrsak;
import no.nav.svangerskapspenger.domene.resultat.PeriodeInnvilgetÅrsak;
import no.nav.svangerskapspenger.regler.uttak.fastsetteperiode.utfall.Sluttpunkt;
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

}
