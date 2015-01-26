package org.robinmalfait.KopyPasta;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KopyPasta implements ClipboardOwner {

    protected String baseURL = "http://kopy.io";

    public void save(String contents) throws MalformedURLException {
        URL pasteUrl = createPasteAndGetUrl(contents);
        copyPasteUrlToClipboard(pasteUrl);
        openPasteInBrowser(pasteUrl);
    }

    private void openPasteInBrowser(URL pasteUrl) {
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().browse(pasteUrl.toURI());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void copyPasteUrlToClipboard(URL pasteUrl) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(new StringSelection(pasteUrl.toString()), this);
    }

    private URL createPasteAndGetUrl(String contents) throws MalformedURLException {
        String id = KopyPasta.executePost(new URL(this.baseURL + "/documents"), "raw:" + contents);
        return new URL(this.baseURL + "/" + id);
    }

    public static String executePost(URL url, String data) {
        String result = "";

        try {
            URLConnection conn = url.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            DataOutputStream out = new DataOutputStream(conn.getOutputStream());

            out.writeBytes(data);
            out.flush();
            out.close();

            DataInputStream in = new DataInputStream(conn.getInputStream());

            result = in.readLine();
            in.close();
        } catch(Exception e) {
            e.printStackTrace();
        }

        Pattern p = Pattern.compile("\\{\\\"key\\\":\\\"(.+)\\\"\\}");
        Matcher m = p.matcher(result);

        if (m.find()) {
            return m.group(1);
        }

        return result;
    }

    @Override
    public void lostOwnership(Clipboard clipboard, Transferable transferable) {

    }
}
