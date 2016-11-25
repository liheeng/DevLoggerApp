/*******************************************************************************
 * Copyright (c) 2014-2016 AeMoney(R) Corporation.
 * 上海大钱信息技术有限公司
 * All rights reserved. 
 *******************************************************************************/

package io.henry.devlogger;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * Javascript跟java在进行编码时遵循的规范不一样,</br>
 * Javascript遵循的是:RFC3986</br>
 * Java的为了在各个系统间做兼容,处理了特殊符号,处理方式如下</br>
 * 1.字符"a"-"z"，"A"-"Z"，"0"-"9"，"."，"-"，"*"，和"_" 都不会被编码;</br>
 * 2.将空格转换为加号 (+) ;</br>
 * 3.将非文本内容转换成"%xy"的形式,xy是两位16进制的数值;</br>
 * 4.在每个 name=value 对之间放置 & 符号</br>
 * 最终将js和java的ascii 1-127的字符循环编码匹配不同,最终发现有以下几值不同</br>
 *
 * <table align="center">
 *    <tbody>
 *        <tr class="firstRow">
 *            <td width="214" valign="top" style="word-break: break-all;" align="center">
 *                <strong>ASCII</strong>
 *            </td>
 *            <td width="214" valign="top" style="word-break: break-all;" align="center">
 *                <strong>Java</strong>
 *            </td>
 *            <td width="214" valign="top" style="word-break: break-all;" align="center">
 *                <strong>Javascript</strong>
 *            </td>
 *        </tr>
 *        <tr>
 *            <td width="214" valign="top" style="word-break: break-all;" align="center">
 *                [Space]
 *            </td>
 *            <td width="214" valign="top" style="word-break: break-all;" align="center">
 *                +
 *            </td>
 *            <td width="214" valign="top" style="word-break: break-all;" align="center">
 *                %20
 *            </td>
 *        </tr>
 *        <tr>
 *            <td width="214" valign="top" style="word-break: break-all;" align="center">
 *                !
 *            </td>
 *            <td width="214" valign="top" style="word-break: break-all;" align="center">
 *                %21
 *            </td>
 *            <td width="214" valign="top" style="word-break: break-all;" align="center">
 *                !
 *            </td>
 *        </tr>
 *        <tr>
 *            <td width="214" valign="top" style="word-break: break-all;" align="center">
 *                &#39;
 *            </td>
 *            <td width="214" valign="top" style="word-break: break-all;" align="center">
 *                %27
 *            </td>
 *            <td width="214" valign="top" style="word-break: break-all;" align="center">
 *              &#39;
 *            </td>
 *        </tr>
 *        <tr>
 *            <td width="214" valign="top" style="word-break: break-all;" align="center">
 *                (
 *            </td>
 *            <td width="214" valign="top" style="word-break: break-all;" align="center">
 *                %28
 *            </td>
 *            <td width="214" valign="top" style="word-break: break-all;" align="center">
 *                (
 *            </td>
 *        </tr>
 *        <tr>
 *            <td width="214" valign="top" style="word-break: break-all;" align="center">
 *                )
 *            </td>
 *            <td width="214" valign="top" style="word-break: break-all;" align="center">
 *                %29
 *            </td>
 *            <td width="214" valign="top" style="word-break: break-all;" align="center">
 *                )
 *            </td>
 *        </tr>
 *        <tr>
 *            <td width="214" valign="top" style="word-break: break-all;" align="center">
 *                ~
 *            </td>
 *            <td width="214" valign="top" style="word-break: break-all;" align="center">
 *                    %7E
 *            </td>
 *            <td width="214" valign="top" style="word-break: break-all;" align="center">
 *                ~
 *            </td>
 *        </tr>
 *    </tbody>
 * </table>
 * @see <a href="http://stackoverflow.com/questions/607176/java-equivalent-to-javascripts-encodeuricomponent-that-produces-identical-outpu">http://stackoverflow.com/questions/607176/java-equivalent-to-javascripts-encodeuricomponent-that-produces-identical-outpu</a>
 * 
 * @author LingTong Feng
 * 
 */
public class JavascriptUtil {
    /**
     * Decodes the passed UTF-8 String using an algorithm that's compatible with
     * JavaScript's <code>decodeURIComponent</code> function. Returns
     * <code>null</code> if the String is <code>null</code>.
     *
     * @param s The UTF-8 encoded String to be decoded
     * @return the decoded String
     */
    public static String decodeURIComponent(String s)
    {
        return decodeURIComponent(s, "UTF-8");
    }
    
    /**
     * Decodes the passed String using an algorithm that's compatible with
     * JavaScript's <code>decodeURIComponent</code> function. Returns
     * <code>null</code> if the String is <code>null</code>.
     *
     * @param s The encoded String to be decoded
     * @param charset
     * @return the decoded String
     */
    public static String decodeURIComponent(String s, String charset)
    {
      if (s == null)
      {
        return null;
      }

      String result = null;

      try
      {
        result = URLDecoder.decode(s, charset);
      }

      // This exception should never occur.
      catch (UnsupportedEncodingException e)
      {
        result = s;  
      }

      return result;
    }

    /**
     * Encodes the passed String as UTF-8 using an algorithm that's compatible
     * with JavaScript's <code>encodeURIComponent</code> function. Returns
     * <code>null</code> if the String is <code>null</code>.
     * 
     * @param s The String to be encoded
     * @return the encoded String
     */
    public static String encodeURIComponent(String s)
    {
        return encodeURIComponent(s, "UTF-8");
    }
    
    /**
     * Encodes the passed String as specified charset using an algorithm that's compatible
     * with JavaScript's <code>encodeURIComponent</code> function. Returns
     * <code>null</code> if the String is <code>null</code>.
     * 
     * @param s The String to be encoded
     * @param charset
     * @return the encoded String
     */
    public static String encodeURIComponent(String s, String charset)
    {
      String result = null;

      try
      {
        result = URLEncoder.encode(s, charset)
                           .replaceAll("\\+", "%20")
                           .replaceAll("\\%21", "!")
                           .replaceAll("\\%27", "'")
                           .replaceAll("\\%28", "(")
                           .replaceAll("\\%29", ")")
                           .replaceAll("\\%7E", "~");
      }

      // This exception should never occur.
      catch (UnsupportedEncodingException e)
      {
        result = s;
      }

      return result;
    }  
}
