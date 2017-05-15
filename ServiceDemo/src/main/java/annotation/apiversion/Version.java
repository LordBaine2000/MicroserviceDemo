package annotation.apiversion;

import javax.annotation.Nonnull;

public class Version implements Comparable<Version> {
	static final String MAX_VERSION = "99.99.99";

	private final int major;
	private final int minor;
	private final int patch;

	Version(String version) {
		String tokens[] = version.split("\\.");

		if (tokens.length != 3) {
			throw new IllegalArgumentException(
							String.format("Invalid version: %s. The version must have major, minor, and patch numbers.", version)
			);
		}

		this.major = Integer.parseInt(tokens[0]);
		this.minor = Integer.parseInt(tokens[1]);
		this.patch = Integer.parseInt(tokens[2]);
	}

	@Override
	public int compareTo(@Nonnull Version other) {
		if (this.major > other.major) {
			return 1;
		} else if (this.major < other.major) {
			return -1;
		} else if (this.minor > other.minor) {
			return 1;
		} else if (this.minor < other.minor) {
			return -1;
		} else if (this.patch > other.patch) {
			return 1;
		} else if (this.patch < other.patch) {
			return -1;
		} else {
			return 0;
		}
	}

	@Override
	public String toString() {
		return String.format("v%d.%d.%d", this.major, this.minor, this.patch);
	}
}
