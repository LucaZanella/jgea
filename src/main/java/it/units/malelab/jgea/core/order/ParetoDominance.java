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

package it.units.malelab.jgea.core.order;

import java.util.List;

/**
 * @author eric
 * @created 2020/06/17
 * @project jgea
 */
public class ParetoDominance<C extends Comparable<C>> implements PartialComparator<List<C>> {

  @Override
  public PartialComparatorOutcome compare(List<C> k1, List<C> k2) {
    if (k1.size() != k2.size()) {
      return PartialComparatorOutcome.NOT_COMPARABLE;
    }
    int afterCount = 0;
    int beforeCount = 0;
    for (int i = 0; i < k1.size(); i++) {
      C o1 = k1.get(i);
      C o2 = k2.get(i);
      int outcome = o1.compareTo(o2);
      if (outcome < 0) {
        beforeCount = beforeCount + 1;
      } else if (outcome > 0) {
        afterCount = afterCount + 1;
      }
    }
    if ((beforeCount > 0) && (afterCount == 0)) {
      return PartialComparatorOutcome.BEFORE;
    }
    if ((beforeCount == 0) && (afterCount > 0)) {
      return PartialComparatorOutcome.AFTER;
    }
    if ((beforeCount == 0) && (afterCount == 0)) {
      return PartialComparatorOutcome.SAME;
    }
    return PartialComparatorOutcome.NOT_COMPARABLE;
  }
}
