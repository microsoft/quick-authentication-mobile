package com.microsoft.quickauth.signin.internal.metric;

import androidx.annotation.NonNull;
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
    private @MSQAMetricEvent String mEventName;

    /** Count of the event. */
    private int mCount;

    /** Duration of the event in milliseconds. */
    private long mDuration;

    /** Custom message for the event. */
    private @MSQAMetricMessage String mMessage;

    private String mComments;

    /** Timestamp of the event in milliseconds. */
    private long mTimestamp;

    /** Custom number field reserved for client. */
    private int mNumberField1;

    /** Custom number field reserver for client. */
    private int mNumberField2;

    public MetricEvent() {
      mCount = 1;
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

    public MetricEvent setEventName(@MSQAMetricEvent String eventName) {
      this.mEventName = eventName;
      return this;
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

    public long getTimestamp() {
      return mTimestamp;
    }

    public MetricEvent setTimestamp(long timestamp) {
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
    String eventResult = null;
    if (mEvents != null && !mEvents.isEmpty()) {
      try {
        JSONArray jsonArray = new JSONArray();
        for (MetricEvent event : mEvents) {
          JSONObject jsonObject = new JSONObject();
          jsonObject.putOpt("OperationId", event.getOperationId());
          jsonObject.putOpt("EventName", event.getEventName());
          jsonObject.putOpt("Count", event.getCount());
          jsonObject.putOpt("Duration", event.getDuration());
          jsonObject.putOpt("NumberField1", event.getNumberField1());
          jsonObject.putOpt("NumberField2", event.getNumberField2());
          jsonArray.put(jsonObject);
        }
        eventResult = jsonArray.toString();
      } catch (JSONException e) {
        MSQALogger.getInstance().error(TAG, "metric transfer to string error", e);
      }
    }
    map.put("Events", eventResult);
    return map;
  }
}
