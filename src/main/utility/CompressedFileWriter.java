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

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;

public final class CompressedFileWriter {

  // https://tools.ietf.org/html/rfc4180
  private static final String CR_LF = "\r\n";

  private OutputStream fout;
  private BufferedOutputStream out;
  private BZip2CompressorOutputStream writer;

  public CompressedFileWriter(final String fileName) {
    try {
      Path filePath = Paths.get(fileName);
      fout = Files.newOutputStream(filePath);
      out = new BufferedOutputStream(fout);
      writer = new BZip2CompressorOutputStream(out);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void write(final String string) {
    try {
      writer.write(string.getBytes());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void writeLine(final String line) {
    write(line);
    write(CR_LF);
  }

  public void close() {
    try {
      writer.flush();
      out.flush();
      fout.flush();
      writer.close();
      out.close();
      fout.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
