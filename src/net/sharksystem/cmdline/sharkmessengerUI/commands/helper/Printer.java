package net.sharksystem.cmdline.sharkmessengerUI.commands.helper;

import java.util.Iterator;

public class Printer {
    public static String getIntegerListAsCommaSeparatedString(Iterator<Integer> intIter) {
        if(intIter == null || !intIter.hasNext()) return "empty";

        StringBuilder sb = new StringBuilder();

        boolean first = true;
        do {
            if(first) first = false;
            else sb.append(", ");
            sb.append(intIter.next());
        } while(intIter.hasNext());

        return sb.toString();
    }
}
