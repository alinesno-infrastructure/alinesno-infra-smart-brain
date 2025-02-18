package com.alinesno.infra.base.search.utils.splitter;

import com.alinesno.infra.base.search.utils.TextSplitter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Slf4j
public class RecursiveCharacterTextSplitter implements TextSplitter {

    private List<String> separators;
    private boolean keepSeparator;
    private boolean isSeparatorRegex;

    public RecursiveCharacterTextSplitter(List<String> separators, boolean keepSeparator, boolean isSeparatorRegex) {
        this.separators = separators != null ? separators : new ArrayList<>();
        this.separators.add("\n\n");
        this.separators.add("\n");
        this.separators.add(" ");
        this.separators.add("");
        this.keepSeparator = keepSeparator;
        this.isSeparatorRegex = isSeparatorRegex;
    }

    private List<String> splitText(String text, List<String> separators) {

        List<String> finalChunks = new ArrayList<>();
        String separator = separators.get(separators.size() - 1);
        List<String> newSeparators = new ArrayList<>();

        for (int i = 0; i < separators.size(); i++) {
            String s = separators.get(i);
            String _separator = isSeparatorRegex ? Pattern.quote(s) : s;
            if (s.isEmpty()) {
                separator = s;
                break;
            }
            if (text.matches(".*" + _separator + ".*")) {
                separator = s;
                newSeparators = separators.subList(i + 1, separators.size());
                break;
            }
        }

        String _separator = isSeparatorRegex ? Pattern.quote(separator) : separator;
        List<String> splits = splitTextWithRegex(text, _separator, keepSeparator);
        List<String> goodSplits = new ArrayList<>();
        String separatorString = keepSeparator ? separator : "";

        assert splits != null;

        for (String s : splits) {
            int chunkSize = 0;
            if (lengthFunction(s) < chunkSize) {
                goodSplits.add(s);
            } else {
                if (!goodSplits.isEmpty()) {
                    List<String> mergedText = mergeSplits(goodSplits, separatorString);
                    finalChunks.addAll(mergedText);
                    goodSplits.clear();
                }
                if (newSeparators.isEmpty()) {
                    finalChunks.add(s);
                } else {
                    List<String> otherInfo = splitText(s, newSeparators);
                    finalChunks.addAll(otherInfo);
                }
            }
        }
        if (!goodSplits.isEmpty()) {
            List<String> mergedText = mergeSplits(goodSplits, separatorString);
            finalChunks.addAll(mergedText);
        }
        return finalChunks;
    }

    public List<String> splitText(String text) {
        return splitText(text, separators);
    }

    private List<String> splitTextWithRegex(String text, String separator, boolean keepSeparator) {
        return null;
    }

    private int lengthFunction(String s) {
        return s.length();
    }

    private List<String> mergeSplits(List<String> splits, String separator) {
        return null;
    }

    //public class Main {
    //    public static void main(String[] args) {
    //        List<String> separators = new ArrayList<>();
    //        separators.add("\n");
    //        RecursiveCharacterTextSplitter textSplitter = new RecursiveCharacterTextSplitter(separators, true, false);
    //        String text = "This is a sample text. It will be split based on new lines. " +
    //                "We want to see how the RecursiveCharacterTextSplitter works.";
    //        List<String> chunks = textSplitter.splitText(text);
    //        for (String chunk : chunks) {
    //            System.out.println(chunk);
    //        }
    //    }
    //}
}
