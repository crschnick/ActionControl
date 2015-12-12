package org.monospark.actionpermissions;

import com.google.gson.annotations.SerializedName;

public enum ActionResponse {

	@SerializedName("allow")
	ALLOW,
	@SerializedName("deny")
	DENY;
}
