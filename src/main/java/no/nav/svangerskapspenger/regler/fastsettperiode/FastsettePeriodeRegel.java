package no.nav.svangerskapspenger.regler.fastsettperiode;

import no.nav.fpsak.nare.RuleService;
import no.nav.fpsak.nare.Ruleset;
import no.nav.fpsak.nare.doc.RuleDocumentation;
import no.nav.fpsak.nare.evaluation.Evaluation;
import no.nav.fpsak.nare.specification.Specification;
import no.nav.svangerskapspenger.domene.resultat.PeriodeIkkeOppfyltÅrsak;
import no.nav.svangerskapspenger.domene.resultat.PeriodeOppfyltÅrsak;
import no.nav.svangerskapspenger.regler.fastsettperiode.betingelser.SjekkBarnetsDødsdato;
import no.nav.svangerskapspenger.regler.fastsettperiode.betingelser.SjekkBrukersDødsdato;
import no.nav.svangerskapspenger.regler.fastsettperiode.betingelser.SjekkFerie;
import no.nav.svangerskapspenger.regler.fastsettperiode.betingelser.SjekkFødselsdato;
import no.nav.svangerskapspenger.regler.fastsettperiode.betingelser.SjekkFørsteLovligeUttaksdato;
import no.nav.svangerskapspenger.regler.fastsettperiode.betingelser.SjekkHullUttak;
import no.nav.svangerskapspenger.regler.fastsettperiode.betingelser.SjekkManueltSVPVilkår;
import no.nav.svangerskapspenger.regler.fastsettperiode.betingelser.SjekkOpptjeningsvilkåret;
import no.nav.svangerskapspenger.regler.fastsettperiode.betingelser.SjekkOpphørsdatoForMedlemskap;
import no.nav.svangerskapspenger.regler.fastsettperiode.betingelser.SjekkStønadsperiode;
import no.nav.svangerskapspenger.regler.fastsettperiode.betingelser.SjekkTermindato;
import no.nav.svangerskapspenger.regler.fastsettperiode.grunnlag.FastsettePeriodeGrunnlag;

/**
 * Regeltjeneste som fastsetter uttaksperioder som er søkt om for svangerskapspenger.
 */
@RuleDocumentation(value = FastsettePeriodeRegel.ID, specificationReference = "TODO")
public class FastsettePeriodeRegel implements RuleService<FastsettePeriodeGrunnlag> {

    static final String ID = "SVP_VK 14.4";

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

        return rs.hvisRegel(ID, "Fastsett periode")

            .hvis(new SjekkBrukersDødsdato(), Sluttpunkt.ikkeOppfylt("UT8004", PeriodeIkkeOppfyltÅrsak.BRUKER_ER_DØD))

            .hvis(new SjekkBarnetsDødsdato(), Sluttpunkt.ikkeOppfylt("UT8005", PeriodeIkkeOppfyltÅrsak.BARN_ER_DØDT))

            .hvis(new SjekkOpphørsdatoForMedlemskap(), Sluttpunkt.ikkeOppfylt("UT8006", PeriodeIkkeOppfyltÅrsak.BRUKER_ER_IKKE_MEDLEM))

            .hvis(new SjekkFørsteLovligeUttaksdato(), Sluttpunkt.ikkeOppfylt("UT8007", PeriodeIkkeOppfyltÅrsak.SØKT_FOR_SENT))

            .hvis(new SjekkFødselsdato(), Sluttpunkt.ikkeOppfylt("UT8009", PeriodeIkkeOppfyltÅrsak.PERIODEN_ER_IKKE_FØR_FØDSEL))

            .hvis(new SjekkTermindato(), Sluttpunkt.ikkeOppfylt("UT8010", PeriodeIkkeOppfyltÅrsak.PERIODEN_MÅ_SLUTTE_SENEST_TRE_UKER_FØR_TERMIN))

            .hvis(new SjekkStønadsperiode(), Sluttpunkt.ikkeOppfylt("UT8013", PeriodeIkkeOppfyltÅrsak.BEGYNT_ANNEN_SAK))

            .hvis(new SjekkFerie(), Sluttpunkt.ikkeOppfylt("UT8012", PeriodeIkkeOppfyltÅrsak.PERIODEN_ER_SAMTIDIG_SOM_EN_FERIE))

            .hvis(new SjekkHullUttak(), Sluttpunkt.ikkeOppfylt("UT8014", PeriodeIkkeOppfyltÅrsak.PERIODEN_ER_ETTER_ET_OPPHOLD_I_UTTAK))

            .hvis(new SjekkOpptjeningsvilkåret(), Sluttpunkt.ikkeOppfylt("UT8015", PeriodeIkkeOppfyltÅrsak.OPPTJENINGSVILKÅRET_IKKE_OPPFYLT))

            .hvis(new SjekkManueltSVPVilkår(), Sluttpunkt.ikkeOppfylt("UT8016   ", PeriodeIkkeOppfyltÅrsak.SVANGERSKAPSVILKÅRET_IKKE_OPPFYLT))

            .ellers(Sluttpunkt.oppfylt("UT8011", PeriodeOppfyltÅrsak.UTTAK_ER_INNVILGET));
    }

}
