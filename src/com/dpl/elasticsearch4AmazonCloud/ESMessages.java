/*Author : Diana P Lazar
Date created : 21/11/2016
Copyright@ FindGoose
 */

package com.dpl.elasticsearch4AmazonCloud;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class ESMessages {
	private static final String BUNDLE_NAME = "com.FG.elasticsearch4AmazonCloud.ESmessages"; //$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

	private ESMessages() {
	}

	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
