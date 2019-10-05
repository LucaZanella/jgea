/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.units.malelab.jgea.core.genotype;

import it.units.malelab.jgea.core.Sequence;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Eric Medvet <eric.medvet@gmail.com>
 */
public class FixedLengthSequence<T> implements Sequence<T> {
  
  private final List<T> values;

  public FixedLengthSequence(int n, T value) {
    if (n<1) {
      throw new IllegalArgumentException("Length must be >0");
    }
    values = new ArrayList<>(n);
    for (int i = 0; i<n; i++) {
      values.add(value);
    }
  }

  @Override
  public T get(int index) {
    return values.get(index);
  }

  @Override
  public void set(int index, T t) {
    values.set(index, t);
  }

  @Override
  public Sequence<T> clone() {
    FixedLengthSequence<T> cloned = new FixedLengthSequence<>(size(), get(0));
    for (int i = 1; i<size(); i++) {
      cloned.set(i, cloned.get(i));
    }
    return cloned;
  }

  @Override
  public int size() {
    return values.size();
  }
  
}