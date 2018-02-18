/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.units.malelab.jgea.problem.classification;

import it.units.malelab.jgea.core.ProblemWithValidation;
import it.units.malelab.jgea.core.fitness.Classification;
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
public abstract class AbstractProblem<C, O, E extends Enum<E>> implements ProblemWithValidation<C, List<Double>>, BiFunction<C, O, E> {
  
  private final Classification<C, O, E> fitnessFunction;
  private final Classification<C, O, E> validationFunction;

  public AbstractProblem(List<Pair<O, E>> data, int folds, int i, Classification.ErrorMetric trainingErrorMetric, Classification.ErrorMetric validationErrorMetric) {
    List<Pair<O, E>> learningData = new ArrayList<>(data);
    learningData.removeAll(DataUtils.fold(data, i, folds));
    this.fitnessFunction = new Classification<>(learningData, this, trainingErrorMetric);
    this.validationFunction = new Classification<>(DataUtils.fold(data, i, folds), this, validationErrorMetric);
  }

  @Override
  public Function<C, List<Double>> getValidationFunction() {
    return validationFunction;
  }

  @Override
  public NonDeterministicFunction<C, List<Double>> getFitnessFunction() {
    return fitnessFunction;
  }
  
}