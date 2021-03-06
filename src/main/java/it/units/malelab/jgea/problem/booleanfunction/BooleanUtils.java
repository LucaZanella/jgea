/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.units.malelab.jgea.problem.booleanfunction;

import it.units.malelab.jgea.core.Node;
import it.units.malelab.jgea.problem.booleanfunction.element.Constant;
import it.units.malelab.jgea.problem.booleanfunction.element.Decoration;
import it.units.malelab.jgea.problem.booleanfunction.element.Element;
import it.units.malelab.jgea.problem.booleanfunction.element.Operator;
import it.units.malelab.jgea.problem.booleanfunction.element.Variable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author eric
 */
public class BooleanUtils {

  public static boolean[] compute(List<Node<Element>> formulas, Map<String, Boolean> values) {
    boolean[] result = new boolean[formulas.size()];
    for (int i = 0; i < result.length; i++) {
      result[i] = compute(formulas.get(i), values);
    }
    return result;
  }

  public static Boolean compute(Node<Element> node, Map<String, Boolean> values) {
    if (node.getContent() instanceof Decoration) {
      return null;
    }
    if (node.getContent() instanceof Variable) {
      Boolean result = values.get(node.getContent().toString());
      if (result == null) {
        throw new RuntimeException(String.format("Undefined variable: %s", node.getContent().toString()));
      }
      return result;
    }
    if (node.getContent() instanceof Constant) {
      return ((Constant) node.getContent()).getValue();
    }
    boolean[] childrenValues = new boolean[node.getChildren().size()];
    int i = 0;
    for (Node<Element> child : node.getChildren()) {
      Boolean childValue = compute(child, values);
      if (childValue != null) {
        childrenValues[i] = childValue;
        i = i + 1;
      }
    }
    return compute((Operator) node.getContent(), childrenValues);
  }

  private static boolean compute(Operator operator, boolean... operands) {
    switch (operator) {
      case AND:
        return operands[0] && operands[1];
      case AND1NOT:
        return (!operands[0]) && operands[1];
      case OR:
        return operands[0] || operands[1];
      case XOR:
        return operands[0] ^ operands[1];
      case NOT:
        return !operands[0];
      case IF:
        return operands[0] ? operands[1] : operands[2];
    }
    return false;
  }

  public static Map<String, boolean[]> buildCompleteCases(String... names) {
    Map<String, boolean[]> map = new LinkedHashMap<>();
    for (String name : names) {
      map.put(name, new boolean[(int) Math.pow(2, names.length)]);
    }
    for (int i = 0; i < Math.pow(2, names.length); i++) {
      for (int j = 0; j < names.length; j++) {
        map.get(names[j])[i] = (i & (int) Math.pow(2, j)) > 0;
      }
    }
    return map;
  }

  public static List<boolean[]> buildCompleteObservations(String... names) {
    Map<String, boolean[]> cases = buildCompleteCases(names);
    List<boolean[]> observations = new ArrayList<>();
    for (int i = 0; i < cases.get(names[0]).length; i++) {
      boolean[] observation = new boolean[names.length];
      for (int j = 0; j < names.length; j++) {
        observation[j] = cases.get(names[j])[i];
      }
      observations.add(observation);
    }
    return observations;
  }

  public static int fromBinary(boolean[] bits) {
    int n = 0;
    for (int i = bits.length - 1; i >= 0; i--) {
      n = (n << 1) | (bits[i] ? 1 : 0);
    }
    return n;
  }

  public static boolean[] toBinary(int input, int size) {
    boolean[] bits = new boolean[size];
    for (int i = size - 1; i >= 0; i--) {
      bits[i] = (input & (1 << i)) != 0;
    }
    return bits;
  }

}
