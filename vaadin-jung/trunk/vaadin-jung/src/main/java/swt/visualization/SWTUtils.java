/*
 * Licensed Materials - Property of IBM
 *
 * com.ibm.rational.test.ct
 *
 * (c) Copyright IBM Corporation 2005. All Rights Reserved. 
 * Note to U.S. Government Users Restricted Rights:  Use, duplication or disclosure restricted by GSA ADP  Schedule Contract with IBM Corp.
 */
package swt.visualization;

import java.util.HashMap;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

/**
 * @author lbigeard
 * @author Lucas Bigeardel
 */
public class SWTUtils {
    static HashMap colorMap = new HashMap();
    public static Color getSWTColor(java.awt.Color c) {
        Color color = null;
        String key = c.getRed() + "_" +  c.getGreen() + "_" + c.getBlue(); //$NON-NLS-1$ //$NON-NLS-2$
        if (!colorMap.containsKey(key)) {
            color = (Color) colorMap.put(key, new Color(Display.getDefault(), c.getRed(), c.getGreen(), c.getBlue()));
        } else {
            color = (Color) colorMap.get(key);
        }
        return color;
    }        
}