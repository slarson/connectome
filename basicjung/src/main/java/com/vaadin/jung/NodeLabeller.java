package com.vaadin.jung;

import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
/**
 * This extend the ToStringLabeller class and it is implemented
 * to display the data that pretends to the cells in the node.
 * @author Ruggero Carloz
 * @date 05-19-2010
 *
 */
public class NodeLabeller extends ToStringLabeller<MyNode> {

        /*
         * (non-Javadoc)
         *
         * @seeedu.uci.ics.jung.visualization.decorators.DefaultToolTipFunction#
         * getToolTipText(java.lang.Object)
         */
        public String transform(MyNode v) {

                MyNode n = (MyNode) v;

                //HTML can be used to format the tooltip.
                //http://sourceforge.net/projects/jung/forums/forum/252062/topic/2294542
                String data_str = "<html>";
                data_str += n.toString();

                data_str += "</html>";
                return data_str;

        }

}
