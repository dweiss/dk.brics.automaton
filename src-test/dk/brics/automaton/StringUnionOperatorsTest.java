package dk.brics.automaton;

import java.util.Arrays;

import static org.junit.Assert.*;
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
        Arrays.sort(input, dk.brics.automaton.StringUnionOperations.LEXICOGRAPHIC_ORDER);

        final State rootState = dk.brics.automaton.StringUnionOperations.build(input);
        final Automaton wordsPattern = new Automaton();
        wordsPattern.setInitialState(rootState);
        wordsPattern.restoreInvariant();

        RunAutomaton runAutomaton = new RunAutomaton(wordsPattern);
        System.out.println(wordsPattern.toDot());

        assertTrue(runAutomaton.run( "abc"));
        assertFalse(runAutomaton.run("abcde"));
        assertTrue(runAutomaton.run( "abcdef"));
        assertFalse(runAutomaton.run( "abcdefg"));

        AutomatonMatcher m = runAutomaton.newMatcher("abcde");
        assertTrue(m.find());
        assertEquals("abc", m.group());
    }
}
