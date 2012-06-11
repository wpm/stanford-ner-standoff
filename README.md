Stanford Named Entity Recognizer Standoff Annotation
----------------------------------------------------

This is example code that demonstrates how to use the
[Stanford Named Entity Recognizer](http://nlp.stanford.edu/software/CRF-NER.shtml "Stanford NER")
to return standoff annotation of text.

Here is an example Maven command that runs this program.

	env MAVEN_OPTS=-mx700m mvn exec:java -Dexec.args="all.3class.distsim.crf.ser.gz file.txt"

Here _all.3class.distsim.crf.ser.gz_ is a classifier file distributed with the
Stanford Named Entity Recognizer and _file.txt_ contains text to classify.
The *MAVEN_OPTS* environment variable is needed to set a Java heap size large enough to
accommodate the recognizer.

If _file.txt_ contains the following text

	Thomas Pynchon was born in Glen Cove, New York.
	He attended Cornell University.

the program will return

	(PERSON,0,14) Thomas Pynchon
	(LOCATION,27,36) Glen Cove
	(LOCATION,38,46) New York
	(ORGANIZATION,60,78) Cornell University

The numbers are span begin and end offsets into the text.

If the program is run with the *--custom-tokenization* switch, it will use a simple regular
expression based tokenization scheme that treats each line of text as a separate sentence.
This is inferior to the tokenization scheme built into the Stanford tools, but serves as an
example of how to use the recognizer with custom tokenization schemes.
