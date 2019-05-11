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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Objects;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public final class WdvcParser {

  private static Logger logger = LogManager.getLogger(Main.class);

  private WdvcHandler handler;
  private LinkedList<Revision> allRevisions = new LinkedList<>();
  private String wdvcPath;

  public WdvcParser(final String wdvcPath) {
    this.wdvcPath = wdvcPath;
  }

  public void extractLinkRevisions(final String dataSetName,
                                   final FeatureStore featureStore) {

    parseWdvcDatset(wdvcPath + dataSetName);

    allRevisions.sort(Revision::compare);
    for (Revision revision : allRevisions) {
      featureStore.addRevision(revision);
    }
    allRevisions.clear();
  }

  private void parseWdvcDatset(final String datasetPath) {
    File directory = new File(datasetPath);
    File[] wdvcDumps = directory.listFiles();
    Arrays.sort(Objects.requireNonNull(wdvcDumps));

    for (File file : wdvcDumps) {
        logger.info("Parse: " + file.getPath());
        parseWdvcFile(file);
        allRevisions.addAll(handler.getRevisions());
        logger.info("Done.");
    }
  }

  private void parseWdvcFile(final File file) {
    handler = new WdvcHandler();

    try {
      InputSource inputSource = createInputSource(file);
      SAXParserFactory factory = SAXParserFactory.newInstance();
      SAXParser saxParser = factory.newSAXParser();
      saxParser.parse(inputSource, handler);
    } catch (ParserConfigurationException | SAXException | IOException e) {
      e.printStackTrace();
    }
  }


  private InputSource createInputSource(final File file) {
    InputSource inputSource = null;
    try {
      InputStream stream = new FileInputStream(file);
      BufferedInputStream stream2 = new BufferedInputStream(stream);
      Reader reader = new InputStreamReader(stream2, StandardCharsets.UTF_8);
      inputSource = new InputSource(reader);
      inputSource.setEncoding("UTF-8");
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    return inputSource;

  }
}
