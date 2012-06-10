package wpmcn;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.*;

@SuppressWarnings({"unchecked"}) // Needed for varargs argument to TokenSpan<TOKEN>(type, token)
public class TokenSpanTest {
   private TokenSpan<String> noTokens;
   private TokenSpan<String> oneToken;
   private TokenSpan<String> twoTokens;

   @Before
   public void setUp() throws Exception {
      noTokens = new TokenSpan<String>("ORGANIZATION");
      oneToken = new TokenSpan<String>("PERSON", "George");
      twoTokens = new TokenSpan<String>("LOCATION", "Los", "Angeles");
   }

   @Test
   public void testToString() throws Exception {
      assertEquals("ORGANIZATION []", noTokens.toString());
      assertEquals("PERSON [George]", oneToken.toString());
      assertEquals("LOCATION [Los, Angeles]", twoTokens.toString());
   }

   @Test
   public void testEquals() throws Exception {
      assertTrue(noTokens.equals(new TokenSpan<String>("ORGANIZATION")));
      assertTrue(oneToken.equals(new TokenSpan<String>("PERSON", "George")));
      assertTrue(twoTokens.equals(new TokenSpan<String>("LOCATION", "Los", "Angeles")));
   }

   @Test
   public void testNotEqual() throws Exception {
      assertFalse(noTokens.equals(oneToken));
      assertFalse(oneToken.equals(twoTokens));
      assertFalse(noTokens.equals(new TokenSpan<String>("PERSON")));
      assertFalse(oneToken.equals(new TokenSpan<String>("PERSON", "Mila")));
      assertFalse(twoTokens.equals(new TokenSpan<String>("LOCATION", "Los", "Lobos")));
   }
}
