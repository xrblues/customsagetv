package org.jdna.sagetv.networkencoder.dvb.lyngsat;


public class LyngsatParser {
	/** REMOVED
    private Pattern transponderPattern = Pattern.compile("([0-9]+)\\s+([RLHV]).*?SR\\s+([0-9]+)FEC\\s+([0-9]/[0-9])");
    private Pattern numPattern = Pattern.compile("([0-9]+)");
    
    private List<IDVBChannel> channels = new ArrayList<IDVBChannel>();
    private DVBChannel transponder = null;
    
    public List<IDVBChannel> getChannels() {
        return channels;
    }
    
    public List<IDVBChannel> parse(URL url) throws ParserException, IOException {
        Parser parser = new Parser();
        parser.setConnection(url.openConnection());
        HtmlPage page= new HtmlPage(parser);
        parser.visitAllNodesWith(page);
        TableTag[] tables = page.getTables();
        for (TableTag table : tables) {
                if (table.getRow(0).toPlainTextString().contains("Lyngemark Satellite, last updated")) {
                    if (table.getRowCount()>10) {
                        for (TableRow row : table.getRows()) {
                            DVBChannel chan = null;
                            if (row.getColumnCount()==10) {
                                Matcher m = transponderPattern.matcher(row.getColumns()[0].toPlainTextString());
                                if (m.find()) {
                                    transponder = new DVBChannel();
                                    transponder.set(Field.TRANSPODER, m.group(1));
                                    transponder.set(Field.POLARITY, m.group(2));
                                    transponder.set(Field.SYMBOL_RATE, m.group(3));
                                    transponder.set(Field.FEC, m.group(4));

                                    chan = new DVBChannel(transponder);
                                    chan.set(Field.SID, number(row.getColumns()[5].toPlainTextString()));
                                    chan.set(Field.VPID, number(row.getColumns()[6].toPlainTextString()));
                                    chan.set(Field.APID, number(row.getColumns()[7].toPlainTextString()));
                                }
                            } else if (row.getColumnCount()==8 && transponder!=null) {
                                chan = new DVBChannel(transponder);
                                chan.set(Field.SID, number(row.getColumns()[4].toPlainTextString()));
                                chan.set(Field.VPID, number(row.getColumns()[5].toPlainTextString()));
                                chan.set(Field.APID, number(row.getColumns()[6].toPlainTextString()));
                            }
                            
                            if (chan!=null) {
                                channels.add(chan);
                            }
                        }
                    }
                }
        }
        return channels;
    }
    
    private String number(String num) {
        if (num==null) return "";
        Matcher m = numPattern.matcher(num);
        if (m.find()) {
            return m.group(1);
        }
        return "";
    }

    public static void main(String args[]) throws ParserException, MalformedURLException, IOException {
        LyngsatParser p = new LyngsatParser();
        List<IDVBChannel> channels = p.parse(new File("testfiles/bell1.html").toURI().toURL());
        for (IDVBChannel ch : channels) {
            System.out.printf("%s %s %s %s %s %s\n", ch.get(Field.TRANSPODER), ch.get(Field.SYMBOL_RATE), ch.get(Field.POLARITY), ch.get(Field.SID), ch.get(Field.VPID), ch.get(Field.APID));
        }
        ChannelsConfChannelProvider prov = new ChannelsConfChannelProvider();
        prov.addChannels(channels);
        prov.save(new File("channels-new.conf"));
        System.out.printf("Dumped %s channels\n", channels.size());
    }
    */
}
