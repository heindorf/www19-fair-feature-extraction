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

public final class Revision {

  private int revisionId = -1;
  private int subject = -1;
  private int predicate = -1;
  private int object = -1;

  public Revision() {

  }

  public Revision(Revision revision) {
    revisionId = revision.revisionId;
    subject = revision.subject;
    predicate = revision.predicate;
    object = revision.object;
  }

  public int getRevisionId() {
    return revisionId;
  }

  public void setRevisionId(final int revisionId) {
    this.revisionId = revisionId;
  }

  public int getSubject() {
    return subject;
  }

  public void setSubject(final int subject) {
    this.subject = subject;
  }

  public int getPredicate() {
    return predicate;
  }

  public void setPredicate(final int predicate) {
    this.predicate = predicate;
  }

  public int getObject() {
    return object;
  }

  public void setObject(final int object) {
    this.object = object;
  }

  public static int compare(final Revision revision, final Revision revision1) {
    return Integer.compare(revision.revisionId, revision1.revisionId);
  }

  public boolean isComplete() {
    return !(revisionId == -1
            || subject == -1
            || predicate == -1
            || object == -1);
  }

  public void reset() {
    revisionId = -1;
    subject = -1;
    predicate = -1;
    object = -1;
  }
}
