package il.ac.technion.cs.ssdl.spartan.refactoring;

import il.ac.technion.cs.ssdl.spartan.refactoring.ShortestOperand.RepositionLiterals;
import il.ac.technion.cs.ssdl.spartan.refactoring.ShortestOperand.RepositionRightLiteral;
import il.ac.technion.cs.ssdl.spartan.refactoring.preferencesTab.PreferencesStrings.Options;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * @author Boris van Sosin <code><boris.van.sosin [at] gmail.com></code> (v2)
 * @author Ofir Elmakias <code><elmakias [at] outlook.com></code> (original /
 *         30.05.2014) (v3)
 * @author Tomer Zeltzer <code><tomerr90 [at] gmail.com></code> (original /
 *         30.05.2014) (v3)
 * @since 2013/07/01
 */
public enum All {
	;
	private static String ignoreRuleStr = "false";

	private static final Map<String, Spartanization> all = new HashMap<>();

	private static enum rulesE { // Converts rule-name to rule array-position
		ComparisonWithBoolean(0), //
		ForwardDeclaration(1), //
		InlineSingleUse(2), //
		RenameReturnVariableToDollar(3), //
		ShortestBranchFirst(4), //
		ShortestOperand(5), //
		Ternarize(6), //
		;
		private final int ruleNum;

		private rulesE(final int rule) {
			ruleNum = rule;
		}

		public int index() {
			return ruleNum;
		}
	}

	private static final Spartanization[] rules = { //
	new ComparisonWithBoolean(), //
		new ForwardDeclaration(), //
		new InlineSingleUse(), //
		new RenameReturnVariableToDollar(), //
		new ShortestBranchFirst(), //
		new ShortestOperand(), //
		new Ternarize(), //
		null };

	private static void put(final Spartanization s) {
		all.put(s.toString(), s);
	}

	private static String[] phrasePrefFile() {
		final String path = Spartanization.getPrefFilePath();
		if (!new File(path).exists())
			return null;
		Scanner sc;
		String[] arr = null;
		try {
			sc = new Scanner(new File(path));
			final List<String> lines = new ArrayList<>();
			while (sc.hasNextLine())
				lines.add(sc.nextLine());
			arr = lines.toArray(new String[0]);
			sc.close();
		} catch (final FileNotFoundException e) {
			e.printStackTrace();
		}
		return arr;
	}

	private static boolean ignored(final String sparta) {
		return sparta.indexOf(ignoreRuleStr) >= 0;
	}

	static {
		reset();
	}

	private static void assignRulesOptions(final String[] str) {
		final ShortestOperand shortestOperandInstance = (ShortestOperand) rules[rulesE.ShortestOperand
		                                                                        .index()];
		for (final String line : str) {
			// There must be a way to make it looks good, it's looks similar to
			// the case with o.equals() and the in() function but it's not the
			// same case...
			if (line.contains(Options.RepositionAllRightLiterals))
				shortestOperandInstance
				.setRightLiteralRule(RepositionRightLiteral.All);
			if (line.contains(Options.RepositionAllButBoolAndNull))
				shortestOperandInstance
						.setRightLiteralRule(RepositionRightLiteral.AllButBooleanAndNull);
			if (line.contains(Options.DoNotRepositionRightLiterals))
				shortestOperandInstance
						.setRightLiteralRule(RepositionRightLiteral.None);
			if (line.contains(Options.RepositionLiterals))
				shortestOperandInstance
						.setBothLiteralsRule(RepositionLiterals.All);
			if (line.contains(Options.DoNotRepositionLiterals))
				shortestOperandInstance
						.setBothLiteralsRule(RepositionLiterals.None);

		}
	}

	/**
	 * Resets the enumeration with the current values from the preferences file.
	 * Letting the rules notification decisions be updated without restarting
	 * eclipse.
	 */
	public static void reset() {
		final String[] str = phrasePrefFile();
		final int offset = Spartanization.getSpartanTitle().length;
		all.clear();
		final boolean useAll = str == null;
		for (int i = 0; i < rules.length - 1; i++)
			if (useAll || str != null && str.length >= i + offset
			&& !ignored(str[i + offset]))
				put(rules[i]);
		assignRulesOptions(str);
		put(new SimplifyLogicalNegation());
	}

	/**
	 * @param name
	 *            the name of the spartanization
	 * @return an instance of the spartanization
	 */
	public static Spartanization get(final String name) {
		return all.get(name);
	}

	/**
	 * @return all the registered spartanization refactoring objects
	 */
	public static Iterable<Spartanization> all() {
		return all.values();
	}

	/**
	 * @return all the registered spartanization refactoring objects names
	 */
	public static List<String> allRulesNames() {
		final List<String> names = new ArrayList<>();
		for (final Spartanization rule : rules)
			if (rule != null)
				names.add(rule.getName());
		return names;
	}
}
