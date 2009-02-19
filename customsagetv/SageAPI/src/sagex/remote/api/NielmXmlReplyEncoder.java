package sagex.remote.api;

import java.io.ByteArrayOutputStream;

import javax.servlet.http.HttpServletRequest;

import net.sf.sageplugins.sagexmlinfo.SageXmlWriter;

public class NielmXmlReplyEncoder extends XmlReplyEncoder {

    @Override
    public String encodeReply(Object o, HttpServletRequest req) throws Exception {
        try {
            SageXmlWriter xmlWriter = new SageXmlWriter();
            xmlWriter.add(o);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            xmlWriter.write(baos);
            baos.flush();
            baos.close();
            return baos.toString();
        } catch (Exception e) {
            // if neilm's cant' process it, then try the default xml one
            return super.encodeReply(o, req);
        }
    }

    
}
