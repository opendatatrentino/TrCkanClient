import java.util.List;

import org.ckan.CKANException;
import org.ckan.Client;
import org.ckan.Connection;



/**
 * *****************************************************************************
 * Copyright 2012-2013 Trento Rise (www.trentorise.eu/)
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License (LGPL)
 * version 2.1 which accompanies this distribution, and is available at
 *
 * http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 *******************************************************************************
 */

public class TestMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Connection c = new Connection("http://ckan-2x.ckan-staging.dati.trentino.it");
		Client cl = new Client(c, null);
		try {
			List<String> dsl = cl.getDatasetList().result;
			System.out.println(dsl.size());
		} catch (CKANException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
