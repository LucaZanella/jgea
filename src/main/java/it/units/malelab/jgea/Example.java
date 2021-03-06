/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.units.malelab.jgea;

import com.google.common.collect.Lists;
import it.units.malelab.jgea.core.Individual;
import it.units.malelab.jgea.core.Node;
import it.units.malelab.jgea.core.Problem;
import it.units.malelab.jgea.core.ProblemWithValidation;
import it.units.malelab.jgea.core.Sequence;
import it.units.malelab.jgea.core.evolver.DeterministicCrowdingEvolver;
import it.units.malelab.jgea.core.evolver.DifferentialEvolution;
import it.units.malelab.jgea.core.evolver.FitnessSharingDivideAndConquerEvolver;
import it.units.malelab.jgea.core.evolver.MutationOnly;
import it.units.malelab.jgea.core.evolver.StandardEvolver;
import it.units.malelab.jgea.core.evolver.StandardWithEnforcedDiversity;
import it.units.malelab.jgea.core.evolver.biased.BiasedGenerator;
import it.units.malelab.jgea.core.evolver.biased.Filler;
import it.units.malelab.jgea.core.evolver.biased.Percentile;
import it.units.malelab.jgea.core.evolver.biased.PercentileProportional;
import it.units.malelab.jgea.core.evolver.stopcondition.ElapsedTime;
import it.units.malelab.jgea.core.evolver.stopcondition.FitnessEvaluations;
import it.units.malelab.jgea.core.evolver.stopcondition.PerfectFitness;
import it.units.malelab.jgea.core.fitness.ClassificationFitness;
import it.units.malelab.jgea.core.function.Function;
import it.units.malelab.jgea.core.function.Reducer;
import it.units.malelab.jgea.core.genotype.BitString;
import it.units.malelab.jgea.core.genotype.BitStringFactory;
import it.units.malelab.jgea.core.listener.Listener;
import it.units.malelab.jgea.core.listener.collector.DoubleArrayPrinter;
import it.units.malelab.jgea.core.listener.collector.Basic;
import it.units.malelab.jgea.core.listener.collector.BestInfo;
import it.units.malelab.jgea.core.listener.collector.BestPrinter;
import it.units.malelab.jgea.core.listener.collector.FunctionOfBest;
import it.units.malelab.jgea.core.listener.collector.Diversity;
import it.units.malelab.jgea.core.listener.collector.FirstOfNthObjective;
import it.units.malelab.jgea.core.listener.collector.FirstRankIndividualInfo;
import it.units.malelab.jgea.core.listener.collector.FunctionOfEvent;
import it.units.malelab.jgea.core.listener.collector.IndividualBasicInfo;
import it.units.malelab.jgea.core.listener.collector.IntrinsicDimension;
import it.units.malelab.jgea.core.listener.collector.LowestNormalizedSum;
import it.units.malelab.jgea.core.listener.collector.ObjectiveMostCentral;
import it.units.malelab.jgea.core.listener.collector.Population;
import it.units.malelab.jgea.core.operator.BitFlipMutation;
import it.units.malelab.jgea.core.operator.GeneticOperator;
import it.units.malelab.jgea.core.operator.LenghtPreservingTwoPointCrossover;
import it.units.malelab.jgea.core.ranker.ComparableRanker;
import it.units.malelab.jgea.core.ranker.FitnessComparator;
import it.units.malelab.jgea.core.ranker.ParetoRanker;
import it.units.malelab.jgea.core.ranker.selector.Tournament;
import it.units.malelab.jgea.core.ranker.selector.Worst;
import it.units.malelab.jgea.core.util.Pair;
import it.units.malelab.jgea.distance.BitStringHamming;
import it.units.malelab.jgea.distance.Distance;
import it.units.malelab.jgea.distance.Edit;
import it.units.malelab.jgea.distance.Pairwise;
import it.units.malelab.jgea.distance.TreeLeaves;
import it.units.malelab.jgea.grammarbased.GrammarBasedMapper;
import it.units.malelab.jgea.grammarbased.GrammarBasedProblem;
import it.units.malelab.jgea.grammarbased.cfggp.RampedHalfAndHalf;
import it.units.malelab.jgea.grammarbased.cfggp.StandardTreeCrossover;
import it.units.malelab.jgea.grammarbased.cfggp.StandardTreeMutation;
import it.units.malelab.jgea.problem.booleanfunction.EvenParity;
import it.units.malelab.jgea.problem.booleanfunction.element.Element;
import it.units.malelab.jgea.problem.classification.BinaryRegexClassification;
import it.units.malelab.jgea.problem.classification.RegexClassification;
import it.units.malelab.jgea.problem.extraction.AbstractExtractionProblem;
import it.units.malelab.jgea.problem.extraction.BinaryRegexExtraction;
import it.units.malelab.jgea.problem.extraction.ExtractionFitness;
import it.units.malelab.jgea.grammarbased.RegexGrammar;
import it.units.malelab.jgea.grammarbased.ge.StandardGEMapper;
import it.units.malelab.jgea.grammarbased.ge.WeightedHierarchicalMapper;
import it.units.malelab.jgea.problem.surrogate.ControlledPrecisionProblem;
import it.units.malelab.jgea.problem.symbolicregression.AbstractSymbolicRegressionProblem;
import it.units.malelab.jgea.problem.symbolicregression.Pagie1;
import it.units.malelab.jgea.problem.synthetic.LinearPoints;
import it.units.malelab.jgea.problem.synthetic.Text;
import it.units.malelab.jgea.problem.synthetic.TreeSize;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author eric
 */
public class Example extends Worker {

  public Example(String[] args) throws FileNotFoundException {
    super(args);
  }

  public final static void main(String[] args) throws FileNotFoundException {
    new Example(args);
  }

  public void run() {
    try {
      //tunablePagie1CFGGP(executorService);
      //linearPointsDE(executorService);
      //treeSizeBiasedGenerator(executorService);
      //textBiasedGenerator(executorService);
      //parityGE(executorService, "whge");
      parity(executorService);
      parityEnforcedDiversity(executorService);
      //parityGE(executorService, "ge");
      //parityGE(executorService, "whge");
      //parityDCGE(executorService, "whge");
      //binaryRegexStandard(executorService);
      //binaryRegexDC(executorService);
      //binaryRegexFSDC(executorService);
      //binaryRegexExtractionStandard(executorService);
    } catch (IOException | InterruptedException | ExecutionException ex) {
      Logger.getLogger(Example.class.getName()).log(Level.SEVERE, null, ex);
      ex.printStackTrace();
    }
  }

  private void tunablePagie1CFGGP(ExecutorService executor) throws IOException, InterruptedException, ExecutionException {
    //GrammarBasedProblem<String, Node<Element>, Double> p = new Pagie1();
    AbstractSymbolicRegressionProblem op = new Pagie1();
    ControlledPrecisionProblem p = new ControlledPrecisionProblem<>(
            op,
            new SurrogatePrecisionControl.HistoricAvgDistanceRatio<>(
                    0.5d, 5,
                    new TreeLeaves<>(new Edit<it.units.malelab.jgea.problem.symbolicregression.element.Element>()),
                    100
            )
    );
    MutationOnly<Node<String>, Node<it.units.malelab.jgea.problem.symbolicregression.element.Element>, Double> ea = new MutationOnly<>(
            500,
            new RampedHalfAndHalf(3, 12, op.getGrammar()),
            new ComparableRanker(new FitnessComparator(Function.identity())),
            op.getSolutionMapper(),
            new StandardTreeMutation(12, op.getGrammar()),
            Lists.newArrayList(new FitnessEvaluations(20000)),
            10000,
            false
    );
    Random random = new Random(1);
    ea.solve(p, random, executor, Listener.onExecutor(listener(
            new Basic(),
            new Population(),
            new BestInfo<>("%5.3f"),
            new FunctionOfBest<>("actual.fitness", (Function) p.getInnerProblem().getFitnessFunction(), "%5.3f"),
            new FunctionOfEvent("history.avg.precision", (e, l) -> p.getController().getHistory().stream().mapToDouble((o) -> ((Double) ((Pair) o).second()).doubleValue()).average().orElse(Double.NaN), "%5.3f")
    ), executor));
  }

  private void linearPointsDE(ExecutorService executor) throws IOException, InterruptedException, ExecutionException {
    Problem<double[], Double> problem = new LinearPoints();
    DifferentialEvolution<Double> de = new DifferentialEvolution<>(
            100, 4,
            0.8, 0.5,
            16,
            5d, 10d,
            new ComparableRanker(new FitnessComparator(Function.identity())),
            Lists.newArrayList(new FitnessEvaluations(5000)), 10000);
    Random random = new Random(1);
    de.solve(problem, random, executor, Listener.onExecutor(listener(new Basic(),
            new Population(),
            new BestInfo<>((Function) problem.getFitnessFunction(), "%5.3f"),
            new BestPrinter(new DoubleArrayPrinter("%+3.1f"), "%s")
    ), executor));
  }

  private void treeSizeBiasedGenerator(ExecutorService executor) throws IOException, InterruptedException, ExecutionException {
    GrammarBasedProblem<Boolean, Node<Boolean>, Double> p = new TreeSize(2, 1);
    System.out.println(p.getGrammar());
    BiasedGenerator<Boolean, Node<Boolean>, Double> bg = new BiasedGenerator<>(
            new Filler<>(10, new Percentile<>(0.2f)),
            //new Uniform<>(),
            0, 0, 100, 1, 10,
            Lists.newArrayList(new FitnessEvaluations(10000)),
            10000);
    Random random = new Random(1);
    bg.solve(p, random, executor, Listener.onExecutor(listener(
            new Basic(),
            new Population(),
            new BestInfo<>("%8.6f")
    ), executor));
  }

  private void textBiasedGenerator(ExecutorService executor) throws IOException, InterruptedException, ExecutionException {
    GrammarBasedProblem<String, String, Double> p = new Text("Hello World!");
    System.out.println(p.getGrammar());
    BiasedGenerator<String, String, Double> bg = new BiasedGenerator<>(
            //new Filler<>(10, new Percentile<>(0.1f)),
            new Filler<>(10, new PercentileProportional(0.01f)),
            //new Uniform<>(),
            4, 4, 100, 1, 20,
            Lists.newArrayList(new FitnessEvaluations(10000)),
            10000);
    Random random = new Random(1);
    bg.solve(p, random, executor, Listener.onExecutor(listener(
            new Basic(),
            new Population(),
            new BestInfo<>("%8.6f"),
            new BestPrinter(null, "%s")
    ), executor));
  }

  private void parity(ExecutorService executor) throws IOException, InterruptedException, ExecutionException {
    final GrammarBasedProblem<String, List<Node<Element>>, Double> p = new EvenParity(8);
    Map<GeneticOperator<Node<String>>, Double> operators = new LinkedHashMap<>();
    operators.put(new StandardTreeMutation<>(12, p.getGrammar()), 0.2d);
    operators.put(new StandardTreeCrossover<>(12), 0.8d);
    StandardEvolver<Node<String>, List<Node<Element>>, Double> evolver = new StandardEvolver<>(
            100,
            new RampedHalfAndHalf<>(3, 12, p.getGrammar()),
            new ComparableRanker(new FitnessComparator<>(Function.identity())),
            p.getSolutionMapper(),
            operators,
            new Tournament<>(3),
            new Worst<>(),
            500,
            true,
            Lists.newArrayList(new FitnessEvaluations(10000), new PerfectFitness<>(p.getFitnessFunction())),
            10000,
            false
    );
    Random r = new Random(1);
    evolver.solve(p, r, executor,
            Listener.onExecutor(listener(
                    new Basic(),
                    new Population(),
                    new BestInfo<>("%6.4f"),
                    new Diversity(),
                    new BestPrinter("%s")
            ), executor)
    );
  }

  private void parityEnforcedDiversity(ExecutorService executor) throws IOException, InterruptedException, ExecutionException {
    final GrammarBasedProblem<String, List<Node<Element>>, Double> p = new EvenParity(8);
    Map<GeneticOperator<Node<String>>, Double> operators = new LinkedHashMap<>();
    operators.put(new StandardTreeMutation<>(12, p.getGrammar()), 0.2d);
    operators.put(new StandardTreeCrossover<>(12), 0.8d);
    StandardWithEnforcedDiversity<Node<String>, List<Node<Element>>, Double> evolver = new StandardWithEnforcedDiversity<>(
            100,
            100,
            new RampedHalfAndHalf<>(3, 12, p.getGrammar()),
            new ComparableRanker(new FitnessComparator<>(Function.identity())),
            p.getSolutionMapper(),
            operators,
            new Tournament<>(3),
            new Worst<>(),
            100,
            true,
            Lists.newArrayList(new FitnessEvaluations(10000), new PerfectFitness<>(p.getFitnessFunction())),
            10000
    );
    Random r = new Random(1);
    evolver.solve(p, r, executor,
            Listener.onExecutor(listener(
                    new Basic(),
                    new Population(),
                    new BestInfo<>("%6.4f"),
                    new Diversity(),
                    new BestPrinter("%s")
            ), executor)
    );
  }

  private void parityGE(ExecutorService executor, String mapperName) throws IOException, InterruptedException, ExecutionException {
    final GrammarBasedProblem<String, List<Node<Element>>, Double> p = new EvenParity(5);
    GrammarBasedMapper<BitString, String> mapper;
    if (mapperName.equals("ge")) {
      mapper = new StandardGEMapper<>(8, 1, p.getGrammar());
    } else if (mapperName.equals("whge")) {
      mapper = new WeightedHierarchicalMapper<>(2, p.getGrammar());
    } else {
      return;
    }
    Map<GeneticOperator<BitString>, Double> operators = new LinkedHashMap<>();
    operators.put(new BitFlipMutation(0.01d), 0.2d);
    operators.put(new LenghtPreservingTwoPointCrossover(), 0.8d);
    StandardEvolver<BitString, List<Node<Element>>, Double> evolver = new StandardEvolver<>(
            500,
            new BitStringFactory(256),
            new ComparableRanker(new FitnessComparator<>(Function.identity())),
            mapper.andThen(p.getSolutionMapper()),
            operators,
            new Tournament<>(3),
            new Worst<>(),
            500,
            true,
            Lists.newArrayList(new FitnessEvaluations(100000), new PerfectFitness<>(p.getFitnessFunction())),
            10000,
            false
    );
    Random r = new Random(1);
    Distance<BitString> hamming = (new BitStringHamming()).cached(10000);
    evolver.solve(p, r, executor,
            Listener.onExecutor(listener(
                    new Basic(),
                    new Population(),
                    new BestInfo<>("%6.4f"),
                    new Diversity(),
                    new IntrinsicDimension(hamming, false),
                    new IntrinsicDimension(hamming, true),
                    new BestPrinter(null, "%s")
            ), executor)
    );
  }

  private void parityDCGE(ExecutorService executor, String mapperName) throws IOException, InterruptedException, ExecutionException {
    final GrammarBasedProblem<String, List<Node<Element>>, Double> p = new EvenParity(5);
    GrammarBasedMapper<BitString, String> mapper;
    if (mapperName.equals("ge")) {
      mapper = new StandardGEMapper<>(8, 1, p.getGrammar());
    } else if (mapperName.equals("whge")) {
      mapper = new WeightedHierarchicalMapper<>(2, p.getGrammar());
    } else {
      return;
    }
    Map<GeneticOperator<BitString>, Double> operators = new LinkedHashMap<>();
    operators.put(new BitFlipMutation(0.01d), 0.2d);
    operators.put(new LenghtPreservingTwoPointCrossover(), 0.8d);
    Distance<List<Node<Element>>> distance = new Pairwise<>(new TreeLeaves<>(new Edit<>()));
    DeterministicCrowdingEvolver<BitString, List<Node<Element>>, Double> evolver = new DeterministicCrowdingEvolver<>(
            null, //TODO put a distance
            500,
            new BitStringFactory(128),
            new ComparableRanker(new FitnessComparator<>(Function.identity())),
            mapper.andThen(p.getSolutionMapper()),
            operators,
            new Tournament<>(3),
            new Worst<>(),
            Lists.newArrayList(new FitnessEvaluations(100000), new PerfectFitness<>(p.getFitnessFunction())),
            10000,
            false
    );
    Random r = new Random(1);
    evolver.solve(p, r, executor,
            Listener.onExecutor(listener(
                    new Basic(),
                    new Population(),
                    new BestInfo<>("%6.4f"),
                    new Diversity(),
                    new BestPrinter(null, "%s")
            ), executor)
    );
  }

  private void binaryRegexStandard(ExecutorService executor) throws IOException, InterruptedException, ExecutionException {
    GrammarBasedProblem<String, String, List<Double>> p = new BinaryRegexClassification(
            50, 100, 1,
            5, 0,
            ClassificationFitness.Metric.CLASS_ERROR_RATE, ClassificationFitness.Metric.CLASS_ERROR_RATE,
            RegexGrammar.Option.ANY, RegexGrammar.Option.ENHANCED_CONCATENATION, RegexGrammar.Option.OR
    );
    Map<GeneticOperator<Node<String>>, Double> operators = new LinkedHashMap<>();
    operators.put(new StandardTreeMutation<>(15, p.getGrammar()), 0.2d);
    operators.put(new StandardTreeCrossover<>(15), 0.8d);
    StandardEvolver<Node<String>, String, List<Double>> evolver = new StandardEvolver<>(
            100,
            new RampedHalfAndHalf<>(3, 15, p.getGrammar()),
            new ParetoRanker<>(false),
            p.getSolutionMapper(),
            operators,
            new Tournament<>(3),
            new Worst(),
            100,
            true,
            Lists.newArrayList(new ElapsedTime(90, TimeUnit.SECONDS), new PerfectFitness<>(p.getFitnessFunction())),
            10000,
            false
    );
    Random r = new Random(1);
    Function learningAssessmentFunction = ((ClassificationFitness) p.getFitnessFunction()).changeMetric(ClassificationFitness.Metric.CLASS_ERROR_RATE);
    Function validationAssessmentFunction = ((ClassificationFitness) ((ProblemWithValidation) p).getFitnessFunction()).changeMetric(ClassificationFitness.Metric.CLASS_ERROR_RATE);
    evolver.solve(p, r, executor,
            Listener.onExecutor(listener(new Basic(),
                    new Population(),
                    new BestInfo<>((ClassificationFitness) p.getFitnessFunction(), "%5.3f"),
                    new FirstRankIndividualInfo("lowest.first", new FirstOfNthObjective<>(0), new IndividualBasicInfo<>((ClassificationFitness) p.getFitnessFunction(), "%5.3f")),
                    new FirstRankIndividualInfo("lowest.second", new FirstOfNthObjective<>(1), new IndividualBasicInfo<>((ClassificationFitness) p.getFitnessFunction(), "%5.3f")),
                    new FirstRankIndividualInfo("central", new ObjectiveMostCentral<>(), new IndividualBasicInfo<>((ClassificationFitness) p.getFitnessFunction(), "%5.3f")),
                    new FirstRankIndividualInfo("lowest.sum", new LowestNormalizedSum<>(), new IndividualBasicInfo<>((ClassificationFitness) p.getFitnessFunction(), "%5.3f")),
                    new FunctionOfBest("best.learning", learningAssessmentFunction.cached(10000), "%5.3f"),
                    new FunctionOfBest("best.validation", validationAssessmentFunction.cached(10000), "%5.3f"),
                    new Diversity(),
                    new BestPrinter()
            ), executor
    )
  

  

  );
  }

  private void binaryRegexDC(ExecutorService executor) throws IOException, InterruptedException, ExecutionException {
    GrammarBasedProblem<String, String, List<Double>> p = new BinaryRegexClassification(
            50, 100, 1,
            5, 0,
            ClassificationFitness.Metric.BALANCED_ERROR_RATE, ClassificationFitness.Metric.CLASS_ERROR_RATE,
            RegexGrammar.Option.ANY, RegexGrammar.Option.ENHANCED_CONCATENATION, RegexGrammar.Option.OR
    );
    Map<GeneticOperator<Node<String>>, Double> operators = new LinkedHashMap<>();
    operators.put(new StandardTreeMutation<>(15, p.getGrammar()), 0.2d);
    operators.put(new StandardTreeCrossover<>(15), 0.8d);
    Distance<Sequence<Character>> edit = new Edit<>();
    Distance<String> editString = (s1, s2, l) -> (edit.apply(
            Sequence.from(s1.chars().mapToObj(c -> (char) c).toArray(Character[]::new)),
            Sequence.from(s2.chars().mapToObj(c -> (char) c).toArray(Character[]::new))
    ));
    Distance<Individual<Node<String>, String, List<Double>>> distance = (Individual<Node<String>, String, List<Double>> i1, Individual<Node<String>, String, List<Double>> i2, Listener l) -> (editString.apply(
            i1.getSolution(),
            i2.getSolution()
    ));
    StandardEvolver<Node<String>, String, List<Double>> evolver = new DeterministicCrowdingEvolver<>(
            distance.cached(10000),
            500,
            new RampedHalfAndHalf<>(3, 15, p.getGrammar()),
            new ParetoRanker<>(false),
            p.getSolutionMapper(),
            operators,
            new Tournament<>(3),
            new Worst(),
            Lists.newArrayList(new ElapsedTime(90, TimeUnit.SECONDS), new PerfectFitness<>(p.getFitnessFunction())),
            10000,
            false
    );
    Random r = new Random(1);
    Function learningAssessmentFunction = ((ClassificationFitness) p.getFitnessFunction()).changeMetric(ClassificationFitness.Metric.CLASS_ERROR_RATE);
    Function validationAssessmentFunction = ((ClassificationFitness) ((ProblemWithValidation) p).getFitnessFunction()).changeMetric(ClassificationFitness.Metric.CLASS_ERROR_RATE);
    evolver.solve(p, r, executor,
            Listener.onExecutor(listener(new Basic(),
                    new Population(),
                    new BestInfo<>((ExtractionFitness) p.getFitnessFunction(), "%5.3f"),
                    new FunctionOfBest("best.learning", learningAssessmentFunction.cached(10000), "%5.3f"),
                    new FunctionOfBest("best.validation", validationAssessmentFunction.cached(10000), "%5.3f"),
                    new Diversity(),
                    new BestPrinter()
            ), executor
            )
    );
  }

  private void binaryRegexFSDC(ExecutorService executor) throws IOException, InterruptedException, ExecutionException {
    GrammarBasedProblem<String, String, List<Double>> p = new BinaryRegexClassification(
            100, 200, 1,
            5, 0,
            ClassificationFitness.Metric.BALANCED_ERROR_RATE, ClassificationFitness.Metric.CLASS_ERROR_RATE,
            RegexGrammar.Option.ANY, RegexGrammar.Option.ENHANCED_CONCATENATION
    );
    Map<GeneticOperator<Node<String>>, Double> operators = new LinkedHashMap<>();
    operators.put(new StandardTreeMutation<>(15, p.getGrammar()), 0.2d);
    operators.put(new StandardTreeCrossover<>(15), 0.8d);
    Distance<List<RegexClassification.Label>> semanticsDistance = (l1, l2, listener) -> {
      double count = 0;
      for (int i = 0; i < Math.min(l1.size(), l2.size()); i++) {
        if (!l1.get(i).equals(l2.get(i))) {
          count = count + 1d;
        }
      }
      return count / (double) Math.min(l1.size(), l2.size());
    };
    Reducer<Pair<String, List<RegexClassification.Label>>> reducer = (p0, p1, listener) -> {
      String s = p0.first() + "|" + p1.first();
      List<RegexClassification.Label> ored = new ArrayList<>(Math.min(p0.second().size(), p1.second().size()));
      for (int i = 0; i < Math.min(p0.second().size(), p1.second().size()); i++) {
        if (p0.second().get(i).equals(RegexClassification.Label.FOUND) || p1.second().get(i).equals(RegexClassification.Label.FOUND)) {
          ored.add(RegexClassification.Label.FOUND);
        } else {
          ored.add(RegexClassification.Label.NOT_FOUND);
        }
      }
      return Pair.build(s, ored);
    };
    StandardEvolver<Node<String>, String, List<Double>> evolver = new FitnessSharingDivideAndConquerEvolver<>(
            reducer,
            semanticsDistance,
            500,
            new RampedHalfAndHalf<>(3, 15, p.getGrammar()),
            new ParetoRanker<>(false),
            p.getSolutionMapper(),
            operators,
            new Tournament<>(3),
            new Worst(),
            500,
            true,
            Lists.newArrayList(new ElapsedTime(90, TimeUnit.SECONDS), new PerfectFitness<>(p.getFitnessFunction())),
            10000
    );
    Random r = new Random(1);
    Function learningAssessmentFunction = ((ClassificationFitness) p.getFitnessFunction()).changeMetric(ClassificationFitness.Metric.CLASS_ERROR_RATE);
    Function validationAssessmentFunction = ((ClassificationFitness) ((ProblemWithValidation) p).getFitnessFunction()).changeMetric(ClassificationFitness.Metric.CLASS_ERROR_RATE);
    evolver.solve(p, r, executor,
            Listener.onExecutor(listener(new Basic(),
                    new Population(),
                    new BestInfo<>((ExtractionFitness) p.getFitnessFunction(), "%5.3f"),
                    new FunctionOfBest("best.learning", learningAssessmentFunction.cached(10000), "%5.3f"),
                    new FunctionOfBest("best.validation", validationAssessmentFunction.cached(10000), "%5.3f"),
                    new Diversity(),
                    new BestPrinter()
            ), executor
            )
    );
  }

  private void binaryRegexExtractionStandard(ExecutorService executor) throws IOException, InterruptedException, ExecutionException {
    GrammarBasedProblem<String, String, List<Double>> p = new BinaryRegexExtraction(
            10, 1,
            new HashSet<>(Arrays.asList(RegexGrammar.Option.ANY, RegexGrammar.Option.ENHANCED_CONCATENATION, RegexGrammar.Option.OR)),
            5, 0,
            ExtractionFitness.Metric.ONE_MINUS_FM);
    Map<GeneticOperator<Node<String>>, Double> operators = new LinkedHashMap<>();
    operators.put(new StandardTreeMutation<>(15, p.getGrammar()), 0.2d);
    operators.put(new StandardTreeCrossover<>(15), 0.8d);
    StandardEvolver<Node<String>, String, List<Double>> evolver = new StandardEvolver<>(
            500,
            new RampedHalfAndHalf<>(3, 15, p.getGrammar()),
            new ParetoRanker<>(false),
            p.getSolutionMapper(),
            operators,
            new Tournament<>(3),
            new Worst(),
            500,
            true,
            Lists.newArrayList(new ElapsedTime(30, TimeUnit.SECONDS), new PerfectFitness<>(p.getFitnessFunction())),
            10000,
            false
    );
    Random r = new Random(1);
    Function learningAssessmentFunction = ((ExtractionFitness) ((AbstractExtractionProblem) p).getFitnessFunction()).changeMetrics(ExtractionFitness.Metric.ONE_MINUS_FM, ExtractionFitness.Metric.ONE_MINUS_PREC, ExtractionFitness.Metric.ONE_MINUS_REC, ExtractionFitness.Metric.CHAR_ERROR);
    Function validationAssessmentFunction = ((ExtractionFitness) ((AbstractExtractionProblem) p).getValidationFunction()).changeMetrics(ExtractionFitness.Metric.ONE_MINUS_FM, ExtractionFitness.Metric.ONE_MINUS_PREC, ExtractionFitness.Metric.ONE_MINUS_REC, ExtractionFitness.Metric.CHAR_ERROR);
    evolver.solve(p, r, executor,
            Listener.onExecutor(listener(new Basic(),
                    new Population(),
                    new BestInfo<>((ExtractionFitness) p.getFitnessFunction(), "%5.3f"),
                    new FunctionOfBest("best.learning", learningAssessmentFunction.cached(10000), "%5.3f"),
                    new FunctionOfBest("best.validation", validationAssessmentFunction.cached(10000), "%5.3f"),
                    new Diversity(),
                    new BestPrinter()
            ), executor
            )
    );
  }

}
