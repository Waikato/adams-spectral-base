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

/**
 * FastWaveletTest.java
 * Copyright (C) 2010-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.data.spectrumfilter;

import adams.data.filter.AbstractFastWavelet.WaveletType;
import adams.data.filter.Filter;
import adams.data.spectrum.Spectrum;
import adams.env.Environment;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Test class for the FastWavelet filter. Run from the command line with: <br><br>
 * java adams.data.spectrumfilter.FastWaveletTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 2242 $
 */
public class FastWaveletTest
  extends AbstractSpectrumFilterTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public FastWaveletTest(String name) {
    super(name);
  }

  /**
   * Returns the configured filter.
   *
   * @return		the filter
   */
  public Filter<Spectrum> getFilter() {
    return new FastWavelet();
  }

  /**
   * Returns the filenames (without path) of the input data files to use
   * in the regression test.
   *
   * @return		the filenames
   */
  protected String[] getRegressionInputFiles() {
    return new String[]{
	"872280-nir.spec",
	"872280-nir.spec",
	"872280-nir.spec",
	"872280-nir.spec"
    };
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  protected Filter[] getRegressionSetups() {
    FastWavelet[]	result;

    result = new FastWavelet[4];

    result[0] = new FastWavelet();
    result[0].setWaveletType(WaveletType.CDF2_4);

    result[1] = new FastWavelet();
    result[1].setWaveletType(WaveletType.DAUBECHIES2);

    result[2] = new FastWavelet();
    result[2].setWaveletType(WaveletType.HAAR);

    result[3] = new FastWavelet();
    result[3].setWaveletType(WaveletType.SYMMLET8);

    return result;
  }

  /**
   * Returns the test suite.
   *
   * @return		the suite
   */
  public static Test suite() {
    return new TestSuite(FastWaveletTest.class);
  }

  /**
   * Runs the test from commandline.
   *
   * @param args	ignored
   */
  public static void main(String[] args) {
    Environment.setEnvironmentClass(Environment.class);
    runTest(suite());
  }
}
