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

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

public final class IsEditingTool {

  private static final int REVISION_ID_COLUMN = 0;
  private static final int REVISION_TAG_COLUMN = 75;

  private HashMap<Integer, String> revisionIdToRevisionTagMapping;

  public IsEditingTool(final String wdvdFeatures) {
    revisionIdToRevisionTagMapping = new HashMap<>();
    readWdvdFeatures(wdvdFeatures);
  }

  public void createFeature(final FeatureStore store) {
    ArrayList<Integer> isEditingTool = new ArrayList<>();

    for (Integer revisionId : store.getRevisionIds()) {
      String revisionTag = revisionIdToRevisionTagMapping.get(revisionId);

      if (revisionTag.contains("OAuth") || revisionTag.contains("HHVM")) {
        isEditingTool.add(1);
      } else {
        isEditingTool.add(0);
      }
    }

    store.setIsEditingTool(isEditingTool);
  }

  private void readWdvdFeatures(final String wdvdFeatures) {
    CompressedFileReader compressedFileReader;
    compressedFileReader = new CompressedFileReader(wdvdFeatures);
    BufferedReader reader = compressedFileReader.getBufferedReader();

    Iterable<CSVRecord> records = null;
    try {
      records = CSVFormat.RFC4180.parse(reader);
    } catch (IOException e) {
      e.printStackTrace();
    }

    int i = 0;

    for (CSVRecord record : Objects.requireNonNull(records)) {
      if (i == 0) {
        i++;
        continue; // skip header
      }

      int revisionID = Integer.parseInt(record.get(REVISION_ID_COLUMN));
      String revisionTag = record.get(REVISION_TAG_COLUMN);
      revisionIdToRevisionTagMapping.put(revisionID, revisionTag);
    }
  }
}
