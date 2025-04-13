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

package io.cdap.wrangler.executor;

import io.cdap.wrangler.api.Directive;
import io.cdap.wrangler.api.DirectiveExecutionException;
import io.cdap.wrangler.api.DirectiveParseException;
import io.cdap.wrangler.api.ExecutorContext;
import io.cdap.wrangler.api.Row;
import io.cdap.wrangler.api.annotations.Categories;
import io.cdap.wrangler.api.parser.ByteSize;
import io.cdap.wrangler.api.parser.ColumnName;
import io.cdap.wrangler.api.parser.Text;
import io.cdap.wrangler.api.parser.TokenType;
import io.cdap.wrangler.api.parser.UsageDefinition;
import io.cdap.wrangler.api.parser.TimeDuration;

import java.util.List;

/**
 * A directive that aggregates byte size and time duration statistics.
 */
@Categories(categories = {"aggregate"})
public class AggregateStats implements Directive {
  public static final String NAME = "aggregate-stats";
  private String sizeColumn;
  private String timeColumn;
  private String totalSizeColumn;
  private String totalTimeColumn;
  private String sizeUnit = "MB";
  private String timeUnit = "s";
  private long totalBytes = 0;
  private long totalNanoseconds = 0;
  private int rowCount = 0;

  @Override
  public UsageDefinition define() {
    UsageDefinition.Builder builder = UsageDefinition.builder(NAME);
    builder.define("size-column", TokenType.COLUMN_NAME);
    builder.define("time-column", TokenType.COLUMN_NAME);
    builder.define("total-size-column", TokenType.COLUMN_NAME);
    builder.define("total-time-column", TokenType.COLUMN_NAME);
    builder.define("size-unit", TokenType.TEXT, false);
    builder.define("time-unit", TokenType.TEXT, false);
    return builder.build();
  }

  @Override
  public void initialize(Arguments args) throws DirectiveParseException {
    this.sizeColumn = ((ColumnName) args.value("size-column")).value();
    this.timeColumn = ((ColumnName) args.value("time-column")).value();
    this.totalSizeColumn = ((ColumnName) args.value("total-size-column")).value();
    this.totalTimeColumn = ((ColumnName) args.value("total-time-column")).value();
    
    if (args.contains("size-unit")) {
      this.sizeUnit = ((Text) args.value("size-unit")).value();
    }
    if (args.contains("time-unit")) {
      this.timeUnit = ((Text) args.value("time-unit")).value();
    }
  }

  @Override
  public List<Row> execute(List<Row> rows, ExecutorContext context) throws DirectiveExecutionException {
    for (Row row : rows) {
      Object sizeValue = row.getValue(sizeColumn);
      Object timeValue = row.getValue(timeColumn);
      
      if (sizeValue instanceof ByteSize) {
        totalBytes += ((ByteSize) sizeValue).getBytes();
      }
      
      if (timeValue instanceof TimeDuration) {
        totalNanoseconds += ((TimeDuration) timeValue).getNanoseconds();
      }
      
      rowCount++;
    }
    
    // Return empty list during execution to accumulate values
    return rows;
  }

  @Override
  public void destroy() {
    // No cleanup needed
  }

  @Override
  public List<Row> finalize(List<Row> rows) throws DirectiveExecutionException {
    Row result = new Row();
    result.add(totalSizeColumn, new ByteSize(totalBytes + "B").getBytesAs(sizeUnit));
    result.add(totalTimeColumn, new TimeDuration(totalNanoseconds + "ns").getDurationAs(timeUnit));
    return List.of(result);
  }
} 