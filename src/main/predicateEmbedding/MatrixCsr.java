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

import it.unimi.dsi.fastutil.ints.IntArrayList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/*
 * Implements Compressed_Row_Storage as explained here:
 *
 * https://de.wikipedia.org/wiki/Compressed_Row_Storage
 *
 */
public final class MatrixCsr {

  private static Logger logger = LogManager.getLogger(MatrixCsr.class);

  private IntArrayList dataList; // dataList array of the matrix
  private IntArrayList indicesList; // index array of the matrix
  private IntArrayList indptrList; // index pointer array of the matrix

  private int numRows = 0;
  private int numCols;

  private int newCols = 0;

  public MatrixCsr(final int numColumns) {
    this.numCols = numColumns;

    dataList = new IntArrayList();
    indicesList = new IntArrayList();
    indptrList = new IntArrayList();

    indptrList.add(0);
  }

  /*
   * use this method in combination with nextRow().
   */
  public void addDataToCurrentRow(final int column, final int data) {
    if (data == 0) {
      return;
    }

    newCols++;
    this.dataList.add(data);
    indicesList.add(column);
  }

  /*
   * use this method in combination with addDataToCurrentRow()
   */
  public void nextRow() {
    indptrList.add(indptrList.getInt(indptrList.size() - 1) + newCols);
    numRows++;
    newCols = 0;
  }

  public void save(final String path) {
    logger.info("Save " + numRows + "x" + numCols + " Matrix here:" + path);

    saveList(dataList, path + "_data.csv.bz2");
    saveList(indicesList, path + "_indices.csv.bz2");
    saveList(indptrList, path + "_indptr.csv.bz2");

    saveShape(path + "_shape.csv.bz2");
  }

  private void saveList(final IntArrayList list, final String path) {
    CompressedFileWriter writer = new CompressedFileWriter(path);

    for (int i = 0; i < list.size() - 1; i++) {
      writer.write(list.getInt(i) + ",");
    }

    writer.write(list.getInt(list.size() - 1) + "");
    writer.close();
  }

  private void saveShape(final String path) {
    CompressedFileWriter writer = new CompressedFileWriter(path);
    writer.writeLine(numRows + "," + numCols);
    writer.close();
  }
}
