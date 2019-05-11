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

import java.util.HashMap;
import java.util.Set;

public final class WikidataGraph {

  private HashMap<Integer, Node> nodes;

  public WikidataGraph(final String graphFile) {
    nodes = new HashMap<>();
    loadGraph(graphFile);
  }

  public Node getNodeById(final int node) {
    return nodes.get(node);    // O(1)
  }

  public Set<Integer> getIncomingEdgePredicates(final int nodeId) {
    Node node = nodes.get(nodeId); // O(1)

    if (node == null) {
      return null;
    }

    return node.getPredicatesIn();
  }

  public Set<Integer> getOutgoingEdgePredicates(final int nodeId) {
    Node node = nodes.get(nodeId); // O(1)

    if (node == null) {
      return null;
    }

    return node.getPredicatesOut();
  }

  private void loadGraph(final String edgeListPath) {
    CompressedFileReader reader = new CompressedFileReader(edgeListPath);

    for (String line : reader) {
      String[] lineElements = line.split(",");

      int subject = Integer.parseInt(lineElements[0]);
      int predicate = Integer.parseInt(lineElements[1]);
      int object = Integer.parseInt(lineElements[2]);

      insertEdge(subject, predicate, object);
    }
  }

  private void insertEdge(final int subject,
                          final int predicate,
                          final int object) {

    if (!nodes.containsKey(subject)) {
      nodes.put(subject, new Node());
    }

    if (!nodes.containsKey(object)) {
      nodes.put(object, new Node());
    }

    Edge edge = new Edge(subject, predicate, object);

    nodes.get(subject).addOutgoingEdge(edge);
    nodes.get(subject).increaseOutCounter(predicate);
    nodes.get(object).increaseInCounter(predicate);
  }
}
