/*
 * Copyright (C) 2020 Eric Medvet <eric.medvet@gmail.com> (as eric)
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package it.units.malelab.jgea.representation.grammar.cfggp;

import it.units.malelab.jgea.representation.tree.Node;
import it.units.malelab.jgea.representation.grammar.Grammar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import it.units.malelab.jgea.core.operator.Mutation;

/**
 * @author eric
 */
public class StandardTreeMutation<T> implements Mutation<Node<T>> {

  private final int maxDepth;
  private GrowTreeFactory<T> factory;

  public StandardTreeMutation(int maxDepth, Grammar<T> grammar) {
    this.maxDepth = maxDepth;
    factory = new GrowTreeFactory<>(0, grammar);
  }

  @Override
  public Node<T> mutate(Node<T> parent, Random random) {
    Node<T> child = (Node<T>) parent.clone();
    List<Node<T>> nonTerminalNodes = new ArrayList<>();
    getNonTerminalNodes(child, nonTerminalNodes);
    Collections.shuffle(nonTerminalNodes, random);
    boolean done = false;
    for (Node<T> toReplaceSubTree : nonTerminalNodes) {
      Node<T> newSubTree = factory.build(random, toReplaceSubTree.getContent(), toReplaceSubTree.height());
      if (newSubTree != null) {
        toReplaceSubTree.getChildren().clear();
        toReplaceSubTree.getChildren().addAll(newSubTree.getChildren());
        done = true;
        break;
      }
    }
    if (!done) {
      return null;
    }
    return child;
  }

  private void getNonTerminalNodes(Node<T> node, List<Node<T>> nodes) {
    if (!node.getChildren().isEmpty()) {
      nodes.add(node);
      for (Node<T> child : node.getChildren()) {
        getNonTerminalNodes(child, nodes);
      }
    }
  }

}
