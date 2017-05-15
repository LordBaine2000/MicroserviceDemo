package annotation.apiversion;

import java.lang.reflect.Method;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

public class ApiVersionRequestMappingHandlerMapping extends RequestMappingHandlerMapping {
	@Override
	protected RequestCondition<?> getCustomTypeCondition(Class<?> handlerType) {
		ApiVersion typeAnnotation = AnnotationUtils.findAnnotation(handlerType, ApiVersion.class);

		return createCondition(typeAnnotation);
	}

	@Override
	protected RequestCondition<?> getCustomMethodCondition(Method method) {
		ApiVersion methodAnnotation = AnnotationUtils.findAnnotation(method, ApiVersion.class);

		return createCondition(methodAnnotation);
	}

	private RequestCondition<?> createCondition(ApiVersion versionMapping) {
		return (versionMapping != null) ?
						new ApiVersionRequestCondition(versionMapping.headerName(), versionMapping.from(), versionMapping.to()) :
						null;
	}
}
