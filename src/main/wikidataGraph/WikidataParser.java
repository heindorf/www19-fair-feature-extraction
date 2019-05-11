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

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MappingJsonFactory;
import com.fasterxml.jackson.databind.util.TokenBuffer;

import java.io.IOException;

public final class WikidataParser {

  private static final String WIKIDATA_GRAPH_PATH
          = "../data/wikidata-graph/wikidata-graph.csv.bz2";

  private static final String TAG_ID = "id";
  private static final String TAG_CLAIMS = "claims";
  private static final String TAG_MAINSNAK = "mainsnak";
  private static final String TAG_DATATYPE = "datatype";
  private static final String TAG_WIKIBASE_ITEM = "wikibase-item";
  private static final String TAG_DATAVALUE = "datavalue";
  private static final String TAG_VALUE = "value";
  private static final String TAG_NUMERIC_ID = "numeric-id";
  private static final String REGEX_TITLE = "Q\\d+";
  private static final String REGEX_PROPERTY = "P\\d+";

  private JsonParser jsonParser;
  private boolean isItem;

  private ItemPropertyStore itemPropertyStore;
  private CompressedFileWriter writer;

  private int subject;
  private int predicate;
  private int object;

  public WikidataParser(final String wikidataJsonDump) {
    itemPropertyStore = new ItemPropertyStore();
    writer = new CompressedFileWriter(WIKIDATA_GRAPH_PATH);
    createJsonParser(wikidataJsonDump);
  }

  public void parse() {
    try {
      jsonParser.nextToken();

      // parse triples for each Item
      while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
        parseTriples();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    itemPropertyStore.save();
    writer.close();
  }

  private void parseTriples() throws IOException {
    parseSubject();
    goToClaims();
    parseStatements();

    // skip sitelinks
    jsonParser.nextToken();
    jsonParser.skipChildren();
    jsonParser.nextToken();
  }

  private void parseSubject() throws IOException {
    String text = jsonParser.getText();
    while (!text.equals(TAG_ID)) {
      jsonParser.nextToken();
      text = jsonParser.getText();
    }

    text = jsonParser.nextTextValue();

    if (text.matches(REGEX_TITLE)) {
      subject = Integer.parseInt(text.substring(1));
      isItem = true;
    } else {
      isItem = false;
    }
  }

  private void parseStatements() throws IOException {
    jsonParser.nextToken();
    jsonParser.nextToken();

    while (jsonParser.getText().matches(REGEX_PROPERTY)) {

      predicate = Integer.parseInt(jsonParser.getText().substring(1));
      jsonParser.nextToken();

      parseClaimsForThisProperty();

      jsonParser.skipChildren();
      jsonParser.nextToken();
    }

    jsonParser.nextToken();
  }

  private void parseClaimsForThisProperty() throws IOException {
    JsonNode claims = getPropertyAsTree();

    for (JsonNode claim : claims) {
      extractObjectFromClaimAndSave(claim);
    }
  }

  private void extractObjectFromClaimAndSave(final JsonNode claim) {
    JsonNode mainsnak = claim.get(TAG_MAINSNAK);
    JsonNode datatypeNode = mainsnak.get(TAG_DATATYPE);

    if (datatypeNode == null) {
      return;
    }

    String datatype = datatypeNode.textValue();

    if (!datatype.equals(TAG_WIKIBASE_ITEM)) {
      return;
    }

    JsonNode datavalue = mainsnak.get(TAG_DATAVALUE);

    if (datavalue == null) {
      return;
    }

    JsonNode value = datavalue.get(TAG_VALUE);
    JsonNode numericid = value.get(TAG_NUMERIC_ID);

    object = Integer.parseInt(numericid.toString());

    if (isItem) {
      itemPropertyStore.addProperty(predicate);
      writer.writeLine(subject + "," + predicate + "," + object);
    }
  }

  private JsonNode getPropertyAsTree() throws IOException {
    TokenBuffer buf = new TokenBuffer(jsonParser);
    buf.copyCurrentStructure(jsonParser);
    JsonParser p = buf.asParser();
    JsonNode claims = p.readValueAsTree();
    buf.close();
    return claims;
  }

  private void goToClaims() throws IOException {
    String text = jsonParser.getText();
    while (!text.equals(TAG_CLAIMS)) {
      jsonParser.nextToken();
      text = jsonParser.getText();
    }
  }

  private void createJsonParser(final String wikidataJsonDump) {
    JsonFactory factory = new MappingJsonFactory();
    CompressedFileReader reader;
    try {
      reader = new CompressedFileReader(wikidataJsonDump);
      jsonParser = factory.createParser(reader.getBufferedReader());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
