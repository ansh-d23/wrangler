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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.cdap.wrangler.api.annotations.PublicEvolving;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A {@link Token} that represents a byte size value with units.
 */
@PublicEvolving
public class ByteSize implements Token {
  private static final Pattern BYTE_SIZE_PATTERN = Pattern.compile("(\\d+(?:\\.\\d+)?)(B|KB|MB|GB|TB|PB)");
  private final long bytes;
  private final String value;

  public ByteSize(String value) {
    this.value = value;
    Matcher matcher = BYTE_SIZE_PATTERN.matcher(value);
    if (!matcher.matches()) {
      throw new IllegalArgumentException("Invalid byte size format: " + value);
    }

    double size = Double.parseDouble(matcher.group(1));
    String unit = matcher.group(2);
    
    switch (unit) {
      case "B":
        bytes = (long) size;
        break;
      case "KB":
        bytes = (long) (size * 1024);
        break;
      case "MB":
        bytes = (long) (size * 1024 * 1024);
        break;
      case "GB":
        bytes = (long) (size * 1024 * 1024 * 1024);
        break;
      case "TB":
        bytes = (long) (size * 1024L * 1024 * 1024 * 1024);
        break;
      case "PB":
        bytes = (long) (size * 1024L * 1024 * 1024 * 1024 * 1024);
        break;
      default:
        throw new IllegalArgumentException("Unsupported byte unit: " + unit);
    }
  }

  public long getBytes() {
    return bytes;
  }

  public double getBytesAs(String unit) {
    switch (unit) {
      case "B":
        return bytes;
      case "KB":
        return bytes / 1024.0;
      case "MB":
        return bytes / (1024.0 * 1024);
      case "GB":
        return bytes / (1024.0 * 1024 * 1024);
      case "TB":
        return bytes / (1024.0 * 1024 * 1024 * 1024);
      case "PB":
        return bytes / (1024.0 * 1024 * 1024 * 1024 * 1024);
      default:
        throw new IllegalArgumentException("Unsupported byte unit: " + unit);
    }
  }

  @Override
  public TokenType type() {
    return TokenType.BYTE_SIZE;
  }

  @Override
  public String value() {
    return value;
  }

  @Override
  public JsonElement toJson() {
    JsonObject object = new JsonObject();
    object.addProperty("type", TokenType.BYTE_SIZE.name());
    object.addProperty("value", value);
    object.addProperty("bytes", bytes);
    return object;
  }
} 
