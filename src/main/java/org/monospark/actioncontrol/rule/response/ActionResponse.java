package org.monospark.actioncontrol.rule.response;

import com.google.gson.annotations.SerializedName;

public enum ActionResponse {

    @SerializedName("allow") ALLOW,

    @SerializedName
    ("deny") DENY;
}
