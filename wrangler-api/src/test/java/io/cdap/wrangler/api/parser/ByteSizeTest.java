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

public class ByteSizeTest {

  @Test
  public void testByteSizeParsing() {
    // Test basic byte sizes
    ByteSize b1 = new ByteSize("10B");
    Assert.assertEquals(10, b1.getBytes());
    
    ByteSize b2 = new ByteSize("1KB");
    Assert.assertEquals(1024, b2.getBytes());
    
    ByteSize b3 = new ByteSize("1MB");
    Assert.assertEquals(1024 * 1024, b3.getBytes());
    
    ByteSize b4 = new ByteSize("1GB");
    Assert.assertEquals(1024L * 1024 * 1024, b4.getBytes());
    
    ByteSize b5 = new ByteSize("1TB");
    Assert.assertEquals(1024L * 1024 * 1024 * 1024, b5.getBytes());
    
    ByteSize b6 = new ByteSize("1PB");
    Assert.assertEquals(1024L * 1024 * 1024 * 1024 * 1024, b6.getBytes());
  }

  @Test
  public void testByteSizeConversion() {
    ByteSize size = new ByteSize("1MB");
    
    Assert.assertEquals(1.0, size.getBytesAs("MB"), 0.001);
    Assert.assertEquals(1024.0, size.getBytesAs("KB"), 0.001);
    Assert.assertEquals(1024 * 1024.0, size.getBytesAs("B"), 0.001);
    Assert.assertEquals(1.0 / 1024, size.getBytesAs("GB"), 0.001);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidByteSize() {
    new ByteSize("invalid");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidUnit() {
    new ByteSize("10XX");
  }
} 
