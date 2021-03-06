/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.units.malelab.jgea.problem.classification;

import it.units.malelab.jgea.core.ProblemWithValidation;
import it.units.malelab.jgea.core.fitness.ClassificationFitness;
import it.units.malelab.jgea.core.function.BiFunction;
import it.units.malelab.jgea.core.function.Function;
import it.units.malelab.jgea.core.function.NonDeterministicFunction;
import it.units.malelab.jgea.core.util.Pair;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author eric
 */
public abstract class AbstractClassificationProblem<C, O, E extends Enum<E>> implements ProblemWithValidation<C, List<Double>>, BiFunction<C, O, E> {
  
  private final ClassificationFitness<C, O, E> fitnessFunction;
  private final ClassificationFitness<C, O, E> validationFunction;
  private final List<Pair<O, E>> learningData;
  private final List<Pair<O, E>> validationData;

  public AbstractClassificationProblem(List<Pair<O, E>> data, int folds, int i, ClassificationFitness.Metric learningMetric, ClassificationFitness.Metric validationMetric) {
    validationData = DataUtils.fold(data, i, folds);
    learningData = new ArrayList<>(data);
    learningData.removeAll(validationData);
    this.fitnessFunction = new ClassificationFitness<>(learningData, this, learningMetric);
    this.validationFunction = new ClassificationFitness<>(validationData, this, validationMetric);
  }

  @Override
  public Function<C, List<Double>> getValidationFunction() {
    return validationFunction;
  }

  @Override
  public NonDeterministicFunction<C, List<Double>> getFitnessFunction() {
    return fitnessFunction;
  }

  public List<Pair<O, E>> getLearningData() {
    return learningData;
  }

  public List<Pair<O, E>> getValidationData() {
    return validationData;
  }
  
}
