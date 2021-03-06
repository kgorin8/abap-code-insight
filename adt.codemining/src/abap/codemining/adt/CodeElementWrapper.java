package abap.codemining.adt;

import java.util.ArrayList;
import java.util.List;

import com.sap.adt.tools.abapsource.internal.sources.codeelementinformation.ICodeElement;
import com.sap.adt.tools.abapsource.internal.sources.codeelementinformation.ICodeElement.ICodeElementProperty;

import abap.codemining.method.MethodParam;
import abap.codemining.method.MethodParamType;
import abap.codemining.utils.StringUtils;

@SuppressWarnings("restriction")
public class CodeElementWrapper {

	private final ICodeElement codeElement;

	public CodeElementWrapper(ICodeElement codeElement) {
		this.codeElement = codeElement;
	}

	public String getVisibility() {
		return codeElement != null && codeElement.getProperty(PropertyKey.visibility.toString()) != null
				? codeElement.getProperty(PropertyKey.visibility.toString()).getValue()
				: StringUtils.EMPTY;
	}

	public String getLevel() {
		return codeElement != null && codeElement.getProperty(PropertyKey.level.toString()) != null
				? codeElement.getProperty("level").getValue()
				: StringUtils.EMPTY;
	}

	public MethodParam getReturningParameter() {
		final List<MethodParam> methodParams = findParameters(codeElement.getCodeElementChildren(),
				MethodParamDirection.returning);
		return methodParams.size() == 1 ? methodParams.get(0) : null;
	}

	public List<MethodParam> getImportingParameters() {
		return findParameters(codeElement.getCodeElementChildren(), MethodParamDirection.importing);
	}

	public List<MethodParam> getExportingParameters() {
		return findParameters(codeElement.getCodeElementChildren(), MethodParamDirection.exporting);
	}

	public List<MethodParam> getChangingParameters() {
		return findParameters(codeElement.getCodeElementChildren(), MethodParamDirection.changing);
	}

	private List<MethodParam> findParameters(List<? extends ICodeElement> childCodeElements,
			MethodParamDirection parameterDirection) {
		final List<MethodParam> methodParameters = new ArrayList<>();
		;

		for (final ICodeElement childCodeElement : childCodeElements) {

			final ICodeElementProperty elementProperty = childCodeElement.getProperty("paramType");
			final ICodeElementProperty abapTypeProperty = childCodeElement.getProperty("abapType");
			if (elementProperty != null
					&& elementProperty.getValue().toLowerCase().equals(parameterDirection.toString())) {
				methodParameters.add(new MethodParam(childCodeElement.getName(), abapTypeProperty.getValue(),
						evalMethodParamType(parameterDirection)));
			}

		}

		return methodParameters;
	}

	enum MethodParamDirection {
		importing, exporting, returning, changing
	}

	enum PropertyKey {
		visibility, level, paramType, abapType
	}

	private MethodParamType evalMethodParamType(MethodParamDirection parameterDirection) {

		switch (parameterDirection) {
		case importing:
			return MethodParamType.IMPORTING;
		case exporting:
			return MethodParamType.EXPORTING;
		case changing:
			return MethodParamType.CHANGING;
		case returning:
			return MethodParamType.RETURNING;
		default:
			return MethodParamType.UNDEFINED;
		}

	}

}
