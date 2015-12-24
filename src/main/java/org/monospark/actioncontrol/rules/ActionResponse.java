package org.monospark.actioncontrol.rules;

import com.google.gson.annotations.SerializedName;

public enum ActionResponse {

    @SerializedName
    ("allow") ALLOW,

    @SerializedName
    ("deny") DENY;
}
