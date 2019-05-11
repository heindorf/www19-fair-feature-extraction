/*
 * MIT License
 *
 * Copyright (c) 2019 Stefan Heindorf, Yan Scholten, Gregor Engels, Martin Potthast
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public final class Node {

  private ArrayList<Edge> outgoing;

  private HashMap<Integer, Integer> predicatesInCounter;
  private HashMap<Integer, Integer> predicatesOutCounter;

  public Node() {
    outgoing = new ArrayList<>();

    predicatesInCounter = new HashMap<>();
    predicatesOutCounter = new HashMap<>();
  }

  public void increaseInCounter(final int predicate) {
    predicatesInCounter.merge(predicate, 1, Integer::sum);
  }

  public void increaseOutCounter(final int predicate) {
    predicatesOutCounter.merge(predicate, 1, Integer::sum);
  }

  public int getInCountFor(final int predicate) {
    Integer count = predicatesInCounter.get(predicate);

    if (count == null) {
      return -1;
    } else {
      return count;
    }
  }

  public int getOutCountFor(final int predicate) {
    Integer counter = predicatesOutCounter.get(predicate);

    if (counter == null) {
      return -1;
    } else {
      return counter;
    }
  }

  public void addOutgoingEdge(final Edge edge) {
    outgoing.add(edge);
  }

  public ArrayList<Edge> getOutgoingEdges() {
    return outgoing;
  }

  public Set<Integer> getPredicatesIn() {
    return predicatesInCounter.keySet();
  }

  public Set<Integer> getPredicatesOut() {
    return predicatesOutCounter.keySet();
  }
}
