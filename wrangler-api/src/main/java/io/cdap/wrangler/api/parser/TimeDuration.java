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
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package io.cdap.wrangler.api.parser;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.cdap.wrangler.api.annotations.PublicEvolving;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.concurrent.TimeUnit;

/**
 * A {@link Token} that represents a time duration value with units.
 */
@PublicEvolving
public class TimeDuration implements Token {
  private static final Pattern DURATION_PATTERN = Pattern.compile("(\\d+(?:\\.\\d+)?)(ns|us|ms|s|m|h|d)");
  private final long milliseconds;
  private final String value;

  public TimeDuration(String value) {
    this.value = value;
    Matcher matcher = DURATION_PATTERN.matcher(value);
    if (!matcher.matches()) {
      throw new IllegalArgumentException("Invalid duration format: " + value);
    }

    double duration = Double.parseDouble(matcher.group(1));
    String unit = matcher.group(2);
    
    switch (unit) {
      case "ns":
        milliseconds = (long) (duration / 1_000_000.0);
        break;
      case "us":
        milliseconds = (long) (duration / 1_000.0);
        break;
      case "ms":
        milliseconds = (long) duration;
        break;
      case "s":
        milliseconds = (long) (duration * 1000);
        break;
      case "m":
        milliseconds = (long) (duration * 60 * 1000);
        break;
      case "h":
        milliseconds = (long) (duration * 60 * 60 * 1000);
        break;
      case "d":
        milliseconds = (long) (duration * 24 * 60 * 60 * 1000);
        break;
      default:
        throw new IllegalArgumentException("Unsupported time unit: " + unit);
    }
  }

  public long getMilliseconds() {
    return milliseconds;
  }

  public long getNanoseconds() {
    return TimeUnit.NANOSECONDS.convert(milliseconds, TimeUnit.MILLISECONDS);
  }

  public long getDurationAs(String unit) {
    switch (unit.toLowerCase()) {
      case "ns":
        return TimeUnit.NANOSECONDS.convert(milliseconds, TimeUnit.MILLISECONDS);
      case "us":
        return TimeUnit.MICROSECONDS.convert(milliseconds, TimeUnit.MILLISECONDS);
      case "ms":
        return milliseconds;
      case "s":
        return TimeUnit.SECONDS.convert(milliseconds, TimeUnit.MILLISECONDS);
      case "m":
        return TimeUnit.MINUTES.convert(milliseconds, TimeUnit.MILLISECONDS);
      case "h":
        return TimeUnit.HOURS.convert(milliseconds, TimeUnit.MILLISECONDS);
      case "d":
        return TimeUnit.DAYS.convert(milliseconds, TimeUnit.MILLISECONDS);
      default:
        throw new IllegalArgumentException("Unknown time unit: " + unit);
    }
  }

  public long get(TimeUnit unit) {
    return unit.convert(milliseconds, TimeUnit.MILLISECONDS);
  }

  @Override
  public TokenType type() {
    return TokenType.TIME_DURATION;
  }

  @Override
  public String value() {
    return value;
  }

  @Override
  public JsonElement toJson() {
    JsonObject object = new JsonObject();
    object.addProperty("type", TokenType.TIME_DURATION.name());
    object.addProperty("value", value);
    object.addProperty("milliseconds", milliseconds);
    return object;
  }
}
