//  Copyright (c) Microsoft Corporation.
//  All rights reserved.
//
//  This code is licensed under the MIT License.
//
//  Permission is hereby granted, free of charge, to any person obtaining a copy
//  of this software and associated documentation files(the "Software"), to deal
//  in the Software without restriction, including without limitation the rights
//  to use, copy, modify, merge, publish, distribute, sublicense, and / or sell
//  copies of the Software, and to permit persons to whom the Software is
//  furnished to do so, subject to the following conditions :
//
//  The above copyright notice and this permission notice shall be included in
//  all copies or substantial portions of the Software.
//
//  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
//  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
//  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
//  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
//  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
//  THE SOFTWARE.
package com.microsoft.quickauth.signin.internal.metric;

import androidx.annotation.NonNull;
import com.microsoft.quickauth.signin.BuildConfig;
import com.microsoft.quickauth.signin.internal.MSQALogger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MSQAMetric {
  private final String TAG = "MSQAMetric";
  /** The id provided by client library to relate events in a user session. */
  private String mSessionId;

  /** The version of the quick auth SDK library. */
  private String mLibVersion;

  /** A set of events to be saved. */
  private List<MetricEvent> mEvents;

  public String getSessionId() {
    return mSessionId;
  }

  public MSQAMetric setSessionId(String sessionId) {
    this.mSessionId = sessionId;
    return this;
  }

  public String getLibVersion() {
    return mLibVersion;
  }

  public MSQAMetric setLibVersion(String libVersion) {
    this.mLibVersion = libVersion;
    return this;
  }

  public List<MetricEvent> getEvents() {
    return mEvents;
  }

  public MSQAMetric setEvents(List<MetricEvent> events) {
    this.mEvents = events;
    return this;
  }

  public MSQAMetric addEvent(MetricEvent event) {
    if (this.mEvents == null) this.mEvents = new ArrayList<>();
    this.mEvents.add(event);
    return this;
  }

  public static class MetricEvent {
    /** The operation id provided by client library to identify a specified user action. */
    private String mOperationId;

    /** The event name, e.g., "SignIn.Clicked", "SignIn.Success". */
    private final @MSQAMetricEvent String mEventName;

    /** Count of the event. */
    private int mCount;

    /** Duration of the event in milliseconds. */
    private long mDuration;

    /** Custom message for the event. */
    private @MSQAMetricMessage String mMessage;

    private String mComments;

    /** Timestamp of the event in milliseconds. */
    private String mTimestamp;

    /** Custom number field reserved for client. */
    private int mNumberField1;

    /** Custom number field reserved for client. */
    private int mNumberField2;

    public MetricEvent(@MSQAMetricEvent String eventName) {
      mCount = 1;
      mEventName = eventName;
    }

    public String getOperationId() {
      return mOperationId;
    }

    public MetricEvent setOperationId(String operationId) {
      this.mOperationId = operationId;
      return this;
    }

    public String getEventName() {
      return mEventName;
    }

    public int getCount() {
      return mCount;
    }

    public MetricEvent setCount(int count) {
      this.mCount = count;
      return this;
    }

    public long getDuration() {
      return mDuration;
    }

    public MetricEvent setDuration(long duration) {
      this.mDuration = duration;
      return this;
    }

    public MetricEvent setComments(String comments) {
      this.mComments = comments;
      return this;
    }

    public String getComments() {
      return mComments;
    }

    public @MSQAMetricMessage String getMessage() {
      return mMessage;
    }

    public MetricEvent setMessage(@MSQAMetricMessage String message) {
      this.mMessage = message;
      return this;
    }

    public String getTimestamp() {
      return mTimestamp;
    }

    public MetricEvent setTimestamp(String timestamp) {
      this.mTimestamp = timestamp;
      return this;
    }

    public int getNumberField1() {
      return mNumberField1;
    }

    public MetricEvent setNumberField1(int numberField1) {
      this.mNumberField1 = numberField1;
      return this;
    }

    public int getNumberField2() {
      return mNumberField2;
    }

    public MetricEvent setNumberField2(int numberField2) {
      this.mNumberField2 = numberField2;
      return this;
    }
  }

  @NonNull
  public Map<String, Object> getMetricParams() {
    Map<String, Object> map = new HashMap<>();
    map.put("EasyAuthSessionId", getSessionId());
    map.put("LibVersion", getLibVersion());
    JSONArray jsonArray = new JSONArray();
    if (mEvents != null && !mEvents.isEmpty()) {
      try {

        for (MetricEvent event : mEvents) {
          JSONObject jsonObject = new JSONObject();
          jsonObject.putOpt("EventName", event.getEventName());
          jsonObject.putOpt("Message", event.getMessage());
          jsonObject.putOpt("Count", event.getCount());
          jsonObject.putOpt("Duration", event.getDuration());
          jsonObject.putOpt("Timestamp", event.getTimestamp());
          jsonArray.put(jsonObject);
        }
      } catch (JSONException e) {
        MSQALogger.getInstance().error(TAG, "metric transfer to string error", e);
      }
    }
    if (BuildConfig.DEBUG)
      MSQALogger.getInstance()
          .verbose(
              TAG,
              "event result= EasyAuthSessionId="
                  + getSessionId()
                  + ", LibVersion="
                  + getLibVersion()
                  + ", "
                  + jsonArray);
    map.put("Events", jsonArray);
    return map;
  }
}
