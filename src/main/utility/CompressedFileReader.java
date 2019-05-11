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
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Objects;

import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;

public final class CompressedFileReader
        implements Iterable<String>, Iterator<String> {

  private final BufferedReader bufferedReader;
  private String nextLine;

  public CompressedFileReader(final String path) {
    bufferedReader = decompress(path);
  }

  @Override
  public Iterator<String> iterator() {
    return this;
  }

  @Override
  public boolean hasNext() {
    try {
      nextLine = bufferedReader.readLine();
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }

    return nextLine != null;
  }

  @Override
  public String next() {
    return nextLine;
  }

  private BufferedReader decompress(final String filePath) {
    if (filePath.matches(".*\\.7z")) {
      SevenZInputStream inputStream = new SevenZInputStream(filePath);
      return new BufferedReader(new InputStreamReader(inputStream));
    }

    CompressorInputStream input = null;

    try {
      FileInputStream fis = new FileInputStream(filePath);
      BufferedInputStream bis = new BufferedInputStream(fis);
      input = new CompressorStreamFactory().createCompressorInputStream(bis);
    } catch (FileNotFoundException | CompressorException e) {
      e.printStackTrace();
    }

    return new BufferedReader(new InputStreamReader(input));
  }

  public BufferedReader getBufferedReader() {
    return bufferedReader;
  }
}
