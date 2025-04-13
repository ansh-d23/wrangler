/*
 * Copyright © 2017-2019 Cask Data, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package io.cdap.wrangler.api.parser;

import org.junit.Assert;
import org.junit.Test;

public class TimeDurationTest {

  @Test
  public void testTimeDurationParsing() {
    // Test basic time durations
    TimeDuration t1 = new TimeDuration("1000ns");
    Assert.assertEquals(1000, t1.getNanoseconds());
    
    TimeDuration t2 = new TimeDuration("1us");
    Assert.assertEquals(1000, t2.getNanoseconds());
    
    TimeDuration t3 = new TimeDuration("1ms");
    Assert.assertEquals(1000 * 1000, t3.getNanoseconds());
    
    TimeDuration t4 = new TimeDuration("1s");
    Assert.assertEquals(1000L * 1000 * 1000, t4.getNanoseconds());
    
    TimeDuration t5 = new TimeDuration("1m");
    Assert.assertEquals(60L * 1000 * 1000 * 1000, t5.getNanoseconds());
    
    TimeDuration t6 = new TimeDuration("1h");
    Assert.assertEquals(60L * 60 * 1000 * 1000 * 1000, t6.getNanoseconds());
    
    TimeDuration t7 = new TimeDuration("1d");
    Assert.assertEquals(24L * 60 * 60 * 1000 * 1000 * 1000, t7.getNanoseconds());
  }

  @Test
  public void testTimeDurationConversion() {
    TimeDuration duration = new TimeDuration("1s");
    
    Assert.assertEquals(1.0, duration.getDurationAs("s"), 0.001);
    Assert.assertEquals(1000.0, duration.getDurationAs("ms"), 0.001);
    Assert.assertEquals(1000 * 1000.0, duration.getDurationAs("us"), 0.001);
    Assert.assertEquals(1000 * 1000 * 1000.0, duration.getDurationAs("ns"), 0.001);
    Assert.assertEquals(1.0 / 60, duration.getDurationAs("m"), 0.001);
    Assert.assertEquals(1.0 / (60 * 60), duration.getDurationAs("h"), 0.001);
    Assert.assertEquals(1.0 / (24 * 60 * 60), duration.getDurationAs("d"), 0.001);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidTimeDuration() {
    new TimeDuration("invalid");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidUnit() {
    new TimeDuration("10xx");
  }
} 
