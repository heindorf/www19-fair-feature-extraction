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

import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xml.sax.Attributes;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public final class WdvcHandler extends DefaultHandler {

  private StringBuilder line = new StringBuilder();
  private Revision currentRevision = new Revision();
  private String currentPageTitle;
  private LinkedList<Revision> revisions = new LinkedList<>();

  private String linkRevision = ".*\\[\\[Property:(P\\d+)]].*\\[\\[(Q\\d+)]].*";
  private Matcher matcher = Pattern.compile(linkRevision).matcher("");

  public LinkedList<Revision> getRevisions() {
    return revisions;
  }

  @Override
  public void startElement(final String uri, final String localName,
                           final String tagName, final Attributes attributes) {

    if (tagName.equals("revision")) {
      currentRevision.reset();
      int subject = Integer.parseInt(currentPageTitle.substring(1));
      currentRevision.setSubject(subject);
    }

    line.setLength(0);
  }

  @Override
  public void characters(final char[] ch, final int start, final int length) {
    line.append(ch, start, length);
  }

  @Override
  public void endElement(final String uri,
                         final String localName,
                         final String tagName) throws SAXParseException {
    switch (tagName) {
      case "title":
        currentPageTitle = line.toString();
        break;
      case "id":
        setRevId();
        break;
      case "comment":
        processComment();
        break;
      case "revision":
        saveRevision();
        break;
      default:
        break;
    }
  }

  private void saveRevision() {
    if (!currentRevision.isComplete()) {
      return;
    }

    revisions.add(new Revision(currentRevision));
    currentRevision.reset();
  }

  private void setRevId() {
    if (currentRevision.getRevisionId() == -1) {
      currentRevision.setRevisionId(Integer.parseInt(line.toString()));
    }
  }

  private void processComment() {
    if (!line.toString().contains("claim")) {
      return;
    }

    matcher.reset(line);
    if (matcher.find()) {
      int predicate = Integer.parseInt(matcher.group(1).substring(1));
      int object = Integer.parseInt(matcher.group(2).substring(1));

      currentRevision.setPredicate(predicate);
      currentRevision.setObject(object);
    }
  }
}
