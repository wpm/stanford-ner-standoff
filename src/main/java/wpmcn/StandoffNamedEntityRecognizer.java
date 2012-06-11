package wpmcn;

import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.util.Triple;
import org.apache.commons.cli.*;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Application that uses the Stanford Named Entity recognizer to return offset annotation of named entities in a text
 * file.
 *
 * This program takes two arguments, a path to a Stanford Named Entity classifier
 * (e.g. all.3class.distsim.crf.ser.gz) and the name of a file containing text to classify. It prints a list of
 * triples indicating the annotation type and beginning and ending offsets into the document along with the string
 * those offsets refer to.
 */
public class StandoffNamedEntityRecognizer {
   public static void main(String[] args) throws ClassNotFoundException, IOException, ParseException {
      Options options = new Options();
      options.addOption("c", "custom-tokenization", false, "Use custom tokenization");

      CommandLineParser parser = new PosixParser();
      CommandLine cmd = parser.parse(options, args);
      args = cmd.getArgs();

      String model = args[0];
      String filename = args[1];
      String text = FileUtils.readFileToString(new File(filename));

      CRFClassifier annotator = cmd.hasOption("c") ?
            new CustomCRFClassifier(model) : CRFClassifier.getClassifier(model);

      @SuppressWarnings({"unchecked"})
      List<Triple<String, Integer, Integer>> annotations = annotator.classifyToCharacterOffsets(text);
      for (Triple<String, Integer, Integer> annotation: annotations) {
         int beginIndex = annotation.second();
         int endIndex = annotation.third();
         System.out.println(annotation + " " + text.substring(beginIndex, endIndex));
      }
   }
}
