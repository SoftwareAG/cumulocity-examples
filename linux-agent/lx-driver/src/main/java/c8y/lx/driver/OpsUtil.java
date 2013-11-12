package c8y.lx.driver;

import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;

import c8y.SupportedOperations;

public class OpsUtil {
	public static void add(ManagedObjectRepresentation mo, String op) {
		SupportedOperations ops = mo.get(SupportedOperations.class);
		
		if (ops == null) {
			ops = new SupportedOperations();
			mo.set(ops);
		}
		
		if (!ops.contains(op)) {
			ops.add(op);
		}
	}
}
