package org.readium.sdk.android.launcher.model;

import android.content.Context;

import org.readium.sdk.android.launcher.SmilInfo;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class SignLanguageSmilRepo {

    private Map<Integer, List<SignLanguageVideoElement>> pagesVideoElements = new HashMap<>();
    private final Context context;

    public SignLanguageSmilRepo(Context context) {
        this.context = context;
        readSignLanguageSMIL();
    }


    public void readSignLanguageSMIL() {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            String[] fileNames = context.getAssets().list("signlanguagesmilfiles");
            for (String fileName : fileNames) {
                Integer pageNumber = Integer.parseInt(fileName.substring(4, fileName.lastIndexOf("_")));
                if (pageNumber > 4) {
                    pageNumber = pageNumber - 1;
                }
                List<SignLanguageVideoElement> pageVideoElements = new ArrayList<>();
                InputStream is = context.getAssets().open("signlanguagesmilfiles/" + fileName);
                Document doc = dBuilder.parse(is);
                Element element = doc.getDocumentElement();
                element.normalize();
                NodeList nList = doc.getElementsByTagName("par");
                for (int i = 0; i < nList.getLength(); i++) {
                    SignLanguageVideoElement signLanguageVideoElement = new SignLanguageVideoElement();
                    Node parNode = nList.item(i);
                    signLanguageVideoElement.setParId(parNode.getAttributes().getNamedItem("id").getNodeValue());
                    //node.get
                    if (parNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element parElement = (Element) parNode;
                        Node textElement = parElement.getElementsByTagName("text").item(0);
                        String textSrc = textElement.getAttributes().getNamedItem("src").getNodeValue();
                        signLanguageVideoElement.setTextSrc(textSrc);
                        Node videoElement = parElement.getElementsByTagName("video").item(0);
                        Integer begin, end;
                        String clipBegin = videoElement.getAttributes().getNamedItem("clipBegin").getNodeValue();
                        if (clipBegin.contains(":")) {
                            begin = durationStringToMilliseconds(clipBegin);
                        } else {
                            begin = (int) (Float.parseFloat(clipBegin) * 1000);
                        }
                        String clipEnd = videoElement.getAttributes().getNamedItem("clipEnd").getNodeValue();
                        if (clipEnd.contains(":")) {
                            end = durationStringToMilliseconds(clipEnd);
                        } else {
                            end = (int) (Float.parseFloat(clipEnd) * 1000);
                        }
                        String videoSrc = videoElement.getAttributes().getNamedItem("src").getNodeValue();
                        signLanguageVideoElement.setClipBegin(begin);
                        signLanguageVideoElement.setClipEnd(end);
                        signLanguageVideoElement.setVideoFileName(videoSrc);
                        pageVideoElements.add(signLanguageVideoElement);
                    }
                }

                pagesVideoElements.put(pageNumber, pageVideoElements);
                System.out.println("videoElements:" + pagesVideoElements.size());
            }
            System.out.println("hello");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public Integer durationStringToMilliseconds(String duration) {
        String[] tokens = duration.split(":");
        int secondsToMs = (int) (Double.parseDouble(tokens[2]) * 1000);
        int minutesToMs = Integer.parseInt(tokens[1]) * 60000;
        int hoursToMs = Integer.parseInt(tokens[0]) * 3600000;
        Integer total = secondsToMs + minutesToMs + hoursToMs;
        return total;
    }

    public SignLanguageVideoElement findSmilInformation(Integer pageNumber, SmilInfo smilInfo) {
        List<SignLanguageVideoElement> videoElements = pagesVideoElements.get(pageNumber);
        if (videoElements != null) {
            for (int i = 0; i < videoElements.size(); i++) {
                SignLanguageVideoElement signLanguageVideoElement = videoElements.get(i);
                if (signLanguageVideoElement.getParId().equals(smilInfo.parId)) {
                    if (i == videoElements.size() - 1) {
                        signLanguageVideoElement.setLastPageElement(true);
                    }
                    return signLanguageVideoElement;
                }
            }
        }
        return null;
    }
}


