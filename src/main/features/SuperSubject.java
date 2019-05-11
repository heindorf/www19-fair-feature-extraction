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

public final class SuperSubject {

  private static final int P_INSTANCE_OF = 31;
  private WikidataGraph wikidataGraph;

  public SuperSubject(final WikidataGraph wikidataGraph) {
    this.wikidataGraph = wikidataGraph;
  }

  public void createFeature(final FeatureStore store) {
    ArrayList<Integer> superSubject = new ArrayList<>();

    for (int subject : store.getFeatureSubject()) {

      Node v = wikidataGraph.getNodeById(subject);

      if (v == null) {
        superSubject.add(-1);
        continue;
      }

      ArrayList<Edge> outgoingEdges = v.getOutgoingEdges();

      if (outgoingEdges == null) {
        superSubject.add(-1);
        continue;
      }

      findEdge(superSubject, outgoingEdges);
    }

    store.setSuperSubject(superSubject);
  }

  private void findEdge(final ArrayList<Integer> superSubject,
                        final ArrayList<Edge> outgoingEdges) {
    for (Edge e : outgoingEdges) {
      if (e.getPredicate() == P_INSTANCE_OF) {
        superSubject.add(e.getObject());
        return;
      }
    }

    superSubject.add(-1);
  }
}
