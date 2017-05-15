package annotation.apiversion;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.condition.AbstractRequestCondition;

public class ApiVersionRequestCondition extends AbstractRequestCondition<ApiVersionRequestCondition> {
	private static Logger logger = LoggerFactory.getLogger(ApiVersionRequestCondition.class);
	private final Set<VersionRange> versions;
	private final String headerName;

	ApiVersionRequestCondition(String headerName, String from, String to) {
		this(headerName, versionRange(from, to));
	}

	private ApiVersionRequestCondition(String headerName, Collection<VersionRange> versions) {
		this.headerName = headerName;
		this.versions = Collections.unmodifiableSet(new HashSet<VersionRange>(versions));
	}

	private static Set<VersionRange> versionRange(String from, String to) {
		HashSet<VersionRange> versionRanges = new HashSet<>();

		if (StringUtils.hasText(from)) {
			String toVersion = (StringUtils.hasText(to) ? to : Version.MAX_VERSION);
			VersionRange versionRange = new VersionRange(from, toVersion);

			versionRanges.add(versionRange);
		}

		return versionRanges;
	}

	@Override
	public ApiVersionRequestCondition combine(ApiVersionRequestCondition other) {
		logger.debug("Combining:\n{}\n{}", this, other);

		Set<VersionRange> newVersions = new LinkedHashSet<VersionRange>(this.versions);
		newVersions.addAll(other.versions);
		String newServiceName;

		if (StringUtils.hasText(this.headerName) && StringUtils.hasText(other.headerName) && !this.headerName.equals(other.headerName)) {
			throw new IllegalArgumentException(
							String.format("Both conditions should have the same header name. %s =!= %s", this.headerName, other.headerName)
			);
		} else if (StringUtils.hasText(this.headerName)) {
			newServiceName = this.headerName;
		} else {
			newServiceName = other.headerName;
		}

		return new ApiVersionRequestCondition(newServiceName, newVersions);
	}

	@Override
	public ApiVersionRequestCondition getMatchingCondition(HttpServletRequest request) {

		String version = request.getHeader(this.headerName);

		logger.debug("Version={}", version);

		if (version == null) {
			throw new IllegalArgumentException(
							String.format("Required header '%s' missing for this request", this.headerName)
			);
		}

		for (VersionRange versionRange : this.versions) {
			if (versionRange.includes(version)) {
				return this;
			}
		}

		logger.debug("Didn't find matching version");

		return null;
	}

	@Override
	public int compareTo(ApiVersionRequestCondition other, HttpServletRequest request) {
		return 0;
	}

	@Override
	protected Collection<?> getContent() {
		return this.versions;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("version={");
		sb.append("headerName=").append(this.headerName).append(",");

		for (VersionRange range : this.versions) {
			sb.append(range).append(",");
		}

		sb.append("}");

		return sb.toString();
	}

	@Override
	protected String getToStringInfix() {
		return " && ";
	}
}
