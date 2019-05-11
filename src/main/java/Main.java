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

public final class Main {

  private static Logger logger = LogManager.getLogger(Main.class);

  public static void main(final String[] args) {
    String wdvcXmlCorpus = args[0];
    String wikidataJsonDump = args[1];
    String wdvdFeatures = args[2];

    logger.info("Create Wikidata Graph");
    WikidataParser wikidataParser = new WikidataParser(wikidataJsonDump);
    wikidataParser.parse();
    logger.info("Finished Wikidata Graph.");

    String[] dataSets = {"training", "validation", "test"};

    for (String dataSet : dataSets) {
      logger.info("Create data set: " + dataSet);

      FeatureBuilder featureBuilder = new FeatureBuilder();
      featureBuilder.buildFeatures(wdvcXmlCorpus, wdvdFeatures, dataSet);
    }

    logger.info("Done.");
  }
}
