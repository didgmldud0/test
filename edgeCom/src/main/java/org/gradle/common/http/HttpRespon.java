package org.gradle.common.http;

public interface HttpRespon {
	void fail(String result);
	void success(String result);
}
