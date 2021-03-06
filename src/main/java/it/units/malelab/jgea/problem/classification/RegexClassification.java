/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.units.malelab.jgea.problem.classification;

import it.units.malelab.jgea.core.fitness.ClassificationFitness;
import it.units.malelab.jgea.core.function.FunctionException;
import it.units.malelab.jgea.core.listener.Listener;
import it.units.malelab.jgea.core.util.Pair;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 *
 * @author eric
 */
public class RegexClassification extends AbstractClassificationProblem<String, String, RegexClassification.Label> {

  public static enum Label {
    FOUND, NOT_FOUND
  };

  public RegexClassification(List<Pair<String, Label>> data, int folds, int i, ClassificationFitness.Metric learningErrorMetric, ClassificationFitness.Metric validationErrorMetric) {
    super(data, folds, i, learningErrorMetric, validationErrorMetric);
  }

  @Override
  public RegexClassification.Label apply(String pattern, String string, Listener listener) throws FunctionException {
    boolean found = false;
    try {
      found = Pattern.compile(pattern).matcher(string).find();
    } catch (PatternSyntaxException ex) {
      //ignore
    }
    return found ? Label.FOUND : Label.NOT_FOUND;
  }

}
