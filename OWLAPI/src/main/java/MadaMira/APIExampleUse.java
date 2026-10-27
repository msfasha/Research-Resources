package MadaMira;

import edu.columbia.ccls.madamira.MADAMIRAWrapper;
import edu.columbia.ccls.madamira.configuration.MadamiraInput;
import edu.columbia.ccls.madamira.configuration.MadamiraOutput;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.concurrent.ExecutionException;

/**
 * An example class that shows how MADAMIRA can be called through its API.
 *
 */
public class APIExampleUse {

    // MADAMIRA namespace as defined by its XML schema
    private static final String MADAMIRA_NS = "edu.columbia.ccls.madamira.configuration";
    //C:\Users\Mohammad Fasha\Documents\NetBeansProjects\TestMadaMira
    private static final String INPUT_FILE = "../samples/xml/SampleInputFile.xml";
    private static final String OUTPUT_FILE = "sampleOutputFile.xml";

    public static void main(String [] args) {

        final MADAMIRAWrapper wrapper = new MADAMIRAWrapper();
        JAXBContext jc = null;

        try {
            jc = JAXBContext.newInstance(MADAMIRA_NS);
            Unmarshaller unmarshaller = jc.createUnmarshaller();

            // The structure of the MadamiraInput object is exactly similar to the
            // madamira_input element in the XML
            final MadamiraInput input = (MadamiraInput)unmarshaller.unmarshal(
                    new File( INPUT_FILE ) );

            {
                int numSents = input.getInDoc().getInSeg().size();
                String outputAnalysis = input.getMadamiraConfiguration().
                        getOverallVars().getOutputAnalyses();
                String outputEncoding = input.getMadamiraConfiguration().
                        getOverallVars().getOutputEncoding();

                System.out.println("processing " + numSents +
                        " sentences for analysis type = " + outputAnalysis +
                        " and output encoding = " + outputEncoding);
            }

            // The structure of the MadamiraOutput object is exactly similar to the
            // madamira_output element in the XML
            final MadamiraOutput output = wrapper.processString(input);

            {
                int numSents = output.getOutDoc().getOutSeg().size();

                System.out.println("processed output contains "+numSents+" sentences...");
            }


            jc.createMarshaller().marshal(output, new File(OUTPUT_FILE));


        } catch (JAXBException ex) {
            System.out.println("Error marshalling or unmarshalling data: "
                    + ex.getMessage());
        } catch (InterruptedException ex) {
            System.out.println("MADAMIRA thread interrupted: "
                    +ex.getMessage());
        } catch (ExecutionException ex) {
            System.out.println("Unable to retrieve result of task. " +
                    "MADAMIRA task may have been aborted: "+ex.getCause());
        }

        wrapper.shutdown();
    }
}