package annotation.apiversion;

public class VersionRange {
	private Version from;
	private Version to;

	VersionRange(String from, String to) {
		this.from = new Version(from);
		this.to = new Version(to);
	}

	boolean includes(String other) {
		Version otherVersion = new Version(other);

		return (this.from.compareTo(otherVersion) <= 0 && this.to.compareTo(otherVersion) > 0);
	}

	@Override
	public String toString() {
		return String.format("range[%s-%s]", this.from, this.to);
	}
}
