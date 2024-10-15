package no.nav.svangerskapspenger.regler.fastsettperiode.betingelser;


import java.util.Objects;

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
        // Kompatibel med eldre grunnlag v/deserialisering - vil gå videre dersom true eller null
        if (Objects.equals(grunnlag.getInngangsvilkår().medlemskapOppfylt(), Boolean.FALSE)) {
            return ja();
        }
        return grunnlag.getAvklarteDatoer().getOpphørsdatoForMedlemskap().map(opphørsdatoForMedlemskap -> {
            var startUttaksperiode = grunnlag.getAktuellPeriode().getFom();
            return startUttaksperiode.equals(opphørsdatoForMedlemskap) || startUttaksperiode.isAfter(opphørsdatoForMedlemskap) ? ja() : nei();
        }).orElse(nei());
    }
}
