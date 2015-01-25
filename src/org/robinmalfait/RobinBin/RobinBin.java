package org.robinmalfait.RobinBin;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RobinBin implements ClipboardOwner {

    protected String baseURL = "http://kopy.io";

    public void save(String contents)
    {
        String id = RobinBin.executePost(this.baseURL + "/documents", "raw:" + contents);

        String url = this.baseURL + "/" + id;

        // Set in clipboard
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(new StringSelection(url), this);

        // Open Browser
        if(Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().browse(new URI(url));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    public static String executePost(String targetURL, String data) {
        String result = "";

        try {
            URL url = new URL(targetURL);
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
