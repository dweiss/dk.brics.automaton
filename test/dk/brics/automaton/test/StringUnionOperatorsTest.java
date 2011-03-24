package dk.brics.automaton.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import dk.brics.automaton.*;

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
    
    @Test
    public void testNoMatch()
    {
        final Automaton runAutomaton = new RegExp("abax").toAutomaton();

        AutomatonMatcher m = new RunAutomaton(runAutomaton).newMatcher("ababax");
        assertTrue(m.find());
        assertEquals("abax", m.group());
        assertFalse(m.find());
    }

    @Test
    public void testAStar()
    {
        final Automaton runAutomaton = new RegExp("a*").toAutomaton();

        AutomatonMatcher m = new RunAutomaton(runAutomaton).newMatcher("baac");
        List<String> result = new ArrayList<String>();
        while (m.find())
        {
            result.add(">" + m.group() + "<");
        }
        
        Assert.assertArrayEquals(new Object[] {
            "><",
            ">aa<",
            "><",
            "><",
        }, result.toArray());
    }
}
