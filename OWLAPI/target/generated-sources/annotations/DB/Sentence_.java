package DB;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2018-04-14T18:51:23")
@StaticMetamodel(Sentence.class)
public class Sentence_ { 

    public static volatile SingularAttribute<Sentence, Integer> storyId;
    public static volatile SingularAttribute<Sentence, String> sentenceOwlPos;
    public static volatile SingularAttribute<Sentence, String> sentenceText;
    public static volatile SingularAttribute<Sentence, String> regularExpressionReadyPos;
    public static volatile SingularAttribute<Sentence, String> sentenceExpandedPos;
    public static volatile SingularAttribute<Sentence, String> correctedParseTree;
    public static volatile SingularAttribute<Sentence, Integer> id;
    public static volatile SingularAttribute<Sentence, Integer> lineNumber;
    public static volatile SingularAttribute<Sentence, String> parseTree;

}