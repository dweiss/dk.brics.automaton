package dk.brics.automaton.test;

import dk.brics.automaton.Automaton;
import dk.brics.automaton.AutomatonMatcher;
import dk.brics.automaton.RegExp;
import dk.brics.automaton.RunAutomaton;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class StringUnionOperatorsTest
{
    @Test
    public void testBuildFromSortedArray()
    {
        final CharSequence [] input = new CharSequence []
        {
            "abc", "abcdef"
        };

        final Automaton wordsPattern = Automaton.makeStringUnion(input);

        RunAutomaton runAutomaton = new RunAutomaton(wordsPattern);

        assertTrue(runAutomaton.run( "abc"));
        assertFalse(runAutomaton.run("abcde"));
        assertTrue(runAutomaton.run( "abcdef"));
        assertFalse(runAutomaton.run( "abcdefg"));

        AutomatonMatcher m = runAutomaton.newMatcher("abcde");
        assertTrue(m.find());
        assertEquals("abc", m.group());
    }

    @Test
    public void testBuildFromSortedArray2()
    {
        final Automaton runAutomaton = new RegExp("(abc)|(abcdef)").toAutomaton();

        assertTrue(runAutomaton.run( "abc"));
        assertFalse(runAutomaton.run("abcde"));
        assertTrue(runAutomaton.run( "abcdef"));
        assertFalse(runAutomaton.run( "abcdefg"));

        AutomatonMatcher m = new RunAutomaton(runAutomaton).newMatcher("abcde");
        assertTrue(m.find());
        assertEquals("abc", m.group());
    }
}
