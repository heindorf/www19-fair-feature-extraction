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
import java.util.Set;

public final class PredicateEmbedding {

  private static final int NUM_FEATURES = 3500;

  private WikidataGraph wikidataGraph;

  private MatrixCsr subjectOutMatrix = new MatrixCsr(NUM_FEATURES);
  private MatrixCsr predicateMatrix = new MatrixCsr(NUM_FEATURES);
  private MatrixCsr objectInMatrix = new MatrixCsr(NUM_FEATURES);
  private MatrixCsr objectOutMatrix = new MatrixCsr(NUM_FEATURES);

  public PredicateEmbedding(final WikidataGraph wikidataGraph) {
    this.wikidataGraph = wikidataGraph;
  }

  public void createEmbedding(final FeatureStore featureStore) {

    ArrayList<Integer> subjects = featureStore.getFeatureSubject();
    ArrayList<Integer> predicates = featureStore.getFeaturePredicate();
    ArrayList<Integer> objects = featureStore.getFeatureObject();

    for (int revision = 0; revision < featureStore.size(); revision++) {
      int subject = subjects.get(revision);
      int predicate = predicates.get(revision);
      int object = objects.get(revision);

      createEmbeddings(subject, predicate, object);
    }
  }

  private void createEmbeddings(final int subject,
                                final int predicate,
                                final int object) {
    handleOutRepresentation(subject, subjectOutMatrix);
    handlePredicateRepresentation(predicate);
    handleOutRepresentation(object, objectOutMatrix);
    handleInRepresentation(object, objectInMatrix);
  }

  private void handleOutRepresentation(final int item, final MatrixCsr matrix) {
    Node node = wikidataGraph.getNodeById(item);

    if (node == null) {
      matrix.nextRow();
      return;
    }

    Set<Integer> outgoingEdgePredicates
            = wikidataGraph.getOutgoingEdgePredicates(item);

    if (outgoingEdgePredicates == null) {
      matrix.nextRow();
      return;
    }

    for (int p : outgoingEdgePredicates) {
      int counter = wikidataGraph.getNodeById(item).getOutCountFor(p);
      matrix.addDataToCurrentRow(p, counter);
    }

    matrix.nextRow();
  }

  private void handlePredicateRepresentation(final int predicate) {
    predicateMatrix.addDataToCurrentRow(predicate, 1);
    predicateMatrix.nextRow();
  }

  private void handleInRepresentation(final int item, final MatrixCsr matrix) {
    Node node = wikidataGraph.getNodeById(item);

    if (node == null) {
      matrix.nextRow();
      return;
    }

    Set<Integer> incomingEdgePredicates
            = wikidataGraph.getIncomingEdgePredicates(item);

    if (incomingEdgePredicates == null) {
      matrix.nextRow();
      return;
    }

    for (int p : incomingEdgePredicates) {
      int counter = wikidataGraph.getNodeById(item).getInCountFor(p);
      matrix.addDataToCurrentRow(p, counter);
    }

    matrix.nextRow();
  }

  public void save(final String dataSet) {
    String path = "../data/features/" + dataSet + "/embeddings/";

    subjectOutMatrix.save(path + "subjectOut");
    predicateMatrix.save(path + "predicate");
    objectOutMatrix.save(path + "objectOut");
    objectInMatrix.save(path + "objectIn");
  }
}
