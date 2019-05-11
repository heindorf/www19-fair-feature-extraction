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

public final class FeatureStore {

  // features
  private ArrayList<Integer> revisionId = new ArrayList<>();
  private ArrayList<Integer> isEditingTool = new ArrayList<>();
  private ArrayList<Integer> subject = new ArrayList<>();
  private ArrayList<Integer> predicate = new ArrayList<>();
  private ArrayList<Integer> object = new ArrayList<>();
  private ArrayList<Integer> superSubject = new ArrayList<>();
  private ArrayList<Integer> superObject = new ArrayList<>();

  public void save(final String dataSet) {
    String fileName = "../data/features/" + dataSet + "/features.csv.bz2";
    CompressedFileWriter writer = new CompressedFileWriter(fileName);

    // csv header
    writer.writeLine("revisionId,isEditingTool,"
            + "subject,predicate,object,"
            + "superSubject,superObject");

    for (int revId = 0; revId < revisionId.size(); revId++) {
      String line = revisionId.get(revId) + ","
              + isEditingTool.get(revId) + ","
              + subject.get(revId) + ","
              + predicate.get(revId) + ","
              + object.get(revId) + ","
              + superSubject.get(revId) + ","
              + superObject.get(revId);
      writer.writeLine(line);
    }

    writer.close();
  }

  public void addRevision(final Revision revision) {
    revisionId.add(revision.getRevisionId());
    subject.add(revision.getSubject());
    predicate.add(revision.getPredicate());
    object.add(revision.getObject());
  }

  public void setIsEditingTool(final ArrayList<Integer> isEditingTool) {
    this.isEditingTool = isEditingTool;
  }

  public void setSuperSubject(final ArrayList<Integer> superSubject) {
    this.superSubject = superSubject;
  }

  public void setSuperObject(final ArrayList<Integer> superObject) {
    this.superObject = superObject;
  }

  public ArrayList<Integer> getFeatureSubject() {
    return subject;
  }

  public ArrayList<Integer> getFeatureObject() {
    return object;
  }

  public ArrayList<Integer> getRevisionIds() {
    return revisionId;
  }

  public ArrayList<Integer> getFeaturePredicate() {
    return predicate;
  }

  public int size() {
    return revisionId.size();
  }
}

