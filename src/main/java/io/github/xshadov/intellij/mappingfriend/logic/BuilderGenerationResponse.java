package io.github.xshadov.intellij.mappingfriend.logic;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class BuilderGenerationResponse {
	private String builderStart;
	@NonNull
	private String fieldChain;
	private String builderEnd;

	public String join() {
		final StringBuilder builder = new StringBuilder();

		if (builderStart != null)
			builder.append(builderStart);

		builder.append(fieldChain);

		if (builderEnd != null)
			builder.append(builderEnd);

		return builder.toString();
	}
}
