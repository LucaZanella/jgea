/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.units.malelab.jgea.core.function;

/**
 *
 * @author eric
 */
public class CachedBoundedNonDeterministicFunction<A, B> extends CachedNonDeterministicFunction<A, B> implements Bounded<B> {
  
  private final Bounded<B> boundedInnerFunction;

  public CachedBoundedNonDeterministicFunction(NonDeterministicFunction<A, B> innerFunction, long cacheSize) {
    super(innerFunction, cacheSize);
    boundedInnerFunction = (Bounded<B>)innerFunction;
  }

  @Override
  public B bestValue() {
    return boundedInnerFunction.bestValue();
  }

  @Override
  public B worstValue() {
    return boundedInnerFunction.worstValue();
  }
    
    
}
