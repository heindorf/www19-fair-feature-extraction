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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class FeatureBuilder {

  private static Logger logger = LogManager.getLogger(FeatureBuilder.class);

  private static final String WIKIDATA_GRAPH_PATH
          = "../data/wikidata-graph/wikidata-graph.csv.bz2";

  private WikidataGraph wikidataGraph;

  public FeatureBuilder() {
    wikidataGraph = new WikidataGraph(WIKIDATA_GRAPH_PATH);
  }

  public void buildFeatures(final String wdvcXmlCorpus,
                            final String wdvdFeatures,
                            final String dataSet) {
    FeatureStore featureStore = new FeatureStore();

    // create features (revisionID, subject, predicate, object)
    WdvcParser wdvcParser = new WdvcParser(wdvcXmlCorpus);
    wdvcParser.extractLinkRevisions(dataSet, featureStore);
    logger.info("Finished WDVC-features.");

    // create feature (isEditingTool)
    IsEditingTool isEditingTool = new IsEditingTool(wdvdFeatures);
    isEditingTool.createFeature(featureStore);
    logger.info("Finished isEditingTool-feature.");

    // create features (superObject, superSubject)
    graphFeatures(featureStore);
    featureStore.save(dataSet);
    logger.info("Finished Graph-Features.");

    // create feature (embeddings)
    buildPredicateEmbedding(dataSet, featureStore);
    logger.info("Finished Embedding-features.");
  }

  private void graphFeatures(final FeatureStore featureStore) {
    SuperSubject superSubject = new SuperSubject(wikidataGraph);
    superSubject.createFeature(featureStore);

    SuperObject superObject = new SuperObject(wikidataGraph);
    superObject.createFeature(featureStore);
  }

  private void buildPredicateEmbedding(final String dataSet,
                                       final FeatureStore featureStore) {
    PredicateEmbedding pe = new PredicateEmbedding(wikidataGraph);
    pe.createEmbedding(featureStore);
    pe.save(dataSet);
  }
}
