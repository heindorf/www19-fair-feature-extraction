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

public final class SuperObject {

  private static final int P_INSTANCE_OF = 31;

  private WikidataGraph wikidataGraph;

  public SuperObject(final WikidataGraph wikidataGraph) {
    this.wikidataGraph = wikidataGraph;
  }

  public void createFeature(final FeatureStore store) {

    ArrayList<Integer> superObject = new ArrayList<>();

    for (int object : store.getFeatureObject()) {
      Node v = wikidataGraph.getNodeById(object);

      if (v == null) {
        superObject.add(-1);
        continue;
      }

      ArrayList<Edge> outgoingEdges = v.getOutgoingEdges();

      if (outgoingEdges == null) {
        superObject.add(-1);
        continue;
      }

      findEdge(superObject, outgoingEdges);
    }

    store.setSuperObject(superObject);
  }

  private void findEdge(final ArrayList<Integer> superObject,
                        final ArrayList<Edge> outgoingEdges) {
    for (Edge e : outgoingEdges) {
      if (e.getPredicate() == P_INSTANCE_OF) {
        superObject.add(e.getObject());
        return;
      }
    }

    superObject.add(-1);
  }
}
