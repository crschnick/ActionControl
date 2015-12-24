package org.monospark.actioncontrol.rule;

import com.google.gson.annotations.SerializedName;

public enum ActionResponse {

    @SerializedName
    ("allow") ALLOW,

    @SerializedName
    ("deny") DENY;
}
