/*
 * Copyright (C) 2013 Cumulocity GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of 
 * this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package c8y.pi.piface;

import c8y.pi.driver.Driver;
import c8y.pi.driver.Executer;

import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.Platform;

public class PiFaceDriver implements Driver {

	@Override
	public void initialize(Platform platform) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public Executer[] getSupportedOperations() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void initializeInventory(ManagedObjectRepresentation mo) {
		// TODO Auto-generated method stub

	}

	@Override
	public void discoverChildren(ManagedObjectRepresentation mo) {
		// TODO Auto-generated method stub
		// Four switches
		// 8 relays
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub

	}

}
