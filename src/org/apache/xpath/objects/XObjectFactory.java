package org.apache.xpath.objects;

import org.apache.xml.dtm.*;
import org.apache.xml.utils.DateTimeObj;
import org.apache.xml.utils.Duration;
import org.apache.xpath.XPathContext;
import org.apache.xpath.NodeSetDTM;
import org.apache.xpath.axes.OneStepIterator;
import org.apache.xml.utils.QName;

public class XObjectFactory
{
  
  /**
   * Create the right XObject based on the type of the object passed.  This 
   * function can not make an XObject that exposes DOM Nodes, NodeLists, and 
   * NodeIterators to the XSLT stylesheet as node-sets.
   *
   * @param val The java object which this object will wrap.
   *
   * @return the right XObject based on the type of the object passed.
   */
  static public XObject create(Object val)
  {

    XObject result;

    if (val instanceof XObject)
    {
      result = (XObject) val;
    }
    else if (val instanceof String)
    {
      result = new XString((String) val);
    }
    else if (val instanceof Boolean)
    {
      result = new XBoolean((Boolean)val);
    }
    else if (val instanceof Double)
    {
      result = new XDouble(((Double) val));
    }
    else if (val instanceof Integer)
    {
      result = new XInteger(((Integer) val)); 
    }
    else if (val instanceof QName)
    {
    	result=new XExpandedQName((QName)val);
    }
    else
    { 
      result = new XJavaObject(val);
    }

    return result;
  }
  
  
  /**
   * Create the right XObject based on the type of the object passed.
   * This function <emph>can</emph> make an XObject that exposes DOM Nodes, NodeLists, and 
   * NodeIterators to the XSLT stylesheet as node-sets.
   *
   * @param val The java object which this object will wrap.
   * @param xctxt The XPath context.
   *
   * @return the right XObject based on the type of the object passed.
   */
  static public XObject create(Object val, XPathContext xctxt)
  {

    XObject result;

    if (val instanceof XObject)
    {
      result = (XObject) val;
    }
    else if (val instanceof String)
    {
      result = new XString((String) val);
    }
    else if (val instanceof Boolean)
    {
      result = new XBoolean((Boolean)val);
    }
    else if (val instanceof Double)
    {
      result = new XDouble(((Double) val));
    }
    else if (val instanceof Integer)
    {
      result = new XInteger(((Integer) val));
    }
    else if (val instanceof DateTimeObj)
    {
      result = new XDateTime((DateTimeObj) val);
    }
    else if (val instanceof Duration)
    {
      result = new XDuration((Duration) val);
    }
    else if (val instanceof DTM)
    {
      DTM dtm = (DTM)val;
      try
      {
        int dtmRoot = dtm.getDocument();
        DTMAxisIterator iter = dtm.getAxisIterator(Axis.SELF);
        iter.setStartNode(dtmRoot);
        DTMIterator iterator = new OneStepIterator(iter, Axis.SELF);
        iterator.setRoot(dtmRoot, xctxt);
        result = new XNodeSet(iterator);
      }
      catch(Exception ex)
      {
        throw new org.apache.xml.utils.WrappedRuntimeException(ex);
      }
    }
    else if (val instanceof DTMAxisIterator)
    {
      DTMAxisIterator iter = (DTMAxisIterator)val;
      try
      {
        DTMIterator iterator = new OneStepIterator(iter, Axis.SELF);
        iterator.setRoot(iter.getStartNode(), xctxt);
        result = new XNodeSet(iterator);
      }
      catch(Exception ex)
      {
        throw new org.apache.xml.utils.WrappedRuntimeException(ex);
      }
    }
    else if (val instanceof DTMIterator)
    {
      result = new XNodeSet((DTMIterator) val);
    }
    // This next three instanceofs are a little worrysome, since a NodeList 
    // might also implement a Node!
    else if (val instanceof org.w3c.dom.Node)
    {
      result = new XNodeSetForDOM((org.w3c.dom.Node)val, xctxt);
    }
    // This must come after org.w3c.dom.Node, since many Node implementations 
    // also implement NodeList.
    else if (val instanceof org.w3c.dom.NodeList)
    {
      result = new XNodeSetForDOM((org.w3c.dom.NodeList)val, xctxt);
    }
    else if (val instanceof org.w3c.dom.traversal.NodeIterator)
    {
      result = new XNodeSetForDOM((org.w3c.dom.traversal.NodeIterator)val, xctxt);
    }
    else
    {
      result = new XJavaObject(val);
    }

    return result;
  }
}