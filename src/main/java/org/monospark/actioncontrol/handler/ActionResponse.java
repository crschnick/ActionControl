package org.monospark.actioncontrol.handler;

import com.google.gson.annotations.SerializedName;

public enum ActionResponse {

    @SerializedName("allow") ALLOW, @SerializedName("deny") DENY;
}
