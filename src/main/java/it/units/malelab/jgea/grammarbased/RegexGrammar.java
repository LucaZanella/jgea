/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.units.malelab.jgea.grammarbased;

import it.units.malelab.jgea.core.util.Pair;
import it.units.malelab.jgea.grammarbased.Grammar;
import it.units.malelab.jgea.problem.classification.RegexClassification;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 *
 * @author eric
 */
public class RegexGrammar extends Grammar<String> {

  public static enum Option {

    OR, QUANTIFIERS, BOUNDED_QUANTIFIERS, CHAR_CLASS, NEGATED_CHAR_CLASS, NON_CAPTURING_GROUP, ANY, ENHANCED_CONCATENATION
  };

  public static final String TO_BE_ESCAPED = "{}[]()?+*.\\^";
  
  public RegexGrammar(Collection<String> texts, Set<Option> options) {
    this(texts.stream()
            .reduce((s1, s2) -> s1+s2)
            .get()
            .chars()
            .mapToObj(c -> (char) c)
            .filter(c -> (c.toString().matches("[\\Wa-zA-Z0-9]")))
            .collect(Collectors.toCollection(TreeSet::new)), options);
  }

  public RegexGrammar(Set<Character> alphabet, Set<Option> options) {
    super();
    getRules().put("<regex>", l(l("<concat>")));
    if (options.contains(Option.OR)) {
      getRules().get("<regex>").add(l("<union>"));
      getRules().put("<union>", l(l("<regex>", "|", "<concat>")));
    }
    getRules().put("<concat>", l(l("<term>", "<concat>"), l("<term>")));
    if (options.contains(Option.ENHANCED_CONCATENATION)) {
      getRules().get("<concat>").add(l("<concat>", "<concat>"));
    }
    getRules().put("<term>", l(l("<element>")));
    if (options.contains(Option.QUANTIFIERS)) {
      getRules().get("<term>").add(l("<element>", "<quantifier>"));
      getRules().put("<quantifier>", l(l("?+"), l("++"), l("*+")));
    }
    if (options.contains(Option.BOUNDED_QUANTIFIERS)) {
      getRules().get("<term>").add(l("<element>", "{", "<digit>", ",", "<digit>", "}"));
      getRules().put("<digit>", l(l("1"), l("2"), l("3"), l("4"), l("5"), l("6"), l("7"), l("8"), l("9")));
    }
    getRules().put("<element>", l(l("<char>")));
    if (options.contains(Option.CHAR_CLASS)) {
      getRules().get("<element>").add(l("[", "<constChars>", "]"));
      getRules().put("<constChars>", l(l("<constChar>"), l("<constChars>", "<constChar>")));
      if (options.contains(Option.ENHANCED_CONCATENATION)) {
        getRules().get("<constChars>").add(l("<constChars>", "<constChars>"));
      }
    }
    if (options.contains(Option.NEGATED_CHAR_CLASS)) {
      getRules().get("<element>").add(l("[^", "<constChars>", "]"));
      getRules().put("<constChars>", l(l("<constChar>"), l("<constChars>", "<constChar>")));
    }
    if (options.contains(Option.NON_CAPTURING_GROUP)) {
      getRules().get("<element>").add(l("(?:", "<regex>", ")"));
    }
    getRules().put("<char>", l(l("<constChar>")));
    if (options.contains(Option.ANY)) {
      getRules().get("<char>").add(l("."));
    }
    getRules().put("<constChar>", new ArrayList<>());
    for (Character character : alphabet) {
      getRules().get("<constChar>").add(l(escape(character.toString())));
    }
    setStartingSymbol("<regex>");
  }

  private String escape(String c) {
    if (TO_BE_ESCAPED.indexOf(c) >= 0) {
      return "\\" + c;
    }
    return c;
  }

  private <T> List<T> l(T... ts) {
    return new ArrayList<>(Arrays.asList(ts));
  }

}
