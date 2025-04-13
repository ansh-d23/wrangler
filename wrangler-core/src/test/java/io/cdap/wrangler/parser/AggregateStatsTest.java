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

package io.cdap.wrangler.parser;

import io.cdap.wrangler.api.Row;
import io.cdap.wrangler.api.parser.ByteSize;
import io.cdap.wrangler.api.parser.TimeDuration;
import io.cdap.wrangler.test.TestingRig;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class AggregateStatsTest {

  @Test
  public void testAggregateStats() throws Exception {
    // Create sample data
    List<Row> rows = Arrays.asList(
      createRow("10MB", "1s"),
      createRow("5MB", "500ms"),
      createRow("2GB", "2m")
    );

    // Define the recipe
    String[] recipe = new String[] {
      "aggregate-stats :data_size :response_time total_size_mb total_time_sec"
    };

    // Execute the recipe
    List<Row> results = TestingRig.execute(recipe, rows);

    // Verify results
    Assert.assertEquals(1, results.size());
    Row result = results.get(0);
    
    // Expected total size: 10MB + 5MB + 2GB = 2059MB
    Assert.assertEquals(2059.0, result.getValue("total_size_mb"), 0.001);
    
    // Expected total time: 1s + 500ms + 2m = 121.5s
    Assert.assertEquals(121.5, result.getValue("total_time_sec"), 0.001);
  }

  @Test
  public void testAggregateStatsWithCustomUnits() throws Exception {
    // Create sample data
    List<Row> rows = Arrays.asList(
      createRow("10MB", "1s"),
      createRow("5MB", "500ms"),
      createRow("2GB", "2m")
    );

    // Define the recipe with custom units
    String[] recipe = new String[] {
      "aggregate-stats :data_size :response_time total_size_gb total_time_min GB m"
    };

    // Execute the recipe
    List<Row> results = TestingRig.execute(recipe, rows);

    // Verify results
    Assert.assertEquals(1, results.size());
    Row result = results.get(0);
    
    // Expected total size: 10MB + 5MB + 2GB = 2.0117GB
    Assert.assertEquals(2.0117, result.getValue("total_size_gb"), 0.001);
    
    // Expected total time: 1s + 500ms + 2m = 2.025m
    Assert.assertEquals(2.025, result.getValue("total_time_min"), 0.001);
  }

  private Row createRow(String size, String time) {
    Row row = new Row();
    row.add("data_size", new ByteSize(size));
    row.add("response_time", new TimeDuration(time));
    return row;
  }
} 