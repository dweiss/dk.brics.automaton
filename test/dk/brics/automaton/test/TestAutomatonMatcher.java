/*
 * dk.brics.automaton - AutomatonMatcher
 *
 * Copyright (c) 2010 John Gibson
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package dk.brics.automaton.test;

import dk.brics.automaton.AutomatonMatcher;
import dk.brics.automaton.RegExp;
import dk.brics.automaton.RunAutomaton;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;

/**
 * Some tests of the AutomatonMatcher.
 * 
 * @author John Gibson, Hans-Martin Adorf
 */
public class TestAutomatonMatcher {

	private static final String OPTIONAL_REGEXP = "[abc]*";
	private static final String MANDATORY_REGEXP = "[abc]+";
	private static final RunAutomaton OPTIONAL_AUTOMATON = new RunAutomaton(new RegExp(OPTIONAL_REGEXP).toAutomaton());
	private static final RunAutomaton MANDATORY_AUTOMATON = new RunAutomaton(new RegExp(MANDATORY_REGEXP).toAutomaton());
	private static final Pattern OPTIONAL_PATTERN = Pattern.compile(OPTIONAL_REGEXP);
	private static final Pattern MANDATORY_PATTERN = Pattern.compile(MANDATORY_REGEXP);

	private static final String MIDDLE_TEST = "defabcdef";
	private static final String MIDDLE_AND_END_TEST = "defabcdefabc";

	/**
	 * This is derived from the example bug that Hans-Martin submitted. The
	 * results of the test were altered to match Java's {@code Matcher}.
	 */
	@Test
	public void hansMartinBug() {
        final String testString = " ";

		final Matcher matcher = Pattern.compile(OPTIONAL_REGEXP).matcher(" ");
		boolean found = matcher.find();
		Assert.assertTrue("While using Java didn't get empty match against '" + testString + "' using " + OPTIONAL_REGEXP, found);
		Assert.assertEquals(0, matcher.start());
		Assert.assertEquals(0, matcher.end());

		found = matcher.find();
		Assert.assertTrue("While using Java didn't get empty match against '" + testString + "' using " + OPTIONAL_REGEXP, found);
		Assert.assertEquals(1, matcher.start());
		Assert.assertEquals(1, matcher.end());

		found = matcher.find();
		Assert.assertFalse("While using Java found a third empty match against '" + testString + "' using " + OPTIONAL_REGEXP + " start was: " + matcher.start() + " end was: " + matcher.end(), found);
		Assert.assertEquals(1, matcher.start());
		Assert.assertEquals(1, matcher.end());


		Assert.assertFalse("Fully matched '" + testString + "' using " + OPTIONAL_REGEXP, OPTIONAL_AUTOMATON.run(testString));
        AutomatonMatcher automatonMatcher = OPTIONAL_AUTOMATON.newMatcher(testString);
		found = automatonMatcher.find();
		Assert.assertTrue("Didn't get empty match against '" + testString + "' using " + OPTIONAL_REGEXP, found);
		Assert.assertEquals(0, automatonMatcher.start());
		Assert.assertEquals(0, automatonMatcher.end());
		
		found = automatonMatcher.find();
		Assert.assertTrue("Didn't get empty match against '" + testString + "' using " + OPTIONAL_REGEXP, found);
		Assert.assertEquals(1, automatonMatcher.start());
		Assert.assertEquals(1, automatonMatcher.end());

		found = automatonMatcher.find();
		Assert.assertFalse("Found a third empty match against '" + testString + "' using " + OPTIONAL_REGEXP, found);
		// Java's Matcher is inconsistent here.  When an the pattern
		// is fully optional and the last match is the empty string
		// at the end of the input, then group() is null and start()
		// and end() are their last valid values. In other cases
		// when the matcher has found its last match start(),
		// group(), and end() throw IllegalStateException as the
		// javadocs indicate that they should
		// If we wanted to match Java's Matcher exactly then these
		// tests should be uncommented.
		//Assert.assertEquals(1, automatonMatcher.start());
		//Assert.assertEquals(1, automatonMatcher.end());
		//Assert.assertEquals(null, automatonMatcher.group());
    }

	@Test
	public void plainTest() {
        final String testString = "aa bb abc ";
		Assert.assertFalse("Fully matched '" + testString + "' using " + OPTIONAL_REGEXP, OPTIONAL_AUTOMATON.run(testString));
		compareMatchers(OPTIONAL_PATTERN, OPTIONAL_AUTOMATON, testString);
	}

	@Test
	public void fullTest() {
		final String testString = "abcacacbb";
		Assert.assertTrue("Didn't fully match '" + testString + "' using " + OPTIONAL_REGEXP, OPTIONAL_AUTOMATON.run(testString));
        AutomatonMatcher automatonMatcher = OPTIONAL_AUTOMATON.newMatcher(testString);
		boolean found = automatonMatcher.find();
		Assert.assertTrue("No matches for optional test: " + testString, found);
		Assert.assertEquals(testString, automatonMatcher.group());

		Assert.assertTrue("Didn't fully match '" + testString + "' using " + MANDATORY_REGEXP, MANDATORY_AUTOMATON.run(testString));
        automatonMatcher = MANDATORY_AUTOMATON.newMatcher(testString);
		found = automatonMatcher.find();
		Assert.assertTrue("No matches for mandatory test: " + testString, found);
		Assert.assertEquals(testString, automatonMatcher.group());
	}

	@Test
	public void noHitTest() {
		final String testString = "defdfefdef";
		Assert.assertFalse("Fully matched '" + testString + "' using " + OPTIONAL_REGEXP, OPTIONAL_AUTOMATON.run(testString));
        AutomatonMatcher automatonMatcher = OPTIONAL_AUTOMATON.newMatcher(testString);
		boolean found = automatonMatcher.find();
//		Assert.assertTrue("No matches for optional test: " + testString, found);
//		Assert.assertEquals(testString, automatonMatcher.group());

		Assert.assertFalse("Fully match '" + testString + "' using " + MANDATORY_REGEXP, MANDATORY_AUTOMATON.run(testString));
        automatonMatcher = MANDATORY_AUTOMATON.newMatcher(testString);
		found = automatonMatcher.find();
		Assert.assertFalse("Matches for mandatory test: " + testString, found);
	}

	@Test
	public void optionalMiddleHitTest() {
		final String testString = MIDDLE_TEST;
		Assert.assertFalse("Fully matched '" + testString + "' using " + OPTIONAL_REGEXP, OPTIONAL_AUTOMATON.run(testString));
		compareMatchers(OPTIONAL_PATTERN, OPTIONAL_AUTOMATON, testString);
//        AutomatonMatcher automatonMatcher = OPTIONAL_AUTOMATON.newMatcher(testString);
//		boolean found = automatonMatcher.find();
//		Assert.assertTrue("No matches for optional test: " + testString, found);
//		Assert.assertEquals("abc", automatonMatcher.group());
//
//		found = automatonMatcher.find();
//		Assert.assertFalse("Multiple matches for optional test: " + testString, found);
	}

	@Test
	public void mandatoryMiddleHitTest() {
		final String testString = MIDDLE_TEST;
		Assert.assertFalse("Fully matched '" + testString + "' using " + MANDATORY_REGEXP, MANDATORY_AUTOMATON.run(testString));
		compareMatchers(MANDATORY_PATTERN, MANDATORY_AUTOMATON, testString);
//        AutomatonMatcher automatonMatcher = MANDATORY_AUTOMATON.newMatcher(testString);
//		boolean found = automatonMatcher.find();
//		Assert.assertTrue("No matches for mandatory test: " + testString, found);
//		Assert.assertEquals("abc", automatonMatcher.group());
//
//		found = automatonMatcher.find();
//		Assert.assertFalse("Multiple matches for mandatory test: " + testString, found);
	}

	@Test
	public void optionalMiddleAndEndHitTest() {
		final String testString = MIDDLE_AND_END_TEST;
		Assert.assertFalse("Fully matched '" + testString + "' using " + OPTIONAL_REGEXP, OPTIONAL_AUTOMATON.run(testString));
		compareMatchers(OPTIONAL_PATTERN, OPTIONAL_AUTOMATON, testString);
//        AutomatonMatcher automatonMatcher = OPTIONAL_AUTOMATON.newMatcher(testString);
//		boolean found = automatonMatcher.find();
//		Assert.assertTrue("No matches for optional test: " + testString, found);
//		Assert.assertEquals("abc", automatonMatcher.group());
//		found = automatonMatcher.find();
//		Assert.assertTrue("Only one match found for optional test: " + testString, found);
//		Assert.assertEquals("abc", automatonMatcher.group());
//
//		found = automatonMatcher.find();
//		Assert.assertFalse("More than two matches for optional test: " + testString, found);
	}

	@Test
	public void mandatoryMiddleAndEndHitTest() {
		final String testString = MIDDLE_AND_END_TEST;
		Assert.assertFalse("Fully matched '" + testString + "' using " + MANDATORY_REGEXP, MANDATORY_AUTOMATON.run(testString));
		compareMatchers(MANDATORY_PATTERN, MANDATORY_AUTOMATON, testString);
//        AutomatonMatcher automatonMatcher = MANDATORY_AUTOMATON.newMatcher(testString);
//		boolean found = automatonMatcher.find();
//		Assert.assertTrue("No matches for mandatory test: " + testString, found);
//		Assert.assertEquals("abc", automatonMatcher.group());
//		found = automatonMatcher.find();
//		Assert.assertTrue("Only one match found for mandatory test: " + testString, found);
//		Assert.assertEquals("abc", automatonMatcher.group());
//
//		found = automatonMatcher.find();
//		Assert.assertFalse("More than two matches for mandatory test: " + testString, found);
	}

	private void compareMatchers(final Pattern pattern, final RunAutomaton automaton, final String testString) {
        final AutomatonMatcher automatonMatcher = automaton.newMatcher(testString);
		final Matcher javaMatcher = pattern.matcher(testString);
		while(javaMatcher.find()) {
			final boolean automaton_found = automatonMatcher.find();
			Assert.assertTrue("Java matched, but automaton didn't. Java start: " + javaMatcher.start() + " Java end: " + javaMatcher.end(), automaton_found);
			Assert.assertEquals(javaMatcher.group(), automatonMatcher.group());
			Assert.assertEquals(javaMatcher.start(), automatonMatcher.start());
			Assert.assertEquals(javaMatcher.end(), automatonMatcher.end());
		}
		if(automatonMatcher.find()) {
			Assert.fail("Java didn't match, but automaton did. Automaton start: " + automatonMatcher.start() + " Automaton end: " + automatonMatcher.end() + " matched sequence: '" + automatonMatcher.group() + "'");
		}
	}
}
