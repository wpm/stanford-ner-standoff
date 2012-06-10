package wpmcn;

import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.util.Triple;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A custom named entity recognizer that uses regular expression based tokenization.
 * <p/>
 * The <code>classifyToCharacterOffsets</code> method of this class uses a simple regular expression based
 * tokenization scheme that treats each line as a sentence and contiguous spans of either word characters or
 * punctuation symbols as tokens. This scheme is inferior to the one built into the Stanford NER. (For example,
 * it will incorrectly handle abbreviations and words containing apostrophes.) It is included here to illustrate how
 * to call the named entity recognizer with tokens determined from some outside source.
 */
public class CustomCRFClassifier extends CRFClassifier<CoreLabel> {
   // Zero or more non-newline characters followed by one or more newline characters.
   private static final Pattern LINE_REGEX = Pattern.compile("([^\r\n]*)[\r\n]+");
   // One or more word characters or one or more punctuation characters.
   private static final Pattern WORD_OR_PUNCTUATION_REGEX = Pattern.compile("\\w+|[.,?!]+");

   public CustomCRFClassifier(String loadPath) throws ClassNotFoundException, IOException {
      loadClassifier(loadPath);
   }

   /**
    * Do named entity recognition of a set of sentences using a simple regular expression based tokenizer that treats
    * each line as a separate sentence.
    *
    * @param sentences the sentences to be analyzed
    * @return list of triples, each of which gives an entity type and the beginning and ending character offsets
    */
   @Override
   public List<Triple<String, Integer, Integer>> classifyToCharacterOffsets(String sentences) {
      List<Triple<String, Integer, Integer>> triples = new ArrayList<Triple<String, Integer, Integer>>();

      // Enumerate over lines in the input, treating each line as a separate sentence.
      Matcher matcher = LINE_REGEX.matcher(sentences);
      while (matcher.find()) {
         String line = matcher.group(1);
         int offset = matcher.start();
         List<CoreLabel> annotatedTokens = annotateSentence(line, offset);
         List<TokenSpan<CoreLabel>> tokenSpans = getTokenSpans(annotatedTokens);
         List<Triple<String, Integer, Integer>> sentenceTriples = getTokenSpanTriples(tokenSpans);
         triples.addAll(sentenceTriples);
      }
      return triples;
   }

   /**
    * Tokenize a sentence and do named entity recognition on the tokens.
    *
    * @param sentence the sentence to be analyzed
    * @param offset   the offset of the beginning of the sentence in the text
    * @return list of annotated tokens in the sentence
    */
   @SuppressWarnings({"unchecked"})
   protected List<CoreLabel> annotateSentence(String sentence, int offset) {
      CoreLabelTokenFactory factory = new CoreLabelTokenFactory();
      List<CoreLabel> tokens = new ArrayList<CoreLabel>();
      // Enumerate over tokens in the sentence, creating a CoreLabel object for each.
      Matcher matcher = WORD_OR_PUNCTUATION_REGEX.matcher(sentence);
      while (matcher.find()) {
         String tokenText = matcher.group();
         int begin = offset + matcher.start();
         int length = matcher.end() - matcher.start();
         CoreLabel token = factory.makeToken(tokenText, begin, length);
         tokens.add(token);
      }
      // Do named entity recognition on the list of CoreLabel tokens.
      return classifySentence(tokens);
   }

   /**
    * Find contiguous sequences of PERSON, LOCATION, and ORGANIZATION annotated tokens and collect them into token
    * spans.
    *
    * @param annotatedTokens list of annotated tokens in a sentence
    * @return list of PERSON, LOCATION, and ORGANIZATION {@link TokenSpan}s
    */
   protected List<TokenSpan<CoreLabel>> getTokenSpans(List<CoreLabel> annotatedTokens) {
      List<TokenSpan<CoreLabel>> tokenSpans = new ArrayList<TokenSpan<CoreLabel>>();
      TokenSpan<CoreLabel> span;
      TokenSpanFinder<CoreLabel> spanFinder = new TokenSpanFinder<CoreLabel>("PERSON", "LOCATION", "ORGANIZATION");
      for (CoreLabel token : annotatedTokens) {
         String annotation = token.get(CoreAnnotations.AnswerAnnotation.class);
         span = spanFinder.nextToken(token, annotation);
         if (null != span) {
            tokenSpans.add(span);
         }
      }
      span = spanFinder.complete();
      if (null != span) {
         tokenSpans.add(span);
      }
      return tokenSpans;
   }

   /**
    * Create type and offset triples out of token spans.
    *
    * @param tokenSpans list of {@link TokenSpan}s
    * @return list of triples, each of which gives an entity type and the beginning and ending character offsets of
    *         the token spans
    */
   protected List<Triple<String, Integer, Integer>> getTokenSpanTriples(List<TokenSpan<CoreLabel>> tokenSpans) {
      List<Triple<String, Integer, Integer>> triples =
            new ArrayList<Triple<String, Integer, Integer>>(tokenSpans.size());
      for (TokenSpan<CoreLabel> tokenSpan : tokenSpans) {
         String type = tokenSpan.getType();
         int begin = tokenSpan.get(0).beginPosition();
         int end = tokenSpan.get(tokenSpan.size() - 1).endPosition();
         Triple<String, Integer, Integer> triple = new Triple<String, Integer, Integer>(type, begin, end);
         triples.add(triple);
      }
      return triples;
   }
}
