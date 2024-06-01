package dynamic_fps.impl.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Version implements Comparable<Version> {
	private final int[] components;
	private final @Nullable String preRelease;
	private final @Nullable String buildMetadata;

	// This is not *fully compliant* with the SemVer spec:
	// Specifying the patch version is optional since Minecraft doesn't on 1.x.0 releases
	// Some other version strings with invalid pre-release or build metadata are accepted
	private static final Pattern VERSION_PATTERN = Pattern.compile(
		"^(?<major>\\d+)\\.(?<minor>\\d+)(?:\\.(?<patch>\\d+))?(?:-(?<prerelease>[\\da-zA-Z\\-\\.]{2,}))?(?:\\+(?<metadata>[\\da-zA-Z\\-\\.]+))?$"
	);

	private Version(int major, int minor, int patch, @Nullable String preRelease, @Nullable String buildMetadata) {
		this.components = new int[]{major, minor, patch};

		this.preRelease = preRelease;
		this.buildMetadata = buildMetadata;
	}

	public static Version of(String raw) throws VersionParseException {
		Matcher matcher = VERSION_PATTERN.matcher(raw);

		if (!matcher.matches()) {
			throw new VersionParseException(raw);
		}

		int major = Integer.parseInt(matcher.group("major"));
		int minor = Integer.parseInt(matcher.group("minor"));
		int patch = 0;

		// Patch is optional due to Minecraft ...
		if (matcher.group("patch") != null) {
			patch = Integer.parseInt(matcher.group("patch"));
		}

		String preRelease = matcher.group("prerelease");
		String buildMetadata = matcher.group("metadata");

		return new Version(major, minor, patch, preRelease, buildMetadata);
	}

	public int major() {
		return this.components[0];
	}

	public int minor() {
		return this.components[1];
	}

	public int patch() {
		return this.components[2];
	}

	public boolean isPreRelease() {
		return this.preRelease() != null;
	}

	public @Nullable String preRelease() {
		return this.preRelease;
	}

	public boolean hasBuildMetadata() {
		return this.buildMetadata() != null;
	}

	public @Nullable String buildMetadata() {
		return this.buildMetadata;
	}

	/**
	 * @return the version as a string. May be different from raw input due to normalization.
	 */
	@Override
	public String toString() {
		String result = String.format("%s.%s.%s", this.major(), this.minor(), this.patch());

		if (this.isPreRelease()) {
			result += "-" + this.preRelease();
		}

		if (this.hasBuildMetadata()) {
			result += "+" + this.buildMetadata();
		}

		return result;
	}

	@Override
	public int compareTo(@NotNull Version other) {
		for (int index = 0; index < 3; index++) {
			int result = Integer.compare(this.components[index], other.components[index]);

			if (result != 0) {
				return result;
			}
		}

		if (this.isPreRelease() && other.isPreRelease()) {
			// noinspection DataFlowIssue (if statement has guards)
			return this.preRelease().compareTo(other.preRelease());
		} else if (this.isPreRelease()) {
			return -1;
		} else if (other.isPreRelease()) {
			return +1;
		}

		return 0;
	}

	public static class VersionParseException extends Exception {
		private VersionParseException(String version) {
			super(version + " is not a semantic version!");
		}
	}
}
