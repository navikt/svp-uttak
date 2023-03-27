package no.nav.svangerskapspenger.regler.fastsettperiode.betingelser;


import no.nav.fpsak.nare.doc.RuleDocumentation;
import no.nav.fpsak.nare.evaluation.Evaluation;
import no.nav.fpsak.nare.specification.LeafSpecification;
import no.nav.svangerskapspenger.regler.fastsettperiode.grunnlag.FastsettePeriodeGrunnlag;

@RuleDocumentation(SjekkOpphørsdatoForMedlemskap.ID)
public class SjekkOpphørsdatoForMedlemskap extends LeafSpecification<FastsettePeriodeGrunnlag> {

    public static final String ID = "SVP_VK 14.4.3";

    public SjekkOpphørsdatoForMedlemskap() {
        super(ID);
    }

    @Override
    public Evaluation evaluate(FastsettePeriodeGrunnlag grunnlag) {
        return grunnlag.getAvklarteDatoer().getOpphørsdatoForMedlemskap().map(opphørsdatoForMedlemskap -> {
            var startUttaksperiode = grunnlag.getAktuellPeriode().getFom();
            return startUttaksperiode.equals(opphørsdatoForMedlemskap) || startUttaksperiode.isAfter(opphørsdatoForMedlemskap) ? ja() : nei();
        }).orElse(nei());
    }
}
