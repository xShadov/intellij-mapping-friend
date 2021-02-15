package io.github.xshadov.intellij.mappingfriend.logic;

public enum OptionalityType {
	NO_PARAMETERS, REQUIRED, OPTIONAL, MULTIPLE_PARAMETERS, UNKNOWN;

	public static OptionalityType of(Boolean found) {
		return found == null ? OptionalityType.UNKNOWN : found ? OptionalityType.REQUIRED : OptionalityType.OPTIONAL;
	}
}
