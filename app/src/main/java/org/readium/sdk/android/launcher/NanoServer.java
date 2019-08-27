package org.readium.sdk.android.launcher;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.HashMap;

import fi.iki.elonen.NanoHTTPD;

public class NanoServer extends NanoHTTPD {

    private final Context context;
    private final HashMap<String, String> mimetypes;

    public NanoServer(Context context) throws IOException {
        super(8080);
        this.context = context;
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        System.out.println("\nRunning! Point your browsers to http://localhost:8080/ \n");
        mimetypes = new HashMap<>();

    }



    @Override
    public Response serve(IHTTPSession session) {
        String uri = session.getUri();
        System.out.println(session.getUri());
        String extension = "";

        int i = uri.lastIndexOf('.');
        if (i > 0) {
            extension = uri.substring(i+1);
        }

        InputStream is = null;
        try {

            is = context.getAssets().open("epub"+session.getUri());
            String mimeType=URLConnection.guessContentTypeFromStream(is);
            System.out.println("mime:"+mimeType);
            return newFixedLengthResponse(Response.Status.OK, mimeType, is, is.available());
        } catch (IOException e) {
            e.printStackTrace();
            return newFixedLengthResponse("OK");
        }

    }
}
