package wpmcn;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;

@SuppressWarnings({"unchecked"}) // Needed for varargs argument to TokenSpan<TOKEN>(type, token)
public class TokenSpanFinderTest {
   private String personLocationSentence;

   @Before
   public void setUp() throws Exception {
      // Make the last token part of a span we're interested in so that we are required to make the final complete()
      // call.
      personLocationSentence =
            "George/PERSON Clooney/PERSON and/OTHER Mila/PERSON Kunis/PERSON " +
                  "live/OTHER " + "in/OTHER Los/LOCATION Angeles/LOCATION";
   }

   @Test
   public void testNameAndLocation() throws Exception {
      TokenSpanFinder<String> personLocation = new TokenSpanFinder<String>("PERSON", "LOCATION");
      List<TokenSpan<String>> expected = Arrays.asList(
            new TokenSpan<String>("PERSON", "George", "Clooney"),
            new TokenSpan<String>("PERSON", "Mila", "Kunis"),
            new TokenSpan<String>("LOCATION", "Los", "Angeles")
      );
      List<TokenSpan<String>> actual = handleSentence(personLocation, personLocationSentence);
      assertEquals(expected, actual);
   }

   private List<TokenSpan<String>> handleSentence(TokenSpanFinder<String> m, String sentence) {
      List<TokenSpan<String>> spans = new ArrayList<TokenSpan<String>>();
      TokenSpan<String> span;
      for (String taggedToken : sentence.split("\\s+")) {
         String[] fields = taggedToken.split("/");
         span = m.nextToken(fields[0], fields[1]);
         if (null != span)
            spans.add(span);
      }
      span = m.complete();
      if (null != span)
         spans.add(span);
      return spans;
   }
}
