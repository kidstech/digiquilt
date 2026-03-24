/*
 * Created by jbiatek on Jun 9, 2009
 *
 * University of Minnesota, Morris
 * http://www.morris.umn.edu/
 */

package umm.digiquilt.xmlsaveload.handlers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.xml.sax.SAXException;


/**
 * An XML handler for simple tags which only contain text, such as:
 * &lt;example&gt;This text will be collected by TextHandler&lt;/example&gt;
 * 
 * @author Jason Biatek, last changed by $Author: biatekjt $
 * on $Date: 2009-06-10 16:58:41 $
 * @version $Revision: 1.1 $
 *
 */

public class DateHandler extends DelegatingHandler {

    /**
     * If we see this tag close, we know it's time to stop.
     */
    String endTag;
    /**
     * StringBuilder to put the data together without allocating a new
     * String each time.
     */
    StringBuilder data = new StringBuilder();
    
    /**
     * Create a TextHandler to get a simple String inside of a tag.
     * 
     * @param parent The parent to be notified of the resulting String
     * @param endTag The tag to watch for and stop at. When a &lt;/endTag&gt;
     * is reached, the TextHandler will know to stop and pass the String that
     * was built to the parent.
     */
    public DateHandler(DelegatingHandler parent, String endTag) {
        super(parent);
        this.endTag = endTag;
    }

    
    
    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        for (int i=0; i<length; i++){
            data.append(ch[start+i]);
        }
    }



    @Override
    public void endElement(String uri, String localName, String name)
            throws SAXException {
        if (name.equals(endTag)){
            stopHandlingEvents();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            Date date = null;
            try {
				date = sdf.parse(data.toString());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            parent.childFinished(date);
        }
    }



    @Override
    public void childFinished(Object o) {
        // This should create no children at all
        throw new UnsupportedOperationException();
    }

}
