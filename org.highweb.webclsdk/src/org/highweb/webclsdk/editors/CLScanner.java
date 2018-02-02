package org.highweb.webclsdk.editors;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.BufferedRuleBasedScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;
import org.eclipse.swt.SWT;

public class CLScanner extends BufferedRuleBasedScanner {
	public static final String[] keywords = new String[] {
			"__global", "global",
			"__local", "local",
			"__constant", "constant",
			"__private", "private",
			"__kernel", "kernel",
			"__read_only", "read_only",
			"__write_only", "write_only",
			"__read_write", "read_write"
	};

	public static String[] dataTypes = new String[] {
			"void", "unsigned", "signed",
			"char", "short", "int", "long",
			"float", "double", "const"
	};

	public static String[] builtInFunctions = new String[] {
			"async_work_group_copy", "async_work_group_strided_copy",
			"wait_group_events", "prefetch",
			"clamp", "degrees", "max", "min",
			"mix", "radians", "sign", "smoothstep",
			"step",
			"get_work_dim", "get_global_size",
			"get_global_id", "get_local_size",
			"get_local_id", "get_num_groups",
			"get_group_id",
			"mem_fence", "read_mem_fence",
			"write_mem_fence",
			"cross", "dot", "distance",
			"length", "normalize", "fast_distance",
			"fast_length", "fast_normalize",
			"read_image", "read_imagei", "read_imageui",
			"read_imageh",
			"write_imagef", "write_imagei", "write_imageui",
			"write_imageh",
			"get_image_width", "get_image_height", "get_image_depth",
			"get_image_channel_data_type", "get_image_channel_order",
			"get_image_dim", "get_image_array_size",
			"abs", "abs-diff", "add_sat", "hadd",
			"rhadd", "clz", "mad_hi", "mad24",
			"mad_sat", "mul_hi", "mul24", "rotate",
			"sub_sat", "upsample", "popcount",
			"vec_step", "shuffle", "shuffle2",
			"isequal", "isnotequal", "isgreater", "isgreaterequal",
			"isless", "islessequal", "islessgreater",
			"isfinite", "isinf", "isnan", "isnormal",
			"isordered", "isunordered", "signbit", "any",
			"all", "bitselect", "select",
			"barrier",
			"acos", "acosh", "acospi", "asin",
			"asinh", "asinpi", "atan", "atan2",
			"atanh", "atanpi", "atan2pi", "cbrt",
			"ceil", "copysign", "cos", "cosh",
			"cospi", "erfc", "erf", "exp",
			"exp2", "exp10", "expm1", "fabs",
			"fdim", "floor", "fma", "fmax",
			"fmin", "fmod", "fract", "frexp",
			"hypot", "ilogb", "ldexp", "lgamma",
			"lgamma_r", "log", "log2", "log10",
			"log1p", "logb", "mad", "modf",
			"nan", "nextafter", "pow", "pown",
			"powr", "remainder", "remquo", "rint",
			"rootn", "round", "rsqrt", "sin",
			"sincos", "sinh", "sinpi", "sqrt",
			"tan", "tanh", "tanpi", "tgamma",
			"trunc",
			"half_cos", "native_cos",
			"half_divide", "native_divide",
			"half_exp", "native_exp",
			"half_exp2", "native_exp2",
			"half_exp10", "native_exp10",
			"half_log", "native_log",
			"half_log2", "native_log2",
			"half_log10", "native_log10",
			"half_powr", "native_powr",
			"half_recip", "native_recip",
			"half_rsqrt", "native_rsqrt",
			"half_sin", "native_sin",
			"half_sqrt", "native_sqrt",
			"half_tan", "native_tan"
	};

	public CLScanner(ColorManager manager) {
		IToken token = new Token(new TextAttribute(manager.getColor(ICLColorConstants.CL_KEYWORD), null, SWT.BOLD));
		WordRule keywordRules = new WordRule(new CLWordDetector());
		for(String keyword : keywords) {
			keywordRules.addWord(keyword, token);
		}

		token = new Token(new TextAttribute(manager.getColor(ICLColorConstants.CL_DATA_TYPE), null, SWT.BOLD));
		WordRule dataTypeRules = new WordRule(new CLWordDetector());
		for(String dataType : dataTypes) {
			dataTypeRules.addWord(dataType, token);
		}

		token = new Token(new TextAttribute(manager.getColor(ICLColorConstants.CL_BUILT_IN_FUNCTION), null, SWT.ITALIC));
		WordRule builtInFunctionRules = new WordRule(new CLWordDetector());
		for(String builtInFunction : builtInFunctions) {
			builtInFunctionRules.addWord(builtInFunction, token);
		}

		token = new Token(new TextAttribute(manager.getColor(ICLColorConstants.CL_COMMENT)));
		MultiLineRule multilineCommentRule = new MultiLineRule("/*", "*/", token);
		SingleLineRule singlelineCommentRule = new SingleLineRule("//", "", token);

		IRule[] rules = new IRule[] {
				keywordRules, dataTypeRules, builtInFunctionRules, multilineCommentRule, singlelineCommentRule,
				new WhitespaceRule(new XMLWhitespaceDetector())
		};

		setRules(rules);
	}
}
