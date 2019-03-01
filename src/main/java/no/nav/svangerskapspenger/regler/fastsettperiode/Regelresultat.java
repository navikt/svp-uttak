package no.nav.svangerskapspenger.regler.fastsettperiode;

import java.util.Optional;

import no.nav.fpsak.nare.evaluation.Evaluation;
import no.nav.fpsak.nare.evaluation.Resultat;
import no.nav.fpsak.nare.evaluation.summary.EvaluationSummary;
import no.nav.svangerskapspenger.domene.resultat.PeriodeÅrsak;
import no.nav.svangerskapspenger.domene.resultat.UtfallType;

public class Regelresultat {

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
        return getProperty(FastsettePeriodeUtfall.UTFALL, UtfallType.class);
    }

    public PeriodeÅrsak getPeriodeÅrsak() {
        return getProperty(FastsettePeriodeUtfall.ÅRSAK, PeriodeÅrsak.class);
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
