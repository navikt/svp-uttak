package no.nav.svangerskapspenger.regler.fastsettperiode;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import no.nav.fpsak.nare.evaluation.Evaluation;
import no.nav.fpsak.nare.evaluation.node.SingleEvaluation;
import no.nav.fpsak.nare.specification.LeafSpecification;
import no.nav.svangerskapspenger.domene.resultat.PeriodeIkkeOppfyltÅrsak;
import no.nav.svangerskapspenger.domene.resultat.PeriodeOppfyltÅrsak;
import no.nav.svangerskapspenger.domene.resultat.PeriodeÅrsak;
import no.nav.svangerskapspenger.domene.resultat.UtfallType;
import no.nav.svangerskapspenger.regler.fastsettperiode.grunnlag.FastsettePeriodeGrunnlag;

class FastsettePeriodeUtfall extends LeafSpecification<FastsettePeriodeGrunnlag> {

    static final String UTFALL = "UTFALL";
    static final String ÅRSAK = "ÅRSAK";


    private final PeriodeOutcome ruleReasonRef;
    private final List<BiConsumer<SingleEvaluation, FastsettePeriodeGrunnlag>> utfallSpesifiserere = new ArrayList<>();

    FastsettePeriodeUtfall(String id, PeriodeOppfyltÅrsak periodeÅrsak) {
        this(id, periodeÅrsak, UtfallType.OPPFYLT);
    }

    FastsettePeriodeUtfall(String id, PeriodeIkkeOppfyltÅrsak periodeÅrsak) {
        this(id, periodeÅrsak, UtfallType.IKKE_OPPFYLT);
    }

    private FastsettePeriodeUtfall(String id, PeriodeÅrsak periodeÅrsak, UtfallType utfallType) {
        super(id);
        if (periodeÅrsak == null) {
            throw new IllegalArgumentException("Årsak kan ikke være null.");
        }
        this.ruleReasonRef = new PeriodeOutcome(periodeÅrsak, utfallType);

        this.utfallSpesifiserere.add((singleEvaluation, grunnlag) -> {
            singleEvaluation.setEvaluationProperty(UTFALL, utfallType);
            singleEvaluation.setEvaluationProperty(ÅRSAK, periodeÅrsak);
        });
    }

    @Override
    public Evaluation evaluate(FastsettePeriodeGrunnlag grunnlag) {
        SingleEvaluation utfall = getHovedUtfall();
        spesifiserUtfall(utfall, grunnlag);
        return utfall;
    }

    private void spesifiserUtfall(SingleEvaluation utfall, FastsettePeriodeGrunnlag grunnlag) {
        if (utfallSpesifiserere.isEmpty()) {
            return;
        }
        utfallSpesifiserere.forEach(utfallSpesifiserer -> utfallSpesifiserer.accept(utfall, grunnlag));
    }

    private SingleEvaluation getHovedUtfall() {
        return ruleReasonRef.periodeÅrsak() instanceof PeriodeOppfyltÅrsak ? ja(ruleReasonRef): nei(ruleReasonRef);
    }

}
