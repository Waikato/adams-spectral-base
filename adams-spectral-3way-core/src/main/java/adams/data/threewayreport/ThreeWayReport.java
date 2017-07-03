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
 * ThreeWayReport.java
 * Copyright (C) 2017 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.data.threewayreport;

import adams.core.DateUtils;
import adams.core.Properties;
import adams.data.id.MutableIDHandler;
import adams.data.report.DataType;
import adams.data.report.Field;
import adams.data.report.Report;

import java.util.Date;

/**
 * Read and store data from the Report for a ThreeWayData structure.
 *
 * @author dale
 * @version $Revision: 11831 $
 */
public class ThreeWayReport
  extends Report
  implements MutableIDHandler {

  /** for serialization. */
  private static final long serialVersionUID = 6796944821226248339L;

  /** field name for time of insertion into database. */
  public final static String INSERT_TIMESTAMP = "Insert Timestamp";

  /** field name for the instrument. */
  public final static String INSTRUMENT = "Instrument";

  /** the default instrument ('unknown'). */
  public final static String DEFAULT_INSTRUMENT = "unknown";

  /** field name for the sample ID. */
  public final static String SAMPLE_ID = "Sample ID";

  /** field name for the sample type. */
  public final static String SAMPLE_TYPE = "Sample Type";

  /** field name for the format. */
  public final static String FORMAT = "Format";

  /** the default data type. */
  public final static String DEFAULT_FORMAT = "EEM";

  /** field name for the source. */
  public final static String SOURCE = "Source";

  /**
   * Updates certain dependant fields. This method should be called before
   * saving it to the database, after loading it from the database or when
   * a quantitation report has been created by hand.
   */
  @Override
  public void update() {
    super.update();

    if (!hasValue(new Field(FORMAT, DataType.STRING)))
      addParameter(FORMAT, DEFAULT_FORMAT);
  }

  /**
   * Creates a dummy report.
   *
   * @param instrument	the instrument this dummy is for
   * @param insertDate	the date for the insertion in the database
   * @param format	the format of the data
   * @return		the generated report
   */
  public static ThreeWayReport createDummy(String instrument, Date insertDate, String format) {
    ThreeWayReport	result;

    result = new ThreeWayReport();

    result.setDummyReport(true);
    result.addParameter(INSTRUMENT, instrument);
    result.addParameter(INSERT_TIMESTAMP, DateUtils.getTimestampFormatter().format(insertDate));
    result.addParameter(FORMAT, format);

    return result;
  }

  /**
   * Parses the string generated by the toString() method.
   *
   * @param s		the string to parse
   * @return		the generated report
   */
  public static ThreeWayReport parseReport(String s) {
    ThreeWayReport	result;
    Report	report;

    report = Report.parseReport(s);
    result = new ThreeWayReport();
    result.assign(report);

    return result;
  }

  /**
   * Parses the properties (generated with the toProperties() method) and
   * generates a sample data object from it.
   *
   * @param props	the properties to generate the sample data from
   * @return		the sample data
   */
  public static ThreeWayReport parseProperties(Properties props) {
    ThreeWayReport	result;
    Report	report;

    report = Report.parseProperties(props);
    result = new ThreeWayReport();
    result.assign(report);

    return result;
  }

  /**
   * Set field types.
   */
  @Override
  protected void initFields(){
    super.initFields();

    addField(new Field(SAMPLE_ID, DataType.STRING));
    addField(new Field(SAMPLE_TYPE, DataType.STRING));
    addField(new Field(INSERT_TIMESTAMP, DataType.STRING));
    addField(new Field(INSTRUMENT, DataType.STRING));
    addField(new Field(FORMAT, DataType.STRING));
    addField(new Field(SOURCE, DataType.STRING));
  }

  /**
   * Returns the ID.
   *
   * @return		the ID
   */
  public String getID() {
    return getStringValue(new Field(SAMPLE_ID, DataType.STRING));
  }

  /**
   * Sets the ID.
   *
   * @param value	the ID
   */
  public void setID(String value) {
    setValue(new Field(SAMPLE_ID, DataType.STRING), value.replace("'", ""));
  }
}
