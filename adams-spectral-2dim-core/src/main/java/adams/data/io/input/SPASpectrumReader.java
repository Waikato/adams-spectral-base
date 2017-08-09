/*
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * SPASpectrumReader.java
 * Copyright (C) 2017 University of Waikato, Hamilton, New Zealand
 */

package adams.data.io.input;

import adams.core.DateFormat;
import adams.core.DateUtils;
import adams.core.LittleEndian;
import adams.core.Utils;
import adams.core.io.FileUtils;
import adams.data.spectrum.Spectrum;
import adams.data.spectrum.SpectrumPoint;

/**
<!-- globalinfo-start -->
* Loads spectral data files in Nicolet SPA format.
* <br><br>
<!-- globalinfo-end -->
*
<!-- options-start -->
* <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
* &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
* &nbsp;&nbsp;&nbsp;default: WARNING
* </pre>
*
* <pre>-input &lt;adams.core.io.PlaceholderFile&gt; (property: input)
* &nbsp;&nbsp;&nbsp;The file to read and turn into a container.
* &nbsp;&nbsp;&nbsp;default: ${CWD}
* </pre>
*
* <pre>-create-dummy-report &lt;boolean&gt; (property: createDummyReport)
* &nbsp;&nbsp;&nbsp;If true, then a dummy report is created if none present.
* &nbsp;&nbsp;&nbsp;default: false
* </pre>
*
* <pre>-instrument &lt;java.lang.String&gt; (property: instrument)
* &nbsp;&nbsp;&nbsp;The name of the instrument that generated the spectra (if not already present
* &nbsp;&nbsp;&nbsp;in data).
* &nbsp;&nbsp;&nbsp;default: unknown
* </pre>
*
* <pre>-format &lt;java.lang.String&gt; (property: format)
* &nbsp;&nbsp;&nbsp;The data format string.
* &nbsp;&nbsp;&nbsp;default: NIR
* </pre>
*
* <pre>-keep-format &lt;boolean&gt; (property: keepFormat)
* &nbsp;&nbsp;&nbsp;If enabled the format obtained from the file is not replaced by the format
* &nbsp;&nbsp;&nbsp;defined here.
* &nbsp;&nbsp;&nbsp;default: false
* </pre>
*
* <pre>-use-absolute-source &lt;boolean&gt; (property: useAbsoluteSource)
* &nbsp;&nbsp;&nbsp;If enabled the source report field stores the absolute file name rather
* &nbsp;&nbsp;&nbsp;than just the name.
* &nbsp;&nbsp;&nbsp;default: false
* </pre>
*
<!-- options-end -->
*
* @author  fracpete (fracpete at waikato dot ac dot nz)
* @version $Revision: 2242 $
*/
public class SPASpectrumReader
  extends AbstractSpectrumReader {

  /** for serialization. */
  private static final long serialVersionUID = 7690015355854851867L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Loads spectral data files in Nicolet SPA format.";
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return 			a description suitable for displaying in the
   * 				file chooser
   */
  public String getFormatDescription() {
    return "Nicolet SPA Format";
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension(s) (without the dot!)
   */
  public String[] getFormatExtensions() {
    return new String[]{"spa"};
  }

  /**
   * Reads a string starting at the offset till encountering a null byte or EOF.
   *
   * @param data	the bytes
   * @param offset	the starting point
   * @return		the parsed string
   */
  protected String readString(byte[] data, int offset) {
    StringBuilder	result;
    int			i;
    byte		b;

    result = new StringBuilder();
    i      = offset;
    do {
      b = data[i];
      if (b == 0)
        break;
      result.append((char) b);
      i++;
    }
    while (i < data.length);

    return result.toString();
  }

  /**
   * Reads an integer (4bytes).
   *
   * @param data	the data to use
   * @param offset	the offset
   * @return		the integer
   */
  protected int readInt(byte[] data, int offset) {
    return LittleEndian.bytesToInt(new byte[]{data[offset+0], data[offset+1], data[offset+2], data[offset+3]});
  }

  /**
   * Reads a float (4bytes).
   *
   * @param data	the data to use
   * @param offset	the offset
   * @return		the float
   */
  protected float readFloat(byte[] data, int offset) {
    return LittleEndian.bytesToFloat(new byte[]{data[offset+0], data[offset+1], data[offset+2], data[offset+3]});
  }

  /**
   * Performs the actual reading.
   */
  @Override
  protected void readData() {
    byte[] 		data;
    Spectrum		sp;
    String[]		comments;
    DateFormat		dfcomm;
    DateFormat 		dfreg;
    String		section;
    String		key;
    String		sval;
    int			dataOff;
    int			numPoints;
    float		minWave;
    float		maxWave;
    int			i;
    SpectrumPoint	point;

    sp = new Spectrum();

    // read data
    data = FileUtils.loadFromBinaryFile(m_Input);

    // ID
    sp.setID(readString(data, 30));

    // comments
    comments = readString(data, 896).split("\r\n");
    if (isLoggingEnabled())
      getLogger().info("Comments:\n" + Utils.flatten(comments, "\n"));
    section = "";
    dfcomm  = new DateFormat("EEE MMM dd HH:mm:ss yyyy (z)");
    dfreg   = DateUtils.getTimestampFormatter();
    for (String comment: comments) {
      if (!comment.startsWith("\t")) {
	section = comment;
	continue;
      }
      comment = comment.trim();
      if (comment.contains(" on ")) {
        key = comment.substring(0, comment.indexOf(" on ")).trim();
        sval = comment.substring(comment.indexOf(" on ") + 4).trim();
        sp.getReport().setStringValue((section.isEmpty() ? "" : (section + " - ") + key), dfreg.format(dfcomm.parse(sval)));
      }
      else if (comment.contains(":")) {
        key = comment.substring(0, comment.indexOf(":")).trim();
        sval = comment.substring(comment.indexOf(":") + 1).trim();
        sp.getReport().setStringValue((section.isEmpty() ? "" : (section + " - ") + key), sval);
      }
    }

    // data offset
    dataOff = readInt(data, 386);
    if (isLoggingEnabled())
      getLogger().info("Data offset: " + dataOff);

    // num points
    numPoints = readInt(data, 564);
    if (isLoggingEnabled())
      getLogger().info("# points: " + numPoints);

    // min/max waveno
    maxWave = readFloat(data, 576);
    minWave = readFloat(data, 580);
    if (isLoggingEnabled())
      getLogger().info("Wave numbers: " + minWave + " - " + maxWave);

    for (i = 0; i < numPoints; i++) {
      point = new SpectrumPoint(
	maxWave - (maxWave - minWave) * i / numPoints,
	readFloat(data, dataOff + i * 4)
      );
      sp.add(point);
      if (isLoggingEnabled())
        getLogger().info(i + ": " + point);
    }

    m_ReadData.add(sp);
  }
}