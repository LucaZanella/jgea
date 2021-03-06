/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.units.malelab.jgea.core.function;

import it.units.malelab.jgea.core.listener.Listener;

/**
 *
 * @author eric
 */
public class CachedFunction<A, B> extends CachedNonDeterministicFunction<A, B> implements Function<A, B> {
  
  public CachedFunction(Function<A, B> innerFunction, long cacheSize) {
    super(innerFunction, cacheSize);
  }

  @Override
  public B apply(A a, Listener listener) throws FunctionException {
    return super.apply(a, null, listener);
  }

}
